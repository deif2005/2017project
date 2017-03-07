package com.mingyu.ices.domain.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 分页对象
 * Created with IntelliJ IDEA.
 * User: 龙腾跃
 * Date: 2016/3/28
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class BizDataPage<T> implements Serializable {
    /** 当前页 */
    private int page=1;
    /** 总页数 */
    private int total=0;
    /** 分页数 */
    private int pagesize=10;
    /** 总记录数 */
    private int records=0;
    /** 具体数据 */
    private List<T> rows;
    /**
     * 查询条件
     */
    private  T objectData;

    /** 开始行*/
    private int offset;

    private Map<String,Object> conditions = new HashMap<String,Object>();

    public Map<String, Object> getConditions() {
        return conditions;
    }

    public void setConditions(Map<String, Object> conditions) {
        this.conditions = conditions;
    }

    /** 返回的用户自定义数据 例如总计行？ */
    private Map<List,Object> userdata;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        total = records / pagesize;
        int mod = records % pagesize;
        if (mod > 0) {
            total = total + 1;
        }
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Map<List, Object> getUserdata() {
        return userdata;
    }

    public void setUserdata(Map<List, Object> userdata) {
        this.userdata = userdata;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public int getOffset() {
        if(page == 0){
            page  = 1;
        }
        offset = (page - 1) * pagesize;
        return offset;
    }

    public T getObjectData() {
        return objectData;
    }

    public void setObjectData(T objectData) {
        this.objectData = objectData;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
