package com.grace.frame.redis;

import com.grace.frame.exception.AppException;
import com.grace.frame.hibernate.TransManager;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.SysLogUtil;

/**
 * 消费者线程
 * 
 * @author yjc
 */
public class RedisConsumerThread implements Runnable{
	private String key;// 消费队列key值
	private int timeout;// 等待超时时间（秒）
	private RedisConsumer consumer;// 消费者处理逻辑

	public RedisConsumerThread(String key, int timeout, RedisConsumer consumer) {
		this.key = key;
		this.timeout = timeout;
		this.consumer = consumer;
	}

	/**
	 * 执行函数
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-19
	 * @since V1.0
	 */
	public void run() {
		this.runFunc();
	}

	/**
	 * 执行函数
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-19
	 * @since V1.0
	 */
	private void runFunc() {
		do {
			TransManager tm = new TransManager();// 增加事物管控
			try {
				tm.begin();// 开启

				DataMap value = RedisMQ.bpop(this.key, this.timeout);
				if (null != value) {
					boolean isSuccess = this.consumer.deal(value);// 处理逻辑-返回是否处理成功
					if (!isSuccess) {// 如果处理失败，则value重新入队列
						RedisMQ.push(this.key, value);
					}
				} else {
					SysLogUtil.logInfo("Consumer（消费者线程）等待超时（队列阻塞超过："
							+ this.timeout + "）。");
					tm.rollback();// 事物回滚。
					break;
				}
				tm.commit();// 提交
			} catch (Exception e) {
				try {
					tm.rollback();// 回滚
				} catch (AppException op) {
					op.printStackTrace();
					SysLogUtil.logError("Consumer（消费者线程）事物出现处理异常", op);
				}
				e.printStackTrace();
				SysLogUtil.logError("Consumer（消费者线程）出现处理异常", e);
			}
		} while (true);

		// 等待超时
		try {
			this.consumer.timeout(this.timeout);// 等待超时操作
		} catch (Exception e) {
			e.printStackTrace();
			SysLogUtil.logError("Consumer（消费者线程）出现处理异常", e);
		}
	}
}