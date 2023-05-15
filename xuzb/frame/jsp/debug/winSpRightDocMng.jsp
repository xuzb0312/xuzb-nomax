<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridSpdoc" title="特殊权限文档" dataSource="dsspdoc">
		<ef:columnText name="tsqxid" label="特殊权限ID" width="16" />
		<ef:columnText name="tsqxmc" label="特殊权限名称" width="20" />
		<ef:columnText name="bz" label="备注" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增" onclick="btnSpRightDocAddClick();"></ef:button>
		<ef:button value="修改" onclick="btnSpRightDocModifyClick();"></ef:button>
		<ef:button value="删除" onclick="btnSpRightDocDelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//刷新grid的数据
	function refreshGrdiData() {
		var url = new URL("debug.do", "refreshGridSpRightDoc");
		AjaxUtil.asyncRefreshBizData(url, "gridSpdoc:dsspdoc");
	}

	//新增
	function btnSpRightDocAddClick() {
		var url = new URL("debug.do", "fwdSpRightDocAdd");
		openWindow("新增特殊权限文档配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrdiData();
		});
	}

	//修改
	function btnSpRightDocModifyClick() {
		var grid = getObject("gridSpdoc");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "fwdSpRightDocModify");
		url.addPara("tsqxid", grid.getCell(grid.getSelectedRow(), "tsqxid"));
		openWindow("修改特殊权限文档配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrdiData();
		});
	}

	//删除
	function btnSpRightDocDelClick() {
		var grid = getObject("gridSpdoc");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		if (!confirm("确定要删除该项文档配置信息吗？")) {
			return;
		}
		var url = new URL("debug.do", "deleteSpRightDoc");
		url.addPara("tsqxid", grid.getCell(grid.getSelectedRow(), "tsqxid"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshGrdiData();
		}
	}
</script>