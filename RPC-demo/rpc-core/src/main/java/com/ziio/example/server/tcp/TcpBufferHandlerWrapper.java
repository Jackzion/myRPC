package com.ziio.example.server.tcp;


import com.ziio.example.protocol.ProtocolConstant;
import io.vertx.core.Handler;
import io.vertx.core.parsetools.RecordParser;
import io.vertx.core.buffer.Buffer;

/**
 * 装饰者模式（使用 recordParser 对原有 buffer 处理能力增强）
 * TcpBufferHandlerWrapper 用于处理数据缓冲区（Buffer），它的 handle 方法在接收到数据时调用。
 */
public class TcpBufferHandlerWrapper implements Handler<Buffer> {

    private final RecordParser parser;

    // 关联原类，对原类方法进行增强
    public TcpBufferHandlerWrapper(Handler<Buffer> bufferHandler){
        parser = initRecordParser(bufferHandler);
    }

    // parser 其实也是 handler 的 一种
    private RecordParser initRecordParser(Handler<Buffer> bufferHandler) {
        // 构造 parser ---  先读取指定头部长度，再从 header 读取 body 长度，解决粘包半包问题。
        RecordParser parser = RecordParser.newFixed(ProtocolConstant.MESSAGE_HEADER_LENGTH);
        parser.setOutput(new Handler<io.vertx.core.buffer.Buffer>() {
            // init
            int size = -1;
            // 存储完全的buffer ， header and body
            // 拦截
            io.vertx.core.buffer.Buffer resultBuffer = io.vertx.core.buffer.Buffer.buffer();

            // 定义 parser handle 逻辑
            @Override
            public void handle(io.vertx.core.buffer.Buffer buffer) {
                if(-1==size){
                    // 读取消息体长度 13 ~ 16
                    size = buffer.getInt(13);
                    parser.fixedSizeMode(size);
                    // 写入头消息到结果
                    resultBuffer.appendBuffer(buffer);
                }else{
                    // 写入body到结果
                    resultBuffer.appendBuffer(buffer);
                    System.out.println(resultBuffer.toString());
                    // 已拼接为完整 Buffer，执行处理
                    bufferHandler.handle(resultBuffer);
                    // 重置一轮
                    parser.fixedSizeMode(8);
                    size = -1;
                    // 重置 resultBuffer
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
