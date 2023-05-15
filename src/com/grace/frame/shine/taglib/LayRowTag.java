package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * 栅格系统标签
 * 
 * @author yjc
 */
public class LayRowTag extends Tag{
	private static final long serialVersionUID = 718896384673418271L;
	private boolean container;// 将栅格放入一个带有 class="layui-container"
	// 的特定的容器中，以便在小屏幕以上的设备中固定宽度，让列可控。默认true，否则：那么宽度将不会固定，而是
	// 100% 适应；如果嵌套则不会附加容器
	private int space;// 列间距：有这些取值1,3,5,8,10,12,15,18,20,22,28,30

	/**
	 * 构造函数
	 */
	public LayRowTag() {
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
		this.container = true;
		this.space = 0;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			if (!(this.getParent() instanceof LayCellTag)) {// 不是栅格嵌套
				strBF.append("<div class=\"layui-")
					.append(this.container ? "container" : "fluid")
					.append("\">");
			}
			strBF.append("<div class=\"layui-row")
				.append(this.space > 0 ? (" layui-col-space" + this.space) : "")
				.append("\">");

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
			if (!(this.getParent() instanceof LayCellTag)) {// 不是栅格嵌套
				strBF.append("</div>");
			}
			strBF.append("</div>");
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
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

	public boolean isContainer() {
		return container;
	}

	public void setContainer(boolean container) {
		this.container = container;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		int[] limitsValue = { 0, 1, 3, 5, 8, 10, 12, 15, 18, 20, 22, 28, 30 };
		this.space = 30;
		for (int i = 0, n = limitsValue.length; i < n; i++) {
			if (space <= limitsValue[i]) {
				this.space = limitsValue[i];
				return;
			} else if (space > limitsValue[i]) {
				continue;
			}
		}
	}
}
