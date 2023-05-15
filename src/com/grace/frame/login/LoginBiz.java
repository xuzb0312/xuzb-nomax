package com.grace.frame.login;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.SecUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysUser;
import com.grace.frame.workflow.Biz;

/**
 * 登录业务biz
 * 
 * @author yjc
 */
public class LoginBiz extends Biz{

	/**
	 * 移除我的功能的功能
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-22
	 * @since V1.0
	 */
	public final DataMap removeMyfunction(final DataMap para) throws Exception {
		// 根据数据库的配置，动态的加载业务功能菜单
		String gnid = para.getString("gnid");
		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("功能ID不能为空。");
		}
		int unionIndex = gnid.indexOf("-union-");
		if (unionIndex > 0) {
			gnid = gnid.substring(0, unionIndex);
		}

		this.sql.setSql(" delete from fw.my_func where gnid = ? and yhid = ? ");
		this.sql.setString(1, gnid);
		this.sql.setString(2, this.getYhid());
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 清空我的功能
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	public final DataMap clearMyfunction(final DataMap para) throws Exception {
		this.sql.setSql(" delete from fw.my_func where yhid = ? ");
		this.sql.setString(1, this.getYhid());
		this.sql.executeUpdate();
		return null;
	}

	/**
	 * 增加我的功能的功能
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-22
	 * @since V1.0
	 */
	public final DataMap addMyfunction(final DataMap para) throws Exception {
		// 根据数据库的配置，动态的加载业务功能菜单
		String gnid = para.getString("gnid");
		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("功能ID不能为空。");
		}
		int unionIndex = gnid.indexOf("-union-");
		if (unionIndex > 0) {
			gnid = gnid.substring(0, unionIndex);
		}
		this.sql.setSql(" delete from fw.my_func where gnid = ? and yhid = ? ");
		this.sql.setString(1, gnid);
		this.sql.setString(2, this.getYhid());
		this.sql.executeUpdate();

