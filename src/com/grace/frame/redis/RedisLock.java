package com.grace.frame.redis;

import java.util.Collections;

import redis.clients.jedis.Jedis;

/**
 * 通过redis实现分布式锁
 * 
 * @author yjc
 */
public class RedisLock{
	private static final String KEY_PREFIX = "lock_";
	private static final String LOCK_SUCCESS = "OK";
	private static final String SET_IF_NOT_EXIST = "NX";
	private static final String SET_WITH_EXPIRE_TIME = "PX";
	private static final Long RELEASE_SUCCESS = 1L;

	/**
	 * 获取分布式锁
	 * 
	 * @param lockKey 锁
	 * @param requestId 请求标识
	 * @param expireTime 超期时间(毫秒)
	 * @return 是否获取成功
	 * @throws Exception
	 */

	public static boolean lock(String lockKey, String requestId, long expireTime) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			lockKey = RedisLock.KEY_PREFIX + lockKey;
			jedis = RedisPool.getJedis();
			String result = jedis.set(lockKey, requestId, RedisLock.SET_IF_NOT_EXIST, RedisLock.SET_WITH_EXPIRE_TIME, expireTime);
			if (RedisLock.LOCK_SUCCESS.equalsIgnoreCase(result)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * 尝试获取分布式锁
	 * 
	 * @param lockKey 锁
	 * @param requestId 请求标识
	 * @param expireTime 超期时间（毫秒）
	 * @param attemptLimit尝试次数
	 * @param delay 延迟（毫秒）。
	 * @return 是否获取成功
	 * @throws Exception
	 */

	public static boolean tryLock(String lockKey, String requestId,
			long expireTime, long delay, int attemptLimit) throws Exception {
		boolean isSuccess = false;
		int i = 0;
		do {
			isSuccess = RedisLock.lock(lockKey, requestId, expireTime);
			if (isSuccess) {
				return true;
			}
			Thread.sleep(delay);
			i++;
		} while (i < attemptLimit);

		return false;
	}

	/**
	 * 尝试获取分布式锁
	 * 
	 * @param lockKey 锁
	 * @param requestId 请求标识
	 * @param expireTime 超期时间（毫秒）
	 * @param attemptLimit尝试次数（每次尝试时间间隔100ms。）
	 * @return 是否获取成功
	 * @throws Exception
	 */

	public static boolean tryLock(String lockKey, String requestId,
			long expireTime, int attemptLimit) throws Exception {
		return RedisLock.tryLock(lockKey, requestId, expireTime, 100, attemptLimit);
	}

	/**
	 * 释放分布式锁
	 * 
	 * @param jedis Redis客户端
	 * @param lockKey 锁
	 * @param requestId 请求标识
	 * @return 是否释放成功
	 * @throws Exception
	 */

	public static boolean unlock(String lockKey, String requestId) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			lockKey = RedisLock.KEY_PREFIX + lockKey;
			String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
			jedis = RedisPool.getJedis();
			Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));
			if (RedisLock.RELEASE_SUCCESS.equals(result)) {
				return true;
			}
			return false;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}
}
