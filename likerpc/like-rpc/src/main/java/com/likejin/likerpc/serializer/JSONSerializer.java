package com.likejin.likerpc.serializer;

import com.alibaba.fastjson.JSON;

/**
 * @Author 李柯锦
 * @Date 2023/7/17 11:26
 * @Description
 */
public class JSONSerializer implements Serializer {


    /*
     * @Description 使用fastjson进行序列化
     * @param object
     * @return byte
     **/
    @Override
    public byte[] serializer(Object object) throws Exception {

        return JSON.toJSONBytes(object);
    }

    /*
     * @Description 使用fastjson进行反序列化
     * @param clazz
     * @param bytes
     * @return T
     **/
    @Override
    public <T> T deserialize(Class<T> clazz, byte[] bytes) throws Exception {

        return JSON.parseObject(bytes, clazz);
    }
}
