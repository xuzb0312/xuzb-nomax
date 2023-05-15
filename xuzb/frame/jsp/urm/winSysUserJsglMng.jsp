<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridJsgl" dataSource="jsglinfo" title="角色信息"
		multi="true">
		<ef:columnText name="jsid" label="角色ID" hidden="true" />
		<ef:columnText name="jsmc" label="角色名称" width="15" />
		<ef:columnText name="bz" label="备注" width="20" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增角色" functionid="sys010109" iconCls="icon-add"
			onclick="btnAddClick();"></ef:button>
		<ef:button value="删除角色" functionid="sys01010a" iconCls="icon-remove"
			onclick="btnDelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//角色新增
	function btnAddClick() {
		var url = new URL("urm.do", "fwdSysUserJsglAdd");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		openWindow("新增角色", url, "normal", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshData();
		});
	}

	//角色删除
	function btnDelClick() {
		var grid = getObject("gridJsgl");
		if (!grid.isSelectRow()) {
			alert("请先选择角色");
			return;
		}

		if (!confirm("是否确认对选中的角色进行删除？")) {
			return;
		}

		var url = new URL("urm.do", "deleteSysUserJsgl");
		url.addQueryGridSelectData("gridJsgl", "jsid");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshData();
		}
	}

	//刷新服务访问权限
	function refreshData() {
		var url = new URL("urm.do", "queryeSysUserJsgl");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		AjaxUtil.asyncRefreshBizData(url, "gridJsgl:jsglinfo");
	}
</script>