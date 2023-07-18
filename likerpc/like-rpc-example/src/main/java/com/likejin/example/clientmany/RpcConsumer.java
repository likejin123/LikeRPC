package com.likejin.example.clientmany;


import com.likejin.likerpc.proxy.RpcClientDynamicProxy;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 12:22
 * @Description
 */
public class RpcConsumer {

    public static void main(String[] args) throws ClassNotFoundException {

        HelloService helloService = new RpcClientDynamicProxy<>(HelloService.class, "127.0.0.1", 7000,"com.likejin.example.server").getProxy();
        new Thread(new Runnable() {
            @Override
            public void run() {
                helloService.hello(1);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                helloService.hello("abcd");
            }
        }).start();


    }
}
