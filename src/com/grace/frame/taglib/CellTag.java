package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.DataMap;

/**
 * 单元格标签，可以在中间增加html代码等，布局使用
 * 
 * @author yjc
 */
public class CellTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private int colspan;// 每行几个单元格，默认1个
	private int rowspan;// 几行，默认1
	private String wrapEndStr;// form布局字符串，结束内容

	public CellTag() {
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
		this.rowspan = 1;
		this.wrapEndStr = "";
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {

			// 如果为父容器为form的话
			String wrapStart = "";
			if (this.getParent() instanceof FormTag) {
				FormTag formTag = (FormTag) this.getParent();
				DataMap wrapDm = formTag.wrapStr4Children(this.colspan, this.rowspan, "left");
				wrapStart = wrapDm.getString("start");
				this.wrapEndStr = wrapDm.getString("end");
			}

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(wrapStart);
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
			out.write(this.wrapEndStr);
			this.release();// 资源释放
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
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

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public int getRowspan() {
		return rowspan;
	}

	public void setRowspan(int rowspan) {
		this.rowspan = rowspan;
	}
}