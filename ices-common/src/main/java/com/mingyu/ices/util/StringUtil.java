package com.mingyu.ices.util;


import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author 廖大剑
 * @version V1.0
 * @Description: 工具类 - 字符串实用方法类
 * @Copyright: Copyright(c) 2011
 * @Company: 广州竞远
 * @date 2012-11-05
 */
public class StringUtil {


	public static final String EMPTY = "";

	/**
	 * 是否为数值
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0; ) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param obj 对象
	 * @Description:把对象转换成String
	 * @modify
	 */
	public final static String toString(Object obj) {
		if (obj == null) {
			return "";
		}
		if (obj.toString().equalsIgnoreCase("null")
				|| obj.toString().length() <= 0) {
			return "";
		}
		return obj.toString().trim();
	}

	/**
	 * 转换字符串，如果为空或null指针，则返回指定默认值
	 */
	public final static String toString(Object obj, String defaultVal) {
		String str = toString(obj);
		if (str.equals("")) {
			return defaultVal;
		}
		return str;

	}

	// 调试方法
	public final static void debug(Object obj) {
		System.out.println("[DEBUG]:" + toString(obj));
	}

	// 调试方法
	public final static void debug(Object[] obj) {
		for (Object tmp : obj) {
			debug(tmp);
		}
	}

	// 过滤将要写入到XML文件中的字符串，即过滤掉<![CDATA[和]]>标签
	public static String toXMLFilter(Object obj) {
		if (trim(obj).equals("")) {
			return " ";
		}
		return trim(obj).replaceAll("<!\\[CDATA\\[", "&lt;!CDATA").replaceAll(
				"\\]\\]>", "]] >");
	}

	// 返回一个对象的字符串，多数是处理字符串的
	public static String trim(Object obj) {
		return obj == null ? "" : String.valueOf(obj).trim();
	}

	// 对一字符串数组进行去空格操作
	public final static String[] trim(String[] aStr) {
		if (aStr == null) {
			return null;
		}
		for (int i = 0; i < aStr.length; i++) {
			aStr[i] = trim(aStr[i]);
		}
		return aStr;
	}

	// 过滤设置到SQL语句中的字符串
	public final static String toDBFilter(String aStr) {
		return trim(aStr).replaceAll("\\\'", "''");
	}

	/**
	 * 数字字符串的整型转换
	 *
	 * @param str        数字字符串
	 * @param defaultVal 默认值
	 * @return 转换后的结果
	 */
	public final static int parseInt(String str, int defaultVal) {
		try {
			return Integer.parseInt(toString(str));
		} catch (NumberFormatException ex) {
			return defaultVal;
		}
	}

	/**
	 * 将float型数据转换成保留用户自定义小数点位数，四舍五入
	 *
	 * @param value
	 * @param scale 要保留的小数点位数
	 * @return
	 */
	public final static float FloatFormat(float value, int scale) {
		BigDecimal b1 = new BigDecimal(value);
		return b1.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();

	}

	/**
	 * 数字字符串的整型转换
	 *
	 * @param o          数字字符串
	 * @param defaultVal 默认值
	 * @return 转换后的结果
	 */
	public final static int parseInt(Object o, int defaultVal) {
		return parseInt(toString(o), defaultVal);
	}

	/**
	 * 数字字符串的整型转换
	 *
	 * @param obj        数字字符串
	 * @param defaultVal 默认值
	 * @return Integer 转换后的结果
	 */
	public final static Integer parseInteger(Object obj, int defaultVal) {
		try {
			return Integer.valueOf(toString(obj));
		} catch (NumberFormatException ex) {
			return new Integer(defaultVal);
		}
	}

	/**
	 * @param str
	 * @return 转换后的结果
	 * @Description:数字字符串的长整型转换
	 * @modify
	 */
	public final static Long parseLong(String str) {
		// 初始化默认值为0
		return parseLongDefaultVal(str, 0);
	}

