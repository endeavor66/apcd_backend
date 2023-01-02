package com.nju.apcd.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nju.apcd.constant.Constants;
import com.nju.apcd.mapper.EventLogMapper;
import com.nju.apcd.mapper.UploadRecordMapper;
import com.nju.apcd.pojo.param.EventLog;
import com.nju.apcd.pojo.param.EventLogQueryParam;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.UploadRecord;
import com.nju.apcd.service.DataProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.util.*;

@Service
public class DataProcessServiceImpl implements DataProcessService {

    @Autowired
    UploadRecordMapper uploadRecordMapper;

    @Autowired
    EventLogMapper eventLogMapper;

    /**
     * 上传文件，保存到服务器
     * @param fileList
     * @param project
     * @return
     */
    @Override
    public ServerResponse uploadEventLog(List<MultipartFile> fileList, String project) {
        if (fileList == null || fileList.size() == 0) {
            return ServerResponse.fail("文件列表为空");
        }

        // 依次保存每份文件
        List<String> successFileList = new ArrayList<>(); // 记录上传成功的文件名
        List<String> errorFileList = new ArrayList<>(); // 记录上传失败的文件名
        for (MultipartFile file : fileList) {
            String originalFileName = file.getOriginalFilename();
            try {
                uploadFile(file.getBytes(), originalFileName);
                successFileList.add(originalFileName);
            } catch (Exception e) {
                e.printStackTrace();
                errorFileList.add(originalFileName);
            }
        }

        // 添加一条上传记录
        UploadRecord record = new UploadRecord();
        record.setSuccessFileNumber(successFileList.size());
        record.setSuccessFileName(String.join(",", successFileList));
        record.setErrorFileNumber(errorFileList.size());
        record.setErrorFileName(String.join(",", errorFileList));
        // TODO 登录功能完成后，这里需要更换为登陆人
        record.setOperator("admin");
        record.setOperateTime(new Date());
        uploadRecordMapper.insert(record);

        // 返回结果
        String message = "成功上传" + (fileList.size() - errorFileList.size()) + "份文件";
        if(errorFileList.size() > 0){
            message += ", 上传失败" + errorFileList.size() + "份文件, ";
            message += String.join(",", errorFileList);
        }

        return ServerResponse.ok(message);
    }

    /**
     * 获取所有上传记录
     * @return
     */
    @Override
    public ServerResponse getUploadRecord() {
        List<UploadRecord> uploadRecords = uploadRecordMapper.selectList(null);
        return ServerResponse.ok(uploadRecords);
    }

    @Override
    public ServerResponse getEventLog(EventLogQueryParam param) {
        // TODO 根据传入的参数，分页查询(注：参数值允许为NULL，表明不添加到查询条件集合)
        QueryWrapper<EventLog> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotBlank(param.getProject())){
            queryWrapper.like("repo",param.getProject());
        }
        if(StrUtil.isNotBlank(param.getScene())){
            queryWrapper.like("Scene",param.getScene());
        }
        if(StrUtil.isNotBlank(param.getPrNumber())){
            queryWrapper.eq("PrNumber",param.getPrNumber());
        }
        Page<EventLog> page=new Page<EventLog>(Long.parseLong(param.getCurrentPage()),Long.parseLong(param.getPageSize()));
        eventLogMapper.selectPage(page, queryWrapper);
        return ServerResponse.ok(page);
    }

    /**
     * 文件保存到本地
     * @param file
     * @param fileName
     * @throws Exception
     */
    public void uploadFile(byte[] file, String fileName) throws Exception {
        String filePath = Constants.BIGQUERY_DATA_DIR + "/" + fileName;
        FileOutputStream out = new FileOutputStream(filePath);
        out.write(file);
        out.flush();
        out.close();
    }


}
