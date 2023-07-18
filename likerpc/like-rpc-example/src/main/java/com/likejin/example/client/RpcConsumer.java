package com.likejin.example.client;

import com.likejin.likerpc.proxy.RpcClientDynamicProxy;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 12:22
 * @Description
 */
public class RpcConsumer {
    public static void main(String[] args) throws ClassNotFoundException {
        HelloService helloService = new RpcClientDynamicProxy<>(HelloService.class, "127.0.0.1", 7000,"com.likejin.example.server").getProxy();
        helloService.hello(1);
    }




}
