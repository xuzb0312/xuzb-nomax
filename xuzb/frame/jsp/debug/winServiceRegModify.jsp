<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="服务注册信息" name="formReg" rowcount="4" dataSource="dsreg">
		<ef:textinput name="fwmc" label="服务注册名称" required="true" colspan="4"
			readonly="true" />
		<ef:textinput name="url" label="服务注册路径" required="true" colspan="4" />
		<ef:textinput name="yhbh" label="用户编号" required="true" colspan="4" />
		<ef:textinput name="pwd" label="用户密钥" required="true" colspan="4"
			prompt="请输入服务提供方提供的32位密钥.." />
		<ef:textinput name="timeout" label="超时（毫秒）" required="true"
			colspan="4" dataType="number" mask="##########0" />
		<ef:textinput name="fwzcsm" label="服务注册说明" colspan="4" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("url").focus();
	}

	//保存
	function btnSaveClick() {
		if (!getObject("formReg").chkFormData(true)) {
			return;
		}
		var timeout = getObject("timeout").getValue();
		if (timeout <= 0) {
			alert("超时时间必须大于0");
			getObject("timeout").focus();
			return;
		}
		var url = new URL("debug.do", "saveServiceRegModify");
		url.addForm("formReg");
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (data) {
			alert("保存成功");
			closeWindow(true);
		}
	}
</script>