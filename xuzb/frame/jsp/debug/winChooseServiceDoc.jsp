<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridServiceDoc" dataSource="dsdoc"
		ondblClickRow="selectOneClick" multi="false" title="本地服务信息" height="9"
		onKeyDown="gridKeyDown">
		<ef:columnText name="fwmc" label="服务名称" width="10" />
		<ef:columnText name="fwff" label="服务方法" width="10" />
		<ef:columnText name="biz" label="服务BIZ" width="18" />
		<ef:columnText name="bizff" label="服务BIZ方法" width="10" />
		<ef:columnText name="fwsm" label="服务说明" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="确认" onclick="selectOneClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("gridServiceDoc").focus();
	}

	//回车事件
	function gridKeyDown(event) {
		if (event.keyCode == 13) {
			selectOneClick();
		}
	}

	function selectOneClick() {
		var grid = getObject("gridServiceDoc");
		var rowid = grid.getSelectedRow();
		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			closeWindow(mapData);
		} else {
			alert("请选择特本地服务配置配置");
		}
	}
</script>