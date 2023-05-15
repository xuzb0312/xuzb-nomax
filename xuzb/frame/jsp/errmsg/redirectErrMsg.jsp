<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String errtext = (String) request.getAttribute("errtext");
	String redirect_url = (String) request.getAttribute("redirect_url");
	if (errtext != null && !"".equalsIgnoreCase(errtext)
			&& redirect_url != null
			&& !"".equalsIgnoreCase(redirect_url)) {
		errtext = errtext.replaceAll("\'", "【");
		errtext = errtext.replaceAll("\"", "】");
		errtext = errtext.replaceAll("\\n", "\\\\\\\\n");
		errtext = errtext.replaceAll("\\t", "\\\\\\\\t");
		redirect_url = redirect_url.replaceAll("\'", "");
		redirect_url = redirect_url.replaceAll("\"", "");
		redirect_url = redirect_url.replaceAll("\\n", "");
		redirect_url = redirect_url.replaceAll("\\t'", "");
		out.println(GlobalVars.ERR_MSG_SIGN_WORDS_4_JS_DIS);
		out.println("<script type=\"text/javascript\">");
		if (errtext != null && !"".equalsIgnoreCase(errtext)) {
			out.println("alert(\"" + errtext + "\");");
		}
		if (redirect_url != null && !"".equalsIgnoreCase(redirect_url)) {
			out.println("top.location.href=\"" + redirect_url + "\";");
		}
		out.println("</script>");
	}
%>
