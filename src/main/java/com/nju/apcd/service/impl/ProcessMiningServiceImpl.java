package com.nju.apcd.service.impl;

import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.service.ProcessMiningService;
import com.nju.apcd.utils.ScriptUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessMiningServiceImpl implements ProcessMiningService {

    @Override
    public ServerResponse processDiscovery(List<String> projectList, String scene, String algorithm, String param) {
        // 1.解析参数param,校验是否符合要求
        ScriptUtil.process_discovery(projectList, scene, algorithm, param);
        return null;
    }
}
