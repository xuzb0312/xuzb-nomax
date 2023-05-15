package com.grace.frame.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

public class TabPageTag extends Tag{

	private static final long serialVersionUID = 1L;
	// 属性
	private String name;
	private String title;
	private String iconCls;
	private String closable;// 是否可关闭
	private String selected;// 是否默认选择
	private List<String> listGridTagName;// grid标签的名称数组

	public TabPageTag() {
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
		this.name = null;
		this.title = null;
		this.iconCls = null;
		this.closable = null;
		this.selected = null;
		this.listGridTagName = new ArrayList<String>();
	}

	/**
	 * 提供给子标签的增加grid的方法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-9-25
	 * @since V1.0
	 */
	public void addGridName(String name) {
		this.listGridTagName.add(name);
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 数据检查
			this.checkData();

			// 数据组装
			StringBuffer strBF = new StringBuffer();

			if (!StringUtil.chkStrNull(this.name)) {
				// 由于easyui的tabpage的查找是通过title，所以这里使用map进行一次数据缓存,保存name与title的关系，实现使用name查找
				TabTag parenttag = (TabTag) this.getParent();// 父表签
				String parentTagName = parenttag.getName();
				if (!StringUtil.chkStrNull(parentTagName)) {
					this.appendln(strBF, "<script type=\"text/javascript\">");
					this.appendln(strBF, parentTagName
							+ "_tabPageKeyIndexMap.put(\"" + this.name
							+ "\",\"" + this.title + "\")");
					this.appendln(strBF, "</script>");
				}
			}

			strBF.append("<div title=\"" + this.title + "\" ");
			// easyui属性
			this.clearEasyUICompAttr();
			if (!StringUtil.chkStrNull(this.iconCls)) {
				this.setEasyUICompAttr("iconCls", this.iconCls, true);
			}
			if ("true".equalsIgnoreCase(this.closable)) {
				this.setEasyUICompAttr("closable", "true", false);
			}
			if ("true".equalsIgnoreCase(this.selected)) {
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
		if (!(this.getParent() instanceof TabTag)) {
			throw new AppException("TabPage的父容器必须为Tab标签。");
		}

		if (StringUtil.chkStrNull(this.title)) {
			throw new AppException("TabPage必须存在标题");
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

			// 向父表签发送grid数据
			TabTag parent = (TabTag) this.getParent();
			parent.addTpGridName(this.title, this.listGridTagName);

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getClosable() {
		return closable;
	}

	public void setClosable(String closable) {
		this.closable = closable;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
}
