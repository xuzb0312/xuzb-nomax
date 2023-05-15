package com.grace.frame.taglib;

import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.StringUtil;

/**
 * 隐藏标签
 * 
 * @author yjc
 */
public class HiddenInputTag extends Tag{

	private static final long serialVersionUID = 1L;
	// 属性
	private String name;// 唯一id
	private String value;// 值

	public HiddenInputTag() {
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
		this.value = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				throw new AppException("name属性不允许为空");
			}
			// 增加formTag不必须放到form中的逻辑
			if (this.getParent() instanceof FormTag) {
				FormTag form = (FormTag) this.getParent();

				// 获取value值
				String valueTemp = this.value;
				if (StringUtil.chkStrNull(valueTemp)) {
					Object valueObj = form.getDataSourceData(this.name);
					if (valueObj == null) {
						valueTemp = "";
					} else if (valueObj instanceof Date) {
						valueTemp = DateUtil.dateToString((Date) valueObj);
					} else {
						valueTemp = String.valueOf(valueObj);
					}
				}

				// 添加到父表签
				form.addHiddenInputTag(this.name, valueTemp);
			} else {
				// 如果不再form下，自动生成隐藏数据
				StringBuffer strBF = new StringBuffer();
				strBF.append("<div style=\"display:none;\">");
				strBF.append("<input obj_type=\"hiddeninput\" type=\"text\" id=\"")
					.append(this.name)
					.append("\" ")
					.append(" name=\"")
					.append(this.name)
					.append("\" ")
					.append(" value=\"")
					.append(this.value)
					.append("\" />");
				strBF.append("</div>");
				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(strBF.toString());
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
