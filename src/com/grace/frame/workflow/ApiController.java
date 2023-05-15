package com.grace.frame.workflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.redis.RedisLock;
import com.grace.frame.util.AccessTokenMap;
import com.grace.frame.util.BizCacheUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.SecUtil;
import com.grace.frame.util.StringUtil;

/**
 * 服务controller-所有的服务实现需要继承本controller
 * 
 * @author yjc
 */
public class ApiController{
	private String ip;// ip
	private String accessToken;// 请求令牌
	private AccessTokenMap accessTokenMap;

	/**
	 * 初始化操作-禁止调用
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-11-15
	 * @since V1.0
	 */
	public void init(DataMap para, HttpServletRequest request,
			HttpServletResponse response) throws AppException {
		this.ip = para.getString("__ip");
		this.accessToken = para.getString("__access_token");
		this.accessTokenMap = AccessTokenMap.fromRequest(request);
	}

	/**
	 * 获取IP
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-5-10
	 * @since V1.0
	 */
	public String getIp() {
		return this.ip;
	}

	/**
	 * 获取IP
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-5-10
	 * @since V1.0
	 */
	public String getAccessToken() {
		return this.accessToken;
	}

	public AccessTokenMap getAccessTokenMap() {
		return this.accessTokenMap;
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
}
