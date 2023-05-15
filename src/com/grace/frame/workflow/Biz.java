package com.grace.frame.workflow;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.redis.RedisLock;
import com.grace.frame.util.AccessTokenMap;
import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.AgencyUtil;
import com.grace.frame.util.BizLogUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.ProgressBar;
import com.grace.frame.util.Sql;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysUser;

/**
 * 业务逻辑的处理超类
 * 
 * @author yjc
 */
public class Biz{
	protected Sql sql;// sql的操作处理类

	private DataMap bizPara;// biz参数
	private String jbjgid;// 经办机构ID
	private String jbjgqxfw;// 经办机构权限范围
	private String ip;// ip地址
	private SysUser sysUser;// 系统登录用户
	private HttpServletRequest request;// 请求
	private HttpServletResponse response;// 响应

	private Date beginTime;// 起始执行时间

	/**
	 * 构造函数，主要是初始化一些业务对象
	 */
	public Biz() {
		this.sql = new Sql();
	}

	/**
	 * 初始化方法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void init(DataMap bizPara) throws AppException {
		this.jbjgid = bizPara.getString("jbjgid");
		this.jbjgqxfw = bizPara.getString("jbjgqxfw");
		this.ip = bizPara.getString("ip");
		this.sysUser = (SysUser) bizPara.get("sysuser");
		this.request = (HttpServletRequest) bizPara.get("request");
		this.response = (HttpServletResponse) bizPara.get("response");

		this.bizPara = bizPara;
		this.beginTime = new Date();// 获取当前时间
	}

	/**
	 * 执行biz的方法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final DataMap doBizMethod(String bizName, String methodName,
			DataMap para, DataMap bizPara) throws Exception {
		return DelegatorUtil.executeFunc(bizName, methodName, para, bizPara);
	}

	/**
	 * 执行biz的方法-底层封装bizPara-无需再次传递
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final DataMap doBizMethod(String bizName, String methodName,
			DataMap para) throws Exception {
		return this.doBizMethod(bizName, methodName, para, this.getBizPara());
	}

	/**
	 * 执行biz的方法-底层封装bizPara-无需再次传递-使用分布式锁保证唯一<br>
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
	protected final DataMap doBizMethodByLock(String bizName,
			String methodName, DataMap para, String lockKey, long expireTime,
			long delay, int attemptLimit) throws Exception {
		String requestId = null;
		try {
			requestId = StringUtil.getUUID();
			if (RedisLock.tryLock(lockKey, requestId, expireTime, delay, attemptLimit)) {
				return this.doBizMethod(bizName, methodName, para, this.getBizPara());
			} else {
				throw new BizException("系统资源（Lock:" + lockKey + "）正忙，请稍候重试。");
			}
		} finally {
			RedisLock.unlock(lockKey, requestId);
		}
	}

	/**
	 * 执行biz的方法-底层封装bizPara-无需再次传递-使用分布式锁保证唯一<br>
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
	 * 执行biz的方法-重开新事务（后台操作逻辑为新开线程--但是主线程会等待子线程完成任务后才继续执行）--不传递bizPara
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final DataMap doBizMethodOtherTrans(String bizName,
			String methodName, DataMap para) throws Exception {
		return this.doBizMethodOtherTrans(bizName, methodName, para, this.getBizPara());
	}

	/**
	 * 执行biz的方法-重开新事务（后台操作逻辑为新开线程--但是主线程会等待子线程完成任务后才继续执行）
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final DataMap doBizMethodOtherTrans(String bizName,
			String methodName, DataMap para, DataMap bizPara) throws Exception {
		return ThreadDelegator.execute(bizName, methodName, para, bizPara);
	}

	/**
	 * 执行biz的方法-异步执行-新线程（后台操作逻辑为新开线程--但是主线程不会等待子线程完成）--不传递bizPara <br>
	 * <br>
	 *没有返回值， 通过bar的形式进行数据传递和业务操作 </br>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final void doBizMethodByAsynBar(String bizName,
			String methodName, DataMap para, ProgressBar bar) throws Exception {
		if (null == bar) {
			this.doBizMethodOtherThread(bizName, methodName, para, this.getBizPara());
		} else {
			bar.enableAsynBar();// 启动异步bar
			DataMap bizPara = this.getBizPara();
			bizPara.put("progressbar", bar);
			this.doBizMethodOtherThread(bizName, methodName, para, bizPara);
		}
		// 调用response向前台输出已经切换异步pgbar进行业务操作--主方法和controller方法一律返回null
		if (null != this.response) {
			ActionUtil.writeMessageToResponse(this.response, ProgressBar.AsynBarReturnFlagStr);
		}
	}

	/**
	 * 执行biz的方法-异步执行-新线程（后台操作逻辑为新开线程--但是主线程不会等待子线程完成）--不传递bizPara <br>
	 * <br>
	 *没有返回值， 通过bar的形式进行数据传递和业务操作 </br>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final void doBizMethodByAsynBar(String bizName,
			String methodName, DataMap para) throws Exception {
		ProgressBar bar = ProgressBar.getProgressBarFromPara(para);
		this.doBizMethodByAsynBar(bizName, methodName, para, bar);
	}

	/**
	 * 执行biz的方法-异步执行-新线程（后台操作逻辑为新开线程--但是主线程不会等待子线程完成）--不传递bizPara <br>
	 * 没有返回值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final void doBizMethodOtherThread(String bizName,
			String methodName, DataMap para) throws Exception {
		this.doBizMethodOtherThread(bizName, methodName, para, this.getBizPara());
	}

	/**
	 * 执行biz的方法-异步执行-新线程（后台操作逻辑为新开线程--但是主线程不会等待子线程完成） <br>
	 * 没有返回值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final void doBizMethodOtherThread(String bizName,
			String methodName, DataMap para, DataMap bizPara) throws Exception {
		ThreadDelegator td = new ThreadDelegator();
		td.startThread(bizName, methodName, para, bizPara);
	}

	/**
	 * 获取jbjgid
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final String getJbjgid() {
		return this.jbjgid;
	}

	/**
	 * 获取上级jbjgid
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final String getSjJbjgid() throws Exception {
		return AgencyUtil.getSjjbjgid(this.jbjgid);
	}

	/**
	 * 获取上级jbjgid
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final String getSjJbjgid(String jbjgid) throws Exception {
		return AgencyUtil.getSjjbjgid(jbjgid);
	}

	/**
	 * 获取jbjgqxfw
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final String getJbjgqxfw() {
		return this.jbjgqxfw;
	}

	/**
	 * 获取IP
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final String getIp() {
		return this.ip;
	}

	/**
	 * 获取系统登录用户
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final SysUser getSysUser() {
		return this.sysUser;
	}

	/**
	 * 获取系统登录用户ID
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final String getYhid() {
		return this.sysUser.getYhid();
	}

	/**
	 * 获取系统登录用户BH
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final String getYhbh() {
		return this.sysUser.getYhbh();
	}

	/**
	 * 获取系统登录用户MC
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final String getYhmc() {
		return this.sysUser.getYhmc();
	}

	/**
	 * 获取执行业务逻辑的起始时间
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final Date getBeginTime() {
		return this.beginTime;
	}

	/**
	 * 获取bizPara(是浅克隆的返回)
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	protected final DataMap getBizPara() {
		return this.bizPara.clone();
	}

	/**
	 * 获取业务请求
	 * 
	 * @author yjc
	 * @date 创建时间 2016-2-17
	 * @since V1.0
	 */
	protected HttpServletRequest getRequest() {
		return this.request;
	}

