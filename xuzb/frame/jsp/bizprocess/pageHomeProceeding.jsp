<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%--待办事项展示页面--%>
<ef:body>
	<ef:tab tabPosition="bottom">
		<ef:tabPage title="待办事项">
			<ef:queryGrid name="gridDbsx" title="待办事项" height="17"
				ondblClickRow="gridDbsxDbClick" dataSource="dsdbsx">
				<ef:columnText name="sxid" label="事项ID" hidden="true" />
				<ef:columnText name="sxzt" label="状态" width="4" align="center" />
				<ef:columnText name="sxztsm" label="状态说明" width="7" />
				<ef:columnText name="sxmc" label="待办事项名称" width="32" />
				<ef:columnText name="fqr" label="发起人" width="5" />
				<ef:columnText name="fqsj" label="发起时间" dataType="date"
					mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss" width="10" />
				<ef:columnText name="jzrq" label="截止日期(含)" dataType="date"
					mask="yyyy-MM-dd" sourceMask="yyyyMMdd" width="6" />
				<ef:columnText name="cz" label="操作" width="6" align="center" />
			</ef:queryGrid>
			<ef:form border="false">
				<ef:text value="注：双击待办事项，展开业务经办窗口；如需查看办结事项，点击下方的【办结事项】按钮，切换查看。"
					colspan="4" color="blue" align="center" />
				<ef:buttons closebutton="false" colspan="2">
					<ef:button value="刷新页面" onclick="btnRefreshClick();"
						iconCls="icon-arrow-refresh"></ef:button>
				</ef:buttons>
			</ef:form>
		</ef:tabPage>
		<ef:tabPage title="办结事项">
			<ef:queryGrid name="gridBjsx" title="办结事项(双击查看详情)" height="17"
				dataSource="dsbjsx" ondblClickRow="gridBjsxDbClick">
				<ef:columnText name="sxid" label="事项ID" hidden="true" />
				<ef:columnText name="sxmc" label="待办事项名称" width="32" />
				<ef:columnText name="blqk" label="办理情况" width="5" />
				<ef:columnText name="fqrxm" label="发起人" width="6" />
				<ef:columnText name="fqsj" label="发起时间" dataType="date"
					mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss" width="10" />
				<ef:columnText name="czsj" label="业务经办时间" dataType="date"
					mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss" width="10" />
				<ef:columnText name="jzrq" label="截止日期" dataType="date"
					mask="yyyy-MM-dd" sourceMask="yyyyMMdd" width="6" />
			</ef:queryGrid>
			<ef:form border="false">
				<ef:text value="注：办结事项只展示本人最近办结的100项，如需查看更多，请点击【查看更多办结事项】按钮！"
					colspan="4" color="blue" align="center" />
				<ef:buttons closebutton="false" colspan="2">
					<ef:button value="查看更多办结事项" iconCls="icon-book-open"
						onclick="btnViewMoreBjsxClick();"></ef:button>
				</ef:buttons>
			</ef:form>
		</ef:tabPage>
	</ef:tab>
</ef:body>
<script type="text/javascript">
	//查看更多的办结事项
	function btnViewMoreBjsxClick() {
		var url = new URL("bizprocess.do", "fwdViewMoreBjsx");
		openWindow("查看更多事项", url, "big");
	}

	//刷新页面
	function btnRefreshClick() {
		var url = new URL("bizprocess.do", "fwdProceedingHomePage");
		AjaxUtil.asyncRefreshPage(url);
	}

	//办结事项双击
	function gridBjsxDbClick() {
		var grid = getObject("gridBjsx");
		var rowid = grid.getSelectedRow();
		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			var toolsUrl = new URL("bizprocess.do",
					"fwdProceedingBPDetailsView");
			toolsUrl.addPara("sxid", mapData.get("sxid"));
			openWindow("查看业务详情", "icon-text-list-bullets", toolsUrl, "big",
					null);
		}
	}

	//业务经办
	function gridDbsxDbClick() {
		var grid = getObject("gridDbsx");
		var rowid = grid.getSelectedRow();
		if (rowid) {
			var mapData = grid.getRowMapData(rowid);
			doProceedingBiz(mapData.get("sxid"));
		}
	}

	function doProceedingBiz(sxid) {
		//获取进入页面参数
		var url = new URL("bizprocess.do", "getDoProceedingPara");
		url.addPara("sxid", sxid);
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (!AjaxUtil.checkIsGoOn(data)) {
			return;
		}
		var mapData = new HashMap(data);

		//数据
		var sxjbjgid = mapData.get("sxjbjgid");
		var sxjbjgqxfw = mapData.get("sxjbjgqxfw");
		var gnsj = mapData.get("gnsj");
		var sxmc = mapData.get("sxmc");
		var ymcs = mapData.get("ymcs");
		var dbcs = mapData.get("dbcs");

		//数据权限
		setJbjgid(sxjbjgid);
		setJbjgqxfw(sxjbjgqxfw);

		//打开页面
		var url;
		if (gnsj.startWith("href:")) {
			url = gnsj.substr(5);
		} else if (gnsj.startWith("url:")) {
			var urlStr = gnsj.substr(4);
			url = new URL(urlStr);
			url.addPara("__sxid", sxid);
			url.addMap(new HashMap(ymcs));
			url.addPara("__dbsxpara", dbcs);
		} else {
			alert("功能权限配置错误。");
			return;
		}

		var tools = [ {
			iconCls : "icon-text-list-bullets",
			handler : function() {
				var toolsUrl = new URL("bizprocess.do",
						"fwdProceedingBPDetailsView");
				toolsUrl.addPara("sxid", sxid);
				openWindow("查看业务详情", "icon-text-list-bullets", toolsUrl, "big",
						null);
			}
		} ];
		openWindow(sxmc, "icon-application-go", url, 1500, 710, function(data) {
			if (!chkObjNull(data)) {
				btnRefreshClick();
			}
		}, tools);
	}

	//业务取消
	function nullifyProceedingBiz(sxid) {
		if (!confirm("确定要忽略掉该项待办事项吗？")) {
			return;
		}
		var url = new URL("bizprocess.do", "saveProceedingBizNullify");
		url.addPara("sxid", sxid);
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("该待办事项已经忽略完成。");
			btnRefreshClick();
		}
	}
</script>