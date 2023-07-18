package com.likejin.likerpc.client;

import com.likejin.likerpc.RequestAndRepsonse.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:22
 * @Description netty客户端 请求响应的
 */
@Data
public class NettyClient {

    //连接成功的通道
    private Channel channel;
    //连接开启的线程组
    private NioEventLoopGroup group;
    //netty的启动BootStap
    private Bootstrap bootstrap;

    public NettyClient(String host,int port){
        group = new NioEventLoopGroup(1);
        bootstrap = new Bootstrap();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
        try{
            channel = bootstrap.connect(host, port).sync().channel();
        }catch (Exception e){
            e.printStackTrace();
        }
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

    


    public synchronized void  destroy() {
        try{
            if(channel.isActive()){
                channel.close().sync();
                group.shutdownGracefully().sync();
                System.out.println("consumer:....." + "执行结束，断开连接");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
