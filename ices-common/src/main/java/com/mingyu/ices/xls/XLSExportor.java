package com.mingyu.ices.xls;

import com.mingyu.ices.xls.*;
import com.mingyu.ices.xls.XLSFile;
import com.mingyu.ices.xls.XLSSheet;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @Description: XLS文件导出工具类,与 {@link XLSFile} 和 {@link XLSSheet} 配合使用
 * @version V1.0
 * @date Oct 29, 2011
 * @eg 	XLSFile xlsFile = new XLSFile();<BR>
 * 		xlsFile.setFileName("导出excel文件(默认为当前时间,精确到分钟)");<BR>
 * 		XLSSheet xlsSheet = new XLSSheet();<BR>
 * 		xlsSheet.setName("表格名(xls中标签位置)");<BR>
 * 		xlsSheet.setDesc("表格标题/描述(首行,可空)");<BR>
 * 		xlsSheet.setTitles(new String[] { "列头A", "列头B", "列头C"});<BR>
 * 		xlsSheet.setCellWeights(new int[] { 180, 0, 120 });//列宽,不设值时设置自动列宽<BR>
 *
 * 		List<Object[]> data = new ArrayList<Object[]>();<BR>
 * 		data.add(new Object[]{"abc",123,new Date()});//一行<BR>
 * 		xlsSheet.setData(data);<BR>
 *
 * 		xlsFile.addXlsSheet(xlsSheet); //可添加多个,或用setSheets(List<XLSSheet> sheets);<BR>
 *
 * 		XLSExportor.expXlsFile(getResponse(), xlsFile);//需传入HttpServletResponse对象
 */
public class XLSExportor {
	static Logger logger = Logger.getLogger(XLSExportor.class);
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
	 * 浏览器导出保存本地
	 * @param response
	 * @param xlsFile
	 */
	public static void export(javax.servlet.http.HttpServletResponse response, XLSFile xlsFile){
		if (response == null || xlsFile == null
				|| xlsFile.getSheets() == null
				|| xlsFile.getSheets().isEmpty()) {
			StringBuilder msg = new StringBuilder("HttpServletResponse is null or XlsFile illegal:");
			msg.append("HttpServletResponse:").append(response==null?"NULL;":"NOT NULL;");
			msg.append("XLSFile:").append(xlsFile);
			throw new RuntimeException(msg.toString());
		}
		try {
			response.addHeader("Content-Disposition", "attachment;filename="
					+ URLEncoder.encode(xlsFile.getFileName() + ".xls", "UTF-8"));

			response.setContentType(xlsFile.CONTENT_TYPE);

			// 输出流
			OutputStream os = response.getOutputStream();
			exportXls(os,xlsFile);
		}catch(Exception e){
			throw new RuntimeException("导出Excel文档错误!", e);
		}
	}


	/**
	 * 导出XLS文件
	 * @param
	 * @param xlsFile XLS文件对象
	 */
	public static void exportXls(OutputStream os, XLSFile xlsFile) {
		
		
		init(xlsFile); //初始化
		 
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
	
	/**
	 * 文件下载
	 * @param response
	 * @param file
	 */
	public static void download(javax.servlet.http.HttpServletResponse response, String file,
			String fileName) {
		try {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			OutputStream fos = null;
			InputStream fis = null;

			File uploadFile = new File(file);
			fis = new FileInputStream(uploadFile);
			bis = new BufferedInputStream(fis);
			fos = response.getOutputStream();
			bos = new BufferedOutputStream(fos);
			// 这个就就是弹出下载对话框的关键代码
			response.setHeader("Content-disposition", "attachment;filename="
					+ URLEncoder.encode(fileName, "utf-8"));
			int bytesRead = 0;
			// 这个地方的同上传的一样。我就不多说了，都是用输入流进行先读，然后用输出流去写，唯一不同的是我用的是缓冲输入输出流
			byte[] buffer = new byte[8192];
			while ((bytesRead = bis.read(buffer, 0, 8192)) != -1) {
				bos.write(buffer, 0, bytesRead);
			}
			bos.flush();
			fis.close();
			bis.close();
			fos.close();
			bos.close();
		} catch (Exception e) {
			logger.error ("下载文件["+fileName+"]",e);
		}
	}
 
}
