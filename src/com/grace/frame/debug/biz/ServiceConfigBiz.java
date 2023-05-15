package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

public class ServiceConfigBiz extends Biz{
	/**
	 * 刷新本地服务配置数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap refreshGridServiceConfig(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.fwmc, a.fwff, a.biz, a.bizff, b.fwsm ");
		sqlBF.append("   from fw.service_config a, fw.service_doc b ");
		sqlBF.append("  where a.fwmc = b.fwmc ");
		sqlBF.append("    and a.fwff = b.fwff ");
		sqlBF.append("    and a.dbid = ? ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet ds = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsservice", ds);
		return rdm;
	}

	/**
	 * 刷新本地服务文档数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap refreshGridServiceDoc(final DataMap para) throws Exception {
		this.sql.setSql(" select fwmc, fwff, biz, bizff, fwsm from fw.service_doc ");
		DataSet ds = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsdoc", ds);
		return rdm;
	}

	/**
	 * 保存本地服务文档新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap saveServiceDocAdd(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");// 服务名称
		String fwff = para.getString("fwff");// 服务方法
		String biz = para.getString("biz");// 业务BIZ
		String bizff = para.getString("bizff");// Biz的方法
		String fwsm = para.getString("fwsm");// 服务说明

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的本地服务名称不允许为空");
		}
		if (StringUtil.chkStrNull(fwff)) {
			throw new BizException("传入的本地服务方法不允许为空");
		}
		if (StringUtil.chkStrNull(biz)) {
			throw new BizException("传入的本地服务biz不允许为空");
		}
		if (StringUtil.chkStrNull(bizff)) {
			throw new BizException("传入的本地服务bizff不允许为空");
		}

		// 检查是否已经存在
		this.sql.setSql(" select fwmc from fw.service_doc where fwmc = ? and fwff = ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, fwff);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该本地服务已经存在不允许重复新增");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.service_doc ");
		sqlBF.append("   (fwmc, fwff, biz, bizff, fwsm) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, fwmc);
		this.sql.setString(2, fwff);
		this.sql.setString(3, biz);
		this.sql.setString(4, bizff);
		this.sql.setString(5, fwsm);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 进入本地服务文档修改页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdServiceDocModify(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");// 服务名称
		String fwff = para.getString("fwff");// 服务方法

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的本地服务名称不允许为空");
		}
		if (StringUtil.chkStrNull(fwff)) {
			throw new BizException("传入的本地服务方法不允许为空");
		}

		this.sql.setSql(" select fwmc, fwff, biz, bizff, fwsm from fw.service_doc where fwmc = ? and fwff = ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, fwff);
		DataSet ds = this.sql.executeQuery();
		if (ds.size() <= 0) {
			throw new BizException("本地服务文档信息不存在。");
		}

		DataMap rdm = new DataMap();
		rdm.put("formdoc", ds.getRow(0));
		return rdm;
	}

	/**
	 * 保存本地服务文档修改信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap saveServiceDocModify(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");// 服务名称
		String fwff = para.getString("fwff");// 服务方法
		String biz = para.getString("biz");// 业务BIZ
		String bizff = para.getString("bizff");// Biz的方法
		String fwsm = para.getString("fwsm");// 服务说明

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的本地服务名称不允许为空");
		}
		if (StringUtil.chkStrNull(fwff)) {
			throw new BizException("传入的本地服务方法不允许为空");
		}
		if (StringUtil.chkStrNull(biz)) {
			throw new BizException("传入的本地服务biz不允许为空");
		}
		if (StringUtil.chkStrNull(bizff)) {
			throw new BizException("传入的本地服务bizff不允许为空");
		}

		// DOC调整
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" update fw.service_doc a ");
		sqlBF.append("    set a.biz = ?, a.bizff = ?, a.fwsm = ? ");
		sqlBF.append("  where fwmc = ? ");
		sqlBF.append("    and fwff = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, biz);
		this.sql.setString(2, bizff);
		this.sql.setString(3, fwsm);
		this.sql.setString(4, fwmc);
		this.sql.setString(5, fwff);

		this.sql.executeUpdate();

		// Config-调整
		sqlBF.setLength(0);
		sqlBF.append(" update fw.service_config a ");
		sqlBF.append("    set a.biz = ?, a.bizff = ? ");
		sqlBF.append("  where fwmc = ? ");
		sqlBF.append("    and fwff = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, biz);
		this.sql.setString(2, bizff);
		this.sql.setString(3, fwmc);
		this.sql.setString(4, fwff);
		this.sql.executeUpdate();

		// 如果要对外提供服务，则加载对应服务映射关系
		if (GlobalVars.IS_START_SERVICE) {
			GlobalVarsUtil.reloadSERVICE_LIST_MAP();
		}
		GlobalVarsUtil.clearSERVICE_RIGHT_MAP();// 清空，缓存服务权限列表

		return null;
	}

	/**
	 * 删除本地服务文档信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap deleteServiceDoc(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");// 服务名称
		String fwff = para.getString("fwff");// 服务方法

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的本地服务名称不允许为空");
		}
		if (StringUtil.chkStrNull(fwff)) {
			throw new BizException("传入的本地服务方法不允许为空");
		}

		// 检测是否可以删除
		this.sql.setSql(" select dbid from fw.service_config where fwmc = ? and fwff = ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, fwff);
		DataSet ds = this.sql.executeQuery();
		if (ds.size() > 0) {
			throw new BizException("本地服务文档信息在fw.service_config中已经配置给了"
					+ ds.getString(0, "dbid") + ",不允许删除。");
		}

		// 执行删除
		this.sql.setSql(" delete from fw.service_doc where fwmc = ? and fwff = ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, fwff);
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
	public final DataMap fwdChooseServiceDoc(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc", "");
		fwmc = "%" + fwmc + "%";

		this.sql.setSql(" select fwmc, fwff, biz, bizff, fwsm from fw.service_doc where fwmc like ? or fwff like ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, fwmc);
		DataSet ds = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsdoc", ds);
		return rdm;
	}

	/**
	 * 保存本地服务配置新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap saveServiceConfigAdd(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");// 服务名称
		String fwff = para.getString("fwff");// 服务方法

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的本地服务名称不允许为空");
		}
		if (StringUtil.chkStrNull(fwff)) {
			throw new BizException("传入的本地服务方法不允许为空");
		}

		// 检查tsqxid是否已经存在
		this.sql.setSql(" select fwmc from fw.service_config where fwmc = ? and fwff = ? and dbid = ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, fwff);
		this.sql.setString(3, GlobalVars.SYS_DBID);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("本地服务配置信息已经存在不允许重复配置。");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.service_config ");
		sqlBF.append("   (dbid, fwmc, fwff, biz, bizff) ");
		sqlBF.append("   select ?, fwmc, fwff, biz, bizff ");
		sqlBF.append("     from fw.service_doc ");
		sqlBF.append("    where fwmc = ? ");
		sqlBF.append("      and fwff = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, fwmc);
		this.sql.setString(3, fwff);
		this.sql.executeUpdate();

		// 如果要对外提供服务，则加载对应服务映射关系
		if (GlobalVars.IS_START_SERVICE) {
			GlobalVarsUtil.reloadSERVICE_LIST_MAP();
		}
		GlobalVarsUtil.clearSERVICE_RIGHT_MAP();// 清空，缓存服务权限列表

		return null;
	}

	/**
	 * 删除本地服务配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap deleteServiceConfig(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");// 服务名称
		String fwff = para.getString("fwff");// 服务方法

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的本地服务名称不允许为空");
		}
		if (StringUtil.chkStrNull(fwff)) {
			throw new BizException("传入的本地服务方法不允许为空");
		}

		// 执行删除
		this.sql.setSql(" delete from fw.service_config where fwmc = ? and fwff = ? and dbid = ?");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, fwff);
		this.sql.setString(3, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 如果要对外提供服务，则加载对应服务映射关系
		if (GlobalVars.IS_START_SERVICE) {
			GlobalVarsUtil.reloadSERVICE_LIST_MAP();
		}
		GlobalVarsUtil.clearSERVICE_RIGHT_MAP();// 清空，缓存服务权限列表

		return null;
	}
}
