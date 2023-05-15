<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%@ page import="com.grace.frame.util.DataSet"%>
<%@ page import="com.grace.frame.util.StringUtil"%>
<%
	DataSet dsPara = (DataSet) request.getAttribute("dspara");
%>
<ef:body>
	<ef:form name="formPara" title="参数配置" rowcount="6">
		<%
			for (int i = 0, n = dsPara.size(); i < n; i++) {
						String csbh = dsPara.getString(i, "csbh");
						String csmc = dsPara.getString(i, "csmc");
						String cssm = dsPara.getString(i, "cssm");
						String csz = dsPara.getString(i, "csz");
						String cssjlx = dsPara.getString(i, "cssjlx");
						String cssjym = dsPara.getString(i, "cssjym");
						String cssjcode = dsPara.getString(i, "cssjcode");
						if ("code".equalsIgnoreCase(cssjlx)) {
							csmc = "<span title='" + cssm + "'>" + csmc
									+ "</span>";
		%>
		<ef:dropdownList name="<%=csbh %>" label="<%=csmc %>"
			value="<%=csz %>" code="<%=cssjcode %>" colspan="2" readonly="true"
			helpTip="<%=cssm %>"></ef:dropdownList>
		<%
			} else if ("yesno".equalsIgnoreCase(cssjlx)) {
		%>
		<ef:dropdownList name="<%=csbh %>" label="<%=csmc %>"
			value="<%=csz %>" colspan="2" readonly="true" helpTip="<%=cssm %>">
			<ef:data key="0" value="否" />
			<ef:data key="1" value="是" />
		</ef:dropdownList>
		<%
			} else {
							if (StringUtil.chkStrNull(cssjym)) {
		%>
		<ef:textinput name="<%=csbh %>" label="<%=csmc %>" value="<%=csz %>"
			dataType="<%=cssjlx %>" readonly="true" helpTip="<%=cssm %>" />
		<%
			} else {
		%>
		<ef:textinput name="<%=csbh %>" label="<%=csmc %>" value="<%=csz %>"
			dataType="<%=cssjlx %>" readonly="true" mask="<%=cssjym%>"
			helpTip="<%=cssm %>" />
		<%
			}

						}

					}
		%>
	</ef:form>
	<ef:buttons>
		<ef:button value="修改" onclick="btnModifyClick();" disabled="false"
			name="btnModify"></ef:button>
		<ef:button value="保存" onclick="btnSaveClick();" disabled="true"
			name="btnSave"></ef:button>
		<ef:button value="取消" onclick="btnCancelClick();" disabled="true"
			name="btnCancle"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//保存
	function btnSaveClick() {
		var url = new URL("debug.do", "saveSysPara");
		url.addForm("formPara");
		AjaxUtil.syncBizRequest(url, function(data) {
			alert("保存成功！");
			AjaxUtil.asyncRefreshPage();
		});
	}

	//修改
	function btnModifyClick() {
		getObject("formPara").setReadOnly(false);
		getObject("btnModify").disable();
		getObject("btnSave").enable();
		getObject("btnCancle").enable();
		getObject("formPara").focus();
	}

	//取消
	function btnCancelClick() {
		AjaxUtil.asyncRefreshPage();
	}
</script>