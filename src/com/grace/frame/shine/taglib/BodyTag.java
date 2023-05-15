package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysUser;

/**
 * 所有标签的父标签
 * 
 * @author yjc
 */
public class BodyTag extends Tag{
	private static final long serialVersionUID = 5426205844986825375L;
	private int padding;// 页边距，默认10px;
	private int minWidth;// 最小宽度，如果小于这个，则出现滚动条

	public BodyTag() {
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
		this.padding = 10;// 页边距默认10px;
		this.minWidth = 0;
	}

	/**
	 * 标签开始时
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			// 对于经办机构id,经办机构权限范围等页面的字段提供容器放置数据
			strBF.append("<div style=\"display: none;\">");
			// 根据用户信息，获取默认的jbjgid和jbjgqxfw;首先从request中获取
			String jbjgid = (String) this.pageContext.getRequest()
				.getAttribute("__jbjgid");
			String jbjgqxfw = (String) this.pageContext.getRequest()
				.getAttribute("__jbjgqxfw");
			String yhid = (String) this.pageContext.getRequest()
				.getAttribute("__yhid");

			// 获取用户信息
			SysUser currentSysUser = (SysUser) this.pageContext.getSession()
				.getAttribute("currentsysuser");// 当前用户
			if (null == currentSysUser) {
				currentSysUser = new SysUser();
				currentSysUser.setAllInfoDM(new DataMap());
			}
			if (StringUtil.chkStrNull(jbjgid) || "00000000".equals(jbjgid)) {
				jbjgid = currentSysUser.getAllInfoDM()
					.getString("ssjbjgid", "");
				if (null == jbjgid) {
					jbjgid = "";
				}
			}
			if (StringUtil.chkStrNull(jbjgqxfw) || "00000000".equals(jbjgqxfw)) {
				jbjgqxfw = currentSysUser.getAllInfoDM()
					.getString("yhjbjgqxfw", "");
				if (null == jbjgqxfw) {
					jbjgqxfw = "";
				}
			}

			// 用户ID为空，获取用户信息的用户ID
			if (StringUtil.chkStrNull(yhid)) {
				yhid = currentSysUser.getYhid();
				if (null == yhid) {
					yhid = "";
				}
			}

			// jsp路径
			String jsppath = (String) this.pageContext.getRequest()
				.getAttribute("__jsppath");// 请求页面的当前jsp路径
			if (!StringUtil.chkStrNull(jsppath)) {
				strBF.append("<input id=\"__jsppath\" value=\"" + jsppath
						+ "\" />");// jsppath
			}
			strBF.append("<input id=\"__jbjgid\" value=\"" + jbjgid + "\" />");// jbjgid
			strBF.append("<input id=\"__jbjgqxfw\" value=\"" + jbjgqxfw
					+ "\" />");// 权限范围
			strBF.append("<input id=\"__yhid\" value=\"" + yhid + "\" />");// yhid
			strBF.append("</div>");

			strBF.append("<div style=\"padding:" + this.padding + "px;")
				.append(this.minWidth > 0 ? ("min-width:" + this.minWidth + "px;") : "")
				.append("\">");
			// 数据的输出
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
	 * 标签结束时，执行
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			// 数据的输出
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

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}
}
