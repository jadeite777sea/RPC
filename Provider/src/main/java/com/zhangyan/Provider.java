package com.zhangyan;


import com.zhangyan.protocol.Protocol;
import com.zhangyan.protocol.dubbo.NettyServer;
import com.zhangyan.protocol.http.HttpServer;
import com.zhangyan.register.LocalRegister;
import com.zhangyan.common.URL;
import com.zhangyan.register.MapRemoteRegister;

import java.io.IOException;

import static com.zhangyan.proxy.ProtocolFactory.getProtocol;

public class Provider {
    public static void main(String[] args) throws IOException, ClassNotFoundException {



        //进行本地注册
        LocalRegister.regist(HelloService.class.getName(),"1.0",HelloServiceImpl.class);
        //LocalRegister.regist(HelloService.class.getName(),"2.0",HelloServiceImpl.class);

        //注册中心注册

        URL url = new URL("localhost", 2048);

        MapRemoteRegister.regist(HelloService.class.getName(), url);


        Protocol server=getProtocol();

        server.start(url.getHostname(),url.getPort());



    }
}
