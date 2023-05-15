package com.grace.frame.util;

import javax.servlet.http.HttpServletRequest;

import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.ServiceException;
import com.grace.frame.redis.RedisUtil;

/**
 * accessTokenMap
 * <p>
 * 对于accessToken进行获取
 * </p>
 * 
 * @author yjc
 */
public class AccessTokenMap{
	// token有效期，默认60分钟，单位分钟--参数在web.xml中配置
	public static int AccessTokenTimeout = 60;
	private static final String KEY_PREFIX = "token_";// key德强前缀
	private DataMap data;// 真正数据
	private boolean init;// 是否初始化
	private boolean modify;// 数据是否被变更
	private String accessToken;// token

	/**
	 * 生成一个随机令牌
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2018-3-30
	 * @since V1.0
	 */
	public static AccessTokenMap genToken() throws Exception {
		UUIDGenerator generator = UUIDGenerator.getInstance();
		UUID uuid = generator.generateRandomBasedUUID();
		String token = uuid.toString();
		token = token.replace("-", "");
		token = AccessTokenMap.KEY_PREFIX + GlobalVars.SYS_DBID
				+ token.toUpperCase();
		DataMap map = new DataMap();
		AccessTokenMap atm = new AccessTokenMap(token, map);
		atm.modify = true;// 将moidfy改为true，因为是新生成，所以需要向redis同步。
		return atm;
	}

	/**
	 * 从request中获取map数据
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public static AccessTokenMap fromRequest(HttpServletRequest request) {
		if (null == request) {
			return null;
		}
		AccessTokenMap atm = (AccessTokenMap) request.getAttribute("__access_token_map");
		return atm;
	}

	/**
	 * 构造
	 * 
	 * @throws AppException
	 */
	public AccessTokenMap(String accessToken) throws AppException {
		if (StringUtil.chkStrNull(accessToken)) {
			throw new AppException("传入的AccessToken为空");
		}
		this.accessToken = accessToken;
		this.data = null;
		this.init = false;
		this.modify = false;
	}

	/**
	 * 数据初始化
	 * 
	 * @param data
	 * @throws AppException
	 */
	private AccessTokenMap(String accessToken, DataMap data) throws AppException {
		this(accessToken);
		this.init(data);
	}

	/**
	 * 初始化
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	private void init(DataMap data) throws AppException {
		if (this.init) {
			throw new AppException("AccessTokenMap已经初始化，禁止再次初始化。");
		}
		this.data = data;
		this.init = true;
	}

	/**
	 * 设置数据
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public void set(String key, Object para) throws Exception {
		if (!this.init) {
			DataMap data = RedisUtil.getDataMap(this.accessToken, AccessTokenMap.AccessTokenTimeout * 60);
			if (null == data) {// 对于redis中不存在的key，报出异常
				throw new ServiceException(ServiceException.IllegalToken_ERROR, this.accessToken
						+ "在redis缓存中不存在");
			}
			this.init(data);
		}
		this.data.put(key, para);
		this.modify = true;
	}

	/**
	 * 获取数据
	 * <p>
	 * 对于获取到的应用对象的值，更改后一定要重新set回去-在在token中正式生效。
	 * </p>
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public Object get(String key) throws Exception {
		if (!this.init) {
			DataMap data = RedisUtil.getDataMap(this.accessToken, AccessTokenMap.AccessTokenTimeout * 60);
			if (null == data) {// 对于redis总不存在的key，直接返回null,并不进行初始化操作。
				return null;
			}
			this.init(data);
		}
		if (this.data.containsKey(key)) {
			return this.data.get(key);
		} else {
			return null;
		}
	}

	/**
	 * 移除key
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public void remove(String key) throws Exception {
		if (!this.init) {
			DataMap data = RedisUtil.getDataMap(this.accessToken, AccessTokenMap.AccessTokenTimeout * 60);
			if (null == data) {// 对于redis中不存在的key，报出异常
				throw new ServiceException(ServiceException.IllegalToken_ERROR, this.accessToken
						+ "在redis缓存中不存在");
			}
			this.init(data);
		}
		if (this.data.containsKey(key)) {
			this.data.remove(key);
			this.modify = true;
		}
	}

	/**
	 * 清空数据
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public void clear() throws Exception {
		if (!this.init) {
			DataMap data = RedisUtil.getDataMap(this.accessToken, AccessTokenMap.AccessTokenTimeout * 60);
			if (null == data) {// 对于redis中不存在的key，报出异常
				throw new ServiceException(ServiceException.IllegalToken_ERROR, this.accessToken
						+ "在redis缓存中不存在");
			}
			this.init(data);
		}
		if (this.data.size() > 0) {
			this.data.clear();
			this.modify = true;
		}
	}

	/**
	 * 验证AccessToken是否在redis中存活
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-11-21
	 * @since V1.0
	 */
	public boolean isActive() throws Exception {
		if (this.init) {
			return true;// 初始化的状态为活动状态
		}
		return RedisUtil.exists(this.accessToken);// 验证是否存在
	}

	/**
	 * 向redis同步数据
	 * 
	 * @author yjc
	 * @throws Exception
	 * @throws AppException
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public void sync() throws AppException, Exception {
		if (this.modify) {// 对于数据变更了的情况同步数据
			RedisUtil.set(this.accessToken, this.data, AccessTokenMap.AccessTokenTimeout * 60);
		}
		// 对于没有初始化的情况，延时缓存时间
		if (!this.init) {
			if (RedisUtil.exists(this.accessToken)) {
				RedisUtil.expire(this.accessToken, AccessTokenMap.AccessTokenTimeout * 60);// 设置超时
			}
		}
	}

	/**
	 * redis直接销毁该key
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public void destroy() throws Exception {
		if (RedisUtil.exists(this.accessToken)) {
			RedisUtil.del(this.accessToken);
		}
	}

	/**
	 * 将该AccessTokenMap重新设置到request中去
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-19
	 * @since V1.0
	 */
	public void reset2Request(HttpServletRequest request) {
		if (null != request) {
			request.setAttribute("__access_token_map", this);
		}
	}

	/**
	 * 数据是否变更了
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public boolean isModify() {
		return this.modify;
	}

	/**
	 * 是否初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public boolean isInit() {
		return init;
	}

	/**
	 * 获取AccessToken
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	public String getAccessToken() {
		return accessToken;
	}
}
