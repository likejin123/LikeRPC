# 一.流程

## 1.协议

+ 约定好请求和响应

请求和响应中维护了一个requestId，为了辨识一个消费者中的多次调用

+ 约定好防止粘包 先发一个len，再发bytes

## 2.序列化

+ 使用fastjson提供的对象转字节
+ 使用fastjson提供的字节转对象

## 3.客户端

```java
//根据动态代理获获取对应的代理对象

//接口 服务提供方主机 地址  要调用的对象在服务提供方中的包名
HelloService helloService = new RpcClientDynamicProxy<>(HelloService.class, "127.0.0.1", 7000,"com.likejin.example.server").getProxy();

//代理对象执行远程调用并且获取返回结果  
helloService.hello("你好吗");
```

## 4.服务端

```java
//开启服务监听端口     

new NettyServer(7000).start();
```



## 5.传输流程

客户端 封装request对象 -> 客户端Encoder为bytes - >

服务端 Decoder为request对象 -> 处理并返回response对象 ->服务端 Encoder为bytes ->

客户端 Decoder为response对象 ->获取结果。。。。

+ 其中核心代码

```java
客户端动态代理获取代理类，收发数据，获取结果
客户端encoder编码为字节并且先发长度，再发字节
客户端Handler维护map来映射本客户端每个请求对应的每个Response（自旋获取结果）
服务端decoder先读int，再读字节，解码为Rquest
服务端通过对应的包名获取包下所有类，通过接口名找到实现了该接口的类，执行对应方法，返回结果。
```



# 二.问题

## 1.解决问题1：如何确定消费者要调用的提供方的全类名

+ RpcRequest中加入对应的调用服务的包名
+ 消费者 传入调用对应对象的 包名
+ 服务方扫描对应的包下的所有类，找到实现消费者调用接口的实现类。

## 2.解决问题2：动态代理中用netty获取的通道发送数据，如何接受返回结果呢？

### 1.解决方案1：在channel中直接加入静态方法

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

### 2.解决方案2：为每一个请求加入对应的id

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



## 3.如何关闭客户端的通道和线程组和服务器端的通道呢？

+ 在invoke中执行完结果直接destroy关闭线程组
