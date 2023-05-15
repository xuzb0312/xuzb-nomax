package com.grace.frame.taglib.grid;

import java.util.HashMap;

import javax.servlet.jsp.JspException;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.taglib.Tag;
import com.grace.frame.util.CodeUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.Sql;
import com.grace.frame.util.StringUtil;

/**
 * grid的列表复选框标签
 * 
 * @author yjc
 */
public class ColumnMultiDropDownTag extends Tag{
	private static final long serialVersionUID = 1L;
	// 属性
	private String name;// 唯一id
	private String label;// 标签名称
	private String align;// 文本方向-默认：文本左对齐，日期居中；数字右对齐
	private int width;// 宽度-以文字的个数为准
	private boolean hidden;// 是否隐藏
	private String code;// 选项来源-使用code缓存数据加载选项；优先级最低
	private String dsCode;// 后台的dataset选项数据-格式为：code-content;优先级最高
	private String sqlCode;// 使用sql加载选项数据；列为：code-content;优先级居中
	private String prefix;// 只有当code时有效；表示选项条件，必须以此开头；
	private String suffix;// 只有当code时有效；表示选项条件，必须以此结尾；
	private boolean readonly;// 只读，默认为false;
	private String tagColors;// 标签颜色
	private String helpTip;// 帮助提示

	// 事件--自定义事件
	private String onClick;// 单击
	private String onChange;// 数据变化
	private String onDblClick;// 双击
	private String onBlur;// 失去焦点
	private String onFocus;// 获得焦点时

	// 自定义
	private DataSet dsChildOpts;// 子标签的数据项缓存标签

	// 构造函数
	public ColumnMultiDropDownTag() {
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
		this.name = null;
		this.label = null;
		this.align = null;
		this.width = 0;
		this.hidden = false;
		this.code = null;
		this.dsCode = null;
		this.sqlCode = null;
		this.prefix = null;
		this.suffix = null;
		this.readonly = false;
		this.tagColors = null;

		this.onClick = null;// 单击
		this.onChange = null;// 数据变化
		this.onDblClick = null;// 双击
		this.onBlur = null;// 失去焦点
		this.onFocus = null;// 获得焦点时
		this.helpTip = null;

		this.dsChildOpts = new DataSet();
	}

	/**
	 * 提供给子标签进行数据的设置
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-7
	 * @since V1.0
	 */
	public void setChildOpt(String key, String value, String color) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("key值不能为空");
		}
		if (StringUtil.chkStrNull(value)) {
			throw new AppException("value为空");
		}

		this.dsChildOpts.addRow();
		this.dsChildOpts.put(this.dsChildOpts.size() - 1, "code", key);
		this.dsChildOpts.put(this.dsChildOpts.size() - 1, "content", value);
		this.dsChildOpts.put(this.dsChildOpts.size() - 1, "color", color);
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
				this.align = "left";
			}

			// 清空自定义选项
			this.dsChildOpts.clear();

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
			// 选项处理--按优先级来
			DataSet dsCode = new DataSet();
			if (!StringUtil.chkStrNull(this.dsCode)) {
				dsCode = (DataSet) this.pageContext.getRequest()
					.getAttribute(this.dsCode);
				if (null == dsCode) {
					throw new BizException("dsCode设置项错误：" + this.dsCode
							+ "数据为空【原因：数据返回时没有put该dsCode值】");
				}
			} else if (!StringUtil.chkStrNull(this.sqlCode)) {
				Sql sql = new Sql();
				sql.setSql(this.sqlCode);
				dsCode = sql.executeQuery();
			} else if (!StringUtil.chkStrNull(this.code)) {
				dsCode = CodeUtil.getDsByDmbh(this.code, this.prefix, this.suffix);
			} else if (this.dsChildOpts.size() > 0) {// 自定义的项
				dsCode = this.dsChildOpts;
			}
			// tag颜色
			if (!StringUtil.chkStrNull(this.tagColors)) {
				HashMap<String, String> tagColorsMap = new HashMap<String, String>();
				String[] tagColorsArr = this.tagColors.split(",");
				for (int i = 0, n = tagColorsArr.length; i < n; i++) {
					String[] oneTagColorArr = tagColorsArr[i].split(":");
					tagColorsMap.put(oneTagColorArr[0], oneTagColorArr[1]);
				}
				for (int i = 0, n = dsCode.size(); i < n; i++) {
					String code = dsCode.getString(i, "code");
					if (tagColorsMap.containsKey(code)) {
						dsCode.put(i, "color", tagColorsMap.get(code));
					}
				}
			}

			// 创建数据
			DataMap tagDm = new DataMap();
			tagDm.put("name", this.name);
			tagDm.put("label", this.label);
			tagDm.put("align", this.align);
			tagDm.put("width", this.width);
			tagDm.put("hidden", this.hidden);
			tagDm.put("dscode", dsCode);
			tagDm.put("readonly", this.readonly);
			tagDm.put("helptip", this.helpTip);

			// 事件
			tagDm.put("onchange", this.onChange);
			tagDm.put("onclick", this.onClick);
			tagDm.put("ondblclick", this.onDblClick);
			tagDm.put("onblur", this.onBlur);
			tagDm.put("onfocus", this.onFocus);

			QueryGridTag queryGrid;
			if (this.getParent() instanceof QueryGridTag) {
				queryGrid = (QueryGridTag) this.getParent();
			} else {// 列组
				ColumnGroupTag groupTag = (ColumnGroupTag) this.getParent();
				queryGrid = (QueryGridTag) (groupTag).getParent();
				groupTag.setGroupColumn(this.name);
			}
			queryGrid.addColumn("columnmultidropdown", tagDm);

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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDsCode() {
		return dsCode;
	}

	public void setDsCode(String dsCode) {
		this.dsCode = dsCode;
	}

	public String getSqlCode() {
		return sqlCode;
	}

	public void setSqlCode(String sqlCode) {
		this.sqlCode = sqlCode;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public String getOnDblClick() {
		return onDblClick;
	}

	public void setOnDblClick(String onDblClick) {
		this.onDblClick = onDblClick;
	}

	public String getOnBlur() {
		return onBlur;
	}

	public void setOnBlur(String onBlur) {
		this.onBlur = onBlur;
	}

	public String getOnFocus() {
		return onFocus;
	}

	public void setOnFocus(String onFocus) {
		this.onFocus = onFocus;
	}

	public String getTagColors() {
		return tagColors;
	}

	public void setTagColors(String tagColors) {
		this.tagColors = tagColors;
	}
	
	public String getHelpTip() {
		return helpTip;
	}

	public void setHelpTip(String helpTip) {
		this.helpTip = helpTip;
	}
}
