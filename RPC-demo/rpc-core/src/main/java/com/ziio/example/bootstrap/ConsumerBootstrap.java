package com.ziio.example.bootstrap;

import com.ziio.example.RpcApplication;

/**
 * 服务消费者启动类
 */
public class ConsumerBootstrap {
    /**
     * 初始化
     */
    public static void init() {
        // RPC 框架初始化（配置和注册中心）
        RpcApplication.init();
    }
}
