package com.grace.frame.urm;

import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DateUtil;
import com.grace.frame.workflow.BizController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户权限管理的Controller
 * 
 * @author yjc
 */
public class UrmController extends BizController{
	/**
	 * 进入用户管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/pageSysUserMng.jsp", para);
	}
	/**
	 * 进入考生管理页面
	 *
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdStdUserMng(HttpServletRequest request,
									  HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/pageStdUserMng.jsp", para);
	}

	/**
	 * 选择用户
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdChooseSysUser(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdChooseSysUser", para);
		return new ModelAndView("frame/jsp/urm/winChooseSysUser.jsp", dm);
	}
	/**
	 * 选择考生
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdChooseStdUser(HttpServletRequest request,
										 HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.urm.biz.StdUserMngBiz", "fwdChooseStdUser", para);
		return new ModelAndView("frame/jsp/urm/winChooseStdUser.jsp", dm);
	}

	/**
	 * 进入用户新增页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserAdd", para);
		return new ModelAndView("frame/jsp/urm/winSysUserAdd.jsp", dm);
	}
	/**
	 * 进入考生新增页面
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdStdUserAdd(HttpServletRequest request,
									  HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.urm.biz.StdUserMngBiz", "fwdStdUserAdd", para);
		return new ModelAndView("frame/jsp/urm/winStdUserAdd.jsp", dm);
	}

	/**
	 * 保存用户新增。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveSysUserAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserAdd", para);
		ActionUtil.writeDataMapToResponse(response, dm);
		return null;
	}
	/**
	 * 保存考生新增。
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveStdUserAdd(HttpServletRequest request,
									   HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.urm.biz.StdUserMngBiz", "saveStdUserAdd", para);
		ActionUtil.writeDataMapToResponse(response, dm);
		return null;
	}

	/**
	 * 进入个人功能权限管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserGnqxMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/winSysUserGnqxMng.jsp", para);
	}



	/**
	 * 保存用户功能权限。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveSysUserGnqxMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserGnqxMng", para);
		return null;
	}

	/**
	 * 进入个人数据访问权限管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserSjqxMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserSjqxMng", para);
		return new ModelAndView("frame/jsp/urm/winSysUserSjqxMng.jsp", rdm);
	}

	/**
	 * 查询数据权限
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView queryeSysUserSjqx(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "queryeSysUserSjqx", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 保存用户数据权限删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView deleteSysUserSjqx(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "deleteSysUserSjqx", para);
		return null;
	}

	/**
	 * 数据权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserSjqxAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserSjqxAdd", para);
		return new ModelAndView("frame/jsp/urm/winSysUserSjqxAdd.jsp", rdm);
	}

	/**
	 * 保存用户数据权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveSysUserSjqxAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserSjqxAdd", para);
		return null;
	}

	/**
	 * 特殊权限管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserTsqxMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserJsglMng", para);
		return new ModelAndView("frame/jsp/urm/winSysUserTsqxMng.jsp", rdm);
	}

	/**
	 * 查询特殊权限
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView queryeSysUserTsqx(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "queryeSysUserTsqx", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 保存用户特殊权限删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView deleteSysUserTsqx(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "deleteSysUserTsqx", para);
		return null;
	}

	/**
	 * 特殊权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserTsqxAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserTsqxAdd", para);
		return new ModelAndView("frame/jsp/urm/winSysUserTsqxAdd.jsp", rdm);
	}

	/**
	 * 特殊用户数据权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveSysUserTsqxAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserTsqxAdd", para);
		return null;
	}

	/**
	 * 服务访问权限管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserFwfwqxMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserFwfwqxMng", para);
		return new ModelAndView("frame/jsp/urm/winSysUserFwfwqxMng.jsp", rdm);
	}

	/**
	 * 查询服务访问权限
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView queryeSysUserFwfwqx(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "queryeSysUserFwfwqx", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 保存用户服务访问权限删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView deleteSysUserFwfwqx(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "deleteSysUserFwfwqx", para);
		return null;
	}

	/**
	 * 服务访问权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserFwfwqxAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserFwfwqxAdd", para);
		return new ModelAndView("frame/jsp/urm/winSysUserFwfwqxAdd.jsp", rdm);
	}

	/**
	 * 服务访问权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveSysUserFwfwqxAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserFwfwqxAdd", para);
		return null;
	}

	/**
	 * 角色管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserJsglMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserJsglMng", para);
		return new ModelAndView("frame/jsp/urm/winSysUserJsglMng.jsp", rdm);
	}

	/**
	 * 查询角色管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView queryeSysUserJsgl(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "queryeSysUserJsgl", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 保存用户角色管理删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView deleteSysUserJsgl(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "deleteSysUserJsgl", para);
		return null;
	}

	/**
	 * 角色管理新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserJsglAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserJsglAdd", para);
		return new ModelAndView("frame/jsp/urm/winSysUserJsglAdd.jsp", rdm);
	}

	/**
	 * 角色管理新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveSysUserJsglAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserJsglAdd", para);
		return null;
	}

	/**
	 * 个人信息管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserGrxxglMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserGrxxglMng", para);
		return new ModelAndView("frame/jsp/urm/winSysUserGrxxglMng.jsp", rdm);
	}

	/**
	 * 考生信息管理页面
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdStdUserGrxxglMng(HttpServletRequest request,
											HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.StdUserMngBiz", "fwdStdUserGrxxglMng", para);
		return new ModelAndView("frame/jsp/urm/winStdUserGrxxglMng.jsp", rdm);
	}

	/**
	 * 密码重置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserResetPwd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/winSysUserResetPwd.jsp", para);
	}

	/**
	 * 密码重置
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdStdUserDel(HttpServletRequest request,
										   HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/winStdUserDel.jsp", para);
	}

	/**
	 * 密码重置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView saveSysUserPwdReset(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserPwdReset", para);
		return null;
	}

	/**
	 * 确定删除考生
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView saveStdUserDel(HttpServletRequest request,
											HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.StdUserMngBiz", "saveStdUserDel", para);
		return null;
	}

	/**
	 * 用户信息注销
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView cancelSysUserDestroy(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "cancelSysUserDestroy", para);
		return null;
	}

	/**
	 * 用户注销页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserDestroy(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		para.put("zxrq", DateUtil.dateToString(DateUtil.getDBTime(), "yyyyMMdd"));
		return new ModelAndView("frame/jsp/urm/winSysUserDestroy.jsp", para);
	}

	/**
	 * 保存用户注销
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView saveSysUserDestroy(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserDestroy", para);
		return null;
	}

	/**
	 * 用户信息修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserInfoModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "fwdSysUserInfoModify", para);
		return new ModelAndView("frame/jsp/urm/winSysUserInfoModify.jsp", rdm);
	}

	/**
	 * 保存用户信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView saveSysUserModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "saveSysUserModify", para);
		return null;
	}

	/**
	 * 考生信息修改
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdStdUserInfoModify(HttpServletRequest request,
											 HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.StdUserMngBiz", "fwdStdUserInfoModify", para);
		return new ModelAndView("frame/jsp/urm/winStdUserInfoModify.jsp", rdm);
	}

	/**
	 * 保存考生信息
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView saveStdUserModify(HttpServletRequest request,
										  HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.StdUserMngBiz", "saveStdUserModify", para);
		return null;
	}


	/**
	 * 操作日志查询
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysUserCzrzView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/winSysUserCzrzView.jsp", para);
	}

	/**
	 * 操作日志
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView querySysUserBizLog(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysUserMngBiz", "querySysUserBizLog", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 进入用户角色管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-12
	 * @since V1.0
	 */
	public ModelAndView fwdSysRoleMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/pageSysRoleMng.jsp", para);
	}

	/**
	 * 选择角色
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdChooseSysRole(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "fwdChooseSysRole", para);
		return new ModelAndView("frame/jsp/urm/winChooseSysRole.jsp", dm);
	}

	/**
	 * 进入角色新增页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdSysRoleAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/winSysRoleAdd.jsp", para);
	}

	/**
	 * 保存角色新增。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveSysRoleAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "saveSysRoleAdd", para);
		ActionUtil.writeDataMapToResponse(response, dm);
		return null;
	}

	/**
	 * 进入角色功能权限管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView fwdSysRoleGnqxMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/winSysRoleGnqxMng.jsp", para);
	}

	/**
	 * 角色信息管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysRoleInfoMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "fwdSysRoleInfoMng", para);
		return new ModelAndView("frame/jsp/urm/winSysRoleInfoMng.jsp", rdm);
	}

	/**
	 * 保存角色功能权限。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public ModelAndView saveSysRoleGnqxMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "saveSysRoleGnqxMng", para);
		return null;
	}

	/**
	 * 操作日志查询
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysRoleCzrzView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/urm/winSysRoleCzrzView.jsp", para);
	}

	/**
	 * 操作日志
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView querySysRoleBizLog(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "querySysRoleBizLog", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 角色信息修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdSysRoleInfoModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "fwdSysRoleInfoModify", para);
		return new ModelAndView("frame/jsp/urm/winSysRoleInfoModify.jsp", rdm);
	}

	/**
	 * 角色信息修改。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-12
	 * @since V1.0
	 */
	public ModelAndView saveSysRoleModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "saveSysRoleModify", para);
		return null;
	}

	/**
	 * 角色信息删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-12
	 * @since V1.0
	 */
	public ModelAndView saveSysRoleDel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "saveSysRoleDel", para);
		return null;
	}

	/**
	 * 对用户的信息进行查询统计操作。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-22
	 * @since V1.0
	 */
	public ModelAndView fwdQuerySysUser(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.UrmQueryBiz", "fwdQuerySysUser", para);
		return new ModelAndView("frame/jsp/urm/pageSysUserQuery.jsp", rdm);
	}

	/**
	 * 查询用户信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-22
	 * @since V1.0
	 */
	public ModelAndView querySysUserInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.UrmQueryBiz", "querySysUserInfo", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 批量将该角色分配给用户
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-22
	 * @since V1.0
	 */
	public ModelAndView fwdSysRoleUserBatchAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "fwdSysRoleUserBatchAdd", para);
		return new ModelAndView("frame/jsp/urm/winSysRoleUserBatchAdd.jsp", rdm);
	}

	/**
	 * 批量将该角色分配给用户-保存
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-22
	 * @since V1.0
	 */
	public ModelAndView saveSysRoleUserBatchAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "saveSysRoleUserBatchAdd", para);
		return null;
	}

	/**
	 * 批量将该角色分配给用户-删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-22
	 * @since V1.0
	 */
	public ModelAndView saveSysRoleUserBatchDel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.urm.biz.SysRoleMngBiz", "saveSysRoleUserBatchDel", para);
		return null;
	}
}