	/**
	 * @param o
	 * @return 转换后的结果
	 * @Description:数字字符串的长整型转换
	 * @author Jason
	 * @date Aug 19, 2011
	 * @modify
	 */
	public final static Long parseLong(Object o) {
		// 初始化默认值为0
		if (o == null) {
			return (long) 0;
		}
		return parseLongDefaultVal(o.toString(), 0);
	}

	/**
	 * @param str        数字字符串
	 * @param defaultVal 默认值
	 * @return 转换后的结果
	 * @Description:数字字符串的长整型转换
	 * @modify
	 */
	public final static Long parseLongDefaultVal(String str, long defaultVal) {
		try {
			return Long.valueOf(str);
		} catch (NumberFormatException ex) {
			return new Long(defaultVal);
		}
	}

	// 数字字符串数组转化为长整型数组
	public final static Long[] parseLong(String[] str) {
		if (str == null || str.length < 1) {
			return new Long[0];
		}
		Long[] result = new Long[str.length];
		for (int i = 0; i < str.length; i++) {
			result[i] = parseLong(str[i]);
		}
		return result;
	}

	/**
	 * @param str        数字字符串
	 * @param defaultVal 默认值
	 * @return 转换后的结果
	 * @Description:数字字符串的整型转换
	 * @modify
	 */
	public final static Integer[] parseInteger(String[] str, int defaultVal) {
		if (str == null || str.length < 1) {
			return new Integer[0];
		}
		Integer[] result = new Integer[str.length];
		for (int i = 0; i < str.length; i++) {
			result[i] = parseInteger(str[i], defaultVal);
		}
		return result;
	}

	public final static String[] parseChar(char[] str) {
		if (str == null || str.length < 1) {
			return new String[0];
		}
		String[] result = new String[str.length];
		for (int i = 0; i < str.length; i++) {
			result[i] = "" + str[i];
		}
		return result;
	}


	/**
	 * @param str        数字字符串
	 * @param defaultVal 默认值
	 * @return 转换后的结果
	 * @Description:数字字符串的整型转换
	 * @modify
	 */
	public final static int[] parseInt(String[] str, int defaultVal) {
		if (str == null || str.length < 1) {
			return new int[0];
		}
		int[] result = new int[str.length];
		for (int i = 0; i < str.length; i++) {
			result[i] = parseInt(str[i], defaultVal);
		}
		return result;
	}

	/**
	 * @param src    原数组
	 * @param target 指定数组
	 * @return 排除后的新数组
	 * @Description:将指定的值从源数组中进行排除，并返回一个新数组
	 * @modify
	 */
	public final static int[] exclude(int[] src, int[] target) {
		if (target == null || target.length < 1) {
			return src;
		}
		StringBuilder tmp = new StringBuilder();
		for (int tt : src) {
			if (!include(target, tt)) {
				tmp.append(tt + ",");
			}
		}
		if (tmp.length() > 1 && tmp.charAt(tmp.length() - 1) == ',') {
			tmp.deleteCharAt(tmp.length() - 1);
		}
		if (tmp.toString().trim().length() < 1) {
			return new int[0];
		}
		String[] array = tmp.toString().split(",");
		return parseInt(array, 0);
	}

	/**
	 * @param src    原数组
	 * @param target 指定数组
	 * @return 排除后的新数组
	 * @Description:将指定的target数组从src源数组中进行排除.
	 * @modify
	 */
	public final static String[] exclude(String[] src, String[] target) {
		if (target == null || target.length < 1) {
			return src;
		}
		StringBuilder tmp = new StringBuilder();
		for (String str : src) {
			if (!include(target, str)) {
				tmp.append(str + ",");
			}
		}
		if (tmp.length() > 1 && tmp.charAt(tmp.length() - 1) == ',') {
			tmp.deleteCharAt(tmp.length() - 1);
		}
		if (tmp.toString().trim().length() < 1) {
			return new String[0];
		}
		return tmp.toString().split(",");
	}

