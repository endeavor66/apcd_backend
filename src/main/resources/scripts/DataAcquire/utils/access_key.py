import os

# 获取本地文件中的access_tocken相关值
def get_token():
    filepath = "D:\\IdeaProjects\\apcd\\src\\main\\resources\\scripts\\DataAcquire\\utils\\resources\\token.txt"
    with open(filepath, 'r') as reader:
        token = reader.readline()
    return token


# 获取本地文件中的access_tocken相关值
def get_mysql_root_psw():
    filepath = "D:\\IdeaProjects\\apcd\\src\\main\\resources\\scripts\\DataAcquire\\utils\\resources\\mysql.txt"
    with open(filepath, 'r') as reader:
        username = reader.readline().strip("\n")
        password = reader.readline().strip("\n")
    return username, password


