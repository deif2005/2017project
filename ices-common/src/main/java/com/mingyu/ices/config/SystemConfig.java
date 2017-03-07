package com.mingyu.ices.config;

import com.mingyu.ices.constant.FileConstants;
import com.mingyu.ices.util.DateUtil;

import java.util.Date;
import java.util.UUID;

/**
 * SystemConf
 * 读取系统配置类
 * @author yuhao
 * @date 2016/10/19 20:26
 */
public class SystemConfig {
    /**
     * 获取静态资源展示地址
     * @return 返回资源展示地址
     * @return 例：http://192.168.10.42:8085/ices-static/upload
     */
    public static String getStaticShowPath(){
        return PropertiesConfig.getConfigString("/static/showPath");
    }

    /**
     * 获取静态资源上传地址
     * @return 例：d:/tomcat/ices-static
     */
    public static String getStaticUploadBasePath(){
        return PropertiesConfig.getConfigString("/static/uploadPath");
    }

    /**
     * 获取试题资源上传地址
     * @param fileType 文件类型
     * @return 返回 根路径+试题资源路径
     */
    public static String getQuestionUploadPath(String fileType){
        return PropertiesConfig.getConfigString("/upload/question/resource/path").replaceAll("#date", DateUtil.getDateToString(new Date())).replaceAll("#fileType", fileType);
    }

    /**
     * 获取试题资源包上传地址
     * @return
     */
    public static String getQuestionResourceUploadPath(){
        return PropertiesConfig.getConfigString("/upload/question/resource/package/path");
    }

    /**
     * 获取试题资源包上传规则
     * @return
     */
    public static String getQuestionResourceUploadRule(){
        return PropertiesConfig.getConfigString("/upload/question/resource/package/rule");
    }

    /**
     * 获取试题内容xml保存地址
     * @return 根路径+试题内容xml目录
     */
    public static String getQuestionXmlSavePath(String subjectId){
        return PropertiesConfig.getConfigString("/upload/question/xmlPath").replaceAll("#subjectId",subjectId);
    }

    /**
     * 获取试卷导出临时地址/下载地址
     * 跟据日期及uuid分文件夹，日期yyyy-MM-dd，以天为单位，标记每天导出的试卷，uuid区分每次导出的临时文件夹
     * @return
     */
    public static String getExportPaperTempPath(){
        return PropertiesConfig.getConfigString("/download/paper/path").replaceAll("#date", DateUtil.getDateToString(new Date()) + FileConstants.FILE_SEPARATOR+ UUID.randomUUID().toString());
    }
}
