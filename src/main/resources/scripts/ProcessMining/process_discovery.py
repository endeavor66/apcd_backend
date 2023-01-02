import pm4py
import os
import pandas as pd
from typing import Union, List
from pm4py.objects.log.obj import EventLog
from pm4py.algo.evaluation.generalization import algorithm as generalization_evaluator
from pm4py.algo.evaluation.simplicity import algorithm as simplicity_evaluator
from Config import *

def inductive_mining(log: Union[EventLog, pd.DataFrame], petri_net_filename: str):
    petri_net, initial_marking, final_marking = pm4py.discover_petri_net_inductive(log)
    pm4py.write_pnml(petri_net, initial_marking, final_marking, f"{petri_net_filename}.pnml")
    pm4py.save_vis_petri_net(petri_net, initial_marking, final_marking, f"{petri_net_filename}.png")
    return petri_net, initial_marking, final_marking

def alpha_mining(log: Union[EventLog, pd.DataFrame], petri_net_filename: str):
    petri_net, initial_marking, final_marking = pm4py.discover_petri_net_alpha(log)
    pm4py.write_pnml(petri_net, initial_marking, final_marking, f"{petri_net_filename}.pnml")
    pm4py.save_vis_petri_net(petri_net, initial_marking, final_marking, f"{petri_net_filename}.png")
    return petri_net, initial_marking, final_marking


'''
功能：启发式算法
'''
def heuristics_mining(log: Union[EventLog, pd.DataFrame], heuristics_net_filepath: str, petri_net_filename: str):
    # 恢复heuristics_net
    heuristics_net = pm4py.discover_heuristics_net(log,
                                                   dependency_threshold=0.5,
                                                   and_threshold=0.65,
                                                   loop_two_threshold=0.5)
    pm4py.save_vis_heuristics_net(heuristics_net, heuristics_net_filepath)

    # 转化为Petri-Net
    petri_net, initial_marking, final_marking = pm4py.convert_to_petri_net(heuristics_net)
    pm4py.write_pnml(petri_net, initial_marking, final_marking, f"{petri_net_filename}.pnml")
    pm4py.save_vis_petri_net(petri_net, initial_marking, final_marking, f"{petri_net_filename}.png")

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


def process_discovery_for_single_scene(repos: List, scene: str, algorithm: str):
    log = pd.DataFrame()
    for repo in repos:
        input_path = f"{LOG_SINGLE_SCENE_DIR}/{repo}_{scene}.csv"
        if not os.path.exists(input_path):
            print(f"{input_path} don't exist")
            continue
        df = pd.read_csv(input_path, parse_dates=['time:timestamp'])
        log = pd.concat([log, df], ignore_index=True)

    print("log case: %s" % len(log['case:concept:name'].unique()))

    # 过滤包含低频行为(ReopenPR, PRReviewDismiss)的案例
    log = pm4py.filter_event_attribute_values(log, 'concept:name', ['ReopenPR', 'PRReviewDismiss'], level="case", retain=False)
    print("filter_event_attribute_values (ReopenPR, PRReviewDismiss)")
    print("rest case: %d" % len(log['case:concept:name'].unique()))

    # 过程发现
    if algorithm == 'heuristics mining':
        heuristics_net_filepath = f"{HEURISTICS_NET_DIR}/{scene}_heuristics_net.png"
        petri_net_filename = f"{PETRI_NET_DIR}/{scene}_petri_net_heuristics"
        petri_net, im, fm = heuristics_mining(log, heuristics_net_filepath, petri_net_filename)

    elif algorithm == 'alpha mining':
        petri_net_filename = f"{PETRI_NET_DIR}/{scene}_petri_net_alpha"
        petri_net, im, fm = alpha_mining(log, petri_net_filename)

    elif algorithm == 'inductive mining':
        petri_net_filename = f"{PETRI_NET_DIR}/{scene}_petri_net_inductive"
        petri_net, im, fm = inductive_mining(log, petri_net_filename)

    else:
        print("暂未支持的过程发现算法")
        return

    # 模型评估
    model_evaluation(log, petri_net, im, fm)


if __name__ == "__main__":
    import sys

    params = []
    for i in range(1, len(sys.argv)):
        params.append((sys.argv[i]))

    # 解析参数
    projects = params[0].split('#')
    scene = params[1]
    algorithm = params[2]
    DATA_DIR = params[3]

    # 文件路径
    LOG_SINGLE_SCENE_DIR = DATA_DIR + "/log_single_scene"
    HEURISTICS_NET_DIR = DATA_DIR + "/process_model/heuristics_net"
    PETRI_NET_DIR = DATA_DIR + "/process_model/petri_net"

    repos = []
    for pro in projects:
        repo = pro.split('/')[1]
        repos.append(repo)

    process_discovery_for_single_scene(repos, scene, algorithm)
    print(f"{scene} process done")
