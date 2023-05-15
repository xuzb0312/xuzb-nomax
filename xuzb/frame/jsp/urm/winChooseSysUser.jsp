<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridUser" dataSource="dsuser"
		ondblClickRow="selectOneClick" multi="false" title="用户信息" height="9"
		onKeyDown="gridKeyDown">
		<ef:columnText name="yhid" label="用户ID" hidden="true" />
		<ef:columnText name="yhbh" label="用户编号" width="8" />
		<ef:columnDropDown label="用户类型" name="yhlx" code="YHLX" width="6" />
		<ef:columnText name="yhmc" label="用户名称" width="5" />
		<ef:columnDropDown label="证件类型" name="zjlx" code="YXZJLX" width="6" />
		<ef:columnText name="zjhm" label="证件号码" width="10" />
		<ef:columnDropDown label="所属经办机构" name="ssjbjgid" dsCode="dsjbjg"
			width="12" />
		<ef:columnText name="ssjgmc" label="所属机构名称" width="12" />
		<ef:columnText name="ssjgbm" label="所属机构部门" width="12" />
		<ef:columnDropDown name="yhzt" label="用户状态" code="YHZT" width="4" />
		<ef:columnText name="zxrq" label="用户注销日期" width="6"
			sourceMask="yyyyMMdd" mask="yyyy-MM-dd" dataType="date" />
		<ef:columnText name="zxjbsj" label="注销经办时间" width="9"
			sourceMask="yyyyMMddhhmmss" mask="yyyy-MM-dd hh:mm:ss"
			dataType="date" />
		<ef:columnText name="zxyy" label="注销原因" width="15" />
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