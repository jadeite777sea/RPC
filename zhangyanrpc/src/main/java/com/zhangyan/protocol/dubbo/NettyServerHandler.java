package com.zhangyan.protocol.dubbo;

import com.zhangyan.common.Invocation;
import com.zhangyan.register.LocalRegister;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        System.out.println("客户端通道建立完成");
    }
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Invocation invocation =(Invocation) msg;
        Class serviceImpl =LocalRegister.get(invocation.getInterfaceName(),"1.0");

        Method method =serviceImpl.getMethod(invocation.getMethodName(),invocation.getParameterTypes());
        Object result =method.invoke(serviceImpl.newInstance(),invocation.getParameters());

        ctx.writeAndFlush("Netty:"+result);


    }
}
