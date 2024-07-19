package com.example.rpcstarter.bootstrap;

import com.example.rpcstarter.annotation.EnableRpc;
import com.ziio.example.RpcApplication;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * rpc 框架启动
 */
@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {

    /**
     * spring 初始化时执行，初始化 rpc 框架
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取 enableRpc 注解属性
        boolean needServer = (boolean)importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName())
                .get("needServer");

        // rpc 框架式初始化
        RpcApplication.init();

        // 全局配置
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 启动服务器
        if(needServer){
            VertxTcpServer tcpServer = new VertxTcpServer();
            tcpServer.doStart(rpcConfig.getServerPort());
        }else{
            log.info("不启动 server");
        }
    }
}
