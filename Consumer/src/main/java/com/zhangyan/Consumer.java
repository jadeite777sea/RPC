package com.zhangyan;

import com.zhangyan.proxy.ProxyFactory;

import java.io.IOException;

public class Consumer {
    public static void main(String []args) throws IOException {

        /*Invocation invocation=new Invocation(HelloService.class.getName(),"sayHello",new Class[]{String.class},new Object[]{"zhangyan"} );
        HttpClient httpClient=new HttpClient();
        String result= httpClient.send("localhost",2048,invocation);
        System.out.println(result);*/
        //使用HelloService.class接口作为参数传入ProxyFactory.getProxy中
        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
        String result = helloService.sayHello("zhouyu123123123");
        System.out.println(result);




    }

}
