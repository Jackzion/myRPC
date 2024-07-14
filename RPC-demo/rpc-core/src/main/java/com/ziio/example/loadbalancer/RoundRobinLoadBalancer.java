package com.ziio.example.loadbalancer;

import com.ziio.example.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {

    // 当前轮询的下标
    // 采用 AtomicInteger 保证线程安全原子性
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfos) {
        if(serviceMetaInfos==null){
            return null;
        }
        // 只有一个服务 , 无需轮询直接返回
        int size = serviceMetaInfos.size();
        if(size==1){
            return serviceMetaInfos.get(0);
        }
        // 取模算法轮询
        int index = currentIndex.getAndIncrement()%size;
        return serviceMetaInfos.get(index);

    }
}
