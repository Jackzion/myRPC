package com.ziio.example.fault.retry;

import com.github.rholder.retry.*;
import com.ziio.example.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔重试
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    /**
     * 采用 guava 库 ， 对传进的 callable 任务进行重试 3次 ， 每次 3 秒间隔
     * @param callable
     * @return
     * @throws Exception
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        Retryer<RpcResponse> retryer  = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 重试条件
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS)) // 重试间隔
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 重试次数
                .withRetryListener(new RetryListener() { // 监听重试任务
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数 {}",attempt.getAttemptNumber());
                    }
                }).build();
        return retryer.call(callable);
    }
}
