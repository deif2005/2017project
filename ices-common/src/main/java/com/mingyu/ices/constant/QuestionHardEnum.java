package com.mingyu.ices.constant;

/**
 * Created by Administrator on 2016/7/11.
 */
public enum  QuestionHardEnum {
    ANY("难度不限","1"),
    HARD("难","2"),
    NORMAL("中","3"),
    EASY("易","4");

    private final String name;
    private final String value;

    private QuestionHardEnum(String name,String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
