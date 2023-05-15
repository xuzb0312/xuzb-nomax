<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String size = (String) request.getAttribute("size");
%>
<ef:body>
	<ef:form rowcount="4" border="false">
		<ef:textinput name="qshh" label="起始行号" dataType="number"
			mask="###############0" value="1" required="true" />
		<ef:textinput name="zzhh" label="终止行号" dataType="number"
			mask="###############0" value="1" required="true" />
		<ef:text colspan="4" color="red" value="注：部分选择时，系统根据数据初始加载的顺序进行选择。"
			align="center" />
	</ef:form>
	<ef:buttons>
		<ef:button value="选择" iconCls="icon-ok" onclick="btnOkClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	var size = <%=size%>;
	function onLoadComplete() {
		getObject("zzhh").setValue(size);
		getObject("zzhh").focus();
	}
	function btnOkClick() {
		var qshh = getObject("qshh").getValue();
		var zzhh = getObject("zzhh").getValue();
		qshh = qshh - 1;
		zzhh = zzhh - 1;
		if (qshh < 0) {
			qshh = 0;
		}
		if (zzhh >= size) {
			zzhh = size - 1;
		}
		var data = {
			qshh : qshh,
			zzhh : zzhh
		};
		closeWindow(data);
	}
</script>
