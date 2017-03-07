package com.mingyu.ices.zk;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 * 监听器管理及调度
 * <p/>
 * 创建时间: 14-8-7 下午3:54<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class ChangeListenerManager {
    /** 注册的监听器集合 <group_dataid(需要确保group和dataid没有“_”字符), value> 目前只支持key有一个监听器 TODO 后续可以考虑有多个（有场景再说） */
    private ConcurrentMap<String, IChangeListener> changeListenerMap = Maps.newConcurrentMap();
    public static final String SPLIT = "_";

    private static class ChangeListenerManagerHolder{
        private static ChangeListenerManager instance = new ChangeListenerManager();
    }

    private ChangeListenerManager(){}

    public static ChangeListenerManager getInstance(){
        return ChangeListenerManagerHolder.instance;
    }
    public void addListener(String group, String dataId, IChangeListener listener){
        Preconditions.checkNotNull(listener);
        String key = group + SPLIT + dataId;
        changeListenerMap.put(key, listener);
    }
}
