import pymysql
from typing import List
from utils.access_key import get_mysql_root_psw
from utils.exception_handdle import write_file

EVENT_TABLE_FIELDS = [
        "id",
        "type",
        "public",
        "created_at",
        "actor_id",
        "actor_login",
        "repo_id",
        "repo_name",
        "payload_ref",
        "payload_ref_type",
        "payload_pusher_type",
        "payload_push_id",
        "payload_size",
        "payload_distinct_size",
        "payload_commits",
        "payload_action",
        "payload_pr_number",
        "payload_forkee_full_name",
        "payload_changes",
        "payload_review_state",
        "payload_review_author_association",
        "payload_member_id",
        "payload_member_login",
        "payload_member_type",
        "payload_member_site_admin"]
COMMIT_TABLE_FIELDS = [
    "pr_number",
    "sha",
    "author",
    "author_email",
    "author_date",
    "committer",
    "committer_email",
    "committer_date",
    "message",
    "line_addition",
    "line_deletion",
    "file_edit_num",
    "file_content"]
PROCESS_EVENT_TABLE_FIELDS = [
    "repo",
    "pr_number",
    "activity",
    "created_at",
    "people",
    "scene"
]
PERMISSION_CHANGE_TABLE_FIELDS = [
    "repo",
    "people",
    "pr_number",
    "change_time",
    "permission"
]
PROCESS_MODEL_TABLE_FIELDS = [
    "scene",
    "log_case",
    "algorithm",
    "param",
    "average_trace_fitness",
    "percentage_of_fitting_traces",
    "precision",
    "generalization",
    "simplicity",
    "petri_net"
]

# 获取数据库连接对象
username, password = get_mysql_root_psw()
conn = pymysql.connect(host='127.0.0.1', port=3306, user=username, password=password, db='poison', charset='utf8', cursorclass=pymysql.cursors.DictCursor)


"""
功能：批量插入到repo_events表中
"""
def batch_insert_into_events(repo: str, data: List):
    table = f"{repo.replace('-', '_')}_events"
    if len(data) == 0:
        print(f"数据为空，不需要插入到{table}表")
        return
    fields = ",".join(EVENT_TABLE_FIELDS)
    fields_param = ("%s," * len(EVENT_TABLE_FIELDS))[0:-1]
    sql = f"insert into `{table}` ({fields}) values({fields_param})"
    # print("执行SQL: " + sql)

    cursor = conn.cursor()
    try:
        conn.ping(reconnect=True)
        result = cursor.executemany(sql, data)
        conn.commit()
        cursor.close()
        # 如果在一次程序运行过程中多次调用该函数，可能会出问题，conn.close()关闭后后续可能无法获取连接
        # conn.close()
        print(f"操作成功，{table}表中插入{result}条数据")
    except Exception as e:
        conn.rollback()
        cursor.close()
        conn.close()
        print(f"批量插入{table}表时, 执行出错:{e}")
        write_file(exception=str(e), filename=repo + "_exception.csv")


"""
功能：批量插入到process_events表中
"""
def batch_insert_into_process_events(repo: str, data: List):
    if len(data) == 0:
        print("数据为空, 不需要插入到process_event表")
        return
    fields = ",".join(PROCESS_EVENT_TABLE_FIELDS)
    fields_param = ("%s," * len(PROCESS_EVENT_TABLE_FIELDS))[0:-1]
    sql = f"insert into process_events ({fields}) values({fields_param})"
    # print("执行SQL: " + sql)

    cursor = conn.cursor()
    try:
        conn.ping(reconnect=True)
        result = cursor.executemany(sql, data)
        conn.commit()
        cursor.close()
        # 如果在一次程序运行过程中多次调用该函数，可能会出问题，conn.close()关闭后后续可能无法获取连接
        # conn.close()
        print("操作成功, process_event表中插入%d条数据" % result)
    except Exception as e:
        conn.rollback()
        cursor.close()
        conn.close()
        print("批量插入到process_event表时, 执行出错:" + str(e))
        write_file(exception=str(e), filename=repo + "_exception.csv")


