<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="griddoc" dataSource="dsdoc" multi="true"
		title="本地化信息" height="10">
		<ef:columnText name="bzjm" label="标准件名" width="17" />
		<ef:columnText name="bdhm" label="本地化名" width="17" />
		<ef:columnText name="bdhsm" label="本地化说明" width="17" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="删除选中行" onclick="btnDelClick();"></ef:button>
	</ef:buttons>
	<ef:form title="提示信息">
		<ef:text color="blue"
			value="注：该窗口只展示在fw.local_doc中存在，但是未在fw.local_config中配置的文档信息。" />
	</ef:form>

</ef:body>
<script type="text/javascript">
	function btnDelClick() {
		var grid = getObject("griddoc");
		var rowids = grid.getSelectedRows();
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		if (!confirm("是否确认对选中的数据进行删除？")) {
			return;
		}

		var url = new URL("debug.do", "deleteNoExistsLocalConfigDoc");
		url.addQueryGridSelectData("griddoc", "bzjm,bdhm");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功。");
			var rurl = new URL("debug.do", "refreshDealNoExistsLocalConfigDoc");
			AjaxUtil.asyncRefreshBizData(rurl, "griddoc:dsdoc");
		}
	}
</script>