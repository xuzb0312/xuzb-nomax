package com.grace.frame.taglib;

import java.io.IOException;
import java.util.Date;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import net.sf.json.JSONArray;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 多行纯文本输入框
 * 
 * @author yjc
 */
public class TextareaTag extends Tag{
	private static final long serialVersionUID = 1L;
	// 属性
	private String name;// 唯一id
	private int colspan;// 占的行数
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private String value;// 值
	private boolean readonly;// 只读，默认为false;
	private boolean required;// 是否必须
	private int height;// 高度-px
	private String note;// 典型批注编号
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
		this.colspan = 6;// 默认占两格
		this.label = null;
		this.labelColor = null;
		this.value = null;
		this.readonly = false;
		this.required = false;// 默认非必须
		this.height = 100;
		this.note = null;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
		this.helpTip = null;

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
			if (StringUtil.chkStrNull(this.name)) {
				throw new AppException("name属性不允许为空");
			}
			if (!(this.getParent() instanceof FormTag)) {// 判断，该标签必须包含在form下
				throw new AppException("该标签必须包含在form标签下");
			}
			FormTag form = (FormTag) this.getParent();
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
			if (!StringUtil.chkStrNull(valueTemp)) {
				valueTemp = StringUtil.htmlEncode(valueTemp);
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
				if (!StringUtil.chkStrNull(this.note)) {
					strBF.append("<a style=\"color:blue;\" href=\"javascript:void(0);\" title=\"典型批注，双击输入框选择批注，点击?可进行自定义维护！\" onclick=\"TextareaUtil.modifyNoteConfig('"
							+ this.note + "');\">(?)</a>&nbsp;");
				}
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
			strBF.append("<textarea obj_type=\"textarea\" class=\"textareatag\" id=\"")
				.append(this.name)
				.append("\" name=\"")
				.append(this.name)
				.append("\" ");
			// 处理典型批注
			if (!StringUtil.chkStrNull(this.note)) {
				Sql sql = new Sql();
				sql.setSql(" select pznr, xh from fw.note_config where dbid = ? and pzbh = ? ");
				sql.setString(1, GlobalVars.SYS_DBID);
				sql.setString(2, this.note);
				DataSet dsNote = sql.executeQuery();
				dsNote.sort("xh");
				JSONArray jsonArr = JSONArray.fromObject(dsNote);
				String noteJson = jsonArr.toString();
				noteJson = noteJson.replace("\"", "'");
				strBF.append(" data-opt=\"").append(noteJson).append("\" ");
			} else {
				strBF.append(" data-opt=\"\" ");
			}
			if (this.readonly) {
				strBF.append(" readonly=\"readonly\" ");
			}
			strBF.append(" style=\"");
			if (this.readonly) {
				strBF.append("background:#F9F9F9;");
			} else {
				strBF.append("background:#FFFFFF;");// 屏蔽不必要的波浪线
			}
			strBF.append("height:")
				.append(this.height)
				.append("px;width:100%;\" ");
			strBF.append(" _required=\"").append(this.required).append("\" ");// 附加信息到标签上

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
			strBF.append(">").append(valueTemp).append("</textarea>");
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

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
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
