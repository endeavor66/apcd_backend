package com.nju.apcd.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("process_model")
public class ProcessModel {
    private Long id;
    private String scene;
    private Integer logCase;
    private String algorithm;
    private String param;
    private Float averageTraceFitness;
    private Float percentageOfFittingTraces;
    @TableField(value = "`precision`")
    private Float precision;
    private Float generalization;
    private Float simplicity;
    private String petriNet;
    @TableField(exist = false)
    private String imgData;
}
