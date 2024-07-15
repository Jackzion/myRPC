package com.ziio.example.loadbalancer;

import com.ziio.example.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {

    /**
     * 一致性 hash ring ， 放置虚拟节点
     */
    private final TreeMap<Integer,ServiceMetaInfo> virtualNodes = new TreeMap<>();

    /**
     * 虚拟节点数
     */
    private static final int VIRTUAL_NODE_NUM = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfos) {
        // 服务列表为空
        if(serviceMetaInfos==null){
            return null;
        }
        // 只有一个服务 , 无需轮询直接返回
        int size = serviceMetaInfos.size();
        if(size==1){
            return serviceMetaInfos.get(0);
        }

        // 构造虚拟节点 --- 每次调用都要重构节点环，为了服务下线等情况
        for(ServiceMetaInfo serviceMetaInfo : serviceMetaInfos){
            for(int i=0;i<VIRTUAL_NODE_NUM;i++){
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash,serviceMetaInfo);
            }
        }

        // 调用参数 hashCode
        int hash = getHash(requestParams);

        // 选择最近且大于等于调用hash值的虚拟节点
        // treeMap.ceilingEntry --- 查找大于等于的键
        Map.Entry<Integer,ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if(entry==null){
            entry = virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    private int getHash(Object object) {
        return object.hashCode();
    }
}
