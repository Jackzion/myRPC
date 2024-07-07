package com.ziio.example.serializer;

import cn.hutool.core.io.resource.ResourceUtil;
import com.esotericsoftware.minlog.Log;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 读取配置并加载实现类的方法
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类：接口名 =>（key => 实现类）
     */
    private static Map<String , Map<String,Class<?>>> loaderMap = new ConcurrentHashMap<>();
    /**
     * 对象实例缓存（避免重复 new），类路径 => 对象实例，单例模式
     */
    private static Map<String , Object> instanceCach = new ConcurrentHashMap<>();
    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";
    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/system/";
    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR,RPC_CUSTOM_SPI_DIR};
    /**
     * 动态加载类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有类型
     */
    public static void loadAll(){
        log.info("加载所有的SPI");
        for(Class<?> aClass : LOAD_CLASS_LIST){
            load(aClass);
        }
    }

    /**
     * 获得某个类型的实力 --- 单例复用
     * @param tClass
     * @param key
     * @return
     * @param <T>
     */
    public static <T> T getInstance(Class<?> tClass, String  key) {
        String tClassName = tClass.getName();
        // 得到实现该接口所有类型信息
        Map<String,Class<?>> keyClassMap = loaderMap.get(tClassName);
        if(keyClassMap == null){
            throw new RuntimeException(String.format("SpiLoader 未加载该接口 %s ",tClassName));
        }
        if(!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("SpiLoader 不存在该类型 %s ",tClassName));
        }
        // 获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        String implClassName = implClass.getName();
        // 从实例缓存加载
        if(!instanceCach.containsKey(key)){
            try {
                // 没有，新建实现类
                instanceCach.put(implClassName,implClass.newInstance());
            } catch (InstantiationException |IllegalAccessException e) {
                String errorMsg = String.format("%s 实例化失败 !",implClassName);
                throw new RuntimeException(errorMsg,e);
            }
        }
        return (T) instanceCach.get(implClassName);
    }

    /**
     * 加载某个类型
     *
     * @param loadClass
     * @throws IOException
     */
    public static Map<String , Class<?>> load(Class<?> loadClass){
        log.info("加载类型为 {} 的 SPI", loadClass.getName());
        Map<String,Class<?>> keyClassMap = new HashMap<>();
        // 扫描路径，用户自定义高于 system
        for(String scanDir : SCAN_DIRS){
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            // 读取每个资源文件
            for(URL resource : resources){
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    // 解析每一行 ， 将路径作为 key ， class 作为 value
                    while ((line = bufferedReader.readLine())!=null){
                        String[] strArray = line.split("=");
                        if(strArray.length>1){
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key,Class.forName(className));
                        }
                    }

                } catch (Exception e) {
                    log.error("spi resource load error !!!!!!!",e);
                }
            }
        }
        loaderMap.put(loadClass.getName(),keyClassMap);
        return keyClassMap;
    }
}
