package com.ziio.example.registry;

import cn.hutool.json.JSONUtil;
import com.ziio.example.config.RegistryConfig;
import com.ziio.example.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry {

    private Client client ;

    private KV kvClient;

    /**
     * 根节点 -- 区分其他 project
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        // 创建监听etcd端口 的 client and kvClient
        client = Client.builder().endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).build();
        kvClient = client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        // 创建 lease and kv client
        Lease leaseClient = client.getLeaseClient();

        // 创建 30s 租约
        long leeseId = leaseClient.grant(30).get().getID();

        // 设置要存储的键值对
        String registerkey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        // key --- root + serviceName + version + host + port
        ByteSequence key = ByteSequence.from(registerkey, StandardCharsets.UTF_8);
        // value -- all info
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo),StandardCharsets.UTF_8);

        // 将键值对串联起来，设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leeseId).build();
        kvClient.put(key,value,putOption).get();

    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        // 删除 key
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(),StandardCharsets.UTF_8));
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 前缀搜索,结尾一定加 '/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey ;

        try {
            // 前缀查询
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix,StandardCharsets.UTF_8),getOption)
                    .get()
                    .getKvs();
            // 解析服务信息 , 将 list 转为 serviceMetaInfo
            return keyValues.stream()
                    .map(keyValue -> {
                        // 将值（all metaInfo）拿出来 , 转换为 list<serviceMetaInfo>
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value,ServiceMetaInfo.class);
                    }).collect(Collectors.toList());

        }catch (Exception e){
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    @Override
    public void destroy() {
        // 释放资源
        if(kvClient!=null){
            kvClient.close();
        }
        if(client!=null){
            client.close();
        }
    }
}
