package com.nju.apcd;

import cn.hutool.core.io.FileUtil;
import com.nju.apcd.constant.Constants;
import com.nju.apcd.utils.ScriptUtil;

import java.io.File;

public class PythonTest {
    public static void main(String[] args) {
        File file = FileUtil.file("classpath:scripts/DataAcquire/data_extract_from_bigquery.py");
        String scriptPath = file.getPath();

        String[] commands = new String[]{"python", scriptPath, "apache/dubbo#gpac/gpac", "2021-01-01", "2021-01-02", Constants.BIGQUERY_DATA_DIR};
        ScriptUtil.execute_command(commands);
    }
}
