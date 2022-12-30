package com.nju.apcd.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataProcessService {
    String uploadEventLog(List<MultipartFile> fileList, String project);

}
