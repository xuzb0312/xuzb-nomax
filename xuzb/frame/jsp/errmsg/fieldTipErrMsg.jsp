<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String errtext = (String) request.getAttribute("errtext");
	String fieldid = (String) request.getAttribute("fieldid");
	if (errtext != null && !"".equalsIgnoreCase(errtext)
			&& fieldid != null && !"".equalsIgnoreCase(fieldid)) {
		errtext = errtext.replaceAll("\'", "【");
		errtext = errtext.replaceAll("\"", "】");
		errtext = errtext.replaceAll("\\n", "\\\\\\\\n");
		errtext = errtext.replaceAll("\\t", "\\\\\\\\t");
		fieldid = fieldid.replaceAll("\'", "");
		fieldid = fieldid.replaceAll("\"", "");
		fieldid = fieldid.replaceAll("\\n", "");
		fieldid = fieldid.replaceAll("\\t'", "");
		out.println(GlobalVars.ERR_MSG_SIGN_WORDS_4_JS_DIS);
		out.println("<script type=\"text/javascript\">");
		if (errtext != null && !"".equalsIgnoreCase(errtext)) {
			out.println("alert(\"" + errtext + "\");");
		}
		if (fieldid != null && !"".equalsIgnoreCase(fieldid)) {
			out.println("try{");
			out.println("get(\"" + fieldid + "\").focus();");//首先框架置焦点
			out.println("}catch(oE){");
			out.println("try{");
			out.println("$(\"#" + fieldid + "\").focus();");//异常则使用jquery置焦点
			out.println("}catch(oE){}");
			out.println("}");
		}
		out.println("</script>");
	}
%>
