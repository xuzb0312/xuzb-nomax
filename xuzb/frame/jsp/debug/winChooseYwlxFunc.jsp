<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridFunc" dataSource="dsfunc"
		ondblClickRow="selectOneClick" multi="false" title="功能权限" height="11" onKeyDown="gridKeyDown">
		<ef:columnText name="ywlyid" label="业务领域ID" width="9" />
		<ef:columnText name="ywlymc" label="业务领域名称" width="15" />
		<ef:columnText name="gntb" label="功能图标" width="9" />
		<ef:columnText name="bz" label="备注" width="20" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button name="btnOk" value="确认" onclick="selectOneClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete(){
		getObject("gridFunc").focus();
	}

	//回车事件
	function gridKeyDown(event) {
		if (event.keyCode == 13) {
			selectOneClick();
		}
	}
	
	function selectOneClick() {
		var grid = getObject("gridFunc");
		var rowid = grid.getSelectedRow();
		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			closeWindow(mapData);
		} else {
			alert("请选择业务领域");
		}
	}
</script>