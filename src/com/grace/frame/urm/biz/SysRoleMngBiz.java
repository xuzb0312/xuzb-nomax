package com.grace.frame.urm.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.SeqUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 用户角色管理
 * 
 * @author yjc
 */
public class SysRoleMngBiz extends Biz{

	/**
	 * 选择系统角色
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap fwdChooseSysRole(final DataMap para) throws Exception {
		String jsmc = para.getString("jsmc");
		if (StringUtil.chkStrNull(jsmc)) {
			jsmc = "%";
		} else {
			jsmc = "%" + jsmc + "%";
		}

		if ("A".equals(this.getSysUser().getYhlx())) {
			this.sql.setSql(" select jsid, jsmc, bz from fw.sys_role where jsmc like ? ");
			this.sql.setString(1, jsmc);
		} else {
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" select jsid, jsmc, bz ");
			sqlBF.append("   from fw.sys_role a ");
			sqlBF.append("  where jsmc like ? ");
			sqlBF.append("    and exists (select 'x' ");
			sqlBF.append("           from fw.user_role b ");
			sqlBF.append("          where a.jsid = b.jsid ");
			sqlBF.append("            and b.yhid = ?) ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, jsmc);
			this.sql.setString(2, this.getYhid());
		}
		DataSet dsRole = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("dsrole", dsRole);
		return dm;
	}

	/**
	 * 角色新增保存
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-12
	 * @since V1.0
	 */
	public final DataMap saveSysRoleAdd(final DataMap para) throws Exception {
		String jsmc = para.getString("jsmc");
		String bz = para.getString("bz");
		String tspzbr = para.getString("tspzbr");// 同时将该权限配置到当前操作员

		if (StringUtil.chkStrNull(jsmc)) {
			throw new BizException("角色名称为空");
		}

		// 检查角色名称是否在系统中已经存在
		this.sql.setSql(" select jsid from fw.sys_role where jsmc = ? ");
		this.sql.setString(1, jsmc);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该角色名称在系统中已经存在，请更换。");
		}

		String jsid = SeqUtil.getId("fw.sq_jsid");// 获取角色ID

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.sys_role ");
		sqlBF.append("   (jsid, jsmc, bz) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, jsid);
		this.sql.setString(2, jsmc);
		this.sql.setString(3, bz);
		this.sql.executeUpdate();

		if ("1".equals(tspzbr) && !"A".equals(this.getSysUser().getYhlx())) {
			// 当同时配置本人信息并且系统用户类型不是超级管理员的将该角色配置给本人
			this.sql.setSql(" insert into fw.user_role(yhid, jsid) values(?, ?) ");
			this.sql.setString(1, this.getYhid());
			this.sql.setString(2, jsid);
			this.sql.executeUpdate();
		}

		// 记录日志
		this.log("SYS-E-YHXZ", "用户角色新增", "E", jsid, "新增角色ID:" + jsid + ",角色名称:"
				+ jsmc, "jsid=" + jsid + ",jsmc=" + jsmc);

		DataMap rdm = new DataMap();
		rdm.put("jsid", jsid);
		rdm.put("jsmc", jsmc);
		return rdm;
	}

