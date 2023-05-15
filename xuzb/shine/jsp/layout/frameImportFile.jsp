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
<link rel="stylesheet" type="text/css"
	href="./shine/plugins/layui/css/layui.css?v=2.2.2" />
<link rel="stylesheet" type="text/css"
	href="./shine/plugins/font-awesome/css/font-awesome.min.css?v=4.7.0" />
<link rel="stylesheet" type="text/css" href="./shine/css/base.css?v=1.0" />
<%--系统的JS代码 --%>
<script type="text/javascript"
	src="./shine/plugins/jquery/jquery.min.js?v=1.12.3"></script>
<script type="text/javascript"
	src="./shine/plugins/layui/layui.js?v=2.2.2"></script>
<%--layui的模块化加载缓存问题，如果更改了模块，则此处的版本搞进行一次调整，则客户端缓存会相应的调整 --%>
<script type="text/javascript">
	layui.config( {
		version : '20171122' //为了更新 js 缓存，可忽略
		});
</script>
<%--让IE8/9支持媒体查询，从而兼容栅格 --%>
<!--[if lt IE 9]>
  <script src="./shine/plugins/mediaQueries/html5.min.js"></script>
  <script src="./shine/plugins/mediaQueries/respond.min.js"></script>
<![endif]-->
<%--工具方法 --%>
<script type="text/javascript" src="./frame/js/util/HashMap.js?v=1.0"></script>
<script type="text/javascript" src="./shine/js/util.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/DateObj.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/DateUtil.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/util/StringExtend.js?v=1.0"></script>
<script type="text/javascript" src="./frame/js/util/NumberUtil.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/mask/DateTimeMask.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/mask/DateTimeMaskExe.js?v=1.1"></script>
<script type="text/javascript" src="./frame/js/mask/NumberMask.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/js/mask/NumberMaskExe.js?v=1.1"></script>
<script type="text/javascript" src="./frame/js/util/CookieUtil.js?v=1.0"></script>
<script type="text/javascript"
	src="./frame/plugins/md5/md5.min.js?v=1.0"></script>
<%--标签库对象 --%>
<script type="text/javascript" src="./shine/js/tag/ButtonObj.js?v=1.0"></script>
<script type="text/javascript" src="./shine/js/tag/TextareaObj.js?v=1.0"></script>
<script type="text/javascript" src="./shine/js/tag/CheckboxObj.js?v=1.0"></script>
<script type="text/javascript"
	src="./shine/js/tag/FormLineGroupObj.js?v=1.0"></script>
<script type="text/javascript"
	src="./shine/js/tag/CheckboxListObj.js?v=1.0"></script>
<script type="text/javascript"
	src="./shine/js/tag/RadioButtonListObj.js?v=1.0"></script>
<script type="text/javascript"
	src="./shine/js/tag/DropdownListObj.js?v=1.0"></script>
<script type="text/javascript"
	src="./shine/js/tag/TextInputObj.js?v=1.0"></script>
<script type="text/javascript" src="./shine/js/tag/FormObj.js?v=1.0"></script>
<script type="text/javascript" src="./shine/js/tag/IndexObj.js?v=1.0"></script>
<script type="text/javascript" src="./shine/js/tag/ProgressObj.js?v=1.0"></script>
<script type="text/javascript" src="./shine/js/tag/TabObj.js?v=1.0"></script>
<%--系统级的常量，从java转换到javascript--%>
<script type="text/javascript">
	var GlobalVars = __initSysGlobalVars();
	function __initSysGlobalVars() {
		var map = new HashMap();
		var debugmode = '<%=GlobalVars.DEBUG_MODE%>';
		map.put("DEBUG_MODE",("true" == debugmode));
		var basePath = '<%=basePath%>';
		map.put("BASE_PATH", basePath);
		return map.values;
	};
</script>