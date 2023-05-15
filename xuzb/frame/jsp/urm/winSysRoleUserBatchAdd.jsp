<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String jsid = (String) request.getAttribute("jsid");
%>
<ef:body>
	<ef:hiddenInput name="jsid" value="<%=jsid%>" />
	<ef:queryGrid name="gridUser" dataSource="userinfo" title="用户信息"
		multi="true" height="10">
		<ef:columnText name="yhid" label="用户ID" hidden="true" />
		<ef:columnText name="yhbh" label="用户编号" width="5" />
		<ef:columnText name="yhmc" label="用户名称" width="5" />
		<ef:columnDropDown label="证件类型" name="zjlx" code="YXZJLX" width="6" />
		<ef:columnText name="zjhm" label="证件号码" width="10" />
		<ef:columnDropDown label="所属经办机构" name="ssjbjgid" dsCode="dsjbjg"
			width="12" />
		<ef:columnText name="ssjgmc" label="所属机构名称" width="12" />
		<ef:columnText name="ssjgbm" label="所属机构部门" width="12" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="增加" iconCls="icon-add" onclick="btnAddClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function btnAddClick() {
		var grid = getObject("gridUser");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		var url = new URL("urm.do", "saveSysRoleUserBatchAdd");
		url.addQueryGridSelectData("gridUser", "yhid");
		url.addPara("jsid", getObject("jsid").getValue());
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功");
			closeWindow(true);
		}
	}
</script>
