<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridRole" dataSource="dsrole"
		ondblClickRow="selectOneClick" multi="false" title="角色信息" height="9"
		onKeyDown="gridKeyDown">
		<ef:columnText name="jsid" label="角色ID" hidden="true" />
		<ef:columnText name="jsmc" label="角色名称" width="15" />
		<ef:columnText name="bz" label="备注说明" width="30" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button name="btnOk" value="确认" onclick="selectOneClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("gridRole").focus();
	}

	//回车事件
	function gridKeyDown(event) {
		if (event.keyCode == 13) {
			selectOneClick();
		}
	}

	function selectOneClick() {
		var grid = getObject("gridRole");
		var rowid = grid.getSelectedRow();
		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			closeWindow(mapData);
		} else {
			alert("请选择角色");
		}
	}
</script>