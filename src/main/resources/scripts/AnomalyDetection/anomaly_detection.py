import pandas as pd
import numpy as np
from sklearn.ensemble import IsolationForest
from sklearn.neighbors import LocalOutlierFactor
from sklearn import svm
from sklearn.preprocessing import MinMaxScaler
from Config import *
import os
from typing import List


'''
功能：基于孤立森林算法来检测x是否为异常点
'''
def isolation_forest(input_path: str, output_path: str):
    # 准备数据
    df = pd.read_csv(input_path)
    data = df.iloc[:, 1:]

    # 训练模型
    model = IsolationForest(random_state=0)
    model.fit(data)

    # 异常程度: 值越小，越异常(默认以0作为判断异常点的阈值)
    score = model.decision_function(data)
    # +1 for inliers, -1 for outliers
    anomaly = model.predict(data)

    # 模型预测
    df['score'] = score

    # 选择5%作为异常数据，-1 for outlier, 1 for inlier
    threshold = np.percentile(df['score'], (ANOMALY_PERCENT), interpolation='nearest')
    df['anomaly'] = df['score'].apply(lambda x: -1 if x < threshold else 1)

    # 保存文件
    return df.to_csv(output_path, index=False, header=True)


'''
功能：归一化
'''
def min_max_scale(data: pd.DataFrame):
    scaler = MinMaxScaler()
    new_data = scaler.fit_transform(data)
    return new_data


'''
功能：One Class SVM 异常检测算法
'''
def one_class_svm(input_path: str, output_path: str):
    # 准备数据
    df = pd.read_csv(input_path)
    data = df.iloc[:, 1:]

    # 数据预处理: 归一化
    data = min_max_scale(data)

    # 训练模型
    model = svm.OneClassSVM()
    model.fit(data)

    # 值越小，越异常
    df['score'] = model.decision_function(data)

    # 选择5%作为异常数据，-1 for outlier, 1 for inlier
    threshold = np.percentile(df['score'], (ANOMALY_PERCENT), interpolation='nearest')
    df['anomaly'] = df['score'].apply(lambda x: -1 if x < threshold else 1)

    df.to_csv(output_path, index=False, header=True)


'''
功能：LOF 异常检测算法
'''
def lof(input_path: str, output_path: str):
    # 准备数据
    df = pd.read_csv(input_path)
    data = df.iloc[:, 1:]

    # 数据预处理: 归一化
    data = min_max_scale(data)

    # 训练模型
    clf = LocalOutlierFactor(novelty=False, contamination=0.05)
    clf.fit(data)

    # 值越小，越异常
    df['score'] = clf.negative_outlier_factor_
    # 选择5%作为异常数据，-1 for outlier, 1 for inlier
    threshold = np.percentile(df['score'], (ANOMALY_PERCENT), interpolation='nearest')
    df['anomaly'] = df['score'].apply(lambda x: -1 if x < threshold else 1)

    # 保存结果
    df.to_csv(output_path, index=False, header=True)


'''
功能：基于箱线图来分析异常原因
'''
def boxplot(repo: str, role: str):
    input_path = f"{FEATURE_DIR}/{repo}_{role}_feature.csv"
    output_path = f"{BOX_PLOT_DIR}/{repo}_{role}_box_plot.csv"

    if not os.path.exists(input_path):
        print(f"{input_path} don't exist")
        return

    df = pd.read_csv(input_path)
    columns = df.columns.values.tolist()

    # 每一列计算上下四分位
    statistics = []
    for index, column in df.iteritems():
        if index == 'people':
            continue
        res = np.percentile(column, (25, 50, 75), interpolation='midpoint')
        mean = column.mean()
        statistics.append([res[0], res[1], res[2], mean])

    # 保存各个属性的上下四分位数、中位数、平均值
    statistics_T = np.array(statistics).transpose()
    df_file = pd.DataFrame(data=statistics_T, columns=columns[1:])
    df_file.insert(0, 'scene', ['25%', '50%', '75%', 'mean'])
    df_file.to_csv(output_path, index=False, header=True)


'''
功能：采用高维度算法检测异常点，低维度算法分析异常点的各个属性是否异常(分析异常原因)
'''
def anomaly_detection(repo: str, role: str, algorithms: List):
    input_path = f"{FEATURE_DIR}/{repo}_{role}_feature.csv"

    if not os.path.exists(input_path):
        print(f"{input_path} don't exist!")
        return

    if "isolation forest" in algorithms:
        # 高维度检测异常值：孤立森林
        output_path = f"{ISOLATION_FOREST_DIR}/{repo}_{role}_isolation_forest.csv"
        isolation_forest(input_path, output_path)

    if "one class svm" in algorithms:
        # 高维度检测异常值：One Class SVM
        output_path = f"{ONE_CLASS_SVM_DIR}/{repo}_{role}_one_class_svm.csv"
        one_class_svm(input_path, output_path)

    if "lof" in algorithms:
        # 高维度检测异常值：LOF
        output_path = f"{LOF_DIR}/{repo}_{role}_lof.csv"
        lof(input_path, output_path)

    # 低维度计算每个属性的上下四分位
    boxplot(repo, role)


if __name__ == '__main__':
    import sys

    params = []
    for i in range(1, len(sys.argv)):
        params.append((sys.argv[i]))

    # 解析参数
    pro = params[0]
    roles = params[1].split(',')
    algorithms = params[2].split(',')
    DATA_DIR = params[3]

    # 文件目录
    FEATURE_DIR = DATA_DIR + '/anomaly_detection/feature'
    ISOLATION_FOREST_DIR = DATA_DIR + '/anomaly_detection/isolation_forest'
    ONE_CLASS_SVM_DIR = DATA_DIR + '/anomaly_detection/one_class_svm'
    LOF_DIR = DATA_DIR + '/anomaly_detection/lof'
    BOX_PLOT_DIR = DATA_DIR + '/anomaly_detection/box_plot'

    repo = pro
    if pro.find("/") != -1:
        repo = pro[pro.index("/")+1:]
    for role in roles:
        anomaly_detection(repo, role, algorithms)
        print(f"{repo} {role} process done")
