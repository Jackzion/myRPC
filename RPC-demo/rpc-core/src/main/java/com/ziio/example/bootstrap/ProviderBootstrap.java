package com.ziio.example.bootstrap;

import com.ziio.example.RpcApplication;
import com.ziio.example.config.RegistryConfig;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.model.ServiceRegisterInfo;
import com.ziio.example.registry.LocalRegistry;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import com.ziio.example.server.tcp.VertxTcpServer;
import lombok.val;

import java.util.List;

/**
 * 服务提供者初始化
 */
public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo> serviceRegisterInfoList){
        // rpc 框架初始化
        RpcApplication.init();

        // 获取服务中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);

        // 注册服务
        for(ServiceRegisterInfo serviceRegisterInfo : serviceRegisterInfoList){
            String serviceName = serviceRegisterInfo.getServiceName();
            // 本地注册
            LocalRegistry.register(serviceName,serviceRegisterInfo.getImplClass());

            // 注册服务
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                throw new RuntimeException("服务注册失败!",e);
            }
        }

        // 启动 tcp 服务
        VertxTcpServer tcpServer = new VertxTcpServer();
        // webservice 动态监听端口 , 这里使用默认
        tcpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

        // 创建并注册 shutdown hook ， jvm 退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }
}
