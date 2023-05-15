<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="轮询服务配置" name="formPollingConfig" rowcount="4">
		<ef:textinput name="lxmc" required="true"
			onsearchclick="choosePollingDoc();" label="轮询名称" colspan="4" />
		<ef:textinput name="lxbiz" required="true" readonly="true"
			label="轮询BIZ" colspan="4" />
		<ef:textinput name="lxff" required="true" readonly="true" label="轮询方法"
			colspan="4" />
		<ef:textinput name="lxcs" label="轮询参数" colspan="2" />
		<ef:text color="red"
			value="注：Biz方法中通过String lxcs = para.getString(\"lxcs\");来获取。"
			colspan="2" align="center" />
		<ef:textinput name="sjjg" label="时间间隔（分钟）" colspan="2"
			dataType="number" mask="####0" required="true" value="60" />
		<ef:text color="red" value="注：值必须为大于零，否则认为永远不执行。" colspan="2"
			align="center" />
		<ef:textinput name="qssj" label="起始时间（时）" colspan="2"
			dataType="number" mask="#0" required="true" />
		<ef:text color="red" value="注：值必须为0到23之间，否则无法保存。" colspan="2"
			align="center" />
		<ef:textinput name="zzsj" label="终止时间（时）" colspan="2"
			dataType="number" mask="#0" required="true" value="23" />
		<ef:text color="red" value="注：值必须为0到23之间，否则无法保存。" colspan="2"
			align="center" />
		<ef:blank />
		<ef:text color="blue" value="注：该轮询服务将配置到当前系统运行的DBID上。" colspan="3" />
		<ef:buttons>
			<ef:button value="新增" onclick="btnAddClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
</ef:body>
<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("lxmc").focus();
	}

	//选择DOC
	function choosePollingDoc() {
		var url = new URL("debug.do", "fwdChoosePollingDoc");
		url.addPara("lxmc", getObject("lxmc").getValue());
		openWindow("选择", url, "normal", actionAfterSearch);
	}

	function actionAfterSearch(data) {
		if (!chkObjNull(data)) {
			getObject("formPollingConfig").setMapData(data, true);
			getObject("lxmc").setReadOnly(true);
			getObject("lxcs").focus();
		} else {
			getObject("lxmc").focus();
		}
	}

	//清空
	function btnClearClick() {
		getObject("formPollingConfig").clear();
		getObject("lxmc").setReadOnly(false);
		getObject("lxmc").focus();
	}

	//新增
	function btnAddClick() {
		var form = getObject("formPollingConfig");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "savePollingConfigAdd");
		url.addForm("formPollingConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(true);
		}
	}
</script>