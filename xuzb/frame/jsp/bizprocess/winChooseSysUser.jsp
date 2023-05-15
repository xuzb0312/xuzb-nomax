<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridUser" dataSource="dsuser"
		ondblClickRow="selectOneClick" multi="false" title="用户信息" height="15"
		onKeyDown="gridKeyDown">
		<ef:columnText name="yhid" label="用户ID" hidden="true" />
		<ef:columnText name="yhbh" label="用户编号" width="8" />
		<ef:columnText name="yhmc" label="用户名称" width="8" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button name="btnOk" value="确认" onclick="selectOneClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("gridUser").focus();
	}

	//回车事件
	function gridKeyDown(event) {
		if (event.keyCode == 13) {
			selectOneClick();
		}
	}

	function selectOneClick() {
		var grid = getObject("gridUser");
		var rowid = grid.getSelectedRow();
		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			closeWindow(mapData);
		} else {
			alert("请选择用户");
		}
	}
</script>