	// 将指定的数组字符串使用指定的符号进行连接.
	public final static String join(Object[] src, String spliter) {
		if (src == null || src.length < 1) {
			return "";
		}
		StringBuffer tmp = new StringBuffer();
		// String mySpliter = trim(spliter).intern() == "" ? "," : spliter;
		String mySpliter = trim(spliter);
		for (int i = 0; i < src.length; i++) {
			tmp.append(src[i]);
			if (i < src.length - 1) {
				tmp.append(mySpliter);
			}
		}
		return tmp.toString();
	}


	// 将指定的数组字符串使用指定的符号进行连接.
	public final static String join(char[] src, String spliter) {
		if (src == null || src.length < 1) {
			return "";
		}
		StringBuffer tmp = new StringBuffer();
		// String mySpliter = trim(spliter).intern() == "" ? "," : spliter;
		String mySpliter = trim(spliter);
		for (int i = 0; i < src.length; i++) {
			tmp.append(src[i]);
			if (i < src.length - 1) {
				tmp.append(mySpliter);
			}
		}
		return tmp.toString();
	}

	// 将指定的数组字符串使用指定的符号进行连接.
	public final static String join(int[] src, String spliter) {
		if (src == null || src.length < 1) {
			return "";
		}
		StringBuffer tmp = new StringBuffer();
		String mySpliter = trim(spliter).intern() == "" ? "," : spliter;
		for (int i = 0; i < src.length; i++) {
			tmp.append(src[i] + mySpliter);
		}
		return tmp.deleteCharAt(tmp.length() - 1).toString();
	}

	/**
	 * @param src
	 * @param str
	 * @return
	 * @Description: 将指定的字符串数组使用指定的字符串进行保围，比如一字符串数组如下： ["hello",
	 * "world"],使用的包围字符串为"'",那么返回的结果就应该是： ["'hello'","'world'"].
	 * @modify
	 */
	public final static String[] arround(String[] src, String str) {
		if (src == null || src.length < 1) {
			return src;
		}
		String[] result = new String[src.length];
		for (int i = 0; i < src.length; i++) {
			result[i] = str + src[i] + str;
		}
		return result;
	}

	// 判断指定的字符串是否是空指针或空串
	public final static boolean isNullAndBlank(String src) {
		return trim(src).intern() == "";
	}

	/**
	 * @param name
	 * @return
	 * @Description:将指定的字符串转换成符合JavaBean规范的方法名称！ 此方法将只转换第一个字母为大写字母，比如有一字符串是
	 * helloWorld
	 * ,那么转换后就是：setHelloWorld.另外如果给出
	 * 的字符串为空（null或""），那么将直接返回空字符串！
	 * @modify
	 */
	public final static String toMethodName(String name) {
		String tmp = trim(name).intern();
		if (tmp == "") {
			return "";
		}
		if (tmp.length() < 2) {
			return "set" + name.toUpperCase();
		} else {
			return "set" + name.substring(0, 1).toUpperCase()
					+ name.substring(1);
		}
	}


	/**
	 * @param map
	 * @return
	 * @Description:将map中的键和值进行对应并返回成字符串.
	 * @modify
	 */

