package com.ziio.example.server;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import jdk.jfr.internal.tool.Main;

public class VertxTcpServer implements HttpServer {
    private byte[] handleRequest(byte[] requestData){
        // todo:写处理请求的逻辑
        return "Hello,client".getBytes();
    }

    @Override
    public void doStart(int port) {
        // 创建 vertx 实例
        Vertx vertx = Vertx.vertx() ;

        // 创建 tcp 服务器
        NetServer server = vertx.createNetServer() ;

        // server 处理请求
        server.connectHandler(socket ->{
            // 处理请求
            socket.handler(buffer -> {
                // 处理接收到的字节数组
                byte[] requestData = buffer.getBytes();
                // todo:进行自定义字节数组处理逻辑 ： 解析 ，调用服务 ， 构造响应 。。。
                byte[] responseData = handleRequest(requestData);
                // 发送响应
                socket.write(Buffer.buffer(responseData));
            });
        });

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