	/**
	 * 获取业务响应
	 * 
	 * @author yjc
	 * @date 创建时间 2016-2-17
	 * @since V1.0
	 */
	protected HttpServletResponse getResponse() {
		return this.response;
	}

	/**
	 * 获取AccessTokenMap
	 * 
	 * @author yjc
	 * @date 创建时间 2016-2-17
	 * @since V1.0
	 */
	protected AccessTokenMap getAccessTokenMap() {
		if (this.request != null) {
			return AccessTokenMap.fromRequest(this.request);
		} else {
			return null;
		}
	}

	/**
	 * 记录日志（主要信息）
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final String log(String czlx, String ztlx, String ztid,
			String czsm, String data) throws AppException {
		String rzid = BizLogUtil.saveBizLog(czlx, null, ztlx, ztid, null, null, czsm, data, this.ip, this.getYhid(), this.beginTime);
		this.beginTime = new Date();
		return rzid;
	}

	/**
	 * 记录日志（主要信息）-增加操作名称
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final String log(String czlx, String czmc, String ztlx,
			String ztid, String czsm, String data) throws AppException {
		String rzid = BizLogUtil.saveBizLog(czlx, czmc, ztlx, ztid, null, null, czsm, data, this.ip, this.getYhid(), this.beginTime);
		this.beginTime = new Date();
		return rzid;
	}

	/**
	 * 记录日志(全信息)
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	protected final String log(String czlx, String czmc, String ztlx,
			String ztid, String ztbh, String ztmc, String czsm, String data) throws AppException {
		String rzid = BizLogUtil.saveBizLog(czlx, czmc, ztlx, ztid, ztbh, ztmc, czsm, data, this.ip, this.getYhid(), this.beginTime);
		this.beginTime = new Date();
		return rzid;
	}
}
