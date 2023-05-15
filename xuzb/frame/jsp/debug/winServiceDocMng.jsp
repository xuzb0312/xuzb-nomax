<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridServiceDoc" title="本地服务文档" dataSource="dsdoc">
		<ef:columnText name="fwmc" label="服务名称" width="10" />
		<ef:columnText name="fwff" label="服务方法" width="10" />
		<ef:columnText name="biz" label="服务BIZ" width="18" />
		<ef:columnText name="bizff" label="服务BIZ方法" width="10" />
		<ef:columnText name="fwsm" label="服务说明" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增" onclick="btnServiceDocAddClick();"></ef:button>
		<ef:button value="修改" onclick="btnServiceDocModifyClick();"></ef:button>
		<ef:button value="删除" onclick="btnServiceDocDelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//刷新grid的数据
	function refreshGrdiData() {
		var url = new URL("debug.do", "refreshGridServiceDoc");
		AjaxUtil.asyncRefreshBizData(url, "gridServiceDoc:dsdoc");
	}

	//新增
	function btnServiceDocAddClick() {
		var url = new URL("debug.do", "fwdServiceDocAdd");
		openWindow("新增本地服务文档配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrdiData();
		});
	}

	//修改
	function btnServiceDocModifyClick() {
		var grid = getObject("gridServiceDoc");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "fwdServiceDocModify");
		url.addPara("fwmc", grid.getCell(grid.getSelectedRow(), "fwmc"));
		url.addPara("fwff", grid.getCell(grid.getSelectedRow(), "fwff"));
		openWindow("修改本地服务文档配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrdiData();
		});
	}

	//删除
	function btnServiceDocDelClick() {
		var grid = getObject("gridServiceDoc");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		if (!confirm("确定要删除该项文档配置信息吗？")) {
			return;
		}
		var url = new URL("debug.do", "deleteServiceDoc");
		url.addPara("fwmc", grid.getCell(grid.getSelectedRow(), "fwmc"));
		url.addPara("fwff", grid.getCell(grid.getSelectedRow(), "fwff"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshGrdiData();
		}
	}
</script>