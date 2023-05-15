package com.grace.frame.shine.taglib;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysUser;
import com.grace.frame.util.TagSupportUtil;

/**
 * 新标签库所有标签需要继承的类(shine标签库)
 * 
 * @author yjc
 */
public class Tag extends BodyTagSupport{
	private static final long serialVersionUID = 1L;
	/**
	 * layui 内置了七种背景色，以便你用于各种元素中，如：徽章、分割线、导航等等--用于限制标签设置颜色时的属性
	 */
	private static HashMap<String, Object> LayuiBgColorLimitsMap = new HashMap<String, Object>();
	static {
		Tag.LayuiBgColorLimitsMap.put("red", null);
		Tag.LayuiBgColorLimitsMap.put("orange", null);
		Tag.LayuiBgColorLimitsMap.put("green", null);
		Tag.LayuiBgColorLimitsMap.put("cyan", null);
		Tag.LayuiBgColorLimitsMap.put("blue", null);
		Tag.LayuiBgColorLimitsMap.put("black", null);
		Tag.LayuiBgColorLimitsMap.put("gray", null);
	}

	/**
	 * 检查颜色-是否符合要求
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2017-11-24
	 * @since V1.0
	 */
	protected final void chkLayuiBgColorLimits(String color) throws AppException {
		if (StringUtil.chkStrNull(color)) {
			return;
		}
		if (!Tag.LayuiBgColorLimitsMap.containsKey(color)) {
			throw new AppException("传入的颜色值不合法");
		}
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
	 * 向前台发送数据,并进行初始化操作<br>
	 * dm:参数信息；--调用在所有标签项输出完成后调用前台通过this.obj.data("obj_data");获取<br>
	 * tagName:标签name;<br>
	 * isInit:参数标识是否调用初始化函数<br>
	 * 
	 * @author yjc
	 * @throws IOException
	 * @throws AppException
	 * @date 创建时间 2017-12-6
	 * @since V1.0
	 */
	protected final void objInit(DataMap dm, String tagName) throws IOException, AppException {
		if (null == dm) {
			dm = new DataMap();
		}
		if (StringUtil.chkStrNull(tagName)) {
			throw new AppException("传入的tagName为空");
		}
		StringBuffer strBF = new StringBuffer();
		strBF.append("<script type=\"text/javascript\">");
		strBF.append("getObject(\"" + tagName + "\").init(new HashMap("
				+ dm.toJsonString() + "));");
		strBF.append("</script>");
		// 输出字符串
		JspWriter out = this.pageContext.getOut();
		out.write(strBF.toString());
	}

	/**
	 * 资源释放
	 */
	public void release() {
		super.release();
	}
}
