package com.ziio.example.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.ziio.example.RpcApplication;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.constant.RpcConstant;
import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.protocol.*;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import com.ziio.example.serializer.Serializer;
import com.ziio.example.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 动态代理 , InvocationHandler实现
 */
@Slf4j
public class TcpServiceProxy implements InvocationHandler {

    // proxy 动态代理对象每执行每个方法都会经过 invoke , 即userService 每个接口都会得到增强实现
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 选择序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //构造请求
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setArgs(args);

        try {
            // 請求序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            // 从注册中心获取服务提供请求地址
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
            registry.init(rpcConfig.getRegistryConfig());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(rpcRequest.getServiceName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            // 注册中心根据 serviceName and version 搜索服务
            List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfos)) {
                throw new RuntimeException("暂无服务地址");
            }
            // todo: 取了第一个 ， 可负载均衡
            ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfos.get(0);

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
                            // 构造消息
                            ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
                            // 构造请求头
                            com.ziio.example.protocol.ProtocolMessage.Header header = new ProtocolMessage.Header();
                            header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
                            header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
                            header.setSerializer((byte) ProtocolMessageSerializerEnum.getEnumByValue(RpcApplication.getRpcConfig().getSerializer()).getKey());
                            header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getValue());
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

                            // 接收响应
                            socket.handler(buffer -> {
                                try {
                                    ProtocolMessage<RpcResponse> resProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                                    // 异步处理结果 --- 完成响应
                                    responseFuture.complete(resProtocolMessage.getBody());
                                } catch (IOException e) {
                                    throw new RuntimeException("协议消息解码错误");
                                }
                            });
                        } else {
                            System.out.println("failed to connect to tcp server");
                        }
                    });
            // 等待响应完成 ，阻塞，而不是往下返回结果
            RpcResponse response = responseFuture.get();
            // 记得关闭连接
            netClient.close();
            // 返回调用结果
            return response.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
