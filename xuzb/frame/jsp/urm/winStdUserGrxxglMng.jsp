<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String yhlx = (String) request.getAttribute("yhlx");
%>
<ef:body>
	<ef:form title="考生信息" name="formGrxx" dataSource="userinfo">
		<ef:hiddenInput name="ryid" />
		<ef:textinput name="xm" label="姓名" readonly="true" />
		<ef:textinput name="xmpy" label="姓名缩写" readonly="true" />
		<ef:textinput name="xb" label="性别" readonly="true" />
		<ef:textinput name="nl" label="年龄" readonly="true" />
		<ef:dropdownList name="yxzjlx" label="证件类型" code="YXZJLX"
			loadOpt="false" readonly="true" />
		<ef:textinput name="yxzjhm" label="证件号码" readonly="true" />
		<ef:textinput name="jtzz" label="家庭住址" readonly="true" />
		<ef:textinput name="bzje" label="补助金额" readonly="true" />
		<ef:textinput name="lxdh" label="联系电话" readonly="true"
					  validType="mobile" />
		<ef:textinput name="sjhm" label="手机号码" readonly="true"
			validType="mobile" />
		<ef:textinput name="rzrq" label="入职时间" mask="yyyy-MM-dd"
			sourceMask="yyyyMMdd" dataType="date" readonly="true" />
		<ef:textinput name="xxgxsj" label="信息更新时间" mask="yyyy-MM-dd hh:mm:ss"
			sourceMask="yyyyMMddhhmmss" dataType="date" readonly="true" />


	</ef:form>
	<ef:buttons>
		<ef:button value="考生信息修改" iconCls="icon-user-edit"
			functionid="sys010302" onclick="btnModifyClick();"></ef:button>
		<ef:button value="考生信息删除" iconCls="icon-user-delete"
			functionid="sys010303" onclick="btnResetPwdClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		//注销用户，使用红色字体标识
		if (getObject("yhzt").getValue() != "1") {
			getObject("yhzt").setLabelColor("red");
			getObject("yhzt").setColor("red");
		}
	}

	//用户信息修改
	function btnModifyClick() {
		var url = new URL("urm.do", "fwdStdUserInfoModify");
		url.addPara("ryid", getObject("ryid").getValue());
		openWindow("用户信息修改", url, "normal", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshPage();
		});
	}



	//密码重置   修改为删除考生
	function btnResetPwdClick() {
		var url = new URL("urm.do", "fwdStdUserDel");
		url.addPara("ryid", getObject("ryid").getValue());
		openWindow("删除考生", url, "small", function(data){
			if(chkObjNull(data)){
				return;
			}
			xmSearch();
		});
	}

	//搜索
	function xmSearch() {
		var url = new URL("urm.do", "fwdChooseStdUser");
		url.addPara("xm","");
		openWindow("选择用户", url, "normal", chooseOneUserAction);
	}
	//考生信息
	function openGrxxglPage(){
		var url = new URL("urm.do", "fwdStdUserGrxxglMng");
		url.addPara("ryid", getObject("ryid").getValue());
		getObject("mainpaenl").loadPage(url);
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
		// loadTree(ryid);
		openGrxxglPage();//打开个人信息页面
	}

	//刷新页面
	function refreshPage() {
		var url = new URL("urm.do", "fwdStdUserGrxxglMng");
		url.addPara("ryid", getObject("ryid").getValue());
		AjaxUtil.asyncRefreshPage(url);
	}
</script>