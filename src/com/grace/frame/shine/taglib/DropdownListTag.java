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
 * 下拉框标签
 * 
 * @author yjc
 */
public class DropdownListTag extends Tag{
	private static final long serialVersionUID = -7222306923824480886L;
	private String name;// 唯一id
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private String value;// 值
	private boolean readonly;// 只读，默认为false;
	private boolean required;// 是否必须
	private String code;// 选项来源-使用code缓存数据加载选项；优先级最低
	private String dsCode;// 后台的dataset选项数据-格式为：code-content;优先级最高
	private String sqlCode;// 使用sql加载选项数据；列为：code-content;优先级居中
	private String prefix;// 只有当code时有效；表示选项条件，必须以此开头；
	private String suffix;// 只有当code时有效；表示选项条件，必须以此结尾；
	private String tips;// 提示信息项--为空时的提示信息项
	private boolean search;// 是否启用模糊检索
	private boolean hidden;// 是否隐藏

	// 事件
	private String onSelect;// 选择数据后才触发(value,elem,othis):选中的值，原始dom对象，美化后的dom对象
	private String onChange;// 值变更时，触发。（value）新值

	// 自定义
	private DataSet dsChildOpts;// 子标签的数据项缓存标签
	private String randomIndex;

	// 构造函数
	public DropdownListTag() {
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
		this.tips = "-请选择-";
		this.search = false;
		this.hidden = false;

		this.onSelect = null;// 单击
		this.onChange = null;

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
	 *附加分组
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2017-12-11
	 * @since V1.0
	 */
	public void setChildOptGroup(String label, DataSet dsOpt) throws AppException {
		if (StringUtil.chkStrNull(label)) {
			throw new AppException("传入的Label值为空");
		}
		if (null == dsOpt) {
			throw new AppException("传入的dsOpt为空");
		}

		// 增加项
		DataMap dmGroup = new DataMap();
		dmGroup.put("label", label);
		dmGroup.put("dsopt", dsOpt);
		this.dsChildOpts.addRow(dmGroup);
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
			if (StringUtil.chkStrNull(this.tips)) {
				this.tips = "-请选择-";
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
				DataMap dmTemp = this.dsChildOpts.get(i);
				dsCode.addRow(dmTemp);
			}

			// value值对应的文本内容
			String valueText = this.value;
			boolean isHaveValue = false;// 判断选项中是否存在值对应的内容

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
			strBF.append("<div style=\"display:")
				.append(this.readonly ? "none" : "block")
				.append(";\">");
			strBF.append("<select obj_type=\"dropdownlist\" id=\"")
				.append(this.name)
				.append("\" lay-filter=\"")
				.append(formTag.getName())
				.append("_")
				.append(this.name)
				.append("\"")
				.append(this.search ? " lay-search" : "")
				.append(" autocomplete=\"off\">");
			strBF.append("<option value=\"\"")
				.append(StringUtil.chkStrNull(this.value) ? " selected" : "")
				.append(">")
				.append(this.tips)
				.append("</option>");// 默认空的选项
			for (int i = 0, n = dsCode.size(); i < n; i++) {
				DataMap dmTemp = dsCode.getRow(i);
				if (dmTemp.containsKey("code")) {// 选项
					String codeTemp = dmTemp.getString("code");
					strBF.append("<option value=\"")
						.append(dmTemp.getString("code"))
						.append("\"");
					if (codeTemp.equals(this.value)) {
						strBF.append(" selected");
						valueText = dmTemp.getString("content");
						isHaveValue = true;
					}
					strBF.append(">")
						.append(dmTemp.getString("content"))
						.append("</option>");
				} else if (dmTemp.containsKey("label")) {// 选项组的情况
					String labelTemp = dmTemp.getString("label");
					strBF.append("<optgroup label=\"")
						.append(labelTemp)
						.append("\">");
					DataSet dsCodeGroup = dmTemp.getDataSet("dsopt");
					for (int j = 0, m = dsCodeGroup.size(); j < m; j++) {
						String codeGroup = dsCodeGroup.getString(j, "code");
						String contentGroup = dsCodeGroup.getString(j, "content");
						strBF.append("<option value=\"")
							.append(codeGroup)
							.append("\"");
						if (codeGroup.equals(this.value)) {
							strBF.append(" selected");
							valueText = contentGroup;
							isHaveValue = true;
						}
						strBF.append(">")
							.append(contentGroup)
							.append("</option>");
					}
					strBF.append("</optgroup>");
				}
			}
			// 当没有该选项，且不为空的时候，选项内容增加该选项
			if (!isHaveValue && !StringUtil.chkStrNull(this.value)) {
				strBF.append("<option value=\"")
					.append(this.value)
					.append("\" selected>")
					.append(this.value)
					.append("</option>");
			}
			strBF.append("</select>");
			strBF.append("</div>");
			// 对于只读的情况，重新放置一个只读的标签
			strBF.append("<input style=\"display:")
				.append(this.readonly ? "block" : "none")
				.append(";background:#FCFCFC;\" type=\"text\" value=\"")
				.append(StringUtil.chkStrNull(valueText) ? "" : valueText)
				.append("\" _value=\"")
				.append(StringUtil.chkStrNull(this.value) ? "" : this.value)
				.append("\" class=\"layui-input\" readonly=\"readonly\">");
			// --end
			strBF.append("</div>");
			if (!"formlinegroup".equals(itemPos)) {
				strBF.append("</div>");
			}

			// --事件
			strBF.append("<script type=\"text/javascript\">");
			strBF.append("function ")
				.append(formTag.getName())
				.append("_ddl_opts_" + this.name + "Func" + this.randomIndex
						+ "(value,elem){");
			strBF.append("var obj = getObject(\"").append(formTag.getName()
					+ "." + this.name).append("\");");
			if (!StringUtil.chkStrNull(this.onSelect)) {
				strBF.append(this.onSelect + "(value,elem);");
			}
			if (!StringUtil.chkStrNull(this.onChange)) {
				strBF.append("if(obj.isValueChange()){")// 值是否发生变化
					.append(this.onChange + "(value,elem)")
					.append("}");
			}
			strBF.append("obj.synValue();");// 同步值--几个标签元素的值同步
			strBF.append("obj.chkValue(true);");// 检查值是否合法
			strBF.append("}");
			strBF.append("</script>");

			// 注册事件
			formTag.addEvent("select", formTag.getName() + "_" + this.name, formTag.getName()
					+ "_ddl_opts_" + this.name + "Func" + this.randomIndex);

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			// 向前台发送数据
			DataMap dmInit = new DataMap();
			dmInit.put("required", this.required);
			dmInit.put("readonly", this.readonly);
			dmInit.put("dsopt", dsCode);
			dmInit.put("tips", this.tips);
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

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(String onSelect) {
		this.onSelect = onSelect;
	}

	public boolean isSearch() {
		return search;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}

	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}