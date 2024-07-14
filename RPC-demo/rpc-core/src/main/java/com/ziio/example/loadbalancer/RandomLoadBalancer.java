package com.ziio.example.loadbalancer;

import com.ziio.example.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();

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

        // 取随机下标
        return serviceMetaInfos.get(random.nextInt(size));

    }
}
