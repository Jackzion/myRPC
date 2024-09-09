package com.ziio.example.registry;

import com.ziio.example.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费端 Service-metaInfo 缓存 , 支持多服务缓存
 */
public class RegistryServiceMultiCache {
    // 消费方本地缓存 , k - v : nodeKey - serviceMetaInfoList
    private Map<String , List<ServiceMetaInfo>> serviceCache = new ConcurrentHashMap<>();

    /**
     * 写缓存
     * @param serviceKey
     * @param serviceMetaInfoList
     */
    void writeCache(String serviceKey , List<ServiceMetaInfo> serviceMetaInfoList){
        this.serviceCache.put(serviceKey , serviceMetaInfoList);
    }

    /**
     * 读缓存
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> readCache(String serviceKey){
        return this.serviceCache.get(serviceKey);
    }

    void clearCache(String serviceKey){
        this.serviceCache.remove(serviceKey);
    }
}
