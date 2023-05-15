<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="CODE信息" dataSource="codelist" rowcount="4">
		<ef:textinput name="dmbh" label="代码编号" readonly="true" />
		<ef:textinput name="dmmc" label="代码名称" readonly="true" />
		<ef:textinput name="dmsm" label="代码说明" readonly="true" colspan="4" />
		<ef:textinput name="bdpz" label="本地（DBID）配置代码" readonly="true"
			colspan="4" />
		<ef:textinput name="bdpzhy" label="本地（DBID）配置代码含义" readonly="true"
			colspan="4" />
	</ef:form>
	<ef:buttons closebutton="false">
		<ef:button value="修改CODE基本信息" onclick="btnCodeListModifyClick();"></ef:button>
		<ef:button value="修改CODE配置" onclick="btnCodeConfigSetClick();"></ef:button>
		<ef:button value="删除(list,doc,config数据一并清空)"
			onclick="btnCodeListDelClick();"></ef:button>
	</ef:buttons>
	<ef:queryGrid name="gridCodeDoc" dataSource="codedoc"
		title="CODE_DOC信息" height="7">
		<ef:columnText name="code" label="代码" width="8" align="center" />
		<ef:columnText name="content" label="含义" width="15" />
		<ef:columnText name="sm" label="说明" width="30" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增CODE" onclick="btnDocAdd();"></ef:button>
		<ef:button value="修改CODE" onclick="btnDocModify();"></ef:button>
		<ef:button value="删除CODE" onclick="btnDocDel();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function btnDocAdd(){
		var dmbh = getObject("dmbh").getValue();

		var url = new URL("debug.do", "fwdCodeDocAdd");
		url.addPara("dmbh",dmbh);
		openWindow("新增代码信息", url, "small", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refrshPage();
		});
	}
	
	function btnDocModify(){
		var grid = getObject("gridCodeDoc");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var dmbh = getObject("dmbh").getValue();
		var url = new URL("debug.do", "fwdCodeDocModify");
		url.addPara("dmbh",dmbh);
		url.addPara("code", grid.getCell(grid.getSelectedRow(), "code"));
		openWindow("修改代码信息", url, "small", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refrshPage();
		});
	}
	
	function btnDocDel(){
		var grid = getObject("gridCodeDoc");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		if (!confirm("确定要删除该code_doc信息吗？(删除时，系统将会一并将CODE_DOC,CODE_CONFIG数据删除。)")) {
			return;
		}

		var dmbh = getObject("dmbh").getValue();
		var url = new URL("debug.do", "deleteCodeDocInfo");
		url.addPara("dmbh",dmbh);
		url.addPara("code", grid.getCell(grid.getSelectedRow(), "code"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refrshPage();
		}
	}

	//修改CODE基本信息
	function btnCodeListModifyClick() {
		var dmbh = getObject("dmbh").getValue();

		var url = new URL("debug.do", "fwdCodeListModify");
		url.addPara("dmbh",dmbh);
		openWindow("修改代码信息", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refrshPage();
		});
	}

	//刷新整个页面
	function refrshPage(){
		var dmbh = getObject("dmbh").getValue();
		window.parent.loadRightPage(dmbh);
	}

	//删除
	function btnCodeListDelClick(){
		if (!confirm("确定要删除该code信息吗？(删除时，系统将会一并将CODE_LIST,CODE_DOC,CODE_CONFIG数据删除。)")) {
			return;
		}

		if (!confirm("再次确定要删除该code信息吗？(删除时，系统将会一并将CODE_LIST,CODE_DOC,CODE_CONFIG数据删除。)")) {
			return;
		}
		
		var dmbh = getObject("dmbh").getValue();
		var url = new URL("debug.do", "deleteCodeList");
		url.addPara("dmbh", dmbh);
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			window.parent.clearRightPage();
			window.parent.btnQueryClick();
		}
	}

	//code本地配置
	function btnCodeConfigSetClick(){
		var dmbh = getObject("dmbh").getValue();

		var url = new URL("debug.do", "fwdCodeConfigSet");
		url.addPara("dmbh",dmbh);
		openWindow("修改代码本地配置", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refrshPage();
		});
	}
</script>