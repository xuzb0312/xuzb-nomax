<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formList" title="批注信息" dataSource="dmlist">
		<ef:textinput name="pzbh" label="批注编号" readonly="true" />
		<ef:textinput name="pzmc" label="批注名称" required="true" readonly="true"
			colspan="4" />
		<ef:textinput name="pzsm" label="批注说明" readonly="true" colspan="6" />
		<ef:buttons closebutton="false">
			<ef:button value="修改" name="btnModify" onclick="btnModifyClick();"></ef:button>
			<ef:button value="保存" name="btnSave" disabled="true"
				onclick="btnSaveListClick();"></ef:button>
			<ef:button value="取消" name="btnCancel" onclick="btnCancelClick();"
				disabled="true"></ef:button>
			<ef:button value="删除" name="btnDel" onclick="btnDelClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridConfig" title="批注配置" edit="true"
		editType="smart" dataSource="dsconfig" exportFile="false" height="10">
		<ef:columnText name="pznr" label="批注内容" width="39" />
		<ef:columnText name="xh" label="序号" width="4" mask="######0"
			dataType="number" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="保存" name="btnSaveConfig"
			onclick="btnSaveConfigClick();"></ef:button>
		<ef:button value="取消" name="btnCancelTwo" onclick="btnCancelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function btnCancelClick() {
		AjaxUtil.asyncRefreshPage();
	}
	function btnModifyClick() {
		getObject("btnModify").disable();
		getObject("btnSave").enable();
		getObject("btnCancel").enable();
		getObject("btnSaveConfig").disable();
		getObject("btnCancelTwo").disable();

		getObject("pzmc").setReadOnly(false);
		getObject("pzsm").setReadOnly(false);
		getObject("pzmc").focus();
	}

	function btnSaveListClick() {
		var form = getObject("formList");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveNoteListModify");
		url.addForm("formList");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			AjaxUtil.asyncRefreshPage();
		}
	}

	function btnSaveConfigClick() {
		var url = new URL("debug.do", "saveNoteConfigModify");
		url.addPara("pzbh", getObject("pzbh").getValue());
		url.addQueryGridAllData("gridConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			AjaxUtil.asyncRefreshPage();
		}
	}

	function btnDelClick() {
		if (!confirm("确定需要删除该典型批注信息吗？")) {
			return;
		}

		var url = new URL("debug.do", "saveNoteListDel");
		url.addPara("pzbh", getObject("pzbh").getValue());
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功。");
			parent.btnQueryClick();
			parent.getObject("mainpaenl").clear();
		}
	}
</script>
