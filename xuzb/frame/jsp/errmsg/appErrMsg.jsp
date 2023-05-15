<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%@ page import="com.grace.frame.util.StringUtil"%>
<%
	String massage = (String) request.getAttribute("message");
	String ex = (String) request.getAttribute("exception");
	int width = 400;
	int height = 150;
	if (GlobalVars.DEBUG_MODE) {
		width = 850;
		height = 500;
	}
	//唯一uiid
	String err_uiid = StringUtil.getUUID();

	out.println(GlobalVars.ERR_MSG_SIGN_WORDS_4_JS_DIS);
	out.println("<div id =\"__app_err_window_"
			+ err_uiid
			+ "\" class=\"easyui-window\" title=\"系统出现异常 【请联系开发人员解决！】\" style=\"width:"
			+ width
			+ "px;height:"
			+ height
			+ "px;padding:8px;color:red;overflow:auto;line-height:1.5;word-break:break-all;word-wrap:break-word;\">");
	out.println(massage + " 【请联系开发人员解决！】");
	if (GlobalVars.DEBUG_MODE) {
		out.println("<hr style=\"border-width:1px;border-color:#d2d2d2;\"/>");
		out.println("<pre>" + ex + "</pre>");
	}
	out.println("</div>");
	out.println("<script type=\"text/javascript\">");
	out.println("try{");
	out.println(" $('#__app_err_window_"
			+ err_uiid
			+ "').window({shadow:false,modal:false,closed:false,iconCls:'icon-exclamation',minimizable:false,collapsible:false,resizable:false,maximizable:false,onClose:function(){$('#__app_err_window_"
			+ err_uiid + "').window('destroy');}});");
	out.println("}catch(oE){");
	out.println(" alert('" + massage.replaceAll("\r|\n|\t|\'|\"", "")
			+ " 【请联系开发人员解决！】');");
	out.println(" console.log('ERR-0:插件提示出错：'+oE.message);");
	out.println(" console.log('ERR-1:系统错误信息：'+$('#__app_err_window_"
			+ err_uiid + "').text());");
	out.println("}");
	out.println("</script>");
%>