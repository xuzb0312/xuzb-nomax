package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;

/**
 * 表单行标签--用于表单内布局
 * 
 * @author yjc
 */
public class FormLineTag extends Tag{
	private static final long serialVersionUID = 2940555156580637758L;

	private int itemWidth;// 子元素的宽度
	private boolean pane;// 是否窗格

	/**
	 * 构造函数
	 */
	public FormLineTag() {
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
		this.itemWidth = 0;
		this.pane = false;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (!(this.getParent() instanceof FormTag)) {// 判断，该标签必须包含在form下
				throw new AppException("该标签必须包含在form标签下");
			}
			FormTag formTag = (FormTag) this.getParent();
			if (!formTag.isPane()) {
				this.pane = false;
			}
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div class=\"layui-form-item\"")
				.append(this.pane ? " pane=\"pane\"" : "")
				.append(">");
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
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		} finally {
			this.release();// 资源释放
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

	public int getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(int itemWidth) {
		if (itemWidth < 0) {
			itemWidth = 0;
		}
		this.itemWidth = itemWidth;
	}

	public boolean isPane() {
		return pane;
	}

	public void setPane(boolean pane) {
		this.pane = pane;
	}
}
