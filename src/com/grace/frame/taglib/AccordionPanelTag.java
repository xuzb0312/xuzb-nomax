package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 可伸缩面板标签的内部panel，其父容器必须为Accordion
 * 
 * @author yjc
 */
public class AccordionPanelTag extends Tag{
	private static final long serialVersionUID = 1L;
	// 属性
	private String title;// 标题
	private String iconCls;// 图标
	private String selected;// 是否是默认选中的
	private String background;// 背景色

	public AccordionPanelTag() {
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
		this.title = "";
		this.iconCls = "icon-application";// 默认图标
		this.selected = "";
		this.background = null;
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
			strBF.append("<div title=\""
					+ this.title
					+ "\" style=\"overflow: auto; padding: 5px; "
					+ (StringUtil.chkStrNull(this.background) ? "" : "background:"
							+ this.background + ";") + "\" ");

			// easyui属性
			this.clearEasyUICompAttr();
			if (!StringUtil.chkStrNull(this.iconCls)) {// 图标不为空，增加
				this.setEasyUICompAttr("iconCls", this.iconCls, true);
			}
			if ("true".equalsIgnoreCase(this.selected.trim())) {// 是否默认选中
				this.setEasyUICompAttr("selected", "true", false);
			}
			String dataOpt = this.getEasyUICompAttrOptions();
			if (!StringUtil.chkStrNull(dataOpt)) {
				strBF.append(" data-options=\"" + dataOpt + "\"");
			}
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
		if (!(this.getParent() instanceof AccordionTag)) {
			throw new AppException("可伸缩面板的父容器必须为accordion");
		}

		if (StringUtil.chkStrNull(this.title)) {
			throw new AppException("标题不允许为空");
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

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}
}
