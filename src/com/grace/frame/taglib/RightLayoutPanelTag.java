package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 右侧布局panel
 * 
 * @author yjc
 */
public class RightLayoutPanelTag extends Tag{

	private static final long serialVersionUID = 1L;
	private String title;// 标题
	private String iconCls;// 图标
	private String split;// 是否可调整大小-分隔条
	private String width;// 宽度
	private String border;// 边框
	private String background;// 背景色
	private String collapsed;// 是否折叠，默认不折叠
	private int padding;// 内边距
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排

	public RightLayoutPanelTag() {
		this.initTag();
	}

	private void initTag() {
		this.title = null;
		this.iconCls = null;
		this.split = null;
		this.width = "200";// 默认200像素
		this.border = null;
		this.background = null;
		this.collapsed = null;
		this.padding = 0;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 检测数据
			this.checkData();

			StringBuffer strBF = new StringBuffer();
			strBF.append("<div ");

			// easyui属性
			this.clearEasyUICompAttr();
			this.setEasyUICompAttr("region", "east", true);// 右侧
			if (!StringUtil.chkStrNull(this.iconCls)) {// 图标不为空，增加
				this.setEasyUICompAttr("iconCls", this.iconCls, true);
			}
			if ("true".equalsIgnoreCase(this.split)) {// 分隔条
				this.setEasyUICompAttr("split", "true", false);
			}
			if ("true".equalsIgnoreCase(this.collapsed)) {// 是否是收起的
				this.setEasyUICompAttr("collapsed", "true", false);
			}
			if ("false".equalsIgnoreCase(this.border)) {
				this.setEasyUICompAttr("border", "false", false);
			}
			if (!StringUtil.chkStrNull(this.title)) {
				this.setEasyUICompAttr("title", this.title, true);
			}
			String dataOpt = this.getEasyUICompAttrOptions();
			if (!StringUtil.chkStrNull(dataOpt)) {
				strBF.append(" data-options=\"" + dataOpt + "\"");
			}
			strBF.append(" style=\"width:"
					+ this.width
					+ "px; "
					+ (StringUtil.chkStrNull(this.background) ? "" : "background:"
							+ this.background + ";")
					+ ((this.padding > 0) ? ("padding:" + this.padding + "px;") : "")
					+ "\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append(">");

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
	 * 数据检测
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-6-27
	 * @since V1.0
	 */
	private void checkData() throws AppException {
		// 首先判断父表签类型
		if (!(this.getParent() instanceof LayoutTag)) {
			throw new AppException("可伸缩面板的父容器必须为layout");
		}

		if (StringUtil.chkStrNull(this.width)) {
			throw new AppException("必须为本容器定义宽度");
		}
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
			this.release();
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getSplit() {
		return split;
	}

	public void setSplit(String split) {
		this.split = split;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getBorder() {
		return border;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getCollapsed() {
		return collapsed;
	}

	public void setCollapsed(String collapsed) {
		this.collapsed = collapsed;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getIntroSetp() {
		return introSetp;
	}

	public void setIntroSetp(int introSetp) {
		this.introSetp = introSetp;
	}

	public String getIntroContent() {
		return introContent;
	}

	public void setIntroContent(String introContent) {
		this.introContent = introContent;
	}

	public String getIntroPosition() {
		return introPosition;
	}

	public void setIntroPosition(String introPosition) {
		this.introPosition = introPosition;
	}
}
