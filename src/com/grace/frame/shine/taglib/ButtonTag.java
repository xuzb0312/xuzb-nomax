package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 按钮标签
 * 
 * @author yjc
 */
public class ButtonTag extends Tag{
	private static final long serialVersionUID = 3289362703690412281L;

	// 属性
	private String name;// 按钮名称
	private String type;// 类型-primary：原始,(null):默认，normal：百搭，warm：暖色，danger：警告，disabled：禁用
	private String size;// 尺寸，(lg):大，(null):默认，(sm)小，(xs)迷你
	private boolean radius;// 是否圆角，默认不是
	private String icon;// 图标，layui的图标库
	private String value;// 按钮文字
	private String functionid;// 功能id
	private String tip;// 提示信息
	private String color;// 颜色-背景颜色

	// 事件
	private String onclick;// 按钮的单击事件

	// 其他
	private boolean hasRight;// 有权限

	public ButtonTag() {
		this.initTag();
	}

	/**
	 * 标签初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-20
	 * @since V1.0
	 */
	private void initTag() {
		this.name = null;
		this.type = null;
		this.size = null;
		this.radius = false;
		this.icon = null;
		this.value = null;
		this.functionid = null;
		this.tip = null;
		this.color = "#38f";// 默认颜色

		this.onclick = null;
		this.hasRight = false;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.icon)
					&& StringUtil.chkStrNull(this.value)) {
				throw new AppException("按钮图标和文字不允许同时为空");
			}
			if (StringUtil.chkStrNull(this.onclick)) {
				throw new AppException("单击事件为空");
			}
			this.hasRight = this.checkFunctionRight(this.functionid);

			StringBuffer strBF = new StringBuffer();
			if (this.hasRight) {// 只有存在权限时，才渲染此标签
				strBF.append("<button ");
				if (StringUtil.chkStrNull(this.type)
						&& !StringUtil.chkStrNull(this.color)) {// 当type为空，且color不为空时处理
					strBF.append("style=\"background-color:")
						.append(this.color)
						.append(";\" ");
				}
				strBF.append(StringUtil.chkStrNull(this.name) ? "" : ("id="
						+ this.name + " "))
					.append("class=\"layui-btn")
					.append(StringUtil.chkStrNull(this.type) ? "" : " layui-btn-"
							+ this.type)
					.append(StringUtil.chkStrNull(this.size) ? "" : " layui-btn-"
							+ this.size)
					.append(this.radius ? " layui-btn-radius" : "")
					.append("\"")
					.append(" onclick=\"")
					.append(this.onclick)
					.append("\" obj_type=\"button\"")
					.append("disabled".equalsIgnoreCase(this.type) ? " disabled=\"disabled\"" : "")
					.append(" _btn_type=\"")
					.append(StringUtil.chkStrNull(this.type) ? "" : this.type)
					.append("\"")
					.append(StringUtil.chkStrNull(this.tip) ? "" : (" title=\""
							+ this.tip + "\""))
					.append(">");
				if (!StringUtil.chkStrNull(this.icon)) {
					strBF.append("<i class=\"layui-icon\">")
						.append(this.icon)
						.append("</i>");
				}
				if (!StringUtil.chkStrNull(this.value)) {
					strBF.append(this.value);
				}
			} else {
				if (!StringUtil.chkStrNull(this.name)) {
					strBF.append("<div id=\"")
						.append(this.name)
						.append("\" style=\"display:none;\" obj_type=\"button\"></div>");
				}
			}
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
			if (this.hasRight) {
				StringBuffer strBF = new StringBuffer();
				strBF.append("</button>");
				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(strBF.toString());
			}
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		if (StringUtil.chkStrNull(type)) {
			this.type = null;
		} else {
			type = type.toLowerCase();
			if ("primary".equals(type) || "normal".equals(type)
					|| "warm".equals(type) || "danger".equals(type)
					|| "disabled".equals(type)) {
				this.type = type;
			} else {
				this.type = null;
			}
		}
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		if (StringUtil.chkStrNull(size)) {
			this.size = null;
		} else {
			size = size.toLowerCase();
			if ("lg".equals(size) || "sm".equals(size) || "xs".equals(size)) {
				this.size = size;
			} else {
				this.size = null;
			}
		}
	}

	public boolean isRadius() {
		return radius;
	}

	public void setRadius(boolean radius) {
		this.radius = radius;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getFunctionid() {
		return functionid;
	}

	public void setFunctionid(String functionid) {
		this.functionid = functionid;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
