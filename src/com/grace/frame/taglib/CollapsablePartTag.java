package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.util.StringUtil;

/**
 * form下的可折叠部分
 * 
 * @author yjc
 */
public class CollapsablePartTag extends Tag{
	private static final long serialVersionUID = 1L;
	private String key;// 进行手动折叠等操作时的检索关键字
	private String expandTitle;// 展开操作提示信息
	private String collapseTitle;// 折叠操作提示信息
	private String align;// 信息展示对齐方式
	private boolean collapsed;// 是否默认折叠-默认折叠

	public CollapsablePartTag() {
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
		this.key = null;
		this.expandTitle = "查看更多信息";
		this.collapseTitle = "收起更多信息";
		this.align = "center";
		this.collapsed = true;
	}

	/**
	 * 获取formTag
	 * 
	 * @author yjc
	 * @date 创建时间 2020-9-1
	 * @since V1.0
	 */
	private FormTag getFormTag() {
		if (this.getParent() instanceof FormTag) {
			FormTag formTag = (FormTag) this.getParent();
			return formTag;
		} else {
			return null;
		}
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			FormTag formTag = this.getFormTag();

			if (formTag != null) {
				// 执行换行
				String nextLine = formTag.nextLine();
				strBF.append(nextLine);

				// 进入part部分
				int partIndex = formTag.enterPart(this.collapsed);
				int colspan = formTag.getRowcount();
				if (formTag.getRowWidthPercent() < 100) {
					colspan = colspan + 1;
				}
				strBF.append("<tr><td colspan=\"")
					.append(colspan)
					.append("\" style=\"text-align:")
					.append(this.align)
					.append(";\"><a href='javascript:;' class='form_collapse_btn'_collapsed='")
					.append(this.collapsed)
					.append("' _expandTitle='")
					.append(this.expandTitle)
					.append("' _collapseTitle='")
					.append(this.collapseTitle)
					.append("'_index=\"")
					.append(partIndex)
					.append("\"")
					.append(StringUtil.chkStrNull(this.key) ? "" : " _key='"
							+ this.key + "'")
					.append(" title='")
					.append(this.collapsed ? this.expandTitle : this.collapseTitle)
					.append("'><span>")
					.append(this.collapsed ? this.expandTitle : this.collapseTitle)
					.append("</span><i class='form_title_tools")
					.append(this.collapsed ? " form_title_tools_expand" : "")
					.append("'></i></a></td></tr>");
				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(strBF.toString());
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
			FormTag formTag = this.getFormTag();
			if (formTag != null) {
				formTag.leavePart();// 离开折叠区域

				// 执行换行
				String nextLine = formTag.nextLine();
				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(nextLine);
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getExpandTitle() {
		return expandTitle;
	}

	public void setExpandTitle(String expandTitle) {
		this.expandTitle = expandTitle;
	}

	public String getCollapseTitle() {
		return collapseTitle;
	}

	public void setCollapseTitle(String collapseTitle) {
		this.collapseTitle = collapseTitle;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}
}
