package com.grace.frame.util;

import java.math.BigDecimal;

import com.grace.frame.exception.AppException;

/**
 * 数字相关的操作封装
 * 
 * @author yjc
 */
public class MathUtil{

	/**
	 * 是否为奇数
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public static boolean isOdd(int para) {
		return (para % 2 == 1);
	}

	/**
	 * 是否为偶数
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public static boolean isEven(int para) {
		return !MathUtil.isOdd(para);// 不是奇数就是偶数
	}

	/**
	 * 得到绝对值
	 * 
	 * @param num
	 * @return
	 * @throws AppException
	 */
	public static double abs(double num) {
		if (num < 0) {
			return -1 * num;
		}
		return num;
	}

	/**
	 * 截取double指定位数函数
	 * 
	 * @param double d 原double
	 * @param int i 小数位
	 * @return double
	 */
	public static double truncate(double d, int i) {
		double tmp = Math.pow(10, i);
		return Math.floor(d * tmp) / tmp;
	}

	/**
	 * 四舍五入函数
	 * 
	 * @param double d 原double
	 * @param int i 小数位
	 * @return double
	 * @throws AppException
	 */
	public static double round(double v, int scale) throws AppException {
		if (scale < 0) {
			throw new AppException("精确度不允许小于0");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		double i = b.divide(one, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
		b = new BigDecimal(Double.toString(i));
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 判定一个字符串是不是数值，包括long ,double, 科学计数法
	 * 
	 * @param numberString
	 * @return
	 */
	public static boolean isNumber(String numberString) {
		try {
			Double.parseDouble(numberString);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}
}
