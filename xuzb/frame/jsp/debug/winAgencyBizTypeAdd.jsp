<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="业务类型配置" rowcount="6" name="formtype">
		<ef:dropdownList name="dbid" dsCode="dsdbid" label="DBID"
			required="true" colspan="3"></ef:dropdownList>
		<ef:textinput name="ywlb" label="业务类别" required="true" colspan="3" />
		<ef:multiSelectBox name="jbjgids" dsCode="dsjbjg" label="经办机构"
			required="true"></ef:multiSelectBox>
		<ef:textinput name="bz" label="备注" colspan="6" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//加载完成后执行的
	function onLoadComplete() {
		getObject("dbid").focus();
	}

	//保存
	function btnSaveClick() {
		if (!getObject("formtype").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveAgencyBizTypeAdd");
		url.addForm("formtype");
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(true);
		}
	}

	//清空
	function btnClearClick() {
		getObject("formtype").clear();
		getObject("dbid").focus();
	}
</script>