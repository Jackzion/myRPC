package com.ziio.example.server.tcp;

import com.ziio.example.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;

public class VertxTcpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        // 创建 vertx 实例
        Vertx vertx = Vertx.vertx() ;

        // 创建 tcp 服务器
        NetServer server = vertx.createNetServer() ;

        // server 处理请求 , TODO: 改为装饰过后的 handler
        server.connectHandler(new TcpServerHandler());

        // 开启 tcp 服务器, 监听端口
        server.listen(port,result -> {
            if(result.succeeded()){
                System.out.println("Server is now listening on port" + port);
            }else{
                System.out.println("Failed to start server" + result.cause());
            }
        });
    }

    // 测试 Tcp 服务器
    public static void main(String[] args) {
        new VertxTcpServer().doStart(8888);
    }
}
