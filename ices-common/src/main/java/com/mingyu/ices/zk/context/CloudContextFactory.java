package com.mingyu.ices.zk.context;


import com.mingyu.ices.zk.util.ILifecycle;

/**
 * ICloudContext 工厂类
 * <p/>
 * 创建时间: 14-8-6 下午1:24<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class CloudContextFactory {
    private static class CloudContextHolder{
        private static final ICloudContext instance = new CloudContextImpl();
    }

    public static ICloudContext getCloudContext(){
        ((ILifecycle) CloudContextHolder.instance).start();
        return CloudContextHolder.instance;
    }
}
