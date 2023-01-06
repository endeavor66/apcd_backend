import pm4py
import os
import json
import pandas as pd
from typing import Union, List
from pm4py.objects.log.obj import EventLog
from pm4py.algo.evaluation.generalization import algorithm as generalization_evaluator
from pm4py.algo.evaluation.simplicity import algorithm as simplicity_evaluator
from utils.mysql_utils import select_all, insert_into_process_model, execute_sql
from Config import *


'''
功能：inductive mining
'''
def inductive_mining(log: Union[EventLog, pd.DataFrame],
                     petri_net_filename: str,
                     bpmn_filename: str,
                     params):
    # Petri-Net
    petri_net, initial_marking, final_marking = pm4py.discover_petri_net_inductive(log, noise_threshold=params["noise_threshold"])
    pm4py.write_pnml(petri_net, initial_marking, final_marking, f"{petri_net_filename}.pnml")
    pm4py.save_vis_petri_net(petri_net, initial_marking, final_marking, f"{petri_net_filename}.png")

    # 转化为BPMN
    bpmn = pm4py.convert_to_bpmn(petri_net, initial_marking, final_marking)
    pm4py.save_vis_bpmn(bpmn, f"{bpmn_filename}.png")

    return petri_net, initial_marking, final_marking


'''
功能：alpha mining
'''
def alpha_mining(log: Union[EventLog, pd.DataFrame],
                 petri_net_filename: str,
                 bpmn_filename: str):
    # Petri-Net
    petri_net, initial_marking, final_marking = pm4py.discover_petri_net_alpha(log)
    pm4py.write_pnml(petri_net, initial_marking, final_marking, f"{petri_net_filename}.pnml")
    pm4py.save_vis_petri_net(petri_net, initial_marking, final_marking, f"{petri_net_filename}.png")

    # 转化为BPMN
    bpmn = pm4py.convert_to_bpmn(petri_net, initial_marking, final_marking)
    pm4py.save_vis_bpmn(bpmn, f"{bpmn_filename}.png")

    return petri_net, initial_marking, final_marking


'''
功能：启发式算法
'''
def heuristics_mining(log: Union[EventLog, pd.DataFrame],
                      heuristics_net_filepath: str,
                      petri_net_filename: str,
                      bpmn_filename: str,
                      params):
    # 发现heuristics_net
    heuristics_net = pm4py.discover_heuristics_net(log,
                                                   dependency_threshold=params["dependency_threshold"],
                                                   and_threshold=params["and_threshold"],
                                                   loop_two_threshold=params["loop_two_threshold"])
    pm4py.save_vis_heuristics_net(heuristics_net, heuristics_net_filepath)

    # 转化为Petri-Net
    petri_net, initial_marking, final_marking = pm4py.convert_to_petri_net(heuristics_net)
    pm4py.write_pnml(petri_net, initial_marking, final_marking, f"{petri_net_filename}.pnml")
    pm4py.save_vis_petri_net(petri_net, initial_marking, final_marking, f"{petri_net_filename}.png")

    # 转化为BPMN
    bpmn = pm4py.convert_to_bpmn(petri_net, initial_marking, final_marking)
    pm4py.save_vis_bpmn(bpmn, f"{bpmn_filename}.png")

    return petri_net, initial_marking, final_marking


'''
功能：模型评估，从四个方面评估模型: 拟合度 fitness, 简单度 simplicity, 泛化度 generalization, 精确度 precision
'''
def model_evaluation(log: Union[EventLog, pd.DataFrame], petri_net, initial_marking, final_marking):
    fitness = pm4py.fitness_token_based_replay(log, petri_net, initial_marking, final_marking)
    precision = pm4py.precision_token_based_replay(log, petri_net, initial_marking, final_marking)
    generalization = generalization_evaluator.apply(log, petri_net, initial_marking, final_marking)
    simplicity = simplicity_evaluator.apply(petri_net)

    print('''
    ====================
    = model evaluation =
    ====================
    ''')
    print("fitness")
    print("\taverage_trace_fitness:%.3f" % fitness['average_trace_fitness'])
    print("\tpercentage_of_fitting_traces:%.3f" % fitness['percentage_of_fitting_traces'])

    print("precision:%.3f" % precision)

    print("generalization:%.3f" % generalization)

    print("simplicity:%.3f" % simplicity)

    return fitness, precision, generalization, simplicity


'''
功能：过程发现
'''
def process_discovery_for_single_repo(repo: str, scene: str):
    filename = f"{repo}_{scene}"
    log_path = f"{LOG_SINGLE_SCENE_DIR}/{filename}.csv"
    heuristics_net_filepath = f"{HEURISTICS_NET_DIR}/{filename}_heuristics_net.png"
    petri_net_filename = f"{PETRI_NET_DIR}/{filename}_petri_net"

    # 加载事件日志
    log = pd.read_csv(log_path, parse_dates=['time:timestamp'])

    # 过程发现
    petri_net, im, fm = heuristics_mining(log, heuristics_net_filepath, petri_net_filename)

    # 模型评估
    model_evaluation(log, petri_net, im, fm)


