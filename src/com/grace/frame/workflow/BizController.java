package com.grace.frame.workflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.exception.BoxException;
import com.grace.frame.exception.FieldTipException;
import com.grace.frame.exception.RedirectException;
import com.grace.frame.redis.RedisLock;
import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.BizCacheUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.ProgressBar;
import com.grace.frame.util.SecUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysLogUtil;
import com.grace.frame.util.SysUser;

/**
 * controller的超类，所有的controller均继承自该超类 <br>
 * 注意：<br>
 * 继承自该类的子类，禁止在该类中定义类变量和书写业务逻辑，<br>
 * 因为该类可能在连接过程中，存在持久化连接的情况。所以禁止以上操作。
 * 
 * @author yjc
 */
public class BizController extends MultiActionController{
	/**
	 * 获取系统当前登录用户
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public SysUser getSysUser(HttpServletRequest request) {
		SysUser user = (SysUser) request.getSession()
			.getAttribute("currentsysuser");
		return user;
	}

	/**
	 * 获取IP
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-5-10
	 * @since V1.0
	 */
	public String getIp(DataMap para) throws AppException {
		return para.getString("__ip");
	}

	/**
	 * 执行biz的method方法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected DataMap doBizMethod(String bizName, String methodName,
			DataMap para) throws Exception {
		return DelegatorUtil.execute(bizName, methodName, para);
	}

	/**
	 *异步bar的方式执行biz方法，主体方法会立即返回（无返回值），通过异步bar携带新开线程数据返回前台<br>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected void doBizMethodByAsynBar(String bizName, String methodName,
			DataMap para) throws Exception {
		if (bizName == null || "".equalsIgnoreCase(bizName)) {
			throw new AppException("传入参数[bizName]为空!", "BizController");
		}
		if (methodName == null || "".equalsIgnoreCase(methodName)) {
			throw new AppException("传入参数[methodName]为空!", "BizController");
		}
		if (para == null) {
			throw new AppException("传入参数[para]为空!", "BizController");
		}
		try {
			Class.forName(bizName);
		} catch (ClassNotFoundException e) {
			throw new AppException("Class:" + bizName + "没有发现。", "BizController");
		}

		// 调用biz，标识需要对数据进行操作，并且存在业务逻辑，此处需要检测数据库版本号和业务系统版本号是否一致，不一致的给出报错
		// 1.检测框架版本号
		if (!GlobalVars.FRAME_VERSION.equalsIgnoreCase(GlobalVars.DB_FRAME_VERSION)) {
			throw new AppException("系统框架程序版本号和数据库版本号不一致，不允许进行业务操作。");
		}
		// 2.对于业务版本号，在运行模式下，不允许不一致
		if (!GlobalVars.APP_VERSION.equalsIgnoreCase(GlobalVars.DB_APP_VERSION)) {
			if (GlobalVars.DEBUG_MODE) {
				System.out.println("*警告*业务系统版本号和数据库版本号不一致，请及时更新系统程序或者升级业务系统数据库版本号。");
			} else {
				throw new AppException("业务系统程序版本号和数据库版本号不一致，不允许进行业务操作。");
			}
		}

		// 开始执行异步bar操作
		DataMap bizPara = DelegatorUtil.genBizparaFromPara(para);
		HttpServletResponse response = (HttpServletResponse) bizPara.get("response");
		ProgressBar bar = ProgressBar.getProgressBarFromPara(para);

		if (null == bar) {
			throw new AppException("没有传入进度条管控对象[ProgressBar]，调用失败。");
		} else {
			bar.enableAsynBar();// 启动异步bar
			bizPara.put("progressbar", bar);

			// 启动新的线程
			ThreadDelegator td = new ThreadDelegator();
			td.startThread(bizName, methodName, para, bizPara);

			// 调用response向前台输出已经切换异步pgbar进行业务操作--主方法和controller方法一律返回null
			if (null != response) {
				ActionUtil.writeMessageToResponse(response, ProgressBar.AsynBarReturnFlagStr);
			}
		}
	}

	/**
	 * 执行biz的method方法-使用分布式锁保证唯一<br>
	 * lockKey:锁定key.<br>
	 * expireTime:锁失效自动超时时间：毫秒<br>
	 * delay：如果获取不到锁，延迟执行时间：毫秒<br>
	 * attemptLimit:尝试次数<br>
	 *一般attemptLimit和delay配合使用。delay最优取值为该业务操作平均耗时。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected DataMap doBizMethodByLock(String bizName, String methodName,
			DataMap para, String lockKey, long expireTime, long delay,
			int attemptLimit) throws Exception {
		String requestId = null;
		try {
			requestId = StringUtil.getUUID();
			if (RedisLock.tryLock(lockKey, requestId, expireTime, delay, attemptLimit)) {
				return DelegatorUtil.execute(bizName, methodName, para);
			} else {
				throw new BizException("系统资源（Lock:" + lockKey + "）正忙，请稍候重试。");
			}
		} finally {
			RedisLock.unlock(lockKey, requestId);
		}
	}

	/**
	 * 执行biz的method方法-使用分布式锁保证唯一<br>
	 * lockKey:锁定key.<br>
	 * <p>
	 * 默认：过期半小时。延时200，重试50次：即等待超过10秒中这返回异常。
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected DataMap doBizMethodByLock(String bizName, String methodName,
			DataMap para, String lockKey) throws Exception {
		return this.doBizMethodByLock(bizName, methodName, para, lockKey, 1800000, 200, 50);
	}

	/**
	 * 主要用于数据查询比较慢的逻辑中，对于业务操作禁止使用 <br>
	 * 执行biz的method方法-使用缓存。<br>
	 * timeLimit:超时时间，缓存数据有效期,单位秒，如果为0，则意味着不会超时 <br>
	 * 全局缓存<br>
	 * <br>
	 * resetCache:是否强制重置该biz的缓存。如果为true则不管有没有在有效期内，会将其对应缓存内容重置掉。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected DataMap doQueryBizMethodByCache(String bizName,
			String methodName, DataMap para, long timeLimit, boolean resetCache) throws Exception {

		// 移除无关参数[框架级参数，该处无需进行计算key]
		DataMap paraTemp = para.clone();
		paraTemp.remove("__jbjgid");
		paraTemp.remove("__jbjgqxfw");
		paraTemp.remove("__yhid");
		paraTemp.remove("__ip");
		paraTemp.remove("__sysuser");
		paraTemp.remove("__request");
		paraTemp.remove("__response");
		// md5计算key值
		StringBuffer keySrcBF = new StringBuffer();
		keySrcBF.append(bizName)
			.append(".")
			.append(methodName)
			.append("$")
			.append(paraTemp.toJsonString());
		String key = SecUtil.encodeStrByOriginalMd5(keySrcBF.toString());
		if (!resetCache) {
			DataMap rdm = BizCacheUtil.get(key, timeLimit);
			if (null != rdm) {
				return rdm;
			}
		}
		// 真正执行的函数
		DataMap rdm = DelegatorUtil.execute(bizName, methodName, para);
		BizCacheUtil.put(key, rdm, timeLimit);// 放入缓存
		return rdm;
	}

	/**
	 * 主要用于数据查询比较慢的逻辑中，对于业务操作禁止使用 <br>
	 * 执行biz的method方法-使用缓存。 <br>
	 * * timeLimit:超时时间，缓存数据有效期,单位秒，如果为0，则意味着不会超时 <br>
	 * 全局缓存<br>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected DataMap doQueryBizMethodByCache(String bizName,
			String methodName, DataMap para, long timeLimit) throws Exception {
		return this.doQueryBizMethodByCache(bizName, methodName, para, timeLimit, false);
	}

	/**
	 * 过滤安全的字符串-主要是xss攻击字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2020-10-10
	 * @since V1.0
	 */
	private String filterSafeStr(String str) {
		if (null == str) {
			str = "NULL";
		}
		str = HtmlUtils.htmlEscape(str);
		str = str.replace("&bull;", "•")
			.replace("%3C", "&lt;")
			.replace("%3E", "&gt;")
			.replace("(", "（")
			.replace(")", "）")
			.replace("%28", "（")
			.replace("%29", "）");
		return str;
	}

