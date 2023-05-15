<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String yhlx = (String) request.getAttribute("yhlx");
%>
<ef:body>
	<ef:form title="个人信息" name="formGrxx" dataSource="userinfo">
		<ef:hiddenInput name="yhid" />
		<ef:textinput name="yhbh" label="用户编号" readonly="true" />
		<ef:textinput name="yhmc" label="用户名称" readonly="true" />
		<ef:dropdownList name="yhlx" label="用户类型" code="YHLX" readonly="true"
			loadOpt="false" />
		<ef:dropdownList name="zjlx" label="证件类型" code="YXZJLX"
			loadOpt="false" readonly="true" />
		<ef:textinput name="zjhm" label="证件号码" readonly="true" />
		<ef:textinput name="ssjbjgid" label="所属经办机构" readonly="true" />
		<ef:textinput name="sjhm" label="手机号码" readonly="true"
			validType="mobile" />
		<ef:textinput name="ssjgmc" label="所属机构名称" readonly="true" colspan="4" />
		<ef:textinput name="dzyx" label="电子邮箱" readonly="true"
			validType="email" />
		<ef:textinput name="ssjgbm" label="所属机构部门" readonly="true" colspan="4" />
		<ef:dropdownList name="yhzt" code="YHZT" label="用户状态" readonly="true"></ef:dropdownList>
		<ef:textinput name="zxrq" label="注销日期" mask="yyyy-MM-dd"
			sourceMask="yyyyMMdd" dataType="date" readonly="true" />
		<ef:textinput name="zxjbsj" label="注销经办时间" mask="yyyy-MM-dd hh:mm:ss"
			sourceMask="yyyyMMddhhmmss" dataType="date" readonly="true" />
		<ef:textinput name="zxyy" label="注销原因" readonly="true" colspan="6" />
		<%
			if ("C".equals(yhlx)) {//服务注册用户把密钥展示在页面上
		%>
		<ef:textinput name="yhmy" label="用户密钥" readonly="true" colspan="6" />
		<ef:text color="red"
			value="注：请注意保密用户密钥，如需更改请使用重置密码功能，重置后可能会影响服务使用方的请求，请即时通知服务使用方更换密钥。"
			align="center" />
		<%
			}
		%>

	</ef:form>
	<ef:buttons>
		<ef:button value="用户信息修改" iconCls="icon-user-edit"
			functionid="sys01010b" onclick="btnModifyClick();"></ef:button>
		<ef:button value="用户注销" iconCls="icon-user-delete"
			functionid="sys01010c" onclick="btnDesClick();"></ef:button>
		<ef:button value="撤销用户注销" iconCls="icon-user-go"
			functionid="sys01010d" onclick="btnCancelDesClick();"></ef:button>
		<ef:button value="密码重置" iconCls="icon-drive-user"
			functionid="sys01010e" onclick="btnResetPwdClick();"></ef:button>
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
		var url = new URL("urm.do", "fwdSysUserInfoModify");
		url.addPara("yhid", getObject("yhid").getValue());
		openWindow("用户信息修改", url, "normal", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshPage();
		});
	}

	//用户注销
	function btnDesClick() {
		var yhzt = getObject("yhzt").getValue();
		if("1" != yhzt){
			alert("该用户状态为注销，无法再次进行注销操作。");
			return;
		}
		var url = new URL("urm.do", "fwdSysUserDestroy");
		url.addPara("yhid", getObject("yhid").getValue());
		openWindow("用户注销", url, "small", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshPage();
		});
	}

	//撤销用户注销
	function btnCancelDesClick() {
		var yhzt = getObject("yhzt").getValue();
		if("1" == yhzt){
			alert("该用户状态为正常，无法进行撤销注销操作。");
			return;
		}
		var url = new URL("urm.do", "cancelSysUserDestroy");
		url.addPara("yhid", getObject("yhid").getValue());
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("撤销成功。");
			refreshPage();
		}
		
	}

	//密码重置
	function btnResetPwdClick() {
		var url = new URL("urm.do", "fwdSysUserResetPwd");
		url.addPara("yhid", getObject("yhid").getValue());
		openWindow("密码重置", url, "small", function(data){
			if(chkObjNull(data)){
				return;
			}
			refreshPage();
		});
	}

	//刷新页面
	function refreshPage() {
		var url = new URL("urm.do", "fwdSysUserGrxxglMng");
		url.addPara("yhid", getObject("yhid").getValue());
		AjaxUtil.asyncRefreshPage(url);
	}
</script>