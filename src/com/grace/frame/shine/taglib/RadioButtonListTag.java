package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.CodeUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.Sql;
import com.grace.frame.util.StringUtil;

/**
 * 单选列表
 * 
 * @author yjc
 */
public class RadioButtonListTag extends Tag{
	private static final long serialVersionUID = -6363219094612253990L;
	// 属性
	private String name;// 唯一id
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private String value;// 默认值
	private boolean readonly;// 只读，默认为false;
	private boolean required;// 是否必须
	private String code;// 选项来源-使用code缓存数据加载选项；优先级最低
	private String dsCode;// 后台的dataset选项数据-格式为：code-content;优先级最高
	private String sqlCode;// 使用sql加载选项数据；列为：code-content;优先级居中
	private String prefix;// 只有当code时有效；表示选项条件，必须以此开头；
	private String suffix;// 只有当code时有效；表示选项条件，必须以此结尾；

	// 事件
	private String onCheck;// 选择，(value,elem)

	// 自定义
	private DataSet dsChildOpts;// 子标签的数据项缓存标签
	private String randomIndex;

	/**
	 * 构造函数
	 */
	public RadioButtonListTag() {
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
		this.required = false;// 默认非必须
		this.code = null;
		this.dsCode = null;
		this.sqlCode = null;
		this.prefix = null;
		this.suffix = null;

		this.onCheck = null;
		this.dsChildOpts = new DataSet();
		this.randomIndex = null;
	}

	/**
	 * 提供给子标签进行数据的设置
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-7
	 * @since V1.0
	 */
	public void setChildOpt(String key, String value) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("key值不能为空");
		}
		if (StringUtil.chkStrNull(value)) {
			throw new AppException("value为空");
		}

		this.dsChildOpts.addRow();
		this.dsChildOpts.put(this.dsChildOpts.size() - 1, "code", key);
		this.dsChildOpts.put(this.dsChildOpts.size() - 1, "content", value);
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
			// 对于标签的渲染放到了结尾，进行，在这期间可以通过data标签进行数据的设置，结尾收集并进行渲染
			this.dsChildOpts.clear();

			// 产生一个随机数
			java.util.Random random = new java.util.Random();
			this.randomIndex = String.valueOf(random.nextInt(10));
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

			// code的解析
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
			if (null == dsCode) {
				dsCode = new DataSet();
			}
			// 形成json数据
			for (int i = 0, n = this.dsChildOpts.size(); i < n; i++) {
				dsCode.addRow();
				dsCode.put(dsCode.size() - 1, "code", this.dsChildOpts.getString(i, "code"));
				dsCode.put(dsCode.size() - 1, "content", this.dsChildOpts.getString(i, "content"));
			}

			// 组装
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
				if (this.required) {
					strBF.append("<span style=\"color:red;\">*</span>");
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
				if (this.required) {
					strBF.append("<span style=\"color:red;\">*</span>");
				}
				strBF.append("</label>");
				strBF.append("<div class=\"layui-input-block\"").append(">");
			}
			// 具体内容的渲染--begin
			strBF.append("<div obj_type=\"radiobuttonlist\" id=\"" + this.name
					+ "\">");
			this.renderOptions(strBF, dsCode, formTag);
			strBF.append("</div>");
			// --end
			strBF.append("</div>");
			if (!"formlinegroup".equals(itemPos)) {
				strBF.append("</div>");
			}

			// --事件
			strBF.append("<script type=\"text/javascript\">");
			strBF.append("function ")
				.append(formTag.getName())
				.append("_rbl_opts_" + this.name + "Func" + this.randomIndex
						+ "(value,elem){");
			strBF.append("getObject(\"").append(formTag.getName() + "."
					+ this.name).append("\").chkValue(true);");
			if (!StringUtil.chkStrNull(this.onCheck)) {
				strBF.append(this.onCheck + "(value,elem);");
			}
			strBF.append("}");
			strBF.append("</script>");

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			// 向前台发送数据
			DataMap dmInit = new DataMap();
			dmInit.put("required", this.required);
			dmInit.put("readonly", this.readonly);
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
	 * 构建选项内容
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2017-12-7
	 * @since V1.0
	 */
	private void renderOptions(StringBuffer strBF, DataSet dsCode,
			FormTag formTag) throws AppException {
		// 检查是否存在匹配值
		if (!StringUtil.chkStrNull(this.value)) {
			boolean isExitsValueItem = false;
			for (int i = 0, n = dsCode.size(); i < n; i++) {
				String code = dsCode.getString(i, "code");
				if (this.value.equals(code)) {
					isExitsValueItem = true;
					break;
				}
			}
			if (!isExitsValueItem) {
				dsCode.addRow();
				dsCode.put(dsCode.size() - 1, "code", this.value);
				dsCode.put(dsCode.size() - 1, "content", this.value);
			}
		}

		// 输出渲染
		for (int i = 0, n = dsCode.size(); i < n; i++) {
			String code = dsCode.getString(i, "code");
			String content = dsCode.getString(i, "content");
			if (StringUtil.chkStrNull(code)) {
				code = "";
			}
			strBF.append("<input type=\"radio\" id=\"rbl_opts_")
				.append(this.name)
				.append("_")
				.append(code)
				.append("\" lay-filter=\"")
				.append(formTag.getName())
				.append("_rbl_opts_")
				.append(this.name)
				.append("_")
				.append(code)
				.append("\" name=\"rbl_opts_")
				.append(this.name)
				.append("\" value=\"")
				.append(code)
				.append("\" title=\"")
				.append(content)
				.append("\"");
			if (this.readonly) {
				strBF.append(" disabled=\"disabled\"");
			}
			if (code.equals(this.value)) {
				strBF.append(" checked=\"checked\"");
			}
			strBF.append("/>");
			// 附加事件
			formTag.addEvent("radio", formTag.getName() + "_rbl_opts_"
					+ this.name + "_" + code, formTag.getName() + "_rbl_opts_"
					+ this.name + "Func" + this.randomIndex);
		}
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

	public String getOnCheck() {
		return onCheck;
	}

	public void setOnCheck(String onCheck) {
		this.onCheck = onCheck;
	}
}
