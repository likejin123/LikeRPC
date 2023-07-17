package com.likejin.likerpc.codec;

import com.likejin.likerpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:30
 * @Description 编码器，将o转化为字节
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Serializer serializer;

    public RpcEncoder(Serializer serializer){
        this.serializer = serializer;
    }

    /*
     * @Description 编码 将对象o转换成 ByteBuf输出
     * @param channelHandlerContext
     * @param o
     * @param byteBuf
     * @return void
     **/
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        byte[] bytes = this.serializer.serializer(o);
        //防止粘包 先发送一个length
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);

    }
}
