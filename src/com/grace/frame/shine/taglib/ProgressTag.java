package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;

/**
 * 进度条对象
 * 
 * @author yjc
 */
public class ProgressTag extends Tag{
	private static final long serialVersionUID = -2963954587728022366L;
	private String name;
	private boolean big;// 大进度条
	private String color;// 运行范围的颜色
	private boolean showPercent;// 展示进度
	private int percent;// 进度值（总长度100）

	/**
	 * 构造函数
	 */
	public ProgressTag() {
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
		this.name = null;
		this.color = null;
		this.big = false;
		this.showPercent = true;
		this.percent = 0;
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
			StringBuffer strBF = new StringBuffer();
			strBF.append("<div class=\"layui-progress")
				.append(this.big ? " layui-progress-big" : "")
				.append("\" obj_type=\"progress\" id=\"")
				.append(this.name)
				.append("\" lay-filter=\"")
				.append(this.name)
				.append("\"")
				.append(this.showPercent ? " lay-showPercent=\"true\"" : "")
				.append(">");
			strBF.append("<div class=\"layui-progress-bar")
				.append(StringUtil.chkStrNull(this.color) ? "" : (" layui-bg-" + this.color))
				.append("\" lay-percent=\"" + this.percent + "%\"></div>");
			strBF.append("</div>");
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			// 标签初始化
			DataMap dmInit = new DataMap();
			dmInit.put("percent", this.percent);
			this.objInit(dmInit, this.name);
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

	public void setColor(String color) throws AppException {
		this.chkLayuiBgColorLimits(color);// 检查颜色值
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isBig() {
		return big;
	}

	public void setBig(boolean big) {
		this.big = big;
	}

	public boolean isShowPercent() {
		return showPercent;
	}

	public void setShowPercent(boolean showPercent) {
		this.showPercent = showPercent;
	}

	public int getPercent() {
		return percent;
	}

	public void setPercent(int percent) {
		if (percent < 0) {
			percent = 0;
		}
		if (percent > 100) {
			percent = 100;
		}
		this.percent = percent;
	}
}
