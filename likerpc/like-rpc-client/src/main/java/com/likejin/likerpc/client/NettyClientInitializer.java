package com.likejin.likerpc.client;

import com.likejin.likerpc.RequestAndRepsonse.RpcResponse;
import com.likejin.likerpc.codec.RpcDecoder;
import com.likejin.likerpc.codec.RpcEncoder;
import com.likejin.likerpc.serializer.JSONSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:25
 * @Description
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //添加编解码器
        //添加编码器(无需对象类型，直接编码为字节)
        pipeline.addLast(new RpcEncoder(new JSONSerializer()));

        //添加解码器（需要对象类型，解码为对应对象）
        pipeline.addLast(new RpcDecoder(RpcResponse.class,new JSONSerializer()));

        //添加自定义handler
        pipeline.addLast(new NettyClientHandler());

    }
}
