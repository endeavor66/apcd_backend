package com.nju.apcd.service.impl;

import com.nju.apcd.constant.Constants;
import com.nju.apcd.mapper.EventLogMapper;
import com.nju.apcd.mapper.PermissionChangeMapper;
import com.nju.apcd.mapper.UploadRecordMapper;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.UploadRecord;
import com.nju.apcd.pojo.param.EventLogQueryParam;
import com.nju.apcd.pojo.param.PermissionQueryParam;
import com.nju.apcd.service.DataProcessService;
import com.nju.apcd.utils.ScriptUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DataProcessServiceImpl implements DataProcessService {

    @Resource
    UploadRecordMapper uploadRecordMapper;

    @Resource
    EventLogMapper eventLogMapper;

    @Resource
    PermissionChangeMapper permissionChangeMapper;

    /**
     * 上传文件，保存到服务器
     * @param fileList 文件列表
     * @return 传输结果消息
     */
    @Override
    public ServerResponse uploadEventLog(List<MultipartFile> fileList) {
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
     * @return 上传记录
     */
    @Override
    public ServerResponse getUploadRecord() {
        List<UploadRecord> uploadRecords = uploadRecordMapper.selectList(null);
        return ServerResponse.ok(uploadRecords);
    }

    @Override
    public ServerResponse getEventLog(EventLogQueryParam param) {
        // TODO 根据传入的参数，分页查询(注：参数值允许为NULL，表明不添加到查询条件集合)
        //Map<String, Object> map = new HashMap<>();
        //if(StrUtil.isNotBlank(param.getProject())){
        //    map.put("repo", param.getProject());
        //}
        //if(StrUtil.isNotBlank(param.getPrNumber())){
        //    map.put("repo", param.getProject());
        //}
        //if(StrUtil.isNotBlank(param.getScene())){
        //    map.put("repo", param.getProject());
        //}
        return null;
    }

    @Override
    public ServerResponse getPermission(PermissionQueryParam param) {
        return null;
    }

    @Override
    public ServerResponse dataPreprocess(List<String> projectList, String start, String end) {
        // 1.从bigquery中提取指定项目的信息，存到bigquery_data目录
        ScriptUtil.data_extract_from_bigquery(projectList);

        // 2.将bigquery_data数据存到数据库repo_event
        ScriptUtil.bigquery_process(projectList, start, end);

        // 3.加工event_log，保存到process_event
        ScriptUtil.event_process(projectList);

        // 4.计算权限变更信息，保存到permission_change
        ScriptUtil.permission_change(projectList);

        // 5.计算各角色的特征信息，保存到数据库
        ScriptUtil.committer_feature_extract(projectList, start, end);
        ScriptUtil.reviewer_feature_extract(projectList, start, end);
        ScriptUtil.maintainer_feature_extract(projectList, start, end);

        return ServerResponse.ok("数据预处理完成");
    }

    /**
     * 文件保存到本地
     * @param file 文件二进制流
     * @param fileName 文件名
     * @throws Exception IO异常
     */
    public void uploadFile(byte[] file, String fileName) throws Exception {
        String filePath = Constants.BIGQUERY_DATA_DIR + "/" + fileName;
        FileOutputStream out = new FileOutputStream(filePath);
        out.write(file);
        out.flush();
        out.close();
    }
}
