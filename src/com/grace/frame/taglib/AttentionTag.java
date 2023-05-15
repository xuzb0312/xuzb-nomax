package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 特别注意标签
 * 
 * @author yjc
 */
public class AttentionTag extends BodyTagSupport{
	private static final long serialVersionUID = 1L;
	private String color;

	public AttentionTag() {
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
		this.color = null;// 默认主题色，可以自定义
	}

	/**
	 * 标签开始时
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div class=\"attention-tag\"")
				.append(StringUtil.chkStrNull(this.color) ? "" : (" style=\"border-left-color:"
						+ this.color + ";\""))
				.append(">");
			// 数据的输出
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
		} catch (IOException e) {
			TagSupportUtil.dealException(e, "AttentionTag标签解析时，将html输出到jsp时，出现IO异常："
					+ e.getMessage(), this.pageContext);
		} catch (Exception e) {
			TagSupportUtil.dealException(e, this.pageContext);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 标签结束时，执行
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			// 数据的输出
			JspWriter out = this.pageContext.getOut();
			out.write("</div>");
			this.release();// 资源释放
		} catch (IOException e) {
			TagSupportUtil.dealException(e, "AttentionTag标签解析时，将html输出到jsp时，出现IO异常："
					+ e.getMessage(), this.pageContext);
		} catch (Exception e) {
			TagSupportUtil.dealException(e, this.pageContext);
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
