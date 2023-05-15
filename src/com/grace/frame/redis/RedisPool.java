package com.grace.frame.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysLogUtil;

/**
 * 连接池
 * 
 * @author yjc
 */
public class RedisPool{
	private static JedisPool pool;// jedis连接池
	private static String password;// 访问密码
	private static boolean isAuth;// 是否开启验证
	private static int dbIndex;// 数据库index

	static {
		if (GlobalVars.ENABLED_REDIS) {// 未启用，则不加载
			RedisPool.initPool();
		}
	}

	/**
	 * 初始化连接池
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	private static void initPool() {
		String ip = PropertiesUtil.getProperty("redis.ip");// 服务器IP
		int port = Integer.parseInt(PropertiesUtil.getProperty("redis.port", "6379"));// 端口号
		String password = PropertiesUtil.getProperty("redis.password", "");// 密码--如果为空则不验证密码

		int dbIndex = Integer.parseInt(PropertiesUtil.getProperty("redis.db.index", "0"));// 数据库index

		int maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "25")); // 最大连接数
		int maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));// 在jedispool中最大的idle状态(空闲的)的jedis实例的个数
		int minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));// 在jedispool中最小的idle状态(空闲的)的jedis实例的个数

		boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.on.borrow", "false"));// 在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
		boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.on.return", "false"));// 在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。

		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		config.setBlockWhenExhausted(true);// 连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。
		RedisPool.pool = new JedisPool(config, ip, port, 2000);

		// 验证密码
		if (StringUtil.chkStrNull(password)) {
			RedisPool.password = null;
			RedisPool.isAuth = false;
		} else {
			RedisPool.password = password;
			RedisPool.isAuth = true;
		}

		// 数据库index
		RedisPool.dbIndex = dbIndex;
	}

	/**
	 * 获取jedis连接
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static Jedis getJedis() {
		Jedis jedis = RedisPool.pool.getResource();
		if (RedisPool.isAuth) {
			jedis.auth(RedisPool.password);
			jedis.select(RedisPool.dbIndex);
		}
		return jedis;
	}

	/**
	 * 关闭连接
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static void close(Jedis jedis) {
		try {
			if (jedis != null) {
				jedis.close();
			}
		} catch (Exception e) {
			SysLogUtil.logError("return redis resource exception", e);
			e.printStackTrace();
		}
	}

	/**
	 * 销毁连接池中的所有连接
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static void destroy() {
		RedisPool.pool.destroy();// 销毁所有对象
	}
}
