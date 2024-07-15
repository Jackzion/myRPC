package com.ziio.example.proxy;

import cn.hutool.core.collection.CollUtil;
import com.ziio.example.RpcApplication;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.constant.RpcConstant;
import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import com.ziio.example.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 动态代理 , InvocationHandler实现
 */
@Slf4j
public class TcpServiceProxy implements InvocationHandler {

    // proxy 动态代理对象每执行每个方法都会经过 invoke , 即userService 每个接口都会得到增强实现
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        // 选择序列化器 , 取消序列化器 ，改用 解码编码器 进行动态加载序列化器
//        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //构造请求
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setServiceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setArgs(args);

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
        // todo: 取了第一个 ， tcp 不适合负载均衡？
        ServiceMetaInfo selectedServiceMetaInfo = serviceMetaInfos.get(0);

        // 发送请求，得到响应
        RpcResponse rpcResponse = VertxTcpClient.doRequest(rpcRequest, selectedServiceMetaInfo);

        // 返回调用结果
        return rpcResponse.getData();
    }
}
