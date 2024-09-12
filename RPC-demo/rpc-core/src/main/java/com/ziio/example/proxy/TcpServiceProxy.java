package com.ziio.example.proxy;

import cn.hutool.core.collection.CollUtil;
import com.ziio.example.RpcApplication;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.constant.RpcConstant;
import com.ziio.example.fault.retry.RetryStrategy;
import com.ziio.example.fault.retry.RetryStrategyFactory;
import com.ziio.example.fault.tolerant.TolerantStrategy;
import com.ziio.example.fault.tolerant.TolerantStrategyFactory;
import com.ziio.example.loadbalancer.LoadBalancer;
import com.ziio.example.loadbalancer.LoadBalancerFactory;
import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import com.ziio.example.server.tcp.VertxTcpClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态代理 , InvocationHandler实现
 */
@Slf4j
public class TcpServiceProxy implements InvocationHandler {

    // proxy 动态代理对象每执行每个方法都会经过 invoke , 即userService 每个接口都会得到增强实现
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
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
        // registry 作为 SPI 对象，在 new rpcApplication ，已经被注册到 map ，并且加载到 cache 中（单例）
        Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegistry());
        // 构建 ServiceMetaInfo
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(rpcRequest.getServiceName());
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        // 注册中心根据 getServiceKey 搜索服务
        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        if (CollUtil.isEmpty(serviceMetaInfos)) {
            throw new RuntimeException("暂无服务地址");
        }
        // 负载均衡
        LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(rpcConfig.getLoadBalancer());
        // 调用方法名作为负载均衡 hashCode
        Map<String,Object> requestParams = new HashMap<>();
        requestParams.put("methodNam",rpcRequest.getMethodName());
        ServiceMetaInfo selectedServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfos);


        // 发送请求，得到响应
        RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(rpcConfig.getRetryStrategy());
        RpcResponse rpcResponse = null;
        try {
            // 使用重试机制 , 对传入 callable 任务进行重试
            rpcResponse = retryStrategy.doRetry(()->
                VertxTcpClient.doRequest(rpcRequest,selectedServiceMetaInfo)
            );
        } catch (Exception e) {
            // 触发容错机制
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(rpcConfig.getTolerantStrategy());
            tolerantStrategy.doTolerant(null,e);
        }

        // 返回调用结果
        return rpcResponse.getData();
    }
}
