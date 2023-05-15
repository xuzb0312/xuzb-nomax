<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:attention>
		<span style="color: #c41d7f;">注：不提供删除功能，删除DBID信息，请从后台数据库进行删除。</span>
	</ef:attention>
	<ef:queryGrid name="gridDbid" dataSource="dbidinfo" title="DBID信息">
		<ef:columnText name="dbid" label="DBID" width="8" />
		<ef:columnText name="dbmc" label="数据库名称" width="12" />
		<ef:columnText name="appname" label="系统名称(特性化)" width="15" />
		<ef:columnText name="bz" label="备注" width="35" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增" onclick="btnSaveClick();"></ef:button>
		<ef:button value="修改" onclick="btnModifyClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//新增
	function btnSaveClick() {
		var url = new URL("debug.do", "fwdDbidInfoAdd");
		openWindow("新增DBID", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGridData();
		});
	}

	//修改
	function btnModifyClick() {
		var grid = getObject("gridDbid");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "fwdDbidInfoModify");
		url.addPara("dbid", grid.getCell(grid.getSelectedRow(), "dbid"));
		openWindow("修改DBID", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGridData();
		});
	}

	//刷新数据
	function refreshGridData(){
		var url = new URL("debug.do", "refreshDbidInfo");
		AjaxUtil.asyncRefreshBizData(url, "gridDbid:dbidinfo");
	}
</script>