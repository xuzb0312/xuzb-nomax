package com.grace.frame.redis;

import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.util.SafeEncoder;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;

/**
 * redis实现简易消息队列
 * 
 * @author yjc
 */
public class RedisMQ{
	private static final String KEY_PREFIX = "mq_";

	/**
	 * 入队列
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static long push(String key, DataMap value) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		if (null == value) {
			value = new DataMap();
		}
		// 打断与DataSet的关联关系，防止无关数据导致DataMap转byte变的非常大--处理完成后，再不想恢复关系
		DataSet table = value.table();
		value.table(null);
		Jedis jedis = null;
		try {
			key = RedisMQ.KEY_PREFIX + key;
			jedis = RedisPool.getJedis();
			long result = jedis.lpush(SafeEncoder.encode(key), value.write2Byte());
			value.table(table);// 恢复关系
			return result;
		} catch (Exception e) {
			throw e;
		} finally {
			RedisPool.close(jedis);
		}
	}

	/**
	 * 出队列(非阻塞)
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static DataMap pop(String key) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			key = RedisMQ.KEY_PREFIX + key;
			jedis = RedisPool.getJedis();
			byte[] bValue = jedis.rpop(SafeEncoder.encode(key));
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
	 * 出队列(阻塞)--对于消费者循环操作，尽量使用该堵塞队列进行处理
	 * <p>
	 * timeout：阻塞超时时间（单位：秒）
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static DataMap bpop(String key, int timeout) throws Exception {
		RedisUtil.chkEnabled();// 检查可用性
		Jedis jedis = null;
		try {
			key = RedisMQ.KEY_PREFIX + key;
			jedis = RedisPool.getJedis();
			List<byte[]> list = jedis.brpop(timeout, SafeEncoder.encode(key));
			if (null == list) {
				return null;
			}
			if (list.size() != 2) {
				return null;
			}
			byte[] bValue = list.get(1);
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
	 * 出队列(阻塞)
	 * <p>
	 * timeout=60:60s，如果1分钟内没有任何信息进入队列，则自动终止操作。
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static DataMap bpop(String key) throws Exception {
		return RedisMQ.bpop(key, 60);
	}

	/**
	 * 注册消费者
	 * <p>
	 * 开启新线程进行队列消息消费处理。（内部集成了事物管理：RedisConsumer-deal） <br>
	 * timeout：阻塞超时时间（单位：秒）
	 * </p>
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-19
	 * @since V1.0
	 */
	public static void registerConsumer(String key, int timeout,
			RedisConsumer consumer) throws AppException {
		RedisUtil.chkEnabled();// 检查可用性
		RedisConsumerThread thread = new RedisConsumerThread(key, timeout, consumer);
		new Thread(thread).start();// 启动新线程处理
	}
}
