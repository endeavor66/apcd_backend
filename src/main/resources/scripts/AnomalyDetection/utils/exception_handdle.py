# 处理无法获得的PR，然后记录到文件中，记录格式如下，时间|PRNumber，时间|PR编号
import os
import time

def write_file(exception, filename):
    current_path = 'data\\exception_data\\'  # 获取当前路径
    if not os.path.exists(current_path):
        os.makedirs(current_path)
    # print(current_path)
    filepath = current_path + filename  # 在当前路径创建名为test的文本文件
    now_time = time.strftime('%Y-%m-%d %H:%M:%S ', time.localtime(time.time()))  # 获取当前时间
    context = now_time + ', ' + exception
    print(context)

    with open(filepath, 'a+') as writer:
        writer.write(context)
