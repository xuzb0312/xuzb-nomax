package com.grace.frame.util;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * 特殊权限工具类
 * 
 * @author yjc
 */
public class SpRightUtil{

	/**
	 * 检测用户是否存在特殊权限
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public static boolean chkSpRight(String tsqxid, String yhid, boolean defalut) throws Exception {
		if (StringUtil.chkStrNull(tsqxid)) {
			throw new AppException("传入的特殊权限ID为空。");
		}
		if (StringUtil.chkStrNull(yhid)) {
			throw new AppException("传入的用户ID为空。");
		}
		Sql sql = new Sql();

		// 判断是否存在该特殊权限的配置
		sql.setSql(" select tsqxid from fw.special_right where tsqxid = ? and dbid = ? ");
		sql.setString(1, tsqxid);
		sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() <= 0) {
			// 如果没有有配置,则返回defalut
			return defalut;
		}

		// 获取用户的类型
		String yhlx = "B";
		sql.setSql(" select a.yhlx from fw.sys_user a where a.yhid = ? ");
		sql.setString(1, yhid);
		dsTemp = sql.executeQuery();
		if (dsTemp.size() > 0) {
			yhlx = dsTemp.getString(0, "yhlx");
		}
		if ("A".equals(yhlx)) {
			return true;// 超级管理员不进行判断，默认全部有
		}

		// 判断是否存在
		sql.setSql(" select a.yhid from fw.user_sp_right a where a.tsqxid = ? and a.yhid = ? ");
		sql.setString(1, tsqxid);
		sql.setString(2, yhid);
		dsTemp = sql.executeQuery();
		if (dsTemp.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检测用户是否存在特殊权限
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public static boolean chkSpRight(String tsqxid, String yhid) throws Exception {
		return SpRightUtil.chkSpRight(tsqxid, yhid, true);// 如果没有配置，则不检验权限
	}
}
