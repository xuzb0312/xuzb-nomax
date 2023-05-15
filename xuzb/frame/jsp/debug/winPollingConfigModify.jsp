<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="轮询服务配置" name="formPollingConfig" rowcount="4"
		dataSource="formpolling">
		<ef:textinput name="lxmc" required="true" readonly="true" label="轮询名称"
			colspan="4" />
		<ef:textinput name="lxbiz" required="true" readonly="true"
			label="轮询BIZ" colspan="4" />
		<ef:textinput name="lxff" required="true" readonly="true" label="轮询方法"
			colspan="4" />
		<ef:textinput name="lxcs" label="轮询参数" colspan="4" />
		<ef:textinput name="sjjg" label="时间间隔（分钟）" colspan="2"
			dataType="number" mask="####0" required="true" />
		<ef:text color="red" value="注：值必须为大于零，否则认为永远不执行。" colspan="2"
			align="center" />
		<ef:textinput name="qssj" label="起始时间（时）" colspan="2"
			dataType="number" mask="#0" required="true" />
		<ef:text color="red" value="注：值必须为0到23之间，否则无法保存。" colspan="2"
			align="center" />
		<ef:textinput name="zzsj" label="终止时间（时）" colspan="2"
			dataType="number" mask="#0" required="true" />
		<ef:text color="red" value="注：值必须为0到23之间，否则无法保存。" colspan="2"
			align="center" />
		<ef:blank />
		<ef:buttons>
			<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		</ef:buttons>
	</ef:form>
</ef:body>
<script type="text/javascript">
	//加载完成后触发
	function onLoadComplete() {
		getObject("lxcs").focus();
	}

	//修改
	function btnSaveClick() {
		var form = getObject("formPollingConfig");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "savePollingConfigModify");
		url.addForm("formPollingConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>