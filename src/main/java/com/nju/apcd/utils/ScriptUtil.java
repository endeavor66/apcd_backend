package com.nju.apcd.utils;

import cn.hutool.core.io.FileUtil;
import com.nju.apcd.constant.Constants;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class ScriptUtil {
    public static void execute_command(String[] commands){
        try {
            Process proc = Runtime.getRuntime().exec(commands); // 执行py文件

            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GB2312"));
                    String line = null;

                    try {
                        while ((line = in.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            new Thread() {
                @SneakyThrows
                @Override
                public void run() {
                    BufferedReader in = new BufferedReader(new InputStreamReader(proc.getErrorStream(), "GB2312"));
                    String line = null;

                    try {
                        while ((line = in.readLine()) != null) {
                            System.out.println(line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();

            proc.waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void data_extract_from_bigquery(String projectList){
        File file = FileUtil.file("classpath:scripts/DataAcquire/data_extract_from_bigquery.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python3", scriptPath, projectList, Constants.BIGQUERY_DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void bigquery_process(String projectList, String start, String end){
        File file = FileUtil.file("classpath:scripts/DataAcquire/bigquery_process.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                projectList, start, end, Constants.EVENT_LOG_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void event_process(String projectList){
        File file = FileUtil.file("classpath:scripts/DataAcquire/event_process.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                projectList, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void permission_change(String projectList){
        File file = FileUtil.file("classpath:scripts/DataAcquire/permission_change.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                projectList, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void process_discovery(String projectList, String sceneList, String algorithm, String param){
        File file = FileUtil.file("classpath:scripts/ProcessMining/process_discovery.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                projectList, sceneList, algorithm, param, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    // TODO 需要数据完全后验证
    public static void conformance_checking(String project, String algorithm){
        File file = FileUtil.file("classpath:scripts/ProcessMining/conformance_checking.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                project, algorithm, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void committer_feature_extract(String project, String start, String end){
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/committer_feature_extract.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                project, start, end, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void maintainer_feature_extract(String project, String start, String end){
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/maintainer_feature_extract.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                project, start, end, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void reviewer_feature_extract(String project, String start, String end){
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/reviewer_feature_extract.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                project, start, end, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void anomaly_detection(String project, String scenes, String algorithms){
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/anomaly_detection.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                project, scenes, algorithms, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }

    public static void multi_model_vote(String project, String scenes, String algorithms){
        File file = FileUtil.file("classpath:scripts/AnomalyDetection/multi_model_vote.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath,
                project, scenes, algorithms, Constants.DATA_DIR};
        ScriptUtil.execute_command(commands);
    }
}
