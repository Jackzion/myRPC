package com.ziio.example.model;

import lombok.Data;

/**
 * RPC 响应封装类
 */
@Data
public class RpcResponse {
    /**
     * 响应数据
     */
    private Object data;
    /**
     * 响应数据类型
     */
    private Class<?> dataType;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 异常信息
     */
    private Exception exception;
}
