<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="CODE信息" name="formcode" dataSource="codelist"
		rowcount="4">
		<ef:textinput name="dmbh" label="代码编号" readonly="true" required="true" />
		<ef:textinput name="dmmc" label="代码名称" required="true" />
		<ef:textinput name="dmsm" label="代码说明" colspan="4" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//保存方法
	function btnSaveClick() {
		var form = getObject("formcode");
		if (!form.chkFormData(true)) {
			return;
		}

		var url = new URL("debug.do", "saveCodeListModify");
		url.addForm("formcode");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>