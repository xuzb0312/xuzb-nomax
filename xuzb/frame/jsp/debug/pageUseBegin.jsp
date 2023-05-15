<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="配置流程">
		<ef:buttons align="center" closebutton="false">
			<ef:button value="配置DBID" iconAlign="top" iconCls="icon-keyboard"
				onclick="btnDbidClick();"></ef:button> → 
			<ef:button value="更改appPara.xml参数DBID为设置DBID" iconAlign="top"
				iconCls="icon-application-side-tree" onclick="btnParaClick();"></ef:button> → 
			<ef:button value="缓存重置" iconAlign="top" iconCls="icon-arrow-refresh"
				onclick="resetCache();"></ef:button> → 
			<ef:button value="框架CODE配置" iconAlign="top" iconCls="icon-lightning"
				onclick="btnCodeClick();"></ef:button> → 
			<ef:button value="框架FUNCTION配置" iconAlign="top"
				iconCls="icon-application-side-tree" onclick="btnFuncClick();"></ef:button> → 
			<ef:button value="配置经办机构" iconAlign="top" iconCls="icon-building-add"
				onclick="btnAgencyClick();"></ef:button> → 
			<ef:button value="配置业务类别" iconAlign="top" iconCls="icon-bricks"
				onclick="btnAgencyTypeClick();"></ef:button> → 
			<ef:button value="页面刷新" iconAlign="top" iconCls="icon-page-refresh"
				onclick="pageRefresh();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:form title="配置日志">
		<ef:textarea name="yxjg" colspan="6" height="300" />
	</ef:form>
	<ef:buttons></ef:buttons>
</ef:body>
<script type="text/javascript">
	function btnDbidClick() {
		var url = new URL("debug.do", "fwdDbidInfoAdd");
		openWindow("新增DBID", url, "normal", function(data) {
			if (chkObjNull(data)) {
				appendLog("关闭页面，未新增DBID");
				return;
			}
			appendLog("DBID新增成功");
		});
	}

	function btnCodeClick() {
		if (!confirm("是否重置框架CODE...建议只在系统DBID初始化时使用...")) {
			return;
		}

		appendLog("正在重置框架CODE...");
		var url = new URL("debug.do", "resetFrameCodeSetting");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			appendLog("框架CODE重置成功");
		}
	}

	function btnFuncClick() {
		if (!confirm("是否重置框架Function...建议只在系统DBID初始化时使用...")) {
			return;
		}

		appendLog("正在重置框架Function...");
		var url = new URL("debug.do", "resetFrameFuncSetting");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			appendLog("框架Function重置成功");
		}

	}

	function resetCache() {
		appendLog("正在重新加载缓存...");
		var url = new URL("debug.do", "resetSysCache");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			appendLog("缓存重新加载成功。");
		}
	}

	function pageRefresh() {
		resetCache();
		top.location.href = "login.do?method=fwdMainPage&ywlxgnid=debugroot";
	}

	function btnAgencyClick() {
		var url = new URL("debug.do", "fwdSysAgencyMng");
		openWindow("经办机构配置", url, "big", function(data) {
			appendLog("经办机构配置页面关闭");
		});
	}

	function btnAgencyTypeClick() {
		var url = new URL("debug.do", "fwdAgencyBizTypeMng");
		openWindow("业务类别配置", url, "big", function(data) {
			appendLog("业务类别配置页面关闭");
		});
	}

	function btnParaClick() {
		var url = new URL("debug.do", "fwdAppParaModfiyView");
		openWindow("appPara.xml更改", url, "normal", function(data) {
			appendLog("appPara更改页面关闭");
		});
	}

	function appendLog(msg) {
		msg = ">> " + DateUtil.getDateString(new Date(), "yyyy-MM-dd hh:mm:ss")
				+ "：" + msg;
		var yxjg = getObject("yxjg").getValue();
		if (!chkObjNull(yxjg)) {
			yxjg = yxjg + "\r\n";
		}
		yxjg = yxjg + msg;
		getObject("yxjg").setValue(yxjg);
		getObject("yxjg").focus();
	}
</script>
