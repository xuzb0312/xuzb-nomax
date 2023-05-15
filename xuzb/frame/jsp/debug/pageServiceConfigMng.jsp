<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:attention>
		<span style="color: #c41d7f;">注：数据窗口只展示本地区（DBID）的本地服务信息。</span>
	</ef:attention>
	<ef:queryGrid name="gridService" title="本地服务信息" dataSource="dsservice">
		<ef:columnText name="fwmc" label="服务名称" width="10" />
		<ef:columnText name="fwff" label="服务方法" width="10" />
		<ef:columnText name="biz" label="服务BIZ" width="18" />
		<ef:columnText name="bizff" label="服务BIZ方法" width="10" />
		<ef:columnText name="fwsm" label="服务说明" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增本地服务配置" onclick="btnConfigAddClick();"></ef:button>
		<ef:button value="删除本地服务配置" onclick="btnConfigDelClick();"></ef:button>
		<ef:button value="本地服务文档配置" onclick="btnDocSetClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
//刷新grid的数据
function refreshGrid() {
	var url = new URL("debug.do", "refreshGridServiceConfig");
	AjaxUtil.asyncRefreshBizData(url, "gridService:dsservice");
}

//本地服务文档配置
function btnDocSetClick() {
	var url = new URL("debug.do", "fwdServiceDocMng");
	openWindow("本地服务文档配置", url, "normal");
}

//新增轮询服务配置
function btnConfigAddClick() {
	var url = new URL("debug.do", "fwdServiceConfigAdd");
	openWindow("新增本地服务配置", url, "normal", function(data) {
		if (chkObjNull(data)) {
			return;
		}
		refreshGrid();
	});
}

//删除轮询服务配置
function btnConfigDelClick() {
	var grid = getObject("gridService");
	if (!grid.isSelectRow()) {
		alert("请先选择数据");
		return;
	}
	if (!confirm("确定要删除该项配置信息吗？")) {
		return;
	}
	var url = new URL("debug.do", "deleteServiceConfig");
	url.addPara("fwmc", grid.getCell(grid.getSelectedRow(), "fwmc"));
	url.addPara("fwff", grid.getCell(grid.getSelectedRow(), "fwff"));
	var data = AjaxUtil.syncBizRequest(url);
	if (AjaxUtil.checkIsGoOn(data)) {
		alert("删除成功");
		refreshGrid();
	}
}
</script>