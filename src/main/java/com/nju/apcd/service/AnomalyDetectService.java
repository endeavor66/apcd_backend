package com.nju.apcd.service;

import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.param.AnomalyDetectQueryParam;
import com.nju.apcd.pojo.param.AnomalyResultQueryParam;

public interface AnomalyDetectService {
    public ServerResponse anomalyDetect(AnomalyDetectQueryParam param);

    ServerResponse getAnomalyResult(AnomalyResultQueryParam param);
}
