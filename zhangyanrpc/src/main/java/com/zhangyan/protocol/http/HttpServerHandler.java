package com.zhangyan.protocol.http;

import com.zhangyan.common.Invocation;
import com.zhangyan.register.LocalRegister;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HttpServerHandler {
    public void handler(HttpServletRequest req, HttpServletResponse resp){
        //处理请求->接口、方法、方法参数
        try {
            Invocation invocation = (Invocation) new ObjectInputStream(req.getInputStream()).readObject();
            String interfaceName = invocation.getInterfaceName();
            //通过接口名获得类对象
            Class classImpl= LocalRegister.get(interfaceName,"1.0");
            //得到方法对象,使用反射方法得到结果
            Method method=classImpl.getMethod(invocation.getMethodName(),invocation.getParameterTypes());
            String result = (String) method.invoke(classImpl.newInstance(), invocation.getParameters());
            //使用common-io的接口将结果写入resp
            IOUtils.write(result, resp.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
