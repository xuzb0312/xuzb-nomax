<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridpolling" title="轮询服务文档">
		<ef:columnText name="lxmc" label="轮询名称" width="6" />
		<ef:columnText name="lxbiz" label="轮询BIZ" width="15" />
		<ef:columnText name="lxff" label="轮询方法" width="8" />
		<ef:columnText name="lxcs" label="轮询参数" width="5" />
		<ef:columnText name="sm" label="说明" width="15" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增" onclick="btnPollingDocAddClick();"></ef:button>
		<ef:button value="修改" onclick="btnPollingDocModifyClick();"></ef:button>
		<ef:button value="删除" onclick="btnPollingDocDelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//加载完成后处理的事情
	function onLoadComplete() {
		refreshGrdiPollingData();
	}

	//刷新grid的数据
	function refreshGrdiPollingData() {
		var url = new URL("debug.do", "refreshGridPollingDoc");
		AjaxUtil.asyncRefreshBizData(url, "gridpolling:dspolling");
	}

	//新增
	function btnPollingDocAddClick() {
		var url = new URL("debug.do", "fwdPollingDocAdd");
		openWindow("新增轮询文档配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrdiPollingData();
		});
	}

	//修改
	function btnPollingDocModifyClick() {
		var grid = getObject("gridpolling");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "fwdPollingDocModify");
		url.addPara("lxmc", grid.getCell(grid.getSelectedRow(), "lxmc"));
		openWindow("修改轮询文档配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrdiPollingData();
		});
	}

	//删除
	function btnPollingDocDelClick() {
		var grid = getObject("gridpolling");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		if (!confirm("确定要删除该项文档配置信息吗？")) {
			return;
		}
		var url = new URL("debug.do", "deletePollingDoc");
		url.addPara("lxmc", grid.getCell(grid.getSelectedRow(), "lxmc"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshGrdiPollingData();
		}
	}
</script>