package com.grace.frame.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 富文本编辑器 <br>
 * <p>
 * 前台的控件报错：“请求后台配置项http错误，上传功能将不能正常使用！”-不用管他，因为我们会屏蔽此上传功能。保证服务器安全。
 * </p>
 * 
 * @author yjc
 */
public class UEditorTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private int colspan;// 占的行数
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private String value;// 值
	private boolean readonly;// 只读，默认为false;
	private boolean required;// 是否必须
	private int height;// 高度
	private String toolbars;// 工具栏，默认为空，所有
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private String helpTip;// 帮助提示

	public UEditorTag() {
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
		this.colspan = 0;// 跟form的一样多
		this.label = null;
		this.labelColor = null;
		this.value = null;
		this.readonly = false;
		this.required = false;
		this.height = 350;// 默认500
		this.toolbars = null;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
		this.helpTip = null;
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
			if (!(this.getParent() instanceof FormTag)) {// 判断，该标签必须包含在form下
				throw new AppException("该标签必须包含在form标签下");
			}
			FormTag form = (FormTag) this.getParent();
			if (0 == this.colspan) {
				this.colspan = form.getRowcount();
			}
			int colspanTemp = this.colspan;
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
			String toobarsTmp = "";
			if (!StringUtil.chkStrNull(this.toolbars)) {
				toobarsTmp = ",toolbars:[[" + this.toolbars + "]]";
			}

			// 开始组装数据
			StringBuffer strBF = new StringBuffer();

			// 标签不为空的，输出标签，占1格
			if (!StringUtil.chkStrNull(this.label)) {
				// 首先判断是否为自动换行
				if (form.isBreak(this.colspan)) {
					strBF.append(form.nextLine());
				}
				DataMap dmLabel = form.wrapStr4Children(1, "right");
				strBF.append(dmLabel.getString("start"));
				strBF.append("<span id=\"")
					.append(this.name)
					.append("_label\" ");
				if (!StringUtil.chkStrNull(this.labelColor)) {
					strBF.append("style=\"color:")
						.append(this.labelColor)
						.append(";\"");
				}
				strBF.append(">");
				strBF.append(this.label);
				strBF.append("</span>");
				if (this.required) {
					strBF.append("<span style=\"color:red;\">*</span>");
				}
				if (!StringUtil.chkStrNull(this.helpTip)) {
					strBF.append("<i _tipmsg=\""
							+ this.helpTip
							+ "\" class=\"input-tip-msg-icon\"><svg viewBox=\"64 64 896 896\" class=\"\" data-icon=\"info-circle\" width=\"1em\" height=\"1em\" fill=\"currentColor\" aria-hidden=\"true\" focusable=\"false\"><path d=\"M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z\"></path><path d=\"M464 336a48 48 0 1 0 96 0 48 48 0 1 0-96 0zm72 112h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V456c0-4.4-3.6-8-8-8z\"></path></svg></i>");
				}
				strBF.append("&nbsp;");
				strBF.append(dmLabel.getString("end"));
				colspanTemp = colspanTemp - 1;
			}
			DataMap dmInput = form.wrapStr4Children(colspanTemp);
			strBF.append(dmInput.getString("start"));
			this.appendln(strBF, "");// 换一行
			this.appendln(strBF, "<div obj_type=\"ueditor\" id=\"" + this.name
					+ "\" _required=\"" + this.required + "\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append("><script id=\"" + this.name + "_container\" name=\""
					+ this.name + "_content\" type=\"text/plain\">");
			this.appendln(strBF, valueTemp);
			this.appendln(strBF, "</script>");
			this.appendln(strBF, "<script type=\"text/javascript\">");
			this.appendln(strBF, "$(function(){");
			this.appendln(strBF, "var __ue_con_map = $(\"body\").data(\"__ue_con_map\");");// 将ue对象绑定到div上
			this.appendln(strBF, "if(chkObjNull(__ue_con_map)){");
			this.appendln(strBF, "__ue_con_map = new HashMap();");
			this.appendln(strBF, "}");// 将ue对象绑定到div上
			this.appendln(strBF, "if(__ue_con_map.containsKey(\"" + this.name
					+ "\")){");
			this.appendln(strBF, "UE.getEditor(\"" + this.name
					+ "_container\").destroy();");
			this.appendln(strBF, "}");
			this.appendln(strBF, "__ue_con_map.put(\"" + this.name
					+ "\", null)");
			this.appendln(strBF, "$(\"body\").data(\"__ue_con_map\", __ue_con_map);");// 将ue对象绑定到div上
			this.appendln(strBF, "var " + this.name
					+ "_ue_obj = UE.getEditor(\"" + this.name
					+ "_container\",{initialFrameHeight:" + this.height
					+ ",readonly:" + this.readonly + toobarsTmp + "});");
			this.appendln(strBF, "$(\"#" + this.name
					+ "\").data(\"container\"," + this.name + "_ue_obj);");// 将ue对象绑定到div上
			this.appendln(strBF, "});");

			// 增加word转存功能--add.yjc.2017年9月6日
			if (StringUtil.chkStrNull(this.toolbars)
					|| this.toolbars.toLowerCase().contains("word")) {// toolbars字符串只读的，所以这样写不会改变原值
				this.appendln(strBF, "UE.registerUI(\"word\", function() {");
				this.appendln(strBF, "	var btn = new UE.ui.Button( {");
				this.appendln(strBF, "		name : \"word\",");
				this.appendln(strBF, "		title : \"Word转存\",");
				this.appendln(strBF, "		cssRules : \"background-position:-660px -40px\",");
				this.appendln(strBF, "		onclick : function() {");
				this.appendln(strBF, "			getObject(\"" + this.name
						+ "\").wordParse();");
				this.appendln(strBF, "		}");
				this.appendln(strBF, "	});");
				this.appendln(strBF, "	return btn;");
				this.appendln(strBF, "}, 100, \"" + this.name
						+ "_container\");");
			}
			this.appendln(strBF, "</script>");
			this.appendln(strBF, "</div>");
			strBF.append(dmInput.getString("end"));

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

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
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

	public String getToolbars() {
		return toolbars;
	}

	public void setToolbars(String toolbars) {
		this.toolbars = toolbars;
	}

	public int getIntroSetp() {
		return introSetp;
	}

	public void setIntroSetp(int introSetp) {
		this.introSetp = introSetp;
	}

	public String getIntroContent() {
		return introContent;
	}

	public void setIntroContent(String introContent) {
		this.introContent = introContent;
	}

	public String getIntroPosition() {
		return introPosition;
	}

	public void setIntroPosition(String introPosition) {
		this.introPosition = introPosition;
	}

	public String getHelpTip() {
		return helpTip;
	}

	public void setHelpTip(String helpTip) {
		this.helpTip = helpTip;
	}
}
