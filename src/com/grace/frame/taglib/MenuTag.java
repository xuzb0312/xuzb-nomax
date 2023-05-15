package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 菜单按钮
 * 
 * @author yjc
 */
public class MenuTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一标识
	private String width;// 宽度，默认130px;

	public MenuTag() {
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
		this.width = "130";
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			String nameTemp = this.name;
			// 如果此标签的父表签为button,则需要向Button按钮上报唯一id-name;以便其自动关联菜单
			if ((this.getParent() instanceof ButtonTag)) {
				ButtonTag parentTag = (ButtonTag) this.getParent();
				String type = parentTag.getType();
				if ("menu".equalsIgnoreCase(type)
						|| "split".equalsIgnoreCase(type)) {
					if (StringUtil.chkStrNull(nameTemp)) {
						nameTemp = StringUtil.getUUID();
					}
					parentTag.setMenu(nameTemp);
				}
			}

			// name不允许为空
			if (StringUtil.chkStrNull(nameTemp)) {
				throw new AppException("name不允许为空[除非此项包含在button(type:menu,split)标签下]");
			}

			StringBuffer strBF = new StringBuffer();
			strBF.append("<div id=\"")
				.append(nameTemp)
				.append("\" class=\"easyui-menu\" obj_type=\"menu\" style=\"width:")
				.append(this.width)
				.append("px;\">");

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
			out.write("</div>");
			this.release();
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

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
