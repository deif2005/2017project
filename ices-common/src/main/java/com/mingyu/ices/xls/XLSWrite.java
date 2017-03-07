package com.mingyu.ices.xls;

import com.mingyu.ices.xls.*;
import com.mingyu.ices.xls.XLSFile;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Description:  写XLS文件工具类 
 * @version V1.0
 * 
 * @eg XLSSheet xlsSheet = new XLSSheet();<BR>
		xlsSheet.setData(list);<BR>
		<BR><BR>
		XLSFile xlsFile = new XLSFile();<BR>
		xlsFile.addXlsSheet(xlsSheet);<BR>
		<BR><BR>
		XLSWrite xls = new XLSWrite();<BR>
		xls.write("", xlsFile);<BR>
 */
public class XLSWrite {
	
	private static HSSFWorkbook wb = null;;  
	
	/** 表格描述样式 **/
	private static HSSFCellStyle descStyle = null;
	
	/** 列头样式 **/
	private static HSSFCellStyle titleStyle = null;
	
	/** 一般内容样式 **/
	private static HSSFCellStyle dataStyle = null;
	
	/** 金额样式 **/
	private static HSSFCellStyle moneyStyle = null;
	
	/** 表格 **/
	private static HSSFSheet sheet = null;
	/** 行 **/
	private static HSSFRow row = null;
	/** 单元格 **/
	private static HSSFCell cell = null;
	
	/** XLS表格对象 **/
	private static XLSSheet xlsSheet = null;

	private static String dateFmt = null;

