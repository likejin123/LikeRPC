# 1.解决问题1：如何确定消费者要调用的提供方的全类名

+ RpcRequest中加入对应的调用服务的包名
+ 消费者 传入调用对应对象的 包名
+ 服务方扫描对应的包下的所有类，找到实现消费者调用接口的实现类。

# 2.解决问题2：动态代理中用netty获取的通道发送数据，如何接受返回结果呢？

## 1.解决方案1：在channel中直接加入静态方法

```java
    //封装响应结果
    private static RpcResponse response;

    /*
     * @Description 客户端接受返回结果
     * @param ctx
     * @param msg
     * @return void
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //response
        response = (RpcResponse) msg;
    }

    /*
     * @Description 获取响应结果（获取不到就自旋）
     * @param
     * @return RpcResponse
     **/
    public static RpcResponse getResult(){
        while(response == null){
            try{
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
            getResult();
        }
        return response;
    }

```

+ 问题：此时如果服务方启动多个调用，无法确定哪个调用是哪一个的，返回返回结果出错。。

## 2.解决方案2：为每一个请求加入对应的id

+ 为RpcRquest加入请求id
+ 在请求时经过handler在handler维护一个map，发送数据时就生成id和对应的RpcResponse
+ 那么在接收数据时只需要赋值RpcResponse即可。。（也需要在RpcResponse中表示RpcRquest，这样接受数据时才可以获得map中对应的RpcResponse）
+ 然后获取即可。

```java
    private static Map<String, RpcResponse> responseMap = new HashMap<>();

    /*
     * @Description 客户端接受返回结果
     * @param ctx
     * @param msg
     * @return void
     **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //接受对象，将Response放入map中与对应的requstId绑定
        responseMap.put(((RpcResponse)msg).getRequestId(),(RpcResponse)msg);
    }
```

```java
//自旋获取结果
    /*
     * @Description 根据requestId获取对应的响应对象
     * @param requestId
     * @return RpcResponse
     **/
    public static RpcResponse getResponseByRequestId(String requestId){
        RpcResponse response = null;
        //自旋次数
        int count = 0;
        while (responseMap.get(requestId) == null){
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            System.out.println("尝试获取次数：" + (++count));
        }
        return responseMap.get(requestId);

    }
```



