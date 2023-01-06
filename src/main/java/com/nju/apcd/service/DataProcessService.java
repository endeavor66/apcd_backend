package com.nju.apcd.service;

import com.nju.apcd.pojo.param.EventLogQueryParam;
import com.nju.apcd.pojo.ServerResponse;
import com.nju.apcd.pojo.param.PermissionQueryParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataProcessService {
    ServerResponse uploadEventLog(List<MultipartFile> fileList);

    ServerResponse getUploadRecord();

    ServerResponse getEventLog(EventLogQueryParam param);

    ServerResponse dataPreprocess(String projectList, String start, String end);

    ServerResponse getPermissionChange(PermissionQueryParam param);
}
