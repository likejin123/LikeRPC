package com.likejin.likerpc.serializer;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:18
 * @Description
 */
public interface Serializer {


    /*
     * @Description java对象序列化为字节
     * @param object
     * @return byte
     **/
    byte[] serializer(Object object) throws Exception;




    /*
     * @Description 字节反序列化为java对象
     * @param clazz
     * @param bytes
     * @return T
     **/
    <T> T deserialize(Class<T> clazz,byte[] bytes) throws Exception;
}
