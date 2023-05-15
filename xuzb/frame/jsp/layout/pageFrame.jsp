<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String panelid = (String) request.getAttribute("panelid");//如果不为空则是loadPanel过来的请求
	if (null == panelid) {
		panelid = "";
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title><%=GlobalVars.APP_NAME%></title>
		<jsp:include page="frameImportFile.jsp"></jsp:include>
		<jsp:include page="bizImportFile.jsp"></jsp:include>
		<script type="text/javascript">
	var _panelid="<%=panelid%>";
		
	$.parser.auto = false;//easyui自动解析该页面置为false;

	//初始化page页面内容
	function _initFramePage(url) {
		showLoading();
		AjaxUtil.asyncRequest(url, _afterInitAction);
	}

	//数据请求返回后的action
	function _afterInitAction(data) {
		//清空body页面的数据
		$("body").html("");

		//检查是否存在异常
		if (AjaxUtil.checkException(data)) {
			AjaxUtil.showException(data);
			hideLoading();
			return;
		}
		//将数据放入body中
		$("body").html(data);

		//easyui重新渲染
		$.parser.parse();

		if ($.browser.msie) {// 对于ie浏览器该处不最后执行，向后延时0.2秒执行
			setTimeout(__onLoadComplete,200);
		}else{
			__onLoadComplete();
		}

		hideLoading();
	}

	//页面加载完成后的操作
	function __onLoadComplete(){
		//grid自动调节高度
		$("[obj_type='querygrid']").each(function(){
			var grid = getObject($(this));
			grid.selfAdaptHeight();
		});
		
		//增加默认的加载完成的函数。页面加载完成后执行的事件，默认定义为onLoadComplete
		if (typeof (onLoadComplete) != "undefined") {
			onLoadComplete();//可以在子页面书写此函数
		}
		
		//待办事项的默认加载函数-操作
		if (typeof (onProceeding) != "undefined") {
			var url = __getReqUrl();
			var reqPara = url.getParas();
			var mapReqPara = new HashMap(reqPara);
			if(mapReqPara.containsKey("__dbsxpara")&&mapReqPara.containsKey("__sxid")){
				onProceeding(mapReqPara.get("__dbsxpara"), mapReqPara.get("__sxid"));//可以在子页面书写此函数
			}
		}
	}

	//调整grid宽度
	function __gridWidthAdapt() {
		$("[obj_type='querygrid']").each(function() {
			var grid = getObject($(this));
			grid.tableobj.setGridWidth($("#" + grid.id + "_width_use").width() - 5);
			grid.selfAdaptHeight();
		});
		$.parser.parse();
	}
	
	//页面加载完成后，去后台获取请求操作
	$(function() {
		_loadPageContent();
	});

	//加载页面内容
	function _loadPageContent(){
		var url = __getReqUrl();
		_initFramePage(url);
	}

	//获取URL地址
	function __getReqUrl(){
		if(chkObjNull(_panelid)){
			return window.__requrl;
		}else{
			return window.parent.getObject(_panelid).obj.data("requrl");
		}
	}
	
	var __data;//存储回调返回数据
	//关闭窗口的方法-只对window窗口有效。
	function closeWindow(data) {
		if(!chkObjNull(_panelid)){
			window.parent.getObject(_panelid).close();
			return true;
		}
		if (typeof (data) != "undefined") {
			__data = data;
		}
		if ("window" == window.__opener_type) {
			var con_id = window.__con_id;
			window.parent.$("#" + con_id).window("close");
		} else if ("tabpage" == window.__opener_type) {
			window.parent.getObject(window.__con_id).closeByTitle(
					window.__con_tabpage_title);
		} else {
			throw new Erro("关闭函数无法执行。");
		}
	}

	//按ESC自动关闭该页面
	$(document).keydown(function(e) {
		if (e.which == 27) {
			closeWindow();
			return;
		} else if (e.which == 116) {
			_loadPageContent();
			e.preventDefault();
			return;
		} else if (e.altKey && e.ctrlKey && e.which == 113) {
			//ctrl+alt+F2打开cookie设置窗口
			var url = new URL("taglib.do", "fwdClientCookieSetting");
			openWindow("浏览器个性化设置", "icon-cog", url, "normal", null);
			e.preventDefault();
			return;
		} else if (e.shiftKey && e.ctrlKey && e.which == 82 && GlobalVars.DEBUG_MODE) {
			showJspPath();
			e.preventDefault();
			return;
		} else if (e.which == 112) {
			//F1帮助
			startIntro();
			e.preventDefault();
			return;
		}
	});
</script>
	</head>
	<body>
	</body>
</html>