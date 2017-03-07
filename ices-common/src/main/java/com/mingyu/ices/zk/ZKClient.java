package com.mingyu.ices.zk;




import com.mingyu.ices.zk.util.AbstractLifecycle;
import com.mingyu.ices.zk.util.ConfigLoader;
import com.mingyu.ices.zk.util.NetUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;

/**
 * zk客户端
 * <p/>
 * 创建时间: 14-8-6 下午1:52<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class ZKClient extends AbstractLifecycle {
    public static final Logger logger = LoggerFactory.getLogger(ZKClient.class);

    /** 云管理中心域名 */
    public static final String DEFAULT_DOMAIN_NAME = "ices.zk.mingyu.com";

    private volatile static CuratorFramework zkClient = null;

    private ZKClient(){};

    @Override
    protected void doStart() {
        isStart = true;
        String ip = null;
        try {
            ip = NetUtil.getIpByDomain(DEFAULT_DOMAIN_NAME);
        } catch (UnknownHostException e) {
            logger.error("getIpByDomain error!", e);
            System.exit(-1);
        }
        String url = ip + ":" + ConfigLoader.getInstance().getProperty("zk.port");
        zkClient = CuratorFrameworkFactory.newClient(url, new ExponentialBackoffRetry(1000, 3));
        //innerRegisterListeners(zkClient);

        zkClient.start();
        logger.warn("ZKClient start success!");
    }

    /**
     * 根据ip获取zk client, 可以直接调用该方法，但不建议 请使用 ZKClientManager 调用
     * @param ip
     * @return
     */
    public static CuratorFramework create(String ip){
        logger.warn(" start conn zk server {} ", ip);

        CuratorFramework newClient = null;
        synchronized (ip){
            String url = ip + ":" + ConfigLoader.getInstance().getProperty("zk.port");
            newClient = CuratorFrameworkFactory.newClient(url, new ExponentialBackoffRetry(1000, 3));
            //innerRegisterListeners(zkClient);

            newClient.start();
        }

        logger.warn("  conn zk server {} success!", ip);
        return newClient;
    }

    @Override
    public void stop() {
        if(zkClient != null) {
            zkClient.close();
        }
        //throw new RuntimeException("un implemented");
    }

    private static class ZKClientHolder{
        private static final ZKClient instance = new ZKClient();
    }

    /**
     * 获取zk客户端实例（单例）
     * @return
     */
    public static CuratorFramework getClient(){
        //初始化client
        ZKClientHolder.instance.start();
        return zkClient;
    }
}
