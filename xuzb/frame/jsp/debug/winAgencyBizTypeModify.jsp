<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="业务类型配置" rowcount="6" name="formtype"
		dataSource="typeinfo">
		<ef:dropdownList name="dbid" dsCode="dsdbid" label="DBID"
			required="true" readonly="true" colspan="3"></ef:dropdownList>
		<ef:textinput name="ywlb" label="业务类别" required="true" readonly="true"
			colspan="3" />
		<ef:multiSelectBox name="jbjgids" dsCode="dsjbjg" label="经办机构"></ef:multiSelectBox>
		<ef:textinput name="bz" label="备注" colspan="6" />
		<ef:blank />
		<ef:text color="red" colspan="5" value="注：不选择经办机构信息，则将删除业务类型配置" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//加载完成后执行的
	function onLoadComplete() {
		getObject("jbjgids").focus();
	}

	//保存
	function btnSaveClick() {
		if (!getObject("formtype").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveAgencyBizTypeModify");
		url.addForm("formtype");
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>