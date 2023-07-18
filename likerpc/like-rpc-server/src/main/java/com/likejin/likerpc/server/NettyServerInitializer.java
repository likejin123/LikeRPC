package com.likejin.likerpc.server;

import com.likejin.likerpc.RequestAndRepsonse.RpcRequest;
import com.likejin.likerpc.codec.RpcDecoder;
import com.likejin.likerpc.codec.RpcEncoder;
import com.likejin.likerpc.serializer.JSONSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:57
 * @Description
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //添加编解码器
        //添加编码器(无需对象类型，直接编码为字节)
        pipeline.addLast(new RpcEncoder(new JSONSerializer()));

        //添加解码器（需要对象类型，解码为对应对象）
        pipeline.addLast(new RpcDecoder(RpcRequest.class,new JSONSerializer()));

        //添加心跳机制关闭客户端
        pipeline.addLast(new IdleStateHandler(0,0,10, TimeUnit.SECONDS));

        //添加自定义handler
        pipeline.addLast(new NettyServerHandler());

    }
}
