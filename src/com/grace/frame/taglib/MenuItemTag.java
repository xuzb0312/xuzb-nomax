package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;

/**
 * 元素项目
 * 
 * @author yjc
 */
public class MenuItemTag extends Tag{

	private static final long serialVersionUID = 1L;
	// 属性
	private String iconCls;// 图标；
	private String value;// 展示的文本
	private String funcitonid;// 功能id,用于权限的控制
	// 事件
	private String onclick;// 单击事件--html的原生时间，对于此种事件我们均以小写定义

	public MenuItemTag() {
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
		this.iconCls = null;
		this.value = null;
		this.onclick = null;
		this.funcitonid = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.value)) {
				throw new AppException("value值不允许为空");
			}
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
			// 如果用户没有权限，该菜单项则不予展示
			if (this.checkFunctionRight(this.funcitonid)) {
				StringBuffer strBF = new StringBuffer();
				strBF.append("<div ");

				// 设置easyui的属性
				this.clearEasyUICompAttr();
				if (!StringUtil.chkStrNull(this.iconCls)) {
					this.setEasyUICompAttr("iconCls", this.iconCls, true);
				}

				String dataOpt = this.getEasyUICompAttrOptions();
				if (!StringUtil.chkStrNull(dataOpt)) {
					strBF.append(" data-options=\"")
						.append(dataOpt)
						.append("\" ");
				}
				if (!StringUtil.chkStrNull(this.onclick)) {
					strBF.append(" onclick=\"")
						.append(this.onclick)
						.append("\" ");
				}
				strBF.append(">").append(this.value).append("</div>");

				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(strBF.toString());
			}
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

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getFuncitonid() {
		return funcitonid;
	}

	public void setFuncitonid(String funcitonid) {
		this.funcitonid = funcitonid;
	}

}
