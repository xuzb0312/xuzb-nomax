<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.grace.frame.util.DataSet"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<style>
ul {
	list-style-type: none;
	padding: 0;
	margin: 0;
}

a {
	text-decoration: none;
}

ul {
	width: 1045px;
	margin: 0 auto;
	border-left: 1px solid #95B8E7;
	border-top: 1px solid #95B8E7;
	margin-bottom: 20px;
	overflow: hidden;
}

ul li {
	float: left;
	width: 208px;
	height: 30px;
	border-right: 1px solid #95B8E7;
	border-bottom: 1px solid #95B8E7;
}

ul li a {
	margin: 7px 0 0 5px;
	display: inline-block;
	color: #992222;
}
</style>
<ef:body>
	<ef:layout>
		<ef:topLayoutPanel height="50" border="false">
			<ef:form rowcount="6" border="false">
				<ef:textinput name="iconName" label="iconCls=" colspan="5" />
			</ef:form>
		</ef:topLayoutPanel>
		<ef:centerLayoutPanel border="false">
			<ul>
				<%
					DataSet dsIcon = (DataSet) request
										.getAttribute("dsicon");
								for (int i = 0, n = dsIcon.size(); i < n; i++) {
									String icon = dsIcon.getString(i, "icon");
									icon = "icon-" + icon;
									String showIcon = icon;
									if (showIcon.length() > 30) {
										showIcon = showIcon.substring(0, 28) + "...";
									}
									String clickName = "setIconName('" + icon + "');";
				%>
				<li>
					<div class="<%=icon%>"
						style="width: 16px; height: 16px; float: left; margin-top: 7px; margin-left: 3px;"></div>
					<a href="javascript:void(0);" onclick="<%=clickName%>"
						title="<%=icon%>"><%=showIcon%></a>
				</li>
				<%
					}
				%>
			</ul>
		</ef:centerLayoutPanel>
	</ef:layout>
</ef:body>
<script type="text/javascript">
	function setIconName(name) {
		getObject("iconName").setValue(name);
	}
</script>