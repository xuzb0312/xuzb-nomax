package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 树标签
 * 
 * @author yjc
 */
public class TreeTag extends Tag{
	private static final long serialVersionUID = 1L;
	// 属性
	private String name;// 控件的唯一ID
	private String className;// 数据url
	private String paras;// 参数
	private boolean checkbox;// 是否有单选框
	private boolean autoToggle;// 点击节点是否自动展开或者关闭-默认自动
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排

	// 事件
	private String onClick;// 当用户点击一个节点时触发
	private String onDblClick;// 当用户双击一个节点时触发。
	private String onSelect;// 当节点被选中时触发。
	private String onContextMenu;// 当右键点击节点时触发。
	private String onBeforeSelect;// 节点被选中前触发，返回 false 则取消选择动作。--参数：node
	private String onLoadComplete;// 通过异步加载完成后执行的事件，参数treeObj
	private String onBeforeCheck;// (node, checked)当用户点击复选框前触发，返回 false则取消该选中动作。
	private String onCheck;// (node, checked)当用户点击复选框时触发。

	/**
	 * 构造函数
	 */
	public TreeTag() {
		this.initTag();
	}

	/**
	 * 初始化
	 * 
	 * @date 创建时间 2015-7-22
	 * @since V1.0
	 */
	private void initTag() {
		this.id = null;
		this.className = null;
		this.onClick = null;
		this.onContextMenu = null;
		this.onDblClick = null;
		this.onSelect = null;
		this.checkbox = false;
		this.autoToggle = true;
		this.onBeforeSelect = null;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
		this.onLoadComplete = null;
		this.onBeforeCheck = null;
		this.onCheck = null;

	}

	/**
	 * 标签开始的操作
	 */
	public int doStartTag() throws JspException {
		try {
			// 数据检测
			this.checkAttr();
		} catch (Exception e) {
			this.dealException(e);
		}
		return EVAL_BODY_INCLUDE;
	}

