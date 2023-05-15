package com.grace.frame.login;

import com.grace.frame.annotation.PublicAccess;
import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.*;
import com.grace.frame.util.captcha.ClickPoint;
import com.grace.frame.util.captcha.ClickTextCaptcha;
import com.grace.frame.util.captcha.GraphicsEngine;
import com.grace.frame.workflow.BizController;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 系统用户登录的controller
 * 
 * @author yjc
 */
public class LoginController extends BizController{
	public ModelAndView chngLoginPage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("loginPage_" + GlobalVars.VIEW_TYPE + ".jsp");
	}

	/**
	 * 生成验证码信息
	 * 
	 * @author yjc
	 * @date 创建时间 2020-4-28
	 * @since V1.0
	 */
	@PublicAccess
	public ModelAndView genCaptcha(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		ClickTextCaptcha captcha = GraphicsEngine.genClickTextCaptcha();// 生成验证码
		request.getSession().setAttribute("login_click_text_captcha", captcha);
		// 写向前台
		DataMap rdm = new DataMap();
		rdm.put("words", captcha.getChkWords());
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 * 获取图片
	 * 
	 * @author yjc
	 * @date 创建时间 2020-4-28
	 * @since V1.0
	 */
	@PublicAccess
	public ModelAndView getCaptchaImage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		ClickTextCaptcha captcha = (ClickTextCaptcha) request.getSession()
			.getAttribute("login_click_text_captcha");
		if (null == captcha) {
			return null;
		}
		ImageIO.write(captcha.getImage(), "png", response.getOutputStream());
		return null;
	}

	/**
	 * 检测验证码是否输入正确
	 * 
	 * @author yjc
	 * @date 创建时间 2020-4-29
	 * @since V1.0
	 */
	@PublicAccess
	public ModelAndView chkCaptcha(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		boolean pass = false;
		ClickTextCaptcha captcha = (ClickTextCaptcha) request.getSession()
			.getAttribute("login_click_text_captcha");
		request.getSession().removeAttribute("login_click_text_captcha");

		// 获取点击点
		String strPoints = para.getString("points");
		DataSet dsPoints = DataSet.fromObject(strPoints);
		List<ClickPoint> points = new ArrayList<ClickPoint>();
		for (DataMap dmPoints : dsPoints) {
			ClickPoint point = new ClickPoint();
			point.setX(dmPoints.getInt("x"));
			point.setY(dmPoints.getInt("y"));
			points.add(point);
		}
		if (null != captcha) {
			pass = captcha.check(points);
		}
		if (pass) {// 实人认证通过
			ClickTextCaptcha.setValidatePass(request.getSession());
		}

		DataMap rdm = new DataMap();
		rdm.put("pass", pass);
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}
	
	/**
	 * 系统登录操作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-2
	 * @since V1.0
	 * @since yjc.2015-11-24-首先获取用户信息，放置数据库连接不上时，提示的错误信息有误。
	 */
	public ModelAndView doLogin(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String yhbh = para.getString("yhbh");
		String pwd = para.getString("pwd");
		String msg = "";// 在登录过程中，想前台输出的提示信息

		if (null == yhbh || "".equals(yhbh)) {
			throw new BizException("请输入用户编号后再进行登录。");
		}
		if (null == pwd || "".equals(pwd)) {
			throw new BizException("请输入用户登录密码后再进行登录。");
		}

		/**
		 * 首先检测是否登录成功
		 */
		DataMap dm = LoginUtil.chkLoginRight(yhbh, pwd);
		if (!"0".equals(dm.getString("code"))) {
			throw new BizException("用户名或者用户密码输入的不正确，无法进行登录。");
		}
		SysUser user = (SysUser) dm.get("sysuser");

		// 判断用户状态
		if ("0".equals(user.getAllInfoDM().getString("yhzt"))) {
			throw new BizException("该用户已经注销，无法进行系统登录。");
		}

		// 对于同一个浏览器，只允许一个用户登录的验证
		if (!LoginUtil.chkCanLoginBySessionUserRelation(request, user)) {
			throw new BizException("同一浏览器只能登录一个用户，需要登录其他用户请先退出当前用户，如果已经退出仍然提示该信息，请关闭所有浏览器后重试。");
		}

		/**
		 * 检查系统版本号是否正确
		 */
		// 1.检测框架版本号
		if (!GlobalVars.FRAME_VERSION.equalsIgnoreCase(GlobalVars.DB_FRAME_VERSION)) {
			throw new BizException("系统框架程序版本号和数据库版本号不一致，不允许进行业务操作。");
		}

		// 2.对于业务版本号，在运行模式下，不允许不一致
		if (!GlobalVars.APP_VERSION.equalsIgnoreCase(GlobalVars.DB_APP_VERSION)) {
			if (GlobalVars.DEBUG_MODE) {
				msg = "*调试警告*业务系统版本号和数据库版本号不一致，请及时更新系统程序或者升级业务系统数据库版本号。";
				System.out.println(msg);
			} else {
				throw new AppException("业务系统程序版本号和数据库版本号不一致，不允许进行业务操作。");
			}
		}

		// 初始化用户的业务功能菜单map
		// 首先查看用户的类别，超级管理员，不判断权限，全部加载，普通业务用户根据角色和权限配置加载，服务用户不允许登录系统
		DataMap btnFuncMap = new DataMap();
		String yhjbjgqxfw = "";
		if ("A".equals(user.getYhlx())) {// 超级管理员
			btnFuncMap = LoginUtil.getBtnFuncListMap4AUser();
		} else if ("B".equals(user.getYhlx())) {// 普通业务操作人员
			btnFuncMap = LoginUtil.getBtnFuncListMap4BUser(user.getYhid());
			yhjbjgqxfw = LoginUtil.getUserDataRightStr(user.getYhid());
		} else {
			throw new BizException("该用户为服务注册用户，不允许登录业务系统。");
		}
		user.getAllInfoDM().put("btnfuncmap", btnFuncMap);// 权限map
		user.getAllInfoDM().put("yhjbjgqxfw", yhjbjgqxfw);

		// 记录系统日志
		SysLogUtil.logInfo("用户登录[" + yhbh + "]");

		// 预防会话未更新的问题。
		HttpSession session = request.getSession();
		session.invalidate();
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (int i = 0; i < cookies.length; i++) {
				if (("JSESSIONID").equalsIgnoreCase(cookies[i].getName())) {
					cookies[i].setMaxAge(0);
				}
			}
		}
		// 将数据放入session中
		request.getSession().setAttribute("currentsysuser", user);

		// 向前台反馈登录结果
		DataMap rdm = new DataMap();
		rdm.put("flag", "1");// 标识登录成功
		rdm.put("msg", msg);// 提示信息，为空不提示

		// 用户登录，需要记录用户的登录的业务级日志
		BizLogUtil.saveBizLog("SYS-A-USERLOGIN", "系统用户登录操作", "A", user.getYhid(), user.getYhbh(), user.getYhmc(), "系统用户登录操作", "yhid="
				+ user.getYhid(), ActionUtil.getRemoteHost(request), user.getYhid(), 0);

		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 * 转向业务主管理界面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-4
	 * @since V1.0
	 */
	public ModelAndView fwdMainPage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.login.LoginBiz", "fwdMainPage", para);
		return new ModelAndView("index_" + GlobalVars.VIEW_TYPE + ".jsp", dm);
	}

	/**
	 * 系统退出
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-2
	 * @since V1.0
	 */
	public ModelAndView doLogout(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		/**
		 * 清空session
		 */
		request.getSession().removeAttribute("currentsysuser");

		return new ModelAndView("loginPage_" + GlobalVars.VIEW_TYPE + ".jsp");// 转向登录页面
	}

	/**
	 * 删除我的功能的某个功能
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-4
	 * @since V1.0
	 */
	public ModelAndView removeMyfunction(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.login.LoginBiz", "removeMyfunction", para);
		return null;
	}

	/**
	 * 清空我的功能
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-4
	 * @since V1.0
	 */
	public ModelAndView clearMyfunction(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.login.LoginBiz", "clearMyfunction", para);
		return null;
	}

	/**
	 * 增加功能节点
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-4
	 * @since V1.0
	 */
	public ModelAndView addMyfunction(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.login.LoginBiz", "addMyfunction", para);
		return null;
	}

	/**
	 * 修改个人密码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	public ModelAndView fwdModifyMyPwd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/user/winModifyMyPwd.jsp");
	}

	/**
	 * 修改个人密码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	public ModelAndView modifyMyPwd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.login.LoginBiz", "modifyMyPwd", para);
		return null;
	}

	/**
	 * 个人信息查看功能。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	public ModelAndView fwdLoginUserPerInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.login.LoginBiz", "fwdLoginUserPerInfo", para);
		return new ModelAndView("frame/jsp/user/winLoginUserPerInfo.jsp", dm);
	}

	/**
	 * 个人信息修改保存功能。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	public ModelAndView saveLoginUserInfoModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.login.LoginBiz", "saveLoginUserInfoModify", para);
		return null;
	}

	/**
	 * 操作日志查询
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView fwdUserBizLogInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/user/winUserBizLogInfo.jsp", para);
	}

	/**
	 * 操作日志
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public ModelAndView queryUserBizLogInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.login.LoginBiz", "queryUserBizLogInfo", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 关于页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-14
	 * @since V1.0
	 */
	public ModelAndView fwdSysAboutInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/user/winSysAboutInfo.jsp", para);
	}

	/**
	 * 重置数据库连接
	 * 
	 * @author yjc
	 * @date 创建时间 2016-10-11
	 * @since V1.0
	 */
	public ModelAndView clearDBLink(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		// 安全验证
		String key = para.getString("key", "");
		if (!key.equals(GlobalVars.APP_ID
				+ DateUtil.dateToString(new Date(), "yyyyMMddHH"))) {
			throw new BizException("安全验证KEY不正确，无法重置DB连接");
		}

		// 连接信息清空
		Sql.clear();

		// 提示成功
		return new ModelAndView("/frame/jsp/errmsg/bizErrMsg.jsp", "message", "数据库连接缓存重置成功");
	}
}
