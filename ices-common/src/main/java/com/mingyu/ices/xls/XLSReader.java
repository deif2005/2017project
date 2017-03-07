package com.mingyu.ices.xls;

//import cn.qtone.zyj.util.DateUtil;
//import cn.thinkjoy.common.exception.BizException;
//import cn.thinkjoy.common.utils.RtnCodeEnum;
import com.mingyu.ices.constant.WebConstants;
import com.mingyu.ices.domain.common.BizException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * @Description: XLS文件读取工具类
 */
public class XLSReader {
	static Logger logger = Logger.getLogger(XLSReader.class);
	/**
	 * 日期和数值
	 */
	public static int CELL_TYPE_NUMERIC = HSSFCell.CELL_TYPE_NUMERIC;
	/**
	 * 字符串
	 */
	public static int CELL_TYPE_STRING = HSSFCell.CELL_TYPE_STRING;
	/**
	 * 手机号码类型
	 */
	public static final int CELL_TYPE_PHONE = 110;

	// log 提示第几行有问题
	public static final String strRowFormat = "第%d行";


	private String[][] data;
	private int rowNum = 0;
	private int cellNum = 0;
	private boolean flag = true; //文档解析是否成功;

	/**
	 * @param realPath  文件绝对路径 request.getSession().getServletContext().getRealPath("/文件")或ServletActionContext.getServletContext().getRealPath("/文件")
	 * @param maxCell   最大列数
	 * @param startRow  读取数据起始行数，从第几行开始读,从0开始算
	 * @param cellTypes 数据类型
	 */
	public XLSReader(String realPath, int maxCell, int startRow, int... cellTypes) {
		try {
			InputStream is = new FileInputStream(realPath);
			String suffix = getSuffix(realPath);
			Workbook wb = null;
			Sheet sheet = null;
			if (suffix.equalsIgnoreCase("xls")) {
				wb = new HSSFWorkbook(is);
				sheet = (HSSFSheet) wb.getSheetAt(0);
			} else if (suffix.equalsIgnoreCase("xlsx")) {
				wb = new XSSFWorkbook(is);
				sheet = (XSSFSheet) wb.getSheetAt(0);
			}
			if (wb == null) return;
			xlsData(sheet, maxCell, startRow, cellTypes);
		} catch (Exception e) {
			logger.error("导入文件" + realPath + "出错：" + e.getMessage(), e);
			flag = false;
			throw new BizException(WebConstants.REQUEST_PARAMETER_ERROR, e.getMessage());
		}
	}


	public String[][] getData() {
		return data;
	}

	public int rowSize() {
		return rowNum;
	}

	public int cellSize() {
		return cellNum;
	}


	private void xlsData(Sheet sheet, int maxCell, int startRow, int... cellTypes) {
		sheet = init(sheet);
		rowNum = (sheet.getLastRowNum() + 1) - startRow;
		int indexNum = sheet.getLastRowNum() + 1;

		cellNum = maxCell;

		data = new String[rowNum][cellNum];
		int type = Cell.CELL_TYPE_STRING;


		for (int rowIndex = startRow; rowIndex < indexNum; rowIndex++) {
			Row row = sheet.getRow(rowIndex);
			int loop = minCellSize(row.getLastCellNum());
			for (int cellIndex = 0; cellIndex < loop; cellIndex++) {
				Cell cell = row.getCell(cellIndex);
				if (cell == null) {
					continue;
				}

				if (cellTypes.length > 0 && cellTypes.length > cellIndex) {
					type = cellTypes[cellIndex];

				}
				int index = rowIndex - startRow;
				try {
					switch (type) {
						case HSSFCell.CELL_TYPE_NUMERIC:
							if (cell.getCellType() != HSSFCell.CELL_TYPE_BLANK)
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
							if (HSSFDateUtil.isCellDateFormatted(cell)) {
								data[index][cellIndex] = formatDate(cell.getDateCellValue());
							} else {
								cell.setCellType(Cell.CELL_TYPE_STRING);
								data[index][cellIndex] = formatNumeric(cell.getStringCellValue());
							}
							break;
						case HSSFCell.CELL_TYPE_STRING:
							if (cell.getCellType() != HSSFCell.CELL_TYPE_BLANK)
								cell.setCellType(Cell.CELL_TYPE_STRING);
							data[index][cellIndex] = cell.getStringCellValue().trim();
							break;
						case CELL_TYPE_PHONE:
							data[index][cellIndex] = getPhone(cell, cellIndex);
							break;
						default:
							data[index][cellIndex] = cell.getStringCellValue().trim();
							break;
					}
				} catch (IllegalStateException e) {
					throw new IllegalArgumentException(String.format("第%d行 %s请输入正确格式的数据！", rowIndex + 1, sheet.getRow(startRow - 1).getCell(cellIndex)));
				}
			}
		}

	}

