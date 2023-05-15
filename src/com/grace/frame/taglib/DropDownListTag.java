package com.grace.frame.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.CodeUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 单选下拉框标签---该标签没有使用easyui，使用的是传统模式构建的单选下拉框
 * 
 * @author yjc
 */
public class DropDownListTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private int colspan;// 占的行数
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private String value;// 值
	private boolean readonly;// 只读，默认为false;
	private boolean required;// 是否必须
	private boolean loadOpt;// 是否加载具体的选项--只有当readonly在true的情况下，才会有效
	private String code;// 选项来源-使用code缓存数据加载选项；优先级最低
	private String dsCode;// 后台的dataset选项数据-格式为：code-content;优先级最高
	private String sqlCode;// 使用sql加载选项数据；列为：code-content;优先级居中
	private String prefix;// 只有当code时有效；表示选项条件，必须以此开头；
	private String suffix;// 只有当code时有效；表示选项条件，必须以此结尾；
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private String helpTip;// 帮助提示

	// 事件
	private String onclick;// 单击
	private String onchange;// 数据变化
	private String ondblclick;// 双击
	private String onblur;// 失去焦点
	private String onfocus;// 获得焦点时

	// 自定义
	private String valueTemp;// 标签的值
	private String wrapEndStr;// form布局字符串，结束内容
	private boolean valueOut;// value是否超出了选项值
	private String valueText;// value展示的对应的文本

	// 构造函数
	public DropDownListTag() {
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
		this.colspan = 2;// 默认占两格
		this.label = null;
		this.labelColor = null;
		this.value = null;
		this.readonly = false;
		this.required = false;// 默认非必须
		this.loadOpt = true;
		this.code = null;
		this.dsCode = null;
		this.sqlCode = null;
		this.prefix = null;
		this.suffix = null;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
		this.helpTip = null;

		this.onclick = null;// 单击
		this.onchange = null;// 数据变化
		this.ondblclick = null;// 双击
		this.onblur = null;// 失去焦点
		this.onfocus = null;// 获得焦点时

		this.valueOut = true;// 默认超出范围
		this.valueText = null;
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
			int colspanTemp = this.colspan;

			// 获取value值
			this.valueTemp = this.value;
			if (StringUtil.chkStrNull(this.valueTemp)) {
				Object valueObj = form.getDataSourceData(this.name);
				if (valueObj == null) {
					this.valueTemp = "";
				} else if (valueObj instanceof Date) {
					this.valueTemp = DateUtil.dateToString((Date) valueObj);
				} else {
					this.valueTemp = String.valueOf(valueObj);
				}
			}
			// 设置的值为空，则没有超出
			if (StringUtil.chkStrNull(this.valueTemp)) {
				this.valueOut = false;
			}
			// valuetext默认为value
			this.valueText = this.valueTemp;

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
			this.wrapEndStr = dmInput.getString("end");
			strBF.append(dmInput.getString("start"));

			strBF.append("<select obj_type=\"dropdownlist\" id=\"")
				.append(this.name)
				.append("\" name=\"")
				.append(this.name)
				.append("\" ");
			strBF.append(" _required=\"").append(this.required).append("\" ");// 附加信息到标签上
			strBF.append(" _loadOpt=\"").append(this.loadOpt).append("\" ");
			if (this.readonly) {
				strBF.append(" readonly=\"readonly\" ");
			}
			strBF.append(" class=\"dropdownlist\" style=\"");
			if (this.readonly) {
				strBF.append("background:#F9F9F9;");
			} else {
				strBF.append("background:#FFFFFF;");// 屏蔽不必要的波浪线
			}
			if (this.readonly) {
				strBF.append("display:none;");
			}
			strBF.append("height:25px;width:100%;\" ");// 调整基本属性，在同样高的情况下，视觉上要矮一些，增加1px来均衡

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
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append(">");

			strBF.append("<option value=\"\" ")
				.append(StringUtil.chkStrNull(this.valueTemp) ? "selected=\"selected\"" : "")
				.append(">" + (this.readonly ? "" : "--请选择--") + "</option>");

			// 选项处理--按优先级来
			DataSet dsCode = new DataSet();
			if (!StringUtil.chkStrNull(this.dsCode)) {
				dsCode = (DataSet) this.pageContext.getRequest()
					.getAttribute(this.dsCode);
				if (null == dsCode) {
					throw new BizException("dsCode设置项错误：" + this.dsCode
							+ "数据为空【原因：数据返回时没有put该dsCode值】");
				}
			} else if (!StringUtil.chkStrNull(this.sqlCode)) {
				Sql sql = new Sql();
				sql.setSql(this.sqlCode);
				dsCode = sql.executeQuery();
			} else if (!StringUtil.chkStrNull(this.code)) {
				dsCode = CodeUtil.getDsByDmbh(this.code, this.prefix, this.suffix);
			}
			if (null != dsCode) {
				for (int i = 0, n = dsCode.size(); i < n; i++) {
					String value = dsCode.getString(i, "code");
					String content = dsCode.getString(i, "content");
					if (null == value) {
						value = "";
					}
					// 在code中找到，则没有超出范围
					if (value.equals(this.valueTemp)) {
						this.valueOut = false;
					}
					// 获取text展示文本
					if (value.equals(this.valueTemp)) {
						this.valueText = content;
					}

					if (this.readonly && !this.loadOpt) {// 如果只读，且不加载选项，则只加载value值选项
						if (value.equals(this.valueTemp)) {
							strBF.append("<option value=\"")
								.append(value)
								.append("\" selected=\"selected\">")
								.append(content)
								.append("</option>");
						}
					} else {
						strBF.append("<option value=\"")
							.append(value)
							.append("\" ")
							.append(value.equals(this.valueTemp) ? "selected=\"selected\"" : "")
							.append(">")
							.append(content)
							.append("</option>");
					}
				}
			}

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
			StringBuffer strBF = new StringBuffer();
			if (this.valueOut) {// value超出范围-返回设置的值展示
				strBF.append("<option value=\"")
					.append(this.valueTemp)
					.append("\" selected=\"selected\">")
					.append(this.valueTemp)
					.append("</option>");
			}
			strBF.append("</select>");
			// 只读展示的字段
			strBF.append("<input id=\"")
				.append(this.name)
				.append("_readonly\" name=\"")
				.append(this.name)
				.append("_readonly\" class=\"easyui-validatebox\" value=\"")
				.append(this.valueText)
				.append("\" readonly=\"readonly\" style=\"background:#F9F9F9;");
			if (!this.readonly) {
				strBF.append("display:none;");
			}
			strBF.append("text-align:left;height:25px;width:100%;\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append("/>");
			// 结尾
			strBF.append(this.wrapEndStr);

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
			this.release();// 资源释放
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		}
		return EVAL_PAGE;
	}

	/**
	 * 提供给子标签来使用
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-6
	 * @since V1.0
	 */
	public String getRealValue() {
		return this.valueTemp;// 获取value的真实值
	}

	/**
	 * 提供给data标签使用-设置valueout
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-27
	 * @since V1.0
	 */
	public void setValueOut(boolean valueout) {
		this.valueOut = valueout;
	}

	/**
	 * 提供给data标签使用-设置valueText
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-27
	 * @since V1.0
	 */
	public void setValueText(String text) {
		this.valueText = text;
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

	public boolean isLoadOpt() {
		return loadOpt;
	}

	public void setLoadOpt(boolean loadOpt) {
		this.loadOpt = loadOpt;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDsCode() {
		return dsCode;
	}

	public void setDsCode(String dsCode) {
		this.dsCode = dsCode;
	}

	public String getSqlCode() {
		return sqlCode;
	}

	public void setSqlCode(String sqlCode) {
		this.sqlCode = sqlCode;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
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
