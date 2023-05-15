<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:layout>
		<ef:topLayoutPanel height="85" border="false">
			<ef:form title="查询条件" name="formquery">
				<ef:hiddenInput name="yhid" />
				<ef:textinput name="yhbh" label="用户编号" onsearchclick="yhbhSearch();"
					prompt="可输入编号,名称进行模糊查询..." />
				<ef:textinput name="yhmc" label="用户名称" readonly="true" />
				<ef:buttons colspan="2">
					<ef:button value="新增用户" iconCls="icon-user-add"
						functionid="sys010101" onclick="btnAddUserClick();"></ef:button>
					<ef:button value="清空" onclick="btnClearClick();"></ef:button>
				</ef:buttons>
			</ef:form>
		</ef:topLayoutPanel>
		<ef:leftLayoutPanel width="190" split="true" padding="5">
			<ef:tree name="usertree" onSelect="openRightPage"></ef:tree>
		</ef:leftLayoutPanel>
		<ef:centerLayoutPanel>
			<ef:loadPanel name="mainpaenl" />
		</ef:centerLayoutPanel>
	</ef:layout>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("yhbh").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formquery").clear();
		getObject("usertree").clear();
		getObject("mainpaenl").clear();
		getObject("yhbh").setReadOnly(false);
		getObject("yhbh").setSearchBtnDisabled(false);
		getObject("yhbh").focus();
	}

	//搜索
	function yhbhSearch() {
		var url = new URL("urm.do", "fwdChooseSysUser");
		url.addPara("yhbh", getObject("yhbh").getValue());
		openWindow("选择用户", url, "normal", chooseOneUserAction);
	}

	//选中一个用户后的操作
	function chooseOneUserAction(data) {
		if (chkObjNull(data)) {
			getObject("yhbh").focus();
			return;
		}
		var yhid = data.get("yhid");
		var yhbh = data.get("yhbh");
		var yhmc = data.get("yhmc");
		getObject("yhid").setValue(yhid);
		getObject("yhbh").setValue(yhbh);
		getObject("yhmc").setValue(yhmc);

		getObject("yhbh").setReadOnly(true);
		getObject("yhbh").setSearchBtnDisabled(true);
		loadTree(yhid);
		openGrxxglPage();//打开个人信息页面
	}

	//用户新增
	function btnAddUserClick() {
		var url = new URL("urm.do", "fwdSysUserAdd");
		openWindow("新增用户", url, "normal", chooseOneUserAction);
	}

	//加载树
	function loadTree(yhid) {
		if (chkObjNull(yhid)) {
			alert("请先选择用户");
			return;
		}
		var map = new HashMap();
		map.put("yhid", yhid);
		getObject("usertree").asyncLoadRemotData(
				"com.grace.frame.urm.biz.SysUserMngTree", map);
	}

	//打开右侧页面
	function openRightPage(node) {
		var id = node.id;
		if ("grxxgl" == node.id) {
			openGrxxglPage();//个人信息管理
		} else if ("jsgl" == node.id) {
			openJsglPage();//角色管理
		} else if ("gnqx" == node.id) {
			openGnqxPage();//功能权限
		} else if ("sjqx" == node.id) {
			opneSjqxPage();//数据权限
		} else if ("tsqx" == node.id) {
			opneTsqxPage();//特殊权限
		} else if ("fwfwqx" == node.id) {
			openFwfwqxPage();//服务访问权限
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
		var url = new URL("urm.do", "fwdSysUserGnqxMng");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//个人数据权限管理
	function opneSjqxPage() {
		var url = new URL("urm.do", "fwdSysUserSjqxMng");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//特殊权限
	function opneTsqxPage() {
		var url = new URL("urm.do", "fwdSysUserTsqxMng");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//服务访问权限
	function openFwfwqxPage() {
		var url = new URL("urm.do", "fwdSysUserFwfwqxMng");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//角色管理
	function openJsglPage() {
		var url = new URL("urm.do", "fwdSysUserJsglMng");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//个人信息
	function openGrxxglPage(){
		var url = new URL("urm.do", "fwdSysUserGrxxglMng");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//操作日志
	function openCzrzPage(){
		var url = new URL("urm.do", "fwdSysUserCzrzView");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}
</script>