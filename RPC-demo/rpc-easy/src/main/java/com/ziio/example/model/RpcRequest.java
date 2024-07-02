package com.ziio.example.model;

import lombok.Data;

/**
 * request 请求封装类
 */
@Data
public class RpcRequest {
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数列表
     */
    private Object[] args;
}
