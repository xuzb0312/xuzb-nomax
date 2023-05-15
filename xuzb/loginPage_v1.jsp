<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%@ page import="com.grace.frame.login.LoginUtil"%>
<%
	String islogin = LoginUtil.isUserLogin(request) ? "1" : "0";
	String debugtxt = "";
	if (GlobalVars.DEBUG_MODE) {
		debugtxt = "(调试模式)";
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><%=GlobalVars.APP_NAME%><%=debugtxt%> 系统登录</title>
		<jsp:include page="frame/jsp/layout/frameImportFile.jsp"></jsp:include>
		<jsp:include page="frame/jsp/layout/bizImportFile.jsp"></jsp:include>
		<script type="text/javascript">
		//判断是否登录，如果已经登录直接转向业务操作界面
		var islogin = "<%=islogin%>";
		if ("1" == islogin) {
			top.location.href = "login.do?method=fwdMainPage";
		}
		</script>
	</head>
	<body>
		<div id="loginDialog" class="easyui-dialog"
			title="<%=GlobalVars.APP_NAME%><%=debugtxt%> 系统登录"
			style="width: 400px; height: 200px; padding: 10px">
			<div class="easyui-layout" data-options="fit:true">
				<div data-options="region:'north',border:false"
					style="height: 50px; padding: 15px 10px 0px 10px; font-size: 14px; text-align: center;">
					用户编号：
					<input class="easyui-validatebox" type="text" name="yhbh" id="yhbh"
						data-options="required:true" style="height: 22px;"></input>
				</div>
				<div data-options="region:'south',border:false"
					style="height: 50px; padding: 0px 10px 0px 10px; font-size: 14px; text-align: center;">
					用户密码：
					<input class="easyui-validatebox" type="password" name="pwd"
						id="pwd" data-options="required:true" style="height: 22px;">
					</input>
				</div>
			</div>
		</div>
	</body>
</html>
<script type="text/javascript">
	$(function() {
		// 浏览器兼容问题
		if (!(typeof document.createElement('canvas').getContext === "function")) {
			var str = "<div style='text-align:center;position:absolute;top:0px;font-size:13px;padding:3px 0px;width:100%;min-height:18px;background:#ff9;border-bottom:1px #cc6 solid;'>" 
				+ "提示: 您正在使用不兼容的浏览器访问本系统。请升级最新新版本IE、Chrome(谷歌浏览器）或Firefox（火狐）浏览器来使用本系统，360浏览器等其他浏览器请使用极速模式访问！" 
				+ "</div>";
			$("body").append(str);
		}
	});
	
	$(function() {
		$("#loginDialog").dialog( {
			iconCls : 'icon-user',
			closable : false,
			buttons : [ {
				text : '登录',
				iconCls : 'icon-ok',
				handler : function() {
					login();
				}
			}, {
				text : '取消',
				iconCls : 'icon-cross',
				handler : function() {
					$("#yhbh").val("");
					$("#pwd").val("");
					$("#yhbh").focus();
				}
			} ]

		});
		//如果登录过了系统系统自动记录用户名
		$("#yhbh").val(CookieUtil.get("sys_yhbh"));

		//焦点
		$("#yhbh").focus();

		//回车键的出来
		$('#yhbh').bind('keypress', function(event) {
			if (event.keyCode == "13") {
				if (chkSelectorNull("#pwd")) {
					$("#pwd").focus();
				} else {
					login();
				}
			}
		});
		$('#pwd').bind('keypress', function(event) {
			if (event.keyCode == "13") {
				login();
			}
		});
	});

	//登录操作
	function login() {
		//判断空
		if (chkSelectorNull("#yhbh")) {
			$("#yhbh").focus();
			alert("用户编号不允许为空！");
			return;
		}
		if (chkSelectorNull("#pwd")) {
			$("#pwd").focus();
			alert("用户密码不允许为空！");
			return;
		}

		var yhbh = $("#yhbh").val();
		var pwd = $("#pwd").val();

		//设置cookie记录用名
		CookieUtil.set("sys_yhbh", yhbh, 7);
		showLoading();

		//请求后台验证登录
		var url = new URL("login.do", "doLogin");
		url.addPara("yhbh", yhbh);
		url.addPara("pwd", encodeStrByMd5(pwd));
		AjaxUtil.asyncRequest(url, afterLoginAction);
	}

	//登录完成后的操作
	function afterLoginAction(data) {
		$("#yhbh").focus();

		if (AjaxUtil.checkException(data)) {
			AjaxUtil.showException(data);
			hideLoading();
			return;
		}
		var map = new HashMap(data);
		var flag = map.get("flag");
		if ("1" == flag) {
			var msg = map.get("msg");
			if (null != msg && "" != msg) {
				alert(msg);//提示信息，允许继续登录
			}
			top.location.href = "login.do?method=fwdMainPage";
		} else {
			alert("用户登录失败，请重试！");
			hideLoading();
		}
	}

	//检查选择器是否为空
	function chkSelectorNull(selector) {
		var str = $(selector).val();
		if (null == str || "" == str) {
			return true;
		} else {
			return false;
		}
	}
</script>
