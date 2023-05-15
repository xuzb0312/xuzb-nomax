package com.grace.frame.login;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.SecUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.SysUser;

/**
 * 登录相关操作的封装
 * 
 * @author yjc
 */
public class LoginUtil{
	// 对于系统功能不进行检测是否登录controller的map
	public static HashMap<String, Object> NO_CHECK_LOIGN_CONTROLLER_METHOD_NAME = new HashMap<String, Object>();

	/**
	 * 静态变量使用的第一次执行
	 */
	static {
		LoginUtil.NO_CHECK_LOIGN_CONTROLLER_METHOD_NAME.put("com.grace.frame.login.LoginController.chngLoginPage", null);// 选择登录页面
		LoginUtil.NO_CHECK_LOIGN_CONTROLLER_METHOD_NAME.put("com.grace.frame.login.LoginController.doLogin", null);// 系统登录不需要检测登录
		LoginUtil.NO_CHECK_LOIGN_CONTROLLER_METHOD_NAME.put("com.grace.frame.login.LoginController.doLogout", null);// 退出登录不需要检测登录
		LoginUtil.NO_CHECK_LOIGN_CONTROLLER_METHOD_NAME.put("com.grace.frame.service.ServiceController.requestService", null);// 对于服务请求操作不进行验证登录
		LoginUtil.NO_CHECK_LOIGN_CONTROLLER_METHOD_NAME.put("com.grace.frame.login.LoginController.clearDBLink", null);// 重置数据库连接
		LoginUtil.NO_CHECK_LOIGN_CONTROLLER_METHOD_NAME.put("com.grace.frame.taglib.TaglibController.downloadUeditorFile", null);// 重置数据库连接
	}

	/**
	 * 验证会话和用户的关系-登录时使用--返回true运行登录，否则不允许登录
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-23
	 * @since V1.0
	 */
	public static boolean chkCanLoginBySessionUserRelation(
			HttpServletRequest request, SysUser user) {
		if (!LoginUtil.isUserLogin(request)) {
			return true;// 没有人登录
		}
		SysUser currentSysUser = (SysUser) request.getSession()
			.getAttribute("currentsysuser");// 当前用户
		if (user.getYhid().equals(currentSysUser.getYhid())) {
			return true;
		}
		return false;
	}

	/**
	 * 检测用户登录权限: <br>
	 * 反回登录code代码： <br>
	 * 0.登录成功；<br>
	 * 1.系统中用户不存在;<br>
	 * 2.密码错误;<br>
	 * ----------------<br>
	 * 使用sysUser参数将user信息带回
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2015-5-22
	 * @since V1.0
	 * @since 2017年10月19日-对于密码的数据传输前台增加了md5加密策略，该处进行一下特殊处理，并兼容之前的情况
	 */
	public static DataMap chkLoginRight(String yhbh, String pwd) throws AppException, BizException {
		DataMap dm = new DataMap();

		// 查询到用户
		SysUser sysUser = SysUser.bulidSysUserByYhbh(yhbh);// 查询到用户
		if (null == sysUser) {
			dm.clear();
			dm.put("code", "1");
			dm.put("sysuser", sysUser);

			return dm;
		}
		// 账户是否处于锁定状态的判断
		Date zhsdz = (Date) sysUser.getAllInfoDM().getDate("zhsdz");
		if (null != zhsdz) {
			Date now = DateUtil.getDBTime();
			if (zhsdz.after(now)) {
				throw new BizException("密码输入错误次数超过3次，账户当前处于锁定状态，请稍候5分钟重试");
			}
		}
		String encodePwd = "";
		// 对密码进行加密操作
		if (32 == pwd.length()) {
			// 如果是32位认为是，已经进行加密的数据串
			encodePwd = pwd.toUpperCase();
		} else {
			encodePwd = SecUtil.encodeStrByMd5(pwd);
		}

		if (!encodePwd.equals(sysUser.getPassword())) {
			int mmcwcs = sysUser.getAllInfoDM().getInt("mmcwcs");
			mmcwcs++;
			if (mmcwcs >= 3) {
				Sql sql = new Sql();
				sql.setSql(" update fw.sys_user set mmcwcs = 0, zhsdz = sysdate + 5 / 24 / 60 where yhid = ? ");
				sql.setString(1, sysUser.getYhid());
				sql.executeUpdate();
			} else {
				Sql sql = new Sql();
				sql.setSql(" update fw.sys_user set mmcwcs = nvl(mmcwcs, 0) + 1 where yhid = ? ");
				sql.setString(1, sysUser.getYhid());
				sql.executeUpdate();
			}
			dm.clear();
			dm.put("code", "2");
			dm.put("sysuser", sysUser);
			return dm;
		} else {
			Sql sql = new Sql();
			sql.setSql(" update fw.sys_user set mmcwcs = null, zhsdz = null where yhid = ? ");
			sql.setString(1, sysUser.getYhid());
			sql.executeUpdate();
		}

		dm.clear();
		dm.put("code", "0");
		dm.put("sysuser", sysUser);
		return dm;
	}

