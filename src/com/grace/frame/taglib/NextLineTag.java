package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

public class NextLineTag extends Tag{

	private static final long serialVersionUID = 1L;

	public NextLineTag() {
		this.initTag();
	}

	/**
	 * 初始化标签
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	private void initTag() {}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 如果为父容器为form的话
			String str = "";
			if (this.getParent() instanceof FormTag) {
				FormTag formTag = (FormTag) this.getParent();
				str = formTag.nextLine();
			}

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(str);
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
		this.release();
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
}
