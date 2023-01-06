package com.nju.apcd.controller;

import com.alibaba.fastjson.JSON;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.param.AnomalyDetectQueryParam;
import com.nju.apcd.pojo.param.AnomalyResultQueryParam;
import com.nju.apcd.service.AnomalyDetectService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dev-api")
public class AnomalyDetectController {

    @Resource
    AnomalyDetectService anomalyDetectService;

    @PostMapping("/anomaly-detect")
    public String AnomalyDetect(AnomalyDetectQueryParam param){
        ServerResponse result = anomalyDetectService.anomalyDetect(param);
        return JSON.toJSONString(result);
    }

    @PostMapping("/get-anomaly-result")
    public String getAnomalyResult(AnomalyResultQueryParam param){
        ServerResponse result = anomalyDetectService.getAnomalyResult(param);
        return JSON.toJSONString(result);
    }
}
