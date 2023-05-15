package com.grace.frame.taglib;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import net.sf.json.JSONObject;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.util.StringUtil;

/**
 * Tag标签
 * 
 * @author yjc
 */
public class TabTag extends Tag{
	private static final long serialVersionUID = 1L;

	// 属性
	private String name;
	private boolean fixed;// 是否为固定，不进行增减的,默认是固定的
	private String tabPosition;// tab切换也位置，top,bottom,left,right

	// 事件
	private String onSelect;// Fires when user select a tab panel.--title,index
	private String onClose;// Fires when user close a tab panel.--title,index
	private String onContextMenu;// Fires when a tab panel is right
	// clicked.--e,title,index
	private String onAdd;// Fires when a new tab panel is added.--title,index

	private String onSelectSourceEvent;// 原生选择后执行的事件
	private HashMap<String, List<String>> tpGridNameMap;

	public TabTag() {
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
		this.onSelect = null;
		this.onClose = null;
		this.onContextMenu = null;
		this.onAdd = null;
		this.fixed = true;
		this.onSelectSourceEvent = null;
		this.tabPosition = "top";
		this.tpGridNameMap = new HashMap<String, List<String>>();
	}

	/**
	 * 增加grid的相关信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-9-25
	 * @since V1.0
	 */
	public void addTpGridName(String tpTitle, List<String> gridNameList) {
		this.tpGridNameMap.put(tpTitle, gridNameList);
	}

	/**
	 * 标签起始
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			if (!StringUtil.chkStrNull(this.name)) {
				this.appendln(strBF, "<script type=\"text/javascript\">");
				this.appendln(strBF, "var " + this.name
						+ "_tabPageKeyIndexMap=new HashMap();");
				this.appendln(strBF, "</script>");
			}
			strBF.append("<div "
					+ (StringUtil.chkStrNull(this.name) ? "" : "id=\""
							+ this.name + "\"")
					+ " obj_type=\"tab\" class=\"easyui-tabs\" ");

			this.onSelectSourceEvent = "on" + StringUtil.getUUID();
			this.clearEasyUICompAttr();
			this.setEasyUICompAttr("fit", "true", false);// 全窗口填充
			this.setEasyUICompAttr("lineHeight", "0", false);
			// 选中后系统需要做一些特殊的操作。
			this.setEasyUICompAttr("onSelect", this.onSelectSourceEvent, false);// 选中事件

			if (!StringUtil.chkStrNull(this.onClose)) {
				this.setEasyUICompAttr("onClose", this.onClose, false);
			}
			if (!StringUtil.chkStrNull(this.onContextMenu)) {
				this.setEasyUICompAttr("onContextMenu", this.onContextMenu, false);
			}
			if (!StringUtil.chkStrNull(this.onAdd)) {
				this.setEasyUICompAttr("onAdd", this.onAdd, false);
			}
			if ("bottom".equalsIgnoreCase(this.tabPosition)) {
				this.setEasyUICompAttr("tabPosition", "bottom", true);
			} else if ("left".equalsIgnoreCase(this.tabPosition)) {
				this.setEasyUICompAttr("tabPosition", "left", true);
			} else if ("right".equalsIgnoreCase(this.tabPosition)) {
				this.setEasyUICompAttr("tabPosition", "right", true);
			}

			if (this.fixed) {// 固定的。-则不显示右键菜单
				this.setEasyUICompAttr("enableConextMenu", "false", false);
			} else {
				if (GlobalVars.DEBUG_MODE) {
					StringBuffer menuStrBF = new StringBuffer();
					menuStrBF.append("[ {");
					menuStrBF.append("	text : '请求路径',");
					menuStrBF.append("	iconCls : 'icon-bug-link',");
					menuStrBF.append("	onclick : function(e, title, index, tabs) {");
					menuStrBF.append("		var url = tabs.tabs('getTab', index).panel('options').requrl;");
					menuStrBF.append("		if ('string' == typeof url) {");
					menuStrBF.append("			MsgBox.alert(url);");
					menuStrBF.append("		} else {");
					menuStrBF.append("		    var frameid = tabs.tabs('getTab', index).panel('options').frameid;");
					menuStrBF.append("			var jspPath = $('#' + frameid)[0].contentWindow.getJspPath();");
					menuStrBF.append("			MsgBox.alert('请求路径：<br/>'+url.getURLString()+'<hr/>JSP页面：<br/>'+jspPath);");
					menuStrBF.append("		}");
					menuStrBF.append("	}");
					menuStrBF.append("}, {");
					menuStrBF.append("	text : '请求数据',");
					menuStrBF.append("	iconCls : 'icon-bug-edit',");
					menuStrBF.append("	onclick : function(e, title, index, tabs) {");
					menuStrBF.append("		var url = tabs.tabs('getTab', index).panel('options').requrl;");
					menuStrBF.append("		if ('string' == typeof url) {");
					menuStrBF.append("			MsgBox.alert('请求类型为URL字符串，无数据信息');");
					menuStrBF.append("		} else {");
					menuStrBF.append("			MsgBox.showJsonData(url.getParas());");
					menuStrBF.append("		}");
					menuStrBF.append("	}");
					menuStrBF.append("} ]");

					this.setEasyUICompAttr("contextMenu", menuStrBF.toString(), false);
				}
			}
			strBF.append(" data-options=\"")
				.append(this.getEasyUICompAttrOptions())
				.append("\">");
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
			StringBuffer strBF = new StringBuffer();
			strBF.append("</div>");
			// 选中事件-对grid进行宽度调整
			this.appendln(strBF, "<script type=\"text/javascript\">");
			this.appendln(strBF, "function " + this.onSelectSourceEvent
					+ "(title, index){ ");
			this.appendln(strBF, "	var map = new HashMap("
					+ JSONObject.fromObject(this.tpGridNameMap).toString()
					+ ");");
			this.appendln(strBF, "	if(map.containsKey(title)){");
			this.appendln(strBF, "		var gridlist = map.get(title);");
			this.appendln(strBF, "      for(var i=0,n=gridlist.length;i<n;i++){");
			this.appendln(strBF, "			var gridName=gridlist[i];");
			this.appendln(strBF, "			var gridTmp = getObject(gridName);");
			this.appendln(strBF, "			gridTmp.selfAdaptHeight();");
			this.appendln(strBF, "			gridTmp.tableobj.setGridWidth($(\"#\" + gridName + \"_width_use\").width() - 5); ");
			this.appendln(strBF, "			gridTmp.refreshData4TabSelect();");// tab切换第一次时，重新加载一下数据
			this.appendln(strBF, "		}");
			this.appendln(strBF, "	}");
			if (!StringUtil.chkStrNull(this.onSelect)) {
				this.appendln(strBF, "	" + this.onSelect + "(title,index);");
			}
			this.appendln(strBF, "}");
			this.appendln(strBF, "</script>");

			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());

			this.release();
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
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

	public String getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(String onSelect) {
		this.onSelect = onSelect;
	}

	public String getOnClose() {
		return onClose;
	}

	public void setOnClose(String onClose) {
		this.onClose = onClose;
	}

	public String getOnContextMenu() {
		return onContextMenu;
	}

	public void setOnContextMenu(String onContextMenu) {
		this.onContextMenu = onContextMenu;
	}

	public String getOnAdd() {
		return onAdd;
	}

	public void setOnAdd(String onAdd) {
		this.onAdd = onAdd;
	}

	public boolean isFixed() {
		return fixed;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public String getTabPosition() {
		return tabPosition;
	}

	public void setTabPosition(String tabPosition) {
		this.tabPosition = tabPosition;
	}
}
