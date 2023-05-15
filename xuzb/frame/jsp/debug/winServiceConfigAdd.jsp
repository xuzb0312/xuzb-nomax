<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="本地服务配置" name="formServiceConfig" rowcount="4">
		<ef:textinput name="fwmc" required="true"
			onsearchclick="chooseServiceDoc();" label="服务名称" colspan="4" />
		<ef:textinput name="fwff" required="true" readonly="true" label="服务方法"
			colspan="4" />
		<ef:textinput name="biz" required="true" readonly="true" label="服务BIZ"
			colspan="4" />
		<ef:textinput name="bizff" required="true" readonly="true"
			label="服务BIZ方法" colspan="4" />
		<ef:textinput name="fwsm" readonly="true" label="服务说明" colspan="4" />
		<ef:blank />
		<ef:text color="blue" value="注：该本地服务将配置到当前系统运行的DBID上。" colspan="3" />
		<ef:buttons>
			<ef:button value="新增" onclick="btnAddClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
</ef:body>
<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("fwmc").focus();
	}

	//选择DOC
	function chooseServiceDoc() {
		var url = new URL("debug.do", "fwdChooseServiceDoc");
		url.addPara("fwmc", getObject("fwmc").getValue());
		openWindow("选择", url, "normal", actionAfterSearch);
	}

	function actionAfterSearch(data) {
		if (!chkObjNull(data)) {
			getObject("formServiceConfig").setMapData(data, true);
			getObject("fwmc").setReadOnly(true);
		} else {
			getObject("fwmc").focus();
		}
	}

	//清空
	function btnClearClick() {
		getObject("formServiceConfig").clear();
		getObject("fwmc").setReadOnly(false);
		getObject("fwmc").focus();
	}

	//新增
	function btnAddClick() {
		var form = getObject("formServiceConfig");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveServiceConfigAdd");
		url.addForm("formServiceConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(true);
		}
	}
</script>