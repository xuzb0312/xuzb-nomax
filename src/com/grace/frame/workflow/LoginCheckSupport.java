package com.grace.frame.workflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 单独验证Class的超类
 * 
 * @author yjc
 */
public abstract class LoginCheckSupport{
	/**
	 * 登录验证的超类
	 * <p>
	 * 对于具有特殊需求的的登录进行登录验证，后台请求验证（保证安全用）；
	 * </p>
	 * 
	 * @return true 检测通过，false 检测不通过。
	 * @return
	 * @author yjc
	 * @date 创建时间 2015-10-6
	 * @since V1.0
	 */
	public abstract boolean doCheckLogin(String className, String methodName,
			HttpServletRequest request, HttpServletResponse response) throws Exception;
}
