<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridFwfwqx" dataSource="fwfwqxinfo"
		title="服务访问权限信息" multi="true" height="10">
		<ef:columnText name="fwmc" label="服务名称" width="10" />
		<ef:columnText name="fwff" label="服务方法" width="10" />
		<ef:columnText name="biz" label="业务BIZ" width="20" />
		<ef:columnText name="bizff" label="BiZ方法" width="10" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="增加" iconCls="icon-add" onclick="btnAddClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//权限删除
	function btnAddClick() {
		var grid = getObject("gridFwfwqx");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		var url = new URL("urm.do", "saveSysUserFwfwqxAdd");
		url.addQueryGridSelectData("gridFwfwqx", "fwmc,fwff");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功");
			closeWindow(true);
		}
	}
</script>