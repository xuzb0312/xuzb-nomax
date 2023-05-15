package com.grace.frame.workflow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;

/**
 * 线程委托--新开线程进行业务操作
 * 
 * @author yjc
 */
public class ThreadDelegator{
	private static ExecutorService exec;// 线程池
	static {
		/**
		 * 来源：Executors.newCachedThreadPool();
		 * 适用场景：快速处理大量耗时较短的任务，如Netty的NIO接受请求时，可使用CachedThreadPool。
		 * 自己写实现：主要是根据阿里巴巴规范禁止使用Executor工厂类创建
		 */
		ThreadDelegator.exec = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	/**
	 * 终止线程池
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public static void shutdown() {
		ThreadDelegator.exec.shutdownNow();
	}

	/**
	 * 同步执行的方法：用于多个事物的控制。--阻塞
	 * 
	 * @author yjc
	 * @date 创建时间 2016-4-22
	 * @since V1.0
	 */
	public static DataMap execute(String bizName, String methodName,
			DataMap para, DataMap bizPara) throws Exception {
		ThreadCallable threadCall = new ThreadCallable(bizName, methodName, para, bizPara);
		Future<DataMap> result = ThreadDelegator.exec.submit(threadCall);
		return result.get();
	}

	/**
	 * 用于异步执行，可以不等待结果。
	 * <p>
	 * future.get:会导致主线程等待子线程完成任务返回结果。
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2016-4-22
	 * @since V1.0
	 */
	public static Future<DataMap> startThreadFunc(String bizName,
			String methodName, DataMap para, DataMap bizPara) throws AppException {
		ThreadCallable threadCall = new ThreadCallable(bizName, methodName, para, bizPara);
		return ThreadDelegator.exec.submit(threadCall);
	}

	/**
	 * 对于原先老代码的兼容性处理
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public Future<DataMap> startThread(String bizName, String methodName,
			DataMap para, DataMap bizPara) throws AppException {
		return ThreadDelegator.startThreadFunc(bizName, methodName, para, bizPara);
	}
}
