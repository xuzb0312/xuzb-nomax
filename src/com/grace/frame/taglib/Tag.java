package com.grace.frame.taglib;

import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysUser;
import com.grace.frame.util.TagSupportUtil;

/**
 * 对BodyTagSupport又进行了一次封装，加入一些本地化特征
 * <p>
 * 要求所有的标签均继承与此类；
 * </p>
 * 
 * @author yjc
 */
public class Tag extends BodyTagSupport{
	private static final long serialVersionUID = 1L;

	/**
	 * 标签-构造
	 */
	public Tag() {
		this.easyuiCompAttrMap = new HashMap<String, String>();
	}

	/**
	 * 异常处理操作
	 * 
	 * @author yjc
	 * @throws JspException
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	protected final void dealException(Exception ex, String msg) throws JspException {
		TagSupportUtil.dealException(ex, msg, this.pageContext);
	}

	/**
	 * 异常处理操作
	 * 
	 * @author yjc
	 * @throws JspException
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	protected final void dealException(Exception ex) throws JspException {
		TagSupportUtil.dealException(ex, this.pageContext);
	}

	/**
	 * 换行追加，在标签输出时，将会频繁使用。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	protected final StringBuffer appendln(StringBuffer strBF, String str) {
		return TagSupportUtil.appendln(strBF, str);
	}

	/**
	 * 获取基准路径
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	protected String getBasePath() {
		return TagSupportUtil.getBasePathFromPageContext(this.pageContext);
	}

	/**
	 * 由于本次封装是基于easyui的，所以该部分提供对于easyui特殊的标签初始化特性的支持，增加属性配置列表工具
	 */
	private HashMap<String, String> easyuiCompAttrMap;// easyui组件属性map

	// 清空属性--使用前，一定要调用一次
	protected final void clearEasyUICompAttr() {
		this.easyuiCompAttrMap.clear();
	}

	// 设置属性，hasQuote是否自动增加引号
	protected final void setEasyUICompAttr(String key, String value,
			boolean hasQuote) {
		if (hasQuote) {
			this.easyuiCompAttrMap.put(key, "\"" + value + "\"");
		} else {
			this.easyuiCompAttrMap.put(key, value);
		}
	}

	// 获取easyui组件的属性脚本;hashScriptTag是否在脚本外围包上<script>代码
	protected final String getEasyUICompAttrScript(String id, String compName,
			boolean hashScriptTag) {
		StringBuffer strBF = new StringBuffer();
		if (hashScriptTag) {
			this.appendln(strBF, "<script type=\"text/javascript\">");
		}
		// 输出js代码
		this.appendln(strBF, "$(\"#" + id + "\")." + compName + "({");
		Object[] keys = this.easyuiCompAttrMap.keySet().toArray();
		for (int i = 0, n = keys.length; i < n; i++) {
			String key = (String) keys[i];
			String value = this.easyuiCompAttrMap.get(key);
			if (i == (n - 1)) {
				this.appendln(strBF, key + ":" + value);
			} else {
				this.appendln(strBF, key + ":" + value + ",");
			}
		}
		this.appendln(strBF, "});");

		if (hashScriptTag) {
			this.appendln(strBF, "</script>");
		}
		this.clearEasyUICompAttr();// 数据组装完成后，清空
		return strBF.toString();
	}

	// 获取easyui组件的data-options
	protected final String getEasyUICompAttrOptions() {
		StringBuffer strBF = new StringBuffer();
		Object[] keys = this.easyuiCompAttrMap.keySet().toArray();
		for (int i = 0, n = keys.length; i < n; i++) {
			String key = (String) keys[i];
			String value = this.easyuiCompAttrMap.get(key);

			// 在此处对于引号进行处理，双引号，变为单引号处理
			if (null != value && value.startsWith("\"") && value.endsWith("\"")) {
				value = "'" + value.substring(1, value.length() - 1) + "'";
			}

			if (i == (n - 1)) {
				strBF.append(key + ":" + value);
			} else {
				strBF.append(key + ":" + value + ",");
			}
		}
		this.clearEasyUICompAttr();// 数据组装完成后，清空
		return strBF.toString();
	}

	/**
	 * 判断是否存在该权限
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-6-30
	 * @since V1.0
	 */
	protected final boolean checkFunctionRight(String functionid) throws AppException {
		// 未设置权限id,默认有该权限
		if (StringUtil.chkStrNull(functionid)) {
			return true;
		}

		// 获取用户
		SysUser currentSysUser = (SysUser) this.pageContext.getSession()
			.getAttribute("currentsysuser");// 当前用户
		if (null == currentSysUser) {
			return false;// 用户未登录，认为没有该权限
		}

		// 超级管理员不判断权限--仍然通过btnfuncmap进行判断，因为在登录的时候，超级管理远的权限已经全部加载。-mod.yjc.2015年8月12日
		// if ("A".equals(currentSysUser.getYhlx())) {
		// return true;// 存在权限
		// }

		DataMap btnFuncMap = (DataMap) currentSysUser.getAllInfoDM()
			.get("btnfuncmap");// 获取权限map

		// mod.yjc.2016年11月24日-支持多个权限id的设置，标签使用逗号间隔
		String[] arrFuncIDS = functionid.split(",");
		for (int i = 0, n = arrFuncIDS.length; i < n; i++) {
			if (btnFuncMap.containsKey(arrFuncIDS[i])) {
				return true;// 有该权限
			}
		}
		return false;
	}

	/**
	 * 资源释放
	 */
	public void release() {
		this.easyuiCompAttrMap.clear();
		this.easyuiCompAttrMap = new HashMap<String, String>();
		super.release();
	}

	/**
	 * 重写form写标签获取父标签时，如果为折叠标签则自动跳过，再向上找一层
	 * 
	 * @author yjc
	 * @date 创建时间 2020-9-1
	 * @since V1.0
	 */
	@Override
	public javax.servlet.jsp.tagext.Tag getParent() {
		javax.servlet.jsp.tagext.Tag parnetTag = super.getParent();
		if (parnetTag instanceof CollapsablePartTag) {
			CollapsablePartTag collapsablePartTag = (CollapsablePartTag) parnetTag;
			parnetTag = collapsablePartTag.getParent();
		}
		return parnetTag;
	}
}
