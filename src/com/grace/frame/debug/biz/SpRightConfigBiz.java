package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 特殊权限管理
 * 
 * @author yjc
 */
public class SpRightConfigBiz extends Biz{
	/**
	 * 刷新特殊权限配置数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap refreshGridSpRightConfig(final DataMap para) throws Exception {
		this.sql.setSql(" select tsqxid, tsqxmc, bz from fw.special_right a where a.dbid = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet ds = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsspright", ds);
		return rdm;
	}

	/**
	 * 刷新特殊权限文档数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap refreshGridSpRightDoc(final DataMap para) throws Exception {
		this.sql.setSql(" select tsqxid, tsqxmc, bz from fw.special_right_doc ");
		DataSet ds = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsspdoc", ds);
		return rdm;
	}

	/**
	 * 保存特殊权限文档新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap saveSpRightDocAdd(final DataMap para) throws Exception {
		String tsqxid = para.getString("tsqxid");// 特殊权限id
		String tsqxmc = para.getString("tsqxmc");// 特殊权限名称
		String bz = para.getString("bz");// 说明

		if (StringUtil.chkStrNull(tsqxmc)) {
			throw new BizException("传入的特殊权限名称不允许为空");
		}
		if (StringUtil.chkStrNull(tsqxid)) {
			throw new BizException("传入的特殊权限ID不允许为空");
		}

		// 检查是否已经存在
		this.sql.setSql(" select tsqxid from fw.special_right_doc where tsqxid = ? ");
		this.sql.setString(1, tsqxid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该特殊权限已经存在不允许重复新增");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.special_right_doc ");
		sqlBF.append("   (tsqxid, tsqxmc, bz) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, tsqxid);
		this.sql.setString(2, tsqxmc);
		this.sql.setString(3, bz);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 进入特殊权限文档修改页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdSpRightDocModify(final DataMap para) throws Exception {
		String tsqxid = para.getString("tsqxid");// 特殊权限Id
		if (StringUtil.chkStrNull(tsqxid)) {
			throw new BizException("传入的特殊权限ID不允许为空");
		}
		this.sql.setSql(" select tsqxid, tsqxmc, bz from fw.special_right_doc where tsqxid = ? ");
		this.sql.setString(1, tsqxid);
		DataSet ds = this.sql.executeQuery();
		if (ds.size() <= 0) {
			throw new BizException("特殊权限ID为" + tsqxid + "的特殊权限文档信息不存在。");
		}

		DataMap rdm = new DataMap();
		rdm.put("formsprightdoc", ds.getRow(0));
		return rdm;
	}

	/**
	 * 保存特殊权限文档修改信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap saveSpRightDocModify(final DataMap para) throws Exception {
		String tsqxid = para.getString("tsqxid");// 特殊权限id
		String tsqxmc = para.getString("tsqxmc");// 特殊权限名称
		String bz = para.getString("bz");// 说明

		if (StringUtil.chkStrNull(tsqxmc)) {
			throw new BizException("传入的特殊权限名称不允许为空");
		}
		if (StringUtil.chkStrNull(tsqxid)) {
			throw new BizException("传入的特殊权限ID不允许为空");
		}

		// DOC调整
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" update fw.special_right_doc a ");
		sqlBF.append("    set a.tsqxmc = ?, a.bz = ? ");
		sqlBF.append("  where a.tsqxid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, tsqxmc);
		this.sql.setString(2, bz);
		this.sql.setString(3, tsqxid);

		this.sql.executeUpdate();

		// Config-调整
		sqlBF.setLength(0);
		sqlBF.append(" update fw.special_right a ");
		sqlBF.append("    set a.tsqxmc = ?, a.bz = ? ");
		sqlBF.append("  where a.tsqxid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, tsqxmc);
		this.sql.setString(2, bz);
		this.sql.setString(3, tsqxid);

		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 删除特殊权限文档信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap deleteSpRightDoc(final DataMap para) throws Exception {
		String tsqxid = para.getString("tsqxid");// 特殊权限Id
		if (StringUtil.chkStrNull(tsqxid)) {
			throw new BizException("传入的特殊权限ID不允许为空");
		}

		// 检测是否可以删除
		this.sql.setSql(" select dbid from fw.special_right where tsqxid = ? ");
		this.sql.setString(1, tsqxid);
		DataSet dsPolling = this.sql.executeQuery();
		if (dsPolling.size() > 0) {
			throw new BizException("特殊权限ID为" + tsqxid
					+ "的特殊权限文档信息在fw.special_right中已经配置给了"
					+ dsPolling.getString(0, "dbid") + ",不允许删除。");
		}

		// 执行删除
		this.sql.setSql(" delete from fw.special_right_doc where tsqxid = ?");
		this.sql.setString(1, tsqxid);
		this.sql.executeUpdate();
		return null;
	}

	/**
	 * 获取doc
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdChooseSpRightDoc(final DataMap para) throws Exception {
		String tsqxid = para.getString("tsqxid", "");
		tsqxid = "%" + tsqxid + "%";

		this.sql.setSql(" select tsqxid, tsqxmc, bz from fw.special_right_doc where tsqxid like ? or tsqxmc like ? ");
		this.sql.setString(1, tsqxid);
		this.sql.setString(2, tsqxid);
		DataSet ds = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dssprightdoc", ds);
		return rdm;
	}

	/**
	 * 保存特殊权限配置新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap saveSpRightConfigAdd(final DataMap para) throws Exception {
		String tsqxid = para.getString("tsqxid");// 特殊权限名称

		if (StringUtil.chkStrNull(tsqxid)) {
			throw new BizException("传入的特殊权限ID不允许为空");
		}

		// 检查tsqxid是否已经存在
		this.sql.setSql(" select tsqxid from fw.special_right where tsqxid = ? and dbid = ? ");
		this.sql.setString(1, tsqxid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("特殊权限配置信息已经存在不允许重复配置。");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.special_right ");
		sqlBF.append("   (dbid, tsqxid, tsqxmc, bz) ");
		sqlBF.append("   select ?, tsqxid, tsqxmc, bz ");
		sqlBF.append("     from fw.special_right_doc ");
		sqlBF.append("    where tsqxid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, tsqxid);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 删除特殊权限配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap deleteSpRightConfig(final DataMap para) throws Exception {
		String tsqxid = para.getString("tsqxid");// 特殊权限名称

		if (StringUtil.chkStrNull(tsqxid)) {
			throw new BizException("传入的特殊权限ID不允许为空");
		}

		// 执行删除
		this.sql.setSql(" delete from fw.special_right where tsqxid = ? and dbid = ?");
		this.sql.setString(1, tsqxid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();
		return null;
	}
}
