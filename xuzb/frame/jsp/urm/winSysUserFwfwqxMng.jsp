<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridFwfwqx" dataSource="fwfwqxinfo"
		title="用户服务访问权限信息" multi="true">
		<ef:columnText name="fwmc" label="服务名称" width="10" />
		<ef:columnText name="fwff" label="服务方法" width="10" />
		<ef:columnText name="biz" label="业务BIZ" width="20" />
		<ef:columnText name="bizff" label="BiZ方法" width="10" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增服务访问权限" functionid="sys010107" iconCls="icon-add"
			onclick="btnAddClick();"></ef:button>
		<ef:button value="删除服务访问权限" functionid="sys010108"
			iconCls="icon-remove" onclick="btnDelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//权限新增
	function btnAddClick() {
		var url = new URL("urm.do", "fwdSysUserFwfwqxAdd");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		openWindow("新增服务访问权限", url, "normal", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshData();
		});
	}

	//权限删除
	function btnDelClick() {
		var grid = getObject("gridFwfwqx");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		if (!confirm("是否确认对选中的数据进行删除？")) {
			return;
		}

		var url = new URL("urm.do", "deleteSysUserFwfwqx");
		url.addQueryGridSelectData("gridFwfwqx", "fwmc,fwff");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshData();
		}
	}

	//刷新服务访问权限
	function refreshData() {
		var url = new URL("urm.do", "queryeSysUserFwfwqx");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		AjaxUtil.asyncRefreshBizData(url, "gridFwfwqx:fwfwqxinfo");
	}
</script>