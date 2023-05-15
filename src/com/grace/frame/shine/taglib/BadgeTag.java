package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.StringUtil;

/**
 * 徽章标签
 * 
 * @author yjc
 */
public class BadgeTag extends Tag{
	private static final long serialVersionUID = 5765215855065289561L;
	private String color;// 颜色--允许的值：赤色red,橙色orange,墨绿green,青色cyan,蓝色blue,黑色black,灰色gray--默认red;增加一种颜色：rim-边框
	private String value;// 值

	/**
	 * 构造函数
	 */
	public BadgeTag() {
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
		this.color = null;
		this.value = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (null == this.value) {
				this.value = "";
			}
			StringBuffer strBF = new StringBuffer();
			// 颜色检查
			if ("rim".equals(this.color)) {
				strBF.append("<span class=\"layui-badge-rim\">");
			} else {
				this.chkLayuiBgColorLimits(this.color);
				strBF.append("<span class=\"layui-badge")
					.append((StringUtil.chkStrNull(this.color) ? "" : (" layui-bg-" + this.color)))
					.append("\">");
			}
			strBF.append(this.value).append("</span>");

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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
