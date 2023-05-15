<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="打印格式" name="formModel" rowcount="4">
		<ef:fileBox name="dygs" label="打印格式" required="true" colspan="4"
			autoUpload="true" fileDesc="打印格式(pft)文件或zip文件" fileType="*.pft;*.zip"
			sizeLimit="51200" />
		<ef:text color="blue" value="注：该功能只允许上传pft格式的文件，多个文件上传需压缩为zip包进行批量上传"
			align="center" colspan="4" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("dygs").openSelectFileWindow4H5();
	}

	function btnSaveClick() {
		if (!getObject("formModel").chkFormData(true)) {
			return;
		}

		var url = new URL("debug.do", "uploadPrintModelFile");
		url.addForm("formModel");
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("保存成功。");
			var dataMap = new HashMap(data);
			closeWindow(dataMap.get("gslxbh"));
		}
	}
</script>
