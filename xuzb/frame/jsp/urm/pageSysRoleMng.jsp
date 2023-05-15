<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:layout>
		<ef:topLayoutPanel height="85" border="false">
			<ef:form title="查询条件" name="formquery">
				<ef:hiddenInput name="jsid" />
				<ef:textinput name="jsmc" label="角色名称" onsearchclick="jsmcSearch();"
					prompt="可输入角色名称进行模糊查询..." />
				<ef:buttons colspan="4">
					<ef:button value="新增角色" iconCls="icon-user-add"
						functionid="sys010201" onclick="btnAddRoleClick();"></ef:button>
					<ef:button value="删除角色" iconCls="icon-user-delete"
						functionid="sys010202" onclick="btnDelRoleClick();"></ef:button>
					<ef:button value="清空" onclick="btnClearClick();"></ef:button>
				</ef:buttons>
			</ef:form>
		</ef:topLayoutPanel>
		<ef:leftLayoutPanel width="190" split="true" padding="5">
			<ef:tree name="roletree" onSelect="openRightPage"></ef:tree>
		</ef:leftLayoutPanel>
		<ef:centerLayoutPanel>
			<ef:loadPanel name="mainpaenl" />
		</ef:centerLayoutPanel>
	</ef:layout>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("jsmc").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formquery").clear();
		getObject("roletree").clear();
		getObject("mainpaenl").clear();
		getObject("jsmc").setReadOnly(false);
		getObject("jsmc").setSearchBtnDisabled(false);
		getObject("jsmc").focus();
	}

	//搜索
	function jsmcSearch() {
		var url = new URL("urm.do", "fwdChooseSysRole");
		url.addPara("jsmc", getObject("jsmc").getValue());
		openWindow("选择角色", url, "normal", chooseOneRoleAction);
	}

	//选中一个用户后的操作
	function chooseOneRoleAction(data) {
		if (chkObjNull(data)) {
			getObject("jsmc").focus();
			return;
		}
		var jsid = data.get("jsid");
		var jsmc = data.get("jsmc");
		getObject("jsid").setValue(jsid);
		getObject("jsmc").setValue(jsmc);

		getObject("jsmc").setReadOnly(true);
		getObject("jsmc").setSearchBtnDisabled(true);
		loadTree(jsid);
		openJsxxglPage();//打开个人信息页面
	}

	//角色新增
	function btnAddRoleClick() {
		var url = new URL("urm.do", "fwdSysRoleAdd");
		openWindow("新增角色", url, "normal", chooseOneRoleAction);
	}

	//加载树
	function loadTree(jsid) {
		if (chkObjNull(jsid)) {
			alert("请先选择角色");
			return;
		}
		var map = new HashMap();
		map.put("jsid", jsid);
		getObject("roletree").asyncLoadRemotData(
				"com.grace.frame.urm.biz.SysRoleMngTree", map);
	}

	//打开右侧页面
	function openRightPage(node) {
		var id = node.id;
		if ("jsxxgl" == node.id) {
			openJsxxglPage();//个人信息管理
		} else if ("gnqx" == node.id) {
			openGnqxPage();//功能权限
		} else if ("czrz" == node.id) {
			openCzrzPage();//操作日志
		}else{
			openRightPageFunc(node);
		}
		return;
	}

	function openRightPageFunc(node){
		var gnsj = node.attributes.gnsj;
		if (chkObjNull(gnsj)) {
			return;
		}
		var url = new URL(gnsj);
		getObject("mainpaenl").loadPage(url);
	}
	
	//个人功能权限
	function openGnqxPage() {
		var url = new URL("urm.do", "fwdSysRoleGnqxMng");
		url.addPara("jsid", getObject("jsid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//个人信息
	function openJsxxglPage() {
		var url = new URL("urm.do", "fwdSysRoleInfoMng");
		url.addPara("jsid", getObject("jsid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//操作日志
	function openCzrzPage() {
		var url = new URL("urm.do", "fwdSysRoleCzrzView");
		url.addPara("jsid", getObject("jsid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//删除
	function btnDelRoleClick() {
		var jsid = getObject("jsid").getValue();
		if (chkObjNull(jsid)) {
			alert("请选择角色信息。");
			return;
		}

		if (!confirm("是否确认对该角色进行删除（删除时会一并将分配给用户的角色收回）？")) {
			return;
		}

		if (!confirm("再次是否确认对该角色进行删除（删除时会一并将分配给用户的角色收回）？")) {
			return;
		}

		var url = new URL("urm.do", "saveSysRoleDel");
		url.addPara("jsid", jsid);
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			btnClearClick();
		}
	}
</script>