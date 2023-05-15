<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formFile" title="选择WORD文件（.doc;.zip-将word另存为htm后的压缩文件）"
		rowcount="4">
		<ef:fileBox name="word" label="文件" required="true" colspan="4"
			fileDesc="Word文件" sizeLimit="20480" fileType="*.doc;*.zip"
			onSelectFile="btnUploadClick" />
		<ef:text color="red" align="center" colspan="4"
			value="注：zip格式是将word另存为筛选后的htm的压缩后文件" />
	</ef:form>
	<ef:buttons>
		<ef:button value="上传" iconCls="icon-page-white-word"
			onclick="btnUploadClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("word").focus();
	}

	//上传操作
	function btnUploadClick() {
		if (!getObject("formFile").chkFormData(true)) {
			return;
		}
		var url = new URL("taglib.do", "parseUploadWord");
		url.addForm("formFile");
		AjaxUtil.syncBizRequest(url, function(data) {
			if (chkObjNull(data)) {
				return;
			}
			closeWindow(data);
		});
	}
</script>
