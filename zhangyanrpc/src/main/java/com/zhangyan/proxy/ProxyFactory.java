package com.zhangyan.proxy;

import com.zhangyan.Loadbalance.Loadbalance;
import com.zhangyan.common.Invocation;
import com.zhangyan.common.URL;
import com.zhangyan.protocol.Protocol;
import com.zhangyan.protocol.dubbo.NettyClient;
import com.zhangyan.protocol.http.HttpClient;
import com.zhangyan.register.MapRemoteRegister;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import static com.zhangyan.proxy.ProtocolFactory.getProtocol;

public class ProxyFactory {

    public static <T> T getProxy(Class interfaceClass){
    //用户配置
        // 用户配置
        //创建代理对象的方法
        Object proxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                //mock服务实现
                /*String mock= System.getProperty("mock");
                if(mock!=null && mock.startsWith("return:"))
                {
                    String result=mock.replace("return:","");
                    return result;

                }*/
                Invocation invocation = new Invocation(interfaceClass.getName(), method.getName(),
                        method.getParameterTypes(), args);

                //用于发送请求
                Protocol client=getProtocol();

                //服务发现
                List<URL> list= MapRemoteRegister.get(interfaceClass.getName());

                List<URL> invokedUrls=new ArrayList<>();

                //服务调用
                int max=3;
                String result=null;
                while(max>0)
                {
                    //负载均衡
                    list.remove(invokedUrls);

                    URL url= Loadbalance.random(list);
                    invokedUrls.add(url);
                    try{
                        return client.send(url.getHostname(),url.getPort(),invocation);


                    }catch (Exception e)
                    {
                        if(max--!=0)
                        {
                            continue;
                        }
                        //error-callback=com.zhouyu.HelloServiceErrorCallback
                        System.out.println("访问失败重试");
                    }
                }
                return "容错处理";





            }
        });
        return (T) proxyInstance;
    }
}
