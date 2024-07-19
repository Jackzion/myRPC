package com.ziio.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务注册信息类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRegisterInfo {
    /**
     * 服务名称 --- 接口 className
     */
    public String serviceName;
    /**
     * 实现类
     */
    public Class<?> implClass;
}
