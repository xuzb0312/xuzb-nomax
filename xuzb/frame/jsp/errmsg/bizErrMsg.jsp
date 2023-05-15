<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String message = null;
	if (pageContext.findAttribute("message") != null) {
		message = pageContext.findAttribute("message").toString();
	}
	if (message != null && !"".equalsIgnoreCase(message)) {
		message = message.replaceAll("\'", "【");
		message = message.replaceAll("\"", "】");
		message = message.replaceAll("\\n", "\\\\\\\\n");
		message = message.replaceAll("\\t", "\\\\\\\\t");
		out.println(GlobalVars.ERR_MSG_SIGN_WORDS_4_JS_DIS);
		out.println("<script type=\"text/javascript\">");
		out.println("alert(\"" + message + "\");");
		out.println("</script>");
	}
%>
