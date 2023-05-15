package com.grace.frame.taglib.grid;

import javax.servlet.jsp.JspException;

import com.grace.frame.exception.AppException;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.StringUtil;

/**
 * 列分组标签
 * 
 * @author yjc
 */
public class ColumnGroupTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 标题
	private String title;// 表格名称

	private String startName;// 开始名称
	private int num;// 合并列数
	private String helpTip;// 帮助提示

	public ColumnGroupTag() {
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
		this.title = null;
		this.startName = null;
		this.num = 0;
		this.helpTip = null;
	}

	/**
	 * 设置开始名称
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-27
	 * @since V1.0
	 */
	public void setGroupColumn(String columnName) {
		if (0 == this.num) {
			this.startName = columnName;
		}
		this.num = this.num + 1;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 数据的检测，首先判断父表签
			if (!(this.getParent() instanceof QueryGridTag)) {
				throw new AppException("该标签必须包含在querygird标签下");
			}
			// 数据判断
			if (StringUtil.chkStrNull(this.title)) {
				throw new AppException("传入的title为空");
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
			QueryGridTag queryGrid = (QueryGridTag) this.getParent();
			queryGrid.addColumnGroup(this.startName, this.num, this.title, this.helpTip);
			this.release();
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHelpTip() {
		return helpTip;
	}

	public void setHelpTip(String helpTip) {
		this.helpTip = helpTip;
	}
}
