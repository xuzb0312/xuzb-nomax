package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 选择框或者开关的方式
 * 
 * @author yjc
 */
public class CheckboxTag extends Tag{
	private static final long serialVersionUID = 3066262566849253435L;
	private String name;// 唯一标识
	private String label;// 标签
	private String labelColor;// 标签颜色
	private String value;// 值
	private boolean readonly;// 只读，默认为false;
	private boolean switchSkin;// 是否开关样式
	private String switchText;// 开关样式的时候的文字，如：开启|关闭

	// 事件
	private String onSwitch;// 开关时，触发：(checked,value,elem,othis):checked:开关是否开启，true或者false;开关value值，也可以通过data.elem.value得到;得到checkbox原始DOM对象;得到美化后的DOM对象

	/**
	 * 构造函数
	 */
	public CheckboxTag() {
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
		this.name = null;
		this.label = null;
		this.labelColor = null;
		this.value = null;
		this.readonly = false;
		this.switchSkin = true;
		this.switchText = "开启|关闭";

		this.onSwitch = null;
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

			// value的处理
			if (StringUtil.chkStrNull(this.value)) {
				this.value = null;
			} else if ("0".equals(this.value)) {
				this.value = null;
			} else if ("false".equalsIgnoreCase(this.value)) {
				this.value = null;
			} else if ("否".equals(this.value)) {
				this.value = null;
			} else if ("no".equalsIgnoreCase(this.value)) {
				this.value = null;
			} else if ("off".equalsIgnoreCase(this.value)) {
				this.value = null;
			} else if ("关".equals(this.value)) {
				this.value = null;
			}
			if (!this.switchSkin) {
				this.switchText = null;
			}

			// form子元素
			StringBuffer strBF = new StringBuffer();
			if ("formline".equals(itemPos)) {// line子元素
				strBF.append("<div class=\"layui-inline\"><label class=\"layui-form-label\" id=\""
						+ this.name + "_label\"")
					.append(StringUtil.chkStrNull(this.labelColor) ? "" : (" style=\"color:"
							+ this.labelColor + "\""))
					.append(">");
				if (!StringUtil.chkStrNull(this.label)) {
					strBF.append(this.label);
				}
				strBF.append("</label>");
				strBF.append("<div class=\"layui-input-inline\"")
					.append(width > 0 ? (" style=\"width:" + width + "px;\"") : "")
					.append(">");
			} else if ("formlinegroup".equals(itemPos)) {// group子元素
				strBF.append("<div class=\"layui-input-inline\"")
					.append(width > 0 ? (" style=\"width:" + width + "px;\"") : "")
					.append(">");
			} else {
				strBF.append("<div class=\"layui-form-item\"")
					.append(formTag.isPane() ? " pane=\"pane\"" : "");
				strBF.append("><label class=\"layui-form-label\" id=\""
						+ this.name + "_label\"")
					.append(StringUtil.chkStrNull(this.labelColor) ? "" : (" style=\"color:"
							+ this.labelColor + "\""))
					.append(">");
				if (!StringUtil.chkStrNull(this.label)) {
					strBF.append(this.label);
				}
				strBF.append("</label>");
				strBF.append("<div class=\"layui-input-block\"").append(">");
			}
			strBF.append("<input type=\"checkbox\" obj_type=\"checkbox\" lay-filter=\"")
				.append(formTag.getName())
				.append("_")
				.append(this.name)
				.append("\" id=\"")
				.append(this.name)
				.append("\"")
				.append(this.switchSkin ? " lay-skin=\"switch\"" : "")
				.append(StringUtil.chkStrNull(this.switchText) ? "" : ("lay-text=\""
						+ this.switchText + "\""))
				.append(StringUtil.chkStrNull(this.value) ? "" : " checked=\"checked\"")
				.append(this.readonly ? " disabled=\"disabled\"" : "")
				.append("/>");
			strBF.append("</div>");
			if (!"formlinegroup".equals(itemPos)) {
				strBF.append("</div>");
			}
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			// 向form标签注册事件
			if (!StringUtil.chkStrNull(this.onSwitch)) {
				formTag.addEvent(this.switchSkin ? "switch" : "checkbox", formTag.getName()
						+ "_" + this.name, this.onSwitch);
			}
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

	public boolean isSwitchSkin() {
		return switchSkin;
	}

	public void setSwitchSkin(boolean switchSkin) {
		this.switchSkin = switchSkin;
	}

	public String getSwitchText() {
		return switchText;
	}

	public void setSwitchText(String switchText) {
		this.switchText = switchText;
	}

	public String getOnSwitch() {
		return onSwitch;
	}

	public void setOnSwitch(String onSwitch) {
		this.onSwitch = onSwitch;
	}
}
