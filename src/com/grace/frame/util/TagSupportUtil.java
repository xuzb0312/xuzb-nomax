package com.grace.frame.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * 对标签库提供一系列的工具方法的封装
 * 
 * @author yjc
 */
public class TagSupportUtil{
	/**
	 * 对异常信息在界面使用红色字体展示，并且使用运行模式和开发模式区分
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static void dealException(Exception ex, String msg,
			PageContext pageContext) throws JspException {
		try {
			// 控制台输出异常信息
			ex.printStackTrace();

			// 记录系统日志
			SysLogUtil.logError("标签解析异常：" + ex.getMessage(), ex);

			// 前台输出报红输出错误信息
			java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(cw, true);
			ex.printStackTrace(pw);

			// 错误信息输出
			JspWriter out = pageContext.getOut();
			out.println("<div style='color:red;'><pre>");
			out.println("标签解析出现异常：" + msg + " 【请联系开发人员解决！】");
			if (GlobalVars.DEBUG_MODE) {
				out.println(cw.toString());
			}
			out.println("</pre><div>");
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException("异常处理时，异常信息输出失败：" + e.getMessage()
					+ "[原异常信息：" + ex.getMessage() + "]");
		}
	}

	/**
	 * 对异常信息在界面使用红色字体展示，并且使用运行模式和开发模式区分;提示信息使用ex.getMessage获取
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static void dealException(Exception ex, PageContext pageContext) throws JspException {
		TagSupportUtil.dealException(ex, ex.getMessage(), pageContext);
	}

	/**
	 * 换行连接字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static StringBuffer appendln(StringBuffer strBF, String str) {
		return strBF.append(str).append("\r\n");
	}

	/**
	 * 从PageContext获取绝对路径
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-2
	 * @since V1.0
	 */
	public static String getBasePathFromPageContext(PageContext pageContext) {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		return TagSupportUtil.getBasePathFromRequest(request);
	}

	/**
	 * 从Request获取绝对路径
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-2
	 * @since V1.0
	 */
	public static String getBasePathFromRequest(HttpServletRequest request) {
		String basePath = GlobalVars.SYS_BASE_PATH;
		if ("".equals(basePath)) {
			String scheme = request.getScheme();
			int port = request.getServerPort();
			if (("HTTP".equalsIgnoreCase(scheme) && 80 == port)
					|| ("HTTPS".equalsIgnoreCase(scheme) && 443 == port)) {
				// HTTP协议，80端口；HTTPS协议端口443的basePath不再进行拼接端口号
				basePath = scheme + "://" + request.getServerName()
						+ request.getContextPath() + "/";
			} else {
				basePath = request.getScheme() + "://"
						+ request.getServerName() + ":"
						+ request.getServerPort() + request.getContextPath()
						+ "/";
			}
		}
		return basePath;
	}

	/**
	 * 附加引导属性的工具方法
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-9-6
	 * @since V1.0
	 */
	public static void appendIntroAttr(StringBuffer strBF, int introSetp,
			String introContent, String introPosition) throws AppException {
		if (strBF == null) {
			strBF = new StringBuffer();
		}
		if (introSetp > 0) {
			strBF.append(" data-step=\"").append(introSetp).append("\"");
			if (StringUtil.chkStrNull(introContent)) {
				throw new AppException("渲染出错，请检查：introSetp设置，而introContent未设置。");
			}
			strBF.append(" data-intro=\"").append(introContent).append("\"");
			if (!StringUtil.chkStrNull(introPosition)) {
				strBF.append(" data-position=\"")
					.append(introPosition)
					.append("\"");
			}
		}
	}

	/**
	 * 附加引导属性的工具方法
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-9-6
	 * @since V1.0
	 */
	public static String appendIntroAttr(int introSetp, String introContent,
			String introPosition) throws AppException {
		StringBuffer strBF = new StringBuffer();
		TagSupportUtil.appendIntroAttr(strBF, introSetp, introContent, introPosition);
		return strBF.toString();
	}
}
