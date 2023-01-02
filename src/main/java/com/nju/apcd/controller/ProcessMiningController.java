package com.nju.apcd.controller;

import com.alibaba.fastjson.JSON;
import com.nju.apcd.exception.FileUploadFailException;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.service.ProcessMiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class ProcessMiningController {

    @Resource
    ProcessMiningService processMiningService;

    /**
     * 构建主流模式
     * @param projectList 项目列表
     * @param scene 场景
     * @param algorithm 过程发现算法
     * @param param 算法参数
     * @return
     */
    @PostMapping("/process-discovery")
    public String processDiscovery(@RequestParam("projectList") List<String> projectList,
                                   @RequestParam("scene") String scene,
                                   @RequestParam("algorithm") String algorithm,
                                   @RequestParam("param") String param){
        ServerResponse result = processMiningService.processDiscovery(projectList, scene, algorithm, param);
        return JSON.toJSONString(result);
    }
}
