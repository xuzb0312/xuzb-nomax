<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title><%=GlobalVars.APP_NAME%></title>
		<meta name="renderer" content="webkit">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<meta name="viewport"
			content="width=device-width, initial-scale=1, maximum-scale=1">
		<jsp:include page="/shine/jsp/layout/frameImportFile.jsp"></jsp:include>
		<jsp:include page="/shine/jsp/layout/bizImportFile.jsp"></jsp:include>
		<script type="text/javascript">
	//初始化page页面内容
	function _initFramePage(url) {
		MsgBoxUtil.load();
		AjaxUtil.asyncRequest(url, _afterInitAction);
	}

	//数据请求返回后的action
	function _afterInitAction(data) {
		//清空body页面的数据
		$("body").html("");

		//检查是否存在异常
		if (AjaxUtil.checkException(data)) {
			AjaxUtil.showException(data);
			MsgBoxUtil.hideLoad();
			return;
		}
		//将数据放入body中
		$("body").html(data);

		// 整体操作向后延时0.2秒执行，等待渲染完成
		setTimeout(function() {
			__onLoadComplete();
			MsgBoxUtil.hideLoad();
		}, 200);
	}

	//页面加载完成后的操作
	function __onLoadComplete() {
		//增加默认的加载完成的函数。页面加载完成后执行的事件，默认定义为onLoadComplete
		if (typeof (onLoadComplete) != "undefined") {
			onLoadComplete();//可以在子页面书写此函数
		}
	}

	//页面加载完成后，去后台获取请求操作
	$(function() {
		_loadPageContent();
	});

	//加载页面内容
	function _loadPageContent() {
		var url = __getReqUrl();
		_initFramePage(url);
	}

	//全局常量
	var __URL;
	var __TYPE;
	//获取类型，window,tab,panel等
	function __getType() {
		if (typeof (__TYPE) == "undefined") {
			var winName = window.name;
			if (chkObjNull(winName)) {
				winName = "";
			}
			if (winName.startWith("tabCon_")) {
				__TYPE = "TAB";
			} else if (winName.startWith("panelCon_")) {
				__TYPE = "PANEL";
			} else {
				__TYPE = "WINDOW";
			}
		}
		return __TYPE;
	}
	//获取URL地址
	function __getReqUrl() {
		if (typeof (__URL) == "undefined") {
			var type = __getType();
			if ("WINDOW" == type) {
				var layerIndex = WinUtil.getIndex();
				if (!chkObjNull(layerIndex)) {
					var url = parent.$("body").data(
							"layer_data_in_" + layerIndex);
					__URL = url;
				} else {
					throw new Erro("无法获取请求URL。");
				}
			} else if ("TAB" == type) {
				var winName = window.name;
				if (!chkObjNull(winName)) {
					var mapIn = parent.$("body").data("tab_data_in_" + winName);
					__URL = mapIn.get("url");
				} else {
					throw new Erro("无法获取请求URL。");
				}
			} else if ("PANEL" == type) {
				var winName = window.name;
				if (!chkObjNull(winName)) {
					var url = parent.$("body").data("panel_data_in_" + winName);
					__URL = url;
				} else {
					throw new Erro("无法获取请求URL。");
				}
			} else {
				throw new Erro("页面类型定义不正确，无法获取请求URL。");
			}
		}
		return __URL;
	}

	//关闭窗口的方法-只对window窗口有效。
	function closeWindow(data) {
		var type = __getType();
		if ("WINDOW" == type) {
			if (!chkObjNull(WinUtil.getIndex())) {
				WinUtil.close(data);
				return true;
			} else {
				throw new Erro("关闭函数无法执行。");
			}
		} else if ("TAB" == type) {
			var winName = window.name;
			var mapIn = parent.$("body").data("tab_data_in_" + winName);
			var tpname = mapIn.get("tpname");
			var tabname = mapIn.get("tabname");
			var callback = mapIn.get("callback");
			if (!chkObjNull(callback)) {//执行回调，然后关闭
				callback(data);
			}
			parent.getObject(tabname).close(tpname);
		} else if ("PANEL" == type) {
			throw new Erro("暂不支持panel。");
		}
	}

	//按ESC自动关闭该页面
	$(document).keydown(function(e) {
		if (e.which == 27) {
			if (!chkObjNull(MsgBoxUtil.alertLayerIndex)) {//存在提示框的，按esc关闭提示框
				layer.close(MsgBoxUtil.alertLayerIndex);
				MsgBoxUtil.alertLayerIndex = null;
				return;
			}
			closeWindow();
			return;
		} else if (e.which == 116) {
			_loadPageContent();
			e.preventDefault();
			return;
		} else if (e.altKey && e.ctrlKey && e.which == 113) {
			//ctrl+alt+F2打开cookie设置窗口
			var url = new URL("shinetag.do", "fwdClientCookieSetting");
			WinUtil.open("浏览器个性化设置", {
				name : "&#xe613;",
				color : "#01AAED"
			}, url, "normal", null);
			e.preventDefault();
			return;
		} else if (e.shiftKey && e.ctrlKey && e.which == 82
				&& GlobalVars.DEBUG_MODE) {
			showJspPath();
			e.preventDefault();
			return;
		}
	});
</script>
	</head>
	<body>
	</body>
</html>
