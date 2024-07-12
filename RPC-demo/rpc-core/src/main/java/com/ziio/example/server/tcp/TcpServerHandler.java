package com.ziio.example.server.tcp;

import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.protocol.ProtocolMessage;
import com.ziio.example.protocol.ProtocolMessageDecoder;
import com.ziio.example.protocol.ProtocolMessageEncoder;
import com.ziio.example.protocol.ProtocolMessageTypeEnum;
import com.ziio.example.registry.LocalRegistry;
import com.ziio.example.server.tcp.TcpBufferHandlerWrapper;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 请求处理器 （服务提供者）
 */
public class TcpServerHandler implements Handler<NetSocket> {

    @Override
    public void handle(NetSocket socket) {
        // 处理连接
        // 利用 TcpBufferHandlerWrapper 进行封装 ，解决粘包半包问题
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper (buffer -> {
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
                // 获取调用服务实现类 ， 通过反射调用
                Class<?> implClass = LocalRegistry.get(request.getServiceName());
                Method method = implClass.getMethod(request.getMethodName(),request.getParameterTypes());
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

            // todo: 发送响应 ， 编码
            ProtocolMessage.Header header = protocolMessage.getHeader();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getValue());
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
    }
}
