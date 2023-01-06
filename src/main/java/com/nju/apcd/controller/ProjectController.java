package com.nju.apcd.controller;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.nju.apcd.constant.Constants;
import com.nju.apcd.pojo.ServerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dev-api")
public class ProjectController {

    /**
     * 获取处理完成的项目列表
     * @return
     */
    @GetMapping("/get-project-list")
    public String getProjectList(){
        File file = FileUtil.file(Constants.DATA_DIR + "/log_all_scene");
        List<String> projectList = new ArrayList<>();
        for(String f: file.list()){
            projectList.add(f.split("\\.")[0]);
        }
        ServerResponse result = ServerResponse.ok(projectList);
        return JSON.toJSONString(result);
    }
}
