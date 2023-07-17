package com.likejin.likerpc.proxy;

import com.likejin.likerpc.RequestAndRepsonse.RpcRequest;
import com.likejin.likerpc.RequestAndRepsonse.RpcResponse;
import com.likejin.likerpc.client.NettyClient;
import com.likejin.likerpc.client.NettyClientHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 12:11
 * @Description
 */
public class RpcClientDynamicProxy<T> implements InvocationHandler {
    private Class<T> interfaceClazz;

    private String host;

    private Integer port;

    private String packageName;
    public RpcClientDynamicProxy(Class<T> interfaceClazz, String host, Integer port,String packageName) {
        this.interfaceClazz = interfaceClazz;
        this.host = host;
        this.port = port;
        this.packageName = packageName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();

        //获取接口名称
        String className = method.getDeclaringClass().getName().substring(method.getDeclaringClass().getName().lastIndexOf(".")+1);
        //获取方法名称
        String methodName = method.getName();
        //传递方法参数
        Class<?>[] parameterTypes = method.getParameterTypes();

        //封装请求request的唯一id
        String id = UUID.randomUUID().toString();
        request.setRequestId(id);
        //封装请求server类所在的包名
        request.setPackageName(packageName);
        //封装请求server类实现的接口
        request.setClassName(className);
        //封装方法参数
        request.setMethodName(methodName);
        request.setParameterTypes(parameterTypes);
        request.setParameters(args);
        //开启Netty 客户端，直连
        NettyClient nettyClient = new NettyClient(host, port);
        //连接并发送请求数据
        nettyClient.connect();
        nettyClient.send(request);

        //获取结果方案1
//        RpcResponse result = NettyClientHandler.getResult();
        //获取结果方案2
        RpcResponse response = NettyClientHandler.getResponseByRequestId(id);
        System.out.println("请求调用返回结果：" + response.getResult());
        return response.getResult();
    }

    public T getProxy() {
        return (T) Proxy.newProxyInstance(interfaceClazz.getClassLoader(), new Class<?>[]{interfaceClazz}, this);
    }
}