"""
功能：批量插入到permission_change表中
"""
def batch_insert_into_permission_change(repo: str, data: List):
    if len(data) == 0:
        print("数据为空，不需要插入到permission_change表")
        return
    fields = ",".join(PERMISSION_CHANGE_TABLE_FIELDS)
    fields_param = ("%s," * len(PERMISSION_CHANGE_TABLE_FIELDS))[0:-1]
    sql = f"insert into permission_change ({fields}) values({fields_param})"
    # print("执行SQL: " + sql)

    cursor = conn.cursor()
    try:
        conn.ping(reconnect=True)
        result = cursor.executemany(sql, data)
        conn.commit()
        cursor.close()
        # 如果在一次程序运行过程中多次调用该函数，可能会出问题，conn.close()关闭后后续可能无法获取连接
        # conn.close()
        print("操作成功，permission_change表中插入%d条数据" % result)
    except Exception as e:
        conn.rollback()
        cursor.close()
        conn.close()
        print("批量插入到permission_change表时执行出错:" + str(e))
        write_file(exception=str(e), filename=repo + "_exception.csv")


'''
功能：插入一条记录到repo_commit表中
'''
def insert_into_commit(repo: str, data: List):
    table = f"{repo.replace('-', '_')}_commit"
    fields = ",".join(COMMIT_TABLE_FIELDS)
    fields_param = ("%s," * len(COMMIT_TABLE_FIELDS))[0:-1]
    sql = f"insert into `{table}` ({fields}) values({fields_param})"
    # print("执行SQL: " + sql)

    cursor = conn.cursor()
    result = 0
    try:
        conn.ping(reconnect=True)
        result = cursor.execute(sql, data)
        conn.commit()
        cursor.close()
        # 如果在一次程序运行过程中多次调用该函数，可能会出问题，conn.close()关闭后后续可能无法获取连接
        # conn.close()
        print(f"操作成功, {table}表中插入{result}条数据")
    except Exception as e:
        conn.rollback()
        cursor.close()
        conn.close()
        write_file(exception=str(e), filename=repo + "_exception.csv")
    return result


'''
功能：插入一条记录到process_model表中
'''
def insert_into_process_model(data: List):
    fields = ",".join(PROCESS_MODEL_TABLE_FIELDS)
    fields_param = ("%s," * len(PROCESS_MODEL_TABLE_FIELDS))[0:-1]
    sql = f"insert into process_model ({fields}) values({fields_param})"

    cursor = conn.cursor()
    result = 0
    try:
        conn.ping(reconnect=True)
        result = cursor.execute(sql, data)
        conn.commit()
        cursor.close()
        # 如果在一次程序运行过程中多次调用该函数，可能会出问题，conn.close()关闭后后续可能无法获取连接
        # conn.close()
        print(f"操作成功, process_model表中插入{result}条数据")
    except Exception as e:
        conn.rollback()
        cursor.close()
        conn.close()
    return result


'''
功能：执行sql，没有返回值
'''
def execute_sql(sql):
    conn.ping(reconnect=True)
    cursor = conn.cursor()
    cursor.execute(sql)
    print(cursor.rowcount)
    cursor.close()
    conn.close()


"""
对table执行sql查询
"""
def select_all(sql):
    # print("执行SQL: " + sql)
    conn.ping(reconnect=True)
    cursor = conn.cursor()
    cursor.execute(sql)
    data = cursor.fetchall()
    cursor.close()
    conn.close()
    return data


def select_one(sql):
    # print("执行SQL: " + sql)
    conn.ping(reconnect=True)
    cursor = conn.cursor()
    cursor.execute(sql)
    data = cursor.fetchone()
    cursor.close()
    conn.close()
    return data
