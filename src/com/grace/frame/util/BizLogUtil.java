package com.grace.frame.util;

import java.util.Date;

import com.grace.frame.exception.AppException;

/**
 * 日志操作
 * 
 * @author yjc
 */
public class BizLogUtil{
	/**
	 * 保存业务日志信息，返回日志id
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	public static String saveBizLog(String czlx, String czmc, String ztlx,
			String ztid, String ztbh, String ztmc, String czsm, String data,
			String czip, String czyid, long hs) throws AppException {
		String rzid = SeqUtil.getId("fw.sq_rzid");
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();

		// 插入数据库的方法
		sqlBF.setLength(0);
		sqlBF.append(" insert into fw.biz_log ");
		sqlBF.append("   (rzid, czlx, czmc, ztlx, ztid, ");
		sqlBF.append("    ztbh, ztmc, czsm, data, czsj, ");
		sqlBF.append("    czip, czyid, hs) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?, ?, ?, sysdate, ");
		sqlBF.append("    ?, ?, ?) ");

		sql.setSql(sqlBF.toString());
		sql.setString(1, rzid);
		sql.setString(2, czlx);
		sql.setString(3, czmc);
		sql.setString(4, ztlx);
		sql.setString(5, ztid);

		sql.setString(6, ztbh);
		sql.setString(7, ztmc);
		sql.setString(8, czsm);
		sql.setString(9, data);
		sql.setString(10, czip);

		sql.setString(11, czyid);
		sql.setInt(12, (int) hs);

		sql.executeUpdate();

		return rzid;
	}

	/**
	 * 保存业务日志信息，返回日志id
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	public static String saveBizLog(String czlx, String czmc, String ztlx,
			String ztid, String ztbh, String ztmc, String czsm, String data,
			String czip, String czyid, Date beginTime) throws AppException {
		long hs = (new Date()).getTime() - beginTime.getTime();
		return BizLogUtil.saveBizLog(czlx, czmc, ztlx, ztid, ztbh, ztmc, czsm, data, czip, czyid, hs);
	}
}
