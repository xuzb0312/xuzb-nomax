package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.taglib.grid.ColumnDropDownTag;
import com.grace.frame.taglib.grid.ColumnMultiDropDownTag;
import com.grace.frame.util.StringUtil;

/**
 * 数据标签，主要用于选择控件的数据设置
 * 
 * @author yjc
 */
public class DataTag extends Tag{
	private static final long serialVersionUID = 1L;
	// 属性
	private String key;
	private String value;
	private String color;// grid的单选下拉框的tag颜色

	public DataTag() {
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
		this.key = null;
		this.value = null;
		this.color = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 数据判断-对于key和value均不允许为空如果为空则抛出异常
			if (StringUtil.chkStrNull(this.key)) {
				throw new AppException("key值不能为空");
			}
			if (StringUtil.chkStrNull(this.value)) {
				throw new AppException("value为空");
			}
			StringBuffer strBF = new StringBuffer();

			// 需要判断父标签为什么类型的标签
			if (this.getParent() instanceof DropDownListTag) {
				DropDownListTag tag = (DropDownListTag) this.getParent();
				// 为下拉框
				strBF.append("<option value=\"")
					.append(this.key)
					.append("\" ")
					.append(this.key.equals(tag.getRealValue()) ? "selected=\"selected\"" : "")
					.append(">")
					.append(this.value)
					.append("</option>");

				// 在范围内找到value值，则范围不超出
				if (this.key.equals(tag.getRealValue())) {
					tag.setValueOut(false);
					tag.setValueText(this.value);
				}
			} else if (this.getParent() instanceof MultiDropdownListTag) {
				// 多选下拉框
				MultiDropdownListTag tag = (MultiDropdownListTag) this.getParent();
				tag.setChildOpt(this.key, this.value);
			} else if (this.getParent() instanceof MultiSelectBoxTag) {
				// 多选框
				MultiSelectBoxTag tag = (MultiSelectBoxTag) this.getParent();
				tag.setChildOpt(this.key, this.value);
			} else if (this.getParent() instanceof CheckBoxListTag) {
				// 复选框
				CheckBoxListTag tag = (CheckBoxListTag) this.getParent();
				tag.setChildOpt(this.key, this.value);
			} else if (this.getParent() instanceof RadiobuttonListTag) {
				// 单选按钮
				RadiobuttonListTag tag = (RadiobuttonListTag) this.getParent();
				tag.setChildOpt(this.key, this.value);
			} else if (this.getParent() instanceof ColumnDropDownTag) {// grid下拉框
				// grid的下拉框数据项设置
				ColumnDropDownTag tag = (ColumnDropDownTag) this.getParent();
				tag.setChildOpt(this.key, this.value, this.color);
			} else if (this.getParent() instanceof ColumnMultiDropDownTag) {// grid复选下拉框
				// grid的下拉框数据项设置
				ColumnMultiDropDownTag tag = (ColumnMultiDropDownTag) this.getParent();
				tag.setChildOpt(this.key, this.value, this.color);
			} else {
				throw new AppException("data标签不允许直接放置到此标签下。");
			}

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		}
		return SKIP_BODY;
	}

	/**
	 * 终止
	 */
	@Override
	public int doEndTag() throws JspException {
		this.release();
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
