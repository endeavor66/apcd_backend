package com.nju.apcd.service.impl;

import cn.hutool.core.io.FileUtil;
import com.nju.apcd.constant.Constants;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.service.DataProcessService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class DataProcessServiceImpl implements DataProcessService {
    @Override
    public ServerResponse uploadEventLog(List<MultipartFile> fileList, String project) {
        if (fileList == null || fileList.size() == 0) {
            return ServerResponse.fail("文件列表为空");
        }
        for (MultipartFile file : fileList) {
            String originalFileName = file.getOriginalFilename();
            try {
                uploadFile(file.getBytes(), originalFileName);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(originalFileName + "上传失败!");
                return ServerResponse.fail("上传失败,"+e.toString());
            }
        }

        return ServerResponse.ok("上传成功");
    }

    public void uploadFile(byte[] file, String fileName) throws Exception {
        String filePath = Constants.BIGQUERY_DATA_DIR + "/" + fileName;
        FileOutputStream out = new FileOutputStream(filePath);
        out.write(file);
        out.flush();
        out.close();
    }
}
