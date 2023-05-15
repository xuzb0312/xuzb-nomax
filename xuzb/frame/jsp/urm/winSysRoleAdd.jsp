<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="角色信息" name="formRole" rowcount="6">
		<ef:textinput name="jsmc" label="角色名称" required="true" />
		<ef:textinput name="bz" label="备注" colspan="4" />
		<ef:blank />
		<ef:checkboxList name="tspzbr" rowcount="1" value="1" colspan="5">
			<ef:data key="1" value="同时将该角色配置给自己（超级管理员不生效）。" />
		</ef:checkboxList>
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>

<script type="text/javascript">
	function onLoadComplete() {
		getObject("jsmc").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formRole").clear();
		getObject("jsmc").focus();
	}

	//保存
	function btnSaveClick() {
		var form = getObject("formRole");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("urm.do", "saveSysRoleAdd");
		url.addForm("formRole");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(new HashMap(data));
		}
	}
</script>