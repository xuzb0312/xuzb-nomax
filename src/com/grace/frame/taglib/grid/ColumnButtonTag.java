package com.grace.frame.taglib.grid;

import javax.servlet.jsp.JspException;

import com.grace.frame.exception.AppException;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.StringUtil;

/**
 * 按钮
 * 
 * @author yjc
 */
public class ColumnButtonTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String key;// 唯一Key
	private String value;// 按钮文字
	private String onClick;// 参数onClick(rowid);
	private String color;// 按钮颜色

	public ColumnButtonTag() {
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
		this.onClick = null;
		this.color = "#FF5722";
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 数据的检测，首先判断父表签
			if (!(this.getParent() instanceof ColumnButtonsTag)) {
				throw new AppException("该标签必须包含在ColumnButtons标签下");
			}
			// 数据判断
			if (StringUtil.chkStrNull(this.value)) {
				throw new AppException("传入的value为空");
			}
			if (StringUtil.chkStrNull(this.onClick)) {
				throw new AppException("传入的onClick为空");
			}
			if (StringUtil.chkStrNull(this.key)) {
				this.key = StringUtil.getPy(this.value);
			}
			if (StringUtil.chkStrNull(this.color)) {
				this.color = "#FF5722";
			}
			ColumnButtonsTag parent = (ColumnButtonsTag) this.getParent();
			parent.addBtn(this.key, this.value, this.onClick, this.color);
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
