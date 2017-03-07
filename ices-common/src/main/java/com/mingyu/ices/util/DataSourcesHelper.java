package com.mingyu.ices.util;


import com.dangdang.ddframe.rdb.sharding.api.rule.DataSourceRule;

/**
 * Created by ZwilL_TP_PC on 2016/5/11.
 * 分库辅助类
 */
public class DataSourcesHelper {

	public static Integer dbSourceSize = null;
	private static Object lock = new Object();

	/**
	 * 获取分库数
	 *
	 * @return
	 */
	public static int getDbSourceSize() {

		if (dbSourceSize == null) {
			synchronized (lock) {
				try {
					DataSourceRule dataSourceRule = SpringUtil.getBean(DataSourceRule.class);
					dbSourceSize = dataSourceRule.getDataSources().size();
				} catch (Exception ex) {
					System.out.println("查找不到  spring.xml dataSourceRule 节点");
					ex.printStackTrace();
					throw ex;
				}
			}
		}
		return dbSourceSize;
	}

	/**
	 * 计算分库
	 * @param paperId
	 * @return
	 */
	public static Integer getDb(String paperId) {
		return (Math.abs(paperId.hashCode())) % DataSourcesHelper.getDbSourceSize();
	}

	/**
	 * 获取分库数据库名
	 * @param paperId
	 * @return
	 */
	public static String getPaperDbName(String paperId) {
		return "zyj_exam_" + getDb(paperId);
	}
}
