<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="特殊权限配置" name="formSpRightConfig" rowcount="4">
		<ef:textinput name="tsqxid" required="true"
			onsearchclick="chooseSpRightDoc();" label="特殊权限ID" colspan="4" />
		<ef:textinput name="tsqxmc" required="true" readonly="true"
			label="特殊权限名称" colspan="4" />
		<ef:textinput name="bz" readonly="true" label="备注" colspan="4" />
		<ef:blank />
		<ef:text color="blue" value="注：该特殊权限将配置到当前系统运行的DBID上。" colspan="3" />
		<ef:buttons>
			<ef:button value="新增" onclick="btnAddClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
</ef:body>
<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("tsqxid").focus();
	}

	//选择DOC
	function chooseSpRightDoc() {
		var url = new URL("debug.do", "fwdChooseSpRightDoc");
		url.addPara("tsqxid", getObject("tsqxid").getValue());
		openWindow("选择", url, "normal", actionAfterSearch);
	}

	function actionAfterSearch(data) {
		if (!chkObjNull(data)) {
			getObject("formSpRightConfig").setMapData(data, true);
			getObject("tsqxid").setReadOnly(true);
		} else {
			getObject("tsqxid").focus();
		}
	}

	//清空
	function btnClearClick() {
		getObject("formSpRightConfig").clear();
		getObject("tsqxid").setReadOnly(false);
		getObject("tsqxid").focus();
	}

	//新增
	function btnAddClick() {
		var form = getObject("formSpRightConfig");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveSpRightConfigAdd");
		url.addForm("formSpRightConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(true);
		}
	}
</script>