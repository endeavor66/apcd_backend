package com.nju.apcd.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import com.alibaba.fastjson.JSON;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.service.DataProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
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
        return dataProcessService.uploadEventLog(fileList, project);
    }


    /**
     * 获取处理后的事件日志
     * @param project
     * @param scene
     * @return
     */
    @PostMapping("/get-event-log")
    public String getEventLog(@RequestParam("project") String project, @RequestParam("scene") String scene) {
        String filePath = "event_log/" + project + "_" + scene + ".csv";

        if(!FileUtil.exist(filePath)){
            return JSON.toJSONString(ServerResponse.fail("没有找到该项目的日志文件"));
        }

        CsvReader reader = CsvUtil.getReader();
        //从文件中读取CSV数据
        reader.setContainsHeader(true);
        CsvData data = reader.read(FileUtil.file(filePath));
        List<CsvRow> rows = data.getRows();
        //遍历行
        List<List<String>> event_log = new ArrayList<>();
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            event_log.add(csvRow.getRawList());
        }

        return JSON.toJSONString(ServerResponse.ok(event_log));
    }
}
