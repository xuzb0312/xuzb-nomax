<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="本地文档文档信息" name="formdoc">
		<ef:textinput name="fwmc" label="服务名称" required="true" colspan="6" />
		<ef:textinput name="fwff" label="服务方法" required="true" colspan="6" />
		<ef:textinput name="biz" label="服务BIZ" required="true" colspan="6" />
		<ef:textinput name="bizff" label="服务BIZ方法" required="true" colspan="6" />
		<ef:textinput name="fwsm" label="服务说明" colspan="6" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>

	</ef:buttons>
</ef:body>

<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("fwmc").focus();
	}
	//保存方法
	function btnSaveClick() {
		var form = getObject("formdoc");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveServiceDocAdd");
		url.addForm("formdoc");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(true);
		}
	}
	//清空
	function btnClearClick() {
		getObject("formdoc").clear();
		getObject("fwmc").focus();
	}
</script>