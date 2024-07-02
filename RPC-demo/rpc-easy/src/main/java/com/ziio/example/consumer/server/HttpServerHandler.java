package com.ziio.example.consumer.server;

import com.ziio.example.consumer.exRpc.LocalRegistry;
import com.ziio.example.consumer.serializer.JdkSerializer;
import com.ziio.example.consumer.serializer.Serializer;
import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * http 请求处理(拦截器)
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        // 使用 jdk 序列化器进行反序列化
        final Serializer serializer = new JdkSerializer();

        // 记录日志
        System.out.println("Received request : " + request.method() + " " + request.uri());
        // 异步处理 http 请求
        request.bodyHandler(body -> {
            // 取出 body ， 反序列化
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes,RpcRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 构造响应对象
            RpcResponse rpcResponse = new RpcResponse();
            // 空 ， 直接返回 空 response
            if(rpcRequest==null){
                rpcResponse.setMessage("request is null");
                doResponse(request ,rpcResponse,serializer);
                return;
            }

            try {
                // 选择调用的服务，通过反射获取类信息
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                // 通过反射获取方法 ， 并 invoke
                Method method = implClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(),rpcRequest.getArgs());
                // 封装 返回类
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            // 响应
            doResponse(request,rpcResponse,serializer);


        });
    }

    /**
     * 响应
     * @param request 对方请求
     * @param rpcResponse 构造的响应体
     * @param serializer 序列化器
     */
    void doResponse(HttpServerRequest request , RpcResponse rpcResponse , Serializer serializer){
        HttpServerResponse httpServerResponse = request.response().putHeader("content-type","application/json");

        // 序列化
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
