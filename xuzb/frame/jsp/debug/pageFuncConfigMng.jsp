<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:layout>
		<ef:topLayoutPanel height="85" border="false">
			<ef:form title="查询条件" name="formquery">
				<ef:textinput name="ywlyid" label="业务领域ID"
					onsearchclick="ywlyidSearch();" prompt="可以输入ID,名称进行模糊查询..." />
				<ef:textinput name="ywlymc" label="业务领域名称" readonly="true" />
				<ef:buttons colspan="2">
					<ef:button name="btnYwlxAdd" value="新增业务领域"
						onclick="btnYwlxAddClick();"></ef:button>
					<ef:button name="btnRefresh" value="刷新"
						onclick="btnRefreshClick();" disabled="true"></ef:button>
					<ef:button value="清空" onclick="btnClearClick();"></ef:button>
				</ef:buttons>
			</ef:form>
		</ef:topLayoutPanel>
		<ef:leftLayoutPanel width="190" split="true" padding="5">
			<ef:tree name="funtree" autoToggle="false"
				onContextMenu="treeSectionContextMenu"
				onBeforeSelect="treeSectionSel"></ef:tree>
		</ef:leftLayoutPanel>
		<ef:centerLayoutPanel>
			<ef:loadPanel name="mainpaenl" />
		</ef:centerLayoutPanel>
	</ef:layout>
	<ef:menu name="treeSectionMenu" width="110">
		<ef:menuItem value="新增下级节点" iconCls="icon-add"
			onclick="menuAddSection();"></ef:menuItem>
	</ef:menu>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("ywlyid").focus();
	}

	//树的右击事件
	function treeSectionContextMenu(e, node) {
		e.preventDefault();
		var tree = get("funtree");
		tree.select(node.target);
		var map = new HashMap();
		map.put("gnid", node.id);
		get("treeSectionMenu").setMapData(map);
		get("treeSectionMenu").show(e.pageX - 10, e.pageY - 5);
	}

	//新增子节点
	function menuAddSection() {
		var gnid = getObject("treeSectionMenu").getMapData().get("gnid");
		if (chkObjNull(gnid)) {
			alert("功能ID为空");
			return;
		}
		var url = new URL("debug.do", "fwdChildrenNodeAdd");
		url.addPara("fgn", gnid);
		openWindow("新增子节点", url, "normal",
		function(data) {
			if(chkObjNull(data)){
				return;
			}
			appendTreeNode(gnid, data.get("gnid"), data.get("gnmc"));
		});
	}

	//增加节点
	function appendTreeNode(targetId, gnid, gnmc) {
		var nodeMap = new HashMap();
		nodeMap.put("id", gnid);
		nodeMap.put("text", gnmc);
		var tree = getObject("funtree");
		tree.append(targetId, nodeMap);
		tree.expandToById(gnid);
		tree.selectById(gnid);
	}

	//节点选择
	function treeSectionSel(node) {
		var tree = getObject("funtree");
		var selNode = tree.getSelected();
		if (!chkObjNull(selNode)) {
			if (selNode.id == node.id) {
				return true;
			}
		}
		openRightPage(node);
	}

	//清空
	function btnClearClick() {
		getObject("btnRefresh").disable();
		getObject("mainpaenl").clear();
		getObject("funtree").clear();
		getObject("formquery").clear();
		getObject("ywlyid").setReadOnly(false);
		getObject("btnYwlxAdd").enable();
		getObject("ywlyid").setSearchBtnDisabled(false);
		getObject("ywlyid").focus();
	}

	//选择
	function ywlyidSearch() {
		var url = new URL("debug.do", "fwdChooseYwlxFunc");
		url.addPara("ywlyid", getObject("ywlyid").getValue());
		openWindow("选择业务领域", url, "normal", function(data) {
			if (chkObjNull(data)) {
				getObject("ywlyid").focus();
				return;
			}
			var ywlyid = data.get("ywlyid");
			var ywlymc = data.get("ywlymc");
			getObject("ywlyid").setValue(ywlyid);
			getObject("ywlymc").setValue(ywlymc);
			getObject("ywlyid").setReadOnly(true);
			getObject("btnYwlxAdd").disable();
			getObject("btnRefresh").enable();
			getObject("ywlyid").setSearchBtnDisabled(true);
			loadTree(ywlyid);
		});
	}
	//业务领域新增
	function btnYwlxAddClick() {
		var url = new URL("debug.do", "fwdYwlxFuncAdd");
		openWindow("新增业务领域", url, "normal", function(data) {
			if (chkObjNull(data)) {
				getObject("ywlyid").focus();
				return;
			}
			var ywlyid = data.get("ywlyid");
			var ywlymc = data.get("ywlymc");
			getObject("ywlyid").setValue(ywlyid);
			getObject("ywlymc").setValue(ywlymc);
			getObject("ywlyid").setReadOnly(true);
			getObject("btnRefresh").enable();
			getObject("ywlyid").setSearchBtnDisabled(true);
			loadTree(ywlyid);
		});
	}

	//加载树
	function loadTree(ywlyid) {
		if (chkObjNull(ywlyid)) {
			alert("请先选择业务领域");
			return;
		}
		var map = new HashMap();
		map.put("ywlyid", ywlyid);
		getObject("funtree").asyncLoadRemotData("com.grace.frame.debug.biz.FuncMngTree", map,
		function(tree) {
			tree.selectById(ywlyid);
		});
	}

	//打开右侧的页面
	function openRightPage(node) {
		var gnid = node.id;
		loadRightPage(gnid);
	}
	function loadRightPage(gnid) {
		var url = new URL("debug.do", "fwdOneFuncDocMng");
		url.addPara("gnid", gnid);
		getObject("mainpaenl").loadPage(url);
	}

	//刷新
	function btnRefreshClick() {
		var ywlyid = getObject("ywlyid").getValue();
		if (chkObjNull(ywlyid)) {
			alert("请先选择业务领域ID");
			return;
		}
		loadTree(ywlyid);
	}
</script>