package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 中间布局panel
 * 
 * @author yjc
 */
public class CenterLayoutPanelTag extends Tag{
	private static final long serialVersionUID = 1L;
	private String title;// 标题
	private String iconCls;// 图标
	private String border;// 边框
	private String background;// 背景色
	private int padding;// 内边距
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排

	public CenterLayoutPanelTag() {
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
		this.title = null;
		this.iconCls = null;
		this.border = null;
		this.background = null;
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
			strBF.append("<div style=\"padding:"
					+ this.padding
					+ "px;"
					+ (StringUtil.chkStrNull(this.background) ? "" : "background:"
							+ this.background + ";") + "\"");

			// easyui属性
			this.clearEasyUICompAttr();
			this.setEasyUICompAttr("region", "center", true);// 中心
			if (!StringUtil.chkStrNull(this.iconCls)) {// 图标不为空，增加
				this.setEasyUICompAttr("iconCls", this.iconCls, true);
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
			this.release();// 资源释放
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
