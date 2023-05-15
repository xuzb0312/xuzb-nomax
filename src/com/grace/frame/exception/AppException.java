package com.grace.frame.exception;

/**
 * 系统级异常
 * <p>
 * 系统使用过程中，对于无法预知的数据环境，网络环境等抛出的异常，该种异常用户无法读懂，需要开发人员介入处理的。<br/>
 * 例如：空指针异常，数据库连接超时异常等。
 * </p>
 * 
 * @author yjc
 */
public class AppException extends Exception{
	private static final long serialVersionUID = 1L;

	public AppException(String errtext) {
		super(errtext);
	}

	public AppException(String errtext, String type) {
		this(type + ":" + errtext);
	}

	public AppException(String errtext, String type, String solution) {
		this(errtext + "[" + solution + "]", type);
	}

	public AppException(Throwable cause) {
		super(cause);
	}

	public AppException(String errtext, Throwable cause) {
		super(errtext, cause);
	}
}
