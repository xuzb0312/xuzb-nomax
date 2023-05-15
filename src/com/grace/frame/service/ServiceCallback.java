package com.grace.frame.service;

import com.grace.frame.util.DataMap;

/**
 * 用于服务的消息机制的处理
 * 
 * @author yjc
 */
public interface ServiceCallback{

	/**
	 * 服务请求完成后的处理操作-进行了相应的事物控制
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-19
	 * @since V1.0
	 */
	public void delaFinish(DataMap para, DataMap callBackPara) throws Exception;

	/**
	 * 服务请求完成后的发生异常则走这块的逻辑
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-19
	 * @since V1.0
	 */
	public void dealException(Exception ex, DataMap callBackPara);
}
