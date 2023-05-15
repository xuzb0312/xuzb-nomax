package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;

/**
 * 索引目录标签
 * 
 * @author yjc
 */
public class IndexTag extends Tag{
	private static final long serialVersionUID = 7434436101761947280L;

	private String name;// 唯一标识，不对外提供设置
	private String title;// 标题

	/**
	 * 构造函数
	 */
	public IndexTag() {
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
		this.title = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				this.name = StringUtil.getUUID();
			}
			StringBuffer strBF = new StringBuffer();
			if (!(this.getParent() instanceof IndexTag)) {// 父表签是indexTag
				strBF.append("<div id=\"")
					.append(this.name)
					.append("\" class=\"pageindex\" obj_type=\"index\">");
			}
			strBF.append("<ul style=\"padding-left: 15px;\">");

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
			if (StringUtil.chkStrNull(this.title)) {
				this.title = "目录";
			}
			StringBuffer strBF = new StringBuffer();
			strBF.append("</ul>");
			if (!(this.getParent() instanceof IndexTag)) {// 父表签是indexTag
				strBF.append("</div>");
			}
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			// 初始化
			if (!(this.getParent() instanceof IndexTag)) {// 父表签是indexTag
				DataMap dmInit = new DataMap();
				dmInit.put("title", this.title);
				this.objInit(dmInit, this.name);
			}
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		} finally {
			this.release();
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
