package com.grace.frame.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TagSupportUtil;

/**
 * 按钮标签 <br>
 * 说明：<br>
 * 1.linkButton:<br>
 * menu属性对其无效；<br>
 * 2.menuButton:<br>
 * iconAlign:属性无效；<br>
 * 3.splitButton:<br>
 * iconAlign:属性无效；<br>
 * 
 * @author yjc
 * @since mod.yjc.2015年12月14日--增加临时授权。
 */
public class ButtonTag extends Tag{

	private static final long serialVersionUID = 1L;
	// 属性
	private String type;// 按钮类型：link,menu,split,temp;--默认link
	private String name;// 按钮的唯一id
	private String disabled;// 是否是不可用状态
	private String plain;// 是否为简洁模式
	private String iconCls;// 图标；
	private String iconAlign;// 按钮位置
	private String selected;// 是否默认选择
	private String menu;// 菜单
	private String functionid;// 功能id
	private String value;// 按钮显示的文字
	private String iconClsTemp;// 临时缓存使用的图标字符串--由于多个标签同时解析时，会使用同一个对象，属性会继承上一个标签的相关属性，所以此处需要调整。
	private int colspan;// 放在form中时，所占的格数-只有放到form中才有效
	private String shortcutkey;// 快捷键，可以通过Alt+[]的方式直接执行按钮事件。
	private int introSetp;// 引导简介顺序号，从1开始，连续
	private String introContent;// 引导提示内容。
	private String introPosition;// 引导提示位置：取值left,right,top,bottom,不设置，会自动安排

	// 事件
	private String onclick;// 按钮的单击事件

	public ButtonTag() {
		this.initTag();
	}

	/**
	 * 标签初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-20
	 * @since V1.0
	 */
	private void initTag() {
		this.type = "link";
		this.name = null;
		this.disabled = "false";
		this.plain = "false";
		this.iconCls = null;
		this.iconAlign = "left";
		this.selected = "false";
		this.menu = null;
		this.functionid = null;
		this.value = null;
		this.colspan = 2;
		this.shortcutkey = null;
		this.introSetp = 0;
		this.introContent = null;
		this.introPosition = null;
	}

