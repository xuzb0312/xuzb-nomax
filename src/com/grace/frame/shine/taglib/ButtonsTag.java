package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

/**
 * 按钮组(在不同的嵌套下，展示的样式不一样。)
 * 
 * @author yjc
 */
public class ButtonsTag extends Tag{
	private static final long serialVersionUID = -570463378492456537L;
	private boolean closebutton;// 是否默认包含关闭按钮，默认值是

	/**
	 * 构造函数
	 */
	public ButtonsTag() {
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
		this.closebutton = true;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			if (this.getParent() instanceof FormTag) {// 如果父表签为form的话调整布局，增加form元素内容
				strBF.append("<div class=\"layui-form-item\">");
				FormTag form = (FormTag) this.getParent();
				if (!form.isPane()) {
					strBF.append("<div class=\"layui-input-block\">");
				}
			} else {
				strBF.append("<div class=\"layui-btn-group\">");
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
			// 输出关闭按钮
			if (this.closebutton) {
				ButtonTag btnTag = new ButtonTag();
				btnTag.setPageContext(this.pageContext);
				btnTag.setParent(this);
				btnTag.setValue("关闭");
				btnTag.setIcon("&#x1006;");
				btnTag.setType("primary");
				btnTag.setTip("关闭页面");
				btnTag.setOnclick("closeWindow();");

				btnTag.doStartTag();
				btnTag.doInitBody();
				btnTag.doAfterBody();
				btnTag.doEndTag();
			}

			StringBuffer strBF = new StringBuffer();
			if (this.getParent() instanceof FormTag) {
				FormTag form = (FormTag) this.getParent();
				if (!form.isPane()) {
					strBF.append("</div>");
				}
			}
			strBF.append("</div>");
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
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

	public boolean isClosebutton() {
		return closebutton;
	}

	public void setClosebutton(boolean closebutton) {
		this.closebutton = closebutton;
	}
}
