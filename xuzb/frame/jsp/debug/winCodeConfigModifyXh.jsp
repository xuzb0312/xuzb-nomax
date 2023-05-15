<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="设置序号" rowcount="3">
		<ef:textinput name="xh" label="序号" dataType="number" mask="########0"
			value='<%=(String)request.getAttribute("xh") %>' />
	</ef:form>
	<ef:buttons>
		<ef:button value="确定" onclick="btnOKClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("xh").focus();
	}

	function btnOKClick() {
		var xh = getObject("xh").getValue();
		closeWindow(xh);
	}
</script>