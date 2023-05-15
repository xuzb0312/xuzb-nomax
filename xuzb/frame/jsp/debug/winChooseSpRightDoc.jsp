<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridSpRightDoc" dataSource="dssprightdoc"
		ondblClickRow="selectOneClick" multi="false" title="特殊权限信息" height="9"
		onKeyDown="gridKeyDown">
		<ef:columnText name="tsqxid" label="特殊权限ID" width="16" />
		<ef:columnText name="tsqxmc" label="特殊权限名称" width="20" />
		<ef:columnText name="bz" label="备注" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="确认" onclick="selectOneClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("gridSpRightDoc").focus();
	}

	//回车事件
	function gridKeyDown(event) {
		if (event.keyCode == 13) {
			selectOneClick();
		}
	}

	function selectOneClick() {
		var grid = getObject("gridSpRightDoc");
		var rowid = grid.getSelectedRow();
		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			closeWindow(mapData);
		} else {
			alert("请选择特殊权限配置");
		}
	}
</script>