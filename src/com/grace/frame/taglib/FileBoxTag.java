package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

public class FileBoxTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private int colspan;// 占的行数
	private String label;// 标签名称
	private String labelColor;// 标签颜色
	private boolean required;// 是否必须
	private boolean autoUpload;// 自动上传
	private String fileType;// 文件类型
	private String fileDesc;// 文件描述
	private int sizeLimit;// 文件大小
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private String helpTip;// 帮助提示

	// 事件
	private String onSelectFile;// 文件选择后触发的事件，在选择文件关闭窗口后触发(参数，fileName)

	private void initTag() {
		this.name = null;
		this.colspan = 4;// 默认为4
		this.label = null;
		this.labelColor = null;
		this.required = false;
		this.autoUpload = true;
		this.fileType = "*.*";// 所有文件均可
		this.fileDesc = "所有文件";
		this.sizeLimit = 0;// 默认无限制
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
		this.helpTip = null;

		this.onSelectFile = "";
	}

	public FileBoxTag() {
		this.initTag();
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
				this.colspan = this.colspan - 1;
			}
			DataMap dmInput = form.wrapStr4Children(this.colspan);
			strBF.append(dmInput.getString("start"));
			strBF.append("<table width=\"100%\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append("><tr><td>");
			this.clearEasyUICompAttr();
			if (this.required) {
				this.setEasyUICompAttr("required", "true", false);
			}

			// 获取唯一id
			String uiid = "uiid_" + StringUtil.getUUID();

			strBF.append("<input obj_type=\"filebox\" id=\"")
				.append(this.name)
				.append("\" class=\"easyui-validatebox\" readonly=\"readonly\" _sizeLimit=\"")
				.append(this.sizeLimit)
				.append("\" _fileDesc=\"")
				.append(this.fileDesc)
				.append("\" _fileType=\"")
				.append(this.fileType)
				.append("\" _uiid=\"")
				.append(uiid)
				.append("\" _autoUpload=\"")
				.append(this.autoUpload)
				.append("\" _required=\"")
				.append(this.required)
				.append("\" _onselectfile=\"")
				.append(this.onSelectFile)
				.append("\" data-options=\"")
				.append(this.getEasyUICompAttrOptions())
				.append("\" ");
			strBF.append(" style=\"");
			strBF.append("background:#F9F9F9;");
			strBF.append("height:25px;width:100%;\" />");

			strBF.append("</td><td style=\"text-align:right;width:105px;\">");
			strBF.append("<a id=\"")
				.append(this.name)
				.append("_select\" href=\"javascript:void(0);\" class=\"easyui-splitbutton\" data-options=\"plain:true,iconCls:'icon-folder',menu:'#")
				.append(this.name)
				.append("_clear'\">选择文件</a>");
			strBF.append("</td></tr></table>");
			strBF.append("<div id=\"")
				.append(this.name)
				.append("_clear\" style=\"width:88px;\"><div id=\"")
				.append(this.name)
				.append("_flash_select_btn\" data-options=\"iconCls:'icon-folder-page'\">选择[FS]</div><div id=\"")
				.append(this.name)
				.append("_tra_select_btn\" data-options=\"iconCls:'icon-folder-page'\">选择[兼容]</div><div id=\"")
				.append(this.name)
				.append("_clear_btn\" data-options=\"iconCls:'icon-undo'\">清空</div></div>");

			this.appendln(strBF, "");
			this.appendln(strBF, "<script type=\"text/javascript\">");
			this.appendln(strBF, "$(function(){getObject(\"" + this.name
					+ "\").init();});");
			this.appendln(strBF, "</script>");
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

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isAutoUpload() {
		return autoUpload;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileDesc() {
		return fileDesc;
	}

	public void setFileDesc(String fileDesc) {
		this.fileDesc = fileDesc;
	}

	public int getSizeLimit() {
		return sizeLimit;
	}

	public void setSizeLimit(int sizeLimit) {
		this.sizeLimit = sizeLimit;
	}

	public String getOnSelectFile() {
		return onSelectFile;
	}

	public void setOnSelectFile(String onSelectFile) {
		this.onSelectFile = onSelectFile;
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
