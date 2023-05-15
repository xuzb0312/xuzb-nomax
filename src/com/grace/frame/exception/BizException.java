package com.grace.frame.exception;

/**
 * 业务级异常
 * <p>
 * 在进行业务操作时，后台检测不满足业务操作条件的，直接抛出业务级异常，<br>
 * 前台截获到该异常后，使用alert方式告知用户。该种异常用户可以读懂。<br>
 * 例如：修改个人基本信息时，如果发现该人员的基本信息已经审核完成，<br>
 * 抛出异常：该考生基本信息已经审核完成，无法进行信息修改。
 * </p>
 * 
 * @author yjc
 */
public class BizException extends Exception{
	private static final long serialVersionUID = 1L;

	public BizException(String errtext) {
		super(errtext);
	}

	public BizException(String errtext, String type) {
		this(type + ":" + errtext);
	}

	public BizException(String errtext, String type, String solution) {
		this(errtext + "[" + solution + "]", type);
	}
}
