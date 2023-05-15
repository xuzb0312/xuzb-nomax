<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridType" title="业务类别信息" dataSource="dstypeinfo">
		<ef:columnText name="dbid" label="DBID" width="5" />
		<ef:columnText name="dbmc" label="数据库名称" width="10" />
		<ef:columnText name="ywlb" label="业务类别" width="5" />
		<ef:columnText name="jbjgids" label="经办机构" width="45" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增" onclick="btnAddClick();"></ef:button>
		<ef:button value="修改" onclick="btnModifyClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//新增
	function btnAddClick() {
		var url = new URL("debug.do", "fwdAgencyBizTypeAdd");
		openWindow("新增业务类别", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGridData();
		});
	}
	//修改
	function btnModifyClick() {
		var grid = getObject("gridType");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "fwdAgencyBizTypeModify");
		url.addPara("dbid", grid.getCell(grid.getSelectedRow(), "dbid"));
		url.addPara("ywlb", grid.getCell(grid.getSelectedRow(), "ywlb"));
		openWindow("修改业务类别", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGridData();
		});
	}

	//刷新数据
	function refreshGridData(){
		var url = new URL("debug.do", "refreshAgencyBizTypeInfo");
		AjaxUtil.asyncRefreshBizData(url, "gridType:dstypeinfo");
	}
</script>