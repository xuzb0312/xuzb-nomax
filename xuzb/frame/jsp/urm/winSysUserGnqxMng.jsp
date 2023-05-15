<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String yhid = (String) request.getAttribute("yhid");
	String paras = "yhid:'" + yhid + "'";
%>
<ef:body>
	<ef:layout>
		<ef:centerLayoutPanel padding="3">
			<ef:tree name="funtree" checkbox="true"
				className="com.grace.frame.urm.biz.SysUserFuncTree"
				paras='<%=paras %>'></ef:tree>
		</ef:centerLayoutPanel>
		<ef:bottomLayoutPanel height="50" border="false">
			<ef:buttons>
				<ef:button value="保存" functionid="sys010102"
					onclick="btnSaveClick();"></ef:button>
			</ef:buttons>
		</ef:bottomLayoutPanel>
	</ef:layout>
</ef:body>
<script type="text/javascript">
	//保存
	function btnSaveClick() {
		var yhid = '<%=(String) request.getAttribute("yhid")%>';
		var url = new URL("urm.do", "saveSysUserGnqxMng");
		url.addTreePara("funtree", "gnids");
		url.addPara("yhid", yhid);
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("保存成功。");
		}
	}
</script>