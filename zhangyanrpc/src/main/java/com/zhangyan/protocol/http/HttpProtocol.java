package com.zhangyan.protocol.http;

import com.zhangyan.common.Invocation;
import com.zhangyan.protocol.Protocol;

import java.io.IOException;

public class HttpProtocol implements Protocol {
    @Override
    public String send(String hostname, Integer port, Invocation invocation) throws IOException {
        return new HttpClient().send(hostname,port,invocation);
    }

    @Override
    public void start(String hostname, Integer port) {
        new HttpServer().start(hostname,port);

    }
}
