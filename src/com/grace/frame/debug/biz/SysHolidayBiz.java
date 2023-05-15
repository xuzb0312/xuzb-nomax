package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 节假日的管理
 * 
 * @author yjc
 */
public class SysHolidayBiz extends Biz{
	/**
	 * 进入节假日的管理页面
	 * 
	 * @author yjc
	 * @date 2016-1-14 下午04:47:48
	 * @since V1.0
	 */
	public final DataMap fwdSysHolidayMng(final DataMap para) throws Exception {
		DataMap dm = new DataMap();
		dm.put("dqnd", DateUtil.dateToString(DateUtil.getDBTime(), "yyyy"));
		return dm;
	}

	/**
	 * 查询系统的节假日信息
	 * 
	 * @author yjc
	 * @date 创建时间 2016-3-2
	 * @since V1.0
	 */
	public final DataMap querySysHolidayInfo(final DataMap para) throws Exception {
		String nd = para.getString("nd");
		if (StringUtil.chkStrNull(nd)) {
			throw new BizException("传入的年度信息为空");
		}

		StringBuffer jjrqsBF = new StringBuffer();
		this.sql.setSql(" select jjrq from fw.sys_holiday where dbid = ? and substr(jjrq, 1, 4) = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, nd);
		DataSet ds = this.sql.executeQuery();
		for (int i = 0, n = ds.size(); i < n; i++) {
			jjrqsBF.append(ds.getString(i, "jjrq")).append(",");
		}
		if (jjrqsBF.length() > 0) {
			jjrqsBF.setLength(jjrqsBF.length() - 1);
		}

		DataMap dm = new DataMap();
		dm.put("jjrqs", jjrqsBF.toString());
		return dm;
	}

	/**
	 * 保存系统的节假日信息
	 * 
	 * @author yjc
	 * @date 创建时间 2016-3-2
	 * @since V1.0
	 */
	public final DataMap saveSysHolidayInfo(final DataMap para) throws Exception {
		String nd = para.getString("nd");
		String jjrqs = para.getString("jjrqs");
		if (StringUtil.chkStrNull(nd)) {
			throw new BizException("传入的年度信息为空");
		}
		if (StringUtil.chkStrNull(jjrqs)) {
			jjrqs = "";
		}

		// 删除
		this.sql.setSql(" delete from fw.sys_holiday where dbid = ? and substr(jjrq, 1, 4) = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, nd);
		this.sql.executeUpdate();

		// 插入
		String[] jjrqArr = jjrqs.split(",");
		this.sql.setSql(" insert into fw.sys_holiday(dbid, jjrq, bz) values(?, ?, null) ");
		for (int i = 0, n = jjrqArr.length; i < n; i++) {
			String jjrq = jjrqArr[i];
			if (StringUtil.chkStrNull(jjrq)) {
				continue;
			}
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, jjrq);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-B-XXJJRWH", "系统节假日维护", "B", "", "维护" + nd + "年度的系统节假日信息", "nd="
				+ nd);

		return null;
	}
}
