package com.grace.frame.exception;

/**
 * 对外提供接口服务的应用程序的异常封装
 * 
 * @author yjc
 */
public class ServiceException extends Exception{
	private static final long serialVersionUID = 4299782178897801800L;
	private String errcode;
	private String errtext;

	/**
	 * ERROR-CODE-系统级应用的errcode定义：code
	 */
	public static final String No_ERROR = "000";// 未发生错误
	public static final String NotFound_ERROR = "404";// 资源无法找到
	public static final String Forbidden_ERROR = "403";// 禁止访问，没有权限
	public static final String IllegalToken_ERROR = "500";// 不合法的token,原因是在redis中无法找到该token值。
	public static final String PARAS_ERROR = "501";// 参数错误
	public static final String Version_ERROR = "502";// 版本号异常不匹配
	public static final String FieldTip_ERROR = "904";// FieldTipException
	public static final String Redirect_ERROR = "905";// RedirectException
	public static final String Box_ERROR = "906";// BoxException
	public static final String Biz_ERROR = "907";// bizException
	public static final String App_ERROR = "908";// appException
	public static final String Req_ERROR = "909";// 服务API请求出错

	public ServiceException(String errcode, String errtext) {
		super(errcode + ":" + errtext);
		this.errcode = errcode;
		this.errtext = errtext;
	}

	public String getErrcode() {
		return errcode;
	}

	public String getErrtext() {
		return errtext;
	}
}
