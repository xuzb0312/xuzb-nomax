<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="CODE代码" name="formCode" rowcount="4">
		<ef:textinput name="code" label="CODE代码" required="true" />
		<ef:textinput name="content" label="代码含义" required="true" />
		<ef:textinput name="sm" label="说明" colspan="4" />
		<ef:blank />
		<ef:checkboxList name="pzbd" value="1" rowcount="1" colspan="3">
			<ef:data key="1" value="将该CODE明细信息一并配置到本地(DBID)" />
		</ef:checkboxList>
		<ef:buttons>
			<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
</ef:body>
<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("code").focus();
	}

	//保存方法
	function btnSaveClick() {
		var form = getObject("formCode");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveCodeDocAdd");
		url.addPara("dmbh", "<%=(String)request.getAttribute("dmbh")%>");
		url.addForm("formCode");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(true);
		}
	}
	//清空
	function btnClearClick() {
		getObject("formCode").clear();
		getObject("code").focus();
		
	}
</script>