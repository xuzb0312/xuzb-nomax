<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridLocalDoc" dataSource="dslocaldoc"
		ondblClickRow="selectOneClick" multi="false" title="本地化信息" height="7"
		onKeyDown="gridKeyDown">
		<ef:columnText name="bzjm" label="标准件名" width="17" />
		<ef:columnText name="bdhm" label="本地化名" width="17" />
		<ef:columnText name="bdhsm" label="本地化说明" width="17" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="确认" onclick="selectOneClick();"></ef:button>
	</ef:buttons>
	<ef:form title="提示信息">
		<ef:text color="blue" value="注：该窗口只展示查询结果的前500行数据，如果需要查询请输入更加精确的条件查询。" />
	</ef:form>

</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("gridLocalDoc").focus();
	}

	//回车事件
	function gridKeyDown(event) {
		if (event.keyCode == 13) {
			selectOneClick();
		}
	}

	function selectOneClick() {
		var grid = getObject("gridLocalDoc");
		var rowid = grid.getSelectedRow();

		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			closeWindow(mapData);
		} else {
			alert("请选择本地化配置");
		}
	}
</script>