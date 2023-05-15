package com.grace.frame.service;

import java.util.concurrent.Callable;

import net.sf.json.JSONObject;

import com.grace.frame.exception.AppException;
import com.grace.frame.hibernate.TransManager;
import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.DataMap;

/**
 * 服务消息机制的回调函数
 * 
 * @author yjc
 */
public class ServiceCallbackThreadCallable implements Callable<DataMap>{

	private String url;// 请求地址
	private String yhbh;// 用户编号
	private String yhmy;// 密钥
	private int timeout;// 请求超时
	private String serviceName;// 服务名称
	private String serviceMethod;// 服务方法
	private DataMap para;// 请求参数
	private DataMap callBackPara;// 回调传入的参数
	private ServiceCallback callback;// 对调函数
	TransManager tmg;

	/**
	 * 构造函数
	 */
	public ServiceCallbackThreadCallable(String url, String yhbh, String yhmy,
		int timeout, String serviceName, String serviceMethod, DataMap para,
		DataMap callBackPara, ServiceCallback callback) {
		super();
		this.url = url;
		this.yhbh = yhbh;
		this.yhmy = yhmy;
		this.timeout = timeout;
		this.serviceName = serviceName;
		this.serviceMethod = serviceMethod;
		this.para = para;
		this.callBackPara = callBackPara;
		this.callback = callback;
		this.tmg = new TransManager();
	}

	/**
	 * 执行操作
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-19
	 * @since V1.0
	 */
	public DataMap call() throws Exception {
		try {
			// 发送请求
			ServiceDAO osd = new ServiceDAO(this.url);
			osd.login(this.yhbh, this.yhmy);
			JSONObject json = osd.post4ReturnJson(this.serviceName, this.serviceMethod, this.para, this.timeout);
			DataMap rdata = ActionUtil.jsonObject2DataMap(json);
			String errcode = rdata.getString("__errcode");
			String errtext = rdata.getString("__errtext");
			DataMap resultData = rdata.getDataMap("__data");
			if (!"0".equals(errcode)) {
				throw new AppException(errcode + ":" + errtext);
			}
			this.tmg.begin();
			this.callback.delaFinish(resultData, callBackPara);
			this.tmg.commit();
		} catch (Exception e) {
			try {
				this.tmg.rollback();
			} catch (AppException op) {
			}
			// 异常处理操作
			this.callback.dealException(e, callBackPara);
		}
		// 不返回值
		return null;
	}
}
