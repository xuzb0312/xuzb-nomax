package com.grace.frame.shine.taglib;

import javax.servlet.jsp.JspException;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.StringUtil;

/**
 * 选项页标签
 * 
 * @author yjc
 */
public class TabPageTag extends Tag{
	private static final long serialVersionUID = 1L;

	private String name;
	private String title;// 标题
	private boolean selected;// 是否默认选择

	public TabPageTag() {
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
		this.name = null;// 页边距默认10px;
		this.title = null;
		this.selected = false;
	}

	/**
	 * 标签开始时
	 */
	@Override
	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	/**
	 * 标签结束时，执行
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.title)) {
				throw new BizException("标签的title属性不允许为空");
			}

			if (!(this.getParent() instanceof TabTag)) {
				throw new AppException("该标签的父表签必须为tab标签，请检查。");
			}
			if (StringUtil.chkStrNull(this.name)) {
				this.name = StringUtil.getUUID();
			}

			String bodyConStr = "";
			if (this.bodyContent != null) {
				bodyConStr = this.bodyContent.getString();
				if (null == bodyConStr) {
					bodyConStr = "";
				}
			}
			// 添加到父表签中
			TabTag tabTag = (TabTag) this.getParent();
			tabTag.addTabPage(this.name, this.title, this.selected, bodyConStr);
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
