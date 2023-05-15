package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 目录项目
 * 
 * @author yjc
 */
public class IndexItemTag extends Tag{
	private static final long serialVersionUID = 81579359858033025L;
	private String label;// 标签
	private String target;// 目标--标签的name
	private String onclick;// 事件

	/**
	 * 构造函数
	 */
	public IndexItemTag() {
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
		this.label = null;
		this.target = null;
		this.onclick = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.label)) {
				throw new AppException("label项不允许为空");
			}
			if (!(this.getParent() instanceof IndexTag)) {
				throw new AppException("该标签的父表签必须为index标签");
			}

			StringBuffer strBF = new StringBuffer();
			strBF.append("<li><a href=\"");
			if (!StringUtil.chkStrNull(this.onclick)) {
				strBF.append("javascrip:void(0);");
			} else {
				strBF.append("#");
				strBF.append(StringUtil.chkStrNull(this.target) ? "" : this.target);
			}
			strBF.append("\"")
				.append(StringUtil.chkStrNull(this.onclick) ? "" : (" onclick=\""
						+ this.onclick + "\""))
				.append(">")
				.append(this.label)
				.append("</a></li>");
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}
}
