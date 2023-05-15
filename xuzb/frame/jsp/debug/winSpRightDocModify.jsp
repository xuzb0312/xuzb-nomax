<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="特殊权限文档信息" name="formSp" dataSource="formsprightdoc">
		<ef:textinput name="tsqxid" label="特殊权限ID" required="true" colspan="6"
			readonly="true" />
		<ef:textinput name="tsqxmc" label="特殊权限名称" required="true" colspan="6" />
		<ef:textinput name="bz" label="备注" colspan="6" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>

<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("tsqxid").focus();
	}
	//保存方法
	function btnSaveClick() {
		var form = getObject("formSp");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveSpRightDocModify");
		url.addForm("formSp");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>