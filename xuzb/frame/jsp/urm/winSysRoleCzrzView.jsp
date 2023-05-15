<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="查询条件" name="formQuery">
		<ef:textinput name="qsrq" label="操作起始日期" dataType="date"
			mask="yyyy-MM-dd" />
		<ef:textinput name="zzrq" label="操作终止日期" dataType="date"
			mask="yyyy-MM-dd" />
		<ef:buttons colspan="2" closebutton="false">
			<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridLog" title="操作日志" height="13">
		<ef:columnText name="czlx" label="操作类型" width="8" />
		<ef:columnText name="czmc" label="操作名称" width="10" />
		<ef:columnText name="czsj" label="操作时间" dataType="date"
			sourceMask="yyyyMMddhhmmss" mask="yyyy-MM-dd hh:mm:ss" width="9"
			align="center" />
		<ef:columnText name="czip" label="操作IP" width="6" align="center" />
		<ef:columnText name="czybh" label="操作员编号" width="5" />
		<ef:columnText name="czymc" label="操作员名称" width="5" />
		<ef:columnText name="czsm" label="操作说明" width="35" />
	</ef:queryGrid>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("qsrq").focus();
	}

	function btnClearClick() {
		getObject("formQuery").clear();
		getObject("gridLog").clear();
		getObject("qsrq").focus();
	}

	//查询
	function btnQueryClick() {
		var url=new URL("urm.do","querySysRoleBizLog");
		url.addPara("jsid", "<%=(String)request.getAttribute("jsid")%>");
		url.addForm("formQuery");
		AjaxUtil.asyncRefreshBizData(url,"gridLog:dslog");
	}
</script>