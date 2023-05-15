package com.grace.frame.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 文本输入框-好麻烦的一个控件，其中很多东西均在前台js中完成，注意！！！
 * 
 * @author yjc
 */
public class TextInputTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private int colspan;// 占的行数
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private String value;// 值
	private boolean readonly;// 只读，默认为false;
	private String dataType;// 数据类型，string,number,date三种-默认为string;
	private boolean required;// 是否必须
	private String align;// 文本对齐方式，默认string-left,date-center,number-right;
	private String prompt;// 提示文本
	private String validType;// 验证文本--具体验证方式包含多种，参照extensions.validatebox的验证类型;外加原生的email,url,length[1,2],remote
	private String mask;// 文本的格式
	private String sourceMask;// 原数据格式
	private boolean password;// 是不是密码
	private String customType;// 订制化类型，默认为null,取值当前有：email(在readonly模式下，后边展示一个发送邮件的图标，可以进行邮件的发送。)
	private String iconCls;// 当存在onsearchclick时，图标
	private String searchBtnTips;// 搜索按钮的提示信息
	private boolean searchBtnDisabled;// 搜索按钮是否可点击
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private int searchBtnIntroSetp;// （搜索按钮）引导简介顺序号，从1开始，连续
	private String searchBtnIntroContent;// （搜索按钮）引导提示内容。
	private String searchBtnIntroPosition;// （搜索按钮）引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private String helpTip;// 帮助提示

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
		this.colspan = 2;// 默认占两格
		this.label = null;
		this.labelColor = null;
		this.value = null;
		this.readonly = false;
		this.dataType = "string";// 数据类型默认为string
		this.required = false;// 默认非必须
		this.prompt = null;
		this.validType = null;
		this.mask = null;
		this.sourceMask = null;
		this.password = false;
		this.customType = null;
		this.searchBtnTips = "搜索";
		this.iconCls = "";// 搜索按钮样式
		this.searchBtnDisabled = false;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
		this.searchBtnIntroSetp = 0;
		this.searchBtnIntroContent = null;
		this.searchBtnIntroPosition = null;
		this.helpTip = null;

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
			if (!(this.getParent() instanceof FormTag)) {// 判断，该标签必须包含在form下
				throw new AppException("该标签必须包含在form标签下");
			}

			// 在渲染标签前先检查订制化信息，然后根据定制化特点，重置属性内容。
			if (!StringUtil.chkStrNull(this.customType)) {
				this.dealCustomTypeInfo();
			}

			FormTag form = (FormTag) this.getParent();
			String formName = form.getName();
			int colspanTemp = this.colspan;
			String alignTemp = this.align;
			if (StringUtil.chkStrNull(alignTemp)) {
				if ("string".equalsIgnoreCase(this.dataType)) {
					alignTemp = "left";
				} else if ("date".equalsIgnoreCase(this.dataType)) {
					alignTemp = "center";
				} else if ("number".equalsIgnoreCase(this.dataType)) {
					alignTemp = "right";
				} else {
					throw new AppException("datatype属性不合法");
				}
			}

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

			// 判断是否为行最后的元素
			boolean isRowLastTag = false;
			if (form.isBreak(colspanTemp + 1)) {// 如果栅格+1需要换行展示，则表名为最后一个展示元素
				isRowLastTag = true;
			}

			DataMap dmInput = form.wrapStr4Children(colspanTemp);
			strBF.append(dmInput.getString("start"));

			if (!StringUtil.chkStrNull(this.onsearchclick)) {
				strBF.append("<table width=\"100%\"><tr><td>");
			}
			strBF.append("<input obj_type=\"textinput\" dataType=\"")
				.append(this.dataType.toLowerCase())
				.append("\" id=\"")
				.append(this.name)
				.append("\" name=\"")
				.append(this.name)
				.append("\" class=\"easyui-validatebox\" ");
			if (!StringUtil.chkStrNull(valueTemp)) {
				valueTemp = StringUtil.htmlEncode(valueTemp);
				strBF.append(" value=\"").append(valueTemp).append("\"");
			}
			if (this.readonly) {
				strBF.append(" readonly=\"readonly\" ");
			}
			if (this.password) {
				strBF.append(" type=\"password\" ");
			}
			if (!StringUtil.chkStrNull(this.mask)) {
				strBF.append(" mask=\"").append(this.mask).append("\" ");
			}
			if (!StringUtil.chkStrNull(this.sourceMask)) {
				strBF.append(" sourceMask=\"")
					.append(this.sourceMask)
					.append("\" ");
			}
			strBF.append(" style=\"");
			if (this.readonly) {
				strBF.append("background:#F9F9F9;");
			} else {
				strBF.append("background:#FFFFFF;");// 屏蔽不必要的波浪线
			}
			strBF.append("text-align:").append(alignTemp).append(";");
			strBF.append("height:25px;width:100%;\" ");

			this.clearEasyUICompAttr();
			if (this.required) {
				this.setEasyUICompAttr("required", "true", false);
			}
			// 判断是否为form的最后一个，如果是最后一个则提示信息，展示在标签底部
			if (isRowLastTag) {
				this.setEasyUICompAttr("tipPosition", "bottom", true);
			}
			if (!StringUtil.chkStrNull(this.prompt)) {
				this.setEasyUICompAttr("prompt", this.prompt, true);
			}
			if (!StringUtil.chkStrNull(this.validType)) {
				this.setEasyUICompAttr("validType", this.validType, true);
			}
			String dataOpt = this.getEasyUICompAttrOptions();
			if (!StringUtil.chkStrNull(dataOpt)) {
				strBF.append(" data-options=\"").append(dataOpt).append("\" ");
			}

			// 输入框的事件
			if (!StringUtil.chkStrNull(this.onclick)) {
				strBF.append(" onclick=\"").append(this.onclick).append("\" ");
			}
			if (!StringUtil.chkStrNull(this.onchange)) {
				strBF.append(" onchange=\"")
					.append(this.onchange)
					.append("\" ");
			}
			if (!StringUtil.chkStrNull(this.ondblclick)) {
				strBF.append(" ondblclick=\"")
					.append(this.ondblclick)
					.append("\" ");
			}
			if (!StringUtil.chkStrNull(this.onfocus)) {
				strBF.append(" onfocus=\"").append(this.onfocus).append("\" ");
			}
			if (!StringUtil.chkStrNull(this.onblur)) {
				strBF.append(" onblur=\"").append(this.onblur).append("\" ");
			}
			if (!StringUtil.chkStrNull(this.onKeyDown)) {
				strBF.append(" onkeydown=\"return ")
					.append(this.onKeyDown)
					.append("(event);\" ");
			}
			strBF.append(" autocomplete=\"off\" ");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append("/>");
			if (!StringUtil.chkStrNull(this.onsearchclick)) {
				this.appendln(strBF, "");
				// 增加Enter事件
				this.appendln(strBF, "<script type=\"text/javascript\">");
				this.appendln(strBF, "$(function(){");
				this.appendln(strBF, "$(\"#" + formName + " #" + this.name
						+ "\").bind(\"keydown\",function(e){if(e.which==13){"
						+ this.onsearchclick + "}});");// mod.yjc.2018年8月14日-限定区域
				this.appendln(strBF, "});");
				this.appendln(strBF, "</script>");

				strBF.append("</td><td width=\"19px\"><span id=\"searchBtn_"
						+ this.name
						+ "\" class=\"searchbox-button searchbox-button-hover ")
					.append(this.iconCls)
					.append("\" style=\"margin-left:8px;border:1px solid #95B8E7;");
				if ("v2".equalsIgnoreCase(GlobalVars.VIEW_TYPE)) {
					strBF.append("width:25px;height:25px;");
				} else {
					strBF.append("width:19px;");
				}
				if (this.searchBtnDisabled) {
					strBF.append("display:none;");
				} else {
					strBF.append("display:block;");
				}
				strBF.append("\" onclick=\"")
					.append(this.onsearchclick)
					.append("\" title=\"")
					.append(this.searchBtnTips)
					.append("\"");
				TagSupportUtil.appendIntroAttr(strBF, this.searchBtnIntroSetp, this.searchBtnIntroContent, this.searchBtnIntroPosition);
				strBF.append("></span><span id=\"searchBtn_" + this.name
						+ "_disabled\" class=\"searchbox-button ")
					.append(this.iconCls)
					.append("\" style=\"margin-left:8px;border:1px solid #95B8E7;");
				if ("v2".equalsIgnoreCase(GlobalVars.VIEW_TYPE)) {
					strBF.append("width:25px;height:25px;");
				} else {
					strBF.append("width:19px;");
				}
				if (this.searchBtnDisabled) {
					strBF.append("display:block;");
				} else {
					strBF.append("display:none;");
				}
				strBF.append("\"");
				TagSupportUtil.appendIntroAttr(strBF, this.searchBtnIntroSetp, this.searchBtnIntroContent, this.searchBtnIntroPosition);
				strBF.append("></span></td></tr></table>");
			}

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

	/**
	 * 处理订制化类型信息
	 * 
	 * @author yjc
	 * @date 创建时间 2016-4-2
	 * @since V1.0
	 */
	private void dealCustomTypeInfo() {
		if ("email".equals(this.customType)) {
			// 电子邮件定制化
			if (this.readonly) {// 只对只读类型进行设置
				this.onsearchclick = "getObject('" + this.name
						+ "').sendEmail();";
				this.iconCls = "icon-email-edit";
				this.searchBtnTips = "发送邮件";
			}
		}
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

	public String getPrompt() {
		return prompt;
	}

	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	public String getValidType() {
		return validType;
	}

	public void setValidType(String validType) {
		this.validType = validType;
	}

	public String getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(String labelColor) {
		this.labelColor = labelColor;
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

	public boolean isPassword() {
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public String getOnKeyDown() {
		return onKeyDown;
	}

	public void setOnKeyDown(String onKeyDown) {
		this.onKeyDown = onKeyDown;
	}

	public String getCustomType() {
		return customType;
	}

	public void setCustomType(String customType) {
		this.customType = customType;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getSearchBtnTips() {
		return searchBtnTips;
	}

	public void setSearchBtnTips(String searchBtnTips) {
		this.searchBtnTips = searchBtnTips;
	}

	public boolean isSearchBtnDisabled() {
		return searchBtnDisabled;
	}

	public void setSearchBtnDisabled(boolean searchBtnDisabled) {
		this.searchBtnDisabled = searchBtnDisabled;
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

	public int getSearchBtnIntroSetp() {
		return searchBtnIntroSetp;
	}

	public void setSearchBtnIntroSetp(int searchBtnIntroSetp) {
		this.searchBtnIntroSetp = searchBtnIntroSetp;
	}

	public String getSearchBtnIntroContent() {
		return searchBtnIntroContent;
	}

	public void setSearchBtnIntroContent(String searchBtnIntroContent) {
		this.searchBtnIntroContent = searchBtnIntroContent;
	}

	public String getSearchBtnIntroPosition() {
		return searchBtnIntroPosition;
	}

	public void setSearchBtnIntroPosition(String searchBtnIntroPosition) {
		this.searchBtnIntroPosition = searchBtnIntroPosition;
	}

	public String getHelpTip() {
		return helpTip;
	}

	public void setHelpTip(String helpTip) {
		this.helpTip = helpTip;
	}
}
