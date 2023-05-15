package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

public class PrinterTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;
	private boolean hidden;// 是否隐藏
	private int height;// 高度
	private boolean watermark;// 水印
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排

	public PrinterTag() {
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
		this.hidden = false;
		this.height = 400;
		this.watermark = false;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				throw new AppException("传入的name为空");
			}

			// 从request中获取内部数据
			String contentStr = (String) this.pageContext.getRequest()
				.getAttribute(name);
			if (StringUtil.chkStrNull(contentStr)) {
				contentStr = "";
			} else {
				contentStr = contentStr.replace("\\", "\\\\")
					.replace("\"", "\\\"")
					.replace("\r", "\\r")
					.replace("\n", "\\n")
					.replace("\t", "\\t");// 特殊字符的处理
			}

			// 字符串组装
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div obj_type=\"printer\" _watermark=\"")
				.append(this.watermark)
				.append("\" id=\"")
				.append(this.name)
				.append("\" style=\"width:100%;display:"
						+ (this.hidden ? "none" : "block") + "\">");
			strBF.append("<div id=\"")
				.append(this.name)
				.append("_con\" style=\"");
			if ("v2".equalsIgnoreCase(GlobalVars.VIEW_TYPE)) {
				strBF.append("border:1px solid #eee;border-bottom:1px solid #3398DE;margin-bottom:7px;box-shadow: 0px 0px 3px 0px #eee;");
			} else {
				strBF.append("border:1px solid #95B8E7;");
			}
			strBF.append("height:").append(this.height).append("px;\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append("></div><div style=\"clear:both;\"></div></div>");

			this.appendln(strBF, "");// 换行增加js
			this.appendln(strBF, "<script type=\"text/javascript\">");
			this.appendln(strBF, "$(\"#" + this.name
					+ "\").data(\"printdata\",\"" + contentStr + "\");");
			this.appendln(strBF, "getObject(\"" + this.name + "\").init();");
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

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isWatermark() {
		return watermark;
	}

	public void setWatermark(boolean watermark) {
		this.watermark = watermark;
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
}
