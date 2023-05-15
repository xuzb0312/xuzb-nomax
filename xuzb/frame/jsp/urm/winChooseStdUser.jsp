<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridUser" dataSource="stduser"
		ondblClickRow="selectOneClick" multi="false" title="考生信息" height="9"
		onKeyDown="gridKeyDown">
		<ef:columnText name="ryid" label="考生ID" hidden="true" />
		<ef:columnText name="xm" label="考生姓名" width="8" />
		<ef:columnText name="xmpy" label="姓名拼音" width="5" />
		<ef:columnText name="xb" label="性别" width="5" />
		<ef:columnText name="nl" label="年龄" width="5" />
		<ef:columnDropDown label="证件类型" name="yxzjlx" code="YXZJLX" width="6" />
		<ef:columnText name="yxzjhm" label="证件号码" width="10" />
		<ef:columnText name="lxdh" label="联系电话" width="10" />
		<ef:columnText name="sjhm" label="手机号码" width="10" />
		<ef:columnText name="jtzz" label="家庭住址" width="12" />
		<ef:columnText name="bzje" label="补助金额" width="8" />
		<ef:columnText name="rzrq" label="入职日期" width="6"
			sourceMask="yyyyMMdd" mask="yyyy-MM-dd" dataType="date" />
		<ef:columnText name="xxgxsj" label="信息更新时间" width="9"
			sourceMask="yyyyMMddhhmmss" mask="yyyy-MM-dd hh:mm:ss"
			dataType="date" />
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