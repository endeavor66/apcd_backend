package com.nju.apcd.controller;

import com.nju.apcd.service.ProcessMiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
public class ProcessMiningController {

    @Autowired
    ProcessMiningService processMiningService;

    @GetMapping("/hello")
    public String test(){
        return "hello";
    }

    /**
     * 构建主流模式
     * @param repos 项目集合
     * @param scene 主流模式场景
     * @return 构建结果
     */
    @PostMapping("/process-discovery")
    public String processDiscovery(@RequestParam List<String> repos, @RequestParam Integer scene){
        try {
            return processMiningService.processDiscovery(repos, scene);
        }catch (Exception e){
            System.err.println(e);
            return e.toString();
        }
    }
}
