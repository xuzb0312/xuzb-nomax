<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String basePath = GlobalVars.SYS_BASE_PATH;
	if ("".equals(basePath)) {
		basePath = request.getScheme() + "://"
				+ request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath()
				+ "/";
	}
%>
<%-- 框架使用的js,css引入，对于业务系统的js,css引入不允许放置到此处，框架升级会直接覆盖此文件 --%>
<%--系统的图标 --%>
<link rel="icon" href="./<%=GlobalVars.APP_ICON%>" type="image/x-icon" />
<link rel="shortcut icon" href="./<%=GlobalVars.APP_ICON%>">
<link rel="Bookmark" href="./<%=GlobalVars.APP_ICON%>">

<%--系统的样式代码css --%>
<link rel="stylesheet" type="text/css" href="./frame/css/base.css?v=1.1" />
<link rel="stylesheet" type="text/css"
	href="./frame/plugins/easyui/themes/new_default/easyui.css?v=1.3.5" />
<link rel="stylesheet" type="text/css"
	href="./frame/plugins/easyui/themes/icon.css?v=1.3.5">
<link rel="stylesheet" type="text/css"
	href="./frame/plugins/easyui/themes/icon-extends.css?v=1.0">
<link rel="stylesheet" type="text/css"
	href="./frame/plugins/easyui/extends/jeasyui.extensions.css?v=1.0">
<link rel="stylesheet" type="text/css"
	href="./frame/plugins/jqgrid/css/jquery-ui.min.css?v=1.11.4" />
<link rel="stylesheet" type="text/css"
	href="./frame/plugins/jqgrid/css/ui.jqgrid.css?v=4.4.3" />
<link rel="stylesheet" type="text/css"
	href="./frame/plugins/intro/introjs.css?v=2.9.3" />

<%--系统的JS代码 --%>
<script type="text/javascript"
	src="./frame/js/jquery-1.7.2.min.js?v=1.7.2"></script>
<script type="text/javascript" src="./frame/plugins/json/json2.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/md5/md5.min.js?v=1.0"></script>
<%--自定义JS引入 --%>
<script type="text/javascript" src="./frame/js/util/URL.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/HashMap.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/PinyinUtil.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/List.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/TreeObj.js?v=1.2"></script>
<script type="text/javascript" src="./frame/js/util/LayoutObj.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/TabObj.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/MenuObj.js?v=1.1"></script>
<script type="text/javascript" src="./frame/js/util/ButtonObj.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/FormObj.js?v=1.2"></script>
<script type="text/javascript"
	src="./frame/js/util/TextInputObj.js?v=1.6"></script>
<script type="text/javascript" src="./frame/js/util/TextObj.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/util/DropdownListObj.js?v=1.2"></script>
<script type="text/javascript"
	src="./frame/js/util/MultiDropdownListObj.js?v=1.5"></script>
<script type="text/javascript"
	src="./frame/js/util/MultiSelectBoxObj.js?v=1.1"></script>
<script type="text/javascript"
	src="./frame/js/util/CheckboxListObj.js?v=1.2"></script>
<script type="text/javascript"
	src="./frame/js/util/RadiobuttonListObj.js?v=1.1"></script>
<script type="text/javascript"
	src="./frame/js/util/HiddenInputObj.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/util/grid/QueryGridObj.js?v=1.9"></script>
<script type="text/javascript" src="./frame/js/util/FileBoxObj.js?v=1.1"></script>
<script type="text/javascript" src="./frame/js/util/PrinterObj.js?v=1.3"></script>
<script type="text/javascript" src="./frame/js/util/UEditorObj.js?v=1.2"></script>
<script type="text/javascript"
	src="./frame/js/util/LoadPanelObj.js?v=1.2"></script>
<script type="text/javascript"
	src="./frame/js/util/TextareaObj.js?v=1.2"></script>
