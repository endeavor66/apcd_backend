package com.nju.apcd.pojo;

import lombok.Getter;

@Getter
public enum ResultEnum {
    OK(200, "成功"),
    BAD_REQUEST(400, "参数错误"),
    NOT_FOUND(404, "找不到请求的资源"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    FAIL(999, "自定义错误");

    private Integer code;
    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
