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

    //方案1 ： 一个客户端只能进行一次远程调用
//    //封装响应结果
//    private static RpcResponse response;
//    /*
//     * @Description 获取响应结果（获取不到就自旋）
//     * @param
//     * @return RpcResponse
//     **/
//    public static RpcResponse getResult(){
//        while(response == null){
//            try{
//                Thread.sleep(10);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            getResult();
//        }
//        return response;
//    }

    private static Map<String, RpcResponse> responseMap = new HashMap<>();

    /*
     * @Description 客户端接受返回结果
     * @param ctx
     * @param msg
     * @return void
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接受对象，将Response放入map中与对应的requstId绑定
        responseMap.put(((RpcResponse)msg).getRequestId(),(RpcResponse)msg);
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
            System.out.println("尝试获取次数：" + (++count));
        }
        return responseMap.get(requestId);
    }
}
