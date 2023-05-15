package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 右下角的bar（一个页面只允许有一个）-通常会出现在那个固定位置，由两个可选的bar和一个默认必选的TopBar组成。
 * 
 * @author yjc
 */
public class TopBarTag extends Tag{
	private static final long serialVersionUID = -5328874882265853569L;

	private String bar1;// 默认false。如果值为true，则显示第一个bar，带有一个默认图标。如果值为图标字符，则显示第一个bar，并覆盖默认图标
	private String bar2;// 默认false。如果值为true，则显示第一个bar，带有一个默认图标。如果值为图标字符，则显示第一个bar，并覆盖默认图标
	private String bgcolor;// 自定义区块背景色
	private int showHeight;// 用于控制出现TOP按钮的滚动条高度临界值。默认：200

	// 事件
	private String onClick;// 点击bar的回调，函数返回一个type参数，用于区分bar类型。支持的类型有：bar1、bar2、top

	/**
	 * 构造函数
	 */
	public TopBarTag() {
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
		this.bar1 = null;
		this.bar2 = null;
		this.bgcolor = null;
		this.showHeight = 0;
		this.onClick = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (null == this.pageContext.getAttribute("__has_render_topbar")) {
				StringBuffer strBF = new StringBuffer();
				strBF.append("<script type=\"text/javascript\">");
				strBF.append("layui.use(\"util\", function(){");
				strBF.append("var util = layui.util;");
				strBF.append("util.fixbar({");
				if (!StringUtil.chkStrNull(this.bar1)) {
					if ("true".equalsIgnoreCase(this.bar1)
							|| "false".equals(this.bar1)) {
						strBF.append("bar1:")
							.append(this.bar1.toLowerCase())
							.append(",");
					} else {
						strBF.append("bar1:\"").append(this.bar1).append("\",");
					}
				}
				if (!StringUtil.chkStrNull(this.bar2)) {
					if ("true".equalsIgnoreCase(this.bar2)
							|| "false".equals(this.bar2)) {
						strBF.append("bar2:")
							.append(this.bar2.toLowerCase())
							.append(",");
					} else {
						strBF.append("bar2:\"").append(this.bar2).append("\",");
					}
				}
				if (!StringUtil.chkStrNull(this.bgcolor)) {
					strBF.append("bgcolor:\"")
						.append(this.bgcolor)
						.append("\",");
				}
				if (this.showHeight > 0) {
					strBF.append("showHeight:")
						.append(this.showHeight)
						.append(",");
				}
				if (!StringUtil.chkStrNull(this.onClick)) {
					strBF.append("click:")
						.append("function(type){")
						.append(this.onClick)
						.append("(type);}")
						.append(",");
				}

				strBF.append("css:{right:10,bottom:20}");
				strBF.append("});");
				strBF.append("});");
				strBF.append("</script>");
				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(strBF.toString());

				this.pageContext.setAttribute("__has_render_topbar", true);
			} else {
				throw new AppException("一个JSP页面只允许存在一个topBar标签，请检查。");
			}
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

	public String getBar1() {
		return bar1;
	}

	public void setBar1(String bar1) {
		this.bar1 = bar1;
	}

	public String getBar2() {
		return bar2;
	}

	public void setBar2(String bar2) {
		this.bar2 = bar2;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public void setBgcolor(String bgcolor) {
		this.bgcolor = bgcolor;
	}

	public int getShowHeight() {
		return showHeight;
	}

	public void setShowHeight(int showHeight) {
		this.showHeight = showHeight;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}
}
