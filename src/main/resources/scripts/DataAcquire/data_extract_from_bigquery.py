import pandas as pd
import json
from utils.mysql_utils import batch_insert_into_events
from datetime import datetime, timedelta
import os


'''
功能：从commits中提取所有commit的SHA，拼接为字符串后返回(#连接)，格式：SHA1#SHA2#SHA3
'''
def join_commits_sha(commits: str) -> str:
    if commits is None:
        return None
    try:
        commits_dic_list = json.loads(commits)
        commit_sha = []
        for commit in commits_dic_list:
            commit_sha.append(commit['sha'])
        return "#".join(commit_sha)
    except Exception as e:
        print(e)
        return ""


'''
功能：如果v是(Nan, None, NaN, NaT), 统一转换为None; 否则返回原值
'''
def check(v):
    if pd.isna(v):
        return None
    # 如果v是字符串，且首尾带引号，去掉引号; 否则直接返回
    if isinstance(v, str) and v[0] == "\"" and v[-1] == "\"":
        v = v[1:-1]
    return v


'''
功能：从csv文件(文件路径为filepath)中提取所有事件，保存到数据库
'''
def extract_data_from_bigquery_csv(filepath: str, repo: str):
    # 1.从csv文件中筛选出repo及其fork仓的所有相关事件(形式: repo_name like '%/repo')
    df = pd.read_csv(filepath)
    df = df.loc[df.repo_name.str.endswith(repo)]
    df2 = df.drop_duplicates()
    print(f"{filepath}去除重复数据:%d, 剩余数据:%d" % ((df.shape[0] - df2.shape[0]), df2.shape[0]))
    # 2.提取关键活动
    datas = []
    for index, event in df2.iterrows():
        t = (check(event['id']),
             check(event['type']),
             check(event['public']),
             check(event['created_at']),  # 由于操作失误，2021年的数据该字段为 create_at, 2022年的数据该字段为 created_at
             check(event['actor_id']),
             check(event['actor_login']),
             check(event['repo_id']),
             check(event['repo_name']),
             check(event['payload_ref']),
             check(event['payload_ref_type']),
             check(event['payload_pusher_type']),
             check(event['payload_push_id']),
             check(event['payload_size']),
             check(event['payload_distinct_size']),
             join_commits_sha(check(event['payload_commits'])),
             check(event['payload_action']),
             check(event['payload_pr_number']),
             check(event['payload_forkee_full_name']),
             check(event['payload_changes']),
             check(event['payload_review_state']),
             check(event['payload_review_author_association']),
             check(event['payload_member_id']),
             check(event['payload_member_login']),
             check(event['payload_member_type']),
             check(event['payload_member_site_admin'])
             )
        datas.append(t)
    # 3.保存到数据库
    batch_insert_into_events(repo, datas)


def auto_process(projects, start_time, end_time, dir_path):
    for pro in projects:
        repo = pro.split('/')[1]
        index = 0
        start = start_time
        end = end_time
        while start < end:
            cur = start.strftime("%Y%m%d")
            filepath = f"{dir_path}/{cur}.csv"
            if not os.path.exists(filepath):
                print(f"{filepath} does not exist")
                start = start + timedelta(days=1)
                continue
            extract_data_from_bigquery_csv(filepath, repo)
            print(f"{filepath} process done")
            start = start + timedelta(days=1)
            index += 1
        print(f"repo#{repo} process done, 共处理{index}份文件")


if __name__ == '__main__':
    import sys

    params = []
    for i in range(1, len(sys.argv)):
        params.append((sys.argv[i]))

    # 解析参数
    projects = params[0].split('#')
    start_time = datetime.strptime(params[1], "%Y-%m-%d")
    end_time = datetime.strptime(params[2], "%Y-%m-%d")
    dir_path = params[3]

    # 执行
    auto_process(projects, start_time, end_time, dir_path)