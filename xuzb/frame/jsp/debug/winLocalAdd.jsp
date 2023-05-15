<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="本地化配置" name="formLocal">
		<ef:textinput name="bzjm" colspan="6" required="true" label="标准件名"
			onsearchclick="bzjmSearchClick();" />
		<ef:textinput name="bdhm" colspan="6" required="true" label="本地化名" />
		<ef:multiDropdownList name="jbjgid" dsCode="dsjbjg" required="true"
			colspan="6" label="经办机构"></ef:multiDropdownList>
		<ef:textinput name="bdhsm" colspan="6" label="本地化说明" />
		<ef:blank />
		<ef:text color="red" colspan="5"
			value="注：对于fw.local_doc中不存在的本地化信息自动增加（或修改）。" />
	</ef:form>
	<ef:buttons>
		<ef:button name="btnAdd" value="新增" onclick="btnAddClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("bzjm").focus();
	}
	//搜索
	function bzjmSearchClick() {
		var url = new URL("debug.do", "fwdSearchLocalDoc");
		url.addPara("bzjm", getObject("bzjm").getValue());
		openWindow("选择", url, "normal", actionAfterSearch);
	}
	function actionAfterSearch(data) {
		if (!chkObjNull(data)) {
			getObject("formLocal").setMapData(data);
			getObject("jbjgid").focus();
		} else {
			getObject("bzjm").focus();
		}
	}

	//新增
	function btnAddClick() {
		if (!getObject("formLocal").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveLocalConfigAdd");
		url.addForm("formLocal");
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (data) {
			actionAfterSave();
		}
	}
	function actionAfterSave() {
		alert("保存成功");
		var map = getObject("formLocal").getMapData();
		closeWindow(map);
	}

	//清空
	function btnClearClick() {
		getObject("formLocal").clearData();
		getObject("btnAdd").enable();
		getObject("bzjm").focus();
	}
</script>