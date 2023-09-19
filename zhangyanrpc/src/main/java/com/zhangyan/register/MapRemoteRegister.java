package com.zhangyan.register;

import com.zhangyan.common.URL;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MapRemoteRegister
{
    //private static Map<String, List<URL>> map = new HashMap<>();
    private static JedisPoolConfig poolConfig = new JedisPoolConfig();
    private static JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);

    public static void regist(String interfaceName, URL url) throws IOException, ClassNotFoundException {


        Jedis jedis = jedisPool.getResource();
        //AOF模式修改为每次写入时候保存，以保证redis数据库的完整性
        jedis.configSet("appendonly", "yes");
        jedis.configSet("appendfsync", "always");
        // 检查 Redis 中是否存在以 name 为键的数据
        byte[] storedBytes = jedis.get(interfaceName.getBytes());

        List<URL> urls;
        if (storedBytes != null) {
            // 如果存在，反序列化已有的 URL 列表
            urls = deserialize(storedBytes);
        } else {
            // 如果不存在，创建一个新的空列表
            urls = new ArrayList<>();
        }

        // 将新的 URL 添加到列表
        urls.add(url);

        // 序列化 URL 列表并存储到 Redis
        byte[] serializedURLs = serialize(urls);
        jedis.set(interfaceName.getBytes(), serializedURLs);

        jedis.close();

    }
    public static List<URL> get(String interfaceName) throws IOException, ClassNotFoundException {
        Jedis jedis = jedisPool.getResource();
        //AOF模式修改为每次写入时候保存，以保证redis数据库的完整性
        jedis.configSet("appendonly", "yes");
        jedis.configSet("appendfsync", "always");
        byte[] storedBytes = jedis.get(interfaceName.getBytes());
        List<URL> urls = deserialize(storedBytes);
        jedis.close();
        return urls;

    }


    // 序列化对象为字节数组
    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        return bos.toByteArray();
    }

    // 反序列化字节数组为对象
    public static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (T) ois.readObject();
    }


}
