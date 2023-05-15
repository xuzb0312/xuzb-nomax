<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%@ page import="com.grace.frame.util.DataSet"%>
<%
	DataSet dsimage = (DataSet) request.getAttribute("dsimage");
	String wjmc = (String) request.getAttribute("wjmc");
	if (null == dsimage) {
		dsimage = new DataSet();
	}
	if (null == wjmc) {
		wjmc = "<font color=\"blue\">文件在页面刷新过程中丢失(File missing)！请重新打开页面查看！</font>";
	}
%>
<ef:body>
	<ef:layout>
		<ef:centerLayoutPanel>

			<div style="text-align: center; padding: 5px;">
				<h1><%=wjmc%></h1>
				<%
					for (int i = 0, n = dsimage.size(); i < n; i++) {
									String wjmc_inner = dsimage.getString(i, "wjmc");
									String img = dsimage.getString(i, "img");
				%>
				<img src="<%=img%>" alt="<%=wjmc_inner%>" style="max-width: 90%"/>
				<br />
				<span style="font-weight: bold;"><%=wjmc_inner%></span>
				<div
					style="width: 100%; clear: both; border-bottom: 1px solid #95B8E7; margin: 5px;"></div>
				<%
					}
				%>
			</div>
		</ef:centerLayoutPanel>
		<ef:bottomLayoutPanel height="45">
			<ef:buttons></ef:buttons>
		</ef:bottomLayoutPanel>
	</ef:layout>
</ef:body>