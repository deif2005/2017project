package com.mingyu.ices.zk.context;



import com.google.common.base.Strings;
import com.mingyu.ices.zk.util.ConfigLoader;
import com.mingyu.ices.zk.util.AbstractLifecycle;
import com.mingyu.ices.zk.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * server上下文实现类
 * <p/>
 * 创建时间: 14-8-6 下午1:19<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class CloudContextImpl extends AbstractLifecycle implements ICloudContext{
    public static final Logger logger = LoggerFactory.getLogger(CloudContextImpl.class);

    private volatile boolean isStart = false;
    public static final String DEPLOY_PATH_KEY = "deploypath";
    public static final String DEPLOY_FILE_KEY = "deployfile";
    //public static final String APP_MAINCONF_FILE = "/config/main-conf.properties";
    public static final String APP_METADATA_FILE = "/config/metadata.properties";
    public static final String APP_METADATA_FILE_NOHUB = "config/metadata.properties";

    public static final String APP_NAME = "name";
    public static final String APP_ZHNAME = "zh_name";
    public static final String APP_TYPE = "type";
    public static final String APP_OWNER = "owner";
    public static final String APP_OWNERCONTACT = "ownercontact";
    public static final String APP_DESCRIPTION = "description";
    public static final String APP_PORT = "port";
    public static final String APP_HTTP_PORT = "http_port";
    public static final String PRODUCT = "product";
    public static final String PRODUCT_CODE = "product_code";

    /** 应用名称 */
    private String applicationName;
    private String zhName;
    /** 应用类型 */
    private AppType appType;
    /** 系统owner */
    private String owner;
    /** 系统owner 联系方式 */
    private String ownerContact;
    private String description;
    /** dubbo端口，必须填写 */
    private int port;
    /** http端口,默认80 */
    private int httpPort = 80;
    /** 系统所属产品线 */
    private String product;
    /** 系统所属产品线代码 */
    private String productCode;
    /**
     * cluster node id
     * ip converter
     */
    private String id;


    /**
     * 通过左移位操作（<<）给每一段的数字加权
     * 第一段的权为2的24次方
     * 第二段的权为2的16次方
     * 第三段的权为2的8次方
     * 最后一段的权为1
     *
     * @param ip
     * @return int
     */
    public static Long ipToLong(String ip) {
        String[] ips = ip.split("\\.");
        return (Long.parseLong(ips[0]) << 24) + (Long.parseLong(ips[1]) << 16)
                + (Long.parseLong(ips[2]) << 8) + Long.parseLong(ips[3]);
    }

    /**
     * 初始化参数
     */
    @Override
    public void doStart(){
        String deploypath = ConfigLoader.getInstance().getProperty(DEPLOY_PATH_KEY);
        String deployfile = ConfigLoader.getInstance().getProperty(DEPLOY_FILE_KEY);

        String metaFile = null;
        boolean isFromClasspath = false;
        if(!Strings.isNullOrEmpty(deploypath)){//以deploypath优先
            metaFile = deploypath + APP_METADATA_FILE;
        } else if(!Strings.isNullOrEmpty(deployfile)){
            metaFile = deployfile;
        } else { //从classpath获取
            isFromClasspath = true;
        }

//        else {
//            logger.error("CloudContextImpl init error! deploypath and deployfile is none");
//            System.exit(-1);
//        }

        Properties properties = new Properties();
        try {
            if(!isFromClasspath) {
                properties.load(new FileInputStream(new File(metaFile)));
            } else {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream(APP_METADATA_FILE);
                if(is == null){
                    is = this.getClass().getClassLoader().getResourceAsStream(APP_METADATA_FILE_NOHUB);
                }
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                properties.load(isr);
            }

            this.applicationName = properties.getProperty(APP_NAME);
            this.id = ipToLong(NetUtil.getIpByHost(NetUtil.getLocalHost())).toString();
            this.appType = AppType.valueOf(properties.getProperty(APP_TYPE).toUpperCase());
            this.owner = properties.getProperty(APP_OWNER);
            this.ownerContact = properties.getProperty(APP_OWNERCONTACT);
            this.description = properties.getProperty(APP_DESCRIPTION);
            this.zhName = properties.getProperty(APP_ZHNAME);
            if(!Strings.isNullOrEmpty(properties.getProperty(APP_PORT))) {
                this.port = Integer.valueOf(properties.getProperty(APP_PORT));
            }
            if(!Strings.isNullOrEmpty(properties.getProperty(APP_HTTP_PORT))) {
                this.httpPort = Integer.valueOf(properties.getProperty(APP_HTTP_PORT));
            }
            this.product = properties.getProperty(PRODUCT);
            this.productCode = properties.getProperty(PRODUCT_CODE);
        } catch (IOException e) {
            logger.error("CloudContextImpl init error! load [{}] error", metaFile, e);
            System.exit(-1);
        }
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setAppType(AppType appType) {
        this.appType = appType;
    }

    public void setOwnerContact(String ownerContact) {
        this.ownerContact = ownerContact;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getApplicationZhName() {
        return zhName;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public String getAppType() {
        return appType.name();
    }

    @Override
    public String getOwnerContact() {
        return ownerContact;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getHttpPort() {
        return httpPort;
    }

    @Override
    public String getProduct() {
        return product;
    }

    @Override
    public String getProductCode() {
        return productCode;
    }

    @Override
    public void stop() {

    }
}
