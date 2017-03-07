package com.mingyu.ices.zk.util;


/**
 * 生命周期抽象类
 * <p/>
 * 创建时间: 14-8-8 下午4:43<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public abstract class AbstractLifecycle implements ILifecycle {
    protected volatile boolean isStart = false;

    @Override
    public void start() {
        if(!isStart){
            doStart();
            isStart = true;
        }
    }

    @Override
    public boolean isStarted() {
        return isStart;
    }

    /**
     * 进行实际的启动操作
     */
    protected abstract void doStart();
}
