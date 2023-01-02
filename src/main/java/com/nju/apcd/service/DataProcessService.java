package com.nju.apcd.service;

import com.nju.apcd.pojo.param.EventLogQueryParam;
import com.nju.apcd.pojo.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataProcessService {
    ServerResponse uploadEventLog(List<MultipartFile> fileList, String project);

    ServerResponse getUploadRecord();

    ServerResponse getEventLog(EventLogQueryParam param);
}
