<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="Hibernate加密串生成" rowcount="6" name="formY">
		<ef:textinput name="name" label="用户名" />
		<ef:textinput name="pwd" label="连接密码" />
		<ef:buttons colspan="2">
			<ef:button value="生成" iconCls="icon-key-go" onclick="btnGenClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:form title="加密信息" rowcount="6" name="formJ">
		<ef:textinput name="name" label="用户名" readonly="true" />
		<ef:textinput name="pwd" label="连接密码" readonly="true" />
	</ef:form>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("name").focus();
	}

	function btnGenClick() {
		var url = new URL("debug.do", "genHibernateEncodeStr");
		url.addForm("formY");
		AjaxUtil.asyncRefreshData(url, "formJ:dmencode");
	}

	function btnClearClick() {
		getObject("formY").clear();
		getObject("formJ").clear();
		getObject("name").focus();
	}
</script>
