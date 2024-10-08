package com.ziio.example;

import com.ziio.example.config.RegistryConfig;
import com.ziio.example.config.RpcConfig;
import com.ziio.example.constant.RpcConstant;
import com.ziio.example.registry.Registry;
import com.ziio.example.registry.RegistryFactory;
import com.ziio.example.utils.ConfigUtils;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

import javax.security.auth.login.AppConfigurationEntry;

/**
 * RPC 框架应用 , 支持传入自定义配置
 * holder 存放全局变量。。双检索单例模式实现
 */
@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化，支持传入自定义配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init, config = {}", newRpcConfig.toString());
        // 注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init , config = {}",registryConfig);
        // 创建并注册 shutdownHook
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }


    /**
     * 框架初始化
     */
    public static void init(){
        RpcConfig newRpcConfig;
        try{
            // 自定义加载，从 properties
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            // 加载配置失败，使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置，双检索单例模式
     */
    public static RpcConfig getRpcConfig(){
        if(rpcConfig==null){
            synchronized (RpcApplication.class){
                if(rpcConfig==null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
