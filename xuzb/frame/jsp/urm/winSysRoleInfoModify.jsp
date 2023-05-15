<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="角色信息" name="formRole" rowcount="6"
		dataSource="roleinfo">
		<ef:hiddenInput name="jsid" />
		<ef:textinput name="jsmc" label="角色名称" required="true" />
		<ef:textinput name="bz" label="备注" colspan="4" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>

<script type="text/javascript">
	function onLoadComplete() {
		getObject("jsmc").focus();
	}
	//保存
	function btnSaveClick() {
		var form = getObject("formRole");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("urm.do", "saveSysRoleModify");
		url.addForm("formRole");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>