package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.StringUtil;

/**
 * 引用区块
 * 
 * @author yjc
 */
public class BlockquoteTag extends Tag{
	private static final long serialVersionUID = 6003335166359914444L;
	private boolean gray;// 默认颜色为深绿色，如果gray为true的话，则为灰色（前面标记）
	private String color;// 颜色

	/**
	 * 构造函数
	 */
	public BlockquoteTag() {
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
		this.gray = false;
		this.color = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			strBF.append("<blockquote class=\"layui-elem-quote layui-text")
				.append(this.gray ? " layui-quote-nm" : "")
				.append("\"")
				.append(StringUtil.chkStrNull(this.color) ? "" : (" style=\"border-color:"
						+ this.color + ";\""))
				.append(">");

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
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write("</blockquote>");
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

	public boolean isGray() {
		return gray;
	}

	public void setGray(boolean gray) {
		this.gray = gray;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
