package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.StringUtil;

/**
 * 分割线标签
 * 
 * @author yjc
 */
public class HrTag extends Tag{
	private static final long serialVersionUID = -8627035113618461637L;
	private String color;// 线条颜色--允许的值：赤色red,橙色orange,墨绿green,青色cyan,蓝色blue,黑色black,灰色gray--默认gray

	/**
	 * 构造函数
	 */
	public HrTag() {
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
		this.color = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 颜色检查
			this.chkLayuiBgColorLimits(this.color);

			StringBuffer strBF = new StringBuffer();
			if (StringUtil.chkStrNull(this.color)) {
				strBF.append("<hr/>");
			} else {
				strBF.append("<hr class=\"layui-bg-")
					.append(this.color)
					.append("\"/>");
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
			this.release();// 资源释放
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