	/**
	 * 初始化
	 */
	private static void init(XLSFile xlsFile){
		wb = new HSSFWorkbook();

		//表格描述样式
		descStyle = wb.createCellStyle();
		descStyle.setFillForegroundColor(HSSFColor.SKY_BLUE.index); //前景色
		descStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		descStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //水平居中
		descStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中
		descStyle.setWrapText(true);	//自动换行
		HSSFFont descFont = wb.createFont();
		descFont.setFontName("Courier New"); // 字体名称
		descFont.setFontHeightInPoints((short) 16); // 字体高度
		descFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 宽度
		descStyle.setFont(descFont);

		//列头样式
		titleStyle = wb.createCellStyle();
		titleStyle.setFillForegroundColor(HSSFColor.GOLD.index); //前景色
		titleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		titleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); //水平居中
		titleStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); //垂直居中
		titleStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);	//上边框
		titleStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		titleStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN); //左边框
		titleStyle.setBorderRight(HSSFCellStyle.BORDER_THIN); //右边框
		HSSFFont titleFont = wb.createFont();
		titleFont.setFontName("Courier New"); // 字体名称
		titleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); // 宽度
		titleStyle.setFont(titleFont);


		HSSFFont dataFont = wb.createFont();
		dataFont.setFontName("Courier New"); // 字体名称
		//一般内容单元格样式
		dataStyle = wb.createCellStyle();
		dataStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);	//上边框
		dataStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		dataStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN); //左边框
		dataStyle.setBorderRight(HSSFCellStyle.BORDER_THIN); //右边框
		dataStyle.setFont(dataFont);

		//金额单元格样式
		moneyStyle = wb.createCellStyle();
		moneyStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);	//上边框
		moneyStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
		moneyStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN); //左边框
		moneyStyle.setBorderRight(HSSFCellStyle.BORDER_THIN); //右边框
		moneyStyle.setFont(dataFont);
		moneyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(xlsFile.getMoneyFmt())); //金额显示格式

		dateFmt = xlsFile.getDateFmt(); //日期时间显示格式
	}

	/**
	 * 写入数据到XLS文件
	 * @param filePath 文件路径 (request.getSession().getServletContext().getRealPath("/文件")或者ServletActionContext.getServletContext().getRealPath("/文件"))
	 * @param xlsFile XLS文件对象
	 */
	public static void write(String filePath, XLSFile xlsFile) {
		 
		
		init(xlsFile); //初始化
		OutputStream os=null;
		try { 

			List<XLSSheet> sheets = xlsFile.getSheets();
			for (int sheetIndex = 0; sheetIndex < sheets.size(); sheetIndex++) {
				xlsSheet = sheets.get(sheetIndex);

				//创建新表格,如重名则在原名后加下标
				if (wb.getSheet(xlsSheet.getName())==null) {
					sheet = wb.createSheet(xlsSheet.getName());	
				} else {
					sheet = wb.createSheet(xlsSheet.getName()+"("+sheetIndex+")");	
				}
				
				
				//表格标题描述,首行,根据sheet列数合并行单元格
				if (xlsSheet.hasDesc()) {
					sheet.addMergedRegion(new CellRangeAddress(0,0,0,xlsSheet.maxCell())); //合并单元格
					row = sheet.createRow(0);// 创建行
					row.setHeightInPoints(xlsSheet.getHeightInPoints());
					
					cell = row.createCell(0);// 创建单元格
					cell.setCellStyle(descStyle);// 设置样式
					
					cell.setCellValue(xlsSheet.getDesc()); 	//表格标题(描述)
				}
				
				int position = xlsSheet.getPosition(); //数据行偏移值(内容首行位置)

				//列头
				if (xlsSheet.hasTitles()) {
					row = sheet.createRow(position-1);	//创建行,位置:内容首行位置-1
					row.setHeightInPoints(18);
					for (int titleIndex = 0; titleIndex < xlsSheet.getTitles().length; titleIndex++) {
						cell = row.createCell(titleIndex);	// 创建单元格
						cell.setCellStyle(titleStyle);	// 设置样式
						cell.setCellValue(xlsSheet.getTitles()[titleIndex]); //设值
					} 
				}
				
				//内容 (数据)
				Object[] rowData = null; //行原始数据
				Object cellData = null;	//单元格原始数据
				for (int rowNum = 0; rowNum < xlsSheet.getData().size(); rowNum++) {
					rowData = xlsSheet.getData().get(rowNum);	//行原始数据
					row = sheet.createRow(rowNum+position);	//创建行
					
					for (int cellNum = 0; cellNum < xlsSheet.getCellSize(); cellNum++) {
						cell = row.createCell(cellNum); // 建立单元格
						cell.setCellStyle(dataStyle); //设置样式
						cellData = rowData[cellNum];
						if (cellData != null) {			//20111116
							if (isNumeric(cellData.toString())) {	//数值
								cell.setCellValue(Double.valueOf(cellData.toString())); 
								cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
								if (xlsSheet.isMoney(cellNum)) { //判断该列是否金额
									cell.setCellStyle(moneyStyle);//金额样式
								} 
								 
							} else if (cellData instanceof Date) {	//日期
								cell.setCellValue(formatDate((Date) cellData)); 
							} else {	//其他数据类型,主要为字符串
								 
								cell.setCellValue(cellData.toString()); 
							}
						}
					}
				}
				
				//设置列宽,如为设置参数则采用自动列宽
				if (xlsSheet.hasCellWeights()) {
					short[] weights = xlsSheet.getCellWeights();	//列宽数组
					
					//需处理列宽数组长度与总列数不等的情况
					
					int loop = weights.length<xlsSheet.getCellSize()?weights.length:xlsSheet.getCellSize();
					for (int cellNum = 0; cellNum < loop; cellNum++) {
						if (weights[cellNum]>0) {
							sheet.setColumnWidth((short)cellNum, (short)35.7*weights[cellNum]);
						} else {
							sheet.autoSizeColumn(cellNum);
						}
					}
					for (int cellNum = loop; cellNum < xlsSheet.getCellSize(); cellNum++) {
						sheet.autoSizeColumn(cellNum);
					}
				} else {	//自动列宽
					for (int cellNum = 0; cellNum < xlsSheet.getCellSize(); cellNum++) {
						sheet.autoSizeColumn(cellNum);
					}
				}
			}
			os = new FileOutputStream(filePath);
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("导出Excel文档错误!", e);
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					throw new RuntimeException("关闭输出流失败!", e);
				}
			}
		}

	}
	
	/**
	 * 判断一个字符串是否为数值
	 * @param str 字符串
	 * @return boolean
	 */
	private static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^[+\\-]?((\\d*\\.\\d+)|(\\d+))$");
		Matcher matcher = pattern.matcher(str.trim());
		if(matcher.matches()){
			if(str.length() > 14){
				//大于14位长度的数值，不做为数值导出，当字符串导出
				return false;
			}
			return true;
		}
		return false;  
	}
	
	/**
	 * 格式化java.util.Date
	 * @param date java.util.Date对象
	 * @return 格式化后的时间(日期)字符串
	 */
	private static String formatDate(Date date) {
		SimpleDateFormat f = new SimpleDateFormat(dateFmt, Locale.US);
		return f.format(date);
	}
	
	
//	public static void main(String[] args) {
//		String str="13242123332,13242123332,13242123332,13242123332,13242123332,13244444444,13242123332";
//		List<Object[]> list = new ArrayList<Object[]>();
//		String[] data=str.split(",");
//		String[] o=null;
//		for(int i=0;i<data.length;i++){
//			o=new String[1];
//			o[0] = data[i];
//			list.add(o);
//		}
//		
//		
//		
//		
//		XLSSheet xlsSheet = new XLSSheet();
//		xlsSheet.setData(list);
//		
//		XLSFile xlsFile = new XLSFile();
//		xlsFile.addXlsSheet(xlsSheet);
//		
//		XLSWrite xls = new XLSWrite();
//		xls.write("", xlsFile);
//		
//		
//	}
	 

}
