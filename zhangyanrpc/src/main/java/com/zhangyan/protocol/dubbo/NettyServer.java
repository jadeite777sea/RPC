package com.zhangyan.protocol.dubbo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;


public class NettyServer {
    public void start(String hostName,Integer port){
        try{
            //创建服务器启动对象
            final ServerBootstrap bootstrap=new ServerBootstrap();
            NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            bootstrap.group(eventLoopGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel)
                        {
                            ChannelPipeline pipeline=socketChannel.pipeline();
                            pipeline.addLast("decoder",new ObjectDecoder(ClassResolvers
                                    .weakCachingConcurrentResolver(this.getClass().getClassLoader())));
                            pipeline.addLast("encoder",new ObjectEncoder());
                            pipeline.addLast("handler",new NettyServerHandler());

                        }

                    })
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.bind(hostName,port).sync();



        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }



    }
}
