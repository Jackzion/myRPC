package com.ziio.example.consumer;

import com.ziio.example.consumer.server.HttpServer;
import com.ziio.example.consumer.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8881);
    }
}
