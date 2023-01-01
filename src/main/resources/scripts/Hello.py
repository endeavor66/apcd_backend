from utils.Config import *
import sys
import os

if __name__ == '__main__':
    params = []
    for i in range(1, len(sys.argv)):
        params.append((sys.argv[i]))
    # 打印传入的参数
    print(params)
    print(CONSTANTS)

    filepath = "hellohello/1.txt"
    if os.path.exists(filepath):
        print("exist")
    else:
        print("don't exist")