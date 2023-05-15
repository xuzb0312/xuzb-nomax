package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 *行组文本
 * 
 * @author yjc
 */
public class FormLineGroupMidTextTag extends Tag{
	private static final long serialVersionUID = 7677891650522510433L;

	private String value;
	private String color;

	/**
	 * 构造函数
	 */
	public FormLineGroupMidTextTag() {
		this.initTag();
	}

	/**
	 * 标签初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	private void initTag() {
		this.value = null;
		this.color = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (!(this.getParent() instanceof FormLineGroupTag)) {
				throw new AppException("该标签的父表签只允许为FormLineGroupTag");
			}
			if (StringUtil.chkStrNull(this.value)) {
				throw new AppException("value值为空");
			}
			// 颜色检查
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div class=\"layui-form-mid\"")
				.append(StringUtil.chkStrNull(this.color) ? "" : (" style=\"color:"
						+ this.color + "\""))
				.append(">")
				.append(this.value)
				.append("</div>");
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 终止
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			this.release();// 资源释放
		} catch (Exception e) {
			this.dealException(e);
		}
		return EVAL_PAGE;
	}

	/**
	 * 资源释放
	 */
	@Override
	public void release() {
		this.initTag();
		super.release();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
