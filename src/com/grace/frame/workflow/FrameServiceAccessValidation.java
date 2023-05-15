package com.grace.frame.workflow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.grace.frame.util.AccessTokenMap;
import com.grace.frame.util.DataMap;

/**
 * 框架的服务认证类
 * 
 * @author yjc
 */
public class FrameServiceAccessValidation extends ServiceAccessValidation{

	/**
	 * 默认全部拒绝
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-15
	 * @since V1.0
	 */
	@Override
	public boolean verifyAccess(String className, String methodName,
			HttpServletRequest request, HttpServletResponse response,
			AccessTokenMap accessTokenMap, DataMap para) throws Exception {
		return false;
	}
}
