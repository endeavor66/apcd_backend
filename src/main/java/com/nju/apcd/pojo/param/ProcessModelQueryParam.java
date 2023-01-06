package com.nju.apcd.pojo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessModelQueryParam {
    public String projectList;
    public String sceneList;
    public String algorithm;
    public String param;
}
