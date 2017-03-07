package com.mingyu.ices.domain.common;


import java.io.Serializable;

/**
 * http服务返回的数据包装类   使用泛型
 * Created by qyang on 14-6-17.
 */
public class ResponseT<T> implements Serializable {
    /** 返回的响应码 为空，说明是正常返回*/
    private String rtnCode;
    /** 错误信息 有业务异常的时候，来源于BizException；否则网关出错（系统异常），使用通用异常 */
    private String msg;
    /** 错误堆栈信息，便于排查问题   正常是调试模式下该字段才返回信息 */
    private String developMsg;
    /** 错误说明url 有业务异常的时候，来源于BizException；否则网关出错（系统异常），使用通用异常 */
    private String uri;
    private long ts = System.currentTimeMillis();
    /** 返回的业务 有业务异常的时候，来源于BizException；否则网关出错（系统异常），使用通用异常 */
    private T bizData;

    /** data的处理方式 */
    private StyleEnum style;
    private String styledData;

    public ResponseT(){
        this.style = StyleEnum.PLAIN;
    }

    public ResponseT(StyleEnum style) {
        this.style = style;
    }

    public ResponseT(RtnCodeEnum rtnCode){
        this.rtnCode = rtnCode.getValue();
        this.style = StyleEnum.PLAIN;
    }

    public ResponseT(BizException bizException) {
        this(bizException, false);
    }

    public ResponseT(BizException bizException, boolean isDebug) {
        this.rtnCode = bizException.getRtnCode();
        this.msg = bizException.getMsg();
        if(isDebug) {
            this.developMsg = bizException.getDevelopMsg();
        }
        this.uri = bizException.getUri();
        this.style = StyleEnum.PLAIN;
    }

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public StyleEnum getStyle() {
        return style;
    }

    public void setStyle(StyleEnum style) {
        this.style = style;
    }

    public T getBizData() {
        if(StyleEnum.PLAIN.equals(style)){
            return bizData;
        }/*else {
            if (bizData != null && !Objects.equal(bizData, "")) {
                return bizData;
            } else {
                //unwrapper data with styled data
                String jsonData;
                if (StyleEnum.GZIP.equals(style)) {
                    jsonData = StringGZIPUtils.uncompressToString(ByteUtils.HexString2Bytes(styledData));
                    if (jsonData != null) {
                        bizData = (T) JSON.parse(jsonData);
                        return bizData;
                    }
                } else if (StyleEnum.AES.equals(style)) {
                    try {
                        jsonData = AES256Utils.decrypt2str(ByteUtils.HexString2Bytes(styledData));
                        if (jsonData != null) {
                            bizData = (T) JSON.parse(jsonData);
                            styledData = null;
                            return bizData;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }*/
        return null;
    }

    public void setBizData(T bizData) {
        if(StyleEnum.PLAIN.equals(style)){
            this.bizData = bizData;
        }/*else {
            if(bizData == null || Objects.equal(bizData, "")){
                this.bizData = bizData;
            }else {
                //wrapper data with style
                String jsonData = JSON.toJSONString(bizData);
                String hexData = null;
                if(StyleEnum.GZIP.equals(style)){
                    hexData = ByteUtils.Bytes2HexString(StringGZIPUtils.compressToByte(jsonData));
                }else if(StyleEnum.AES.equals(style)){
                    try {
                        hexData = ByteUtils.Bytes2HexString(AES256Utils.encrypt(jsonData));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(!Strings.isNullOrEmpty(hexData)){
                    this.styledData = hexData;
                    this.bizData = null;
                }
            }
        }*/
    }

    public String getDevelopMsg() {
        return developMsg;
    }

    public String getStyledData() {
        return styledData;
    }

    public void setStyledData(String styledData) {
        this.styledData = styledData;
    }

    /**
     * @deprecated 应用不要使用，仅供fastjson转换用
     * @param developMsg
     */
    public void setDevelopMsg(String developMsg) {
        this.developMsg = developMsg;
    }

    /**
     * @deprecated 应用不要使用，仅供fastjson转换用
     * @param ts
     */
    public void setTs(long ts) {
        this.ts = ts;
    }

    public long getTs() {
        return ts;
    }
}
