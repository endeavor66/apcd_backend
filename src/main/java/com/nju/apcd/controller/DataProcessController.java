package com.nju.apcd.controller;

import com.alibaba.fastjson.JSON;
import com.nju.apcd.pojo.param.EventLogQueryParam;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.service.DataProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/dev-api")
public class DataProcessController {

    @Autowired
    DataProcessService dataProcessService;

    /**
     * 上传日志文件
     * @param fileList
     * @param project
     * @return
     */
    @PostMapping("/upload-event-log")
    public String uploadEventLog(@RequestParam("fileList") List<MultipartFile> fileList,
                     @RequestParam("project") String project) {
        ServerResponse result = dataProcessService.uploadEventLog(fileList, project);
        return JSON.toJSONString(result);
    }

    @GetMapping("/get-upload-record")
    public String getUploadRecord(){
        ServerResponse result = dataProcessService.getUploadRecord();
        return JSON.toJSONString(result);
    }


    /**
     * 获取处理后的事件日志
     * @param param
     * @return
     */
    @PostMapping("/get-event-log")
    public String getEventLog(EventLogQueryParam param) {
        ServerResponse result = dataProcessService.getEventLog(param);
        return JSON.toJSONString(ServerResponse.ok(result));
    }
}
