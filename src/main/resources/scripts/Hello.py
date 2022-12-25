import sys

if __name__ == '__main__':
    params = []
    for i in range(1, len(sys.argv)):
        params.append((sys.argv[i]))
    # 打印传入的参数
    print(params)