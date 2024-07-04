package com.ziio.example.consumer.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        // 创建 vertx 实例
        Vertx vertx = Vertx.vertx() ;

        // 创建 http 服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer() ;

        // server 处理请求
        server.requestHandler(new HttpServerHandler());

        // 开启server 监听端口
        server.listen(port,result -> {
            if(result.succeeded()){
                System.out.println("Server is now listening on port" + port);
            }else{
                System.out.println("Failed to start server" + result.cause());
            }
        });

    }
}
