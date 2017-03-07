package com.mingyu.ices.util;

import java.util.Random;
import java.util.UUID;

/**
 * Created by aikuangyong on 2016/2/29.
 * 下面就是实现为数据库获取一个唯一的主键id的代码
 */
public class UUIDGenerator {

    final static char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
            'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
            'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z' };
    /**
     * 随机ID生成器，由数字、小写字母和大写字母组成
     *
     * @param size
     * @return
     */
    public static String generatorId(int size) {
        Random random = new Random();
        char[] cs = new char[size];
        for (int i = 0; i < cs.length; i++) {
            cs[i] = digits[random.nextInt(digits.length)];
        }
        return new String(cs);
    }

    public UUIDGenerator() {
    }
    /**
     * 获得一个UUID
     * @return String UUID
     */
    public static String getUUID(){
        /*return generatorId(16);*/
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    /**
     * 获得指定数目的UUID
     * @param number int 需要获得的UUID数量
     * @return String[] UUID数组
     */
    public static String[] getUUID(int number){
        if(number < 1){
            return null;
        }
        String[] ss = new String[number];
        for(int i=0;i<number;i++){
            ss[i] = getUUID();
        }
        return ss;
    }
}
