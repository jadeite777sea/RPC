package com.zhangyan.protocol.dubbo;


import com.zhangyan.common.Invocation;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext ctx;

    private Invocation invocation;

    private Object result;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 初始化ChannelHandlerContext, 如果没有初始化，在执行提交任务的时候会空指针异常。
        this.ctx = ctx;
    }

    @Override
    public synchronized Object call() throws Exception {
        // 在Call 方法执行逻辑
        System.out.println("向服务端发送消息...");
        ctx.writeAndFlush(invocation);
        wait();
        return result;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //读取消息
        this.result = msg;
        notify();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端消息读取完毕!");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getCause().getLocalizedMessage());
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }
}
