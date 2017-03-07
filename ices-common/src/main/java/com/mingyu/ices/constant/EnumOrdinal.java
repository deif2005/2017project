package com.mingyu.ices.constant;

/**
 * Created by defi on 2016/7/7.
 * 用途；用来对应取值序号常量值，以便直观了解取值的具体内容
 */
public enum EnumOrdinal {
    //对应status状态：1正常，2删除，3停用或其它'
    normal(1), delete(2), other(3){
        @Override
        public boolean isRest() {
            return true;
        }
    },
    //对应题型导入excel表的列名:1题型名称，2题型备注（主客观类型）
    QuestionTypeName(1),QuestionTypeRemark(2) {
        @Override
        public boolean isRest() {
            return true;
        }
    },
    //审核状态:1未审核，2已审核
    statusUnchecked(1),statusChecked(2){
        @Override
        public boolean isRest() {return true; }
    };


    private int value;

    private EnumOrdinal(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public boolean isRest() {
        return false;
    }
}

