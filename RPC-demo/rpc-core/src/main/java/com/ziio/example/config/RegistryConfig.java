package com.ziio.example.config;

import lombok.Data;

@Data
public class RegistryConfig {

    /**
     * 注册中心类别
     */
    private String registry = "etcd";

    /**
     * 注册中心地址 --- 默认为 2379(etcd) 可更 （2181）--- zookeeper
     */
    private String address = "http://localhost:2379";

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间 --- 默认 30s
     */
    private Long timeout = 30000L;
}
