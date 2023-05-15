<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridTsqx" dataSource="tsqxinfo" title="用户特殊权限信息"
		multi="true">
		<ef:columnText name="tsqxid" label="特殊权限ID" width="10" />
		<ef:columnText name="tsqxmc" label="特殊权限名称" width="20" />
		<ef:columnText name="bz" label="备注说明" width="30" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增特殊权限" functionid="sys010105" iconCls="icon-add"
			onclick="btnAddClick();"></ef:button>
		<ef:button value="删除特殊权限" functionid="sys010106" iconCls="icon-remove"
			onclick="btnDelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//权限新增
	function btnAddClick() {
		var url = new URL("urm.do", "fwdSysUserTsqxAdd");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		openWindow("新增特殊权限", url, "normal", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshData();
		});
	}

	//权限删除
	function btnDelClick() {
		var grid = getObject("gridTsqx");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		if (!confirm("是否确认对选中的数据进行删除？")) {
			return;
		}

		var url = new URL("urm.do", "deleteSysUserTsqx");
		url.addQueryGridSelectData("gridTsqx", "tsqxid");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshData();
		}
	}

	//刷新特殊权限
	function refreshData() {
		var url = new URL("urm.do", "queryeSysUserTsqx");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		AjaxUtil.asyncRefreshBizData(url, "gridTsqx:tsqxinfo");
	}
</script>