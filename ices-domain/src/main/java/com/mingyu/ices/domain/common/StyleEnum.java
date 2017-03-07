/*
 * Copyright (c) 2013-2014, thinkjoy Inc. All Rights Reserved.
 * 
 * Project Name: tecc-ms-service
 * $Id: ErrorCodeType.java 2014年4月16日 下午6:51:07 $ 
 */
package com.mingyu.ices.domain.common;


/**
 * RequestT中Style类型枚举类
 * <p/>
 * 创建时间: 2016年1月7日 下午3:50:07 <br/>
 *
 * @author xule
 * @version
 * @since v0.0.1
 */
public enum StyleEnum {

    PLAIN("plain", "普通明文"),
    GZIP("gzip", "gzip压缩"),
    AES("aes", "AES加密");

    private String code;
    private String desc;

    private StyleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static StyleEnum codeOf(String code) {
        if(code == null ){
            return null;
        }
        for (StyleEnum rtnCodeEnum : StyleEnum.values()) {
            if (code.equalsIgnoreCase(rtnCodeEnum.name())) {
                return rtnCodeEnum;
            }
        }
        return null;
    }

    public boolean equals(StyleEnum style){
        return style != null && style.getCode().equals(this.getCode());
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    @Override
    public String toString() {
        return this.getCode();
    }
}