<script type="text/javascript" src="./frame/js/util/AjaxUtil.js?v=1.2"></script>
<script type="text/javascript"
	src="./frame/js/util/commonJsUtil.js?v=1.3"></script>
<script type="text/javascript" src="./frame/js/util/CookieUtil.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/DateObj.js?v=1.1"></script>
<script type="text/javascript"
	src="./frame/js/util/CalendarObj.js?v=1.1"></script>
<script type="text/javascript" src="./frame/js/util/DateUtil.js?v=1.1"></script>
<script type="text/javascript" src="./frame/js/util/MsgBox.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/util/ProgressBar.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/util/StringExtend.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/NumberUtil.js?v=1.0"></script>
<%--EasyUI引入 --%>
<script type="text/javascript"
	src="./frame/plugins/easyui/jquery.easyui.min.js?v=1.3.5.1"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/locale/easyui-lang-zh_CN.js?v=1.3.5"></script>
<%--EasyUI扩展引入 --%>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jquery.jdirk.js?v=1.1"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jeasyui.extensions.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jeasyui.extensions.form.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jeasyui.extensions.menu.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jeasyui.extensions.panel.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jeasyui.extensions.window.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jeasyui.extensions.dialog.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jeasyui.extensions.tabs.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/easyui/extends/jeasyui.extensions.validatebox.js?v=1.0"></script>
<%--Jqgrid引入 --%>
<script type="text/javascript"
	src="./frame/plugins/jqgrid/i18n/grid.locale-cn.js?v=4.4.3"></script>
<script type="text/javascript"
	src="./frame/plugins/jqgrid/jquery.jqGrid.min.js?v=4.4.3"></script>
<%--文本mask验证插件引入--%>
<script type="text/javascript"
	src="./frame/plugins/jquery.mask/jquery.maskedinput.js?v=1.3.1"></script>
<%--文本mask验证插件引入--%>
<script type="text/javascript"
	src="./frame/plugins/laydate/laydate.js?v=1.1"></script>
<script type="text/javascript"
	src="./frame/js/mask/DateTimeMask.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/mask/DateTimeMaskExe.js?v=1.1"></script>
<script type="text/javascript" src="./frame/js/mask/NumberMask.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/mask/NumberMaskExe.js?v=1.1"></script>
<%--日期选择框 --%>
<script type="text/javascript"
	src="./frame/plugins/jquery.mask/jquery.maskedinput.js?v=1.3.1"></script>
<script type="text/javascript"
	src="./frame/plugins/resizeEnd/jQuery.resizeEnd.min.js?v=1.0"></script>
<%--UEditor的引入 --%>
<script type="text/javascript"
	src="./frame/plugins/ueditor/ueditor.config.js?v=1.4.3"></script>
<script type="text/javascript"
	src="./frame/plugins/ueditor/ueditor.all.min.js?v=1.4.3_0930"></script>
<script type="text/javascript"
	src="./frame/js/util/GlobalOperate.js?v=1.0"></script>
<%--引导介绍插件 --%>
<script type="text/javascript" src="./frame/plugins/intro/intro.js?v=2.9.3"></script>
<%--系统级的常量，从java转换到javascript--%>
<script type="text/javascript">
	var GlobalVars = __initSysGlobalVars();
	function __initSysGlobalVars() {
		var map = new HashMap();
		var debugmode = '<%=GlobalVars.DEBUG_MODE%>';
		map.put("DEBUG_MODE",("true" == debugmode));
		var basePath = '<%=basePath%>';
		map.put("BASE_PATH", basePath);
		var viewType = '<%=GlobalVars.VIEW_TYPE%>';
		map.put("VIEW_TYPE", viewType);
		return map.values;
	};
</script>
<%
if("v2".equalsIgnoreCase(GlobalVars.VIEW_TYPE)){
%>
<script type="text/javascript" src="./frame/v2/js/base.js?v=1.0"></script>
<link rel="stylesheet" type="text/css"
	href="./frame/v2/css/base.css?v=1.2" />
<%
}
%>