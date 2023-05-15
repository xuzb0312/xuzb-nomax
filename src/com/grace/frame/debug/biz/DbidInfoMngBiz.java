package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

public class DbidInfoMngBiz extends Biz{

	/**
	 * 获取dbid的配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public final DataMap getDbidInfoDs(final DataMap para) throws Exception {
		this.sql.setSql(" select dbid, dbmc, bz, appname from fw.dbid_info ");
		DataSet ds = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("dbidinfo", ds);
		return dm;
	}

	/**
	 * DBID信息的修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public final DataMap fwdDbidInfoModify(final DataMap para) throws Exception {
		String dbid = para.getString("dbid");
		if (StringUtil.chkStrNull(dbid)) {
			throw new BizException("传入的DBID为空。");
		}
		this.sql.setSql(" select dbid, dbmc, bz, appname from fw.dbid_info where dbid = ? ");
		this.sql.setString(1, dbid);
		DataSet ds = this.sql.executeQuery();
		if (ds.size() <= 0) {
			throw new BizException("根据传入的DBID=" + dbid + "无法获取到数据。");
		}

		DataMap dm = new DataMap();
		dm.put("dbidinfo", ds.getRow(0));
		return dm;
	}

	/**
	 * 保存dbid信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public final DataMap saveDbidInfoAdd(final DataMap para) throws Exception {
		String dbid = para.getString("dbid");
		String dbmc = para.getString("dbmc");
		String bz = para.getString("bz");
		String appname = para.getString("appname");

		if (StringUtil.chkStrNull(dbid)) {
			throw new BizException("DBID为空");
		}
		if (StringUtil.chkStrNull(dbmc)) {
			throw new BizException("DBMC为空");
		}

		// 检查dbid是否存在
		this.sql.setSql(" select dbid from fw.dbid_info where dbid = ? ");
		this.sql.setString(1, dbid);
		DataSet ds = this.sql.executeQuery();
		if (ds.size() > 0) {
			throw new BizException("该dbid已经存在，无法新增");
		}

		this.sql.setSql(" insert into fw.dbid_info(dbid, dbmc, bz, appname) values(?, ?, ?, ?) ");
		this.sql.setString(1, dbid);
		this.sql.setString(2, dbmc);
		this.sql.setString(3, bz);
		this.sql.setString(4, appname);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 保存dbid信息修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public final DataMap saveDbidInfoModify(final DataMap para) throws Exception {
		String dbid = para.getString("dbid");
		String dbmc = para.getString("dbmc");
		String bz = para.getString("bz");
		String appname = para.getString("appname");

		if (StringUtil.chkStrNull(dbid)) {
			throw new BizException("DBID为空");
		}
		if (StringUtil.chkStrNull(dbmc)) {
			throw new BizException("DBMC为空");
		}

		this.sql.setSql(" update fw.dbid_info set dbmc = ?, bz = ?, appname = ? where dbid = ? ");
		this.sql.setString(1, dbmc);
		this.sql.setString(2, bz);
		this.sql.setString(3, appname);
		this.sql.setString(4, dbid);
		this.sql.executeUpdate();

		// 重置系统的DBName
		GlobalVarsUtil.reloadSYS_DBName();
		GlobalVarsUtil.adjustAppName();

		return null;
	}
}
