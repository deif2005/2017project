package com.mingyu.ices.domain.po;

/**
 * Created by defi on 2016/7/1.
 */
public class SysQuestionType {
    private String id;
    private String name;         //题型名称
    private String code;         //题型名称
    private String type;         //题目类型（主观题，客观题）
    private String createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

}
