<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="查询条件" name="formQuery">
		<ef:multiDropdownList name="ssjbjgid" label="所属经办机构" dsCode="dsjbjg"></ef:multiDropdownList>
		<ef:dropdownList name="yhzt" label="用户状态" code="YHZT"></ef:dropdownList>
		<ef:dropdownList name="yhlx" label="用户类型" code="YHLX"></ef:dropdownList>
		<ef:buttons>
			<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridSysUser" title="系统用户信息">
		<ef:columnText name="yhid" label="用户ID" hidden="true" />
		<ef:columnText name="yhbh" label="用户编号" width="7" />
		<ef:columnText name="yhmc" label="用户名称" width="6" />
		<ef:columnDropDown label="用户类型" name="yhlx" code="YHLX" width="6"></ef:columnDropDown>
		<ef:columnDropDown label="证件类型" name="zjlx" code="YXZJLX" width="7"></ef:columnDropDown>
		<ef:columnText name="zjhm" label="证件号码" width="10" />
		<ef:columnDropDown name="ssjbjgid" label="所属经办机构" width="12"
			dsCode="dsjbjg" />
		<ef:columnText name="ssjgmc" label="所属机构名称" width="12" />
		<ef:columnText name="ssjgbm" label="所属机构部门" width="10" />
		<ef:columnDropDown name="yhzt" label="用户状态" code="YHZT" width="5"></ef:columnDropDown>
		<ef:columnText name="zxrq" label="用户注销日期" width="6" dataType="date"
			mask="yyyy-MM-dd" sourceMask="yyyyMMdd" />
		<ef:columnText name="zxjbsj" label="注销经办时间" width="10" dataType="date"
			mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss" />
		<ef:columnText name="zxyy" label="注销原因" width="10" />
	</ef:queryGrid>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("ssjbjgid").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formQuery").clear();
		getObject("gridSysUser").clear();
		getObject("ssjbjgid").focus();
	}

	//查询
	function btnQueryClick() {
		var url = new URL("urm.do", "querySysUserInfo");
		url.addForm("formQuery");
		AjaxUtil.asyncRefreshBizData(url, "gridSysUser:dsuser");
	}
</script>