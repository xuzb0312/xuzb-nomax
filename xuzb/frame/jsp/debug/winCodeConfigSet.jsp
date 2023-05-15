<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:layout>
		<ef:leftLayoutPanel width="350" border="false">
			<ef:queryGrid name="griddoc" dataSource="dsdoc" title="CODE_DOC数据"
				height="10" multi="true">
				<ef:columnText name="code" label="CODE代码" width="6" />
				<ef:columnText name="content" label="CODE含义" width="10" />
			</ef:queryGrid>
			<ef:buttons closebutton="false">
				<ef:button value="添加到CONFIG" onclick="btnAddConfigClick();"
					iconCls="icon-add"></ef:button>
			</ef:buttons>
		</ef:leftLayoutPanel>
		<ef:centerLayoutPanel border="false">
			<ef:queryGrid name="gridconfig" dataSource="dsconfig"
				title="CODE_COFIG配置数据" height="10" multi="true" edit="true"
				editType="onlyedit">
				<ef:columnText name="code" label="CODE代码" width="6" readonly="true" />
				<ef:columnText name="content" label="CODE含义" width="10"
					readonly="true" />
				<ef:columnText name="xh" label="序号" dataType="number"
					mask="############0" width="5" />
			</ef:queryGrid>
			<ef:buttons>
				<ef:button value="移除" onclick="btnDelConfigClick();"></ef:button>
				<ef:button value="修改序号" onclick="btnModifyXhClick();"></ef:button>
				<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
			</ef:buttons>
		</ef:centerLayoutPanel>
	</ef:layout>
</ef:body>
<script type="text/javascript">
	//修改行号
	function btnModifyXhClick() {
		var gridConfig = getObject("gridconfig");
		if (!gridConfig.isSelectOneRow()) {
			alert("请选择一行进行操作。");
			return;
		}
		var rowid = gridConfig.getDefalutSelectedRow();

		var url = new URL("debug.do", "fwdCodeConfigModifyXh");
		url.addPara("xh", gridConfig.getCell(rowid, "xh"));
		openWindow("修改序号", url, "small", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			gridConfig.setCell(rowid, "xh", data);
		});
	}
	
	//保存
	function btnSaveClick(){
		var dmbh='<%=(String)request.getAttribute("dmbh")%>';
		var url = new URL("debug.do", "saveCodeConfig");
		url.addPara("dmbh", dmbh);
		url.addQueryGridAllData("gridconfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("保存成功。");
			closeWindow(true);
		}
	}

	//增加
	function btnAddConfigClick(){
		var gridDoc = getObject("griddoc");
		var gridConfig = getObject("gridconfig");
		if (!gridDoc.isSelectRow()) {
			alert("请选择至少一行进行操作。");
			return;
		}

		var selectData = gridDoc.getSelectData();
		for(var i=0,n=selectData.length; i<n; i++){
			var rowid=selectData[i].rowid;
			gridDoc.delRowData(rowid);
			var rowdata = selectData[i];
			rowdata.xh = (n-i);
			gridConfig.addRowData(rowdata);
		}
	}

	//移除
	function btnDelConfigClick(){
		var gridDoc = getObject("griddoc");
		var gridConfig = getObject("gridconfig");
		if (!gridConfig.isSelectRow()) {
			alert("请选择至少一行进行操作。");
			return;
		}

		var selectData = gridConfig.getSelectData();
		for(var i=0,n=selectData.length; i<n; i++){
			var rowid=selectData[i].rowid;
			gridConfig.delRowData(rowid);
			gridDoc.addRowData(selectData[i]);
		}
	}
</script>