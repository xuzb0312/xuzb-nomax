package com.grace.frame.redis;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.Sql;

/**
 * 消费者
 * 
 * @author yjc
 */
public abstract class RedisConsumer{
	protected Sql sql;// sql

	public RedisConsumer() {
		this.sql = new Sql();
	}

	/**
	 * 消费者处理逻辑-消费逻辑(消费成功，返回true，否则返回false)【管控事物】
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-19
	 * @since V1.0
	 */
	public abstract boolean deal(DataMap value) throws Exception;

	/**
	 * 等待超时逻辑--如果超时，会向该接口发送一个通知【不管控事物】
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-19
	 * @since V1.0
	 */
	public abstract void timeout(int timeout) throws Exception;
}
