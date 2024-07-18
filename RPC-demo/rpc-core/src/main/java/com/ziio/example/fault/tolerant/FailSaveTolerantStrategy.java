package com.ziio.example.fault.tolerant;

import com.ziio.example.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理 --- 容错策略
 * 不中断异常 ，返回一个空 rpcResponse
 */
@Slf4j
public class FailSaveTolerantStrategy implements TolerantStrategy {

    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.info("静默处理",e);
        return new RpcResponse();
    }
}