		// 检查该功能权限是否在func表中有配置，如果没有配置，则不增加
		this.sql.setSql(" select gnid from fw.func where dbid = ? and gnlx = 'C' ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet ds = this.sql.executeQuery();
		if (ds.size() <= 0) {
			return null;
		}

		this.sql.setSql(" insert into fw.my_func(gnid, yhid, tjsj) values(?, ?, sysdate) ");
		this.sql.setString(1, gnid);
		this.sql.setString(2, this.getYhid());
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 修改我的密码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-22
	 * @since V1.0
	 */
	public final DataMap modifyMyPwd(final DataMap para) throws Exception {
		String ypwd = para.getString("ypwd");
		String xpwd = para.getString("xpwd");
		if (StringUtil.chkStrNull(ypwd)) {
			throw new BizException("传入的原始密码为空");
		}
		if (StringUtil.chkStrNull(xpwd)) {
			throw new BizException("传入的新密码为空");
		}

		// 对密码进行加密操作
		String encodeYPwd = SecUtil.encodeStrByMd5(ypwd);
		if (!encodeYPwd.equals(this.getSysUser().getPassword())) {
			throw new BizException("输入的原始密码不正确");
		}
		String encodeXPwd = SecUtil.encodeStrByMd5(xpwd);

		// 数据的更改
		this.sql.setSql(" update fw.sys_user set password = ? where yhid = ? ");
		this.sql.setString(1, encodeXPwd);
		this.sql.setString(2, this.getYhid());
		this.sql.executeUpdate();

		// 系统中登录的密码进行一次修正
		this.getSysUser().setPassword(encodeXPwd);

		this.log("SYS-A-YHMMXG", "用户密码修改", "A", this.getYhid(), this.getYhbh(), this.getYhmc(), "用户密码修改", "yhid="
				+ this.getYhid());

		return null;
	}

	/**
	 * 转向业务主界面的相关权限获取操作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-30
	 * @since V1.0
	 */
	public final DataMap fwdMainPage(final DataMap para) throws Exception {
		// 根据数据库的配置，动态的加载业务功能菜单
		String ywlxgnid = para.getString("ywlxgnid", "");
		ywlxgnid = this.getDefalutYwlxgnid(ywlxgnid);
		String subsysgnid = para.getString("subsysgnid", "");
		String gnid = para.getString("gnid", "");

		DataSet dsYwlx = this.getYwlxDs();// 获取业务领域
		DataSet dsSubSys = this.getSubSystemDs(ywlxgnid);
		for (int i = 0, n = dsSubSys.size(); i < n; i++) {
			String tempGnid = dsSubSys.getString(i, "gnid");
			dsSubSys.put(i, "selected", tempGnid.equals(subsysgnid) ? "true" : "false");// 默认展开的子系统
		}

		// 自定义参数
		DataMap pdm = para.clone();
		pdm.remove("ywlxgnid");
		pdm.remove("subsysgnid");
		pdm.remove("gnid");
		String reqpara = pdm.toJsonString().replace("\"", "\\\"");// 对于"的转义

		DataMap dm = new DataMap();
		dm.put("ywlxgnid", ywlxgnid);
		dm.put("dsywlx", dsYwlx);
		dm.put("dssubsys", dsSubSys);
		dm.put("subsysgnid", subsysgnid);
		dm.put("gnid", gnid);
		dm.put("reqpara", reqpara);
		return dm;
	}

	/**
	 * 默认的业务领域功能id
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-22
	 * @since V1.0
	 */
	private String getDefalutYwlxgnid(String ywlxgnid) throws AppException, BizException {
		SysUser user = this.getSysUser();
		if ("A".equals(user.getYhlx())) {
			// 超级管理员
			if (!StringUtil.chkStrNull(ywlxgnid)) {
				// 如果为调试开发模式
				if (GlobalVars.DEBUG_MODE
						&& "debugroot".equalsIgnoreCase(ywlxgnid)) {
					return "debugroot";
				}

				// 如果ywlxgnid不为空，则检查该用户是否存在此权限-没有该权限的抛出异常
				this.sql.setSql(" select gnid from fw.func where dbid = ? and gnlx = ? and gnid = ? ");
				this.sql.setString(1, GlobalVars.SYS_DBID);
				this.sql.setString(2, "A");
				this.sql.setString(3, ywlxgnid);
				DataSet dsTmp = this.sql.executeQuery();
				if (dsTmp.size() <= 0) {
					throw new BizException("您没有访问该功能的权限。");
				}
				return ywlxgnid;
			}

			// 当传入的ywlxgnid为空的时候，则获取默认的业务领域
			this.sql.setSql(" select gnid, sxh from fw.func where dbid = ? and gnlx = ? ");
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, "A");
			DataSet dsTmp = this.sql.executeQuery();
			if (dsTmp.size() <= 0) {
				if (GlobalVars.DEBUG_MODE) {// 调试模式返回debugroot领域
					return "debugroot";
				}
				throw new BizException("系统没有配置任何业务领域功能。");
			}
			dsTmp = dsTmp.sort("sxh");// 按照顺序号进行排序
			return dsTmp.getString(0, "gnid");
		} else if ("B".equals(user.getYhlx())) {
			StringBuffer sqlBF = new StringBuffer();
			// 普通业务用户
			if (!StringUtil.chkStrNull(ywlxgnid)) {
				// 如果为调试开发模式
				if (GlobalVars.DEBUG_MODE
						&& "debugroot".equalsIgnoreCase(ywlxgnid)) {
					return "debugroot";
				}

				// 如果ywlxgnid不为空，则检查该用户是否存在此权限-没有该权限的抛出异常
				sqlBF.setLength(0);
				sqlBF.append(" select a.gnid ");
				sqlBF.append("   from fw.func a ");
				sqlBF.append("  where a.dbid = ? ");
				sqlBF.append("    and a.gnlx = ? ");
				sqlBF.append("    and a.gnid = ? ");
				sqlBF.append("    and (exists (select 'x' ");
				sqlBF.append("                   from fw.user_func b ");
				sqlBF.append("                  where a.gnid = b.gnid ");
				sqlBF.append("                    and b.yhid = ?) or exists ");
				sqlBF.append("         (select 'x' ");
				sqlBF.append("            from fw.user_role c, fw.role_func d ");
				sqlBF.append("           where c.jsid = d.jsid ");
				sqlBF.append("             and d.gnid = a.gnid ");
				sqlBF.append("             and c.yhid = ?)) ");

				this.sql.setSql(sqlBF.toString());
				this.sql.setString(1, GlobalVars.SYS_DBID);
				this.sql.setString(2, "A");
				this.sql.setString(3, ywlxgnid);
				this.sql.setString(4, this.getYhid());
				this.sql.setString(5, this.getYhid());

				DataSet dsTmp = this.sql.executeQuery();
				if (dsTmp.size() <= 0) {
					throw new BizException("您没有访问该功能的权限。");
				}
				return ywlxgnid;
			}

			// 当传入的ywlxgnid为空的时候，则获取默认的业务领域
			sqlBF.setLength(0);
			sqlBF.append(" select a.gnid, sxh ");
			sqlBF.append("   from fw.func a ");
			sqlBF.append("  where a.dbid = ? ");
			sqlBF.append("    and a.gnlx = ? ");
			sqlBF.append("    and (exists (select 'x' ");
			sqlBF.append("                   from fw.user_func b ");
			sqlBF.append("                  where a.gnid = b.gnid ");
			sqlBF.append("                    and b.yhid = ?) or exists ");
			sqlBF.append("         (select 'x' ");
			sqlBF.append("            from fw.user_role c, fw.role_func d ");
			sqlBF.append("           where c.jsid = d.jsid ");
			sqlBF.append("             and d.gnid = a.gnid ");
			sqlBF.append("             and c.yhid = ?)) ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, "A");
			this.sql.setString(3, this.getYhid());
			this.sql.setString(4, this.getYhid());
			DataSet dsTmp = this.sql.executeQuery();
			if (dsTmp.size() <= 0) {
				if (GlobalVars.DEBUG_MODE) {// 调试模式返回debugroot领域
					return "debugroot";
				}
				throw new BizException("您没有访问此功能的权限");
			}
			dsTmp = dsTmp.sort("sxh");// 按照顺序号进行排序
			return dsTmp.getString(0, "gnid");
		} else {
			throw new BizException("该类型【" + user.getYhlx() + "】用户不允许进行系统登录。");
		}
	}

	/**
	 * 获取业务领域的ds
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-22
	 * @since V1.0
	 */
	private DataSet getYwlxDs() throws Exception {
		SysUser user = this.getSysUser();
		if ("A".equals(user.getYhlx())) {
			this.sql.setSql(" select gnid, gnmc, nvl(gntb, 'icon-application-view-icons') gntb, sxh from fw.func where dbid = ? and gnlx = ? ");
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, "A");
		} else if ("B".equals(user.getYhlx())) {
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" select gnid, gnmc, nvl(gntb, 'icon-application-view-icons') gntb, sxh ");
			sqlBF.append("   from fw.func a ");
			sqlBF.append("  where a.dbid = ? ");
			sqlBF.append("    and a.gnlx = ? ");
			sqlBF.append("    and (exists (select 'x' ");
			sqlBF.append("                   from fw.user_func b ");
			sqlBF.append("                  where a.gnid = b.gnid ");
			sqlBF.append("                    and b.yhid = ?) or exists ");
			sqlBF.append("         (select 'x' ");
			sqlBF.append("            from fw.user_role c, fw.role_func d ");
			sqlBF.append("           where c.jsid = d.jsid ");
			sqlBF.append("             and d.gnid = a.gnid ");
			sqlBF.append("             and c.yhid = ?)) ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, "A");
			this.sql.setString(3, this.getYhid());
			this.sql.setString(4, this.getYhid());
		} else {
			throw new BizException("该类型【" + user.getYhlx() + "】用户不允许进行系统登录。");
		}
		DataSet dsTmp = this.sql.executeQuery();
		// 如果是调试模式，则将debugroot增加上
		if (GlobalVars.DEBUG_MODE) {
			DataMap dmDebug = new DataMap();
			dmDebug.put("gnid", "debugroot");
			dmDebug.put("gnmc", "调试模式");
			dmDebug.put("gntb", "icon-bug");
			dmDebug.put("sxh", 999999);
			dsTmp.addRow(dmDebug);
		}
		dsTmp = dsTmp.sort("sxh");// 按照顺序号进行排序
		return dsTmp;
	}

