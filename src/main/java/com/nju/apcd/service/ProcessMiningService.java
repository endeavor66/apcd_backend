package com.nju.apcd.service;

import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.param.ProcessModelQueryParam;

import java.io.IOException;
import java.util.List;

public interface ProcessMiningService {

    ServerResponse processDiscovery(ProcessModelQueryParam param);

    ServerResponse getProcessModel(String[] sceneList, String display);

    ServerResponse conformanceCheck(String project, String algorithm);

    ServerResponse getConformanceCheckResult(String project, String algorithm);
}
