package com.nju.apcd.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ScriptUtil {
    public static void execute_command(String[] commands){
        try {
            Process proc = Runtime.getRuntime().exec(commands); // 执行py文件

            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(), "GB2312"));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            // 输出错误流
            in = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            in.close();
            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
