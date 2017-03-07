package com.mingyu.ices.xls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: XLS表格对象
 * @version V1.0
 * @date Oct 29, 2011
 */
public class XLSSheet {

	private String name = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()); // sheet名

	private String desc; // 表格描述

	private String[] titles = {}; // 列头
	
	private short[] cellWeights = {}; //列宽,某列设为0则采用自动列宽
	
	private short heightInPoints=40;//设置标题行高度

	private List<Object[]> data; // 列表数据,每个Object[]代表一行
	
	private int cellSize = 0; // 总列数
	
	private boolean[] isMoneys = {};//标识各列是否金额:true-是,false-否
	
	public String toString() {
		return new StringBuilder()
				.append("{SheetName:").append(name)
				.append(",Description:").append(desc).append("}").toString();
	}
	
	/**
	 * 最大列索引
	 */
	public int maxCell(){
		return cellSize-1;
	}
	
	/**
	 * 是否已设置表格描述
	 */
	public boolean hasDesc(){
		return desc!=null && !"".equals(desc);
	}
	
	/**
	 * 是否已设置列头
	 */
	public boolean hasTitles(){
		return titles!=null && titles.length>0;
	}
	
	/**
	 * 是否已设置列宽
	 */
	public boolean hasCellWeights(){
		return cellWeights!=null && cellWeights.length>0;
	}
	
	/**
	 * 数据行偏移值(首行数据位置)
	 */
	public int getPosition(){
		int p = 0;
		if (hasDesc()) {
			p++;
		}
		if (hasTitles()) {
			p++;
		}
		return p;
	}

	/**
	 * 获取总列数
	 */
	public int getCellSize() {
		return cellSize;
	}
	
	/**
	 * 获取Sheet名
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 设置Sheet名
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取表格标题(描述)
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * 设置表格标题(描述)
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 设置表格列头
	 */
	public String[] getTitles() {
		return titles;
	}

	/**
	 * 获取表格列头
	 */
	public void setTitles(String[] titles) {
		this.titles = titles;
		
		//设置总列数
		if (titles.length > cellSize) {
			this.cellSize = titles.length;
		}
	}

	/**
	 * 获取列宽
	 */
	public short[] getCellWeights() {
		return cellWeights;
	}

	/**
	 * 设置列宽,覆盖原有设置(宽度单位为像素)
	 */
	public void setCellWeights(short[] cellWeights) {
		if (cellWeights != null) {
			this.cellWeights = cellWeights;
		}
	}
	
	/**
	 * 设置某列列宽,不影响其它列(宽度单位为像素)
	 * @param cellIndex 列索引(首列为1)
	 * @param weight 列宽(像素)
	 */
	public void setCellWeight(int cellIndex, short weight){
		if (cellIndex>cellWeights.length) {
			short[] temp = new short[cellIndex];
			for (int i = 0; i < cellWeights.length; i++) {
				temp[i] = cellWeights[i];
			}
			cellWeights = temp;
		} 
		cellWeights[cellIndex-1] = weight;
	}
	
	/**
	 * 获取内容(数据)
	 */
	public List<Object[]> getData() {
		return data;
	}
	
	/**
	 * 设置内容(数据)
	 */
	public void setData(List<Object[]> data) {
		this.data = data;
		
		//设置总列数
		for (Object[] row : data) {
			if (row.length > cellSize) {
				this.cellSize = row.length;
			}
		}
	}

	public boolean[] getIsMoneys() {
		return isMoneys;
	}

	public void setIsMoneys(boolean[] isMoneys) {
		this.isMoneys = isMoneys;
	}
	
	/**
	 * 判断某列存放的数据是否为金额
	 * @param cellNum 列索引(首列为0)
	 */
	public boolean isMoney(int cellNum){
		boolean isMoney = false;
		
		if (isMoneys.length>cellNum) {
			isMoney = isMoneys[cellNum];
		}
		
		return isMoney;
	}
	
	/**
	 * 指定存放金额数据的列,覆盖原有设置<BR>
	 * 如指定第1,3,4列存放金额数据,setMoneyCells(1,3,4);
	 * @param cellIndexs 列索引(首列为1)
	 */
	public void setMoneyCells(int... cellIndexs){
		int max = max(cellIndexs);
		isMoneys = new boolean[max>cellSize?max:cellSize];
		for (int index : cellIndexs) {
			isMoneys[index-1] = true;
		}
	}
	
	/**
	 * 获取整型数组中的最大值
	 * @param cellNums
	 * @return
	 */
	private static int max(int... cellNums){
		int max = 0;
		for (int num : cellNums) {
			if (num > max) {
				max = num;
			}
		}
		return max;
	}

	public short getHeightInPoints() {
		return heightInPoints;
	}

	public void setHeightInPoints(short heightInPoints) {
		this.heightInPoints = heightInPoints;
	}
	
	
	
}
