package com.grace.frame.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.grace.frame.annotation.PublicAccess;
import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.ServiceException;
import com.grace.frame.util.AccessTokenMap;
import com.grace.frame.util.DataMap;
import com.grace.frame.workflow.ApiController;

/**
 * 框架封装的apicontroller
 * 
 * @author yjc
 */
public class FrameApiController extends ApiController{
	/**
	 * 请求token
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-12
	 * @since V1.0
	 */
	@PublicAccess
	public DataMap requestToken(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		AccessTokenMap atm = this.getAccessTokenMap();
		if (null != atm) {// 如果请求中带有原来的accesstoken则将原来的token进行销毁
			atm.destroy();
		}
		// 重新生成新的token
		atm = AccessTokenMap.genToken();
		atm.reset2Request(request);// 重新设置到request中

		DataMap rdm = new DataMap();
		rdm.put("access_token", atm.getAccessToken());
		return rdm;
	}

	/**
	 * 获取服务端版本号
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-18
	 * @since V1.0
	 */
	@PublicAccess
	public DataMap getAppInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = new DataMap();
		rdm.put("appid", GlobalVars.APP_ID);
		rdm.put("appname", GlobalVars.APP_NAME);
		rdm.put("version", GlobalVars.APP_VERSION);
		return rdm;
	}

	/**
	 * 验证AccessToken是否在redis中正常存在
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-21
	 * @since V1.0
	 */
	@PublicAccess
	public DataMap isActive(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		AccessTokenMap atm = this.getAccessTokenMap();
		if (null == atm) {
			throw new ServiceException(ServiceException.PARAS_ERROR, "请检查是否传递access_token参数");
		}
		DataMap rdm = new DataMap();
		rdm.put("active", atm.isActive());
		return rdm;
	}

}
