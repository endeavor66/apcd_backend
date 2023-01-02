package com.nju.apcd.service;

import com.nju.apcd.pojo.ServerResponse;

import java.io.IOException;
import java.util.List;

public interface ProcessMiningService {

    public ServerResponse processDiscovery(List<String> projectList, String scene, String algorithm, String param);
}
