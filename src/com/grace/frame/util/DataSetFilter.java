package com.grace.frame.util;

/**
 * 数据过滤器
 * 
 * @author yjc
 */
public interface DataSetFilter{
	public boolean filter(DataMap para) throws Exception;// 筛选操作-返回true满足条件，否则不满足条件
}
