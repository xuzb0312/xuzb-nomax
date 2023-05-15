package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.DataMap;

/**
 * 空行标签
 * 
 * @author yjc
 */
public class BlankTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private int colspan;// 每行几个单元格，默认1个

	public BlankTag() {
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
		this.colspan = 1;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 如果为父容器为form的话
			String wrapStart = "";
			String wrapEnd = "";
			if (this.getParent() instanceof FormTag) {
				FormTag formTag = (FormTag) this.getParent();
				DataMap wrapDm = formTag.wrapStr4Children(this.colspan);
				wrapStart = wrapDm.getString("start");
				wrapEnd = wrapDm.getString("end");
			}

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(wrapStart + wrapEnd);
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

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
}