	/**
	 * 获取子业务系统ds
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-22
	 * @since V1.0
	 */
	private DataSet getSubSystemDs(String ywlyid) throws Exception {
		if (GlobalVars.DEBUG_MODE && "debugroot".equals(ywlyid)) {
			// debug模式
			DataSet dsDebug = new DataSet();
			// 详细配置
			dsDebug.addRow();
			dsDebug.put(dsDebug.size() - 1, "gnid", "debug01");
			dsDebug.put(dsDebug.size() - 1, "gnmc", "详细配置");
			dsDebug.put(dsDebug.size() - 1, "gntb", "icon-application-xp-terminal");
			dsDebug.put(dsDebug.size() - 1, "sxh", 1);

			// 引导配置
			dsDebug.addRow();
			dsDebug.put(dsDebug.size() - 1, "gnid", "debug02");
			dsDebug.put(dsDebug.size() - 1, "gnmc", "引导配置");
			dsDebug.put(dsDebug.size() - 1, "gntb", "icon-arrow-merge");
			dsDebug.put(dsDebug.size() - 1, "sxh", 10);

			// 开发工具
			dsDebug.addRow();
			dsDebug.put(dsDebug.size() - 1, "gnid", "debug03");
			dsDebug.put(dsDebug.size() - 1, "gnmc", "开发工具");
			dsDebug.put(dsDebug.size() - 1, "gntb", "icon-award-star-silver-3");
			dsDebug.put(dsDebug.size() - 1, "sxh", 20);

			dsDebug = dsDebug.sort("sxh");// 按照顺序号进行排序
			return dsDebug;
		}

		SysUser user = this.getSysUser();
		if ("A".equals(user.getYhlx())) {// 超级管理员
			// 获取业务领域下的子系统列表
			this.sql.setSql(" select gnid, gnmc, nvl(gntb, 'icon-application') gntb, sxh from fw.func where dbid = ? and gnlx = ? and fgn = ? ");
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, "B");
			this.sql.setString(3, ywlyid);
		} else if ("B".equals(user.getYhlx())) {
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" select gnid, gnmc, nvl(gntb, 'icon-application') gntb, sxh ");
			sqlBF.append("   from fw.func a ");
			sqlBF.append("  where a.dbid = ? ");
			sqlBF.append("    and a.gnlx = ? ");
			sqlBF.append("    and a.fgn = ? ");
			sqlBF.append("    and (exists (select 'x' ");
			sqlBF.append("                   from fw.user_func b ");
			sqlBF.append("                  where a.gnid = b.gnid ");
			sqlBF.append("                    and b.yhid = ?) or exists ");
			sqlBF.append("         (select 'x' ");
			sqlBF.append("            from fw.user_role c, fw.role_func d ");
			sqlBF.append("           where c.jsid = d.jsid ");
			sqlBF.append("             and d.gnid = a.gnid ");
			sqlBF.append("             and c.yhid = ?)) ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, "B");
			this.sql.setString(3, ywlyid);
			this.sql.setString(4, this.getYhid());
			this.sql.setString(5, this.getYhid());
		} else {
			throw new BizException("该类型【" + user.getYhlx() + "】用户不允许进行系统登录。");
		}
		DataSet dsTmp = this.sql.executeQuery();
		dsTmp = dsTmp.sort("sxh");// 按照顺序号进行排序
		return dsTmp;
	}

	/**
	 * 个人信息展示页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdLoginUserPerInfo(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select yhid, yhbh, yhlx, yhmc, zjlx, ");
		sqlBF.append("        zjhm, ssjbjgid, ssjgmc, ssjgbm, nvl(yhzt, '1') yhzt, ");
		sqlBF.append("        zxrq, to_char(zxjbsj, 'yyyymmddhh24miss') zxjbsj, zxyy, sjhm, dzyx ");
		sqlBF.append("   from fw.sys_user ");
		sqlBF.append("  where yhid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, this.getYhid());
		DataSet dsUser = this.sql.executeQuery();
		if (dsUser.size() <= 0) {
			throw new BizException("用户ID为" + this.getYhid() + "的用户在系统中不存在。");
		}

		DataMap dmUser = dsUser.getRow(0);
		String ssjbjgid = dmUser.getString("ssjbjgid");
		if (!StringUtil.chkStrNull(ssjbjgid)) {
			this.sql.setSql(" select jbjgmc from fw.sys_agency where jbjgid = ? ");
			this.sql.setString(1, ssjbjgid);
			DataSet dsTemp = this.sql.executeQuery();
			if (dsTemp.size() > 0) {
				dmUser.put("ssjbjgid", dsTemp.getString(0, "jbjgmc"));
			}
		}

		DataMap rdm = new DataMap();
		rdm.put("userinfo", dmUser);
		return rdm;
	}

	/**
	 * 个人信息修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-3-29
	 * @since V1.0
	 */
	public final DataMap saveLoginUserInfoModify(final DataMap para) throws Exception {
		String zjlx = para.getString("zjlx");
		String zjhm = para.getString("zjhm");
		String ssjgmc = para.getString("ssjgmc");
		String ssjgbm = para.getString("ssjgbm");
		String sjhm = para.getString("sjhm");
		String dzyx = para.getString("dzyx");

		// 更新用户信息
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" update fw.sys_user ");
		sqlBF.append("    set zjlx = ?, zjhm = ?, ssjgmc = ?, ssjgbm = ?, sjhm = ?, dzyx = ? ");
		sqlBF.append("  where yhid = ? ");

		this.sql.setSql(sqlBF.toString());

		this.sql.setString(1, zjlx);
		this.sql.setString(2, zjhm);
		this.sql.setString(3, ssjgmc);
		this.sql.setString(4, ssjgbm);
		this.sql.setString(5, sjhm);

		this.sql.setString(6, dzyx);
		this.sql.setString(7, this.getYhid());
		this.sql.executeUpdate();

		// 更新用户缓存信息
		SysUser user = this.getSysUser();
		user.setZjlx(zjlx);
		user.setZjhm(zjhm);
		user.getAllInfoDM().put("ssjgmc", ssjgmc);
		user.getAllInfoDM().put("ssjgbm", ssjgbm);
		user.getAllInfoDM().put("sjhm", sjhm);
		user.getAllInfoDM().put("dzyx", dzyx);

		// 记录日志
		this.log("SYS-A-YHXXXG", "用户信息修改", "A", this.getYhid(), this.getYhbh(), this.getYhmc(), "用户信息修改", "yhid="
				+ this.getYhid());

		return null;
	}

	/**
	 * 操作日志查询
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap queryUserBizLogInfo(final DataMap para) throws Exception {
		String qsrq = para.getString("qsrq");
		String zzrq = para.getString("zzrq");

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select rzid, czlx, czmc, ztlx, ztid, ");
		sqlBF.append("        ztbh, ztmc, czsm, data, to_char(czsj, 'yyyymmddhh24miss') czsj, ");
		sqlBF.append("        czip, czyid, hs ");
		sqlBF.append("   from fw.biz_log ");
		sqlBF.append("  where czyid = ? ");
		if (!StringUtil.chkStrNull(qsrq)) {
			sqlBF.append("    and to_char(czsj, 'yyyymmdd') >= '" + qsrq + "' ");
		}
		if (!StringUtil.chkStrNull(zzrq)) {
			sqlBF.append("    and to_char(czsj, 'yyyymmdd') <= '" + zzrq + "' ");
		}
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, this.getYhid());
		DataSet dsLog = this.sql.executeQuery();
		dsLog.sortdesc("czsj");

		DataMap rdm = new DataMap();
		rdm.put("dslog", dsLog);
		return rdm;
	}
}
