<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formUser" rowcount="4">
		<ef:hiddenInput name="yhid" />
		<ef:textinput name="yhbh" label="用户编号"
			onsearchclick="chooseUserAction();" onchange="chooseUserAction();"
			required="true" />
		<ef:textinput name="yhmc" label="用户名称" readonly="true" />
		<ef:textinput name="password" label="用户密码" password="true" colspan="4"
			required="true" />
		<ef:text color="red" value="注：如若要给该用户永久赋权，请在权限管理中为本用户配置此权限功能。"
			align="center" colspan="4"/>
	</ef:form>
	<ef:buttons closebutton="false">
		<ef:button value="授权" iconCls="icon-user-go"
			onclick="btnAppTempRightClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		<ef:button value="取消" onclick="closeWindow();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		btnClearClick();
	}

	function btnClearClick() {
		getObject("formUser").clear();
		getObject("yhbh").focus();
	}

	//选择用户
	function chooseUserAction(){
		var functionid = '<%=(String)request.getAttribute("functionid")%>';
		if(chkObjNull(functionid)){
			alert("功能ID丢失，请重新进入");
			return;
		}
		var yhbh = getObject("yhbh").getValue();
		getObject("formUser").clear();
		//防止重复打开窗口
		if("open" == getObject("yhid").getValue()){
			return;
		}
		getObject("yhid").setValue("open");
		
		var url = new URL("taglib.do", "fwdChooseSysUser4TempRight");
		url.addPara("yhbh", yhbh);
		url.addPara("functionid",functionid);
		openTopWindow("选择用户", null, url, 500, 580, chooseOneUserAction);
	}

	//选中一个用户后的操作
	function chooseOneUserAction(data) {
		if (chkObjNull(data)) {
			getObject("yhid").clear();
			getObject("yhbh").focus();
			return;
		}
		var yhid = data.get("yhid");
		var yhbh = data.get("yhbh");
		var yhmc = data.get("yhmc");
		getObject("yhid").setValue(yhid);
		getObject("yhbh").setValue(yhbh);
		getObject("yhmc").setValue(yhmc);

		getObject("password").focus();
	}

	//授权
	function btnAppTempRightClick() {
		if (!getObject("formUser").chkFormData(true)) {
			return;
		}

		var yhid=getObject("yhid").getValue();
		var password=getObject("password").getValue();
		var functionid = '<%=(String)request.getAttribute("functionid")%>';
		if(chkObjNull(functionid)){
			alert("功能ID丢失，请重新进入");
			return;
		}
		if(chkObjNull(yhid)){
			alert("请选择用户信息！");
			btnClearClick();
			return;
		}
		if(chkObjNull(password)){
			alert("请输入密码！");
			getObject("password").focus();
			return;
		}
		
		var url = new URL("taglib.do", "checkUserTempRight");
		url.addPara("functionid",functionid);
		url.addPara("yhid", yhid);
		url.addPara("password", password);
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			if("true" == data){
				closeWindow(true);
			}else{
				alert("授权失败。");
			}
		}
	}
</script>
