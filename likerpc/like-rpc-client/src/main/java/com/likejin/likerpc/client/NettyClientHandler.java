package com.likejin.likerpc.client;

import com.likejin.likerpc.RequestAndRepsonse.RpcResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:48
 * @Description 客户端处理器
 */
public class NettyClientHandler extends ChannelDuplexHandler {

    private RpcResponse response;

    /*
     * @Description 客户端接受返回结果
     * @param ctx
     * @param msg
     * @return void
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("consumer:....." + "获取到consumer响应结果" + (RpcResponse)msg);
        System.out.println("consumer:....." + "通道：" + ctx.channel());
        response = (RpcResponse) msg;
    }


    /*
     * @Description 获取响应结果（获取不到就自旋）
     * @param
     * @return RpcResponse
     **/
    public RpcResponse getResult(){
        while(response == null){
            try{
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
            getResult();
        }
        return response;
    }

    //异常发生了
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
    }

    //通道手动关闭
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("consumer:....." + "通道手动关闭了");
    }

}
