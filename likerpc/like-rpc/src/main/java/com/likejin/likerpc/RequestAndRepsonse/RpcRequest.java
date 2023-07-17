package com.likejin.likerpc.RequestAndRepsonse;

import lombok.Data;
import lombok.ToString;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:44
 * @Description request请求对象
 */
@ToString
@Data
public class RpcRequest {


    /**
     * 用来表示本次请求的id号，方便取出对应Response
     */
    private String requestId;
    /**
     * 包名
     */
    private String packageName;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;
    /**
     * 入参
     */
    private Object[] parameters;

}
