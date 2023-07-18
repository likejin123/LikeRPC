package com.likejin.likerpc.client;

import com.likejin.likerpc.RequestAndRepsonse.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
    NioEventLoopGroup group;

    public NettyClient(String host,int port){
        this.host = host;
        this.port = port;
    }


    /*
     * @Description 客户端连接目标主机
     * @param
     * @return void
     **/
    public void connect() throws Exception{
        group = new NioEventLoopGroup(1);
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientInitializer());

        channel = bootstrap.connect(host, port).sync().channel();
    }

    /*
     * @Description 发送请求数据
     * @param rpcRequest
     * @return void
     **/
    public void send(RpcRequest rpcRequest)  {
        System.out.println("consumer:....." + rpcRequest);
        channel.writeAndFlush(rpcRequest);
    }

    public void destroy(){
        try{
            channel.close().sync();
            group.shutdownGracefully().sync();
            System.out.println("consumer:....." + "执行结束，断开连接");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
