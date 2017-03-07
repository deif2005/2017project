package com.mingyu.ices.config;

import java.io.*;
import java.util.Properties;

/**
 * properties文件配置类
 * @author yuhao
 * @date 2014年8月27日
 */
public class PropertiesConfig {

	/** 系统加载初始化properties对象 */
	private static Properties properties = null;

	/** 系统加载时的配置文件路径 */
	private static final String propertiesPath = PropertiesConfig.class.getClassLoader().getResource("config/system-conf.properties").getFile();

	/**
	 * 系统启动初始化加载配置文件
	 */
	static{
		try {
			properties = new Properties();
			InputStream in = new BufferedInputStream(new FileInputStream(propertiesPath));
			properties.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("加载properties文件出错，退出系统!");
			System.exit(-1);
		}
	}

	/**
	 * 跟据key读取properties文件value
	 * @param key
	 * @return
	 */
	public static String getConfigString(String key) {
		return properties.getProperty(key);
	}

	/**
	 * 写入/修改properties文件属性
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean setConfigString(String key,String value){
		try {
			OutputStream out = new FileOutputStream(propertiesPath);
			properties.setProperty(key, value);
			properties.store(out, null);
			out.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 跟据路径读取properties文件value
	 * @param filePath
	 * @param key
	 * @return
	 */
	public static String getConfigStringByKey(String filePath, String key) {
		Properties pps = new Properties();
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			pps.load(in);
			return pps.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 跟据路径写入properties文件
	 * @param filePath
	 * @param pKey
	 * @param pValue
	 * @throws java.io.IOException
	 */
	public static void setConfigString(String filePath, String pKey,String pValue) throws IOException {
		Properties pps = new Properties();
		InputStream in = new FileInputStream(filePath);
		// 从输入流中读取属性列表（键和元素对）
		pps.load(in);
		// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
		// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
		OutputStream out = new FileOutputStream(filePath);
		pps.setProperty(pKey, pValue);
		// 以适合使用 load 方法加载到 Properties 表中的格式，
		// 将此 Properties 表中的属性列表（键和元素对）写入输出流
		pps.store(out, "Update " + pKey + " name");
	}
}
