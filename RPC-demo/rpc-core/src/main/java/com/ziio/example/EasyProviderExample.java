package com.ziio.example;

import com.ziio.example.server.HttpServer;
import com.ziio.example.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8881);
    }
}
