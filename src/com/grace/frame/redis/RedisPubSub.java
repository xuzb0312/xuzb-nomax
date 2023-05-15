package com.grace.frame.redis;

import redis.clients.jedis.JedisPubSub;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.SysLogUtil;

/**
 * 订阅者(自定义实现)
 * 
 * @author yjc
 */
public abstract class RedisPubSub extends JedisPubSub{

	/**
	 * 接收到消息后的处理逻辑
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-22
	 * @since V1.0
	 */
	@Override
	public void onMessage(String channel, String message) {
		try {
			this.onMessage(channel, DataMap.fromObject(message));
		} catch (Exception e) {
			e.printStackTrace();
			SysLogUtil.logError("接收到消息后的处理逻辑出错", e);
		}
		super.onMessage(channel, message);
	}

	/**
	 * 接收到消息后的处理逻辑（真正需要实现这个）--可以调用this.unsubscribe来终止订阅
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-22
	 * @since V1.0
	 */
	public abstract void onMessage(String channel, DataMap message) throws Exception;
}
