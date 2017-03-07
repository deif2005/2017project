package com.mingyu.ices.util;

import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 廖大剑
 * @version V1.0
 * @Description:  redis 工具类
 * @Company: 广东全通教育股份有限公司
 * @date 2015/9/7
 */
public class RedisUtil {
    //服务端Ip  ,通过zk配置
    private String redisServerIP = "127.0.0.1";
    //服务端端口，通过zk配置
    private int redisServerPort = 6379;

    static Map<String,Object> temp = new HashMap<>();//临时用，连上redis后请将这个删除

    private  static  Jedis jedis = null;
    private static RedisUtil redisUtil = null;

    private RedisUtil(){
        //jedis = new Jedis(redisServerIP , redisServerPort);
    }

    public static synchronized RedisUtil getInstance(){
        if(redisUtil == null) redisUtil = new RedisUtil();
        return redisUtil;
    }

    /**
     * 集合 sets ，添加一个或者多个元素到集合(set)里
     * @param key
     * @param value
     */
    public static void sadd(String key , String value){
        //jedis.sadd(key , value);
        temp.put(key , value);
    }

    /**
     * 集合 sets ，获取集合里面的元素数量
     * @param key
     */
    public static long scard(String key){
        //return jedis.scard(key);
        return temp.size();
    }
    /**
     * 列表 lists 从队列的左边入队一个或多个元素
     * @param key
     * @param value  数组
     */
    public static void lpush(String key , String ... value){
        //jedis.lpush(key , value);
        temp.put(key , value);
    }


    public static void main(String[] str){
        RedisUtil ru = RedisUtil.getInstance();
        ru.sadd("jason1","测试5");
        ru.lpush("jason2","测试2","测试3");
        System.out.println("完成");
    }

}
