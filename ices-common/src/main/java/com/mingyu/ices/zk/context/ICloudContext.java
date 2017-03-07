package com.mingyu.ices.zk.context;

/**
 * 被管理server上下文
 * <p/>
 * 创建时间: 14-8-6 上午11:11<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public interface ICloudContext {
    String getApplicationName();

    String getId();

    String getApplicationZhName();

    String getAppType();

    String getOwner();

    String getOwnerContact();

    String getDescription();

    int getPort();

    int getHttpPort();

    /** 产品中文名称 */
    String getProduct();

    /** 产品编码 */
    String getProductCode();
}
