package com.mingyu.ices.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @author 廖大剑
 * @version V1.0
 * @Description: 工具类 - 日期实用方法类
 * @Copyright: Copyright(c) 2011
 * @Company: 广州竞远系统网络技术有限公司
 * @date Aug 12, 2011
 */
public class DateUtil {

	// 取得格式化效果的系统日期！ 格式如：yyyy-MM-dd kk:mm:ss
	public final static String getDateToString(Date d) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		return f.format(d);
	}

	// 取得格式化效果的系统日期！ 格式如：kk
	public final static String getHourToString(Date d) {
		SimpleDateFormat f = new SimpleDateFormat("kk:mm", Locale.US);
		return f.format(d);
	}

	// 取得格式化效果的系统日期！ 格式如：yyyy-MM-dd kk:mm:ss
	public final static String getDateToString(Date date, String format) {
		SimpleDateFormat f = new SimpleDateFormat(format, Locale.US);
		return f.format(date);
	}

	// 取得格式化效果的系统日期！ 格式如：yyyy-MM-dd kk:mm:ss
	public final static String getFormateDate(String formate) {
		SimpleDateFormat f = new SimpleDateFormat(formate, Locale.US);
		return f.format(new Date());
	}

	// 获取默认格式的日期和时间.形如：2007-7-8- 12:23:54
	public final static String getDateTime() {
		return getFormateDate("yyyy-MM-dd kk:mm:ss");
	}

	// 获取默认格式的日期.形如：2007-7-8
	public final static String getDate() {
		return getFormateDate("yyyy-MM-dd");
	}

	// 获取当前的年份
	public final static String getYear() {
		return getFormateDate("yyyy");
	}

	// 获取当前的短年份
	public final static String getShortYear() {
		return getFormateDate("yy");
	}

	// 获取当前的月份
	public final static String getMonth() {
		return getFormateDate("MM");
	}

	// 获取当前的小时
	public final static String getHour() {
		return getFormateDate("kk");
	}

	// 获取当前分钟
	public final static String getMinute() {
		return getFormateDate("mm");
	}

	// 获取当前的短月份
	public final static String getShortMonth() {
		return getFormateDate("M");
	}

	// 获取当前的日期
	public final static String getDay() {
		return getFormateDate("dd");
	}

	// 获取当前的短日期
	public final static String getShortDay() {
		return getFormateDate("d");
	}

	// 获取默认格式的时间(24小时制).形如：16:23:54
	public final static String getTime() {
		return getFormateDate("kk:mm:ss");
	}

	// 判断指定的字符串是否是正确的日期时间字符串.
	// 该方法支持日期或日期时间的判断.

	public final static boolean isDate(String dateStr) {
		Date dt = parseSimpleDate(dateStr);
		if (dt != null)
			return true;
		return parseSimpleDateTime(dateStr) != null;

	}

	// 使用指定的模式来判断字符串是否是日期时间字符串.
	public final static boolean isDate(String pattern, String dateStr) {
		return parseSimpleDT(pattern, dateStr) != null;
	}

	// 将指定的日期时间格式的字符串转换成日期对象.
	public final static Date parseDateTime(String dateStr) {
		try {
			return DateFormat.getDateTimeInstance().parse(dateStr);
		} catch (ParseException ex) {
			return null;
		}
	}

	// 将指定日期格式的字符串转换成日期对象.
	public final static Date parseDate(String dateStr) {
		try {
			return DateFormat.getDateInstance().parse(dateStr);
		} catch (ParseException ex) {
			return null;
		}
	}

	// 使用简单化的方式来解析日期时间格式.
	public final static Date parseSimpleDateTime(String dateStr) {
		return parseSimpleDT("yyyy-MM-dd kk:mm:ss", dateStr);
	}

	public final static Date parseSimpleDate(String dateStr) {
		return parseSimpleDT("yyyy-MM-dd", dateStr);
	}

	public final static Date parseSimpleTime(String timeStr) {
		return parseSimpleDT("kk:mm:ss", timeStr);
	}

	// 使用指定的模式来解析字符串日期时间.
	public final static Date parseSimpleDT(String pattern, String dateStr) {
		try {
			return new SimpleDateFormat(pattern, Locale.US).parse(dateStr);
		} catch (ParseException ex) {
			return null;
		}
	}

	// 比较两个日期的大小.返回-1表示date1在date2之前，返回0表示两者相等，返回1 则表示date1在date2之后.
	public final static int compareDate(Date date1, Date date2) {
		if (date1.before(date2))
			return -1;
		if (date1.after(date2))
			return 1;
		return 0;
	}

	// 测试日期date1是否在date2之前.
	public final static boolean isBefore(Date date1, Date date2) {
		if (date1 == null || date2 == null)
			return false;
		return date1.before(date2);
	}

	public final static boolean isBeforeNow(Date date1) {
		return isBefore(date1, Calendar.getInstance().getTime());
	}

	// 测试日期date1是否在date2之后.
	public final static boolean isAfter(Date date1, Date date2) {
		if (date1 == null || date2 == null)
			return false;
		return date1.after(date2);
	}

	public final static boolean isAfterNow(Date date1) {
		return isAfter(date1, Calendar.getInstance().getTime());
	}

	// 测试日期date1和date2是否相等.
	public final static boolean isEquals(Date date1, Date date2) {
		if (date1 == null || date2 == null)
			return false;
		return date1.equals(date2);
	}

	public final static boolean isEqualsNow(Date date1) {
		return isEquals(date1, Calendar.getInstance().getTime());
	}

	// 获取当前日期时间，参数表示在此基础上的偏差，参数依次表示年、月、日、时、分、秒。 为正则表示在此日期上加、为负则表示在此日期上减。
	public final static Date getNowDate(int... deviation) {
		return setDate(new Date(), deviation);
	}

	// 在某一指定的日期基础上进行日期的偏差设置，参数意义同getNowDate
	public final static Date setDate(Date date, int... deviation) {
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.setTime(date);
		if (deviation.length < 1)
			return cal.getTime();
		final int[] filed = {Calendar.YEAR, Calendar.MONTH,
				Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE,
				Calendar.SECOND};
		for (int i = 0; i < deviation.length; i++) {
			cal.add(filed[i], deviation[i]);
		}
		return cal.getTime();
	}

	/**
	 * @param dt 日期时间字符串,必须包含时间
	 * @return String
	 * @Description:对日期时间字符串的提示字符串生成方法. 该方法主要是对日期时间字符串的提示, 类似:1分钟前,1小时前等.对于大于1天的,则会提示
	 * 1天前,2天前等等这样的提示.
	 * @date Aug 12, 2011
	 * @modify
	 */
	public final static String dateTimeTips(Date dt) {
		Calendar cal = Calendar.getInstance(); // 获取当前日期时间
		long times = cal.getTimeInMillis() - dt.getTime(); // 获取时间差
		if (times <= 60 * 1000L)
			return "1 分钟前";
		else if (times <= 60 * 60 * 1000L)
			return (times / (60 * 1000)) + " 分钟前";
		else if (times <= 24 * 60 * 60 * 1000L)
			return (times / (60 * 60 * 1000L)) + " 小时前";
		else if (times <= 7 * 24 * 60 * 60 * 1000L)
			return (times / (24 * 60 * 60 * 1000L)) + " 天前";
		else if (times <= 30 * 24 * 60 * 60 * 1000L)
			return (times / (7 * 24 * 60 * 60 * 1000L)) + " 星期前";
		else if (times <= 12 * 30 * 24 * 60 * 60 * 1000L)
			return (times / (30 * 24 * 60 * 60 * 1000L)) + " 个月前";
		return (times / (12 * 30 * 24 * 60 * 60 * 1000L)) + " 年前";
	}

	public final static String dateTips(String dateStr) {
		Date dt = parseSimpleDate(dateStr);
		if (dt == null)
			return dateStr;
		return dateTimeTips(dt);
	}

	public final static String dateTimeTips(String dateTime) {
		Date dt = parseSimpleDateTime(dateTime); // 转换成日期时间类型
		if (dt == null)
			return dateTime;
		return dateTimeTips(dt);
	}

	/**
	 * java.util.date转换成java.sql.date,适合保存数据库
	 *
	 * @param d
	 * @return java.sql.Date
	 * @author Jason
	 */
	public static java.sql.Date stringToDate(Date d) {
		java.sql.Date sqlDate = new java.sql.Date(d.getTime());
		return sqlDate;
	}

	/**
	 * java.util.date转换成java.sql.Timestamp,适合保存数据库 带时间格式
	 *
	 * @param d
	 * @return java.sql.Timestamp 2011-01-01 12:52:00
	 * @author Jason
	 */
	public static java.sql.Timestamp stringToDateTime(Date d) {
		java.sql.Timestamp time = new java.sql.Timestamp(d.getTime());
		return time;
	}

	/**
	 * Object 转换成java.sql.Timestamp,适合保存数据库 带时间格式
	 *
	 * @param o
	 * @return java.sql.Timestamp 2011-01-01 12:52:00
	 * @author Jason
	 */
	public static java.sql.Timestamp stringToDateTime(Object o) {
		return stringToDateTime(parseSimpleDateTime(StringUtil.toString(o)));
	}

	/**
	 * String 2010-12-25转换成java.sql.date,适合保存数据库
	 *
	 * @param date
	 * @return java.sql.Date
	 * @author Jason
	 */
	public static java.sql.Date stringToDate(String date) {
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		try {
			d = bartDateFormat.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		java.sql.Date sqlDate = new java.sql.Date(d.getTime());
		return sqlDate;
	}

	/**
	 * 返回2个日期之间相差几个日
	 *
	 * @param D1 开始日期（日期类型）
	 * @param D2 结束日期（日期类型）
	 * @return 相差的天数 廖大剑
	 */
	public static int getTowDateDays(Date D1, Date D2) {
		int returnValue = 0;
		long aL = 0, oneday = 3600 * 24 * 1000;
		aL = D2.getTime() - D1.getTime();
		returnValue = Integer.parseInt(aL / oneday + "");
		return returnValue;
	}

	/**
	 * 返回2个日期之间相差几个日
	 *
	 * @param D1 开始日期（日期类型）
	 * @param D2 结束日期（日期类型）
	 * @return 相差的小时 取值俩个小数点
	 */
	public static String getTowDateHours(Date D1, Date D2) {
		float aL = 0, oneday = (long) (3600  * 1000.0);
		aL = D2.getTime() - D1.getTime();
		DecimalFormat df = new DecimalFormat("0.00");
		return  df.format(aL / oneday);
	}

	/**
	 * 格式化时间(日期)
	 *
	 * @param pattern 模式(如yyyy-MM-dd)
	 * @param date    java.util.Date对象
	 * @return 格式化后的时间字符串
	 */
	public static String formatDate(String pattern, Date date) {
		String dateStr = "";
		try {
			dateStr = new SimpleDateFormat(pattern, Locale.US).format(date);
		} catch (Exception e) {
			return dateStr;
		}
		return dateStr;
	}


	/**
	 * 获取昨天日期
	 *
	 * @return
	 * @author 廖大剑
	 */
	public static String getYesterDay() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		Date d = cal.getTime();

		SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
		return sp.format(d);// 获取昨天日期

	}

	/**
	 * 清除时间格式，如 12:00-13:00;返回出来是12001300 ; 月日时分09-02 12:00/09-05 18:00 返回出来的是
	 * 0902120009051800
	 *
	 * @param dateTime
	 * @return
	 * @author 廖大剑
	 */
	public static String clearFormat(String dateTime) {
		if (dateTime == null || dateTime.equalsIgnoreCase("")) {
			return dateTime;
		}
		dateTime = dateTime.replaceAll("[//:-[ ]]", "");
		return dateTime;
	}

	/**
	 * 日期相加
	 *
	 * @param date 要加的日期
	 * @param day  相加的天数
	 * @return 相加后的日期 注：2006-1-1 返回为2006-01-01
	 */
	public static String AddDate(Date date, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_YEAR,
				(calendar.get(Calendar.DAY_OF_YEAR) + day));
		return getDateToString(calendar.getTime());
	}

	/**
	 * 日期相加
	 *
	 * @param sD  要加的日期（字符串）
	 * @param day 相加的天数
	 * @return 相加后的日期
	 */
	public static String AddDate(String sD, int day) {
		return AddDate(parseSimpleDate(sD), day);
	}

	/**
	 * 根据difnum获取日期 0:获取当前 -1:获取1天前 1:获取一天后
	 *
	 * @param difnum
	 * @return
	 */
	public static String getToday(int difnum) {
		// TODO Auto-generated method stub
		Date myDate = new Date();
		long myTime = (myDate.getTime() / 1000) + (difnum * 60 * 24 * 365);
		myDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd");
		String mDate = formatter.format(myDate);
		myTime = (myDate.getTime() / 1000) + (difnum * 60 * 60 * 24);
		myDate.setTime(myTime * 1000);
		mDate = formatter.format(myDate);
		return mDate;
	}

	/**
	 * 根据difnum获取日期 0:获取当前 -1:获取1天前 1:获取一天后
	 *
	 * @param difnum
	 * @return
	 */
	public static String getBirthday(int difnum) {
		// TODO Auto-generated method stub
		Date myDate = new Date();
		long myTime = (myDate.getTime() / 1000) + (difnum * 60 * 24 * 365);
		myDate = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat(
				"MM-dd");
		String mDate = formatter.format(myDate);
		myTime = (myDate.getTime() / 1000) + (difnum * 60 * 60 * 24);
		myDate.setTime(myTime * 1000);
		mDate = formatter.format(myDate);
		return mDate;
	}

	// 取得格式化效果的系统日期！ 格式如：yyyy-MM-dd kk:mm:ss
	public final static String getFormateDate(Date date, String formate) {
		SimpleDateFormat f = new SimpleDateFormat(formate, Locale.US);
		return f.format(date);
	}


	/**
	 * 获取某年第一天日期
	 *
	 * @param date
	 * @return Date
	 */
	public static Date getCurrYearFirst(Date date) {
		int year = StringUtil.parseInt(getFormateDate(date, "yyyy"), 2010);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 获取某年第一天日期
	 * @param date
	 * @return Date
	 */
	public static Date getCurrYearFirst(String date) {
		int year = StringUtil.parseInt(getFormateDate(DateUtil.parseSimpleDateTime(date), "yyyy"), 2010);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date currYearFirst = calendar.getTime();
		return currYearFirst;
	}

	/**
	 * 获取某年最后一天日期
	 * @param date
	 * @return Date
	 */
	public static Date getCurrYearLast(Date date) {
		int year = StringUtil.parseInt(getFormateDate(date, "yyyy"), 2010);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();
		return DateUtil.parseSimpleDateTime(DateUtil.getDateToString(currYearLast) + " 23:59:59");
	}

	/**
	 * 获取某年最后一天日期
	 * @param date
	 * @return Date
	 */
	public static Date getCurrYearLast(String date) {
		int year = StringUtil.parseInt(getFormateDate(DateUtil.parseSimpleDateTime(date), "yyyy"), 2010);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();
		return DateUtil.parseSimpleDateTime(DateUtil.getDateToString(currYearLast) + " 23:59:59");

	}

	/**
	 * 获取某月第一天日期
	 * @param date
	 * @return string  string 2012-01-01
	 */
	public static String getCurrMonthFirst(String date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.parseSimpleDateTime(date));
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		String dates = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-01";
		return dates;
	}

	/**
	 * 获取某月第一天日期
	 * @param date
	 * @return string 2012-01-31
	 */
	public static String getCurrMonthFirst(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		String dates = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-01";
		return dates;
	}

	/**
	 * 获取某月最后一天日期
	 * @param date
	 * @return string  string 2010-01-31
	 */
	public static String getCurrMonthLast(String date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(DateUtil.parseSimpleDateTime(date));
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		int endday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		String dates = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + endday;
		return dates;
	}

	/**
	 * 获取某月最后一天日期
	 * @param date
	 * @return string 2012-01-31
	 */
	public static String getCurrMonthLast(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
		int endday = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		String dates = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + endday;
		return dates;
	}


	/**
	 * 返回两个date对象是否为同一天
	 * @param d1
	 * @param d2
	 * @return true表示同一天，false非同一天
	 */
	public static boolean isTheSameDay(Date d1, Date d2) {

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(d1);
		c2.setTime(d2);
		return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR))
				&& (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH))
				&& (c1.get(Calendar.DAY_OF_MONTH) == c2
				.get(Calendar.DAY_OF_MONTH));

	}


	/****
	 * 加减月份
	 *
	 * @param date 日期(2014-04-20)
	 * @return 2014-03-20
	 * @throws java.text.ParseException
	 */
	public static String subMonth(String date, int month) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = sdf.parse(date);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(dt);

			rightNow.add(Calendar.MONTH, -month);
			Date dt1 = rightNow.getTime();
			String reStr = sdf.format(dt1);

			return reStr;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/****
	 * 加减年份
	 *
	 * @param date 日期(2014-04-20)
	 * @return 2014-03-20
	 * @throws java.text.ParseException
	 */
	public static String subYear(String date, int year) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = sdf.parse(date);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(dt);

			rightNow.add(Calendar.YEAR, year);
			Date dt1 = rightNow.getTime();
			String reStr = sdf.format(dt1);
			return reStr;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/****
	 * 加减周
	 *
	 * @param date 日期(2014-04-20)
	 * @return 2014-03-20
	 * @throws java.text.ParseException
	 */
	public static String subDay(String date, int day) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = sdf.parse(date);
			Calendar rightNow = Calendar.getInstance();
			rightNow.setTime(dt);

			rightNow.add(Calendar.DAY_OF_WEEK, day);
			Date dt1 = rightNow.getTime();
			String reStr = sdf.format(dt1);
			return reStr;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}


	// 测试
	public static void main(String[] args) {

		//System.out.println(subWeek("2016-01-18", 10));
	}


}
