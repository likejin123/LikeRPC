package com.likejin.likerpc.codec;

import com.likejin.likerpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:33
 * @Description
 */
public class RpcDecoder extends ByteToMessageDecoder {

    //解码的对象类型
    private Class<?> clazz;
    //序列化器
    private Serializer serializer;

    public RpcDecoder(Class<?> clazz,Serializer serializer){
        this.clazz = clazz;
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //根据协议，先读长度
        int length = byteBuf.readInt();
        byte[] data = new byte[length];
        //将byteBuf中的数据读入data字节数组
        byteBuf.readBytes(data);
        Object obj = serializer.deserialize(clazz, data);
        list.add(obj);

    }
}
