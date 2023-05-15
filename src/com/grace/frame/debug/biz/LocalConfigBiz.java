package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 本地化操作
 * 
 * @author yjc
 */
public class LocalConfigBiz extends Biz{

	/**
	 * 进入本地化管理的页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap fwdLocalConfigMng(final DataMap para) throws Exception {
		DataSet dsJbjg = this.getJbjgDs();

		// 增加dbid的信息
		this.sql.setSql(" select dbid code, dbmc || '【DBID:' || dbid || '】' content from fw.dbid_info where dbid = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsDbid = this.sql.executeQuery();
		dsJbjg.combineDataSet(dsDbid);

		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dsjbjg", dsJbjg);
		return rdm;
	}

	/**
	 * 查询本地化配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap queryLocalConfig(final DataMap para) throws Exception {
		String jbjgid = para.getString("jbjgid");
		String bzjm = para.getFilterSqlString("bzjm");
		String bdhm = para.getFilterSqlString("bdhm");

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.jbjgid, b.jbjgbh, b.jbjgmc, a.bzjm, a.bdhm, d.bdhsm ");
		sqlBF.append("   from fw.local_config a, fw.sys_agency b, fw.local_doc d ");
		sqlBF.append("  where a.jbjgid = b.jbjgid ");
		sqlBF.append("    and a.bzjm = d.bzjm ");
		sqlBF.append("    and a.bdhm = d.bdhm ");
		if (StringUtil.chkStrNull(jbjgid)) {
			sqlBF.append("    and exists (select 'x' ");
			sqlBF.append("           from fw.agency_biz_type c ");
			sqlBF.append("          where a.jbjgid = c.jbjgid ");
			sqlBF.append("            and c.dbid = '" + GlobalVars.SYS_DBID
					+ "') ");
		} else {
			sqlBF.append("    and "
					+ StringUtil.replaceC2QCQ("a.jbjgid", jbjgid));
		}
		if (!StringUtil.chkStrNull(bzjm)) {
			sqlBF.append("    and a.bzjm like '%" + bzjm + "%' ");
		}
		if (!StringUtil.chkStrNull(bdhm)) {
			sqlBF.append("    and a.bdhm like '%" + bdhm + "%' ");
		}
		sqlBF.append(" union all ");
		sqlBF.append(" select a.jbjgid, b.dbid jbjgbh, b.dbmc jbjgmc, a.bzjm, a.bdhm, d.bdhsm ");
		sqlBF.append("   from fw.local_config a, fw.dbid_info b, fw.local_doc d ");
		sqlBF.append("  where a.jbjgid = b.dbid ");
		sqlBF.append("    and a.bzjm = d.bzjm ");
		sqlBF.append("    and a.bdhm = d.bdhm ");
		if (StringUtil.chkStrNull(jbjgid)) {
			sqlBF.append("    and a.jbjgid = '" + GlobalVars.SYS_DBID + "' ");
		} else {
			sqlBF.append("    and "
					+ StringUtil.replaceC2QCQ("a.jbjgid", jbjgid));
		}
		if (!StringUtil.chkStrNull(bzjm)) {
			sqlBF.append("    and a.bzjm like '%" + bzjm + "%' ");
		}
		if (!StringUtil.chkStrNull(bdhm)) {
			sqlBF.append("    and a.bdhm like '%" + bdhm + "%' ");
		}
		this.sql.setSql(sqlBF.toString());
		DataSet dsLocal = this.sql.executeQuery();

		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dslocal", dsLocal);
		return rdm;

	}

	/**
	 * 获取经办机构DS
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	private DataSet getJbjgDs() throws Exception {
		StringBuffer sqlBF = new StringBuffer();

		// 获取dsJbjg-所有
		sqlBF.setLength(0);
		sqlBF.append(" select distinct a.jbjgid code, a.jbjgmc content ");
		sqlBF.append("   from fw.sys_agency a, fw.agency_biz_type b ");
		sqlBF.append("  where a.jbjgid = b.jbjgid ");
		sqlBF.append("    and b.dbid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsJbjg = this.sql.executeQuery();
		dsJbjg.sort("code");
		return dsJbjg;
	}

	/**
	 * 进入本地化新增的页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap fwdLocalAddWin(final DataMap para) throws Exception {
		DataSet dsJbjg = this.getJbjgDs();

		// 增加dbid的信息
		this.sql.setSql(" select dbid code, dbmc || '【DBID:' || dbid || '】' content from fw.dbid_info where dbid = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsDbid = this.sql.executeQuery();
		dsJbjg.combineDataSet(dsDbid);

		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dsjbjg", dsJbjg);
		return rdm;
	}

	/**
	 * 新增本地化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap saveLocalConfigAdd(final DataMap para) throws Exception {
		String bzjm = para.getString("bzjm");
		String bdhm = para.getString("bdhm");
		String jbjgid = para.getString("jbjgid");
		String bdhsm = para.getString("bdhsm");

		if (StringUtil.chkStrNull(bzjm)) {
			throw new BizException("标准件名为空");
		}
		if (StringUtil.chkStrNull(bdhm)) {
			throw new BizException("本地化名为空");
		}
		if (StringUtil.chkStrNull(jbjgid)) {
			throw new BizException("经办机构为空");
		}

		// 检测该本地化是否已经存在
		this.sql.setSql(" select jbjgid from fw.local_config where "
				+ StringUtil.replaceC2QCQ("jbjgid", jbjgid) + "and bzjm = ? ");
		this.sql.setString(1, bzjm);
		DataSet dsTmp = this.sql.executeQuery();
		if (dsTmp.size() > 0) {
			StringBuffer hasJbjgidsBF = new StringBuffer();
			for (int i = 0, n = dsTmp.size(); i < n; i++) {
				hasJbjgidsBF.append(dsTmp.getString(i, "jbjgid")).append(",");
			}
			hasJbjgidsBF.setLength(hasJbjgidsBF.length() - 1);
			throw new BizException("该本地化配置在以下经办机构【" + hasJbjgidsBF.toString()
					+ "】中已经存在相应配置，无法新增。");
		}

		// 记录doc数据
		this.sql.setSql(" delete from fw.local_doc where bzjm = ? and bdhm = ? ");
		this.sql.setString(1, bzjm);
		this.sql.setString(2, bdhm);
		this.sql.executeUpdate();

		this.sql.setSql(" insert into fw.local_doc (bzjm, bdhm, bdhsm) values (?, ?, ?) ");
		this.sql.setString(1, bzjm);
		this.sql.setString(2, bdhm);
		this.sql.setString(3, bdhsm);
		this.sql.executeUpdate();

		// config数据的新增
		String[] arrJbjg = jbjgid.split(",");
		this.sql.setSql(" insert into fw.local_config (jbjgid, bzjm, bdhm) values (?, ?, ?) ");
		for (int i = 0, n = arrJbjg.length; i < n; i++) {
			this.sql.setString(1, arrJbjg[i]);
			this.sql.setString(2, bzjm);
			this.sql.setString(3, bdhm);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 重新加载缓存
		GlobalVarsUtil.reloadLOCAL_CONFIG_MAP();

		return null;
	}

	/**
	 * 进入本地化选择窗口
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap fwdSearchLocalDoc(final DataMap para) throws Exception {
		String bzjm = para.getString("bzjm");
		StringBuffer sqlBF = new StringBuffer();
		bzjm = "%" + bzjm + "%";

		sqlBF.setLength(0);
		sqlBF.append(" select bzjm, bdhm, bdhsm ");
		sqlBF.append("   from fw.local_doc ");
		sqlBF.append("  where (bzjm like ? ");
		sqlBF.append("     or bdhm like ?) ");
		sqlBF.append("    and rownum <= 500 ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, bzjm);
		this.sql.setString(2, bzjm);
		DataSet dsLocalDoc = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dslocaldoc", dsLocalDoc);
		return rdm;
	}

	/**
	 * 删除本地化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap deleteLocalConfig(final DataMap para) throws Exception {
		DataSet dsDel = para.getDataSet("gridlocal");
		if (dsDel == null || dsDel.size() <= 0) {
			throw new BizException("传入的本地化删除信息为空");
		}

		this.sql.setSql(" delete from fw.local_config where jbjgid = ? and bzjm = ? ");
		for (int i = 0, n = dsDel.size(); i < n; i++) {
			String jbjgid = dsDel.getString(i, "jbjgid");
			String bzjm = dsDel.getString(i, "bzjm");

			this.sql.setString(1, jbjgid);
			this.sql.setString(2, bzjm);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 重新加载缓存
		GlobalVarsUtil.reloadLOCAL_CONFIG_MAP();

		return null;
	}

	/**
	 * 进入删除本地化DOC
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap fwdDealNoExistsLocalConfigDoc(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.bzjm, a.bdhm, a.bdhsm ");
		sqlBF.append("   from fw.local_doc a ");
		sqlBF.append("  where not exists (select 'x' ");
		sqlBF.append("           from fw.local_config b ");
		sqlBF.append("          where a.bzjm = b.bzjm ");
		sqlBF.append("            and a.bdhm = b.bdhm) ");

		this.sql.setSql(sqlBF.toString());
		DataSet dsDoc = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("dsdoc", dsDoc);
		return dm;
	}

	/**
	 * 删除本地化DOC
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap deleteNoExistsLocalConfigDoc(final DataMap para) throws Exception {
		DataSet dsDel = para.getDataSet("griddoc");

		// 数据自我纠正
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" delete from fw.local_doc a ");
		sqlBF.append("  where not exists (select 'x' ");
		sqlBF.append("           from fw.local_config b ");
		sqlBF.append("          where a.bzjm = b.bzjm ");
		sqlBF.append("            and a.bdhm = b.bdhm) ");
		sqlBF.append("    and bzjm = ?  ");
		sqlBF.append("    and bdhm = ?  ");

		this.sql.setSql(sqlBF.toString());
		for (int i = 0, n = dsDel.size(); i < n; i++) {
			String bzjm = dsDel.getString(i, "bzjm");
			String bdhm = dsDel.getString(i, "bdhm");
			this.sql.setString(1, bzjm);
			this.sql.setString(2, bdhm);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		return null;
	}
}
