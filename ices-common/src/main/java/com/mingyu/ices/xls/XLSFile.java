package com.mingyu.ices.xls;

import com.mingyu.ices.xls.*;
import com.mingyu.ices.xls.XLSSheet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: XLS文件对象
 * @version V1.0
 * @date Oct 29, 2011
 */
public class XLSFile {

	public String CONTENT_TYPE = "application/ms-excel;charset=UTF-8";

	private String fileName = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()); // 文件名

	private List<XLSSheet> sheets = new ArrayList<XLSSheet>(); //sheets集合

	private String dateFmt = "yyyy-MM-dd HH:mm:ss"; //日期时间显示格式
	private String moneyFmt = "#,##0.00"; //金额显示格式

	/**
	 * 添加一个XLS表格
	 * @param xlsSheet XLS表格对象,请参考{@link XLSSheet}
	 */
	public void addXlsSheet(XLSSheet xlsSheet){
		this.sheets.add(xlsSheet);
	}

	/**
	 * 获取文件名
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取XLS表格集合,请参考{@link XLSSheet}
	 */
	public List<XLSSheet> getSheets() {
		return sheets;
	}

	/**
	 * 添加XLS表格集合
	 * @param sheets XLS表格对象集合,请参考{@link XLSSheet}
	 */
	public void setSheets(List<XLSSheet> sheets) {
		if (sheets != null) {
			this.sheets = sheets;
		}
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		
		str.append("{FileName:"+fileName);
		str.append(",XLSSheet:[");
		
		if (!sheets.isEmpty()) {
			str.append(sheets.get(0).getName());
			for (int i = 1; i < sheets.size(); i++) {
				str.append(",").append(sheets.get(0).getName());
			}
		}
		
		str.append("]}");
		
		return str.toString();
	}

	/**
	 * 获取日期时间格式
	 */
	public String getDateFmt() {
		return dateFmt;
	}

	/**
	 * 设置日期时间格式,<BR>
	 * <strong>该设置对文档中所有java.util.Date类型的数据有效,默认为"yyyy-MM-dd"</strong>
	 * @param dateFmt 格式,如"yyyy-MM-dd HH:mm","MM月dd日"等
	 */
	public void setDateFmt(String dateFmt) {
		if (null != dateFmt && !"".equals(dateFmt)) {
			this.dateFmt = dateFmt;
		}
	}

	/**
	 * 获取金额显示格式
	 */
	public String getMoneyFmt() {
		return moneyFmt;
	}

	/**
	 * 设置金额显示格式<BR>
	 * <strong>对显示指定为金额类型的列有效,默认为"0.00"</strong><BR>
	 * 设置列为金额类型方法:<BR>
	 * 1.XLSSheet.setMoneyCells(int... cellNums);<BR>
	 * 2.XLSSheet.setIsMoneyArr(boolean[] isMoneyArr).
	 * @param moneyFmt 格式,如"0.00","#,##0.00"等
	 */
	public void setMoneyFmt(String moneyFmt) {
		if (null != moneyFmt && !"".equals(moneyFmt)) {
			this.moneyFmt = moneyFmt;
		}
	}

}
