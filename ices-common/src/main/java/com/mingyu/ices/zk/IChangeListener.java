package com.mingyu.ices.zk;


import java.util.concurrent.Executor;

/**
 * 数据改变监听器
 * <p/>
 * 创建时间: 14-8-5 下午11:06<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public interface IChangeListener {
    /**
     * 返回线程池执行器
     * @return
     */
    public Executor getExecutor();


    /**
     * 接收到配置项文件处理
     * @param configuration
     */
    public void receiveConfigInfo(final Configuration configuration);
}
