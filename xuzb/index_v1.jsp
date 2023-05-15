<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%@ page import="com.grace.frame.util.DataSet"%>
<%@ page import="com.grace.frame.util.DateUtil"%>
<%@page import="com.grace.frame.util.SysUser"%>
<%
	SysUser currentUser = (SysUser) request.getSession()
		.getAttribute("currentsysuser");
	String debugtxt = "";
	if (GlobalVars.DEBUG_MODE) {
		debugtxt = "(调试模式)";
	}
	String usernamestr = "欢迎您：" + currentUser.getYhmc();

	// 业务领域数据
	String ywlxgnid = (String) request.getAttribute("ywlxgnid");
	DataSet dsYwlx = (DataSet) request.getAttribute("dsywlx");
	//子系统的详细数据
	DataSet dsSubSys = (DataSet) request.getAttribute("dssubsys");
	//当前日期
	Date nowD = DateUtil.getDBTime();
	String dateweek = DateUtil.dateToString(nowD, "yyyy年MM月dd日");
	dateweek = dateweek + " " + DateUtil.date2Week(nowD);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<title><%=currentUser.getYhmc()%>(<%=currentUser.getYhbh()%>)
			<%=GlobalVars.APP_NAME%></title>
		<jsp:include page="frame/jsp/layout/frameImportFile.jsp"></jsp:include>
		<jsp:include page="frame/jsp/layout/bizImportFile.jsp"></jsp:include>
	</head>
	<body>
		<ef:body padding="0">
			<ef:layout>
				<ef:topLayoutPanel border="false" height="50">
					<ef:layout>
						<ef:leftLayoutPanel border="false" width="400"
							background="#EEF4FF">
							<div
								style="width: 300px; height: 45px; margin-left: 20px; background-image: url(frame/imgs/sys_logo.png); background-repeat: no-repeat; background-position: left center;"></div>
						</ef:leftLayoutPanel>
						<ef:centerLayoutPanel border="false" background="#EEF4FF">
							<div style="padding-top: 2px; text-align: center;">
								<%
									for (int i = 0, n = dsYwlx.size(); i < n; i++) {
															String gnid = dsYwlx.getString(i, "gnid");
															String gnmc = dsYwlx.getString(i, "gnmc");
															String gntb = dsYwlx.getString(i, "gntb");
															if (gnid.equals(ywlxgnid)) {
								%>
								<ef:button value="<%=gnmc %>" iconCls="<%=gntb %>"
									iconAlign="top" selected="true"></ef:button>
								<%
									} else {
																String gnsj = "chngYwlx('" + gnid
																		+ "');";
								%>
								<ef:button value="<%=gnmc %>" iconCls="<%=gntb %>"
									iconAlign="top" plain="true" onclick="<%=gnsj %>"></ef:button>
								<%
									}
														}
								%>
							</div>
						</ef:centerLayoutPanel>
						<ef:rightLayoutPanel border="false" width="380"
							background="#EEF4FF">
							<div style="padding: 10px 10px 0px 0px;">
								<ef:button value="<%=dateweek%>" plain="true"></ef:button>
								<ef:button value="设置" iconCls="icon-cog" type="menu"
									plain="true">
									<ef:menu>
										<ef:menuItem value="个人信息" iconCls="icon-user" onclick="perinfoView();"></ef:menuItem>
										<ef:menuItem value="修改密码" onclick="modifyMyPwd();"></ef:menuItem>
										<ef:menuItem value="个人操作记录" onclick="userLogView();"></ef:menuItem>
										<ef:menuSep></ef:menuSep>
										<ef:menuItem value="页面展示设置(F2)" iconCls="icon-cog"
											onclick="clientCookieClick();"></ef:menuItem>
										<ef:menuSep></ef:menuSep>
										<ef:menuItem value="关于" onclick="aboutClick();"></ef:menuItem>
									</ef:menu>
								</ef:button>
								<ef:button value="帮助" iconCls="icon-help" plain="true"></ef:button>
								<ef:button value="安全退出" iconCls="icon-cross" plain="true"
									onclick="dologoutClick();"></ef:button>
							</div>
						</ef:rightLayoutPanel>
					</ef:layout>
				</ef:topLayoutPanel>
				<ef:centerLayoutPanel border="false">
					<ef:layout name="mainlayout">
						<ef:leftLayoutPanel split="true" iconCls="icon-user"
							title="<%=usernamestr%>" width="200">
							<ef:accordion border="false">
								<%
									for (int i = 0, n = dsSubSys.size(); i < n; i++) {
																String gnid = dsSubSys.getString(i, "gnid");
																String gnmc = dsSubSys.getString(i, "gnmc");
																String gntb = dsSubSys.getString(i, "gntb");
																String treeid = "tree_" + gnid;
																String treeParas = "fgn:'" + gnid + "'";
								%>
								<ef:accordionPanel title="<%=gnmc %>" iconCls="<%=gntb %>">
									<ef:tree name="<%=treeid%>"
										className="com.grace.frame.login.SubSysMenuTree"
										paras="<%=treeParas %>" onContextMenu="addmyFuncContextMenu"
										onClick="openPage"></ef:tree>
								</ef:accordionPanel>
								<%
									}
								%>
							</ef:accordion>
						</ef:leftLayoutPanel>
						<ef:centerLayoutPanel border="false">
							<ef:tab name="sys_frame_main_tab" fixed="false">
							</ef:tab>
						</ef:centerLayoutPanel>
						<ef:rightLayoutPanel split="true" title="我的功能" width="120"
							collapsed="true" padding="6">
							<ef:tree name="myfunction"
								className="com.grace.frame.login.MyFuncTree"
								onContextMenu="myFuncContextMenu" onClick="openPage"></ef:tree>
						</ef:rightLayoutPanel>
					</ef:layout>
				</ef:centerLayoutPanel>
			</ef:layout>
			<ef:menu name="myfunctionMenu" width="80">
				<ef:menuItem value="移除" iconCls="icon-tag-blue-delete"
					onclick="removeMyfunc();"></ef:menuItem>
				<ef:menuItem value="清空" iconCls="icon-bin-closed"
					onclick="clearMyfunc();"></ef:menuItem>
			</ef:menu>
			<ef:menu name="addMyfunctionMenu" width="120">
				<ef:menuItem value="添加到我的功能" iconCls="icon-pencil-add"
					onclick="addMyfunc();"></ef:menuItem>
			</ef:menu>
		</ef:body>
	</body>
