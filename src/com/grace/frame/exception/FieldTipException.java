package com.grace.frame.exception;

/**
 * 信息项提示异常
 * <p>
 * 跟BizException的差异为：该信息项会通过fieldId值寻找前台相关信息项，并进行焦点置入操作
 * </p>
 * 
 * @author yjc
 */
public class FieldTipException extends Exception{
	private static final long serialVersionUID = 1L;
	private String fieldId;
	private String errtext;

	public FieldTipException(String errtext, String fieldId) {
		super(errtext);
		this.fieldId = (null == fieldId) ? "" : fieldId;
		this.errtext = (null == errtext) ? "" : errtext;
	}

	public String getFieldId() {
		return fieldId;
	}

	public String getErrText() {
		return errtext;
	}
}
