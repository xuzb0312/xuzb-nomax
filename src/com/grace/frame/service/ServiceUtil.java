package com.grace.frame.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.sf.json.JSONObject;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.SysLogUtil;

/**
 * 同等的使用grace.easyFrame框架的业务系统进行通讯使用的服务请求类
 * 
 * @author yjc
 */
public class ServiceUtil{
	/**
	 * 来源：Executors.newCachedThreadPool();
	 * 适用场景：快速处理大量耗时较短的任务，如Netty的NIO接受请求时，可使用CachedThreadPool。
	 * 自己写实现：主要是根据阿里巴巴规范禁止使用Executor工厂类创建
	 */
	private static ExecutorService exec;
	static {
		ServiceUtil.exec = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	/**
	 * 可以通过系统层级进行统一配置，请求地址，用户名，密码，超时时间等，无需每次调用设置； <br>
	 * 同等的都使用grace.easyFrame框架的使用该方法请求服务--同步
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-22
	 * @since V1.0
	 */
	public static DataMap postRequest(String serviceName, String serviceMethod,
			DataMap data) throws AppException {
		try {
			if (null == serviceName || "".equals(serviceName.trim())) {
				throw new AppException("服务名称不允许为空");
			}
			if (null == serviceMethod || "".equals(serviceMethod.trim())) {
				throw new AppException("服务方法不允许为空");
			}

			// 记录系统日志
			SysLogUtil.logInfo(ServiceUtil.class, "**请求服务：" + serviceName + "."
					+ serviceMethod);

			// 跟你serviceName从数据库中读取配置信息--系统启动时将其加载到内存中即可。
			if (!GlobalVars.SERVICE_REG_INFO_MAP.containsKey(serviceName)) {
				throw new AppException("服务" + serviceName + "未在系统中注册，请注册后再使用。");
			}

			Object[] arrServiceReg = GlobalVars.SERVICE_REG_INFO_MAP.get(serviceName);
			String url = (String) arrServiceReg[0];
			String yhbh = (String) arrServiceReg[1];
			String yhmy = (String) arrServiceReg[2];// 用户密钥
			int timeout = (Integer) arrServiceReg[3];

			// 发送请求
			ServiceDAO osd = new ServiceDAO(url);
			osd.login(yhbh, yhmy);
			JSONObject json = osd.post4ReturnJson(serviceName, serviceMethod, data, timeout);
			DataMap rdata = ActionUtil.jsonObject2DataMap(json);
			String errcode = rdata.getString("__errcode");
			String errtext = rdata.getString("__errtext");
			DataMap resultData = rdata.getDataMap("__data");
			if (!"0".equals(errcode)) {
				throw new AppException(errcode + ":" + errtext);
			}
			return resultData;
		} catch (Exception e) {
			SysLogUtil.logError(ServiceUtil.class, "请求服务时发生异常："
					+ e.getMessage(), e);
			throw new AppException(e);
		}
	}

	/**
	 * 可以通过系统层级进行统一配置，请求地址，用户名，密码，超时时间等，无需每次调用设置； <br>
	 * 同等的都使用grace.easyFrame框架的使用该方法请求服务(消息机制的方式进行服务请求)--异步
	 * <p>
	 * 该方法已经不建议继续使用：原因使用复杂度较高并且存在内存线程泄漏bug。--请使用biz的异步线程开启方式，实现相应的逻辑。
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-22
	 * @since V1.0
	 */
	@Deprecated
	public static void postRequest(String serviceName, String serviceMethod,
			DataMap data, DataMap callBackPara, ServiceCallback callback) throws AppException {
		try {
			if (null == serviceName || "".equals(serviceName.trim())) {
				throw new AppException("服务名称不允许为空");
			}
			if (null == serviceMethod || "".equals(serviceMethod.trim())) {
				throw new AppException("服务方法不允许为空");
			}

			// 记录系统日志
			SysLogUtil.logInfo(ServiceUtil.class, "**请求服务：" + serviceName + "."
					+ serviceMethod);

			// 跟你serviceName从数据库中读取配置信息--系统启动时将其加载到内存中即可。
			if (!GlobalVars.SERVICE_REG_INFO_MAP.containsKey(serviceName)) {
				throw new AppException("服务" + serviceName + "未在系统中注册，请注册后再使用。");
			}

			Object[] arrServiceReg = GlobalVars.SERVICE_REG_INFO_MAP.get(serviceName);
			String url = (String) arrServiceReg[0];
			String yhbh = (String) arrServiceReg[1];
			String yhmy = (String) arrServiceReg[2];// 用户密钥
			int timeout = (Integer) arrServiceReg[3];

			// 开启新线程，发送请求
			ServiceCallbackThreadCallable call = new ServiceCallbackThreadCallable(url, yhbh, yhmy, timeout, serviceName, serviceMethod, data, callBackPara, callback);
			ServiceUtil.exec.submit(call);
		} catch (Exception e) {
			SysLogUtil.logError(ServiceUtil.class, "请求服务时发生异常："
					+ e.getMessage(), e);
			throw new AppException(e);
		}
	}
}
