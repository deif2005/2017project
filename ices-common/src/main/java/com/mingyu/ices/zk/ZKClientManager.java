package com.mingyu.ices.zk;
import org.apache.curator.framework.CuratorFramework;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * zk client管理类
 * <p/>
 * 创建时间: 15/6/23 上午11:41<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class ZKClientManager {
    private static ConcurrentMap<String, CuratorFramework> zkClientMap = new ConcurrentHashMap<String, CuratorFramework>();

    private ZKClientManager(){};

    public static CuratorFramework getClient(String ip){
        if(ip == null || ip.trim().length() == 0){
            throw new IllegalArgumentException("zk ip not null!");
        }

        synchronized (ip) {
            if (!zkClientMap.containsKey(ip)) {
                CuratorFramework client = ZKClient.create(ip);

                CuratorFramework oldClient = zkClientMap.putIfAbsent(ip, client);
                if (oldClient != null) {
                    //close old client
                    oldClient.close();
                }

                return client;
            }
        }
        return zkClientMap.get(ip);
    }
}
