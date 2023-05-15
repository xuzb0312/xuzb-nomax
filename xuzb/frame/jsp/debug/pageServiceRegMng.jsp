<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:attention>
		<span style="color: #c41d7f;">注：该窗口只展示本地区（DBID）的服务注册信息。</span>
	</ef:attention>
	<ef:queryGrid name="gridReg" dataSource="dsreg" title="服务注册信息">
		<ef:columnText name="fwmc" label="服务名称" width="12" />
		<ef:columnText name="url" label="服务路径" width="20" />
		<ef:columnText name="yhbh" label="用户编号" width="8" />
		<ef:columnText name="pwd" label="用户密钥" width="16" />
		<ef:columnText name="timeout" label="超时" width="6" dataType="number"
			mask="######################0" />
		<ef:columnText name="fwzcsm" label="服务注册说明" width="20" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增" onclick="btnAddClick();"></ef:button>
		<ef:button value="修改" onclick="btnModifyClick();"></ef:button>
		<ef:button value="删除" onclick="btnDeleteClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//刷新
	function refreshGrid() {
		var url = new URL("debug.do", "refreshServiceRegInfo");
		AjaxUtil.asyncRefreshBizData(url, "gridReg:dsreg");
	}

	//新增
	function btnAddClick() {
		var url = new URL("debug.do", "fwdServiceRegAdd");
		openWindow("新增服务注册信息", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrid();
		});
	}

	//修改
	function btnModifyClick() {
		var grid = getObject("gridReg");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "fwdServiceRegModify");
		url.addPara("fwmc", grid.getCell(grid.getSelectedRow(), "fwmc"));
		openWindow("修改服务注册信息", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrid();
		});
	}

	//删除
	function btnDeleteClick() {
		var grid = getObject("gridReg");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		if (!confirm("再次确认是否删除，点击确定继续？")) {
			return;
		}
		var url = new URL("debug.do", "deleteServiceReg");
		url.addPara("fwmc", grid.getCell(grid.getSelectedRow(), "fwmc"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshGrid();
		}
	}
</script>