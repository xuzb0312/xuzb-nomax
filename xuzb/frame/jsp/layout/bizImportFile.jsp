<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String basePath = GlobalVars.SYS_BASE_PATH;
	if ("".equals(basePath)) {
		basePath = request.getScheme() + "://"
				+ request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath()
				+ "/";
	}
%>
<%--  业务系统的外围js,样式等引入，放置到该引入文件中 --%>
