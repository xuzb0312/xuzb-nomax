<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="轮询文档信息" name="formpollingdoc" dataSource="formpolling">
		<ef:textinput name="lxmc" label="轮询名称" required="true"
			validType="engNum" colspan="6" readonly="true" />
		<ef:textinput name="lxbiz" label="轮询Biz" required="true" colspan="6" />
		<ef:textinput name="lxff" label="轮询方法" required="true" colspan="6" />
		<ef:textinput name="lxcs" label="轮询参数" colspan="6" />
		<ef:textinput name="sm" label="轮询说明" colspan="6" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>

<script type="text/javascript">
	//保存方法
	function btnSaveClick() {
		var form = getObject("formpollingdoc");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "savePollingDocModify");
		url.addForm("formpollingdoc");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>