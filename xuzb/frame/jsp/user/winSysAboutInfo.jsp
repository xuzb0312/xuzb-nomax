<%@ page language="java" contentType="text/html; charset=gbk"%>
<%@page import="java.util.Properties"%>
<%@page import="com.grace.frame.constant.GlobalVars"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	Properties props = System.getProperties();
%>
<ef:body>
	<ef:form title="ϵͳ��Ϣ" rowcount="4">
		<%
			if (GlobalVars.DEBUG_MODE) {
		%>
		<ef:textinput name="appname" label="ϵͳ����"
			value='<%=GlobalVars.APP_NAME%>' readonly="true" />
		<ef:textinput name="appversion" label="ϵͳ�汾��"
			value='<%=GlobalVars.APP_VERSION%>' readonly="true" />
		<ef:textinput name="osname" label="����ϵͳ����"
			value='<%=props.getProperty("os.name")%>' readonly="true" />
		<ef:textinput name="arch" label="����ϵͳ����"
			value='<%=props.getProperty("os.arch")%>' readonly="true" />

		<ef:textinput name="version" label="����ϵͳ�汾"
			value='<%=props.getProperty("os.version")%>' readonly="true" />
		<ef:textinput name="javaversion" label="Java���л����汾"
			value='<%=props.getProperty("java.version")%>' readonly="true" />
		<%
			} else {
		%>
		<ef:textinput name="appname" label="ϵͳ����"
			value='<%=GlobalVars.APP_NAME%>' readonly="true" colspan="4" />
		<ef:textinput name="appversion" label="ϵͳ�汾��"
			value='<%=GlobalVars.APP_VERSION%>' readonly="true" colspan="4" />
		<%
			}
		%>
	</ef:form>
	<ef:form border="false" tableLine="false">
		<ef:text
			value="Copyright &copy;2018 ŵ���ſƼ�(nomax.cn) All Rights Reserved"
			align="center" />
		<ef:text
			value="��ַ��<a href='http://www.nomax.cn' target='_blank'>http://www.nomax.cn</a>&nbsp;&nbsp;����:<a href='mailto:hr@nomax.top'>hr@nomax.top</a>"
			align="center" />
	</ef:form>
</ef:body>
