package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

public class ServiceRegMngBiz extends Biz{
	/**
	 * 进入服务注册信息管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap fwdServiceRegMng(final DataMap para) throws Exception {
		// 默认查询本DBID下的。
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select dbid, fwmc, url, yhbh, pwd, ");// mod.yjc.2017年4月30日-PWD为用户签名密钥
		sqlBF.append("        timeout, fwzcsm ");
		sqlBF.append("   from fw.service_reg a ");
		sqlBF.append("  where a.dbid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsReg = this.sql.executeQuery();

		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dsreg", dsReg);

		return rdm;
	}

	/**
	 * 服务注册信息的新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap saveServiceRegAdd(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");// 服务注册名称
		String url = para.getString("url");// 服务注册路径
		String yhbh = para.getString("yhbh");// 用户编号
		String pwd = para.getString("pwd");// 为用户签名密钥
		int timeout = para.getInt("timeout");// 超时-毫秒
		String fwzcsm = para.getString("fwzcsm");// 参数说明

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的服务注册名称为空");
		}
		if (StringUtil.chkStrNull(url)) {
			throw new BizException("传入的服务注册路径为空");
		}
		if (StringUtil.chkStrNull(yhbh)) {
			throw new BizException("传入的用户编号为空");
		}
		if (StringUtil.chkStrNull(pwd)) {
			throw new BizException("传入的用户密钥为空");
		}
		if (timeout <= 0) {
			throw new BizException("超时数据必须大于0");
		}

		// 检测是否已经存在
		this.sql.setSql(" select fwmc from fw.service_reg where fwmc = ? and dbid = ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该服务注册名称已经存在无法新增");
		}

		// 插入服务注册
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.service_reg ");
		sqlBF.append("   (dbid, fwmc, url, yhbh, pwd, ");// mod.yjc.2017年4月30日-PWD为用户签名密钥
		sqlBF.append("    timeout, fwzcsm) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, fwmc);
		this.sql.setString(3, url);
		this.sql.setString(4, yhbh);
		this.sql.setString(5, pwd);

		this.sql.setInt(6, timeout);
		this.sql.setString(7, fwzcsm);
		this.sql.executeUpdate();

		// 重置系统缓存
		GlobalVarsUtil.reloadSERVICE_REG_INFO_MAP();

		return null;
	}

	/**
	 * 信息修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap fwdServiceRegModify(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的服务注册名称为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select dbid, fwmc, url, yhbh, pwd, ");// mod.yjc.2017年4月30日-PWD为用户签名密钥
		sqlBF.append("        timeout, fwzcsm ");
		sqlBF.append("   from fw.service_reg a ");
		sqlBF.append("  where a.dbid = ? ");
		sqlBF.append("    and a.fwmc = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, fwmc);
		DataSet dsReg = this.sql.executeQuery();
		if (dsReg.size() <= 0) {
			throw new BizException("该服务注册信息在系统中不存在。");
		}

		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dsreg", dsReg.getRow(0));

		return rdm;
	}

	/**
	 * 服务注册信息的修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap saveServiceRegModify(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");// 服务注册名称
		String url = para.getString("url");// 服务注册路径
		String yhbh = para.getString("yhbh");// 用户编号
		String pwd = para.getString("pwd");// mod.yjc.2017年4月30日-PWD为用户签名密钥
		int timeout = para.getInt("timeout");// 超时-毫秒
		String fwzcsm = para.getString("fwzcsm");// 参数说明

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的服务注册名称为空");
		}
		if (StringUtil.chkStrNull(url)) {
			throw new BizException("传入的服务注册路径为空");
		}
		if (StringUtil.chkStrNull(yhbh)) {
			throw new BizException("传入的用户编号为空");
		}
		if (StringUtil.chkStrNull(pwd)) {
			throw new BizException("传入的用户密码为空");
		}
		if (timeout <= 0) {
			throw new BizException("超时数据必须大于0");
		}

		// 删除
		this.sql.setSql(" delete from fw.service_reg where fwmc = ? and dbid = ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 插入服务注册
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.service_reg ");
		sqlBF.append("   (dbid, fwmc, url, yhbh, pwd, ");// mod.yjc.2017年4月30日-PWD为用户签名密钥
		sqlBF.append("    timeout, fwzcsm) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, fwmc);
		this.sql.setString(3, url);
		this.sql.setString(4, yhbh);
		this.sql.setString(5, pwd);

		this.sql.setInt(6, timeout);
		this.sql.setString(7, fwzcsm);
		this.sql.executeUpdate();

		// 重置系统缓存
		GlobalVarsUtil.reloadSERVICE_REG_INFO_MAP();

		return null;
	}

	/**
	 * 服务注册信息删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap deleteServiceReg(final DataMap para) throws Exception {
		String fwmc = para.getString("fwmc");

		if (StringUtil.chkStrNull(fwmc)) {
			throw new BizException("传入的服务注册名称为空");
		}

		// 删除
		this.sql.setSql(" delete from fw.service_reg where fwmc = ? and dbid = ? ");
		this.sql.setString(1, fwmc);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 重置系统缓存
		GlobalVarsUtil.reloadSERVICE_REG_INFO_MAP();

		return null;
	}
}
