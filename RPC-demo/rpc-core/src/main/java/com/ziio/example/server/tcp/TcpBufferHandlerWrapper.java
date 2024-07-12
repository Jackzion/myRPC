package com.ziio.example.server.tcp;


import com.ziio.example.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.parsetools.RecordParser;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;

import io.vertx.core.buffer.Buffer;

/**
 * 装饰者模式（使用 recordParser 对原有 buffer 处理能力增强）
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser parser;

    // 关联原类，对原类方法进行增强
    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        parser = initRecordParser(bufferHandler);
    }

    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造 parser ---  先读取指定头部长度，再从 header 读取 body 长度，解决粘包半包问题。
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<io.vertx.core.buffer.Buffer>() {
            // init
            int size = -1;
            // 存储完全的buffer ， header and body
            io.vertx.core.buffer.Buffer resultBuffer = io.vertx.core.buffer.Buffer.buffer();

            @Override
            public void handle(io.vertx.core.buffer.Buffer buffer) {
                if(-1==size){
                    // 读取消息体长度
                    size = buffer.getInt(4);
                    parser.fixedSizeMode(size);
                    // 写入头消息到结果
                    resultBuffer.appendBuffer(buffer);
                }else{
                    // 写入body到结果
                    resultBuffer.appendBuffer(buffer);
                    System.out.println(resultBuffer.toString());
                    // 重置一轮
                    parser.fixedSizeMode(8);
                    size = -1;
                    resultBuffer = io.vertx.core.buffer.Buffer.buffer();
                }
            }
        });
        return parser;
    }

//  当调用处理器的 handle 方法时，改为调用 RecordParser.handle
    @Override
    public void handle(Buffer buffer) {
        parser.handle(buffer);
    }
}
