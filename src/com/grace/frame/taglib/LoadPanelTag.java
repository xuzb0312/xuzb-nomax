package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

public class LoadPanelTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;

	public LoadPanelTag() {
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
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				throw new AppException("传入的Name为空");
			}
			StringBuffer strBF = new StringBuffer();
			strBF.append("<iframe obj_type=\"loadpanel\" id=\"")
				.append(this.name)
				.append("\" scrolling=\"auto\" frameborder=\"0\" style=\"width: 100%; height: 99%;\" src=\"\"></iframe>");
			if (GlobalVars.DEBUG_MODE) {
				strBF.append("<div id=\"")
					.append(this.name)
					.append("_debug_msg_url\" style=\"position:absolute;bottom:1px;right:5px;width:20px;height:20px;cursor:pointer;display:none;\" title=\"请求路径\"><span class=\"l-btn-icon icon-script-link\">&nbsp;</span></div>");
				strBF.append("<div id=\"")
					.append(this.name)
					.append("_debug_msg_data\" style=\"position:absolute;bottom:1px;right:25px;width:20px;height:20px;cursor:pointer;display:none;\" title=\"请求数据\"><span class=\"l-btn-icon icon-script-code-red\">&nbsp;</span></div>");
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
}
