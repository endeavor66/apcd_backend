package com.nju.apcd.service.impl;

import com.nju.apcd.constant.Constants;
import com.nju.apcd.mapper.UploadRecordMapper;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.UploadRecord;
import com.nju.apcd.service.DataProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataProcessServiceImpl implements DataProcessService {

    @Autowired
    UploadRecordMapper uploadRecordMapper;

    @Override
    public ServerResponse uploadEventLog(List<MultipartFile> fileList, String project) {
        if (fileList == null || fileList.size() == 0) {
            return ServerResponse.fail("文件列表为空");
        }
        List<String> fileNameList = new ArrayList<>();
        for (MultipartFile file : fileList) {
            String originalFileName = file.getOriginalFilename();
            fileNameList.add(originalFileName);
            try {
                uploadFile(file.getBytes(), originalFileName);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(originalFileName + "上传失败!");
                return ServerResponse.fail("上传失败,"+e.toString());
            }
        }

        // 添加一条上传记录
        UploadRecord record = new UploadRecord();
        record.setFileNumber(fileList.size());
        record.setFileName(String.join(",", fileNameList));
        // TODO 登录功能完成后，这里需要更换为登陆人
        record.setOperator("admin");
        record.setOperateTime(new Date());
        uploadRecordMapper.insert(record);

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
