package com.nju.apcd.controller;

import com.nju.apcd.exception.FileUploadFailException;
import com.nju.apcd.service.ProcessMiningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
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
            System.out.println(repos);
            System.out.println(scene);
            return processMiningService.processDiscovery(repos, scene);
        }catch (Exception e){
            System.err.println(e);
            return e.toString();
        }
    }

    //处理文件上传
    @PostMapping("/uploading")
    public @ResponseBody String uploading(@RequestParam("fileList") List<MultipartFile> fileList,
                                          @RequestParam("project") String project,
                                          HttpServletRequest request) {
        System.out.println(project);
        if (fileList == null || fileList.size() == 0) {
            return "上传文件不能为空";
        }
        for (MultipartFile file : fileList) {
            String originalFileName = file.getOriginalFilename();
            try {
                uploadFile(file.getBytes(), originalFileName);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(originalFileName + "上传失败!");
                return "uploading fail";
            }
            System.out.println(originalFileName + "上传成功!");
        }
        return "uploading success";
    }

    public void  uploadFile(byte[] file, String fileName) throws Exception {
        String file_dir = "E:/test/";
        File targetFile = new File(file_dir);
        if(!targetFile.exists()){
            targetFile.mkdirs();
        }
        String filePath = file_dir + fileName;
        FileOutputStream out = new FileOutputStream(filePath);
        out.write(file);
        out.flush();
        out.close();
    }
}
