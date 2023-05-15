package com.grace.frame.util;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * 系统参数操作类
 * 
 * @author yjc
 */
public class SysParaUtil{

	/**
	 * 获取系统参数
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	public static String getPara(String csbh) throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select csz from fw.sys_para where csbh = ? and dbid = ? ");
		sql.setString(1, csbh);
		sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new AppException("该系统参数" + csbh + "未进行配置。");
		}

		return dsTemp.getString(0, "csz");
	}

	/**
	 * 获取系统参数-有默认值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-9
	 * @since V1.0
	 */
	public static String getPara(String csbh, String defalut) throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select csz from fw.sys_para where csbh = ? and dbid = ? ");
		sql.setString(1, csbh);
		sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() <= 0) {
			return defalut;
		}

		return dsTemp.getString(0, "csz");
	}
}
