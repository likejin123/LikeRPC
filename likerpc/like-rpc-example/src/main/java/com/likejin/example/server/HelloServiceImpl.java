package com.likejin.example.server;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 12:23
 * @Description
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        return msg;
    }
}
