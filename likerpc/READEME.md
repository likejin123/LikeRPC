# 一.服务端

## 1.协议

+ 约定好请求和响应

请求和响应中维护了一个requestId，为了辨识一个消费者中的多次调用

+ 约定好防止粘包 先发一个len，再发bytes

## 2.序列化

+ 使用fastjson提供的对象转字节
+ 使用fastjson提供的字节转对象

## 3.服务端

```java
//开启服务监听端口     

new NettyServer(7000).start();
```

## 4.客户端



# 二.核心技术

## 1.服务端

通过接口名和包名反射找到对应实现类并执行

## 2.客户端

+ 动态代理来调用方法 （一个动态代理对应一个接口，一个接口对应一个NettyClient）
+ 在invoke方法中利用netty客户端远程请求服务端，获取结果。

```java
//请求服务端容易
Channel channel = bootstrap.connect().sync().channel()

channel.writeAndFlush()
    

//如何获取结果(handler中的read方法)
解决方案1：在channel中直接加入静态方法
    
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
//问题：如果一个接口通过多线程调用多个方法，那么如何确定谁的返回结果？
//解决方案：维护一个map 在request和response中都维护一个requestID（使用同一个通道）
优点：速度快 缺点：编码复杂（判断map为空，则关闭netty客户端）....有问题的。。。
//解决方案：每一次调用方法nettyClient的send---串行化，增加synchronized，一个通道发完获取结果，另一个通道再发。。
优点：编码简单。缺点：耗时（计数器，count++）....有问题的。。。
//解决方案：直接对invoke上锁。
优点：编码简单。 缺点：非常耗时（必须在应用代码时计数器，客户端体验差）。。
    

三者共有缺点：什么时候判断关闭netty客户端。
```

```java
//维护map
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

```java
//每次调用都获取一个通道
即每次先connnect获取通道。。
```

```java
//第一种方法的通道id
consumer:.....获取到consumer响应结果RpcResponse(requestId=8049583d-05ae-460c-a9f3-8067e44cb6a4, error=null, result=abcd)
consumer:.....通道：[id: 0x1bafc469, L:/127.0.0.1:1388 - R:/127.0.0.1:7000]
consumer:.....获取到consumer响应结果RpcResponse(requestId=f1d2d7fa-3851-4f97-a54e-88d155588d64, error=null, result=1)
consumer:.....通道：[id: 0x1bafc469, L:/127.0.0.1:1388 - R:/127.0.0.1:7000]

//第二种方法的通道id
[id: 0x8c22a86e, L:/127.0.0.1:1076 - R:/127.0.0.1:7000]
[id: 0xb9db136b, L:/127.0.0.1:1075 - R:/127.0.0.1:7000]
```

