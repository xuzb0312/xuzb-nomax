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
	<ef:form title="用户信息" name="formUser" rowcount="4">
		<ef:textinput name="yhbh" label="用户编号" required="true" />
		<ef:textinput name="yhmc" label="用户名称" required="true" />
		<ef:dropdownList name="yhlx" label="用户类型" code="YHLX" required="true"
			value="B" readonly="<%=yhlxreadonly %>" />
		<ef:textinput name="password" label="用户密码" required="true" />
		<ef:dropdownList name="zjlx" label="证件类型" code="YXZJLX" value="A" />
		<ef:textinput name="zjhm" label="证件号码" />
		<ef:dropdownList name="ssjbjgid" label="所属经办机构" dsCode="dsjbjg" />
		<ef:textinput name="ssjgmc" label="所属机构名称" />
		<ef:textinput name="ssjgbm" label="所属机构部门" colspan="4" />

		<ef:textinput name="sjhm" label="手机号码" validType="mobile" />
		<ef:textinput name="dzyx" label="电子邮箱" validType="email" />
		<ef:blank />
		<ef:checkboxList name="sjqxpz" value="1" rowcount="1" colspan="3">
			<ef:data key="1" value="同时给该用户配置所属经办机构的数据访问权限" />
		</ef:checkboxList>
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("yhbh").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formUser").clear();
		getObject("yhlx").setValue("B");
		getObject("yhbh").focus();
	}

	//保存
	function btnSaveClick() {
		var form = getObject("formUser");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("urm.do", "saveSysUserAdd");
		url.addForm("formUser");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(new HashMap(data));
		}
	}
</script>