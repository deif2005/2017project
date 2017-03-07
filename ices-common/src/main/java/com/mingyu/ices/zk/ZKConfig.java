package com.mingyu.ices.zk;

import com.mingyu.ices.constant.FileConstants;
import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizException;
import com.mingyu.ices.util.DateUtil;
import org.apache.curator.framework.CuratorFramework;

import java.util.Date;
import java.util.UUID;

/**
 * Created by BIG on 2016/4/7.
 */
public class ZKConfig {

	// ZK的根目录
	static String rootPath = "/configs/ices/ices-zj";

	/**
	 * 获取ZK上的配置路径
	 * @param zk_path ZK的配置路径
	 * @return
	 */
	public static String getUploadPath(String zk_path) {
		zk_path = rootPath + zk_path;
		// 默认上传地址
		String uploadPath = null;

		// 判断 ZK是否有配置，如果没有，使用默认
		CuratorFramework zk = ZKClient.getClient();
		try {
			if (zk.checkExists().forPath(zk_path) != null) {
				byte[] data = zk.getData().forPath(zk_path);
				uploadPath = new String(data);
			} else {
				throw new BizException(WebConstants.ERROR,"读取zk配置失败，配置不存在：" + zk_path);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uploadPath;
	}

/*	*//**
	 * 获取静态资源展示地址
	 * @return 返回资源展示地址
	 * @return 例：http://192.168.10.42:8085/ices-static/upload
	 *//*
	public static String getStaticShowPath(){
		return getUploadPath("/static/showPath");
	}

	*//**
	 * 获取静态资源上传地址
	 * @return 例：d:/tomcat/ices-static
	 *//*
	public static String getStaticUploadBasePath(){
		return getUploadPath("/static/uploadPath");
	}

	*//**
	 * 获取试题资源上传地址
	 * @param fileType 文件类型
	 * @return 返回 根路径+试题资源路径
	 *//*
	public static String getQuestionUploadPath(String fileType){
		return ZKConfig.getUploadPath("/upload/question/resource/path").replaceAll("#date", DateUtil.getDateToString(new Date())).replaceAll("#fileType", fileType);
	}

	*//**
	 * 获取试题资源包上传地址
	 * @return
	 *//*
	public static String getQuestionResourceUploadPath(){
		return getUploadPath("/upload/question/resource/package/path");
	}

	*//**
	 * 获取试题资源包上传规则
	 * @return
	 *//*
	public static String getQuestionResourceUploadRule(){
		return getUploadPath("/upload/question/resource/package/rule");
	}

	*//**
	 * 获取试题内容xml保存地址
	 * @return 根路径+试题内容xml目录
	 *//*
	public static String getQuestionXmlSavePath(String subjectId){
		return getUploadPath("/upload/question/xmlPath").replaceAll("#subjectId",subjectId);
	}

	*//**
	 * 获取试卷导出临时地址/下载地址
	 * 跟据日期及uuid分文件夹，日期yyyy-MM-dd，以天为单位，标记每天导出的试卷，uuid区分每次导出的临时文件夹
	 * @return
	 *//*
	public static String getExportPaperTempPath(){
		return getUploadPath("/download/paper/path").replaceAll("#date", DateUtil.getDateToString(new Date()) + FileConstants.FILE_SEPARATOR+ UUID.randomUUID().toString());
}*/
}
