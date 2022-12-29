package com.nju.apcd.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "文件上传失败")
public class FileUploadFailException extends RuntimeException{
    String message;

    public FileUploadFailException(String message) {
        super(message);
        this.message = message;
    }
}
