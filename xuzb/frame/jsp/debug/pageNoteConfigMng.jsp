<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:layout>
		<ef:topLayoutPanel height="85" border="false">
			<ef:form title="查询条件" name="formquery">
				<ef:textinput name="pzbh" label="批注编号" prompt="输入批注编号、名称进行模糊查询..."
					colspan="3" />
				<ef:buttons colspan="3">
					<ef:button name="btnQuery" value="查询" onclick="btnQueryClick();"></ef:button>
					<ef:button name="btnAdd" value="新增" onclick="btnAddClick();"></ef:button>
					<ef:button name="btnClear" value="清空" onclick="btnClearClick();"></ef:button>
				</ef:buttons>
			</ef:form>
		</ef:topLayoutPanel>
		<ef:leftLayoutPanel width="220" split="true" padding="5">
			<ef:tree name="notetree" onSelect="openRightPage"></ef:tree>
		</ef:leftLayoutPanel>
		<ef:centerLayoutPanel>
			<ef:loadPanel name="mainpaenl" />
		</ef:centerLayoutPanel>
	</ef:layout>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("pzbh").focus();
	}

	//清空
	function btnClearClick() {
		getObject("mainpaenl").clear();
		getObject("notetree").clear();
		getObject("formquery").clear();

		getObject("pzbh").focus();
	}

	//查询
	function btnQueryClick() {
		var pzbh = getObject("pzbh").getValue();
		var map = new HashMap();
		map.put("pzbh", pzbh);
		getObject("notetree").asyncLoadRemotData(
				"com.grace.frame.debug.biz.NoteTree", map);
	}

	//打开右侧的页面
	function openRightPage(node) {
		var pzbh = node.id;
		loadRightPage(pzbh);
	}
	function loadRightPage(pzbh) {
		var url = new URL("debug.do", "fwdOneNoteMng");
		url.addPara("pzbh", pzbh);
		getObject("mainpaenl").loadPage(url);
	}
	function clearRightPage() {
		getObject("mainpaenl").clear();
	}

	//新增NOTE
	function btnAddClick() {
		var url = new URL("debug.do", "fwdNoteListAdd");
		openWindow("新增典型批注", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			getObject("pzbh").setValue(data);
			btnQueryClick();
			loadRightPage(data);
		});
	}
</script>