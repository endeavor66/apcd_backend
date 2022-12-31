package com.nju.apcd.pojo;

import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ServerResponse {
    private Integer code;
    private String message;
    private Object data;

    // 构造方法设为私有
    private ServerResponse() {}

    public static ServerResponse ok(Object params) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setCode(ResultEnum.OK.getCode());
        serverResponse.setMessage(ResultEnum.OK.getMessage()); // 成功展示默认提示信息
        serverResponse.setData(params); // 返回传入的参数
        return serverResponse;
    }

    public static ServerResponse badRequest(@Nullable String message) {
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setCode(ResultEnum.BAD_REQUEST.getCode());
        serverResponse.setMessage(message != null ? message : ResultEnum.BAD_REQUEST.getMessage()); // 校验失败传入指定的提示信息
        serverResponse.setData(null); // 校验失败不返回参数
        return serverResponse;
    }

    public static ServerResponse fail(@Nullable String message){
        ServerResponse serverResponse = new ServerResponse();
        serverResponse.setCode(ResultEnum.FAIL.getCode());
        serverResponse.setMessage(message != null ? message : ResultEnum.FAIL.getMessage()); // 校验失败传入指定的提示信息
        serverResponse.setData(null); // 校验失败不返回参数
        return serverResponse;
    }
}

