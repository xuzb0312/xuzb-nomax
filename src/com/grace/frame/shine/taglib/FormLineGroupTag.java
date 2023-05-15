package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 行内分组
 * 
 * @author yjc
 */
public class FormLineGroupTag extends Tag{
	private static final long serialVersionUID = 487618762431361532L;
	private String name;// 唯一标识
	private String label;// 组标签
	private String labelColor;// 标签颜色

	/**
	 * 构造函数
	 */
	public FormLineGroupTag() {
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
		this.name = null;
		this.label = null;
		this.labelColor = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (!(this.getParent() instanceof FormLineTag)) {// 判断，该标签必须包含在FormLineTag下
				throw new AppException("该标签必须包含在FormLineTag标签下");
			}
			if (StringUtil.chkStrNull(this.name)) {
				this.name = StringUtil.getUUID();
			}
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div class=\"layui-inline\"><label obj_type=\"formlinegroup\" class=\"layui-form-label\" id=\""
					+ this.name + "\"")
				.append(StringUtil.chkStrNull(this.labelColor) ? "" : (" style=\"color:"
						+ this.labelColor + "\""))
				.append(">");
			if (!StringUtil.chkStrNull(this.label)) {
				strBF.append(this.label);
			}
			strBF.append("</label>");
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(String labelColor) {
		this.labelColor = labelColor;
	}
}
