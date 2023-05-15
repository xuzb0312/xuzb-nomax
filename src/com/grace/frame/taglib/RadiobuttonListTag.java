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

public class RadiobuttonListTag extends Tag{
	private static final long serialVersionUID = 1L;
	// 属性
	private String name;// 唯一id
	private int colspan;// 占的行数
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
	private int rowcount;// 每行放置的选项的个数
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private String helpTip;// 帮助提示

	// 自定义
	private String valueTemp;// 标签的值
	private DataSet dsChildOpts;// 子标签的数据项缓存标签

	// 构造函数
	public RadiobuttonListTag() {
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
		this.colspan = 6;// 默认占两格
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
		this.rowcount = 5;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
		this.helpTip = null;

		this.dsChildOpts = new DataSet();
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
			if (!(this.getParent() instanceof FormTag)) {// 判断，该标签必须包含在form下
				throw new AppException("该标签必须包含在form标签下");
			}

			// 对于标签的渲染放到了结尾，进行，在这期间可以通过data标签进行数据的设置，结尾收集并进行渲染
			this.dsChildOpts.clear();
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
			if (null == this.valueTemp) {
				this.valueTemp = "";
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

			if (this.rowcount <= 0) {
				throw new AppException("传入的rowcount参数不合法");
			}
			int colsWidth = 100 / this.rowcount;
			int lastcolsWidth = 100 - colsWidth * (this.rowcount - 1);
			// 复选框构造
			strBF.append("<table obj_type=\"radiobuttonlist\" width=\"100%\" id=\"")
				.append(this.name)
				.append("\" name=\"")
				.append(this.name)
				.append("\" ");
			strBF.append(" _required=\"").append(this.required).append("\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append(">");// 附加信息到标签上

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

			for (int i = 0, n = dsCode.size(); i < n; i++) {
				if ((i % this.rowcount) == 0) {
					strBF.append("<tr>");
				}
				String code = dsCode.getString(i, "code");
				String content = dsCode.getString(i, "content");
				StringBuffer optsBF = new StringBuffer();
				optsBF.append("<input type=\"radio\" id=\"rbl_opts_")
					.append(this.name)
					.append("_")
					.append(code)
					.append("\" ")
					.append(" name=\"rbl_opts_")
					.append(this.name)
					.append("\" value=\"")
					.append(code)
					.append("\"");
				if (this.readonly) {
					optsBF.append(" readonly=\"readonly\" disabled=\"disabled\" ");
				}
				if (this.valueTemp.equals(code)) {
					optsBF.append(" checked=\"checked\" ");
				}
				optsBF.append(" _content=\"").append(content).append("\" ");// 将content放到标签上，方便获取其展示的值
				optsBF.append(" class=\"magic-radio\"/>");
				optsBF.append("<label for=\"rbl_opts_")
					.append(this.name)
					.append("_")
					.append(code)
					.append("\">")
					.append(content)
					.append("</label>");
				if ((i % this.rowcount) == (this.rowcount - 1)) {
					// 最后一行
					strBF.append("<td width=\"")
						.append(lastcolsWidth)
						.append("%\">")
						.append(optsBF.toString())
						.append("</td></tr>");
				} else {
					strBF.append("<td width=\"")
						.append(colsWidth)
						.append("%\">")
						.append(optsBF.toString())
						.append("</td>");
				}
			}

			// 最后补充
			int lastAutoComCount = this.rowcount
					- (dsCode.size() % this.rowcount);
			if ((lastAutoComCount) != 0 && lastAutoComCount != this.rowcount) {
				for (int i = 0, n = lastAutoComCount; i < n; i++) {
					if (i == n - 1) {
						// 最后一行
						strBF.append("<td width=\"")
							.append(lastcolsWidth)
							.append("%\"></td></tr>");
					} else {
						strBF.append("<td width=\"")
							.append(colsWidth)
							.append("%\"></td>");
					}
				}
			}

			strBF.append("</table>");
			strBF.append(dmInput.getString("end"));
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

	public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
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
