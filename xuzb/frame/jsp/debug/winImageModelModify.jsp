<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formImageModel" title="图片模板信息" rowcount="5"
		dataSource="dmimagemodel">
		<ef:hiddenInput name="mbid" />
		<ef:textinput name="mbbh" label="模板编号" colspan="2" required="true" />
		<ef:textinput name="mbmc" label="模板名称" colspan="2" required="true" />
		<ef:textarea name="mbms" label="模板描述" colspan="4" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("formImageModel").focus();
	}

	function btnSaveClick() {
		if (!getObject("formImageModel").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveImageModelModify");
		url.addForm("formImageModel");
		AjaxUtil.syncBizRequest(url, function(data) {
			alert("保存成功");
			closeWindow(true);
		});
	}
</script>