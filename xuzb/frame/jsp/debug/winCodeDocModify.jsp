<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="CODE代码" name="formCode" rowcount="4"
		dataSource="docinfo">
		<ef:textinput name="code" label="CODE代码" required="true"
			readonly="true" />
		<ef:textinput name="content" label="代码含义" required="true" />
		<ef:textinput name="sm" label="说明" colspan="4" />
		<ef:hiddenInput name="dmbh" />
		<ef:buttons>
			<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		</ef:buttons>
	</ef:form>
</ef:body>
<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("content").focus();
	}

	//保存方法
	function btnSaveClick() {
		var form = getObject("formCode");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveCodeDocModify");
		url.addForm("formCode");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>