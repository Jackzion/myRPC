package com.ziio.example.provider;

import com.ziio.example.RpcApplication;
import com.ziio.example.common.model.User;
import com.ziio.example.common.service.UserService;
import com.ziio.example.consumer.exRpc.LocalRegistry;
import com.ziio.example.consumer.server.HttpServer;
import com.ziio.example.consumer.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        // webservice 动态监听端口 , 这里使用默认
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
