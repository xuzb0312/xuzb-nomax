<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String id = (String) request.getAttribute("id");
	String name = (String) request.getAttribute("name");
	String con_id = (String) request.getAttribute("con_id");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<script type="text/javascript">
	var __data = {id:"<%=id%>",name:"<%=name%>"};
	var con_id = "<%=con_id%>";
	window.parent.$("#" + con_id).window("close");
</script>
	</head>
	<body>
	</body>
</html>