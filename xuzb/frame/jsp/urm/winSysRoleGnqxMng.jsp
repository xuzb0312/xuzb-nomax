<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String jsid = (String) request.getAttribute("jsid");
	String paras = "jsid:'" + jsid + "'";
%>
<ef:body>
	<ef:layout>
		<ef:centerLayoutPanel padding="3">
			<ef:tree name="funtree" checkbox="true"
				className="com.grace.frame.urm.biz.SysRoleFuncTree"
				paras='<%=paras %>'></ef:tree>
		</ef:centerLayoutPanel>
		<ef:bottomLayoutPanel height="50" border="false">
			<ef:buttons>
				<ef:button value="保存" functionid="sys010204"
					onclick="btnSaveClick();"></ef:button>
			</ef:buttons>
		</ef:bottomLayoutPanel>
	</ef:layout>
</ef:body>
<script type="text/javascript">
	//保存
	function btnSaveClick() {
		var jsid = '<%=(String) request.getAttribute("jsid")%>';
		var url = new URL("urm.do", "saveSysRoleGnqxMng");
		url.addTreePara("funtree", "gnids");
		url.addPara("jsid", jsid);
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("保存成功。");
		}
	}
</script>