<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formQuery" title="查询条件">
		<ef:hiddenInput name="fqr" />
		<ef:textinput name="fqrxm" label="发起人"
			onsearchclick="yhSearchClick('fqrxm', 'fqr', 'qsfqrq');" />
		<ef:textinput name="qsfqrq" label="起始发起日期" dataType="date"
			mask="yyyy-MM-dd" />
		<ef:textinput name="zzfqrq" label="终止发起日期" dataType="date"
			mask="yyyy-MM-dd" />

		<ef:hiddenInput name="czr" />
		<ef:textinput name="czrxm" label="操作人"
			onsearchclick="yhSearchClick('czrxm', 'czr', 'qsczrq');" />
		<ef:textinput name="qsczrq" label="起始操作日期" dataType="date"
			mask="yyyy-MM-dd" />
		<ef:textinput name="zzczrq" label="终止操作日期" dataType="date"
			mask="yyyy-MM-dd" />

		<ef:hiddenInput name="zfr" />
		<ef:textinput name="zfrxm" label="作废人"
			onsearchclick="yhSearchClick('zfrxm', 'zfr', 'qszfrq');" />
		<ef:textinput name="qszfrq" label="起始作废日期" dataType="date"
			mask="yyyy-MM-dd" />
		<ef:textinput name="zzzfrq" label="终止作废日期" dataType="date"
			mask="yyyy-MM-dd" />

		<ef:multiDropdownList name="sxzt" code="SXZT" prefix="1,2"
			label="事项状态"></ef:multiDropdownList>
		<ef:textinput name="sxmc" label="事项名称" prompt="根据事项名称模糊查询..." />
		<ef:buttons colspan="2">
			<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridSx"
		title="事项信息(查询结果只展示前1000条数据，如需精确查询，请填写精确查询条件。)" height="12"
		ondblClickRow="grdiSxDbClick">
		<ef:columnText name="sxid" label="事项ID" hidden="true" />
		<ef:columnText name="sxmc" label="事项名称" width="30" />
		<ef:columnDropDown label="事项状态" name="sxzt" width="5" code="SXZT"></ef:columnDropDown>
		<ef:columnText name="fqrbh" label="发起人编号" width="6" />
		<ef:columnText name="fqrxm" label="发起人姓名" width="6" />
		<ef:columnText name="fqsj" label="发起时间" dataType="date"
			mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss" width="10" />
		<ef:columnText name="czrbh" label="操作人编号" width="6" />
		<ef:columnText name="czrxm" label="操作人姓名" width="6" />
		<ef:columnText name="czsj" label="操作时间" dataType="date"
			mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss" width="10" />
		<ef:columnText name="czsm" label="操作说明" width="15" />
		<ef:columnText name="jzrq" label="截止日期" dataType="date"
			mask="yyyy-MM-dd" sourceMask="yyyyMMdd" width="7" />
		<ef:columnText name="zfrbh" label="作废人编号" width="6" />
		<ef:columnText name="zfrxm" label="作废人姓名" width="6" />
		<ef:columnText name="zfsj" label="作废时间" dataType="date"
			mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss" width="10" />
		<ef:columnText name="zfyy" label="作废原因" width="15" />
	</ef:queryGrid>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("fqrxm").focus();
	}

	function grdiSxDbClick() {
		var grid = getObject("gridSx");
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

	function yhSearchClick(mcflag, idFlag, nextFocusTag) {
		var yhmc = getObject(mcflag).getValue();
		getObject(idFlag).setValue("");
		getObject(mcflag).setValue("");
		var url = new URL("bizprocess.do", "fwdChooseSysUser");
		url.addPara("yhbh", yhmc);
		openWindow("选择用户", null, url, 500, 580, function(data) {
			if (chkObjNull(data)) {
				getObject(mcflag).focus();
				return;
			}
			getObject(idFlag).setValue(data.get("yhid"));
			getObject(mcflag).setValue(data.get("yhmc"));
			getObject(nextFocusTag).focus();
		});
	}

	function btnClearClick() {
		getObject("formQuery").clear();
		getObject("gridSx").clear();
		getObject("fqrxm").focus();
	}

	function btnQueryClick() {
		var url = new URL("bizprocess.do", "queryUserBjsxInfo");
		url.addForm("formQuery");
		AjaxUtil.asyncRefreshBizData(url, "gridSx:dssx");
	}
</script>