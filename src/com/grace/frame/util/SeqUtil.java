package com.grace.frame.util;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * sequence的操作类
 * 
 * @author yjc
 */
public class SeqUtil{

	/**
	 * 获取某一个seq
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	public static String getSeqByName(String seq) throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select to_char(" + seq + ".nextval) seq from sys.dual ");
		DataSet ds = sql.executeQuery();
		return ds.getString(0, "seq");
	}

	/**
	 * 获取系统默认的标准20位ID
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	public static String getId(String seq) throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select (to_char(sysdate,'yymmdd')||to_char(" + seq
				+ ".nextval)) seq from sys.dual ");
		DataSet ds = sql.executeQuery();
		String id = GlobalVars.SYS_DBID + ds.getString(0, "seq");
		return id;
	}

	/**
	 * 获取系统默认的标准20位ID-日期精确到秒，防止短时重复的情况发生
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	public static String getExactId(String seq) throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select (to_char(sysdate,'yymmddhh24miss')||to_char(" + seq
				+ ".nextval)) seq from sys.dual ");
		DataSet ds = sql.executeQuery();
		String id = GlobalVars.SYS_DBID + ds.getString(0, "seq");
		return id;
	}
}
