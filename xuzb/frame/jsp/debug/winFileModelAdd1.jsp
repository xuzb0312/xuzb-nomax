<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="文件信息" name="formFile" rowcount="4">
		<ef:textinput name="wjbs" label="文件标识" required="true" />
		<ef:textinput name="wjmc" label="文件名称" required="true" />
		<ef:fileBox name="wjnr" label="文件内容" autoUpload="true" required="true"
			sizeLimit="51200" colspan="4" onSelectFile="selectFileAction"/>
		<ef:textinput name="bz" label="备注" colspan="4" />
	</ef:form>
	<ef:queryGrid name="gridPrintImg" title="PDF打印参数(图片)" height="5"
		exportFile="false" edit="true" editType="smart">
		<ef:columnText name="csmc" label="参数名称" />
		<ef:columnText name="tpwz_x" label="图片绝对位置(X)" dataType="number"
			mask="#######0" />
		<ef:columnText name="tpwz_y" label="图片绝对位置(Y)" dataType="number"
			mask="#######0" />
		<ef:columnText name="tpsf" label="图片缩放%" dataType="number"
			mask="#######0" />
	</ef:queryGrid>
	<ef:form border="false">
		<ef:text color="red" value="注：PDF打印参数，只对使用精确打印的PDF模板文件参数进行维护。" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("wjbs").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formFile").clear();
		getObject("wjbs").focus();
	}

	//保存
	function btnSaveClick() {
		if (!getObject("formFile").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveFileModelAdd1");
		url.addForm("formFile");
		url.addQueryGridAllData("gridPrintImg");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功");
			closeWindow(getObject("wjbs").getValue());
		}
	}

	//选择文件后的操作
	function selectFileAction(fileName) {
		if (chkObjNull(fileName)) {
			return;
		}

		var arrFileName = fileName.split(".");
		if (arrFileName.length > 2) {
			getObject("wjbs").setValue(arrFileName[0]);
			getObject("wjmc").setValue(arrFileName[1]);
		}
	}
</script>