	@SuppressWarnings("unchecked")
	public final static String mapToString(Map map) {
		if (map == null) {
			return null;
		}
		StringBuilder buf = new StringBuilder("[");
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {
			Object key = it.next();
			buf.append(String.valueOf(key) + ":" + String.valueOf(map.get(key))
					+ ",");
		}
		if (buf.charAt(buf.length() - 1) == ',') {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.append(']').toString();
	}

	// 检查给定的数组长度是否一致，若全部一致则返回true，否则返回false
	public final static boolean sameLength(String[]... array) {
		if (array.length <= 1) {
			return true;
		}
		for (int i = 0; i < array.length; i++) {
			String[] str1 = array[i];
			for (int j = i + 1; j < array.length; j++) {
				String[] str2 = array[j];
				if (str1 == null && str2 == null) {
					continue;
				}
				if (str1 == null && str2 != null) {
					return false;
				}
				if (str1 != null && str2 == null) {
					return false;
				}
				if (str1.length != str2.length) {
					return false;
				}
			}
		}
		return true;
	}

	// 检查指定的数组中是否包含了指定的数字.
	public final static boolean include(int[] source, int test) {
		if (source == null || source.length < 1) {
			return false;
		}
		for (int tmp : source) {
			if (tmp == test) {
				return true;
			}
		}
		return false;
	}

	// 检查指定的字符串数组中是否包含了指定的字符串.
	public final static boolean include(String[] source, String test) {
		if (source == null || source.length < 1) {
			return false;
		}
		for (String tmp : source) {
			if (tmp == null && test == null) {
				return true;
			}
			if (tmp != null && tmp.equals(test)) {
				return true;
			}
		}
		return false;
	}

	// 检查指定的字符串数组中是否包含了指定的字符串，不区分大小写.
	public final static boolean includeIgnoreCase(String[] source, String test) {
		if (source == null || source.length < 1) {
			return false;
		}
		for (String tmp : source) {
			if (tmp == null && test == null) {
				return true;
			}
			if (tmp != null && tmp.equalsIgnoreCase(test)) {
				return true;
			}
		}
		return false;
	}

	// 将指定字符串的首字母变成大写.
	public final static String capitalize(String str) {
		if (str == null || str.length() < 1) {
			return str;
		}
		if (str.length() == 1) {
			return str.toUpperCase();
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	// 将指定字符串的首字母变成小写.
	public final static String unCapitalize(String str) {
		if (str == null || str.length() < 1) {
			return str;
		}
		if (str.length() == 1) {
			return str.toLowerCase();
		}
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	// 获取当前日期形成的路径字符串.
	//public final static String getDailyDirectory() {
	//	return DateUtil.getFormateDate("yyyy/MM/dd/");
	//}

	// 反转字符串.
	public final static String reverse(String str) {
		if (trim(str).equals("")) {
			return str;
		}
		return new StringBuilder(str).reverse().toString();
	}

	/**
	 * 数据保留多少位小数
	 *
	 * @param d   数据
	 * @param len 后小数位数
	 * @return
	 * @author 廖大剑
	 */
	public static String round(double d, int len) {
		String slen = "";
		for (int i = 0; i < len; i++) {
			slen += "0";
		}
		DecimalFormat myformat = new DecimalFormat("#0." + slen);
		return myformat.format(d);
	}

	/**
	 * 字符串转换double类型，如不是double类型，返回 0
	 *
	 * @param o
	 * @return
	 */
	public static double parseDouble(Object o) {
		if (o == null) {
			return 0;
		}
		try {
			return Double.parseDouble(o.toString());
		} catch (NumberFormatException e) {
			// TODO: handle exception
			return 0;
		}
	}

	/**
	 * 获取项目的根目录，如：E:/项目/web ，到web目录位置
	 *
	 * @return
	 */
	public static String getPath() {
		String path = StringUtil.class.getResource("/").toString();
		path = path.replaceAll("file:", "");
		path = path.replaceAll("/classes", "");
		path = path.replaceAll("/WEB-INF", "");
		return path;
	}

	/**
	 * 获得项目classes位置
	 *
	 * @return
	 */
	public static String getPathClasses() {
		String path = StringUtil.class.getResource("").toString();
		path = path.substring(0, path.indexOf("classes")) + "classes/";
		return path.replaceAll("file:", "");
	}

	/**
	 * 将数组合成字符串，用分隔符号分开
	 *
	 * @param strs
	 * @param symbol 分隔符号
	 * @return
	 */
	@SuppressWarnings("null")
	public static String asArray(Object[] strs, String symbol) {
		if (strs == null) {
			return null;
		}
		String newstr = "";
		for (Object o : strs) {
			if (o != null || !o.toString().equals("")) {
				newstr += newstr.equals("") ? o.toString() : symbol + o;
			}
		}
		return newstr;
	}

	public static String byte2hex(byte[] b) {// 二行制转字符串

		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	public static String getSha1(String str) {
		java.security.MessageDigest md = null;
		try {
			md = java.security.MessageDigest.getInstance("sha-1");
			byte[] text = str.getBytes();
			md.update(text);
			byte[] sha1 = md.digest();
			return byte2hex(sha1);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 删除LIST中指定值
	 *
	 * @param list       原LIST
	 * @param removeList 要删除的指定值
	 * @return
	 */
	public static List<Integer> remove(List<Integer> list, List<Integer> removeList) {
		List<Integer> newlist = new ArrayList<Integer>();
		for (int id1 : list) {
			boolean t = false;
			for (int id2 : removeList) {
				if (id1 == id2) {
					t = true;
				}
			}
			if (!t) {
				newlist.add(id1);
			}
		}
		if (newlist.size() <= 0) {
			newlist.add(0);
		}
		return newlist;
	}

	/**
	 * 获取完整时间
	 *
	 * @return
	 */
	public static String getDateTime() {
		String datetime = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
		datetime = sdf.format(new Date());
		return datetime;
	}

	public static String getDateTime(String text) {
		String datetime = "";
		SimpleDateFormat sdf = new SimpleDateFormat(text);
		datetime = sdf.format(new Date());
		return datetime;
	}

	/**
	 * 检查邮件地址格式是否正确
	 *
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email) {
		if (!email.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")) {
			return false;
		}
		return true;
	}

	/**
	 * 全为字母 add by livia.
	 *
	 * @param letters 转入值
	 * @return boolean
	 */
	public static boolean isFullLetters(String letters) {
		for (int i = letters.length(); --i >= 0; ) {
			if (!Character.isLetter(letters.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 数字+字母 add by livia.
	 *
	 * @param letters 转入值
	 * @return boolean
	 */
	public static boolean isNumericAndLetters(String letters) {

		/*for (int i = letters.length();--i>=0;){
			if (!Character.isDigit(letters.charAt(i)) && !Character.isLetter(letters.charAt(i))){
				return false;
			}
		}
		return true;
		*/

		int iDigit = 0;
		int iLetter = 0;
		for (int i = letters.length(); --i >= 0; ) {
			if (Character.isDigit(letters.charAt(i))) {
				iDigit++;
			} else if (Character.isLetter(letters.charAt(i))) {
				iLetter++;
			}
		}
		boolean bRtn = false;
		if (iDigit > 0 && iLetter > 0) {
			bRtn = true;
		}
		return bRtn;
	}

	/**
	 * 数字 或着 字母 add by livia.
	 *
	 * @param letters 转入值
	 * @return boolean
	 */
	public static boolean isNumericOrLetters(String letters) {

		/*for (int i = letters.length();--i>=0;){
			if (!Character.isDigit(letters.charAt(i)) && !Character.isLetter(letters.charAt(i))){
				return false;
			}
		}
		return true;
		*/

		int iDigit = 0;
		int iLetter = 0;
		for (int i = letters.length(); --i >= 0; ) {
			if (Character.isDigit(letters.charAt(i))) {
				iDigit++;
			} else if (Character.isLetter(letters.charAt(i))) {
				iLetter++;
			}
		}
		boolean bRtn = false;
		if (iDigit > 0 || iLetter > 0) {
			bRtn = true;
		}
		return bRtn;
	}

	// 生成随机标识号
//	public static  String randomIDNO(int num,String formates){
//		Random r = new Random();
//		String val="";
//		for(int n=0;n<num;n++){
//			int i = r.nextInt(26);
//			val+=((char)(i+65)); //65大写, 97小写
//		}
//		String formate  =DateUtil.getFormateDate(formates);
//		if(!toString(formates).equals("")){
//			val=val+formate;
//		}
//		return val;
//	}


//	public static  String generateCardNo(int num){
//		Random r = new Random();
//		String val=notInt0()+"";
//		for(int n=0;n<num - 4;n++){
//			int i = r.nextInt(10);
//			val+= i;
//		}
//		String formate  =DateUtil.getFormateDate("SSS");
//		val=val+formate; 
//		return val;
//	}

	//生成非整形0，必需大于0
	public static int notInt0() {
		Random r = new Random();
		int i = r.nextInt(10);
		if (i <= 0) {
			i = notInt0();
		}
		return i;
	}

	/**
	 * 根据字符串转换为指定格式的时间字符串yyyy-MM-dd hh:mm:ss，若参数为空，则取当前系统时间
	 *
	 * @param dateString
	 * @return
	 */
	public static String getDateByString(String dateString) {

		String date = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if ("".equals(StringUtil.toString(dateString, ""))) {
			date = sdf.format(new Date());
		} else {
			String dateStr = dateString.substring(0, 4) + "-" + dateString.substring(4, 6) + "-" + dateString.substring(6, 8) + " " + dateString.substring(8, 10) + ":" + dateString.substring(10, 12) + ":" + dateString.substring(12);
			date = dateStr;
		}
		return date;
	}


	/**
	 * 生成随机标识号
	 * @param count                字母个数
	 * @param numericCount        数字个数
	 * @return
	 */
//	public static String randomIDNO(Integer count, Integer numericCount){
//		String val = "";
//		String alphabetic = RandomStringUtils.randomAlphabetic(count).toUpperCase();
//		alphabetic = alphabetic.replace('O', 'R');
//		alphabetic = alphabetic.replace('I', 'E');
//		
//		String numeric = RandomStringUtils.randomNumeric(numericCount);
//		val = alphabetic + numeric;
//		return val;
//	}

	/**
	 * 路径斜杆转议 将 '/' 转成 '\' ,将 '\\' 转成 '\'
	 *
	 * @param path
	 * @return
	 */
	public static String dirReplace(String path) {
		path = path.replace("\\", "/");
		path = path.replace("\\\\", "/");
		return path;
	}

	/**
	 * 去掉前后路径斜杆
	 *
	 * @param data 数据串
	 * @return
	 */
	public static String removeBeforeAfterDir(String data) {
		data = dirReplace(data);
		if (data.startsWith("/")) {
			data = data.substring(1, data.length());
		}
		if (data.endsWith("/")) {
			data = data.substring(0, data.length() - 1);
		}
		return data;
	}

	/**
	 * html 必须是格式良好的
	 * @param str
	 * @return
	 * @throws Exception
	 */
//	public static String formatHtml(String str) throws Exception {
//		Document document = null;
//		document = DocumentHelper.parseText(str);
//		OutputFormat format = OutputFormat.createPrettyPrint();
//		format.setEncoding("utf-8");
//		StringWriter writer = new StringWriter();
//		HTMLWriter htmlWriter = new HTMLWriter(writer, format);
//		htmlWriter.write(document);
//		htmlWriter.close();
//		return writer.toString();
//	}

	/**
	 * 转换字符串中的"，和空格"为","号
	 *
	 * @param content
	 * @return
	 */
	public static String replace(String content) {
		if (toString(content).equals(""))
			return "";
		content = content.replaceAll("@", "");
		content = content.replaceAll(" ", ",");
		content = content.replaceAll("，", ",");
		return content;
	}


	// 测试
	public static void main(String[] args) {
//		System.out.println(replace("@hl730218，@HL622171，hl22222 hl33333"));
		System.out.println(StringUtil.removeBeforeAfterDir("/abc/bbb/eeee"));
	}


	/**
	 * 通过数量获得 A,B,C,D.. 数组, 1 返回 A, 2 返回 A B , 26 返回 A B C ... Z
	 *
	 * @param answerCount
	 * @return
	 */
	public static Object[] answerCountToChar(int answerCount) {

		String answers[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
				"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
				"U", "V", "W", "X", "Y", "Z"};

		ArrayList<String> ans = new ArrayList();

		for (int i = 0; i < answerCount; i++) {
			ans.add(answers[i]);
		}

		return ans.toArray();
	}
}