	/**
	 * 判断用户是否登录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-4
	 * @since V1.0
	 */
	public static boolean isUserLogin(HttpServletRequest request) {
		SysUser currentSysUser = (SysUser) request.getSession()
			.getAttribute("currentsysuser");// 当前用户
		if (null == currentSysUser) {
			currentSysUser = new SysUser();
		}
		if (null == currentSysUser.getYhid()
				|| "".equals(currentSysUser.getYhid().trim())) {
			return false;
		}
		return true;
	}

	/**
	 * 初始化按钮权限列表map(A超级管理员)
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-16
	 * @since V1.0
	 */
	public static DataMap getBtnFuncListMap4AUser() throws AppException {
		StringBuffer sqlBF = new StringBuffer();
		Sql sql = new Sql();

		sqlBF.setLength(0);
		sqlBF.append(" select gnid, gnmc ");
		sqlBF.append("   from fw.func ");
		sqlBF.append("  where dbid = ? ");
		sqlBF.append("    and gnlx in ('D') ");

		sql.setSql(sqlBF.toString());
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet ds = sql.executeQuery();

		DataMap dmap = new DataMap();
		for (int i = 0, n = ds.size(); i < n; i++) {
			String gnid = ds.getString(i, "gnid");
			String gnmc = ds.getString(i, "gnmc");
			dmap.put(gnid, gnmc);
		}
		return dmap;
	}

	/**
	 * 初始化按钮权限列表map(B普通业务操作员)
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-6-16
	 * @since V1.0
	 */
	public static DataMap getBtnFuncListMap4BUser(String yhid) throws AppException {
		StringBuffer sqlBF = new StringBuffer();
		Sql sql = new Sql();

		sqlBF.setLength(0);
		sqlBF.append(" select a.gnid, a.gnmc ");
		sqlBF.append("   from fw.func a ");
		sqlBF.append("  where a.dbid = ? ");
		sqlBF.append("    and a.gnlx in ('D') ");
		sqlBF.append("    and (exists (select 'x' ");
		sqlBF.append("                   from fw.user_func b ");
		sqlBF.append("                  where a.gnid = b.gnid ");
		sqlBF.append("                    and b.yhid = ?) or exists ");
		sqlBF.append("         (select 'x' ");
		sqlBF.append("            from fw.user_role c, fw.role_func d ");
		sqlBF.append("           where c.jsid = d.jsid ");
		sqlBF.append("             and d.gnid = a.gnid ");
		sqlBF.append("             and c.yhid = ?)) ");

		sql.setSql(sqlBF.toString());
		sql.setString(1, GlobalVars.SYS_DBID);
		sql.setString(2, yhid);
		sql.setString(3, yhid);
		DataSet ds = sql.executeQuery();

		DataMap dmap = new DataMap();
		for (int i = 0, n = ds.size(); i < n; i++) {
			String gnid = ds.getString(i, "gnid");
			String gnmc = ds.getString(i, "gnmc");
			dmap.put(gnid, gnmc);
		}
		return dmap;
	}

	/**
	 * 对于普通的操作员获取数据权限
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-6-16
	 * @since V1.0
	 */
	public static String getUserDataRightStr(String yhid) throws AppException {
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select jbjgid ");
		sqlBF.append("   from fw.user_data_right a ");
		sqlBF.append("  where exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type b ");
		sqlBF.append("          where a.jbjgid = b.jbjgid ");
		sqlBF.append("            and b.dbid = ?) ");
		sqlBF.append("    and a.yhid = ? ");

		sql.setSql(sqlBF.toString());
		sql.setString(1, GlobalVars.SYS_DBID);
		sql.setString(2, yhid);

		DataSet dsTmp = sql.executeQuery();
		StringBuffer strBf = new StringBuffer();
		for (int i = 0, n = dsTmp.size(); i < n; i++) {
			strBf.append(dsTmp.getString(i, "jbjgid")).append(",");
		}
		if (strBf.length() > 0) {
			strBf.setLength(strBf.length() - 1);
		}
		return strBf.toString();
	}
}
