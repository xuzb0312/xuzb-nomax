package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;

/**
 * 文本域-父表签必须是form
 * 
 * @author yjc
 */
public class TextareaTag extends Tag{
	private static final long serialVersionUID = -6839149417293976760L;
	private String name;
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private String value;// 值
	private boolean readonly;// 只读，默认为false;
	private boolean required;// 是否必须
	private int height;// 高度-px\
	private String placeholder;// 文本为空的时候的提示内容

	// 事件
	private String onclick;// 单击
	private String onchange;// 数据变化
	private String ondblclick;// 双击
	private String onblur;// 失去焦点
	private String onfocus;// 获得焦点时

	public TextareaTag() {
		this.initTag();
	}

	/**
	 * 参数初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-16
	 * @since V1.0
	 */
	private void initTag() {
		this.name = null;
		this.label = null;
		this.labelColor = null;
		this.value = null;
		this.readonly = false;
		this.required = false;// 默认非必须
		this.height = 100;
		this.placeholder = null;

		this.onclick = null;// 单击
		this.onchange = null;// 数据变化
		this.ondblclick = null;// 双击
		this.onblur = null;// 失去焦点
		this.onfocus = null;// 获得焦点时
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 参数检查
			if (StringUtil.chkStrNull(this.name)) {
				throw new AppException("name属性不允许为空");
			}
			if (!(this.getParent() instanceof FormTag)) {// 判断，该标签必须包含在form下
				throw new AppException("该标签必须包含在form标签下");
			}
			FormTag formTag = (FormTag) this.getParent();// 获取到父表签
			if (StringUtil.chkStrNull(this.placeholder)) {
				this.placeholder = "";
			}

			// 开始组装数据
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div class=\"layui-form-item layui-form-text\">");
			strBF.append("<label class=\"layui-form-label\" id=\"" + this.name
					+ "_label\"")
				.append(StringUtil.chkStrNull(this.labelColor) ? "" : (" style=\"color:"
						+ this.labelColor + "\""))
				.append(">");
			if (!StringUtil.chkStrNull(this.label)) {
				strBF.append(this.label);
			}
			if (this.required) {
				strBF.append("<span style=\"color:red;\">*</span>");
			}
			strBF.append("</label>");
			strBF.append("<div class=\"layui-input-block\">");
			strBF.append("<textarea obj_type=\"textarea\" id=\"" + this.name
					+ "\" ")
				.append((StringUtil.chkStrNull(this.placeholder) || this.readonly) ? "" : ("placeholder=\""
						+ this.placeholder + "\" "))
				.append("class=\"layui-textarea\" style=\"height: "
						+ this.height + "px;")
				.append(this.readonly ? "background:#FCFCFC;" : "")
				.append("\"")
				.append(this.readonly ? " readonly=\"readonly\"" : "")
				.append(StringUtil.chkStrNull(this.onclick) ? "" : (" onclick=\""
						+ this.onclick + "\""))
				.append(StringUtil.chkStrNull(this.onblur) ? "" : (" onblur=\""
						+ this.onblur + "\""))
				.append(StringUtil.chkStrNull(this.onchange) ? "" : (" onchange=\""
						+ this.onchange + "\""))
				.append(StringUtil.chkStrNull(this.ondblclick) ? "" : (" ondblclick=\""
						+ this.ondblclick + "\""))
				.append(StringUtil.chkStrNull(this.onfocus) ? "" : (" onfocus=\""
						+ this.onfocus + "\""))
				.append(">")
				.append(StringUtil.chkStrNull(this.value) ? "" : this.value)
				.append("</textarea>");
			strBF.append("</div>");
			strBF.append("</div>");
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			// 发送标签参数并初始化
			DataMap dmInit = new DataMap();
			dmInit.put("required", this.required);
			dmInit.put("placeholder", placeholder);
			this.objInit(dmInit, formTag.getName() + "." + this.name);
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
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
			this.release();
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(String labelColor) {
		this.labelColor = labelColor;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getOnchange() {
		return onchange;
	}

	public void setOnchange(String onchange) {
		this.onchange = onchange;
	}

	public String getOndblclick() {
		return ondblclick;
	}

	public void setOndblclick(String ondblclick) {
		this.ondblclick = ondblclick;
	}

	public String getOnblur() {
		return onblur;
	}

	public void setOnblur(String onblur) {
		this.onblur = onblur;
	}

	public String getOnfocus() {
		return onfocus;
	}

	public void setOnfocus(String onfocus) {
		this.onfocus = onfocus;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

}
