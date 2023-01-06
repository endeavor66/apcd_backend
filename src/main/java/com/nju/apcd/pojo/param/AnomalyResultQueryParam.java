package com.nju.apcd.pojo.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyResultQueryParam {
    public String project;
    public String scene;
    public String resultType;
}
