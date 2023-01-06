package com.nju.apcd.pojo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyDetectQueryParam {
    public String project;
    public String scenes;
    public String algorithms;
    public Boolean featureFlag;
    public String dateRange;
}
