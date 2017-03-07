package com.mingyu.ices.zk;


import com.mingyu.ices.zk.util.AESUtil;

/**
 * AES加密工具类，仅用于 配置中心的启动配置串
 * <p/>
 * 创建时间: 15/10/21 下午5:31<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class EncryptUtil {
    private static final byte[] encryptTag = new byte[]{83, 1};
    private static final char oneChar = 1;
    public static final String encryptStr = "S" + oneChar;
    public static final String encryptKey = "i love thinkoy, and i love change";

    /**
     *
     * @param datas 数据串
     * @return true  加密的串；  false 没有加密的串
     */
    public static boolean isEncrypt(byte[] datas){
        if(datas[0] == encryptTag[0] && datas[1] == encryptTag[1]){
            return true;
        }

        return false;
    }

    /**
     * 按照 加密规则 加密
     * @param cryptStr
     * @return  返回加密后的结果
     * @throws Exception
     */
    public static String encrypt(String cryptStr) throws Exception {
        return EncryptUtil.encryptStr + AESUtil.aesEncrypt(cryptStr, EncryptUtil.encryptKey);
    }

    /**
     * 解压加密串
     * @param encryptStr
     * @return
     * @throws Exception
     */
    public static String decrypt(String encryptStr) throws Exception {
        String realEncryptStr = encryptStr.substring(2);

        return AESUtil.aesDecrypt(realEncryptStr, encryptKey);
    }
}
