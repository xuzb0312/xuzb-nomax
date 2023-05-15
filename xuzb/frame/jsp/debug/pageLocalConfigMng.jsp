<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="查询条件" name="formQuery">
		<ef:multiDropdownList name="jbjgid" label="经办机构 " dsCode="dsjbjg"></ef:multiDropdownList>
		<ef:textinput name="bzjm" label="标准件名（模糊）" />
		<ef:textinput name="bdhm" label="本地化名（模糊）" />
		<ef:cell colspan="5">
			<ef:attention>
				<span style="color: #c41d7f;">注：至少输入一个查询条件进行查询；在实际开发中最好直接书写sql进行配置，便于验证升级SQL正确性。</span>
			</ef:attention>
		</ef:cell>
		<ef:buttons colspan="1" closebutton="false">
			<ef:button name="btnQuery" value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridlocal" multi="true" title="本地化配置信息">
		<ef:columnText name="jbjgid" label="经办机构ID" width="5" />
		<ef:columnText name="jbjgbh" label="经办机构编号" width="5" />
		<ef:columnText name="jbjgmc" label="经办机构名称" width="12" />
		<ef:columnText name="bzjm" label="标准件名" width="17" />
		<ef:columnText name="bdhm" label="本地化名" width="17" />
		<ef:columnText name="bdhsm" label="本地化说明" width="17" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增本地化" onclick="btnLocalAddClick();"></ef:button>
		<ef:button value="删除本地化" onclick="btnLocalDeleteClick();"></ef:button>
		<ef:button value="重新加载本地化" onclick="btnReloadLocalConfigClick();"></ef:button>
		<ef:button value="修正本地化文档数据" onclick="btnLocalDocDeleteClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("jbjgid").focus();
	}
	//清空
	function btnClearClick() {
		getObject("formQuery").clearData();
		getObject("gridlocal").clear();
		getObject("btnQuery").enable();
		getObject("jbjgid").focus();
	}

	//查询
	function btnQueryClick() {
		var jbjgid = getObject("jbjgid").getValue();
		var bzjm = getObject("bzjm").getValue();
		var bdhm = getObject("bdhm").getValue();

		if (chkObjNull(jbjgid) && chkObjNull(bzjm) && chkObjNull(bdhm)) {
			alert("请至少选择一个条件后进行查询");
			return;
		}

		var url = new URL("debug.do", "queryLocalConfig");
		url.addPara("jbjgid", jbjgid);
		url.addPara("bzjm", bzjm);
		url.addPara("bdhm", bdhm);

		AjaxUtil.asyncRefreshBizData(url, "gridlocal:dslocal");
	}

	//新增
	function btnLocalAddClick() {
		var url = new URL("debug.do", "fwdLocalAddWin");
		openWindow("新增本地化", url, "normal", actionLoaclAdd);
	}
	function actionLoaclAdd(data) {
		if (!chkObjNull(data)) {
			getObject("formQuery").setMapData(data);
			btnQueryClick();
		}
	}

	//删除
	function btnLocalDeleteClick() {
		var grid = getObject("gridlocal");
		var rowids = grid.getSelectedRows();
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		if (!confirm("是否确认对选中的数据进行删除？")) {
			return;
		}

		var url = new URL("debug.do", "deleteLocalConfig");
		url.addQueryGridSelectData("gridlocal", "jbjgid,bzjm");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			btnQueryClick();
		}
	}

	//重新加载本地化配置
	function btnReloadLocalConfigClick() {
		if (!confirm("是否重新加载本地化配置？")) {
			return;
		}
		var url = new URL("debug.do", "reloadLocalConfig");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("重新加载成功。");
		}
	}

	//修正本地化文档数据
	function btnLocalDocDeleteClick() {
		var url = new URL("debug.do", "fwdDealNoExistsLocalConfigDoc");
		openWindow("新增本地化", url, "big");
	}
</script>