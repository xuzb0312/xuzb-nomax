package com.grace.frame.util;

/**
 * DataSet的union的自定义实现
 * 
 * @author yjc
 */
public interface DataSetUnion{
	public DataMap union(DataMap one, DataMap two, boolean isOneExist,
			boolean isTowExist) throws Exception;
}
