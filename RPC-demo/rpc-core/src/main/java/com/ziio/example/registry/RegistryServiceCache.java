package com.ziio.example.registry;

import com.ziio.example.model.ServiceMetaInfo;

import java.util.ArrayList;
import java.util.List;

public class RegistryServiceCache {
    /**
     * 服务缓存
     */
    List<ServiceMetaInfo> serviceCache;

    void writeCache(List<ServiceMetaInfo> newServiceCache){
        this.serviceCache = serviceCache;
    }

    List<ServiceMetaInfo> readCache(){
        return this.serviceCache;
    }
    void clearCache(){
        this.serviceCache.clear();
    }

}