	/**
	 * 用来处理系统异常类
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public ModelAndView handleException(HttpServletRequest request,
			HttpServletResponse response, Throwable ex) {
		ModelAndView model = null;
		if (ex instanceof BizException) {// 业务异常alter提示
			model = new ModelAndView("/frame/jsp/errmsg/bizErrMsg.jsp", "message", this.filterSafeStr(ex.getMessage()));
		} else if (ex instanceof BoxException) {// 前台提示框的样式
			model = new ModelAndView("/frame/jsp/errmsg/boxErrMsg.jsp", "message", this.filterSafeStr(ex.getMessage()));
		} else if (ex instanceof RedirectException) {// 重定向异常
			RedirectException rex = (RedirectException) ex;
			DataMap dmErr = new DataMap();
			dmErr.put("errtext", this.filterSafeStr(rex.getErrText()));
			dmErr.put("redirect_url", rex.getRedirectUrl());
			model = new ModelAndView("/frame/jsp/errmsg/redirectErrMsg.jsp", dmErr);
		} else if (ex instanceof FieldTipException) {// 信息项空异常
			FieldTipException fex = (FieldTipException) ex;
			DataMap dmErr = new DataMap();
			dmErr.put("errtext", this.filterSafeStr(fex.getErrText()));
			dmErr.put("fieldid", fex.getFieldId());
			model = new ModelAndView("/frame/jsp/errmsg/fieldTipErrMsg.jsp", dmErr);
		} else {// 其他类型异常报红错。
			ex.printStackTrace();// 控制台打印错误信息

			// 如将错误日志记录到服务器上
			// 用户信息
			SysUser currentSysUser = (SysUser) request.getSession()
				.getAttribute("currentsysuser");
			if (null == currentSysUser) {
				currentSysUser = new SysUser();
			}
			SysLogUtil.logError(BizController.class, "系统抛出系统级异常："
					+ ex.getMessage() + "[详细数据信息："
					+ request.getParameterMap().toString() + "登录用户信息："
					+ currentSysUser.toString() + "]", ex);

			// 后台转换错误信息
			if (GlobalVars.DEBUG_MODE) {
				java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(cw, true);
				ex.printStackTrace(pw);
				request.setAttribute("exception", this.filterSafeStr(cw.toString()));
			} else {
				request.setAttribute("exception", "");
			}
			String message = this.filterSafeStr(ex.getMessage());
			model = new ModelAndView("/frame/jsp/errmsg/appErrMsg.jsp", "message", message);
		}
		return model;
	}

	/**
	 * 刷新前台的业务数据-可以刷新前台的grid和form数据;建议只用来刷前台grid和form，<br>
	 * 结构为：key1->dataset1,key2-> dataset2...
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-6-5
	 * @since V1.0
	 */
	protected ModelAndView refreshData(HttpServletResponse response,
			DataMap para) throws AppException {
		ActionUtil.writeDataMapToResponse(response, para);
		return null;
	}
}
