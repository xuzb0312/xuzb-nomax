<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formList" title="批注信息" dataSource="dmlist">
		<ef:textinput name="pzbh" label="批注编号" readonly="true" />
		<ef:textinput name="pzmc" label="批注名称" required="true" readonly="true"
			colspan="4" />
		<ef:textinput name="pzsm" label="批注说明" readonly="true" colspan="6" />
	</ef:form>
	<ef:queryGrid name="gridConfig" title="批注配置" edit="true"
		editType="smart" dataSource="dsconfig" exportFile="false" height="8">
		<ef:columnText name="pznr" label="批注内容" width="39" />
		<ef:columnText name="xh" label="序号" width="4" mask="######0"
			dataType="number" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="保存" name="btnSaveConfig"
			onclick="btnSaveConfigClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function btnSaveConfigClick() {
		var url = new URL("taglib.do", "saveNoteConfigModify");
		url.addPara("pzbh", getObject("pzbh").getValue());
		url.addQueryGridAllData("gridConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>
