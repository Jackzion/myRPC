package com.ziio.example.consumer.exRpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地注册中心
 */
public class LocalRegistry {
    /*
     * 注册信息存储 (className , class)
     */
    private static final Map<String,Class<?>> map = new ConcurrentHashMap<>();

    /*
     *注册服务
     */
    public static void register(String serviceName,Class<?> implClass){
        map.put(serviceName,implClass);
    }

    /*
     *获取服务
     */
    public static Class<?> get(String serviceName){
       return map.get(serviceName);
    }

    /*
     *删除服务
     */
    public static void remove(String serviceName){
        map.remove(serviceName);
    }
}
