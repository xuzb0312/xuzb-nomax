<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridPollingDoc" dataSource="dspolling"
		ondblClickRow="selectOneClick" multi="false" title="轮询信息" height="9"
		onKeyDown="gridKeyDown">
		<ef:columnText name="lxmc" label="轮询名称" width="9" />
		<ef:columnText name="lxbiz" label="轮询BIZ" width="17" />
		<ef:columnText name="lxff" label="轮询方法" width="9" />
		<ef:columnText name="lxcs" label="轮询参数" width="5" />
		<ef:columnText name="sm" label="说明" width="15" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="确认" onclick="selectOneClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("gridPollingDoc").focus();
	}

	//回车事件
	function gridKeyDown(event) {
		if (event.keyCode == 13) {
			selectOneClick();
		}
	}

	function selectOneClick() {
		var grid = getObject("gridPollingDoc");
		var rowid = grid.getSelectedRow();
		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			closeWindow(mapData);
		} else {
			alert("请选择轮询配置");
		}
	}
</script>