<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:attention>
		<span style="color: #c41d7f;">注：数据窗口只展示本地区（DBID）的特殊权限。</span>
	</ef:attention>
	<ef:queryGrid name="gridSpRight" title="特殊权限" dataSource="dsspright">
		<ef:columnText name="tsqxid" label="特殊权限ID" width="16" />
		<ef:columnText name="tsqxmc" label="特殊权限名称" width="20" />
		<ef:columnText name="bz" label="备注" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增特殊权限配置" onclick="btnConfigAddClick();"></ef:button>
		<ef:button value="删除特殊权限配置" onclick="btnConfigDelClick();"></ef:button>
		<ef:button value="特殊权限文档配置" onclick="btnDocSetClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
//刷新grid的数据
function refreshGrid() {
	var url = new URL("debug.do", "refreshGridSpRightConfig");
	AjaxUtil.asyncRefreshBizData(url, "gridSpRight:dsspright");
}

//特殊权限文档配置
function btnDocSetClick() {
	var url = new URL("debug.do", "fwdSpRightDocMng");
	openWindow("特殊权限文档配置", url, "big");
}

//新增轮询服务配置
function btnConfigAddClick() {
	var url = new URL("debug.do", "fwdSpRightConfigAdd");
	openWindow("新增特殊权限配置", url, "big", function(data) {
		if (chkObjNull(data)) {
			return;
		}
		refreshGrid();
	});
}

//删除轮询服务配置
function btnConfigDelClick() {
	var grid = getObject("gridSpRight");
	if (!grid.isSelectRow()) {
		alert("请先选择数据");
		return;
	}
	if (!confirm("确定要删除该项配置信息吗？")) {
		return;
	}
	var url = new URL("debug.do", "deleteSpRightConfig");
	url.addPara("tsqxid", grid.getCell(grid.getSelectedRow(), "tsqxid"));
	var data = AjaxUtil.syncBizRequest(url);
	if (AjaxUtil.checkIsGoOn(data)) {
		alert("删除成功");
		refreshGrid();
	}
}
</script>