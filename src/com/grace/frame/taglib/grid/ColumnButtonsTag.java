package com.grace.frame.taglib.grid;

import javax.servlet.jsp.JspException;

import com.grace.frame.exception.AppException;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;

/**
 * 按钮组
 * 
 * @author yjc
 */
public class ColumnButtonsTag extends Tag{
	private static final long serialVersionUID = 1L;
	private String name;// 唯一id
	private int width;// 宽度
	private String label;// 表格名称
	private String helpTip;// 帮助提示

	// 属性
	private DataSet dsBtns;

	public ColumnButtonsTag() {
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
		this.dsBtns = new DataSet();
		this.name = null;
		this.width = 0;
		this.label = null;
		this.helpTip = null;
	}

	/**
	 * 提供给子标签增加按钮使用
	 * 
	 * @author yjc
	 * @date 创建时间 2018-6-14
	 * @since V1.0
	 */
	public void addBtn(String key, String value, String onClick, String color) {
		DataMap dm = new DataMap();
		dm.put("key", key);
		dm.put("value", value);
		dm.put("onclick", onClick);
		dm.put("color", color);
		this.dsBtns.addRow(dm);
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
				this.name = "czbtn_" + (int) (Math.random() * 999);
			}
			if (StringUtil.chkStrNull(this.label)) {
				this.label = "操作";
			}

			// 创建数据
			DataMap tagDm = new DataMap();
			tagDm.put("name", this.name);
			tagDm.put("width", this.width);
			tagDm.put("label", this.label);
			tagDm.put("dsbtns", this.dsBtns);
			tagDm.put("hidden", false);// 因为此项为必传，所以传递false
			tagDm.put("helptip", this.helpTip);

			// 增加到queryGrid
			QueryGridTag queryGrid;
			if (this.getParent() instanceof QueryGridTag) {
				queryGrid = (QueryGridTag) this.getParent();
			} else {// 列组
				ColumnGroupTag groupTag = (ColumnGroupTag) this.getParent();
				queryGrid = (QueryGridTag) (groupTag).getParent();
				groupTag.setGroupColumn(this.name);
			}
			queryGrid.addColumn("columnbuttons", tagDm);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getHelpTip() {
		return helpTip;
	}

	public void setHelpTip(String helpTip) {
		this.helpTip = helpTip;
	}
}
