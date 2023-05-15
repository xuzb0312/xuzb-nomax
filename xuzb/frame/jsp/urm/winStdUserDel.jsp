<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="删除考生信息" name="formPwd" rowcount="3">
		<ef:text value="你确定要删除此考生吗？"/>
	</ef:form>
	<ef:buttons>
		<ef:button value="确定" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("pwd").focus();
	}

	//保存  确定删除
	function btnSaveClick() {
		var url = new URL("urm.do", "saveStdUserDel");
		url.addForm("formPwd");
		url.addPara("ryid","<%=(String)request.getAttribute("ryid")%>");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功。");
			closeWindow(true);
		}
	}
</script>