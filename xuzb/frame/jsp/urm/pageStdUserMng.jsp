<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:layout>
		<ef:topLayoutPanel height="105" border="false">
			<ef:form title="查询条件" name="formquery">
				<ef:hiddenInput name="ryid" />
				<ef:textinput name="xm" label="考生姓名" onsearchclick="xmSearch();"
					prompt="可输入姓名,拼音进行模糊查询..." />
				<ef:textinput name="xmpy" label="考生姓名缩写" readonly="true" />
				<ef:textinput name="yxzjhm" label="证件号码" readonly="true" />
				<ef:buttons colspan="2">
					<ef:button value="新增考生" iconCls="icon-user-add"
						functionid="sys010301" onclick="btnAddStdClick();"></ef:button>
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
		getObject("ryid").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formquery").clear();
		getObject("usertree").clear();
		getObject("mainpaenl").clear();
		getObject("xm").setReadOnly(false);
		getObject("xm").setSearchBtnDisabled(false);
		getObject("xm").focus();
	}

	//搜索
	function xmSearch() {
		var url = new URL("urm.do", "fwdChooseStdUser");
		url.addPara("xm", getObject("xm").getValue());
		openWindow("选择用户", url, "normal", chooseOneUserAction);
	}

	//选中一个用户后的操作
	function chooseOneUserAction(data) {
		if (chkObjNull(data)) {
			getObject("xm").focus();
			return;
		}
		var ryid = data.get("ryid");
		var xm = data.get("xm");
		var xmpy = data.get("xmpy");
		var yxzjhm = data.get("yxzjhm");
		getObject("ryid").setValue(ryid);
		getObject("xm").setValue(xm);
		getObject("xmpy").setValue(xmpy);
		getObject("yxzjhm").setValue(yxzjhm);

		getObject("xm").setReadOnly(true);
		getObject("xm").setSearchBtnDisabled(true);
		loadTree(ryid);
		openGrxxglPage();//打开个人信息页面
	}

	//用户新增
	function btnAddStdClick() {
		var url = new URL("urm.do", "fwdStdUserAdd");
		url.addPara("xm", getObject("xm").getValue());
		openWindow("新增考生", url, "normal", xmSearch);
	}

	//加载树
	function loadTree(ryid) {
		if (chkObjNull(ryid)) {
			alert("请先选择用户");
			return;
		}
		var map = new HashMap();
		map.put("ryid", ryid);
		getObject("usertree").asyncLoadRemotData(
				"com.grace.frame.urm.biz.StdUserMngTree", map);
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
		var url = new URL("urm.do", "fwdStdUserGnqxMng");
		url.addPara("ryid", getObject("ryid").getValue());
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
		var url = new URL("urm.do", "fwdStdUserFwfwqxMng");
		url.addPara("ryid", getObject("ryid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//角色管理
	function openJsglPage() {
		var url = new URL("urm.do", "fwdSysUserJsglMng");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//考生信息
	function openGrxxglPage(){
		var url = new URL("urm.do", "fwdStdUserGrxxglMng");
		url.addPara("ryid", getObject("ryid").getValue());
		getObject("mainpaenl").loadPage(url);
	}

	//操作日志
	function openCzrzPage(){
		var url = new URL("urm.do", "fwdSysUserCzrzView");
		url.addPara("yhid", getObject("yhid").getValue());
		getObject("mainpaenl").loadPage(url);
	}
</script>