package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.StringUtil;

/**
 * 代码块
 * 
 * @author yjc
 */
public class CodeTag extends Tag{
	private static final long serialVersionUID = 7519626436703863772L;
	private String title;
	private int height;
	private boolean encode;
	private String skin;// 默认和notepad风格
	private String name;// 唯一标识

	public CodeTag() {
		this.initTag();
	}

	/**
	 * 参数初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-16
	 * @since V1.0
	 */
	private void initTag() {
		this.title = "代码";
		this.height = 100;
		this.encode = false;
		this.skin = null;
		this.name = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				this.name = StringUtil.getUUID();
			}
			if (StringUtil.chkStrNull(this.title)) {
				this.title = "代码";
			}
			// 开始组装数据
			StringBuffer strBF = new StringBuffer();
			strBF.append("<pre id=\"")
				.append(this.name)
				.append("\" class=\"layui-code\">");
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
			// 开始组装数据
			StringBuffer strBF = new StringBuffer();
			strBF.append("</pre>");
			strBF.append("<script type=\"text/javascript\">");
			strBF.append("layui.use(\"code\", function(){");
			strBF.append("layui.code({");
			strBF.append("elem:\"#" + this.name + "\",");
			strBF.append("title:\"" + this.title + "\",");
			strBF.append("height:\"" + this.height + "px\",");
			strBF.append("encode:" + this.encode + ",");
			if (!StringUtil.chkStrNull(this.skin)) {
				strBF.append("skin:\"" + this.skin + "\",");
			}
			strBF.append("about:false");
			strBF.append("});");
			strBF.append("});");
			strBF.append("</script>");
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isEncode() {
		return encode;
	}

	public void setEncode(boolean encode) {
		this.encode = encode;
	}

	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		if (!"notepad".equalsIgnoreCase(skin)) {
			this.skin = null;
		} else {
			this.skin = skin.toLowerCase();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
