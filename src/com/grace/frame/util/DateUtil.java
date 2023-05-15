package com.grace.frame.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * 日期时间的操作处理类
 * 
 * @author yjc
 */
public class DateUtil{
	/**
	 * 变更日期格式
	 * 
	 * @author yjc
	 * @date 创建时间 2016-2-2
	 * @since V1.0
	 */
	public static String changeDateFormat(String dateStr, String sourceFormat,
			String targetFormat) throws AppException {
		Date date = DateUtil.stringToDate(dateStr, sourceFormat);
		return DateUtil.dateToString(date, targetFormat);
	}

	/**
	 * 将字符串转化为日期. 要求传入6位(yyyyMM)或者8位(yyyyMMdd)的参数
	 * 
	 * @param String
	 * @return Date
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public static Date stringToDate(String dateString) throws AppException {
		Date vdate = null;
		String vformat = null;
		if (dateString == null)
			return null;
		if (dateString.length() != 4 && dateString.length() != 6
				&& dateString.length() != 7 && dateString.length() != 8
				&& dateString.length() != 10 && dateString.length() != 14
				&& dateString.length() != 19) {
			throw new AppException("[时间串]输入格式错误,请输入合法的日期格式!", "DateUtil");
		}
		if (dateString.length() == 4) {
			vformat = "yyyy";
		} else if (dateString.length() == 6) {
			vformat = "yyyyMM";
		} else if (dateString.length() == 7) {
			dateString = dateString.substring(0, 4)
					+ dateString.substring(5, 7);
			vformat = "yyyyMM";
		} else if (dateString.length() == 8) {
			vformat = "yyyyMMdd";
		} else if (dateString.length() == 10) {
			dateString = dateString.substring(0, 4)
					+ dateString.substring(5, 7) + dateString.substring(8, 10);
			vformat = "yyyyMMdd";
		} else if (dateString.length() == 14) {
			vformat = "yyyyMMddHHmmss";
		} else if (dateString.length() == 19) {
			vformat = "yyyy-MM-dd HH:mm:ss";
		}
		vdate = stringToDate(dateString, vformat);
		return vdate;
	}

	/**
	 * 将字符串转成时间
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public static Date stringToDate(String dateString, String format) throws AppException {
		if (dateString == null)
			return null;
		if (dateString.equalsIgnoreCase(""))
			throw new AppException("传入参数中的[时间串]为空", "DateUtil");
		if (format == null || format.equalsIgnoreCase("")) {
			throw new AppException("传入参数中的[时间格式]为空", "DateUtil");
		}
		HashMap<Integer, String> h = new HashMap<Integer, String>();
		String javaFormat = new String();
		if (format.indexOf("yyyy") != -1)
			h.put(new Integer(format.indexOf("yyyy")), "yyyy");
		else if (format.indexOf("yy") != -1)
			h.put(new Integer(format.indexOf("yy")), "yy");
		if (format.indexOf("MM") != -1)
			h.put(new Integer(format.indexOf("MM")), "MM");
		else if (format.indexOf("mm") != -1)
			h.put(new Integer(format.indexOf("mm")), "MM");
		if (format.indexOf("dd") != -1)
			h.put(new Integer(format.indexOf("dd")), "dd");
		if (format.indexOf("hh24") != -1)
			h.put(new Integer(format.indexOf("hh24")), "HH");
		else if (format.indexOf("hh") != -1) {
			h.put(new Integer(format.indexOf("hh")), "HH");
		} else if (format.indexOf("HH") != -1) {
			h.put(new Integer(format.indexOf("HH")), "HH");
		}
		if (format.indexOf("mi") != -1)
			h.put(new Integer(format.indexOf("mi")), "mm");
		else if (format.indexOf("mm") != -1 && h.containsValue("HH"))
			h.put(new Integer(format.lastIndexOf("mm")), "mm");
		if (format.indexOf("ss") != -1)
			h.put(new Integer(format.indexOf("ss")), "ss");
		if (format.indexOf("SSS") != -1)
			h.put(new Integer(format.indexOf("SSS")), "SSS");

		for (int intStart = 0; format.indexOf("-", intStart) != -1; intStart++) {
			intStart = format.indexOf("-", intStart);
			h.put(new Integer(intStart), "-");
		}
		for (int intStart = 0; format.indexOf(".", intStart) != -1; intStart++) {
			intStart = format.indexOf(".", intStart);
			h.put(new Integer(intStart), ".");
		}
		for (int intStart = 0; format.indexOf("/", intStart) != -1; intStart++) {
			intStart = format.indexOf("/", intStart);
			h.put(new Integer(intStart), "/");
		}
		for (int intStart = 0; format.indexOf(" ", intStart) != -1; intStart++) {
			intStart = format.indexOf(" ", intStart);
			h.put(new Integer(intStart), " ");
		}
		for (int intStart = 0; format.indexOf(":", intStart) != -1; intStart++) {
			intStart = format.indexOf(":", intStart);
			h.put(new Integer(intStart), ":");
		}
		if (format.indexOf("年") != -1)
			h.put(new Integer(format.indexOf("年")), "年");
		if (format.indexOf("月") != -1)
			h.put(new Integer(format.indexOf("月")), "月");
		if (format.indexOf("日") != -1)
			h.put(new Integer(format.indexOf("日")), "日");
		if (format.indexOf("时") != -1)
			h.put(new Integer(format.indexOf("时")), "时");
		if (format.indexOf("分") != -1)
			h.put(new Integer(format.indexOf("分")), "分");
		if (format.indexOf("秒") != -1)
			h.put(new Integer(format.indexOf("秒")), "秒");
		int i = 0;
		while (h.size() != 0) {
			Iterator<Integer> e = h.keySet().iterator();
			int n = 0;
			while (e.hasNext()) {
				i = ((Integer) e.next()).intValue();
				if (i >= n)
					n = i;
			}
			String temp = (String) h.get(new Integer(n));
			h.remove(new Integer(n));
			javaFormat = temp + javaFormat;
		}
		SimpleDateFormat df = new SimpleDateFormat(javaFormat);
		df.setLenient(false);// 这个的功能是不把1996-13-3 转换为1997-1-3
		Date myDate = new Date();
		try {
			myDate = df.parse(dateString);
		} catch (ParseException e) {
			throw new AppException("日期格式转换错误!将dateString转换成时间时出错", "DateUtil");
		}
		return myDate;
	}

	/**
	 * 将时间转化成字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public static String dateToString(Date date) throws AppException {
		return dateToString(date, "yyyyMMddHHmmss");
	}

	/**
	 * 将时间转化成字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public static String dateToString(Date date, String format) throws AppException {
		if (date == null) {
			return null;
		}
		if (StringUtil.chkStrNull(format)) {
			throw new AppException("传入参数中的[时间格式]为空", "DateUtil");
		}

		HashMap<Integer, String> h = new HashMap<Integer, String>();
		String javaFormat = new String();
		if (format.indexOf("yyyy") != -1)
			h.put(new Integer(format.indexOf("yyyy")), "yyyy");
		else if (format.indexOf("yy") != -1)
			h.put(new Integer(format.indexOf("yy")), "yy");
		if (format.indexOf("MM") != -1)
			h.put(new Integer(format.indexOf("MM")), "MM");
		else if (format.indexOf("mm") != -1)
			h.put(new Integer(format.indexOf("mm")), "MM");
		if (format.indexOf("dd") != -1)
			h.put(new Integer(format.indexOf("dd")), "dd");
		if (format.indexOf("hh24") != -1)
			h.put(new Integer(format.indexOf("hh24")), "HH");
		else if (format.indexOf("hh") != -1) {
			h.put(new Integer(format.indexOf("hh")), "HH");
		} else if (format.indexOf("HH") != -1) {
			h.put(new Integer(format.indexOf("HH")), "HH");
		}
		if (format.indexOf("mi") != -1)
			h.put(new Integer(format.indexOf("mi")), "mm");
		else if (format.indexOf("mm") != -1 && h.containsValue("HH"))
			h.put(new Integer(format.lastIndexOf("mm")), "mm");
		if (format.indexOf("ss") != -1)
			h.put(new Integer(format.indexOf("ss")), "ss");
		if (format.indexOf("SSS") != -1)
			h.put(new Integer(format.indexOf("SSS")), "SSS");

		for (int intStart = 0; format.indexOf("-", intStart) != -1; intStart++) {
			intStart = format.indexOf("-", intStart);
			h.put(new Integer(intStart), "-");
		}
		for (int intStart = 0; format.indexOf(".", intStart) != -1; intStart++) {
			intStart = format.indexOf(".", intStart);
			h.put(new Integer(intStart), ".");
		}
		for (int intStart = 0; format.indexOf("/", intStart) != -1; intStart++) {
			intStart = format.indexOf("/", intStart);
			h.put(new Integer(intStart), "/");
		}

		for (int intStart = 0; format.indexOf(" ", intStart) != -1; intStart++) {
			intStart = format.indexOf(" ", intStart);
			h.put(new Integer(intStart), " ");
		}

		for (int intStart = 0; format.indexOf(":", intStart) != -1; intStart++) {
			intStart = format.indexOf(":", intStart);
			h.put(new Integer(intStart), ":");
		}

		if (format.indexOf("年") != -1)
			h.put(new Integer(format.indexOf("年")), "年");
		if (format.indexOf("月") != -1)
			h.put(new Integer(format.indexOf("月")), "月");
		if (format.indexOf("日") != -1)
			h.put(new Integer(format.indexOf("日")), "日");
		if (format.indexOf("时") != -1)
			h.put(new Integer(format.indexOf("时")), "时");
		if (format.indexOf("分") != -1)
			h.put(new Integer(format.indexOf("分")), "分");
		if (format.indexOf("秒") != -1)
			h.put(new Integer(format.indexOf("秒")), "秒");
		int i = 0;
		while (h.size() != 0) {
			Iterator<Integer> e = h.keySet().iterator();
			int n = 0;
			while (e.hasNext()) {
				i = ((Integer) e.next()).intValue();
				if (i >= n)
					n = i;
			}
			String temp = (String) h.get(new Integer(n));
			h.remove(new Integer(n));
			javaFormat = temp + javaFormat;
		}
		SimpleDateFormat df = new SimpleDateFormat(javaFormat, new DateFormatSymbols());
		return df.format(date);
	}

	/**
	 * 获取数据库时间，建议均使用这个，这样可以对于系统统一时间，即使是多台前置服务器
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	public static Date getDBTime() throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select sysdate time from sys.dual ");
		DataSet ds = sql.executeQuery();
		return ds.getDate(0, "time");
	}

	/**
	 * 根据日期获取属于星期几
	 * 
	 * @author yjc
	 * @date 创建时间 2015-9-26
	 * @since V1.0
	 */
	public static String date2Week(Date date) throws AppException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int iWeek = calendar.get(Calendar.DAY_OF_WEEK);
		String strWeek = "";
		if (1 == iWeek) {
			strWeek = "星期日";
		} else if (2 == iWeek) {
			strWeek = "星期一";
		} else if (3 == iWeek) {
			strWeek = "星期二";
		} else if (4 == iWeek) {
			strWeek = "星期三";
		} else if (5 == iWeek) {
			strWeek = "星期四";
		} else if (6 == iWeek) {
			strWeek = "星期五";
		} else if (7 == iWeek) {
			strWeek = "星期六";
		}
		return strWeek;
	}

	/**
	 * 将传入的Date型参数增加天数
	 * 
	 * @param Date pdate ,
	 * @param int paddmon 增加月数，可以是负数
	 * @return Date
	 */
	public static Date addDay(Date date, int dayNumber) throws AppException {
		if (date == null || dayNumber == 0) {
			return date;
		}
		Calendar vcal = Calendar.getInstance();
		vcal.setTime(date);
		vcal.add(Calendar.DATE, dayNumber);
		date = vcal.getTime();
		return date;
	}

	/**
	 * 将传入的String型参数增加天数，按照格式区分年月
	 * 
	 * @param String pny ,
	 * @param String pformat :可以是6位yyyyMM或者8位yyyyMMdd,
	 * @param int pint 增加月数，可以是负数
	 */
	public static String addDayToString(String dateString, String format,
			int dayNumber) throws AppException {
		Date vdate = stringToDate(dateString, format);
		vdate = DateUtil.addDay(vdate, dayNumber);
		String vdates = dateToString(vdate, format);
		return vdates;
	}

	/**
	 * 根据起始日期，获取结束日期，其中会排除掉节假日。
	 * <p>
	 * 如果dayNumber为0，则返回当天，不进行节假日计算，一定要注意，对于不是0的情况，如果date当天为节假日，
	 * 则自动向前或者向后滚动到非节假日的哪一天。
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2016-3-5
	 * @since V1.0
	 */
	public static Date addDayExceptHoliday(Date date, int dayNumber) throws Exception {
		if (date == null || dayNumber == 0) {
			return date;
		}
		Sql sql = new Sql();
		Date zzrq = DateUtil.addDay(date, dayNumber);

		int jjrqsl = 0;
		do {
			sql.setSql(" select count(jjrq) jjrqsl from fw.sys_holiday where dbid = ? and jjrq between to_char(?, 'yyyymmdd') and to_char(?, 'yyyymmdd') ");
			sql.setString(1, GlobalVars.SYS_DBID);
			if (dayNumber < 0) {
				sql.setDate(2, zzrq);
				sql.setDate(3, date);
			} else {
				sql.setDate(2, date);
				sql.setDate(3, zzrq);
			}
			DataSet dsTemp = sql.executeQuery();
			int jjrqsTemp = dsTemp.getInt(0, "jjrqsl");
			if (jjrqsl == jjrqsTemp) {
				break;
			}
			jjrqsl = jjrqsTemp;
			if (dayNumber < 0) {
				zzrq = DateUtil.addDay(date, dayNumber - jjrqsl);
			} else {
				zzrq = DateUtil.addDay(date, dayNumber + jjrqsl);
			}
		} while (true);

		return zzrq;
	}

	/**
	 * 根据起始日期，获取结束日期，其中会排除掉节假日。
	 * <p>
	 * 如果dayNumber为0，则返回当天，不进行节假日计算，一定要注意，对于不是0的情况，如果date当天为节假日，
	 * 则自动向前或者向后滚动到非节假日的哪一天。
	 * </p>
	 * 
	 * @param String pformat :可以是6位yyyyMM或者8位yyyyMMdd,
	 * @author yjc
	 * @date 创建时间 2016-3-5
	 * @since V1.0
	 */
	public static String addDayExceptHolidayToString(String dateString,
			String format, int dayNumber) throws Exception {
		Date vdate = stringToDate(dateString, format);
		vdate = DateUtil.addDayExceptHoliday(vdate, dayNumber);
		String vdates = dateToString(vdate, format);
		return vdates;
	}

	/**
	 * 求两个日期之间相差的天数
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return long
	 */
	public static long getDaysDiffBetweenTwoDate(Date beginDate, Date endDate) throws AppException {
		if (beginDate == null) {
			throw new AppException("传入参数[开始时间]为空");
		}
		if (endDate == null) {
			throw new AppException("传入参数[结束时间]为空");
		}
		if (beginDate.after(endDate)) {
			throw new AppException("传入参数[开始时间]晚于[结束时间]");
		}
		long ld1 = beginDate.getTime();
		long ld2 = endDate.getTime();
		long days = (long) ((ld2 - ld1) / 86400000);
		return days;
	}

	/**
	 * 求两个日期之间相差的天数（排查节假日）--工作日的天数
	 * 
	 * @param beginDate
	 * @param endDate
	 * @return long
	 */
	public static long getDaysDiffBetweenTwoDateExceptHoliday(Date beginDate,
			Date endDate) throws AppException {
		long days = DateUtil.getDaysDiffBetweenTwoDate(beginDate, endDate);
		if (days == 0) {// 如果同一天的话返回0天
			return days;
		}

		// 排除掉节假日
		Sql sql = new Sql();
		sql.setSql(" select count(jjrq) jjrqsl from fw.sys_holiday where dbid = ? and jjrq between to_char(?, 'yyyymmdd') and to_char(?, 'yyyymmdd') ");
		sql.setString(1, "100");
		sql.setDate(2, beginDate);
		sql.setDate(3, endDate);
		DataSet dsTemp = sql.executeQuery();
		int jjrqsl = dsTemp.getInt(0, "jjrqsl");
		days = days - jjrqsl;
		if (days < 0) {
			days = 0;
		}
		return days;
	}
}
