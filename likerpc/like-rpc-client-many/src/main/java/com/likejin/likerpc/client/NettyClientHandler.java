package com.likejin.likerpc.client;

import com.likejin.likerpc.RequestAndRepsonse.RpcResponse;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:48
 * @Description 客户端处理器
 */
public class NettyClientHandler extends ChannelDuplexHandler {

    public static Map<String, RpcResponse> responseMap = new HashMap<>();
    /*
     * @Description 客户端接受返回结果
     * @param ctx
     * @param msg
     * @return void
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("consumer:....." + "获取到consumer响应结果" + (RpcResponse)msg);
        //输出对应通道
        System.out.println("consumer:....." + "通道：" + ctx.channel());
        //接受对象，将Response放入map中与对应的requstId绑定
        responseMap.put(((RpcResponse)msg).getRequestId(),(RpcResponse)msg);
        //处理完成返回结果关闭通道即可。

    }


    /*
     * @Description 根据requestId获取对应的响应对象
     * @param requestId
     * @return RpcResponse
     **/
    public static RpcResponse getResponseByRequestId(String requestId){
        RpcResponse response = null;
        //自旋次数
        int count = 0;
        while (responseMap.get(requestId) == null){
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("consumer:....." +"尝试获取次数：" + (++count));
        }
        response = responseMap.get(requestId);
        responseMap.remove(requestId);
        return response;
    }

    //异常发生了
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("consumer:....." + "通道手动关闭了");
    }

}
