<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="考生信息" name="formUser" rowcount="4"
		dataSource="userinfo">
		<ef:hiddenInput name="ryid" />
		<ef:textinput name="xm" label="姓名" readonly="true"/>
		<ef:textinput name="xmpy" label="姓名缩写" readonly="true" />
		<ef:textinput name="xb" label="性别" />
		<ef:textinput name="nl" label="年龄" />

		<ef:dropdownList name="yxzjlx" label="证件类型" code="YXZJLX" />
		<ef:textinput name="jtzz" label="家庭住址" />
		<ef:textinput name="yxzjhm" label="证件号码" />
		<ef:dropdownList name="bzje" label="补助金额" />
		<ef:textinput name="lxdh" label="联系电话" />
		<ef:textinput name="sjhm" label="手机号码" validType="mobile" />
		<ef:textinput name="rzrq" label="入职时间" mask="yyyy-MM-dd"
					  sourceMask="yyyyMMdd" dataType="date" />
		<ef:textinput name="xxgxsj" label="信息更新时间" mask="yyyy-MM-dd hh:mm:ss"
					  sourceMask="yyyyMMddhhmmss" dataType="date" readonly="true"/>
		<ef:blank />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("xm").focus();
	}

	//保存
	function btnSaveClick() {
		var form = getObject("formUser");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("urm.do", "saveStdUserModify");
		url.addForm("formUser");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>