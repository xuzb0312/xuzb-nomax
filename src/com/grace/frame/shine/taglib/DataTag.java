package com.grace.frame.shine.taglib;

import javax.servlet.jsp.JspException;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 数据选项标签-提供给其他的一些标签使用的选项提供
 * 
 * @author yjc
 */
public class DataTag extends Tag{
	private static final long serialVersionUID = -6411838127519887665L;
	private String key;
	private String value;

	/**
	 * 构造函数
	 */
	public DataTag() {
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
		this.key = null;
		this.value = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.key)) {
				throw new AppException("data标签key不允许为空");
			}
			if (StringUtil.chkStrNull(this.value)) {
				throw new AppException("data标签value不允许为空");
			}
			if (this.getParent() instanceof CheckboxListTag) {
				// 复选框
				CheckboxListTag tag = (CheckboxListTag) this.getParent();
				tag.setChildOpt(this.key, this.value);
			} else if (this.getParent() instanceof RadioButtonListTag) {
				// 单选框
				RadioButtonListTag tag = (RadioButtonListTag) this.getParent();
				tag.setChildOpt(this.key, this.value);
			} else if (this.getParent() instanceof DropdownListTag) {
				// 下拉框
				DropdownListTag tag = (DropdownListTag) this.getParent();
				tag.setChildOpt(this.key, this.value);
			} else if (this.getParent() instanceof DataGroupTag) {
				// 选项组
				DataGroupTag tag = (DataGroupTag) this.getParent();
				tag.setChildOpt(this.key, this.value);
			} else {
				throw new AppException("data标签不支持放到本标签下渲染");
			}
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
