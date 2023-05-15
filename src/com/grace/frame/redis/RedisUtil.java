package com.grace.frame.redis;

import java.util.Iterator;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;

/**
 * redis工具类
 * 
 * @author yjc
 */
public class RedisUtil{
	/**
	 * 检查redis是否可用
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static void chkEnabled() throws AppException {
		if (!GlobalVars.ENABLED_REDIS) {
			throw new AppException("Redis服务器配置为不可用，请在appPara.xml中配置。");
		}
	}

	/**
	 * 清空数据
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String flushDB() throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			String result = jedis.flushDB();
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * 判断键是否存在
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static boolean exists(String key) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			boolean result = jedis.exists(key);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * set值
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String set(String key, String value) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			String result = jedis.set(key, value);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * set值
	 * <p>
	 * seconds:过期时间，秒
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String set(String key, String value, int seconds) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			String result = jedis.setex(key, seconds, value);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * set值(DataMap)
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String setJson(String key, DataMap value) throws Exception {
		if (null == value) {
			value = new DataMap();
		}
		return RedisUtil.set(key, value.toJsonString());
	}

	/**
	 * set值(DataMap)
	 * <p>
	 * seconds:过期时间，秒
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String setJson(String key, DataMap value, int seconds) throws Exception {
		if (null == value) {
			value = new DataMap();
		}
		return RedisUtil.set(key, value.toJsonString(), seconds);
	}

	/**
	 * set值(DataSet)
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String setJson(String key, DataSet value) throws Exception {
		if (null == value) {
			value = new DataSet();
		}
		return RedisUtil.set(key, value.toJsonString());
	}

	/**
	 * set值(DataSet)
	 * <p>
	 * seconds:过期时间，秒
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String setJson(String key, DataSet value, int seconds) throws Exception {
		if (null == value) {
			value = new DataSet();
		}
		return RedisUtil.set(key, value.toJsonString(), seconds);
	}

	/**
	 * get值
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String get(String key) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			String result = jedis.get(key);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * get值(DataMap)
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static DataMap getJson2DataMap(String key) throws Exception {
		String value = RedisUtil.get(key);
		if (null == value) {
			return null;
		}
		return DataMap.fromObject(value);
	}

	/**
	 * get值(DataSet)
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static DataSet getJson2DataSet(String key) throws Exception {
		String value = RedisUtil.get(key);
		if (null == value) {
			return null;
		}
		return DataSet.fromObject(value);
	}

	/**
	 * 删除key
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static Long del(String key) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			long result = jedis.del(key);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * 删除根据正则表达式，删除复核条件的所有key
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static Long delMulti(String keyReg) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			Set<String> keys = jedis.keys(keyReg);
			Iterator<String> it = keys.iterator();
			long result = 0;
			while (it.hasNext()) {
				String key = it.next();
				jedis.del(key);
				result++;
			}
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * 设置key的有效期，单位是秒
	 * <p>
	 * seconds:过期时间，秒
	 * </p>
	 * 
	 * @return
	 * @throws Exception
	 */
	public static long expire(String key, int seconds) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			long result = jedis.expire(key, seconds);
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * set值(DataSet)-效率要比json转换方式快3~5倍。数据越大，影响越明显
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String set(String key, DataSet value) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		if (null == value) {
			value = new DataSet();
		}
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			String result = jedis.set(SafeEncoder.encode(key), value.write2Byte());
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * set值(DataSet)-效率要比json转换方式快3~5倍。数据越大，影响越明显
	 * <p>
	 * seconds:过期时间，秒
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String set(String key, DataSet value, int seconds) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		if (null == value) {
			value = new DataSet();
		}
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			String result = jedis.setex(SafeEncoder.encode(key), seconds, value.write2Byte());
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * get值(DataSet)-效率要比json转换方式快3~5倍。数据越大，影响越明显
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static DataSet getDataSet(String key) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			byte[] bValue = jedis.get(SafeEncoder.encode(key));
			if (null == bValue) {
				return null;
			}
			return DataSet.fromObject(bValue);
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * set值(DataMap)-效率要比json转换方式快3~5倍。数据越大，影响越明显
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String set(String key, DataMap value) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		if (null == value) {
			value = new DataMap();
		}
		// 打断与DataSet的关联关系，防止无关数据导致DataMap转byte变的非常大--处理完成后，再不想恢复关系
		DataSet table = value.table();
		value.table(null);

		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			String result = jedis.set(SafeEncoder.encode(key), value.write2Byte());
			value.table(table);// 恢复关系
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * set值(DataMap)-效率要比json转换方式快3~5倍。数据越大，影响越明显
	 * <p>
	 * seconds:过期时间，秒
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String set(String key, DataMap value, int seconds) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		if (null == value) {
			value = new DataMap();
		}
		// 打断与DataSet的关联关系，防止无关数据导致DataMap转byte变的非常大--处理完成后，再不想恢复关系
		DataSet table = value.table();
		value.table(null);

		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			String result = jedis.setex(SafeEncoder.encode(key), seconds, value.write2Byte());
			value.table(table);// 恢复关系
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * get值(DataMap)-效率要比json转换方式快3~5倍。数据越大，影响越明显
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static DataMap getDataMap(String key) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			byte[] bValue = jedis.get(SafeEncoder.encode(key));
			if (null == bValue) {
				return null;
			}
			return DataMap.fromObject(bValue);
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * get值(DataMap)-效率要比json转换方式快3~5倍。数据越大，影响越明显
	 * <p>
	 * 获取数据，并延期seconds秒（适合分布式架构的,会话共享的数据获取）
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static DataMap getDataMap(String key, int seconds) throws Exception {
		DataMap data = RedisUtil.getDataMap(key);
		if (null != data) {
			RedisUtil.expire(key, seconds);// 设置超时
		}
		return data;
	}

	/**
	 * 频道-信息发布-Pub
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static long publish(String channel, DataMap value) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		if (null == value) {
			value = new DataMap();
		}
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			System.out.println(value.toJsonString());
			long result = jedis.publish(channel, value.toJsonString());// 使用jons串，防止底层冲突
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * 频道订阅-Sub
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-22
	 * @since V1.0
	 */
	public static void subscribe(RedisPubSub redisPubSub, String... channels) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			jedis = RedisPool.getJedis();
			jedis.subscribe(redisPubSub, channels);// 注意订阅操作会，阻塞在此处，进行处理频道上发布的消息，除非调用unsubscribe来终结订阅。
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * 频道订阅（取消订阅）-unSub
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-22
	 * @since V1.0
	 */
	public static void unsubscribe(RedisPubSub redisPubSub) throws Exception {
		redisPubSub.unsubscribe();
	}

	/**
	 * 频道订阅（取消订阅）-unSub
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-22
	 * @since V1.0
	 */
	public static void unsubscribe(RedisPubSub redisPubSub, String... channels) throws Exception {
		redisPubSub.unsubscribe(channels);
	}
}
