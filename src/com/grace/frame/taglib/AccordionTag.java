package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * 可伸缩面板标签
 * 
 * @author yjc
 */
public class AccordionTag extends Tag{

	private static final long serialVersionUID = 1L;
	private String border;// 是否有边框

	public AccordionTag() {
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
		this.border = "true";// 默认存在边框
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// easyui属性
			this.clearEasyUICompAttr();
			if ("false".equalsIgnoreCase(this.border)) {
				this.setEasyUICompAttr("border", "false", false);
			}
			this.setEasyUICompAttr("fit", "true", false);
			this.setEasyUICompAttr("animate", "false", false);

			// 组装数据
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div class=\"easyui-accordion\" data-options=\""
					+ this.getEasyUICompAttrOptions() + "\">");

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
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write("</div>");
			this.release();// 资源释放
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		}
		return EVAL_PAGE;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	/**
	 * 资源释放
	 */
	@Override
	public void release() {
		this.initTag();
		super.release();
	}
}
