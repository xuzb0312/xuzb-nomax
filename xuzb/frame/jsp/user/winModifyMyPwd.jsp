<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="修改密码" rowcount="4" name="formpwd">
		<ef:textinput name="ypwd" label="原始密码" required="true" colspan="4"
			password="true" />
		<ef:textinput name="xpwd" label="新密码" required="true" colspan="4"
			password="true" />
		<ef:textinput name="qrxpwd" label="确认新密码" required="true" colspan="4"
			password="true" />
	</ef:form>
	<ef:buttons>
		<ef:button name="btnModifyPwd" value="修改密码"
			onclick="btnModifyPwdClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete(){
		getObject("ypwd").focus();
	}

	function btnModifyPwdClick() {
		if (!getObject("formpwd").chkFormData(true)) {
			return;
		}
		var ypwd = getObject("ypwd").getValue();
		var xpwd = getObject("xpwd").getValue();
		var qrxpwd = getObject("qrxpwd").getValue();
		if (xpwd != qrxpwd) {
			alert("输入的两次新密码不一致，请重新输入！");
			getObject("xpwd").focus();
			return;
		}
		var url = new URL("login.do", "modifyMyPwd");
		url.addPara("ypwd", ypwd);
		url.addPara("xpwd", xpwd);
		AjaxUtil.asyncBizRequest(url, actionAfterModifyPwd);
	}
	function actionAfterModifyPwd() {
		alert("密码修改成功！");
		closeWindow();
	}
</script>
