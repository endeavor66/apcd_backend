package com.nju.apcd.pojo;

import cn.hutool.core.text.csv.CsvRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConformanceResult {
    String project;
    String algorithm;
    String[] header;
    List<List<String>> rows;
}
