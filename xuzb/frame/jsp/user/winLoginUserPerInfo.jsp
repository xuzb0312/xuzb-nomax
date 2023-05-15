<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="个人信息" name="formGrxx" dataSource="userinfo"
		rowcount="4">
		<ef:hiddenInput name="yhid" />
		<ef:textinput name="yhbh" label="用户编号" readonly="true" />
		<ef:textinput name="yhmc" label="用户名称" readonly="true" />

		<ef:dropdownList name="yhlx" label="用户类型" code="YHLX" readonly="true"
			loadOpt="false" />
		<ef:textinput name="ssjbjgid" label="所属经办机构" readonly="true" />

		<ef:dropdownList name="zjlx" label="证件类型" code="YXZJLX" loadOpt="true"
			readonly="true" />
		<ef:textinput name="zjhm" label="证件号码" readonly="true" />

		<ef:textinput name="ssjgmc" label="所属机构名称" readonly="true" colspan="4" />

		<ef:textinput name="ssjgbm" label="所属机构部门" readonly="true" colspan="4" />

		<ef:textinput name="sjhm" label="手机号码" readonly="true"
			validType="mobile" />
		<ef:textinput name="dzyx" label="电子邮箱" readonly="true"
			validType="email" />
	</ef:form>
	<ef:buttons>
		<ef:button value="修改" name="btnModify" onclick="btnModifyClick();"></ef:button>
		<ef:button value="保存" disabled="true" name="btnSave"
			onclick="btnSaveClick();"></ef:button>
		<ef:button value="取消" disabled="true" name="btnCancel"
			onclick="btnCancelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function btnModifyClick() {
		setReadOnly(false);
		getObject("zjlx").focus();
	}

	function btnCancelClick() {
		AjaxUtil.asyncRefreshPage();
	}

	function btnSaveClick() {
		if (!getObject("formGrxx").chkFormData(true)) {
			return;
		}
		var url = new URL("login.do", "saveLoginUserInfoModify");
		url.addForm("formGrxx");
		AjaxUtil.syncBizRequest(url, function(data) {
			alert("修改成功");
			AjaxUtil.asyncRefreshPage();
		});
	}

	function setReadOnly(readOnly) {
		getObject("zjlx").setReadOnly(readOnly);
		getObject("zjhm").setReadOnly(readOnly);
		getObject("ssjgmc").setReadOnly(readOnly);
		getObject("ssjgbm").setReadOnly(readOnly);
		getObject("sjhm").setReadOnly(readOnly);
		getObject("dzyx").setReadOnly(readOnly);
		if (readOnly) {
			getObject("btnModify").enable();
			getObject("btnSave").disable();
			getObject("btnCancel").disable();
		} else {
			getObject("btnModify").disable();
			getObject("btnSave").enable();
			getObject("btnCancel").enable();
		}
	}
</script>