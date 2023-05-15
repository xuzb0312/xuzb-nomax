package com.grace.frame.exception;

/**
 * 跳转异常异常<br>
 * 抛出该异常后，前台的动作为首先alert提示信息，然后进行页面调整
 * 
 * @author yjc
 */
public class RedirectException extends Exception{
	private static final long serialVersionUID = 1L;
	private String redirect_url;
	private String errtext;

	public RedirectException(String errtext, String redirect_url) {
		super(errtext);
		this.redirect_url = (null == redirect_url) ? "" : redirect_url;
		this.errtext = (null == errtext) ? "" : errtext;
	}

	public String getRedirectUrl() {
		return redirect_url;
	}

	public String getErrText() {
		return errtext;
	}
}
