<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="注销信息" name="formZxxx" rowcount="3">
		<ef:textinput name="zxrq" label="注销日期" dataType="date"
			mask="yyyy-MM-dd" required="true" colspan="3"
			value='<%=(String)request.getAttribute("zxrq") %>'
			sourceMask="yyyyMMdd" />
		<ef:textinput name="zxyy" label="注销原因" colspan="3" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("zxyy").focus();
	}

	//保存
	function btnSaveClick() {
		var form = get("formZxxx");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("urm.do", "saveSysUserDestroy");
		url.addForm("formZxxx");
		url.addPara("yhid","<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("用户注销成功。");
			closeWindow(true);
		}
	}
</script>