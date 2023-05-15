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
<%-- 框架使用的js,css引入，对于业务系统的js,css引入不允许放置到此处，框架升级会直接覆盖此文件(轻量级引入) --%>
<%--系统的图标 --%>
<link rel="icon" href="./<%=GlobalVars.APP_ICON%>" type="image/x-icon" />
<link rel="shortcut icon" href="./<%=GlobalVars.APP_ICON%>">
<link rel="Bookmark" href="./<%=GlobalVars.APP_ICON%>">

<%--系统的样式代码css --%>
<link rel="stylesheet" type="text/css" href="./frame/css/base.css?v=1.0" />

<%--系统的JS代码 --%>
<script type="text/javascript"
	src="./frame/js/jquery-2.2.4.min.js?v=2.2.4"></script>
<script type="text/javascript" src="./frame/plugins/json/json2.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/md5/md5.min.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/lightweightUtil.min.js?v=1.0"></script>

<%--系统级的常量，从java转换到javascript--%>
<script type="text/javascript">
	var GlobalVars = __initSysGlobalVars();
	function __initSysGlobalVars() {
		var map = new HashMap();
		var debugmode = '<%=GlobalVars.DEBUG_MODE%>';
		map.put("DEBUG_MODE",("true" == debugmode));
		var basePath = '<%=basePath%>';
		map.put("BASE_PATH", basePath);
		return map.values;
	};
</script>