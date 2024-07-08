package com.ziio.example.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 服务元信息
 */
@Data
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName ;

    /**
     * 服务版本号
     */
    private String serviceVersion = "1.0";

    /**
     * 服务域名
     */
    private String serviceHost;

    /**
     * 服务端口号
     */
    private int servicePort;

    /**
     * 服务分组 -- 未实现
     */
    private String serviceGroup;

    /**
     * 获取服务键名
     */
    public String getServiceKey(){
        // 后续可拓展 serviceGroup
        return String.format("%s:%s",serviceName,serviceVersion);
    }

    /**
     * 获取服务注册节点键名
     */
    public String getServiceNodeKey(){
        return String.format("%s:%s:%s",getServiceKey(),serviceHost,servicePort);
    }

    /**
     * 获取 服务地址
     */
    public String getServiceAddress(){
        if(!StrUtil.contains(serviceHost,"http")){
            return String.format("http://%s:%s",serviceHost,servicePort);
        }
        return String.format("%s:%s",serviceHost,servicePort);
    }
}
