<%@ page language="java" contentType="text/html; charset=gbk"%>
<%@page import="java.util.Properties"%>
<%@page import="com.grace.frame.constant.GlobalVars"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	Properties props = System.getProperties();
%>
<ef:body>
	<ef:form title="系统信息" rowcount="4">
		<%
			if (GlobalVars.DEBUG_MODE) {
		%>
		<ef:textinput name="appname" label="系统名称"
			value='<%=GlobalVars.APP_NAME%>' readonly="true" />
		<ef:textinput name="appversion" label="系统版本号"
			value='<%=GlobalVars.APP_VERSION%>' readonly="true" />
		<ef:textinput name="osname" label="操作系统名称"
			value='<%=props.getProperty("os.name")%>' readonly="true" />
		<ef:textinput name="arch" label="操作系统构架"
			value='<%=props.getProperty("os.arch")%>' readonly="true" />

		<ef:textinput name="version" label="操作系统版本"
			value='<%=props.getProperty("os.version")%>' readonly="true" />
		<ef:textinput name="javaversion" label="Java运行环境版本"
			value='<%=props.getProperty("java.version")%>' readonly="true" />
		<%
			} else {
		%>
		<ef:textinput name="appname" label="系统名称"
			value='<%=GlobalVars.APP_NAME%>' readonly="true" colspan="4" />
		<ef:textinput name="appversion" label="系统版本号"
			value='<%=GlobalVars.APP_VERSION%>' readonly="true" colspan="4" />
		<%
			}
		%>
	</ef:form>
	<ef:form border="false" tableLine="false">
		<ef:text
			value="Copyright &copy;2018 诺码信科技(nomax.cn) All Rights Reserved"
			align="center" />
		<ef:text
			value="网址：<a href='http://www.nomax.cn' target='_blank'>http://www.nomax.cn</a>&nbsp;&nbsp;邮箱:<a href='mailto:hr@nomax.top'>hr@nomax.top</a>"
			align="center" />
	</ef:form>
</ef:body>
