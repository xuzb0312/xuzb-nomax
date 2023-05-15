<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="DBID信息" name="formdbid" dataSource="dbidinfo"
		rowcount="4">
		<ef:textinput name="dbid" label="DBID" required="true" readonly="true" />
		<ef:textinput name="dbmc" label="数据库名称" required="true" />
		<ef:textinput name="appname" label="系统名称(特性化)" colspan="2" />
		<ef:text value="注：地区如果存在特性化名称要求，请配置该项。" colspan="2" tiptag="true" />
		<ef:textinput name="bz" label="备注" colspan="4" />
	</ef:form>
	<ef:buttons>
		<ef:button name="btnAdd" value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("dbmc").focus();
	}

	//新增
	function btnSaveClick() {
		if (!getObject("formdbid").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveDbidInfoModify");
		url.addForm("formdbid");
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (data) {
			alert("保存成功");
			closeWindow(true);
		}
	}
</script>