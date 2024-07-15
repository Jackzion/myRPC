package com.ziio.example.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.esotericsoftware.minlog.Log;
import com.ziio.example.RpcApplication;
import com.ziio.example.config.RegistryConfig;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.constant.RpcConstant;
import com.ziio.example.loadbalancer.LoadBalancer;
import com.ziio.example.loadbalancer.LoadBalancerFactory;
import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import com.ziio.example.serializer.JdkSerializer;
import com.ziio.example.serializer.Serializer;
import com.ziio.example.serializer.SerializerFactory;
import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态代理 , InvocationHandler实现
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {

    // proxy 动态代理对象每执行每个方法都会经过 invoke , 即userService 每个接口都会得到增强实现
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 选择序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        //发请求
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
            if(CollUtil.isEmpty(serviceMetaInfos)){
                throw new RuntimeException("暂无服务地址");
            }
            //  负载均衡
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
            // 调用方法名作为负载均衡hashCode
            Map<String,Object> requestParams = new HashMap<>();
            requestParams.put("methodNam",rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfos);

            // 发送 http' 请求
            try(HttpResponse httpResponse = HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute())
            {
                result = httpResponse.bodyBytes();
            }
            // 反序列化 result
            RpcResponse rpcResponse = serializer.deserialize(result,RpcResponse.class);
            return  rpcResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
