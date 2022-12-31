package com.nju.apcd.service.impl;

import com.nju.apcd.service.DataProcessService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class DataProcessServiceImpl implements DataProcessService {
    @Override
    public String uploadEventLog(List<MultipartFile> fileList, String project) {
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

    public void uploadFile(byte[] file, String fileName) throws Exception {
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
