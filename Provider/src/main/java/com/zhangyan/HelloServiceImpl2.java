package com.zhangyan;

public class HelloServiceImpl2 implements HelloService
{

    @Override//一个标签用于检查下面的方法是否为父类的方法
    public String sayHello(String name){
        return "hello"+ name;
    }
}
