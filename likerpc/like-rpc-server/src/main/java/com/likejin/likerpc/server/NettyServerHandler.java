package com.likejin.likerpc.server;

import com.likejin.likerpc.RequestAndRepsonse.RpcRequest;
import com.likejin.likerpc.RequestAndRepsonse.RpcResponse;
import com.likejin.likerpc.util.GetClassNameUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import java.lang.reflect.Method;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:58
 * @Description
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) {
        Channel channel = ctx.channel();
        System.out.println("客户端" + ctx.channel().remoteAddress() + ":....." +"收到请求");
        System.out.println("客户端" +
                ctx.channel().remoteAddress() + ":....." +"请求信息为" + msg.toString());
        RpcResponse rpcResponse = new RpcResponse();
        try {
            Object handler = handler(msg,ctx);
            rpcResponse.setRequestId(msg.getRequestId());
            rpcResponse.setResult(handler);
        } catch (Throwable throwable) {
            rpcResponse.setRequestId(msg.getRequestId());
            rpcResponse.setError(throwable.toString());
            throwable.printStackTrace();
        }
        ctx.writeAndFlush(rpcResponse);
        System.out.println("客户端" + ctx.channel().remoteAddress() + ":....." +
                "返回的结果为"+ rpcResponse);
    }


    private Object handler(RpcRequest request,ChannelHandlerContext ctx) throws Exception {
        //获取实现接口的实现类的对象全类名
        String packageName = request.getPackageName();
        String interfaceName  = request.getClassName();
        String classImplName = GetClassNameUtil.getClassImpl(packageName,interfaceName);
        System.out.println("客户端" + ctx.channel().remoteAddress() + ":....."  +
                "处理的实际类为" + classImplName);
        //使用Class.forName进行加载Class文件(这里如何获取实例化对象。。。)
        Class<?> clazz = Class.forName(classImplName);
        
        //加载
        //获取对象
        Object bean = clazz.newInstance();
        //获取方法名
        String methodName = request.getMethodName();
        //获取参数类型
        Class<?>[] parameterTypes = request.getParameterTypes();
        //获取参数
        Object[] parameters = request.getParameters();

        //具体执行的方法
        Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
        Object result = method.invoke(bean, parameters);

        return result;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){
            //将evt向下转型 IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            String eventType = null;
            //判断事件
            switch (event.state()){
                case READER_IDLE:
                    eventType = "读空闲";
                    break;
                case WRITER_IDLE:
                    eventType = "写空闲";
                    break;
                case ALL_IDLE:
                    eventType = "读写空闲";
                    break;
            }

            System.out.println("客户端" + ctx.channel().remoteAddress() + ":....."
                    + ctx.channel().remoteAddress() + "--超时事件发生--" + eventType + "服务器主动关闭通道");


            //如果发生空闲，我们关闭通道
            ctx.channel().close();
        }
    }





}
