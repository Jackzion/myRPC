package com.ziio.example.server.tcp;

import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.protocol.*;
import com.ziio.example.registry.LocalRegistry;
import com.ziio.example.server.tcp.TcpBufferHandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 请求处理器 （服务提供者）
 * 用于处理新的 TCP 连接，它的 handle 方法在每次连接建立时调用。
 */
@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket socket) {
        log.info("New connection from: {}", socket.remoteAddress());
        // 处理连接
        // 利用 TcpBufferHandlerWrapper 进行封装 ，解决粘包半包问题
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper (buffer -> {
            // 直接截取 buffer 即可 ，因为已经用 parser 处理器截断过
            // 接受请求 ， 解码
            ProtocolMessage<RpcRequest> protocolMessage ;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            RpcRequest request = protocolMessage.getBody();

            // 处理请求 ， 和 httpHandler 逻辑一样
            // 构造响应对象
            RpcResponse rpcResponse = new RpcResponse();
            try {
                // 调用本地注册表 , 获得类信息
                Class<?> implClass = LocalRegistry.get(request.getServiceName());
                //  获取方法
                Method method = implClass.getMethod(request.getMethodName(),request.getParameterTypes());
                // 调用方法
                Object result = method.invoke(implClass.newInstance(),request.getArgs());
                // 封装 rpcResponse
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");
                rpcResponse.setData(result);
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());
            }

            // 发送响应 ， 编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            // todo : 设置状态码！
            // 设置 RESPONSE
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getValue());
            header.setStatus((byte) ProtocolMessageStatusEnum.OK.getValue());
            ProtocolMessage<RpcResponse> responseProtocolMessage = new ProtocolMessage<>(header,rpcResponse);
            try {
                // 编码
                Buffer encode = ProtocolMessageEncoder.encode(responseProtocolMessage);
                // 发送响应
                socket.write(encode);
            } catch (IOException e) {
                throw new RuntimeException("协议消息编码错误");
            }
        });
        socket.handler(bufferHandlerWrapper);
    }
}
