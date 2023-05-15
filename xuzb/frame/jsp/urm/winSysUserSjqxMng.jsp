<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridSjqx" dataSource="sjqxinfo" title="用户数据权限信息"
		multi="true">
		<ef:columnText name="jbjgid" label="JBJGID" hidden="true" />
		<ef:columnText name="jbjgbh" label="经办机构编号" width="8" />
		<ef:columnText name="jbjgmc" label="经办机构名称" width="15" />
		<ef:columnText name="sjjbjgbh" label="上级经办机构编号" width="8" />
		<ef:columnText name="sjjbjgmc" label="上级经办机构名称" width="15" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增数据权限" functionid="sys010103" iconCls="icon-add"
			onclick="btnAddClick();"></ef:button>
		<ef:button value="删除数据权限" functionid="sys010104" iconCls="icon-remove"
			onclick="btnDelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//权限新增
	function btnAddClick() {
		var url = new URL("urm.do", "fwdSysUserSjqxAdd");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		openWindow("新增数据权限", url, "normal", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshData();
		});
	}

	//权限删除
	function btnDelClick() {
		var grid = getObject("gridSjqx");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		if (!confirm("是否确认对选中的数据进行删除？")) {
			return;
		}

		var url = new URL("urm.do", "deleteSysUserSjqx");
		url.addQueryGridSelectData("gridSjqx", "jbjgid");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshData();
		}
	}

	//刷新数据权限
	function refreshData() {
		var url = new URL("urm.do", "queryeSysUserSjqx");
		url.addPara("yhid", "<%=(String)request.getAttribute("yhid")%>");
		AjaxUtil.asyncRefreshBizData(url, "gridSjqx:sjqxinfo");
	}
</script>