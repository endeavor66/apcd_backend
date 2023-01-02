package com.nju.apcd.utils;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ScriptUtil {
    //public static void execute_command(String[] commands){
    //    try {
    //        Process proc = Runtime.getRuntime().exec(commands); // 执行py文件
    //
    //        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GB2312"));
    //        String line = null;
    //        while ((line = in.readLine()) != null) {
    //            System.out.println(line);
    //        }
    //        in.close();
    //
    //        // 输出错误流
    //        BufferedReader err = new BufferedReader(new InputStreamReader(proc.getErrorStream(), "GB2312"));
    //        while ((line = err.readLine()) != null) {
    //            System.out.println(line);
    //        }
    //
    //        err.close();
    //
    //        proc.waitFor();
    //    } catch (IOException | InterruptedException e) {
    //        e.printStackTrace();
    //    }
    //}

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
}
