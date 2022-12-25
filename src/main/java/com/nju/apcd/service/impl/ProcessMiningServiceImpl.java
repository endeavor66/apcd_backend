package com.nju.apcd.service.impl;

import com.nju.apcd.service.ProcessMiningService;
import com.nju.apcd.utils.ScriptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ProcessMiningServiceImpl implements ProcessMiningService {

    @Autowired
    ResourceLoader resourceLoader;

    @Override
    public String processDiscovery(List<String> repos, Integer scene) throws IOException {
        // 获取类路径(resources目录)中脚本的绝对路径
        Resource resource = resourceLoader.getResource("classpath:scripts/Hello.py");
        String path = resource.getFile().getPath();
        System.out.println(path);
        // 执行python脚本
        String[] commands = new String[]{"python", path, "param1", "param2"};
        ScriptUtil.execute_command(commands);
        return "hello";
    }
}