	/**
	 * 对属性、参数进行数据合法性进行检测
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	private void checkAttr() throws AppException {
		if (StringUtil.chkStrNull(this.name)) {
			throw new AppException("name属性不允许为空");
		}
	}

	/**
	 * 标签结束的操作
	 */
	public int doEndTag() throws JspException {
		try {
			StringBuffer strBF = new StringBuffer();
			this.appendln(strBF, "<ul id=\"" + this.name
					+ "\" obj_type=\"tree\"");
			TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
			strBF.append("></ul>");

			// js事件
			this.appendln(strBF, "<script type=\"text/javascript\">");
			this.appendln(strBF, "$(function(){");// 保证在系统加载完成后再执行；

			this.setEasyUICompAttr("lines", "true", false);// 设置带线条连接
			if (this.checkbox) {
				this.setEasyUICompAttr("checkbox", "true", false);// 是否是带checbox
			}
			/**
			 * 单击事件：<br>
			 * 参数：node:<br>
			 * 当用户点击节点时触发node参数包含如下属性： id：节点id。 text：显示在节点上的文本。 checked：节点是否被选择。
			 * attributes：节点的自定义属性。 target：被点击的目标DOM对象。
			 */
			// 单击事件(点击折叠-展开)
			StringBuffer onClickBF = new StringBuffer();
			onClickBF.append("function (node){ ");
			onClickBF.append(" 	var target = node.target; ");
			onClickBF.append(" 	var treeObject = getObject(\"" + this.name
					+ "\"); ");
			if (this.autoToggle) {
				onClickBF.append(" 	if(!treeObject.isLeaf(target)){ ");
				onClickBF.append(" 		treeObject.toggle(target); ");
				onClickBF.append(" 	} ");
			}
			if (!StringUtil.chkStrNull(this.onClick)) {
				onClickBF.append(" 	return " + this.onClick + "(node); ");
			} else {
				onClickBF.append(" 	return true; ");
			}
			onClickBF.append(" } ");
			this.setEasyUICompAttr("onClick", onClickBF.toString(), false);// 解决在frame页面打开提示方法不存在的bug
			this.setEasyUICompAttr("lines", "false", false);// 美化展示，不再展示线条

			// 右击事件
			if (!StringUtil.chkStrNull(this.onContextMenu)) {
				this.setEasyUICompAttr("onContextMenu", "function(e,node){"
						+ this.onContextMenu + "(e,node);}", false);
			}
			// 双击事件
			if (!StringUtil.chkStrNull(this.onDblClick)) {
				this.setEasyUICompAttr("onDblClick", "function(node){"
						+ this.onDblClick + "(node);}", false);
			}
			// 选中前事件
			if (!StringUtil.chkStrNull(this.onBeforeSelect)) {
				this.setEasyUICompAttr("onBeforeSelect", "function(node){"
						+ this.onBeforeSelect + "(node);}", false);
			}
			// 选中事件
			if (!StringUtil.chkStrNull(this.onSelect)) {
				this.setEasyUICompAttr("onSelect", "function(node){"
						+ this.onSelect + "(node);}", false);
			}

			// checkbox事件
			if (this.checkbox) {
				if (!StringUtil.chkStrNull(this.onBeforeCheck)) {
					this.setEasyUICompAttr("onBeforeCheck", "function(node,checked){if(typeof "
							+ this.onBeforeCheck
							+ "  === \"function\"){return "
							+ this.onBeforeCheck
							+ "(node,checked);}else{return true;}}", false);
				}
				if (!StringUtil.chkStrNull(this.onCheck)) {
					this.setEasyUICompAttr("onCheck", "function(node,checked){if(typeof "
							+ onCheck
							+ " === \"function\"){"
							+ this.onCheck
							+ "(node,checked);}}", false);
				}
			}

			this.appendln(strBF, this.getEasyUICompAttrScript(this.name, "tree", false));// 输出

			// 后台数据加载
			if (!StringUtil.chkStrNull(this.className)) {
				if (null == this.paras || "".equals(this.paras)) {
					this.appendln(strBF, "var map = new HashMap();");
				} else {
					this.appendln(strBF, "var map = new HashMap(\"{"
							+ this.paras + "}\");");
				}
				if (StringUtil.chkStrNull(this.onLoadComplete)) {
					this.appendln(strBF, "getObject(\"" + this.name
							+ "\").asyncLoadRemotData(\"" + this.className
							+ "\",map);");
				} else {
					this.appendln(strBF, "getObject(\"" + this.name
							+ "\").asyncLoadRemotData(\"" + this.className
							+ "\",map," + this.onLoadComplete.trim() + ");");
				}
			}
			this.appendln(strBF, "});");
			this.appendln(strBF, "</script>");
			// 输出字符串
			JspWriter out = this.pageContext.getOut();
			out.write(strBF.toString());
			this.release();// 资源释放
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		}
		return EVAL_PAGE;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getParas() {
		return paras;
	}

	public void setParas(String paras) {
		this.paras = paras;
	}

	public String getOnClick() {
		return onClick;
	}

	public void setOnClick(String onClick) {
		this.onClick = onClick;
	}

	public String getOnDblClick() {
		return onDblClick;
	}

	public void setOnDblClick(String onDblClick) {
		this.onDblClick = onDblClick;
	}

	public String getOnSelect() {
		return onSelect;
	}

	public void setOnSelect(String onSelect) {
		this.onSelect = onSelect;
	}

	public String getOnContextMenu() {
		return onContextMenu;
	}

	public void setOnContextMenu(String onContextMenu) {
		this.onContextMenu = onContextMenu;
	}

	public boolean isCheckbox() {
		return checkbox;
	}

	public void setCheckbox(boolean checkbox) {
		this.checkbox = checkbox;
	}

	public boolean isAutoToggle() {
		return autoToggle;
	}

	public void setAutoToggle(boolean autoToggle) {
		this.autoToggle = autoToggle;
	}

	public String getOnBeforeSelect() {
		return onBeforeSelect;
	}

	public void setOnBeforeSelect(String onBeforeSelect) {
		this.onBeforeSelect = onBeforeSelect;
	}

	public int getIntroSetp() {
		return introSetp;
	}

	public void setIntroSetp(int introSetp) {
		this.introSetp = introSetp;
	}

	public String getIntroContent() {
		return introContent;
	}

	public void setIntroContent(String introContent) {
		this.introContent = introContent;
	}

	public String getIntroPosition() {
		return introPosition;
	}

	public void setIntroPosition(String introPosition) {
		this.introPosition = introPosition;
	}

	public String getOnLoadComplete() {
		return onLoadComplete;
	}

	public void setOnLoadComplete(String onLoadComplete) {
		this.onLoadComplete = onLoadComplete;
	}

	public String getOnBeforeCheck() {
		return onBeforeCheck;
	}

	public void setOnBeforeCheck(String onBeforeCheck) {
		this.onBeforeCheck = onBeforeCheck;
	}

	public String getOnCheck() {
		return onCheck;
	}

	public void setOnCheck(String onCheck) {
		this.onCheck = onCheck;
	}

	/**
	 * 资源释放
	 */
	public void release() {
		this.initTag();
		super.release();
	}
}
