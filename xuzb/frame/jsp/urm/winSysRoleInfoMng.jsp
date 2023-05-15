<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="角色信息" name="formRole" rowcount="6"
		dataSource="roleinfo">
		<ef:hiddenInput name="jsid" />
		<ef:textinput name="jsmc" label="角色名称" readonly="true" />
		<ef:textinput name="bz" label="备注" colspan="4" readonly="true" />
	</ef:form>
	<ef:buttons>
		<ef:button value="信息修改" iconCls="icon-user-edit"
			onclick="btnModifyClick();" functionid="sys010203"></ef:button>
	</ef:buttons>
	<ef:queryGrid name="gridUser" dataSource="userinfo" title="配置该角色的用户信息"
		height="11" multi="true">
		<ef:columnText name="yhbh" label="用户编号" width="8" />
		<ef:columnDropDown label="用户类型" name="yhlx" code="YHLX" width="6" />
		<ef:columnText name="yhmc" label="用户名称" width="8" />
		<ef:columnDropDown label="证件类型" name="zjlx" code="YXZJLX" width="8" />
		<ef:columnText name="zjhm" label="证件号码" width="15" />
		<ef:columnDropDown name="yhzt" label="用户状态" code="YHZT" width="8" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="批量将该角色分配给用户" iconCls="icon-user-add"
			onclick="btnBatchAddClick();" functionid="sys010205"></ef:button>
		<ef:button value="批量删除该角色的用户配置" iconCls="icon-user-delete"
			onclick="btnBatchDelClick();" functionid="sys010206"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function btnModifyClick() {
		var url = new URL("urm.do", "fwdSysRoleInfoModify");
		url.addPara("jsid", getObject("jsid").getValue());
		openWindow("角色信息修改", url, "normal", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshPage();
		});
	}

	//刷新页面
	function refreshPage() {
		var url = new URL("urm.do", "fwdSysRoleInfoMng");
		url.addPara("jsid", getObject("jsid").getValue());
		AjaxUtil.asyncRefreshPage(url);
	}

	//批量分配角色
	function btnBatchAddClick(){
		var url = new URL("urm.do", "fwdSysRoleUserBatchAdd");
		url.addPara("jsid", getObject("jsid").getValue());
		openWindow("批量将该角色分配给用户", url, "normal", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshPage();
		});
	}

	//批量删除
	function btnBatchDelClick(){
		var grid = getObject("gridUser");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}

		var url = new URL("urm.do", "saveSysRoleUserBatchDel");
		url.addQueryGridSelectData("gridUser", "yhid");
		url.addPara("jsid", getObject("jsid").getValue());
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			refreshPage();
		}
	}
	
</script>