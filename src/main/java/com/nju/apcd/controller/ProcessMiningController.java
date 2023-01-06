package com.nju.apcd.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.nju.apcd.constant.Constants;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.param.ProcessModelQueryParam;
import com.nju.apcd.service.ProcessMiningService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dev-api")
public class ProcessMiningController {

    @Resource
    ProcessMiningService processMiningService;

    /**
     * 构建主流模式
     * @param param 查询参数
     * @return 执行结果
     */
    @PostMapping("/build-process-model")
    public String processDiscovery(ProcessModelQueryParam param){
        ServerResponse result = processMiningService.processDiscovery(param);
        return JSON.toJSONString(result);
    }

    /**
     * 获取主流模式
     * @param sceneList 场景，如：fork_merge, unfork_close
     * @param display 呈现形式，如：petriNet, processTree
     * @return
     */
    @PostMapping("/get-process-model")
    public String getProcessModel(@RequestParam("sceneList") String sceneList,
                                  @RequestParam("display") String display){
        // 如果sceneList为空，等价于查询所有场景
        String[] sceneArr = Constants.SCENE_LIST;
        if(!StrUtil.isBlank(sceneList)){
            sceneArr = sceneList.split(",");
        }
        ServerResponse result = processMiningService.getProcessModel(sceneArr, display);
        return JSON.toJSONString(result);
    }

    /**
     * 执行一致性检验
     * @param project
     * @param algorithm
     * @return
     */
    @PostMapping("/conformance-check")
    public String conformanceCheck(@RequestParam("project") String project,
                                   @RequestParam("algorithm") String algorithm){
        ServerResponse result = processMiningService.conformanceCheck(project, algorithm);
        return JSON.toJSONString(result);
    }

    @PostMapping("/get-conformance-check-result")
    public String getConformanceCheckResult(@RequestParam("project") String project,
                                            @RequestParam("algorithm") String algorithm){
        ServerResponse result = processMiningService.getConformanceCheckResult(project, algorithm);
        return JSON.toJSONString(result);
    }
}