	public String toString() {
		StringBuilder src = new StringBuilder();
		src.append("{");
		if (rowNum > 0 && cellNum > 0) {
			src.append("[").append(data[0][0]);
			for (int j = 1; j < cellNum; j++) {
				src.append(",").append(data[0][j]);
			}
			src.append("]");
			for (int i = 1; i < rowNum; i++) {
				src.append(",[").append(data[i][0]);
				for (int j = 1; j < cellNum; j++) {
					src.append(",").append(data[i][j]);
				}
				src.append("]");
			}
		}
		src.append("}");
		return src.toString();
	}

	private int minCellSize(int lastCellNum) {
		return lastCellNum < cellNum ? lastCellNum : cellNum;
	}

	private static String formatDate(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
	}

	private static String formatNumeric(String value) {
//		String str = BigDecimal.valueOf(num).toString();
//		if (str.endsWith(".0")) {
//			str = str.replace(".0", "");
//		}
		String str = "";
		if (value.indexOf(".") > -1) {
			str = String.valueOf(new Double(value))
					.trim();
		} else {
			str = value.trim();
		}
		return str;
	}

	public boolean getFlag() {
		return flag;
	}


	/**
	 * get 手机号码字段
	 */
	private String getPhone(Cell cell, int rNum) {
		try {
			if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				DecimalFormat df = new DecimalFormat("###########");
				String value = df.format(cell.getNumericCellValue());
				return value;
			} else {
				String value = cell.getStringCellValue().trim();
				if (StringUtils.isBlank(value) || (!Double.isNaN(Double.parseDouble(value)) && value.length() == 11))
					return value;
				else
					throw new IllegalArgumentException(String.format(strRowFormat, rNum) + "手机号码只能输入11位数字");
			}
		} catch (Exception e) {
			logger.error("获取文件中的手机号码出错：" + String.format(strRowFormat, rNum) + e.getMessage(), e);
			throw new IllegalArgumentException(String.format(strRowFormat, rNum) + "手机号码只能输入11位数字");
		}

	}


	/**
	 * 取文件扩展名
	 *
	 * @param file
	 */
	private String getSuffix(String file) {
		file = file.replaceAll("\\\\", "/");
		file = file.substring(file.lastIndexOf("/") + 1);
		return file.substring(file.lastIndexOf(".") + 1);
	}

	/* 20150925补充by邝晓林，方法功能：初始化表格，去除无效空白行，帮助找出真实有效的行数 */
	private Sheet init(Sheet sheet) {
		boolean flag = false;
		for (int i = 0; i <= sheet.getLastRowNum(); ) {
			Row r = sheet.getRow(i);
			if (r == null) {
				// 如果是空行（即没有任何数据、格式）
				if (i == sheet.getLastRowNum())//如果到了最后一行，直接将那一行remove掉
					sheet.removeRow(r);
				else//如果还没到最后一行，则数据往上移一行
					//sheet.shiftRows(i+1, sheet.getLastRowNum(),-1);
					throw new IllegalArgumentException(String.format(strRowFormat, i) + "导入数据存在空行,请核查！");//20150930测试组要求，改为遇到空行直接抛异常（个人认为不好）
				continue;
			}
			flag = false;
			for (Cell c : r) {
				if (c.getCellType() != Cell.CELL_TYPE_BLANK) {
					flag = true;
					break;
				}
			}
			if (flag) {
				i++;
				continue;
			} else {//如果是空白行（即可能没有数据，但是有一定格式）
				if (i == sheet.getLastRowNum())//如果到了最后一行，直接将那一行remove掉
					sheet.removeRow(r);
				else//如果还没到最后一行，则数据往上移一行
					//sheet.shiftRows(i+1, sheet.getLastRowNum(),-1);
					throw new IllegalArgumentException("导入数据存在空行,请核查！");
				//20150930测试组要求，改为遇到空行直接抛异常（个人认为不好）
			}
		}
		return sheet;
	}

}
