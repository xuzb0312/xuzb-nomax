package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 时间线标签的元素
 * 
 * @author yjc
 */
public class TimelineItemTag extends Tag{
	private static final long serialVersionUID = 8340525952401214441L;
	private String title;// 标题名称（可为空，展示样式不太一致）
	private String icon;// 图标-使用标准layui图标库，内容值为：&#xe6c6;的形式默认为：&#xe63f;
	private String iconColor;// 图标颜色
	private boolean titleBold;// 标题是否进行加错展示默认为true
	private String content;// 内容--不对外提供-为父表签进行赋值使用

	/**
	 * 构造函数
	 */
	public TimelineItemTag() {
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
		this.title = null;
		this.icon = "&#xe63f;";
		this.titleBold = true;
		this.iconColor = null;
		this.content = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 首先判断父表签是否为Timeline标签
			if (!(this.getParent() instanceof TimelineTag)) {
				throw new AppException("timelineItem标签的父表签必须为timeline标签");
			}
			if (StringUtil.chkStrNull(this.title)) {
				throw new AppException("timelineItem标签的title属性不允许为空");
			}
			if (StringUtil.chkStrNull(this.icon)) {
				this.icon = "&#xe63f;";
			}
			StringBuffer strBF = new StringBuffer();
			strBF.append("<li class=\"layui-timeline-item\"><i class=\"layui-icon ");
			if ("&#x1002;&#xe63d;&#xe63e;".indexOf(this.icon) >= 0) {// 动画--需要进行动画旋转的图标-增加动画选择效果
				strBF.append("layui-anim layui-anim-rotate layui-anim-loop ");
			}
			strBF.append("layui-timeline-axis\"")
				.append((StringUtil.chkStrNull(this.iconColor) ? "" : "style=\"color:"
						+ this.iconColor + ";\""))
				.append(">")
				.append(this.icon)
				.append("</i><div class=\"layui-timeline-content layui-text\">");
			if (this.titleBold) {
				strBF.append("<h3 class=\"layui-timeline-title\">")
					.append(this.title)
					.append("</h3>");
			} else {
				strBF.append("<div class=\"layui-timeline-title\">")
					.append(this.title)
					.append("</div>");
			}
			if (!StringUtil.chkStrNull(this.content)) {// 内容信息
				strBF.append(this.content);
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
	 * 注入内容信息，父标签调用使用
	 * 
	 * @author yjc
	 * @date 创建时间 2017-11-24
	 * @since V1.0
	 */
	public void injectContent(String content) {
		this.content = content;
	}

	/**
	 * 终止
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write("</div></li>");
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isTitleBold() {
		return titleBold;
	}

	public void setTitleBold(boolean titleBold) {
		this.titleBold = titleBold;
	}

	public String getIconColor() {
		return iconColor;
	}

	public void setIconColor(String iconColor) {
		this.iconColor = iconColor;
	}
}
