<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridJsgl" dataSource="jsglinfo" title="角色信息"
		multi="true" height="10">
		<ef:columnText name="jsid" label="角色ID" hidden="true" />
		<ef:columnText name="jsmc" label="角色名称" width="15" />
		<ef:columnText name="bz" label="备注" width="20" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="增加" iconCls="icon-add" onclick="btnAddClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//权限删除
	function btnAddClick() {
		var grid = getObject("gridJsgl");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		var url = new URL("urm.do", "saveSysUserJsglAdd");
		url.addQueryGridSelectData("gridJsgl", "jsid");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功");
			closeWindow(true);
		}
	}
</script>