	/**
	 * 保存权限
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap saveSysRoleGnqxMng(final DataMap para) throws Exception {
		String jsid = para.getString("jsid");
		String gnids = para.getString("gnids");

		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("用户ID为空");
		}

		// 首先删除所有的权限配置
		this.sql.setSql(" delete from fw.role_func where jsid = ? ");
		this.sql.setString(1, jsid);
		this.sql.executeUpdate();

		// 只有gnid不为空的时候，才允许插入功能。
		if (!StringUtil.chkStrNull(gnids)) {
			// 查询叶子节点
			String[] arrGnids = gnids.split(",");
			this.sql.setSql(" insert into fw.role_func(jsid, gnid) values (?, ?) ");
			for (int i = 0, n = arrGnids.length; i < n; i++) {
				this.sql.setString(1, jsid);
				this.sql.setString(2, arrGnids[i]);
				this.sql.addBatch();
			}
			this.sql.executeBatch();

			// 自动生成上级的节点-自动纠正
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" insert into fw.role_func ");
			sqlBF.append("   (jsid, gnid) ");
			sqlBF.append("   select ? jsid, b.fgn gnid ");
			sqlBF.append("     from fw.func b ");
			sqlBF.append("    where b.dbid = ? ");
			sqlBF.append("      and exists (select 'x' ");
			sqlBF.append("             from fw.role_func d ");
			sqlBF.append("            where b.gnid = d.gnid ");
			sqlBF.append("              and d.jsid = ?) ");
			sqlBF.append("      and b.fgn <> 'root' ");
			sqlBF.append("      and b.gnlx <> 'A' ");
			sqlBF.append("      and not exists (select 'x' ");
			sqlBF.append("             from fw.role_func c ");
			sqlBF.append("            where b.fgn = c.gnid ");
			sqlBF.append("              and c.jsid = ?) ");
			sqlBF.append("    group by b.fgn ");
			do {// 循环执行，直到自动纠正完成
				this.sql.setSql(sqlBF.toString());
				this.sql.setString(1, jsid);
				this.sql.setString(2, GlobalVars.SYS_DBID);
				this.sql.setString(3, jsid);
				this.sql.setString(4, jsid);
				int rows = this.sql.executeUpdate();
				if (rows <= 0) {
					break;
				}
			} while (true);
		}

		// 日志记录
		this.log("SYS-E-GNQXBC", "功能权限保存", "E", jsid, "保存角色ID:" + jsid
				+ "的角色功能权限", "jsid=" + jsid);

		return null;
	}

	/**
	 * 操作日志查询
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap querySysRoleBizLog(final DataMap para) throws Exception {
		String jsid = para.getString("jsid");
		String qsrq = para.getString("qsrq");
		String zzrq = para.getString("zzrq");

		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("用户角色ID为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.czlx, ");
		sqlBF.append("        a.czmc, ");
		sqlBF.append("        a.czsm, ");
		sqlBF.append("        to_char(a.czsj, 'yyyymmddhh24miss') czsj, ");
		sqlBF.append("        a.czip, ");
		sqlBF.append("        b.yhbh czybh, ");
		sqlBF.append("        b.yhmc czymc ");
		sqlBF.append("   from fw.biz_log a, fw.sys_user b ");
		sqlBF.append("  where a.czyid = b.yhid(+) ");
		sqlBF.append("    and a.ztlx = 'E' ");
		sqlBF.append("    and a.ztid = ? ");
		if (!StringUtil.chkStrNull(qsrq)) {
			sqlBF.append("    and to_char(a.czsj, 'yyyymmdd') >= '" + qsrq
					+ "' ");
		}
		if (!StringUtil.chkStrNull(zzrq)) {
			sqlBF.append("    and to_char(a.czsj, 'yyyymmdd') <= '" + zzrq
					+ "' ");
		}
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, jsid);
		DataSet dsLog = this.sql.executeQuery();
		dsLog.sortdesc("czsj");

		DataMap rdm = new DataMap();
		rdm.put("dslog", dsLog);
		return rdm;
	}

	/**
	 * 进入系统角色信息管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-12
	 * @since V1.0
	 */
	public final DataMap fwdSysRoleInfoMng(final DataMap para) throws Exception {
		String jsid = para.getString("jsid");
		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("用户角色ID为空");
		}

		// 角色信息
		this.sql.setSql(" select jsid, jsmc, bz from fw.sys_role where jsid = ? ");
		this.sql.setString(1, jsid);
		DataSet dsRole = this.sql.executeQuery();
		if (dsRole.size() <= 0) {
			throw new BizException("角色ID为" + jsid + "的角色信息在系统中不存在。");
		}

		// 配置该角色的用户
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select yhid, yhbh, yhlx, yhmc, zjlx, ");
		sqlBF.append("        zjhm, ssjbjgid, ssjgmc, ssjgbm, nvl(yhzt, '1') yhzt, ");
		sqlBF.append("        zxrq, to_char(zxjbsj, 'yyyymmddhh24miss') zxjbsj, zxyy ");
		sqlBF.append("   from fw.sys_user a ");
		sqlBF.append("  where exists (select 'x' from fw.user_role b where a.yhid = b.yhid and b.jsid = ?) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, jsid);
		DataSet dsUser = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("roleinfo", dsRole.getRow(0));
		rdm.put("userinfo", dsUser);
		return rdm;
	}

	/**
	 * 进入系统角色信息修改页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-12
	 * @since V1.0
	 */
	public final DataMap fwdSysRoleInfoModify(final DataMap para) throws Exception {
		String jsid = para.getString("jsid");
		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("用户角色ID为空");
		}

		// 角色信息
		this.sql.setSql(" select jsid, jsmc, bz from fw.sys_role where jsid = ? ");
		this.sql.setString(1, jsid);
		DataSet dsRole = this.sql.executeQuery();
		if (dsRole.size() <= 0) {
			throw new BizException("角色ID为" + jsid + "的角色信息在系统中不存在。");
		}

