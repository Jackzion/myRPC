package com.example.rpcstarter.bootstrap;

import com.example.rpcstarter.annotation.RpcService;
import com.ziio.example.RpcApplication;
import com.ziio.example.config.RegistryConfig;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.model.ServiceMetaInfo;
import com.ziio.example.registry.LocalRegistry;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * rpc 服务提供者启动
 * 让启动类实现 BeanPostProcessor 接口的 postProcessAfterInitialization 方法，
 * 就可以在某个服务提供者 Bean 初始化后，执行注册服务等操作了
 */
public class RpcProviderBootstrap implements BeanPostProcessor {

    /**
     * bean 初始化后执行
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 反射 获得 bean class信息
        Class<?> beanClass = bean.getClass();
        // 获得注解信息
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);

        if(rpcService!=null){
            // 需要注册服务
            // 获取服务基本信息
            Class<?> interfaceClass = rpcService.interfaceClass();
            // 默认值处理
            if(interfaceClass== Void.class){
                // 获取 bean 接口信息 ，赋值
                interfaceClass = beanClass.getInterfaces()[0];
            }
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();

            // 全局配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            // 获取服务中心
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
            registry.init(registryConfig);

            // 注册服务
            // 本地注册
            LocalRegistry.register(serviceName,beanClass);

            // 注册中心
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
        return BeanPostProcessor.super.postProcessAfterInitialization(bean,beanName);
    }
}
