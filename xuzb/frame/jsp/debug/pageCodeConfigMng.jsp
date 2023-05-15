<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:layout>
		<ef:topLayoutPanel height="85" border="false">
			<ef:form title="查询条件" name="formquery">
				<ef:textinput name="dmbh" label="代码编号" prompt="输入代码编号、名称进行模糊查询..."
					colspan="3" />
				<ef:buttons colspan="3">
					<ef:button name="btnQuery" value="查询" onclick="btnQueryClick();"></ef:button>
					<ef:button name="btnAdd" value="新增" onclick="btnAddClick();"></ef:button>
					<ef:button name="btnClear" value="清空" onclick="btnClearClick();"></ef:button>
				</ef:buttons>
			</ef:form>
		</ef:topLayoutPanel>
		<ef:leftLayoutPanel width="220" split="true" padding="5">
			<ef:tree name="codetree" onSelect="openRightPage"></ef:tree>
		</ef:leftLayoutPanel>
		<ef:centerLayoutPanel>
			<ef:loadPanel name="mainpaenl" />
		</ef:centerLayoutPanel>
	</ef:layout>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("dmbh").focus();
	}

	//清空
	function btnClearClick() {
		getObject("mainpaenl").clear();
		getObject("codetree").clear();
		getObject("formquery").clear();

		getObject("dmbh").focus();
	}

	//查询
	function btnQueryClick() {
		var dmbh = getObject("dmbh").getValue();
		var map = new HashMap();
		map.put("dmbh", dmbh);
		getObject("codetree").asyncLoadRemotData(
				"com.grace.frame.debug.biz.CodeMngTree", map);
	}

	//打开右侧的页面
	function openRightPage(node) {
		var dmbh = node.id;
		loadRightPage(dmbh);
	}
	function loadRightPage(dmbh) {
		var url = new URL("debug.do", "fwdOneCodeMng");
		url.addPara("dmbh", dmbh);
		getObject("mainpaenl").loadPage(url);
	}
	function clearRightPage() {
		getObject("mainpaenl").clear();
	}

	//新增CODE
	function btnAddClick() {
		var url = new URL("debug.do", "fwdCodeListAdd");
		openWindow("新增CODE", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			getObject("dmbh").setValue(data);
			btnQueryClick();
			loadRightPage(data);
		});
	}
</script>