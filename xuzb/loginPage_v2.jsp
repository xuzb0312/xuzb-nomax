<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%@ page import="com.grace.frame.login.LoginUtil"%>
<%
	String islogin = LoginUtil.isUserLogin(request) ? "1" : "0";
	String debugtxt = "";
	String debugtitle = "";
	if (GlobalVars.DEBUG_MODE) {
		debugtxt = "<span style='font-size:14px;'>&nbsp;(调试模式)</span>";
		debugtitle="(调试模式)";
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><%=GlobalVars.SYS_DBNAME%><%=GlobalVars.APP_NAME%><%=debugtitle%> 系统登录</title>
		<jsp:include page="frame/jsp/layout/frameImportFile.jsp"></jsp:include>
		<jsp:include page="frame/jsp/layout/bizImportFile.jsp"></jsp:include>
		<link rel="stylesheet" type="text/css"
			href="./frame/v2/css/login.css?v=1.0" />
		<script type="text/javascript">
		//判断是否登录，如果已经登录直接转向业务操作界面
		var islogin = "<%=islogin%>";
		if ("1" == islogin) {
			top.location.href = "login.do?method=fwdMainPage";
		}
		</script>
	</head>
	<body>
		<div class="login_box">
			<div class="login_l_img">
				<div class="systitle">
					<%=GlobalVars.SYS_DBNAME%><%=GlobalVars.APP_NAME%><%=debugtxt%>
				</div>
				<img src="frame/v2/img/login-img.png" />
			</div>
			<div class="login">
				<div class="login_logo">
					<img src="frame/v2/img/login_logo.png" />
				</div>
				<div class="login_name">
					<p>
						系统用户登录
					</p>
				</div>
				<input id="yhbh" type="text" value="请输入用户名" />
				<span id="pwd_text">请输入密码</span>
				<input id="pwd" type="password" style="display: none;" />
				<input value="登录" style="width: 100%;" type="button" id="btnLogin" />
			</div>
			<div style="clear: both;"></div>
		</div>
		<div class="copyright">
			Copyright ©2018 诺码信科技(nomax.cn) All Rights Reserved
			<br />
			网址：
			<a href='http://www.nomax.cn' target='_blank'>http://www.nomax.cn</a>&nbsp;&nbsp;邮箱:
			<a href='mailto:hr@nomax.top'>hr@nomax.top</a>
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
		$("#yhbh").on("focus",
		function() {
			if ("请输入用户名" == $(this).val()) {
				$(this).val("");
			}
		});
		$("#yhbh").on("blur",
		function() {
			if (chkObjNull($(this).val())) {
				$(this).val("请输入用户名");
			} else {
				pwdFocus();
			}
		});
		$("#pwd_text").on("click",
		function() {
			pwdFocus();
		});
		$("#pwd").on("blur",
		function() {
			if (chkObjNull($(this).val())) {
				$(this).hide();
				$("#pwd_text").show();
			}
		});
		$("#btnLogin").on("click",
		function() {
			login();
		});

		//如果登录过了系统系统自动记录用户名
		$("#yhbh").val(CookieUtil.get("sys_yhbh"));

		//焦点
		$("#yhbh").focus();

		//回车键的出来
		$('#yhbh').bind('keypress',
		function(event) {
			if (event.keyCode == "13") {
				if (chkSelectorNull("#pwd")) {
					pwdFocus();
				} else {
					login();
				}
			}
		});
		$('#pwd').bind('keypress',
		function(event) {
			if (event.keyCode == "13") {
				login();
			}
		});
	});

	function pwdFocus(){
		$("#pwd_text").hide();
		$("#pwd").show();
		$("#pwd").focus();
	}

	//登录操作
	function login() {
		//判断空
		if (chkSelectorNull("#yhbh")) {
			$("#yhbh").focus();
			alert("用户编号不允许为空！");
			return;
		}
		if (chkSelectorNull("#pwd")) {
			pwdFocus();
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
		if (null == str || "" == str || "请输入用户名" == str) {
			return true;
		} else {
			return false;
		}
	}
</script>
