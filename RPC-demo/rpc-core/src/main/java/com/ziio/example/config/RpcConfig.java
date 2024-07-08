package com.ziio.example.config;

import com.ziio.example.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC 框架配置项
 */
@Data
public class RpcConfig {
    /**
     * 名称
     */
    private String name = "ziio-rpc";
    /**
     * 版本号
     */
    private String version = "1.0";
    /**
     * 主机名
     */
    private String serverHost = "localhost";
    /**
     * 服务端口号
     */
    private Integer serverPort = 8080;
    /**
     * 模拟调用
     */
    private boolean mock =false;
    /**
     * 指定序列化器 --- JDK.DEFAULT
     */
    private String serializer = SerializerKeys.JDK;

    /**
     * 指定注册中心
     */
    private RegistryConfig registryConfig = new RegistryConfig();
}
