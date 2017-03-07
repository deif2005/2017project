package com.mingyu.ices.redis;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by wdong on 15/7/24.
 */
public interface IRedisRepository<K,V>  {

    // Key
    public void del(final K key);
    public void del(final Collection<K> keys);
    public Boolean exists(final K key);
    public Boolean expire(final K key, final long timeout, final TimeUnit unit) ;
    public void expireAt(final K key, Date date);
    public Set<K> keys(final K pattern);
    public String type(final K key);
    public V get(final K key) ;
    public V getSet(final K key, final V value) ;
    public Long incr(final K key, final long delta) ;
    public void set(final K key, final V value) ;
    public void set(final K key, final V value, final long timeout, final TimeUnit unit);

    // Hash
    public void hDel(final K key, final Object... hKeys) ;
    public Boolean hExists(final K key, final K hKeys) ;
    public Map<K,V> hGet(final K key) ;
    public V hGet(final K key, final K hKey) ;
    public Set<K> hKeys(final K key) ;
    public Long hLen(final K key) ;
    public void hSet(final K key, final K hk, final V hv);
    public void hSet(final K key, final Map<K, V> map);
    public List<V> hVals(final K key) ;

    // List
    public V lIndex(final K key, final long index) ;
    public void lInsert(final K key, final long index, V value) ;
    public Long lLen(final K key) ;
    public V lPop(final K key) ;
    public V lPop(final K key, long timeout, TimeUnit unit) ;
    public Long lPush(final K key, final V value) ;
    public List<V> lRange(final K key, final long start, final long end) ;
    public Long lRem(final K key, final long index, final V value) ;
    public void lSet(final K key, final long index, final V value) ;
    public void ltrim(final K key, final long start, final long end) ;
    public Long rPush(final K key, final V value) ;
    public V rPop(final K key) ;

    // Set
    public Long sAdd(final K key, final V value) ;
    public Set<V> sDiff(final K key) ;
    public Set<V> sMembers(final K key) ;
    public Boolean sIsMember(final K key, final V value);
    public V sPop(final K key) ;
    public Long sRem(final K key, final V value) ;
    public Long sCard(final K key) ;

    // SortedSet
    public void zAdd(final K key, final V value, final double score) ;
    public Set<V> zRange(final K key, final long start, final long end) ;
    public Long zRem(final K key, final Object... values) ;
    public Long zCard(final K key) ;

}
