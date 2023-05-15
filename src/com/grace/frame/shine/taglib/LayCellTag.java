package com.grace.frame.shine.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.StringUtil;

/**
 * 栅格元素
 * 
 * @author yjc
 */
public class LayCellTag extends Tag{
	private static final long serialVersionUID = -4174642372527998723L;
	// xs（超小屏幕，如手机）、sm（小屏幕，如平板）、md（桌面中等屏幕）、lg（桌面大型屏幕）(取值1-12)
	private int width_xs;
	private int width_sm;
	private int width_md;
	private int width_lg;

	private int offset_xs;
	private int offset_sm;
	private int offset_md;
	private int offset_lg;

	private String display_xs;
	private String display_sm;
	private String display_md;
	private String display_lg;

	private String functionid;// 功能id
	// 其他
	private boolean hasRight;// 有权限

	/**
	 * 构造函数
	 */
	public LayCellTag() {
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
		this.width_xs = 0;
		this.width_sm = 0;
		this.width_md = 0;
		this.width_lg = 0;

		this.offset_xs = 0;
		this.offset_sm = 0;
		this.offset_md = 0;
		this.offset_lg = 0;

		this.display_xs = null;
		this.display_sm = null;
		this.display_md = null;
		this.display_lg = null;

		this.functionid = null;
		this.hasRight = false;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			this.hasRight = this.checkFunctionRight(this.functionid);
			if (this.hasRight) {// 有权限
				StringBuffer strBF = new StringBuffer();
				strBF.append("<div class=\"")
					.append(this.width_xs > 0 ? (" layui-col-xs" + this.width_xs) : "")
					.append(this.width_sm > 0 ? (" layui-col-sm" + this.width_sm) : "")
					.append(this.width_md > 0 ? (" layui-col-md" + this.width_md) : "")
					.append(this.width_lg > 0 ? (" layui-col-lg" + this.width_lg) : "")
					.append(this.offset_xs > 0 ? (" layui-col-xs-offset" + this.offset_xs) : "")
					.append(this.offset_sm > 0 ? (" layui-col-sm-offset" + this.offset_sm) : "")
					.append(this.offset_md > 0 ? (" layui-col-md-offset" + this.offset_md) : "")
					.append(this.offset_lg > 0 ? (" layui-col-lg-offset" + this.offset_lg) : "");
				if ("block".equals(this.display_xs)
						|| "inline".equals(this.display_xs)
						|| "inline-block".equals(this.display_xs)) {
					strBF.append(" layui-show-xs-").append(this.display_xs);
				} else if ("none".equals(this.display_xs)) {
					strBF.append(" layui-hide-xs");
				}
				if ("block".equals(this.display_sm)
						|| "inline".equals(this.display_sm)
						|| "inline-block".equals(this.display_sm)) {
					strBF.append(" layui-show-sm-").append(this.display_sm);
				} else if ("none".equals(this.display_sm)) {
					strBF.append(" layui-hide-sm");
				}
				if ("block".equals(this.display_md)
						|| "inline".equals(this.display_md)
						|| "inline-block".equals(this.display_md)) {
					strBF.append(" layui-show-md-").append(this.display_md);
				} else if ("none".equals(this.display_md)) {
					strBF.append(" layui-hide-md");
				}
				if ("block".equals(this.display_lg)
						|| "inline".equals(this.display_lg)
						|| "inline-block".equals(this.display_lg)) {
					strBF.append(" layui-show-lg-").append(this.display_lg);
				} else if ("none".equals(this.display_lg)) {
					strBF.append(" layui-hide-lg");
				}
				strBF.append("\">");
				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(strBF.toString());
			} else {
				return SKIP_BODY;
			}
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
			if (this.hasRight) {
				JspWriter out = this.pageContext.getOut();
				out.write("</div>");
			}
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		} finally {
			this.release();
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

	public int getWidth_xs() {
		return width_xs;
	}

	public void setWidth_xs(int widthXs) {
		width_xs = widthXs;
	}

	public int getWidth_sm() {
		return width_sm;
	}

	public void setWidth_sm(int widthSm) {
		width_sm = widthSm;
	}

	public int getWidth_md() {
		return width_md;
	}

	public void setWidth_md(int widthMd) {
		width_md = widthMd;
	}

	public int getWidth_lg() {
		return width_lg;
	}

	public void setWidth_lg(int widthLg) {
		width_lg = widthLg;
	}

	public int getOffset_xs() {
		return offset_xs;
	}

	public void setOffset_xs(int offsetXs) {
		offset_xs = offsetXs;
	}

	public int getOffset_sm() {
		return offset_sm;
	}

	public void setOffset_sm(int offsetSm) {
		offset_sm = offsetSm;
	}

	public int getOffset_md() {
		return offset_md;
	}

	public void setOffset_md(int offsetMd) {
		offset_md = offsetMd;
	}

	public int getOffset_lg() {
		return offset_lg;
	}

	public void setOffset_lg(int offsetLg) {
		offset_lg = offsetLg;
	}

	public String getDisplay_xs() {
		return display_xs;
	}

	public void setDisplay_xs(String displayXs) {
		display_xs = this.dealDisplayAttr(displayXs);
	}

	public String getDisplay_sm() {
		return display_sm;
	}

	public void setDisplay_sm(String displaySm) {
		display_sm = this.dealDisplayAttr(displaySm);
	}

	public String getDisplay_md() {
		return display_md;
	}

	public void setDisplay_md(String displayMd) {
		display_md = this.dealDisplayAttr(displayMd);
	}

	public String getDisplay_lg() {
		return display_lg;
	}

	public void setDisplay_lg(String displayLg) {
		display_lg = this.dealDisplayAttr(displayLg);
	}

	private String dealDisplayAttr(String display) {
		if (StringUtil.chkStrNull(display)) {
			return null;
		}
		display = display.toLowerCase();
		if ("block".equals(display) || "inline".equals(display)
				|| "inline-block".equals(display)) {
			return display;
		}
		if ("none".equals(display) || "hide".equals(display)
				|| "hidden".equals(display)) {
			return "none";
		}
		return null;
	}

	public String getFunctionid() {
		return functionid;
	}

	public void setFunctionid(String functionid) {
		this.functionid = functionid;
	}
}
