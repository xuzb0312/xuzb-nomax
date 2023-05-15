package com.grace.frame.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.StringUtil;

/**
 * 纯文本展示标签
 * 
 * @author yjc
 */
public class TextTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private String value;// 值
	private String color;// color【tiptag为true则颜色代指背景色。前景色为白色。】
	private int colspan;// 每行几个单元格，默认一行
	private String align;// 文本对齐方式
	private boolean tiptag;// 是否为提示标签[已标签样式展示]

	public TextTag() {
		this.initTag();

	}

	/**
	 * 初始化标签
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-23
	 * @since V1.0
	 */
	private void initTag() {
		this.name = null;
		this.value = null;
		this.color = null;
		this.colspan = 0;
		this.align = null;
		this.tiptag = false;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			String valueTemp = this.value;
			int colspanTemp = this.colspan;
			String wrapStart = "";
			String wrapEnd = "";

			// 首先判断父容器是不是form，如果不是则不进行布局控制
			if (this.getParent() instanceof FormTag) {
				FormTag form = (FormTag) this.getParent();

				if (StringUtil.chkStrNull(valueTemp)) {
					Object valueObj = form.getDataSourceData(this.name);
					if (valueObj == null) {
						valueTemp = "";
					} else if (valueObj instanceof Date) {
						valueTemp = DateUtil.dateToString((Date) valueObj);
					} else {
						valueTemp = String.valueOf(valueObj);
					}
				}

				if (0 == colspanTemp) {
					colspanTemp = form.getRowcount();
				}

				String alignTemp = this.align;
				if (StringUtil.chkStrNull(alignTemp)) {
					alignTemp = "left";
				}
				DataMap wrapDm = form.wrapStr4Children(colspanTemp, alignTemp);
				wrapStart = wrapDm.getString("start");
				wrapEnd = wrapDm.getString("end");
			}

			StringBuffer strBF = new StringBuffer();
			strBF.append(wrapStart);
			strBF.append("<span obj_type=\"text\" ");
			if (!StringUtil.chkStrNull(this.name)) {
				strBF.append(" id=\"").append(this.name).append("\" ");
			}
			if (this.tiptag) {// 如果为标签
				strBF.append(" class=\"tiptag\" ");
				if (!StringUtil.chkStrNull(this.color)) {
					strBF.append(" style=\"background:")
						.append(this.color)
						.append(";\" ");
				}
			} else {
				if (!StringUtil.chkStrNull(this.color)) {
					strBF.append(" style=\"color:")
						.append(this.color)
						.append(";\" ");
				}
			}
			strBF.append(">");
			if (!StringUtil.chkStrNull(valueTemp)) {
				strBF.append(valueTemp);
			}
			strBF.append("</span>");
			strBF.append(wrapEnd);

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public boolean isTiptag() {
		return tiptag;
	}

	public void setTiptag(boolean tiptag) {
		this.tiptag = tiptag;
	}

}
