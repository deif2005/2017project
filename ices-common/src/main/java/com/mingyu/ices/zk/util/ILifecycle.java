package com.mingyu.ices.zk.util;

/**
 * 生命周期管理接口
 * <p/>
 * 创建时间: 14-8-8 下午4:37<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public interface ILifecycle {
    void start();
    void stop();
    boolean isStarted();
}
