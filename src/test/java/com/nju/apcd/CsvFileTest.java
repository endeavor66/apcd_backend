package com.nju.apcd;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.alibaba.fastjson.JSON;

import java.util.List;

public class CsvFileTest {
    public static void main(String[] args) {
        String project = "zipkin";
        String scene = "fork_close";
        String filePath = "event_log/" + project + "_" + scene + ".csv";

        CsvReader reader = CsvUtil.getReader();
        //从文件中读取CSV数据
        reader.setContainsHeader(true);
        CsvData data = reader.read(FileUtil.file(filePath));
        List<CsvRow> rows = data.getRows();
        //遍历行
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            Console.log(csvRow.getRawList());
        }

        String r = JSON.toJSONString(rows);
        Console.log(r);
    }
}
