<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%@ page import="com.grace.frame.util.SysUser"%>
<%
	SysUser user = (SysUser) request.getSession().getAttribute(
			"currentsysuser");// 当前用户
	boolean yhlxreadonly = true;
	if ("A".equals(user.getYhlx())) {
		yhlxreadonly = false;
	}
%>
<ef:body>
	<ef:form title="考生信息" name="formUser" rowcount="4">
		<ef:textinput name="xm" label="姓名" required="true" />
		<ef:textinput name="xb" label="性别" required="true" />
		<ef:dropdownList name="yxzjlx" label="证件类型" code="YXZJLX" value="A" />
		<ef:textinput name="yxzjhm" label="证件号码" />
		<ef:textinput name="nl" label="年龄" required="true" />
		<ef:textinput name="jtzz" label="家庭住址" />

		<ef:textinput name="lxdh" label="联系电话" validType="mobile" />
		<ef:textinput name="sjhm" label="手机号码" validType="mobile" />
		<ef:textinput name="bzje" label="补助金额" />
		<ef:textinput name="rzrq" label="入职日期" />
		<ef:blank />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("xm").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formUser").clear();
		getObject("yxzjlx").setValue("A");
		getObject("xm").focus();
	}

	//保存
	function btnSaveClick() {
		var form = getObject("formUser");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("urm.do", "saveStdUserAdd");
		url.addForm("formUser");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(new HashMap(data));
		}
	}
</script>