package com.nju.apcd.service;

import java.io.IOException;
import java.util.List;

public interface ProcessMiningService {

    public String processDiscovery(List<String> repos, Integer scene) throws IOException;
}
