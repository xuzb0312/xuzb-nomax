<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="文件信息" name="formFile" rowcount="6" dataSource="dmfile">
		<ef:textinput name="wjbs" label="文件标识" readonly="true" />
		<ef:textinput name="wjmc" label="文件名称" required="true" />
		<ef:textinput name="wjgs" label="文件格式" readonly="true" />
		<ef:textinput name="cjsj" label="创建时间" readonly="true" dataType="date"
			mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss" />
		<ef:textinput name="bz" label="备注" colspan="4" />
		<ef:fileBox name="wjnr" label="文件内容" autoUpload="true"
			sizeLimit="51200" colspan="4" />
		<ef:text color="red" align="center" value="注：不选择文件，数据库的文件不进行变更"
			colspan="2" />
	</ef:form>
	<ef:queryGrid name="gridPrintImg" title="PDF打印参数(图片)" height="5"
		exportFile="false" edit="true" editType="smart" dataSource="dsdycs">
		<ef:columnText name="csmc" label="参数名称" />
		<ef:columnText name="tpwz_x" label="图片绝对位置(X)" dataType="number"
			mask="#######0" />
		<ef:columnText name="tpwz_y" label="图片绝对位置(Y)" dataType="number"
			mask="#######0" />
		<ef:columnText name="tpsf" label="图片缩放%" dataType="number"
			mask="#######0" />
	</ef:queryGrid>
	<ef:form>
		<ef:text color="red" value="注：PDF打印参数，只对使用精确打印的PDF模板文件参数进行维护。" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("wjmc").focus();
	}

	//保存
	function btnSaveClick() {
		if (!getObject("formFile").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveFileModelModify");
		url.addForm("formFile");
		url.addQueryGridAllData("gridPrintImg");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功");
			closeWindow(true);
		}
	}
</script>