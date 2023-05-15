<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:attention>
		<span style="color: #c41d7f;">轮询服务程序启动状态： </span>
		<span class="tiptag">${forminfo.sm} </span>
	</ef:attention>
	<ef:queryGrid name="gridpolling" title="轮询服务配置情况">
		<ef:columnText name="lxmc" label="轮询名称" width="8" />
		<ef:columnText name="lxbiz" label="轮询BIZ" width="17" />
		<ef:columnText name="lxff" label="轮询方法" width="8" />
		<ef:columnText name="lxcs" label="轮询参数" width="5" />
		<ef:columnText name="qssj" label="起始时间(时)" dataType="number"
			mask="####0" width="6" />
		<ef:columnText name="zzsj" label="终止时间(时)" dataType="number"
			mask="####0" width="6" />
		<ef:columnText name="sjjg" label="时间间隔(分钟)" dataType="number"
			mask="######0" width="7" />
		<ef:columnText name="sm" label="说明" width="15" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="手动执行" onclick="btnRunHandClick();"
			iconCls="icon-control-play-blue"></ef:button>
		<ef:button value="新增轮询服务配置" onclick="btnPollingConfigAddClick();"></ef:button>
		<ef:button value="修改轮询服务配置" onclick="btnPollingConfigModifyClick();"></ef:button>
		<ef:button value="删除轮询服务配置" onclick="btnPollingConfigDelClick();"></ef:button>
		<ef:button value="轮询服务文档配置" onclick="btnPollingDocSetClick();"></ef:button>
	</ef:buttons>&nbsp;
	<ef:attention>
		<span style="color: #c41d7f;">注：1.对于appParas.xml的轮询启动总控参数，如果重新设置，需要重启服务器。对于配置的轮询数据变更，只需重新加载缓存即可。
			<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.轮询参数，在Biz方法中通过String
			lxcs = para.getString(\"lxcs\");来获取。 </span>
	</ef:attention>
</ef:body>
<script type="text/javascript">
	//加载完成后处理的事情
	function onLoadComplete() {
		refreshGrdiPollingData();
	}

	//刷新grid的数据
	function refreshGrdiPollingData() {
		var url = new URL("debug.do", "refreshGridPollingConfig");
		AjaxUtil.asyncRefreshBizData(url, "gridpolling:dspolling");
	}

	//轮询文档配置
	function btnPollingDocSetClick() {
		var url = new URL("debug.do", "fwdPollingDocMng");
		openWindow("轮询文档配置", url, "normal");
	}

	//新增轮询服务配置
	function btnPollingConfigAddClick() {
		var url = new URL("debug.do", "fwdPollingConfigAdd");
		openWindow("新增轮询服务配置", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrdiPollingData();
		});
	}
	
	//修改轮询服务配置
	function btnPollingConfigModifyClick() {
		var grid = getObject("gridpolling");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "fwdPollingConfigModify");
		url.addPara("lxmc", grid.getCell(grid.getSelectedRow(), "lxmc"));
		openWindow("修改轮询文档配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			refreshGrdiPollingData();
		});
	}

	//手动执行执行一次
	function btnRunHandClick(){
		var grid = getObject("gridpolling");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "runPollingConfigByHand");
		url.addPara("lxmc", grid.getCell(grid.getSelectedRow(), "lxmc"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("执行成功，返回数据：" + data);
		}
	}
	
	//删除轮询服务配置
	function btnPollingConfigDelClick() {
		var grid = getObject("gridpolling");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		if (!confirm("确定要删除该项文档配置信息吗？")) {
			return;
		}
		var url = new URL("debug.do", "deletePollingConfig");
		url.addPara("lxmc", grid.getCell(grid.getSelectedRow(), "lxmc"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshGrdiPollingData();
		}
	}
</script>