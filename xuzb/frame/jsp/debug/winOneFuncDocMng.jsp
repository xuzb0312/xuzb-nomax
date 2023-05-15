<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="功能信息" dataSource="funcinfo">
		<ef:textinput name="gnid" label="功能ID" readonly="true" />
		<ef:textinput name="gnmc" label="功能名称" readonly="true" colspan="4" />
		<ef:textinput name="fgn" label="父功能" readonly="true" />
		<ef:dropdownList name="gnlx" loadOpt="false" readonly="true"
			code="GNLX" label="功能类型"></ef:dropdownList>
		<ef:textinput name="gntb" label="功能图标" readonly="true" />
		<ef:textinput name="gnsj" label="功能事件" colspan="6" readonly="true" />
		<ef:textinput name="bz" label="备注" colspan="6" readonly="true" />
	</ef:form>
	<ef:queryGrid name="gridConfig" dataSource="configinfo"
		title="功能配置信息(双击查看节点合并配置)" height="8" ondblClickRow="gridDblClick">
		<ef:columnText name="dbid" label="DBID" width="9" />
		<ef:columnText name="dbmc" label="数据库（地区）名称" width="15" />
		<ef:columnText name="sxh" label="顺序号" dataType="number"
			mask="###########0" width="9" />
		<ef:columnText name="ywlb" label="业务类别" width="10" />
	</ef:queryGrid>
	<ef:buttons closebutton="false">
		<ef:button value="新增子节点" onclick="btnAddChildrenNodeClick();"></ef:button>
		<ef:button value="修改信息" onclick="btnMoidfyClick();"></ef:button>
		<ef:button value="删除该功能节点及其子节点和相关地区配置" onclick="btnDeleteClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//新增子节点
	function btnAddChildrenNodeClick() {
		var gnid = getObject("gnid").getValue();
		if (chkObjNull(gnid)) {
			alert("功能ID为空");
			return;
		}
		var url = new URL("debug.do", "fwdChildrenNodeAdd");
		url.addPara("fgn", gnid);
		openWindow("新增子节点", url, "normal", function(data) {
			if(chkObjNull(data)){
				return;
			}
			window.parent.appendTreeNode(gnid, data.get("gnid"), data
					.get("gnmc"));
		});
	}

	//修改节点信息
	function btnMoidfyClick() {
		var gnid = getObject("gnid").getValue();
		if (chkObjNull(gnid)) {
			alert("功能ID为空");
			return;
		}
		var url = new URL("debug.do", "fwdChildrenNodeModify");
		url.addPara("gnid", gnid);
		openWindow("修改子节点", url, "normal", function(data) {
			if (!chkObjNull(data)) {
				refreshPage();
			}
		});

	}

	//刷新该页面
	function refreshPage() {
		var gnid = getObject("gnid").getValue();
		window.parent.loadRightPage(gnid);
	}

	//双击事件
	function gridDblClick(rowid) {
		var dbid = getObject("gridConfig").getCell(rowid, "dbid");
		var gnid = getObject("gnid").getValue();
		var url = new URL("debug.do", "fwdFuncUnionView");
		url.addPara("dbid", dbid);
		url.addPara("gnid", gnid);
		openWindow("查看区域合并信息", null, url, 600, 300, null);
	}

	//删除
	function btnDeleteClick() {
		var gnid = getObject("gnid").getValue();
		if (chkObjNull(gnid)) {
			alert("功能ID为空");
			return;
		}
		if (!confirm("确定要删除本节点及其下属子节点的信息吗（注：在删除节点信息是会对配置信息一并删除）？")) {
			return;
		}
		if (!confirm("再次确定要删除本节点及其下属子节点的信息吗（注：在删除节点信息是会对配置信息一并删除）？")) {
			return;
		}
		if (!confirm("最终确定要删除本节点及其下属子节点的信息吗（注：在删除节点信息是会对配置信息一并删除：一经删除无法恢复）？")) {
			return;
		}

		var url = new URL("debug.do", "saveChildrenNodeDelete");
		url.addPara("gnid", gnid);
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			if ("A" == getObject("gnlx").getValue()) {
				window.parent.btnClearClick();
			} else {
				window.parent.btnRefreshClick();
			}
		}
	}
</script>