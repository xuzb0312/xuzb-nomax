<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formCode" title="CODE信息" rowcount="4">
		<ef:textinput name="dmbh" label="代码编号" required="true" />
		<ef:textinput name="dmmc" label="代码名称" required="true" />
		<ef:textinput name="dmsm" label="代码说明" colspan="4" />
		<ef:blank />
		<ef:checkboxList name="pzbd" value="1" rowcount="1" colspan="3">
			<ef:data key="1" value="将该CODE明细信息一并配置到本地(DBID)" />
		</ef:checkboxList>
	</ef:form>
	<ef:queryGrid name="gridcode" title="CODE明细" edit="true" height="6"
		editType="smart">
		<ef:columnText name="code" label="CODE代码" width="10" />
		<ef:columnText name="content" label="含义" width="12" />
		<ef:columnText name="sm" label="说明" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("dmbh").focus();
	}
	//清空
	function btnClearClick() {
		getObject("formCode").clear();
		getObject("gridcode").clear();
		getObject("dmbh").focus();
	}
	//保存
	function btnSaveClick() {
		var form = getObject("formCode");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveCodeListAdd");
		url.addForm("formCode");
		url.addQueryGridAllData("gridcode");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(getObject("dmbh").getValue());
		}
	}
</script>