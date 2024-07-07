package com.ziio.example.serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂 + 单例 -- 复用
 */
public class SerializerFactory {

    /**
     * 序列化器映射，单例
     */
    private static final Map<String,Serializer> KEY_SERIALIZER_MAP = new HashMap<String,Serializer>(){{
        put(SerializerKeys.JDK,new JdkSerializer());
        put(SerializerKeys.JSON,new JsonSerivalizer());
        put(SerializerKeys.KRYO,new KryoSerializer());
        put(SerializerKeys.HESSIAN,new HessianSerializer());
    }
    };
    /**
     * 默认序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get("jdk");

    public static Serializer getInstance(String key){
        return KEY_SERIALIZER_MAP.get(key);
    }


}
