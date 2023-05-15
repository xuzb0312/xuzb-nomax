package com.grace.frame.shine.taglib;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;

/**
 * 选项卡标签
 * 
 * @author yjc
 */
public class TabTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;
	private String type;// 显示样式null,brief,card
	private boolean allowClose;// 是否运行删除

	/**
	 * 固定导航的样式-事例：<br>
	 * headerStyle="position:fixed;top:9px;left:0px;right:0px;"<br>
	 * pageStyle="position:absolute;left:0px;right:0;top:50px;bottom:0px;width:auto;overflow:auto;box-sizing:border-box;"
	 */
	private String conStyle;// 整体样式开发的接口
	private String headerStyle;// 标题样式开放的接口
	private String pageStyle;// 内容页开放的样式接口

	// 事件
	private String onAdd;
	private String onChange;// this,index,elem-当前Tab标题所在的原始DOM元素，得到当前Tab的所在下标,得到当前的Tab大容器
	private String onDelete;

	// 子页面
	private DataSet dsTabPage = new DataSet();
	private boolean isFirstTabSelect;// 是否第一个页面选中，当都没有设置默认时，该项为true

	public TabTag() {
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
		this.name = null;// 页边距默认10px;
		this.type = null;
		this.allowClose = false;

		// 子页面
		this.dsTabPage.clear();
		this.isFirstTabSelect = true;

		// 事件
		this.onAdd = "";
		this.onChange = "";
		this.onDelete = "";
	}

	/**
	 * 新增子页面
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-2-1
	 * @since V1.0
	 */
	public void addTabPage(String c_name, String c_title, boolean c_selected,
			String c_bodyContent) throws AppException {
		if (c_selected) {
			this.isFirstTabSelect = false;
		}
		DataMap dmPage = new DataMap();
		dmPage.put("name", c_name);
		dmPage.put("title", c_title);
		dmPage.put("selected", c_selected);
		dmPage.put("bodycontent", c_bodyContent);
		this.dsTabPage.addRow(dmPage);
	}

	/**
	 * 标签开始时
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(this.name)) {
				this.name = StringUtil.getUUID();
			}

			StringBuffer strBF = new StringBuffer();
			strBF.append("<div obj_type=\"tab\" id=\"")
				.append(this.name)
				.append("\" lay-filter=\"")
				.append(this.name)
				.append("\" class=\"layui-tab")
				.append(StringUtil.chkStrNull(this.type) ? "" : (" layui-tab-" + this.type))
				.append("\"")
				.append(this.allowClose ? " lay-allowClose=\"true\"" : "")
				.append("")
				.append(StringUtil.chkStrNull(this.conStyle) ? "" : (" style=\""
						+ this.conStyle + "\""))
				.append(">");

			// 数据的输出
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
	 * 标签结束时，执行
	 */
	@Override
	public int doEndTag() throws JspException {
		try {
			// 所有的子页面
			HashMap<String, Boolean> mapTabPageName = new HashMap<String, Boolean>();

			StringBuffer strBF = new StringBuffer();
			strBF.append("<ul class=\"layui-tab-title\"")
				.append(StringUtil.chkStrNull(this.headerStyle) ? "" : (" style=\""
						+ this.headerStyle + "\""))
				.append(">");
			for (int i = 0, n = this.dsTabPage.size(); i < n; i++) {
				String c_name = this.dsTabPage.getString(i, "name");
				String c_title = this.dsTabPage.getString(i, "title");
				boolean c_selected = this.dsTabPage.getBoolean(i, "selected");

				// 放入页面
				mapTabPageName.put(c_name, true);

				if (i == 0 && this.isFirstTabSelect) {
					c_selected = true;
				}
				strBF.append("<li")
					.append(c_selected ? (" class=\"layui-this\"") : "")
					.append(" lay-id=\"")
					.append(c_name)
					.append("\">")
					.append(c_title)
					.append("</li>");
			}
			strBF.append("</ul>");
			strBF.append("<div class=\"layui-tab-content\"")
				.append(StringUtil.chkStrNull(this.pageStyle) ? "" : (" style=\""
						+ this.pageStyle + "\""))
				.append(">");
			for (int i = 0, n = this.dsTabPage.size(); i < n; i++) {
				String c_bodyContent = this.dsTabPage.getString(i, "bodycontent");
				boolean c_selected = this.dsTabPage.getBoolean(i, "selected");
				if (i == 0 && this.isFirstTabSelect) {
					c_selected = true;
				}
				strBF.append("<div class=\"layui-tab-item")
					.append(c_selected ? " layui-show" : "")
					.append("\">")
					.append(c_bodyContent)
					.append("</div>");
			}
			strBF.append("</div>");
			strBF.append("</div>");
			// 数据的输出
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			DataMap pdm = new DataMap();
			pdm.put("onadd", this.onAdd);// 注册事件
			pdm.put("onchange", this.onChange);// 选项卡切换
			pdm.put("ondelete", this.onDelete);// 选项卡删除
			pdm.put("tabpagesname", mapTabPageName);
			this.objInit(pdm, this.name);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isAllowClose() {
		return allowClose;
	}

	public void setAllowClose(boolean allowClose) {
		this.allowClose = allowClose;
	}

	public String getConStyle() {
		return conStyle;
	}

	public void setConStyle(String conStyle) {
		this.conStyle = conStyle;
	}

	public String getHeaderStyle() {
		return headerStyle;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
	}

	public String getPageStyle() {
		return pageStyle;
	}

	public void setPageStyle(String pageStyle) {
		this.pageStyle = pageStyle;
	}

	public String getOnAdd() {
		return onAdd;
	}

	public void setOnAdd(String onAdd) {
		this.onAdd = onAdd;
	}

	public String getOnChange() {
		return onChange;
	}

	public void setOnChange(String onChange) {
		this.onChange = onChange;
	}

	public String getOnDelete() {
		return onDelete;
	}

	public void setOnDelete(String onDelete) {
		this.onDelete = onDelete;
	}
}
