package com.grace.frame.exception;

/**
 * 用于区分前台报出异常的提示方式--使用MsgBox.alert进行提示
 * 
 * @author yjc
 */
public class BoxException extends Exception{

	private static final long serialVersionUID = 1L;

	public BoxException(String errtext) {
		super(errtext);
	}

	public BoxException(String errtext, String type) {
		this(type + ":" + errtext);
	}

	public BoxException(String errtext, String type, String solution) {
		this(errtext + "[" + solution + "]", type);
	}
}
