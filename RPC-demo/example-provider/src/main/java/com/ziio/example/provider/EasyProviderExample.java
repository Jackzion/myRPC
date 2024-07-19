package com.ziio.example.provider;

import com.ziio.example.RpcApplication;
import com.ziio.example.bootstrap.ProviderBootstrap;
import com.ziio.example.common.service.UserService;
import com.ziio.example.config.RegistryConfig;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.model.ServiceRegisterInfo;
import com.ziio.example.registry.LocalRegistry;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import com.ziio.example.server.tcp.VertxTcpServer;

import java.util.ArrayList;
import java.util.List;

public class EasyProviderExample {
    public static void main(String[] args) {

        // 要注册的服务
        List<ServiceRegisterInfo> serviceRegisterInfoList = new ArrayList<>();
        ServiceRegisterInfo serviceRegisterInfo = new ServiceRegisterInfo(UserService.class.getName(), UserServiceImpl.class);
        serviceRegisterInfoList.add(serviceRegisterInfo);

        // 服务提供者初始化 --- 传入服务信息即可
        ProviderBootstrap.init(serviceRegisterInfoList);

    }
}
