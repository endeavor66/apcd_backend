package com.nju.apcd;

import cn.hutool.core.io.FileUtil;
import com.nju.apcd.constant.Constants;
import com.nju.apcd.utils.ScriptUtil;

import java.io.File;
import java.text.DecimalFormat;

public class PythonTest {
    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("0.00");
        String v = "0.99524234";
        String format = df.format(Double.parseDouble(v));
        System.out.println(format);
    }

    public static void data_extract_from_bigquery(){
        // 1.测试data_extract_from_bigquery.py
        File file = FileUtil.file("classpath:scripts/DataAcquire/data_extract_from_bigquery.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python3", scriptPath,
                "apache/dubbo#gpac/gpac", "2021-01-01", "2021-01-02", Constants.BIGQUERY_DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void bigquery_process(){
        // 2.测试data_extract_from_bigquery.py
        File file = FileUtil.file("classpath:scripts/DataAcquire/bigquery_process.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath, "apache/dubbo#gpac/gpac", "2021-01-01", "2021-02-01", Constants.EVENT_LOG_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void event_process(){
        // 3.测试 event_process.py
        File file = FileUtil.file("classpath:scripts/DataAcquire/event_process.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{
                "python", scriptPath, "apache/hadoop",
                Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void permission_change(){
        // 4.测试 permission_change.py
        File file = FileUtil.file("classpath:scripts/DataAcquire/permission_change.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath, "apache/dubbo", Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void process_discovery(){
        // 5.测试 process_discovery.py
        File file = FileUtil.file("classpath:scripts/ProcessMining/process_discovery.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{
                "python", scriptPath,
                "apache/dubbo#gpac/gpac", "fork_close", "heuristics_mining", Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    // TODO 需要数据完全后验证
    public static void conformance_checking(){
        // 6.测试 permission_change.py
        File file = FileUtil.file("classpath:scripts/ProcessMining/conformance_checking.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath, "apache/dubbo", Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void committer_feature_extract(){
        // 6.测试 permission_change.py
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/committer_feature_extract.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                "apache/dubbo", "2021-01-01", "2021-01-02", Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void maintainer_feature_extract(){
        // 6.测试 permission_change.py
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/maintainer_feature_extract.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                "apache/dubbo", "2021-01-01", "2021-01-02", Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void reviewer_feature_extract(){
        // 6.测试 permission_change.py
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/reviewer_feature_extract.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                "apache/dubbo", "2021-01-01", "2021-01-02", Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void anomaly_detection(){
        // 6.测试 permission_change.py
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/anomaly_detection.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                "apache/dubbo#gpac/gpac", "committer#reviewer#maintainer",
                "isolation forest#one class svm#lof", Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void multi_model_vote(){
        // 6.测试 permission_change.py
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/multi_model_vote.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                "apache/dubbo#gpac/gpac", "committer#reviewer#maintainer",
                "isolation forest#one class svm#lof", Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }
}
