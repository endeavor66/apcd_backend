import pandas as pd
import os
from typing import List
from datetime import datetime, timedelta
from utils.pr_self_utils import get_all_pr_number_between
from utils.mysql_utils import batch_insert_into_permission_change


'''
功能：寻找所有权限变更发生的时刻
'''
def permission_change(input_path: str, unfork_pr_list: List):
    df = pd.read_csv(input_path, parse_dates=['time:timestamp'])
    role_change = []
    for person, group in df.groupby('People'):
        group.sort_values(by='time:timestamp', inplace=True)

        # 判断是否获得过reviewer权限
        df_reviewer = group.loc[group['concept:name'].isin(['PRReviewApprove', 'PRReviewReject'])]
        if df_reviewer.shape[0] > 0:
            row = df_reviewer.iloc[0]
            role_change.append([row['People'], row['case:concept:name'], row['time:timestamp'], 'Reviewer'])

        # 判断是否获得过maintainer权限
        df_maintainer = group.loc[group['concept:name'].isin(['MergePR', 'ClosePR'])]
        if df_maintainer.shape[0] > 0:
            row = df_maintainer.iloc[0]
            role_change.append([row['People'], row['case:concept:name'], row['time:timestamp'], 'Maintainer'])

    # 筛选出所有"协作者模式"的PR，判断committer权限变更
    df = df.loc[df['case:concept:name'].isin(unfork_pr_list)]
    for person, group in df.groupby('People'):
        df_committer = group.loc[group['concept:name'].isin(['SubmitCommit', 'Revise'])]
        if df_committer.shape[0] > 0:
            row = df_committer.iloc[0]
            role_change.append([row['People'], row['case:concept:name'], row['time:timestamp'], 'Committer'])

    return role_change


'''
功能：找出所有的采用协作者模式开发的PR
'''
def cal_unfork_pr_list(repo: str):
    unfork_pr_list = []
    for scene in ['unfork_merge', 'unfork_close']:
        input_path = f"{LOG_SINGLE_SCENE_DIR}/{repo}_{scene}.csv"
        if not os.path.exists(input_path):
            print(f"{input_path} don't exist")
            continue
        df = pd.read_csv(input_path)
        pr_list = df['case:concept:name'].unique()
        unfork_pr_list.extend(pr_list)
        del df
    return unfork_pr_list


'''
功能：权限变更识别流程自动化
'''
def auto_analysis(repo: str):
    output_path = f"{ROLE_CHANGE_DIR}/{repo}_role_change.csv"
    role_change = []

    # 获取unfork_pr_list
    unfork_pr_list = cal_unfork_pr_list(repo)

    # 读取log文件，识别权限变更信息
    input_path = f"{LOG_ALL_SCENE_DIR}/{repo}.csv"
    result = permission_change(input_path, unfork_pr_list)
    role_change.extend(result)

    # 保存为文件
    df_file = pd.DataFrame(data=role_change, columns=['people', 'change_pr_number', 'change_role_time', 'change_role'])
    df_file.to_csv(output_path, index=False, header=True)



'''
功能：确定一个特定的person在权限发生变更之后一段时间内所参与的所有PR，(并在这些PR中使用了新的权限)
'''
def cal_involved_pr(repo: str, person: str, change_role_time: datetime):
    # 获取权限变更后一个月内的PR
    end_time = change_role_time + timedelta(days=30)
    pr_number_list = get_all_pr_number_between(repo, change_role_time, end_time)

    # 从log中筛选出权限变更人在权限变更后一个月内参与的所有PR
    role_info_path = f"{LOG_ALL_SCENE_DIR}/{repo}.csv"
    df = pd.read_csv(role_info_path, parse_dates=['time:timestamp'])
    pr_df = df.loc[(df['People'] == person) & df['case:concept:name'].isin(pr_number_list)]
    involved_pr_list = pr_df['case:concept:name'].unique()
    return "#".join(str(x) for x in involved_pr_list)


'''
功能：确定一个仓库中所有发生权限变更的person，在权限变更后一段时间内所参与的所有PR，并在这些PR中使用了新的权限
'''
def cal_involved_pr_after_permission_change(repo: str):
    role_change_path = f"{ROLE_CHANGE_DIR}/{repo}_role_change.csv"
    df_role_change = pd.read_csv(role_change_path, parse_dates=['change_role_time'])
    df_role_change['involved_pr_after_permission_change'] = df_role_change.apply(lambda x: cal_involved_pr(repo, x['people'], x['change_role_time']), axis=1)
    df_role_change.to_csv(role_change_path, index=False, header=True)


# 将权限变更信息保存到数据库 permission_change
def save(repo: str):
    filepath = f"{ROLE_CHANGE_DIR}/{repo}_role_change.csv"
    if not os.path.exists(filepath):
        print(f"{filepath} don't exist")
        return

    df = pd.read_csv(filepath)
    datas = []
    for index, row in df.iterrows():
        t = (
            repo,
            row['people'],
            row['change_pr_number'],
            row['change_role_time'],
            row['change_role']
        )
        datas.append(t)
    batch_insert_into_permission_change(repo, datas)


if __name__ == '__main__':
    import sys

    params = []
    for i in range(1, len(sys.argv)):
        params.append((sys.argv[i]))

    # 解析参数
    projects = params[0].split(',')
    DATA_DIR = params[1]

    # 文件路径
    LOG_SINGLE_SCENE_DIR = DATA_DIR + "/log_single_scene"
    LOG_ALL_SCENE_DIR = DATA_DIR + "/log_all_scene"
    ROLE_CHANGE_DIR = DATA_DIR + "/role_change"

    # 执行
    for pro in projects:
        repo = pro.split('/')[1]
        auto_analysis(repo)
        cal_involved_pr_after_permission_change(repo)
        save(repo)
        print(f"repo#{repo} process done")