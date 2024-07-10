package com.ziio.example.registry;

import com.ziio.example.config.RegistryConfig;
import com.ziio.example.model.ServiceMetaInfo;

import javax.xml.ws.Service;
import java.security.PrivateKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 注册中心 api
 */
public interface Registry {


    /**
     * 初始化
     * @param registryConfig
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务 服务端
     * @param serviceMetaInfo
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销服务 服务端
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo);

    /**
     * 服务发现 （获取某服务的所有节点） 消费端
     * @param serviceKey
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 销毁 注册中心 --- 释放资源 （client）
     */
    void destroy();

    /**
     * 心跳检测 (provider 服务端）
     */
    void heartBeat();

    /**
     * 监听（消费端）
     * @param serviceNodeKey
     */
    void watch(String serviceNodeKey);
}
