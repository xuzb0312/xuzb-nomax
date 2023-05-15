<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridSjqx" dataSource="sjqxinfo" title="数据权限信息"
		multi="true" height="10">
		<ef:columnText name="jbjgid" label="JBJGID" hidden="true" />
		<ef:columnText name="jbjgbh" label="经办机构编号" width="8" />
		<ef:columnText name="jbjgmc" label="经办机构名称" width="15" />
		<ef:columnText name="sjjbjgbh" label="上级经办机构编号" width="8" />
		<ef:columnText name="sjjbjgmc" label="上级经办机构名称" width="15" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="增加" iconCls="icon-add" onclick="btnAddClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//权限删除
	function btnAddClick() {
		var grid = getObject("gridSjqx");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		var url = new URL("urm.do", "saveSysUserSjqxAdd");
		url.addQueryGridSelectData("gridSjqx", "jbjgid");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功");
			closeWindow(true);
		}
	}
</script>