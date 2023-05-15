<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="打印格式地区配置" name="formConfig" dataSource="configinfo">
		<ef:hiddenInput name="gsid" />
		<ef:multiSelectBox name="jbjgids" dsCode="dsjbjg" label="经办机构"
			height="17"></ef:multiSelectBox>
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//保存
	function btnSaveClick() {
		var url = new URL("debug.do", "savePrintConfigSet");
		url.addForm("formConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>