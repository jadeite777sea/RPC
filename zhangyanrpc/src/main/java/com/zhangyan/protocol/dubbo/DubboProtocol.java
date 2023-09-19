package com.zhangyan.protocol.dubbo;

import com.zhangyan.common.Invocation;
import com.zhangyan.protocol.Protocol;

public class DubboProtocol implements Protocol {
    @Override
    public String send(String hostname, Integer port, Invocation invocation) {
        NettyClient nettyClient =new NettyClient();
        return nettyClient.send(hostname,  port,  invocation);
    }

    @Override
    public void start(String hostname, Integer port) {
        new NettyServer().start(hostname,  port);
    }
}
