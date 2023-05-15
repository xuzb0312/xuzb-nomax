package com.grace.frame.util;

import java.util.concurrent.ConcurrentHashMap;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.redis.RedisUtil;

/**
 * 缓存工具类
 * 
 * @author yjc
 */
public class BizCacheUtil{
	// 高速数据缓存区Table-使用线程安全ConcurrentHashMap
	private static ConcurrentHashMap<String, DataMap> DATA_CACHE = new ConcurrentHashMap<String, DataMap>();
	private static final String REDIS_KEY_PREFIX = "bizCache_";

	/**
	 * 放入缓存数据
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-2
	 * @since V1.0
	 */
	private static void put4Table(String key, DataMap data) throws AppException {
		if (null == data) {
			data = new DataMap();
		} else {
			data = data.clone();
		}
		data.put("_puttime", System.currentTimeMillis());
		data.put("_hcsj", DateUtil.dateToString(DateUtil.getDBTime(), "yyyyMMddhhmmss"));// 放入缓存的时间
		BizCacheUtil.DATA_CACHE.put(key, data);
	}

	/**
	 * 放入缓存数据<br>
	 * timeLimit:缓存有效期，如果超过有效期，则即时存在也认为不存在。0永不过期。单位：秒
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-2
	 * @since V1.0
	 */
	private static void put4Redis(String key, DataMap data, int timeLimit) throws Exception {
		if (null == data) {
			data = new DataMap();
		}
		key = BizCacheUtil.REDIS_KEY_PREFIX + key;
		data.put("_puttime", System.currentTimeMillis());
		data.put("_hcsj", DateUtil.dateToString(DateUtil.getDBTime(), "yyyyMMddhhmmss"));// 放入缓存的时间
		if (timeLimit <= 0) {
			RedisUtil.set(key, data);
		} else {
			RedisUtil.set(key, data, timeLimit);
		}
	}

	/**
	 * 放入缓存数据<br>
	 * timeLimit:缓存有效期，如果超过有效期，则即时存在也认为不存在。0永不过期。单位：秒
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-2
	 * @since V1.0
	 */
	public static void put(String key, DataMap data, long timeLimit) throws Exception {
		if (GlobalVars.ENABLED_REDIS) {
			BizCacheUtil.put4Redis(key, data, (int) timeLimit);
		} else {
			BizCacheUtil.put4Table(key, data);
		}
	}

	/**
	 * 从缓存获取数据 <br>
	 * timeLimit:缓存有效期，如果超过有效期，则即时存在也认为不存在。0永不过期。单位：秒
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-2
	 * @since V1.0
	 */
	private static DataMap get4Table(String key, long timeLimit) throws AppException {
		if (timeLimit <= 0) {
			if (BizCacheUtil.DATA_CACHE.containsKey(key)) {
				DataMap data = (DataMap) BizCacheUtil.DATA_CACHE.get(key);
				DataMap rdm = data.clone();
				rdm.remove("_puttime");
				return rdm;
			} else {
				return null;
			}
		}

		if (BizCacheUtil.DATA_CACHE.containsKey(key)) {
			DataMap data = (DataMap) BizCacheUtil.DATA_CACHE.get(key);
			double puttime = (Double) data.get("_puttime");
			double now = System.currentTimeMillis();
			if ((now - puttime) <= (timeLimit * 1000)) {
				DataMap rdm = data.clone();
				rdm.remove("_puttime");
				return rdm;
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 从缓存获取数据 <br>
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-2
	 * @since V1.0
	 */
	private static DataMap get4Redis(String key) throws Exception {
		key = BizCacheUtil.REDIS_KEY_PREFIX + key;
		DataMap data = RedisUtil.getDataMap(key);
		if (null != data) {
			data.remove("_puttime");
		}
		return data;
	}

	/**
	 * 从缓存获取数据 <br>
	 * timeLimit:缓存有效期，如果超过有效期，则即时存在也认为不存在。0永不过期。单位：秒
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-2
	 * @since V1.0
	 */
	public static DataMap get(String key, long timeLimit) throws Exception {
		if (GlobalVars.ENABLED_REDIS) {
			return BizCacheUtil.get4Redis(key);
		} else {
			return BizCacheUtil.get4Table(key, timeLimit);
		}
	}

	/**
	 * 清空缓存操作--防止缓存数据过大，占用系统资源
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static void clear() {
		if (GlobalVars.ENABLED_REDIS) {// 启用的redis则需要清空一下redis的缓存
			try {
				RedisUtil.delMulti(BizCacheUtil.REDIS_KEY_PREFIX + "*");
			} catch (Exception e) {
				e.printStackTrace();
				SysLogUtil.logError("清空redis的Biz缓存失败", e);
			}
		}
		BizCacheUtil.DATA_CACHE.clear();
	}
}
