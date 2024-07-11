package com.ziio.example.protocol;

import com.ziio.example.model.RpcRequest;
import com.ziio.example.model.RpcResponse;
import com.ziio.example.serializer.Serializer;
import com.ziio.example.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.awt.*;
import java.io.IOException;

/**
 * 解码器 Buffer ---> ProtocolMessage
 */
public class ProtocolMessageDecoder {
    /**
     * 解码
     * @param buffer
     * @return
     * @throws IOException
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        // 分别从指定位置读出 Buffer
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        // 第一个字节读出 magic
        byte magic = buffer.getByte(0);
        // 效验魔数
        if(magic!=ProtocolConstant.PROTOCOL_MAGIC){
            return null;
        }
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        // long 占 8字节
        header.setRequestId(buffer.getLong(5));
        header.setBodyLength(buffer.getInt(13));

        // 读取 17 开始 ---> body
        // 解决粘包问题 ， 只读指定长度的数据
        byte[] bodyBytes = buffer.getBytes(17,17 + header.getBodyLength());
        // 选择序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if(serializerEnum==null){
            throw new RuntimeException("序列化消息的协议不存在");
        }
        Serializer serializer = SerializerFactory.getInstance(serializerEnum.getValue());

        // 根据不同的消息类型，序列化为不同的 Class 类型
        ProtocolMessageTypeEnum typeEnum = ProtocolMessageTypeEnum.getEnumByValue(header.getType());
        if(typeEnum==null){
            throw new RuntimeException("序列化消息的类型不存在");
        }
        switch (typeEnum){
            case REQUEST:
                RpcRequest request = serializer.deserialize(bodyBytes,RpcRequest.class);
                return new ProtocolMessage<>(header,request);
            case RESPONSE:
                RpcResponse response = serializer.deserialize(bodyBytes,RpcResponse.class);
                return new ProtocolMessage<>(header,response);
            case HEART_BEAT:
            case OTHERS:
            default:
                throw new RuntimeException("暂不支持该消息类型");
        }
    }
}
