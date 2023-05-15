<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="密码重置" name="formPwd" rowcount="3">
		<ef:textinput name="pwd" label="密码" required="true" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("pwd").focus();
	}

	//保存
	function btnSaveClick() {
		var form = get("formPwd");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("urm.do", "saveSysUserPwdReset");
		url.addForm("formPwd");
		url.addPara("yhid","<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("重置成功。");
			closeWindow(true);
		}
	}
</script>