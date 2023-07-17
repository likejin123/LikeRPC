package com.likejin.example.server;

import com.likejin.likerpc.server.NettyServer;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 12:22
 * @Description
 */
public class RpcProvider {

    public static void main(String[] args) {
        new NettyServer(7000).start();
    }
}