		DataMap rdm = new DataMap();
		rdm.put("roleinfo", dsRole.getRow(0));
		return rdm;
	}

	/**
	 * 保存系统角色修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-12
	 * @since V1.0
	 */
	public final DataMap saveSysRoleModify(final DataMap para) throws Exception {
		String jsid = para.getString("jsid");
		String jsmc = para.getString("jsmc");
		String bz = para.getString("bz");

		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("用户角色ID为空");
		}
		if (StringUtil.chkStrNull(jsmc)) {
			throw new BizException("角色名称为空");
		}

		// 检查角色名称是否在系统中已经存在
		this.sql.setSql(" select jsid from fw.sys_role where jsmc = ? and jsid <> ? ");
		this.sql.setString(1, jsmc);
		this.sql.setString(2, jsid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该角色名称在系统中已经存在，请更换。");
		}

		this.sql.setSql(" update fw.sys_role set jsmc = ?, bz = ? where jsid = ? ");
		this.sql.setString(1, jsmc);
		this.sql.setString(2, bz);
		this.sql.setString(3, jsid);
		this.sql.executeUpdate();

		// 日志记录
		this.log("SYS-E-JSXG", "角色修改", "E", jsid, "修改角色ID:" + jsid, "jsid="
				+ jsid);

		return null;
	}

	/**
	 * 保存系统角色删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-12
	 * @since V1.0
	 */
	public final DataMap saveSysRoleDel(final DataMap para) throws Exception {
		String jsid = para.getString("jsid");

		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("用户角色ID为空");
		}

		// user配置删除
		this.sql.setSql("delete from fw.user_role where jsid = ? ");
		this.sql.setString(1, jsid);
		this.sql.executeUpdate();

		// 功能权限删除
		this.sql.setSql("delete from fw.role_func where jsid = ? ");
		this.sql.setString(1, jsid);
		this.sql.executeUpdate();

		// 角色删除
		this.sql.setSql("delete from fw.sys_role where jsid = ? ");
		this.sql.setString(1, jsid);
		this.sql.executeUpdate();

		// 日志记录
		this.log("SYS-E-JSSC", "角色删除", "E", jsid, "删除角色ID:" + jsid, "jsid="
				+ jsid);

		return null;
	}

	/**
	 * 批量将该角色分配给用户
	 * 
	 * @author yjc
	 * @date 创建时间 2017-2-8
	 * @since V1.0
	 */
	public final DataMap fwdSysRoleUserBatchAdd(final DataMap para) throws Exception {
		String jsid = para.getString("jsid");

		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("用户角色ID为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select yhid, yhbh, yhlx, yhmc, zjlx, ");
		sqlBF.append("        zjhm, ssjbjgid, ssjgmc, ssjgbm ");
		sqlBF.append("   from fw.sys_user a ");
		sqlBF.append("  where not exists (select 'x' ");
		sqlBF.append("           from fw.user_role b ");
		sqlBF.append("          where a.yhid = b.yhid ");
		sqlBF.append("            and b.jsid = ?) ");
		sqlBF.append("   and yhlx = 'B' ");
		sqlBF.append("   and nvl(yhzt, '1') = '1' ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, jsid);
		DataSet dsUser = this.sql.executeQuery();

		// 获取经办机构mcDs
		this.sql.setSql(" select jbjgid code, jbjgmc content from fw.sys_agency ");
		DataSet dsJbjg = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("userinfo", dsUser);
		dm.put("dsjbjg", dsJbjg);
		dm.put("jsid", jsid);
		return dm;
	}

	/**
	 * 批量将该角色分配给用户-保存
	 * 
	 * @author yjc
	 * @date 创建时间 2017-2-8
	 * @since V1.0
	 */
	public final DataMap saveSysRoleUserBatchAdd(final DataMap para) throws Exception {
		DataSet dsUser = para.getDataSet("gridUser");
		String jsid = para.getString("jsid");
		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("传入的JSID为空");
		}
		if (null == dsUser) {
			throw new BizException("传入的用户信息为空");
		}

		// 数据权限删除
		this.sql.setSql(" insert into fw.user_role(yhid, jsid) values (?, ?) ");
		for (int i = 0, n = dsUser.size(); i < n; i++) {
			String yhid = dsUser.getString(i, "yhid");

			this.sql.setString(1, yhid);
			this.sql.setString(2, jsid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-E-SJQXXZ", "角色用户批量新增", "E", jsid, "角色用户批量新增，角色ID:" + jsid, "jsid="
				+ jsid);

		return null;
	}

	/**
	 * 批量将该角色分配给用户-删除
	 * 
	 * @author yjc
	 * @date 创建时间 2017-2-8
	 * @since V1.0
	 */
	public final DataMap saveSysRoleUserBatchDel(final DataMap para) throws Exception {
		String jsid = para.getString("jsid");
		DataSet dsUser = para.getDataSet("gridUser");
		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("传入的JSID为空");
		}
		if (null == dsUser) {
			throw new BizException("传入的用户信息为空");
		}

		// 数据权限删除
		this.sql.setSql(" delete from fw.user_role where jsid = ? and yhid = ? ");
		for (int i = 0, n = dsUser.size(); i < n; i++) {
			String yhid = dsUser.getString(i, "yhid");

			this.sql.setString(1, jsid);
			this.sql.setString(2, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-E-TSQXSC", "批量删除该角色的用户配置", "E", jsid, "批量删除该角色的用户配置，角色ID:"
				+ jsid, "jsid=" + jsid);

		return null;
	}
}
