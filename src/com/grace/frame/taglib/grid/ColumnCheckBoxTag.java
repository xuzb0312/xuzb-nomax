package com.grace.frame.taglib.grid;

import javax.servlet.jsp.JspException;

import com.grace.frame.exception.AppException;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;

/**
 * 列选择框
 * 
 * @author yjc
 */
public class ColumnCheckBoxTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private String label;// 表格名称
	private int width;// 宽度-以文字的个数为准
	private boolean hidden;// 是否隐藏
	private boolean readonly;// 是否只读列，grid可编辑时，该列不允许编辑--默认可编辑
	private String helpTip;// 帮助提示

	public ColumnCheckBoxTag() {
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
		this.label = null;
		this.width = 0;
		this.hidden = false;
		this.readonly = false;
		this.helpTip = null;
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			// 数据的检测，首先判断父表签
			if (!(this.getParent() instanceof QueryGridTag)
					&& !(this.getParent() instanceof ColumnGroupTag)) {
				throw new AppException("该标签必须包含在querygird标签下");
			}
			// 数据判断
			if (StringUtil.chkStrNull(this.name)) {
				throw new AppException("传入的name为空");
			}
			if (StringUtil.chkStrNull(this.label)) {
				throw new AppException("传入的label为空");
			}

			// 创建数据
			DataMap tagDm = new DataMap();
			tagDm.put("name", this.name);
			tagDm.put("label", this.label);
			tagDm.put("width", this.width);
			tagDm.put("hidden", this.hidden);
			tagDm.put("readonly", this.readonly);
			tagDm.put("helptip", this.helpTip);

			QueryGridTag queryGrid;
			if (this.getParent() instanceof QueryGridTag) {
				queryGrid = (QueryGridTag) this.getParent();
			} else {// 列组
				ColumnGroupTag groupTag = (ColumnGroupTag) this.getParent();
				queryGrid = (QueryGridTag) (groupTag).getParent();
				groupTag.setGroupColumn(this.name);
			}
			queryGrid.addColumn("columncheckbox", tagDm);

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
		this.release();
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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	
	public String getHelpTip() {
		return helpTip;
	}

	public void setHelpTip(String helpTip) {
		this.helpTip = helpTip;
	}
}
