<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String message = null;
	if (pageContext.findAttribute("message") != null) {
		message = pageContext.findAttribute("message").toString();
	}
	if (message != null && !"".equalsIgnoreCase(message)) {
		message = message.replaceAll("\"", "'");
		message = message.replaceAll("\n", "");
		message = message.replaceAll("\r", "");
		out.println(GlobalVars.ERR_MSG_SIGN_WORDS_4_JS_DIS);
		out.println("<script type=\"text/javascript\">");
		out.println("try{");
		out.println("$.messager.alert(\"操作提示\", \"" + message
				+ "\", \"warning\");");
		out.println("}catch(oE){");
		out.println(" alert('" + message.replaceAll("<br>", "") + "');");
		out.println("}");
		out.println("</script>");
	}
%>