def process_discovery_for_single_scene(repos: List, scene: str, algorithm: str, params):
    log = pd.DataFrame()
    for repo in repos:
        input_path = f"{LOG_SINGLE_SCENE_DIR}/{repo}_{scene}.csv"
        if not os.path.exists(input_path):
            print(f"{input_path} don't exist")
            continue
        df = pd.read_csv(input_path, parse_dates=['time:timestamp'])
        log = pd.concat([log, df], ignore_index=True)

    if log.shape[0] == 0:
        print("log is empty, can't execute process discovery")
        return

    print("log case: %s" % len(log['case:concept:name'].unique()))

    # 过滤包含低频行为(ReopenPR, PRReviewDismiss)的案例
    log = pm4py.filter_event_attribute_values(log, 'concept:name', ['ReopenPR', 'PRReviewDismiss'], level="case", retain=False)
    rest_case = len(log['case:concept:name'].unique())
    print("filter_event_attribute_values (ReopenPR, PRReviewDismiss)")
    print("rest case: %d" % rest_case)

    if rest_case == 0:
        print("log is empty, can't execute process discovery")
        return

    heuristics_net_filepath = f"{HEURISTICS_NET_DIR}/{scene}_heuristics_net.png"
    petri_net_filename = f"{PETRI_NET_DIR}/{scene}_petri_net"
    bpmn_filename = f"{BPMN_DIR}/{scene}_bpmn"

    # 过程发现
    if algorithm == 'heuristics-mining':
        # 解析参数
        param_dic = {
            "dependency_threshold": 0.5,
            "and_threshold": 0.65,
            "loop_two_threshold": 0.5
        }
        for p in params:
            pArr = p.split("=")
            if pArr[0] in param_dic:
                param_dic[pArr[0]] = float(pArr[1])
        # 执行过程发现
        petri_net, im, fm = heuristics_mining(log,
                                              heuristics_net_filepath,
                                              petri_net_filename,
                                              bpmn_filename,
                                              param_dic)

    elif algorithm == 'alpha-mining':
        petri_net, im, fm = alpha_mining(log,
                                         petri_net_filename,
                                         bpmn_filename,
                                         process_tree_filename)

    elif algorithm == 'inductive-mining':
        # 解析参数
        param_dic = {
            "noise_threshold": 0.0
        }
        for p in params:
            pArr = p.split("=")
            if pArr[0] in param_dic:
                param_dic[pArr[0]] = float(pArr[1])
        # 执行过程发现
        petri_net, im, fm = inductive_mining(log,
                                             petri_net_filename,
                                             bpmn_filename,
                                             param_dic)

    else:
        print("暂未支持的过程发现算法")
        return

    # 模型评估
    fitness, precision, generalization, simplicity = model_evaluation(log, petri_net, im, fm)

    # 结果保存到数据库
    save_process_model(scene, rest_case, algorithm, algorithm_param, fitness, precision, generalization, simplicity, petri_net_filename)


def save_process_model(scene, log_case, algorithm, algorithm_param, fitness, precision, generalizaion, simplicity, petri_net):
    # 1.先查询数据库中是否已有该场景的记录
    sql = f"select * from process_model where scene='{scene}'"
    data = select_all(sql)
    if len(data) > 0:
        # 2.删除旧记录
        sql = f"delete from process_model where scene='{scene}'"
        execute_sql(sql)
        print("旧记录删除完成")
    # 3.执行插入操作
    model_data = (
        scene,
        log_case,
        algorithm,
        json.dumps(algorithm_param),
        fitness['average_trace_fitness'],
        fitness['percentage_of_fitting_traces'],
        precision,
        generalizaion,
        simplicity,
        petri_net
    )
    insert_into_process_model(model_data)
    print("新记录添加完成")


if __name__ == "__main__":
    import sys

    params = []
    for i in range(1, len(sys.argv)):
        params.append((sys.argv[i]))

    # 解析参数
    projects = params[0].split(',')
    scenes = params[1].split(',')
    algorithm = params[2]
    algorithm_param = params[3].split(',')
    DATA_DIR = params[4]

    # 文件路径
    LOG_SINGLE_SCENE_DIR = DATA_DIR + "/log_single_scene"
    HEURISTICS_NET_DIR = DATA_DIR + "/process_model/heuristics_net"
    PETRI_NET_DIR = DATA_DIR + "/process_model/petri_net"
    BPMN_DIR = DATA_DIR + "/process_model/bpmn"

    for scene in scenes:
        process_discovery_for_single_scene(projects, scene, algorithm, algorithm_param)
        print(f"{scene} process done")
