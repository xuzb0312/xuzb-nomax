<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridTsqx" dataSource="tsqxinfo" title="特殊权限信息"
		multi="true" height="10">
		<ef:columnText name="tsqxid" label="特殊权限ID" width="10" />
		<ef:columnText name="tsqxmc" label="特殊权限名称" width="20" />
		<ef:columnText name="bz" label="备注说明" width="30" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="增加" iconCls="icon-add" onclick="btnAddClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//权限删除
	function btnAddClick() {
		var grid = getObject("gridTsqx");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		var url = new URL("urm.do", "saveSysUserTsqxAdd");
		url.addQueryGridSelectData("gridTsqx", "tsqxid");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功");
			closeWindow(true);
		}
	}
</script>