<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formList" title="批注信息">
		<ef:textinput name="pzbh" label="批注编号" required="true" />
		<ef:textinput name="pzmc" label="批注名称" required="true" colspan="4" />
		<ef:textinput name="pzsm" label="批注说明" colspan="6" />
	</ef:form>
	<ef:queryGrid name="gridConfig" title="批注配置" edit="true"
		editType="smart" height="8" exportFile="false">
		<ef:columnText name="pznr" label="批注内容" width="39" />
		<ef:columnText name="xh" label="序号" width="4" mask="######0"
			dataType="number" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("pzbh").focus();
	}

	function btnSaveClick() {
		var form = getObject("formList");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveNoteListAdd");
		url.addForm("formList");
		url.addQueryGridAllData("gridConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(getObject("pzbh").getValue());
		}
	}

	function btnClearClick() {
		getObject("formList").clear();
		getObject("gridConfig").clear();
		getObject("pzbh").focus();
	}
</script>