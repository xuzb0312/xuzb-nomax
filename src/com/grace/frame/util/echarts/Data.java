package com.grace.frame.util.echarts;

/**
 * @author yjc
 */
public interface Data<T> {
	/**
	 * 添加元素
	 * 
	 * @param values
	 * @return
	 */
	T data(Object... values);
}
