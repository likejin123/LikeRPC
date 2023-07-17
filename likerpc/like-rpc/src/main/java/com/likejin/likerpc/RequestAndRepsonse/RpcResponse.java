package com.likejin.likerpc.RequestAndRepsonse;

import lombok.Data;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:45
 * @Description
 */
@Data
public class RpcResponse {

    /**
     * 标识本次RpcResponse回应的是哪一个requestId
     */
    private String requestId;
    /**
     * 错误信息
     */
    private String error;
    /**
     * 返回的结果
     */
    private Object result;
}
