package com.mingyu.ices.zk;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 * DynConfigClient 工厂类
 * <p/>
 * 创建时间: 14-8-8 下午3:45<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class DynConfigClientFactory {
    private static class DynConfigClientHolder{
        private static DynConfigClient instance = new DynConfigClient();
    }

    private DynConfigClientFactory(){}

    private static ConcurrentMap<String, DynConfigClient> dynConfigClientMap = Maps.newConcurrentMap();

    /**
     * 获取DynConfigClient实例(单例)
     * @return
     */
    public static DynConfigClient getClient(){
        DynConfigClientHolder.instance.init();
        return  DynConfigClientHolder.instance;
    }

    public static DynConfigClient getClient(final String zkIp){
        synchronized (zkIp){
            if(dynConfigClientMap.get(zkIp) == null) {
                DynConfigClient client = new DynConfigClient(zkIp);
                client.init(true);
                dynConfigClientMap.put(zkIp, client);
            }

            return dynConfigClientMap.get(zkIp);
        }
    }
}
