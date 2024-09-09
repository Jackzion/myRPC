package com.ziio.example.utils;


import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 */
public class ConfigUtils {
    public static <T> T loadConfig(Class<T> tClass , String prefix){
        return loadConfig(tClass,prefix,"");
    }

    /**
     * 加载配置对象，支持区分环境(读取不同env的properties，转为bean)
     * @param tClass 配置类类型 -- rpcConfig
     * @param prefix 配置文件中的属性前缀
     * @param env 环境标识
     * @return 配置对象
     * @param <T>
     */
    private static <T> T loadConfig(Class<T> tClass, String prefix, String env) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if(StrUtil.isNotBlank(env)){
            configFileBuilder.append("-").append(env);
        }
        configFileBuilder.append(".properties");
        // 读取 .properties文件
        Props props = new Props(configFileBuilder.toString());
        // 将加载的配置文件中的属性转换为指定类型的 Java 对象 ， 并以 prefix 筛选配置文件中的属性
        return props.toBean(tClass,prefix);
    }
}
