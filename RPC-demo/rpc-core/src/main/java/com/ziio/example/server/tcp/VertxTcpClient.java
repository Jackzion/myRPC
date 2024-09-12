package com.ziio.example.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.ziio.example.RpcApplication;
import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.protocol.*;
import com.ziio.example.server.tcp.TcpBufferHandlerWrapper;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * tcpClient --- 对发送请求，返回响应 进行封装
 */
public class VertxTcpClient {

    public static RpcResponse doRequest(RpcRequest rpcRequest , ServiceMetaInfo selectedServiceMetaInfo) throws ExecutionException, InterruptedException {
        // 创建 vertx 客户端
        // 发送 Tcp 请求
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
        netClient.connect(selectedServiceMetaInfo.getServicePort(), selectedServiceMetaInfo.getServiceHost(),
                res -> {
                    if (res.succeeded()) {
                        System.out.println("Connnected to Tcp server");
                        NetSocket socket = res.result();
                        // 发送数据
                        // 构造 protocolMessage (tcp包)
                        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                        // 构造请求头
                        com.ziio.example.protocol.ProtocolMessage.Header header = new ProtocolMessage.Header();
                        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                        header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getValue());
                        // 生成全局请求 ID
                        header.setRequestId(IdUtil.getSnowflakeNextId());
                        protocolMessage.setHeader(header);
                        protocolMessage.setBody(rpcRequest);

                        // 编码请求
                        try {
                            Buffer encodeBuffer = ProtocolMessageEncoder.encode(protocolMessage);
                            socket.write(encodeBuffer);
                        } catch (Exception e) {
                            throw new RuntimeException("协议消息编码错误");
                        }

                        // 接收响应 , 用 TcpBufferHandlerWrapper ，解决包处理问题 ，装饰类
                        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                            try {
                                ProtocolMessage<RpcResponse> resProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                // 异步处理结果 --- 完成响应
                                responseFuture.complete(resProtocolMessage.getBody());
                            } catch (IOException e) {
                                throw new RuntimeException("协议消息解码错误");
                            }
                        });
                        socket.handler(bufferHandlerWrapper);
                    } else {
                        System.out.println("failed to connect to tcp server");
                    }
                });
        // 等待响应完成 ，阻塞，而不是往下返回结果
        RpcResponse response = responseFuture.get();
        // 记得关闭连接
        netClient.close();
        return response;
    }

}
