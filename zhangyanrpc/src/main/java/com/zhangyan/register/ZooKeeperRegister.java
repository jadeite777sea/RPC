package com.zhangyan.register;


import com.zhangyan.HelloService;
import com.zhangyan.common.URL;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author bingbing
 * @date 2021/4/30 0030 18:39
 * 注册接口名，服务地址列表
 */
public class ZooKeeperRegister {


    // 在静态块里初始化客户端
    static CuratorFramework client;

    private static String dubboServicePath = "/dubbo/service/";

    static {
        client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(3, 1000));
        client.start();
    }

    // 用map做本地缓存
    private static Map<String, List<URL>> listMap = new HashMap<>();

    // 使用临时节点挂载URL相关信息
    public static void registry(String interfaceName, URL url) {
        // 1. 将url信息挂载到zookeeper目录下
        try {
            String result = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(String.format(dubboServicePath + "%s/%s", interfaceName, JSONObject.toJSONString(url)));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过watch机制监听zookeeper
     *
     * @return
     */
    public static void watch() {
        //将interfaceName的服务地址列表更新zookeeper
        AtomicReference<Boolean> watchFlag= new AtomicReference<>(false);
        Watcher watcher = (WatchedEvent -> {
            watchFlag.set(true);
            System.out.println("监听到事件!" + WatchedEvent);
        });
        if (watchFlag.get()==false) {
            return;
        }
        // 1.获取到所有的接口节点，刷新本地map

        try {
            List<String> strs = client.getChildren().forPath(dubboServicePath);
            for (String s : strs) {
                // 先获取到类Class
                String userInterfaceName = JSONObject.parseObject(s, HelloService.class).getClass().getName();
                List<URL> urlList = new ArrayList<>();
                if (listMap.containsKey(userInterfaceName)) {
                    List<String> urlStrs = client.getChildren().forPath(dubboServicePath + userInterfaceName);
                    for (String urlStr : urlStrs) {
                        urlList.add(JSONObject.parseObject(urlStr, URL.class));
                    }
                    //更新map
                    listMap.put(userInterfaceName, urlList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static List<URL> get(String interfaceName) {
        // 1. 如果本地map里有，那么就从map中取数据
        if (listMap.containsKey(interfaceName)) {
            // 监听机制
            watch();
            return listMap.get(interfaceName);
        }
        List<URL> urlList = new ArrayList<>();
        // 2. 如果本地map没有，那么从zookeeper注册中心拿数据
        try {
            List<String> strs = client.getChildren().forPath(String.format(dubboServicePath + "%s", interfaceName));
            for (String s : strs) {
                // 反序列化url对象
                urlList.add(JSONObject.parseObject(s, URL.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 3. 将zookeeper里的信息缓存到本地map里
        listMap.put(interfaceName, urlList);
        return urlList;
    }



}
