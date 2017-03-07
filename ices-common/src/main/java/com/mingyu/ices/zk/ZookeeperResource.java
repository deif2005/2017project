package com.mingyu.ices.zk;

import com.google.common.collect.Maps;
import com.mingyu.ices.zk.context.CloudContextFactory;
import com.mingyu.ices.zk.util.AESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.AbstractResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentMap;

/**
 * 来源于zookeeper的配置文件资源，该配置文件只做第一次装载，不做动态处理？
 * <p/>
 * 创建时间: 14-8-5 下午4:43<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class ZookeeperResource extends AbstractResource implements ApplicationContextAware, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(ZookeeperResource.class);

    public static final String URL_HEADER = "zk://";
    private static final String PATH_FORMAT = "/startconfigs/%s/config";
    /**
     * 多产品线支持 2015-08-19 add
     */
    private static final String CLOUD_PATH_FORMAT = "/startconfigs/%s/%s/config";
    private String path = String.format(PATH_FORMAT, CloudContextFactory.getCloudContext().getApplicationName());
    private String cloud_path = String.format(CLOUD_PATH_FORMAT, CloudContextFactory.getCloudContext().getProductCode(), CloudContextFactory.getCloudContext().getApplicationName());
    ConcurrentMap<String, Object> recoverDataCache = Maps.newConcurrentMap();

    AbstractApplicationContext ctx;

    @Override
    public boolean exists() {
        try {
            return null != ZKClient.getClient().checkExists().forPath("");
        } catch (Exception e) {
            log.error("Falied to detect the config in zoo keeper.", e);
            return false;
        }
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public URL getURL() throws IOException {
        return new URL(URL_HEADER + path);
    }

    @Override
    public String getFilename() throws IllegalStateException {
        return path;
    }

    @Override
    public String getDescription() {
        return "Zookeeper resouce at '" + URL_HEADER + path;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        byte[] data = null;
        try {
            //data = ZKClient.getClient().getData().forPath(path);
            //check cloud path exists
            if (ZKClient.getClient().checkExists().forPath(cloud_path) != null) { // cloud mode, NODE: /startconfigs/%s/%s/config
                data = ZKClient.getClient().getData().forPath(cloud_path);
            } else if (ZKClient.getClient().checkExists().forPath(path) != null) {// cloud mode, NODE: /startconfigs/%s/config
                data = ZKClient.getClient().getData().forPath(path);
            } else { //cloud mode
                log.error("{} and {} none exists", cloud_path, path);
                System.exit(-1);
            }
        } catch (Exception e) {
            log.error("zk server error", e);
            // 读取cmc配置失败时加载本地备份的配置
            try {
                data = ZKRecoverUtil.loadRecoverData(cloud_path);
            } catch (Exception e1) {
                log.error("zk server cloud_path error", e);
                data = ZKRecoverUtil.loadRecoverData(path);
            }

        }

        // 备份cmc配置到本地
        ZKRecoverUtil.doRecover(data, path, recoverDataCache);
        ZKRecoverUtil.doRecover(data, cloud_path, recoverDataCache);

        log.debug("init get startconfig data {}", new String(data));
        //  add by qyang  2015.10.21
        if (EncryptUtil.isEncrypt(data)) {
            byte[] pureData = new byte[data.length - 2];
            System.arraycopy(data, 2, pureData, 0, data.length - 2);
            String originStr = null;
            try {
                originStr = AESUtil.aesDecrypt(new String(pureData), EncryptUtil.encryptKey);
            } catch (Exception e) {
                log.error("decrypt error", e);
                System.exit(-1);
            }
            return new ByteArrayInputStream(originStr.getBytes());
        } else {
            return new ByteArrayInputStream(data);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.ctx = (AbstractApplicationContext) ctx;
    }

    @Override
    public void destroy() throws Exception {
        log.info("Destory Zookeeper Resouce.");
        //TODO destory zk connection
//        if (executor != null) {
//            log.info("Close connection to Zookeeper Server.");
//            try {
//                //executor.getZk().close();
//                log.info("Connection to Zookeeper Server closed.");
//            } catch (Exception e) {
//                log.error("Error found when close zookeeper connection.", e);
//            }
//        }

    }

}