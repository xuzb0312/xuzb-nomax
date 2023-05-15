package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;

/**
 * 文本输入框标签
 * 
 * @author yjc
 */
public class TextInputTag extends Tag{
	private static final long serialVersionUID = 6894314604463770785L;

	// 属性
	private String name;// 唯一id
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private String value;// 值
	private boolean readonly;// 只读，默认为false;
	private String dataType;// 数据类型，string,number,date三种-默认为string;
	private boolean required;// 是否必须
	private String align;// 文本对齐方式，默认string-left,date-center,number-right;
	private String placeholder;// 文本为空的时候的提示内容
	private String validType;// 验证文本--具体验证方式包含多种，参照extensions.validatebox的验证类型;外加原生的email,url,length[1,2],remote
	private String mask;// 文本的格式
	private String sourceMask;// 原数据格式
	private boolean password;// 是不是密码
	private boolean hidden;// 是否隐藏

	// 事件
	private String onclick;// 单击
	private String onchange;// 数据变化
	private String ondblclick;// 双击
	private String onblur;// 失去焦点
	private String onfocus;// 获得焦点时
	private String onsearchclick;// 当配置了此事件-则会存在searchbtn
	private String onKeyDown;// 键盘事件-add.yjc.2015年10月22日

	// 事件
	public TextInputTag() {
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
		this.dataType = "string";// 数据类型默认为string
		this.required = false;// 默认非必须
		this.placeholder = null;
		this.validType = null;
		this.mask = null;
		this.sourceMask = null;
		this.password = false;
		this.hidden = false;
		this.align = null;

		this.onclick = null;// 单击
		this.onchange = null;// 数据变化
		this.ondblclick = null;// 双击
		this.onblur = null;// 失去焦点
		this.onfocus = null;// 获得焦点时
		this.onsearchclick = null;
		this.onKeyDown = null;
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
			if (null == this.value) {
				this.value = "";
			}
			if (StringUtil.chkStrNull(this.align)) {
				if ("string".equalsIgnoreCase(this.dataType)) {
					this.align = "left";
				} else if ("date".equalsIgnoreCase(this.dataType)) {
					this.align = "center";
				} else if ("number".equalsIgnoreCase(this.dataType)) {
					this.align = "left";
				} else {
					throw new AppException("datatype属性不合法");
				}
			}
			if (StringUtil.chkStrNull(this.placeholder)) {
				this.placeholder = "";
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
			FormTag formTag;
			int width = 0;// 子元素宽度
			Object parent = this.getParent();
			String itemPos = "form";// 子元素所处位置
			if (parent instanceof FormTag) {
				itemPos = "form";
				formTag = (FormTag) parent;
			} else if (parent instanceof FormLineTag) {
				itemPos = "formline";
				formTag = (FormTag) ((FormLineTag) parent).getParent();
				width = ((FormLineTag) parent).getItemWidth();
			} else if (parent instanceof FormLineGroupTag) {
				itemPos = "formlinegroup";
				formTag = (FormTag) ((FormLineGroupTag) parent).getParent()
					.getParent();
				width = ((FormLineTag) ((FormLineGroupTag) parent).getParent()).getItemWidth();
			} else {
				throw new AppException("该标签必须包含在form标签下");
			}

			// 开始构建
			StringBuffer strBF = new StringBuffer();
			if ("formline".equals(itemPos)) {// line子元素
				strBF.append("<div class=\"layui-inline\" style=\"display:")
					.append(this.hidden ? "none" : "block")
					.append(";\"><label class=\"layui-form-label\" id=\""
							+ this.name + "_label\"")
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
				strBF.append("<div class=\"layui-input-inline\"")
					.append(width > 0 ? (" style=\"width:" + width + "px;\"") : "")
					.append(">");
			} else if ("formlinegroup".equals(itemPos)) {// group子元素
				strBF.append("<div class=\"layui-input-inline\"")
					.append(" style=\"display:")
					.append(this.hidden ? "none" : "block")
					.append(";")
					.append(width > 0 ? ("width:" + width + "px;") : "")
					.append("\">");
			} else {
				strBF.append("<div class=\"layui-form-item\" style=\"display:")
					.append(this.hidden ? "none" : "block")
					.append(";\"><label class=\"layui-form-label\" id=\""
							+ this.name + "_label\"")
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
				strBF.append("<div class=\"layui-input-block\"").append(">");
			}
			// 具体内容的渲染--begin
			strBF.append("<input obj_type=\"textinput\" id=\"")
				.append(this.name)
				.append("\" type=\"")
				.append(this.password ? "password" : "text")
				.append("\"")
				.append((StringUtil.chkStrNull(this.placeholder) || this.readonly) ? "" : (" placeholder=\""
						+ this.placeholder + "\""))
				.append(" autocomplete=\"off\" class=\"layui-input\" value=\"")
				.append(this.value)
				.append("\"")
				.append(this.readonly ? " readonly=\"readonly\"" : "")
				.append(" style=\"text-align:")
				.append(StringUtil.chkStrNull(this.align) ? "left" : this.align)
				.append(";")
				.append(this.readonly ? "background:#FCFCFC;" : "")
				.append("\"");
			// 事件
			if (!StringUtil.chkStrNull(this.onclick)) {
				strBF.append(" onclick=\"").append(this.onclick).append("\"");
			}
			if (!StringUtil.chkStrNull(this.onchange)) {
				strBF.append(" onchange=\"").append(this.onchange).append("\"");
			}
			if (!StringUtil.chkStrNull(this.ondblclick)) {
				strBF.append(" ondblclick=\"")
					.append(this.ondblclick)
					.append("\"");
			}
			if (!StringUtil.chkStrNull(this.onfocus)) {
				strBF.append(" onfocus=\"").append(this.onfocus).append("\"");
			}
			if (!StringUtil.chkStrNull(this.onblur)) {
				strBF.append(" onblur=\"").append(this.onblur).append("\"");
			}
			if (!StringUtil.chkStrNull(this.onKeyDown)) {
				strBF.append(" onkeydown=\"return ")
					.append(this.onKeyDown)
					.append("(event);\" ");
			}
			strBF.append("/>");
			// --end
			strBF.append("</div>");
			if (!"formlinegroup".equals(itemPos)) {
				strBF.append("</div>");
			}
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			// 向前台发送数据
			DataMap dmInit = new DataMap();
			dmInit.put("required", this.required);
			dmInit.put("readonly", this.readonly);
			dmInit.put("mask", this.mask);
			dmInit.put("sourcemask", this.sourceMask);
			dmInit.put("validtype", this.validType);
			dmInit.put("datatype", this.dataType);
			dmInit.put("placeholder", this.placeholder);
			dmInit.put("itempos", itemPos);// 元素位置
			this.objInit(dmInit, formTag.getName() + "." + this.name);
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public String getValidType() {
		return validType;
	}

	public void setValidType(String validType) {
		this.validType = validType;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getSourceMask() {
		return sourceMask;
	}

	public void setSourceMask(String sourceMask) {
		this.sourceMask = sourceMask;
	}

	public boolean isPassword() {
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
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

	public String getOnsearchclick() {
		return onsearchclick;
	}

	public void setOnsearchclick(String onsearchclick) {
		this.onsearchclick = onsearchclick;
	}

	public String getOnKeyDown() {
		return onKeyDown;
	}

	public void setOnKeyDown(String onKeyDown) {
		this.onKeyDown = onKeyDown;
	}
}
