package com.mingyu.ices.zk.util;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * properties文件读取
 * <p/>
 * 创建时间: 14/10/29 上午9:27<br/>
 *
 * @author qyang
 * @since v0.0.1
 */
public class FileLoader {
    private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);

    /**
     * 涉及到支持 junit测试   文件路径会自动  进行  /PP/PP  和 PP/PP的匹配
     *
     * @param file  文件路径，传入需要带 /前缀
     * @return
     */
    public static Properties getFile(String file){
        Properties properties = new Properties();

        try {
            if(!Strings.isNullOrEmpty(file)) {//没有配置 从classloader获取
                InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
                if(is == null){
                    file = file.substring(1);
                    is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
                }
                properties.load(is);
            }
        } catch (IOException e) {
            logger.error("load properties {} error", file, e);
            System.exit(-1);
        }

        return properties;
    }
}
