<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formEmail" title="发送电子邮件">
		<ef:textinput name="address" label="收件人地址" required="true" colspan="6"
			readonly="true" value='<%=(String)request.getAttribute("address") %>' />
		<ef:textinput name="subject" label="邮件主题" required="true" colspan="6" />
		<ef:ueditor name="content" label="邮件内容" required="true" colspan="6"
			height="270" />
	</ef:form>
	<ef:buttons>
		<ef:button value="发送" name="btnSend" onclick="btnSendClick();"
			iconCls="icon-email-go"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("subject").focus();
	}

	//发送邮件
	function btnSendClick() {
		if (!getObject("formEmail").chkFormData(true)) {
			return;
		}

		getObject("btnSend").disable();
		var url = new URL("taglib.do", "sendEmail4TextInput");
		url.addForm("formEmail");
		AjaxUtil.asyncBizRequest(url, function(data) {
			alert("发送成功！");
			closeWindow(true);
		});
	}
</script>
