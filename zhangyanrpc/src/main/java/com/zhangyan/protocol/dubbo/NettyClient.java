package com.zhangyan.protocol.dubbo;

import com.zhangyan.common.Invocation;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.channel.Channel;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Executable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;


public class NettyClient {


    public com.zhangyan.protocol.dubbo.NettyClientHandler client=null;
    private URL url;
    private static ExecutorService executorService = Executors.newCachedThreadPool();
    public  NettyClientHandler start(String hostname, Integer port) {
        client =new NettyClientHandler();

        Bootstrap b =new Bootstrap();

        EventLoopGroup group = new NioEventLoopGroup();

        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel (SocketChannel socketChannel){
                    ChannelPipeline pipeline =socketChannel.pipeline();
                    pipeline.addLast("decoder",new ObjectDecoder(ClassResolvers
                            .weakCachingConcurrentResolver(this.getClass()
                                    .getClassLoader())));
                    pipeline.addLast("encoder",new ObjectEncoder());
                    pipeline.addLast("handler",client);

                    }
                });
        try{
            b.connect(hostname,port).sync();

        } catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        return client;
    }

    public String send(String hostName,Integer port,Invocation invocation)
    {

        if (client == null) {
            client = start(hostName,port);
        }
        client.setInvocation(invocation);
        try {
            String obj = null;
            try {
                obj = (String) executorService.submit(client).get(3L, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return obj;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;


    }
}