	/**
	 * 标签起始--注意，此方法中不要书写输出，因为MenuTag标签有对上级父标签的操作，即当Menutag标签的父表签为ButtonTag时，
	 * 会自动关联菜单menu;
	 */
	@Override
	public int doStartTag() throws JspException {
		try {
			if (StringUtil.chkStrNull(type)) {
				throw new AppException("type属性不能为空");
			}
			if (StringUtil.chkStrNull(value)) {
				throw new AppException("value属性不能为空");
			}
			// 对于按钮iconCls的自动适配-根据文本内容自动适配
			if (StringUtil.chkStrNull(this.iconCls)) {
				this.iconClsTemp = GlobalVars.TEXT_ICONCLS_MAP.get(this.value);
			} else {
				this.iconClsTemp = this.iconCls;
			}

			// 判断快捷键设置是否正确-只能A-Z。
			if (!StringUtil.chkStrNull(this.shortcutkey)) {
				if (this.shortcutkey.length() != 1) {
					throw new AppException("shortcutkey属性设置错误[A-Z]");
				}
				String allowStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
				int index = allowStr.indexOf(this.shortcutkey);
				if (index < 0) {
					throw new AppException("shortcutkey属性设置错误[A-Z]");
				}
				this.value = this.value
						+ " (<span style='text-decoration: underline;' title='快捷键(Alt+"
						+ this.shortcutkey + ")'>" + this.shortcutkey
						+ "</span>)";
				this.shortcutkey = String.valueOf(65 + index);
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
			// 如果为父容器为form的话
			String wrapStart = "";
			String wrapEnd = "";
			if (this.getParent() instanceof FormTag) {
				FormTag formTag = (FormTag) this.getParent();
				DataMap wrapDm = formTag.wrapStr4Children(this.colspan);
				wrapStart = wrapDm.getString("start");
				wrapEnd = wrapDm.getString("end");
			}

			if (this.checkFunctionRight(functionid)) {// 只有存在权限时，才渲染此标签
				String outstr = "";
				if ("link".equalsIgnoreCase(this.type)
						|| "temp".equalsIgnoreCase(this.type)) {
					outstr = this.getLinkBtnStr();
				} else if ("menu".equalsIgnoreCase(this.type)) {
					outstr = this.getMenuBtnStr();
				} else if ("split".equalsIgnoreCase(this.type)) {
					outstr = this.getSplitBtnStr();
				} else {
					throw new AppException("Type属性不合法");
				}

				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(wrapStart + outstr + wrapEnd);
			} else {
				// 对于没有权限的创建一个隐藏的元素，防止前台的js报错。
				StringBuffer strBF = new StringBuffer();
				strBF.append(wrapStart);
				if ("temp".equalsIgnoreCase(this.type)) {
					strBF.append(this.getTempBtnStr());
				} else {
					strBF.append(" <div obj_type=\"hiddenbutton\" style=\"display:none;\" ");
					if (!StringUtil.chkStrNull(this.name)) {
						strBF.append(" id=\"").append(this.name).append("\" ");
					}
					strBF.append("></div>");
				}
				strBF.append(wrapEnd);

				// 输出字符串
				JspWriter out = this.pageContext.getOut();
				out.write(strBF.toString());
			}
			this.release();// 资源释放
		} catch (IOException e) {
			this.dealException(e, "标签解析时，将html输出到jsp时，出现IO异常：" + e.getMessage());
		} catch (Exception e) {
			this.dealException(e);
		}
		return EVAL_PAGE;
	}

	/**
	 * linkBtn的处理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-30
	 * @since V1.0
	 */
	private String getLinkBtnStr() throws Exception {
		StringBuffer strBF = new StringBuffer();
		strBF.append("<a obj_type=\"linkbutton\" class=\"easyui-linkbutton\" href=\"javascript:void(0);\" ");
		if (!StringUtil.chkStrNull(this.onclick)) {
			strBF.append(" onclick=\"").append(this.onclick).append("\" ");
		}
		if (!StringUtil.chkStrNull(this.name)) {
			strBF.append(" id=\"").append(this.name).append("\"");
		}
		if (!StringUtil.chkStrNull(this.shortcutkey)) {
			strBF.append(" _shortcutkey=\"")
				.append(this.shortcutkey)
				.append("\"");
		}
		this.clearEasyUICompAttr();
		if ("true".equalsIgnoreCase(this.disabled)) {
			this.setEasyUICompAttr("disabled", "true", false);
		}
		if ("true".equalsIgnoreCase(this.plain)) {
			this.setEasyUICompAttr("plain", "true", false);
		}
		if ("true".equalsIgnoreCase(this.selected)) {
			this.setEasyUICompAttr("selected", "true", false);
		}
		if (!StringUtil.chkStrNull(this.iconClsTemp)) {
			this.setEasyUICompAttr("iconCls", this.iconClsTemp, true);
		}
		if (!StringUtil.chkStrNull(this.iconAlign)) {
			this.setEasyUICompAttr("iconAlign", this.iconAlign, true);
		}

		String dataOpt = this.getEasyUICompAttrOptions();
		if (!StringUtil.chkStrNull(dataOpt)) {
			strBF.append(" data-options=\"").append(dataOpt).append("\"");
		}
		TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
		strBF.append(">").append(this.value).append("</a>");
		return strBF.toString();
	}

	/**
	 * menuBtn的处理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-30
	 * @since V1.0
	 */
	private String getMenuBtnStr() throws Exception {
		StringBuffer strBF = new StringBuffer();
		strBF.append("<a obj_type=\"menubutton\" class=\"easyui-menubutton\" href=\"javascript:void(0);\" ");
		if (!StringUtil.chkStrNull(this.onclick)) {
			strBF.append(" onclick=\"").append(this.onclick).append("\" ");
		}
		if (!StringUtil.chkStrNull(this.name)) {
			strBF.append(" id=\"").append(this.name).append("\"");
		}
		if (!StringUtil.chkStrNull(this.shortcutkey)) {
			strBF.append(" _shortcutkey=\"")
				.append(this.shortcutkey)
				.append("\"");
		}
		this.clearEasyUICompAttr();
		if ("true".equalsIgnoreCase(this.disabled)) {
			this.setEasyUICompAttr("disabled", "true", false);
		}
		if ("false".equalsIgnoreCase(this.plain)) {
			this.setEasyUICompAttr("plain", "false", false);
		}
		if ("true".equalsIgnoreCase(this.selected)) {
			this.setEasyUICompAttr("selected", "true", false);
		}
		if (!StringUtil.chkStrNull(this.iconClsTemp)) {
			this.setEasyUICompAttr("iconCls", this.iconClsTemp, true);
		}
		if (!StringUtil.chkStrNull(this.menu)) {
			this.setEasyUICompAttr("menu", "#" + this.menu, true);
		}

		String dataOpt = this.getEasyUICompAttrOptions();
		if (!StringUtil.chkStrNull(dataOpt)) {
			strBF.append(" data-options=\"").append(dataOpt).append("\"");
		}
		TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
		strBF.append(">").append(this.value).append("</a>");
		return strBF.toString();
	}

	/**
	 * splitBtn的处理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-30
	 * @since V1.0
	 */
	private String getSplitBtnStr() throws Exception {
		StringBuffer strBF = new StringBuffer();
		strBF.append("<a obj_type=\"splitbutton\" class=\"easyui-splitbutton\" href=\"javascript:void(0);\" ");
		if (!StringUtil.chkStrNull(this.onclick)) {
			strBF.append(" onclick=\"").append(this.onclick).append("\" ");
		}
		if (!StringUtil.chkStrNull(this.name)) {
			strBF.append(" id=\"").append(this.name).append("\"");
		}
		if (!StringUtil.chkStrNull(this.shortcutkey)) {
			strBF.append(" _shortcutkey=\"")
				.append(this.shortcutkey)
				.append("\"");
		}
		this.clearEasyUICompAttr();
		if ("true".equalsIgnoreCase(this.disabled)) {
			this.setEasyUICompAttr("disabled", "true", false);
		}
		if ("false".equalsIgnoreCase(this.plain)) {
			this.setEasyUICompAttr("plain", "false", false);
		}
		if ("true".equalsIgnoreCase(this.selected)) {
			this.setEasyUICompAttr("selected", "true", false);
		}
		if (!StringUtil.chkStrNull(this.iconClsTemp)) {
			this.setEasyUICompAttr("iconCls", this.iconClsTemp, true);
		}
		if (!StringUtil.chkStrNull(this.menu)) {
			this.setEasyUICompAttr("menu", "#" + this.menu, true);
		}

		String dataOpt = this.getEasyUICompAttrOptions();
		if (!StringUtil.chkStrNull(dataOpt)) {
			strBF.append(" data-options=\"").append(dataOpt).append("\"");
		}
		TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
		strBF.append(">").append(this.value).append("</a>");
		return strBF.toString();
	}

	/**
	 * tempBtn的处理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-30
	 * @since V1.0
	 */
	private String getTempBtnStr() throws Exception {
		String func_uiid = StringUtil.getUUID();

		StringBuffer strBF = new StringBuffer();
		strBF.append("<a obj_type=\"linkbutton\" class=\"easyui-linkbutton\" href=\"javascript:void(0);\" ");
		if (!StringUtil.chkStrNull(this.onclick)) {
			strBF.append(" onclick=\"func_" + func_uiid + "();\" ");
		}
		if (!StringUtil.chkStrNull(this.name)) {
			strBF.append(" id=\"").append(this.name).append("\"");
		}
		if (!StringUtil.chkStrNull(this.shortcutkey)) {
			strBF.append(" _shortcutkey=\"")
				.append(this.shortcutkey)
				.append("\"");
		}
		this.clearEasyUICompAttr();
		if ("true".equalsIgnoreCase(this.disabled)) {
			this.setEasyUICompAttr("disabled", "true", false);
		}
		if ("true".equalsIgnoreCase(this.plain)) {
			this.setEasyUICompAttr("plain", "true", false);
		}
		if ("true".equalsIgnoreCase(this.selected)) {
			this.setEasyUICompAttr("selected", "true", false);
		}
		if (!StringUtil.chkStrNull(this.iconClsTemp)) {
			this.setEasyUICompAttr("iconCls", this.iconClsTemp, true);
		}
		if (!StringUtil.chkStrNull(this.iconAlign)) {
			this.setEasyUICompAttr("iconAlign", this.iconAlign, true);
		}

		String dataOpt = this.getEasyUICompAttrOptions();
		if (!StringUtil.chkStrNull(dataOpt)) {
			strBF.append(" data-options=\"").append(dataOpt).append("\"");
		}
		TagSupportUtil.appendIntroAttr(strBF, this.introSetp, this.introContent, this.introPosition);
		strBF.append(">").append(this.value).append("</a>");
		this.appendln(strBF, "");
		this.appendln(strBF, "<script type=\"text/javascript\">");
		this.appendln(strBF, "	function func_" + func_uiid + "(){");
		this.appendln(strBF, "		applyTempRight('" + this.functionid
				+ "', function(data){");
		this.appendln(strBF, "			if(chkObjNull(data)){");
		this.appendln(strBF, "				return;");
		this.appendln(strBF, "			}");
		this.appendln(strBF, "			if(true == data){");
		this.appendln(strBF, "				" + this.onclick);
		this.appendln(strBF, "			}");
		this.appendln(strBF, "		});");
		this.appendln(strBF, "	}");
		this.appendln(strBF, "</script>");

		return strBF.toString();
	}

	/**
	 * 资源释放
	 */
	@Override
	public void release() {
		this.initTag();
		super.release();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisabled() {
		return disabled;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public String getPlain() {
		return plain;
	}

	public void setPlain(String plain) {
		this.plain = plain;
	}

	public String getIconCls() {
		return iconCls;
	}

	public void setIconCls(String iconCls) {
		this.iconCls = iconCls;
	}

	public String getIconAlign() {
		return iconAlign;
	}

	public void setIconAlign(String iconAlign) {
		this.iconAlign = iconAlign;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getFunctionid() {
		return functionid;
	}

	public void setFunctionid(String functionid) {
		this.functionid = functionid;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOnclick() {
		return onclick;
	}

	public void setOnclick(String onclick) {
		this.onclick = onclick;
	}

	public String getIconClsTemp() {
		return iconClsTemp;
	}

	public void setIconClsTemp(String iconClsTemp) {
		this.iconClsTemp = iconClsTemp;
	}

	public int getColspan() {
		return colspan;
	}

	public void setColspan(int colspan) {
		this.colspan = colspan;
	}

	public String getShortcutkey() {
		return shortcutkey;
	}

	public void setShortcutkey(String shortcutkey) {
		this.shortcutkey = shortcutkey;
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
}
