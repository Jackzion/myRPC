package com.ziio.example.provider;

import com.ziio.example.RpcApplication;
import com.ziio.example.common.service.UserService;
import com.ziio.example.config.RegistryConfig;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.registry.LocalRegistry;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import com.ziio.example.server.HttpServer;
import com.ziio.example.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {
        // rpc 框架初始化
        RpcApplication.init();

        // 注册服务
        String serviceName = UserService.class.getName();
        // 使用本地注册表注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        // 获取服务中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);

        // 注册服务
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();
        // webservice 动态监听端口 , 这里使用默认
        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

        // 创建并注册 shutdown hook ， jvm 退出时执行操作
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }
}