</html>
<script type="text/javascript">
	$(function(){
		//默认操作-打开首页
		var url = new URL("bizprocess.do", "fwdProceedingHomePage");
		getObject("sys_frame_main_tab").addTabPage("首页", "icon-house", url, "home_page_main", false);

		//根据屏幕分辨率设置窗口大小
		setWindowTypeSizeByScreenSize();
	});

	function dologoutClick() {
		//退出
		if (!confirm("确定要退出系统吗？")) {
			return;
		}
		top.location.href = "login.do?method=doLogout";
	}

	//业务领域点击后的切换
	function chngYwlx(ywlxgnid){
		top.location.href = "login.do?method=fwdMainPage&ywlxgnid="+ywlxgnid;
	}

	//我的功能的右击事件的操作
	function myFuncContextMenu(e, node){
		e.preventDefault();//阻止事件响应
		var mymenuObj=getObject("myfunctionMenu");
		mymenuObj.show(e.pageX-10,e.pageY-5);
		var dataMap=new HashMap();
		dataMap.put("gnid", node.id);
		mymenuObj.setMapData(dataMap);
	}

	//删除我的功能
	function removeMyfunc(){
		var mymenuObj=getObject("myfunctionMenu");
		var gnid = mymenuObj.getMapData().get("gnid");
		if(chkObjNull(gnid)){
			return;
		}
		//清空
		mymenuObj.clearMapData();

		//请求后台
		var url = new URL("login.do","removeMyfunction");
		url.addPara("gnid",gnid);
		AjaxUtil.asyncBizRequest(url, function(data){
			getObject("myfunction").asyncLoadRemotData("com.grace.frame.login.MyFuncTree",new HashMap());
		});
	}

	//清空
	function clearMyfunc(){
		if(!confirm("是否确定要清空所有功能？")){
			return false;
		}
		//请求后台
		var url = new URL("login.do","clearMyfunction");
		AjaxUtil.asyncBizRequest(url, function(data){
			getObject("myfunction").asyncLoadRemotData("com.grace.frame.login.MyFuncTree",new HashMap());
		});
	}

	//我的功能的右击事件的操作
	function addmyFuncContextMenu(e, node){
		e.preventDefault();//阻止事件响应
		if(chkObjNull(node.attributes.gnsj)){
			return false;
		}
		var mymenuObj=getObject("addMyfunctionMenu");
		mymenuObj.show(e.pageX-10,e.pageY-5);
		var dataMap=new HashMap();
		dataMap.put("gnid", node.id);
		mymenuObj.setMapData(dataMap);
	}
	
	//增加我的功能
	function addMyfunc(){
		var mymenuObj=getObject("addMyfunctionMenu");
		var gnid = mymenuObj.getMapData().get("gnid");
		if(chkObjNull(gnid)){
			return;
		}
		//清空
		mymenuObj.clearMapData();

		//请求后台
		var url = new URL("login.do","addMyfunction");
		url.addPara("gnid",gnid);
		AjaxUtil.asyncBizRequest(url, function(data){
			getObject("myfunction").asyncLoadRemotData("com.grace.frame.login.MyFuncTree",new HashMap());
		});
	}

	//增加tab页
	function openPage(node){
		var gnsj = node.attributes.gnsj;
		if(chkObjNull(gnsj)){
			return;
		}
		var url;
		if(gnsj.startWith("href:")){
			url = gnsj.substr(5);
		}else if(gnsj.startWith("url:")){
			var urlStr = gnsj.substr(4);
			url = new URL(urlStr);
			url.addPara("__jbjgid",node.attributes.jbjgid);
			url.addPara("__jbjgqxfw",node.attributes.jbjgqxfw);
			url.addPara("__yhid", getPageYhid());
		}else{
			alert("功能事件配置错误");
			return;
		}
		getObject("sys_frame_main_tab").addTabPage(node.text, node.iconCls, url, node.id);
	}

	//修改密码
	function modifyMyPwd(){
		var url = new URL("login.do", "fwdModifyMyPwd");
		openTopWindow("修改密码","icon-group-edit",url, 400, 200, null);
	}

	//个人基本信息查看
	function perinfoView(){
		var url = new URL("login.do", "fwdLoginUserPerInfo");
		openTopWindow("个人信息","icon-user",url, 700, 370, null);
	}

	//查询用户操作日志
	function userLogView(){
		var url = new URL("login.do", "fwdUserBizLogInfo");
		openTopWindow("用户操作日志", url, "big");
	}

	//关于操作
	function aboutClick(){
		var url = new URL("login.do", "fwdSysAboutInfo");
		openTopWindow("关于...", null, url, 650, 230, null);
	}

	//浏览器个人性化设置
	function clientCookieClick(){
		var url = new URL("taglib.do", "fwdClientCookieSetting");
		openTopWindow("页面展示设置","icon-cog",url,"normal",null);
	}
</script>