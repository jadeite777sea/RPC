package com.zhangyan.protocol;

import com.zhangyan.common.Invocation;

import java.io.IOException;

public interface Protocol {
    public String send(String hostname, Integer port, Invocation invocation) throws IOException;

    public void start(String hostname,Integer port);

}
