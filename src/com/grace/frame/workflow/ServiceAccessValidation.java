package com.grace.frame.workflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.grace.frame.util.AccessTokenMap;
import com.grace.frame.util.DataMap;

/**
 * 服务请求允许访问的验证类
 * 
 * @author yjc
 */
public abstract class ServiceAccessValidation{
	/**
	 * 服务请求允许访问的验证类
	 * 
	 * @return true 检测通过，false 检测不通过。
	 * @author yjc
	 * @date 创建时间 2015-10-6
	 * @since V1.0
	 */
	public abstract boolean verifyAccess(String className, String methodName,
			HttpServletRequest request, HttpServletResponse response,
			AccessTokenMap accessTokenMap, DataMap para) throws Exception;
}
