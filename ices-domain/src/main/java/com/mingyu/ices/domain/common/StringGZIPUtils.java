package com.mingyu.ices.domain.common;

/**
 * GZIP压缩工具
 * <p/>
 * 创建时间: 16/1/7<br/>
 *
 * @author xule
 * @since v0.0.1
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StringGZIPUtils {
    private static String encode = "utf-8";

    /**
     * 字符串压缩为字节数组
     * @param str
     * @param encoding
     * @return
     */
    public static byte[] compressToByte(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 字符串压缩为字节数组, 使用默认UTF-8编码
     * @param str
     * @return
     */
    public static byte[] compressToByte(String str) {
        return compressToByte(str, encode);
    }

    /**
     * 字节数组解压缩后返回字符串
     * @param b
     * @param encoding
     * @return
     */
    public static String uncompressToString(byte[] b, String encoding) {
        if (b == null || b.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(b);
        try {
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte[256];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toString(encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节数组解压缩后返回字符串, 使用默认UTF-8编码
     * @param b
     * @return
     */
    public static String uncompressToString(byte[] b) {
        return uncompressToString(b, encode);
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        StringGZIPUtils.encode = encode;
    }
}
