package com.likejin.likerpc.client;

import com.likejin.likerpc.RequestAndRepsonse.RpcRequest;
import com.likejin.likerpc.RequestAndRepsonse.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:22
 * @Description netty客户端 请求响应的
 */
public class NettyClient {

    //连接的目标主机
    private String host;
    //目标主机端口号
    private int port;
    //连接成功的通道
    private Channel channel;
    //连接开启的线程组
    private NioEventLoopGroup group;
    //客户端
    private Bootstrap bootstrap;


    public NettyClient(String host,int port){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
        this.host = host;
        this.port = port;


    }



    /*
     * @Description 连接目标主机并且发送请求数据
     * @param rpcRequest
     * @return void
     **/
    public synchronized RpcResponse send(RpcRequest rpcRequest) throws InterruptedException {
        channel = bootstrap.connect(host, port).sync().channel();
        System.out.println("..........................." + channel);
        System.out.println("consumer:....." + rpcRequest);
        channel.writeAndFlush(rpcRequest);
        ChannelPipeline pipeline = channel.pipeline();
        NettyClientHandler handler = pipeline.get(NettyClientHandler.class);
        RpcResponse result = handler.getResult();
        channel.close();
        return result;
    }


    public void destroy(){
        try{
                group.shutdownGracefully().sync();
                System.out.println("consumer:....." + "执行结束，断开连接");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
