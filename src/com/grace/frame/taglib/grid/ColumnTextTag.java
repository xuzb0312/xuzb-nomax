package com.grace.frame.taglib.grid;

import javax.servlet.jsp.JspException;

import com.grace.frame.exception.AppException;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;

/**
 * 列文本内容
 * 
 * @author yjc
 */
public class ColumnTextTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;// 唯一id
	private String label;// 表格名称
	private String align;// 文本方向-默认：文本左对齐，日期居中；数字右对齐
	private int width;// 宽度-以文字的个数为准
	private boolean hidden;// 是否隐藏
	private String mask;// 标签的掩码值
	private String dataType;// 标签文本框中输入值的类别
	private String sourceMask;// 当时间类型是String时，source的mask。
	private boolean sum;// 是否合计
	private boolean readonly;// 是否只读列，grid可编辑时，该列不允许编辑--默认可编辑
	private String helpTip;// 帮助提示

	private String onSearchClick;// 当配置了此事件-则会存在searchbtn--在可编辑模式下

	public ColumnTextTag() {
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
		this.align = null;
		this.width = 0;
		this.hidden = false;
		this.mask = null;
		this.dataType = "string";
		this.sourceMask = null;
		this.sum = false;
		this.readonly = false;
		this.helpTip = null;

		this.onSearchClick = "";// 事件
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
			if (StringUtil.chkStrNull(this.align)) {
				if ("string".equalsIgnoreCase(this.dataType)) {
					this.align = "left";
				} else if ("date".equalsIgnoreCase(this.dataType)) {
					this.align = "center";
				} else if ("number".equalsIgnoreCase(this.dataType)) {
					this.align = "right";
				} else {
					throw new AppException("传入的dataType格式不正确");
				}
			}
			if (StringUtil.chkStrNull(this.onSearchClick)) {
				this.onSearchClick = "";
			}

			// 创建数据
			DataMap tagDm = new DataMap();
			tagDm.put("name", this.name);
			tagDm.put("label", this.label);
			tagDm.put("align", this.align);
			tagDm.put("width", this.width);
			tagDm.put("hidden", this.hidden);
			tagDm.put("mask", this.mask);
			tagDm.put("datatype", this.dataType);
			tagDm.put("sourcemask", this.sourceMask);
			tagDm.put("sum", this.sum);
			tagDm.put("readonly", this.readonly);
			tagDm.put("onsearchclick", this.onSearchClick);
			tagDm.put("helptip", this.helpTip);

			QueryGridTag queryGrid;
			if (this.getParent() instanceof QueryGridTag) {
				queryGrid = (QueryGridTag) this.getParent();
			} else {// 列组
				ColumnGroupTag groupTag = (ColumnGroupTag) this.getParent();
				queryGrid = (QueryGridTag) (groupTag).getParent();
				groupTag.setGroupColumn(this.name);
			}
			queryGrid.addColumn("columntext", tagDm);

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAlign() {
		return align;
	}

	public void setAlign(String align) {
		this.align = align;
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

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSourceMask() {
		return sourceMask;
	}

	public void setSourceMask(String sourceMask) {
		this.sourceMask = sourceMask;
	}

	public boolean isSum() {
		return sum;
	}

	public void setSum(boolean sum) {
		this.sum = sum;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getOnSearchClick() {
		return onSearchClick;
	}

	public void setOnSearchClick(String onSearchClick) {
		this.onSearchClick = onSearchClick;
	}

	public String getHelpTip() {
		return helpTip;
	}

	public void setHelpTip(String helpTip) {
		this.helpTip = helpTip;
	}
}
