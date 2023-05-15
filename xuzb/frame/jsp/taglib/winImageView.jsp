<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String imagesrc = (String) request.getAttribute("imagesrc");
%>
<ef:body>
	<ef:layout>
		<ef:centerLayoutPanel>
			<div style="text-align: center; padding: 5px;">
				<img alt="图片查看" src="<%=imagesrc%>">
			</div>
		</ef:centerLayoutPanel>
		<ef:bottomLayoutPanel height="45">
			<ef:buttons></ef:buttons>
		</ef:bottomLayoutPanel>
	</ef:layout>
</ef:body>