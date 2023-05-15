package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysUser;
import com.grace.frame.util.TagSupportUtil;

/**
 * 子页面需要加载的内容，所有的om标签最好包含在此标签中，便于标签的控制和扩展。以及一些页面常用属性的扩充
 * 
 * @since2019年9月27日-对于后台用户页面一致性判断，调整幅度较大-yjc
 * @author yjc
 */
public class BodyTag extends BodyTagSupport{
	private static final long serialVersionUID = 1L;
	private String padding;// 页边距，默认5px;
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排
	private boolean nullToolTip;// 页面内的必录输入框为空时，是否进行toolTip提示

	public BodyTag() {
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
		this.padding = "5";// 页边距默认5px;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
		this.nullToolTip = false;// 默认不提示
	}

	/**
	 * 标签开始时
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();

			// 对于经办机构id,经办机构权限范围等页面的字段提供容器放置数据
			strBF.append("<div style=\"display: none;\">");
			// 根据用户信息，获取默认的jbjgid和jbjgqxfw;首先从request中获取
			String jbjgid = (String) this.pageContext.getRequest()
				.getAttribute("__jbjgid");
			String jbjgqxfw = (String) this.pageContext.getRequest()
				.getAttribute("__jbjgqxfw");
			String yhid = (String) this.pageContext.getRequest()
				.getAttribute("__yhid");

			// 获取用户信息
			SysUser currentSysUser = (SysUser) this.pageContext.getSession()
				.getAttribute("currentsysuser");// 当前用户
			if (null == currentSysUser) {
				currentSysUser = new SysUser();
				currentSysUser.setAllInfoDM(new DataMap());
			}
			if (StringUtil.chkStrNull(jbjgid) || "00000000".equals(jbjgid)) {
				jbjgid = currentSysUser.getAllInfoDM()
					.getString("ssjbjgid", "");
				if (null == jbjgid) {
					jbjgid = "";
				}
			}

			if (StringUtil.chkStrNull(jbjgqxfw) || "00000000".equals(jbjgqxfw)) {
				jbjgqxfw = currentSysUser.getAllInfoDM()
					.getString("yhjbjgqxfw", "");
				if (null == jbjgqxfw) {
					jbjgqxfw = "";
				}
			}

			// 用户ID为空，获取用户信息的用户ID
			if (StringUtil.chkStrNull(yhid)) {
				yhid = currentSysUser.getYhid();
				if (null == yhid) {
					yhid = "";
				}
			}

			// jsp路径
			String jsppath = (String) this.pageContext.getRequest()
				.getAttribute("__jsppath");// 请求页面的当前jsp路径
			if (!StringUtil.chkStrNull(jsppath)) {
				strBF.append("<input id=\"__jsppath\" value=\"" + jsppath
						+ "\" />");// jsppath
			}

			strBF.append("<input id=\"__jbjgid\" value=\"" + jbjgid + "\" />");// jbjgid
			strBF.append("<input id=\"__jbjgqxfw\" value=\"" + jbjgqxfw
					+ "\" />");// 权限范围
			strBF.append("<input id=\"__yhid\" value=\"" + yhid + "\" />");// 用户ID
			strBF.append("</div>");

			strBF.append("<div id=\"sys_main_body_con_div\" class=\"easyui-panel\" data-options=\"fit:true,border:false\" style=\"padding:"
					+ this.padding + "px;\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append(">");
			// 数据的输出
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
		} catch (IOException e) {
			TagSupportUtil.dealException(e, "Body标签解析时，将html输出到jsp时，出现IO异常："
					+ e.getMessage(), this.pageContext);
		} catch (Exception e) {
			TagSupportUtil.dealException(e, this.pageContext);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 标签结束时，执行
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			// 数据的输出
			JspWriter out = this.pageContext.getOut();
			out.write("</div>");
			if (!this.nullToolTip) {
				out.write("<script type=\"text/javascript\">if($.fn.validatebox){$.fn.validatebox.defaults.missingMessage=null;}</script>");
			}
			this.release();// 资源释放
		} catch (IOException e) {
			TagSupportUtil.dealException(e, "Body标签解析时，将html输出到jsp时，出现IO异常："
					+ e.getMessage(), this.pageContext);
		} catch (Exception e) {
			TagSupportUtil.dealException(e, this.pageContext);
		}
		return EVAL_PAGE;
	}

	public String getPadding() {
		return padding;
	}

	public void setPadding(String padding) {
		this.padding = padding;
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

	public boolean isNullToolTip() {
		return nullToolTip;
	}

	public void setNullToolTip(boolean nullToolTip) {
		this.nullToolTip = nullToolTip;
	}

	/**
	 * 资源释放
	 */
	@Override
	public void release() {
		this.initTag();
		super.release();
	}
}
