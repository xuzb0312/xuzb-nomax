/**
 * 将一些通用的小的js函数放到该js文件中，对于一整套js逻辑建议单独放到一个文件中进行管理-依赖于jquery
 * 
 * @author yjc
 */
// 关闭浏览器页面，不给弹窗提示
function closeBrowser() {
	if ($.browser.msie) {// ie浏览器
		window.opener = null;
		window.open("", "_self");// 强制消除关闭提示
		window.close();
	} else {// 其他浏览器转向空白页
		window.location.href = "about:blank";
	}
};

// 给页面设置经办机构IDjbjgid
function setJbjgid(jbjgid) {
	if ($("#__jbjgid").length <= 0) {
		throw new Error("页面中不存在jbjgid参数");
	}
	$("#__jbjgid").val(jbjgid);
};

// 给页面设置经办机构权限范围
function setJbjgqxfw(jbjgqxfw) {
	if ($("#__jbjgqxfw").length <= 0) {
		throw new Error("页面中不存在__jbjgqxfw参数");
	}
	$("#__jbjgqxfw").val(jbjgqxfw);
};

// 获取经办机构ID
function getJbjgid(defalut) {
	if ($("#__jbjgid").length <= 0) {
		if(chkObjNull(defalut)){
			throw new Error("页面中不存在jbjgid参数");
		}
		return defalut;
	}
	return $("#__jbjgid").val();
};

// 获取页面YHID
function getPageYhid() {
	if ($("#__yhid").length <= 0) {
		return "";
	}
	return $("#__yhid").val();
};

// 显示JSP路径
function showJspPath() {
	if ($("#__jsppath").length <= 0) {
		return false;
	}
	var jsppath = $("#__jsppath").val();
	MsgBox.alert(jsppath);
	return true;
};

// 获取JSP路径
function getJspPath() {
	if ($("#__jsppath").length <= 0) {
		return false;
	}
	return $("#__jsppath").val();
};

// 获取经办机构权限范围
function getJbjgqxfw(defalut) {
	if ($("#__jbjgqxfw").length <= 0) {
		if(chkObjNull(defalut)){
			throw new Error("页面中不存在__jbjgqxfw参数");
		}
		return defalut;
	}
	return $("#__jbjgqxfw").val();
};

// 整个页面的等待mask-展示
function showLoading() {
	if ($("#sys_loading_mask_div").length <= 0) {
		var divstr = "<div id=\"sys_loading_mask_div\" class=\"sys-loading-mask\"><input id='sys_loading_mask_div_focus_input' style=\"position: absolute;left:-200px;top:-200px;\" type=\"text\"/></div>";
		$("body").append(divstr);
	} else {
		$("#sys_loading_mask_div").show();
	}
	$("#sys_loading_mask_div_focus_input").focus();
};

// 整个页面的等待mask-取消
function hideLoading() {
	if ($("#sys_loading_mask_div").length > 0) {
		$("#sys_loading_mask_div").hide();
	}
};

/**
 * 获取随机数-字符串
 * 
 * @param len
 * @return
 */
function randomString(len) {
　　len = len || 32;
　　var chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';   
　　var maxPos = chars.length;
　　var str = '';
　　for (var i = 0; i < len; i++) {
　　　　str += chars.charAt(Math.floor(Math.random() * maxPos));
　　}
　　return str;
};

/**
 * 窗口的类型
 */
var WindowType={
	big:"big",// 大窗口
	normal:"normal",// 正常，中等的
	small:"small"// 小窗口
};

/**
 * 根据窗口类型获取默认的窗口大小
 * 
 * @return
 */
function getDefalutWindowWHByType(type){
	var map = new HashMap();
	// 由于不同的电脑，分辨率不一致，所以通过cookies进行微调，适应不同分辨率。
	// ctrl+alt+F2打开cookie设置窗口
	var widthdiff = CookieUtil.get("sys_win_type_"+type+"_width");
	if(chkObjNull(widthdiff)){
		widthdiff = 0;
	}else{
		var widthdiff = Number(widthdiff);
		if (isNaN(widthdiff)) {
			widthdiff = 0;
		}
	}
	var heightdiff = CookieUtil.get("sys_win_type_"+type+"_height");
	if(chkObjNull(heightdiff)){
		heightdiff = 0;
	}else{
		var heightdiff = Number(heightdiff);
		if (isNaN(heightdiff)) {
			heightdiff = 0;
		}
	}
	
	// 根据类型动态调整宽高
	var mapViewTypeAdjust = new HashMap();
	mapViewTypeAdjust.put("big_w", 0);
	mapViewTypeAdjust.put("big_h", 0);
	mapViewTypeAdjust.put("normal_w", 0);
	mapViewTypeAdjust.put("normal_w", 0);
	mapViewTypeAdjust.put("small_w", 0);
	mapViewTypeAdjust.put("small_w", 0);
	if ("v2" == GlobalVars.VIEW_TYPE) {
		mapViewTypeAdjust.put("big_w", 10);
		mapViewTypeAdjust.put("big_h", 50);
		mapViewTypeAdjust.put("normal_w", 10);
		mapViewTypeAdjust.put("normal_h", 50);
		mapViewTypeAdjust.put("small_w", 10);
		mapViewTypeAdjust.put("small_h", 50);
	}

	var adjustW4ViewType = mapViewTypeAdjust.get(type + "_w");
	var adjustH4ViewType = mapViewTypeAdjust.get(type + "_h");
	if (WindowType.big == type) {
		map.put("width", 1000 + widthdiff + adjustW4ViewType);
		map.put("height", 550 + heightdiff + adjustH4ViewType);
	} else if (WindowType.normal == type) {
		map.put("width", 900 + widthdiff + adjustW4ViewType);
		map.put("height", 450 + heightdiff + adjustH4ViewType);
	} else if (WindowType.small == type) {
		map.put("width", 500 + widthdiff + adjustW4ViewType);
		map.put("height", 200 + heightdiff + adjustH4ViewType);
	} else {
		throw new Error("输入的窗口类型不正确（big,normal,small）");
	}
	return map.values;
};

/**
 * 重写打开顶层window方法，解决跨域的问题
 */
function openTopWindow(arg1, arg2, arg3, arg4, arg5, arg6, arg7){
	try{
		// 有些浏览器进行顶层div弹窗拦截，对于这种情况，使用嵌入方式打开，如果设置cookie
		if("1" == CookieUtil.get("sys_top_win_open_type")){// 使用嵌入方式打开
			throw new Error("Cookie设置嵌入方式打开");
		}
		// 先使用顶层打开，报出跨域问题后，使用嵌入的window打开
		if(arguments.length==3){
			openTopWindowFunc(arg1, arg2, arg3);
		}else if(arguments.length==4){
			openTopWindowFunc(arg1, arg2, arg3, arg4);
		}else if(arguments.length==5){
			openTopWindowFunc(arg1, arg2, arg3, arg4, arg5);
		}else if(arguments.length==6){
			openTopWindowFunc(arg1, arg2, arg3, arg4, arg5, arg6);
		}else if(arguments.length==7){
			openTopWindowFunc(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
	}catch (oE) {
		console.log("顶层窗口无法打开："+oE.message);
		if(arguments.length==3){
			openWindow(arg1, arg2, arg3);
		}else if(arguments.length==4){
			openWindow(arg1, arg2, arg3, arg4);
		}else if(arguments.length==5){
			openWindow(arg1, arg2, arg3, arg4, arg5);
		}else if(arguments.length==6){
			openWindow(arg1, arg2, arg3, arg4, arg5, arg6);
		}else if(arguments.length==7){
			openWindow(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
		}
	}
}

/**
 * 在最顶层打开window窗口 <br>
 * 创建几种重载机制：<br>
 * 1.openTopWindow(title,url,windowtype);<br>
 * 2.openTopWindow(title,url,windowtype,callback);<br>
 * 3.openTopWindow(title,iconCls,url,windowtype,callback);<br>
 * 4.openTopWindow(title,iconCls,url,width,height,callback);<br>
 * 5.openTopWindow(title,iconCls,url,width,height,callback, tools);<br>
 * tools格式[iconCls:"",handler:function(){}];<br>
 */
function openTopWindowFunc(arg1, arg2, arg3, arg4, arg5, arg6, arg7){
	// 参数处理，形成重载
	var title="业务窗口";
	var url;
	var iconCls="icon-application-double";
	var width;
	var height;
	var callback;
	var tools=[];
	if(arguments.length==3){
		title=arg1;
		url=arg2;
		var typeconf=getDefalutWindowWHByType(arg3);
		width=typeconf.width;
		height=typeconf.height;
	}else if(arguments.length==4){
		title=arg1;
		url=arg2;
		callback=arg4;
		var typeconf=getDefalutWindowWHByType(arg3);
		width=typeconf.width;
		height=typeconf.height;
	}else if(arguments.length==5){
		title=arg1;
		iconCls=arg2;
		url=arg3;
		callback=arg5;
		var typeconf=getDefalutWindowWHByType(arg4);
		width=typeconf.width;
		height=typeconf.height;
	}else if(arguments.length==6){
		title=arg1;
		iconCls=arg2;
		url=arg3;
		width=arg4;
		height=arg5;
		callback=arg6;
	}else if(arguments.length==7){
		title=arg1;
		iconCls=arg2;
		url=arg3;
		width=arg4;
		height=arg5;
		callback=arg6;
		tools=arg7;
	}
	
	var windowid = randomString(16); // 窗口唯一id
	var con_id="con_"+windowid;
	
	// body上附加window窗口容器
	var divstr = "<div id=\""+con_id+"\"></div>";
	top.window.$("body").append(divstr);
	
	// 窗口参数
	var mapOpt = new HashMap();
	mapOpt.put("title",title);
	mapOpt.put("width",width);
	mapOpt.put("height",height);
	mapOpt.put("iconCls",iconCls);
	mapOpt.put("minimizable",false);
	mapOpt.put("modal",true);
	mapOpt.put("inline",true);
	mapOpt.put("tools",tools);
	mapOpt.put("autoCloseOnEsc",false);// 不使用扩展的esc关闭窗口，而是使用子页面的esc关闭
	if(null==callback||""==callback||typeof(callback)=="undefined"){
		mapOpt.put("onClose",function(){$("#"+con_id).window('destroy', true);});
	}else{
		mapOpt.put("onClose",function(){
			var data=top.window.$("#" + frameid)[0].contentWindow.__data;
			$("#"+con_id).window('destroy', true);
			callback(data);
		});
	}
	// 内容
	var frameid = "frm_"+windowid;
	if ("string" == typeof url) {
		mapOpt.put("content", "<iframe id='" + frameid
				+ "' scrolling=\"auto\" frameborder=\"0\" " + "src=\"" + url
				+ "\" " + "style=\"width:100%;height:100%;\"></iframe>");
		// 增加debug模式
		if(GlobalVars.DEBUG_MODE){
			mapOpt.put("headerContextMenu",[{text: "请求路径", iconCls: "icon-bug-link", disabled: false, onclick: function () {MsgBox.alert(url);}}]);
		}
	} else {
		// 默认处理URL-增加业务信息jbjgid,jbjgfw
		var parasMap = url.paras;
		if (!parasMap.containsKey("__jbjgid")) {
			url.addPara("__jbjgid", getJbjgid("00000000"));
		}
		if (!parasMap.containsKey("__jbjgqxfw")) {
			url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		}
		if (!parasMap.containsKey("__yhid")) {
			url.addPara("__yhid", getPageYhid());
		}
		mapOpt.put("content", "<iframe id='" + frameid
				+ "' scrolling=\"auto\" frameborder=\"0\" "
				+ "src=\"taglib.do?method=fwdPageFrame&frameid="+frameid+"\" "
				+ "style=\"width:100%;height:100%;\"></iframe>");
		
		// 增加debug模式
		if(GlobalVars.DEBUG_MODE){
			mapOpt.put("headerContextMenu",[{text: "请求路径", iconCls: "icon-bug-link", disabled: false, onclick: function () {var jspPath = top.window.$("#" + frameid)[0].contentWindow.getJspPath();MsgBox.alert("请求路径：<br/>"+url.getURLString()+"<hr/>JSP页面：<br/>"+jspPath);}},
			                                {text: "请求数据", iconCls: "icon-bug-edit", disabled: false, onclick: function () {MsgBox.showJsonData(url.getParas());}}]);
		}
	}

	// 创建window
	top.window.$("#"+con_id).window(mapOpt.values);

	top.window.$("#" + frameid)[0].contentWindow.__con_id = con_id;// id
	if ("string" != typeof url) {
		top.window.$("#" + frameid)[0].contentWindow.__requrl = url;// 传递参数，-可以支持传递大数据量数据
		top.window.$("#" + frameid)[0].contentWindow.__opener_type = "window";// 打开类型
	}
};


/**
 * 原则每个页面只允许开一个window页; <br>
 * 创建几种重载机制：<br>
 * 1.openWindow(title,url,windowtype);<br>
 * 2.openWindow(title,url,windowtype,callback);<br>
 * 3.openWindow(title,iconCls,url,windowtype,callback);<br>
 * 4.openWindow(title,iconCls,url,width,height,callback);<br>
 * 5.openWindow(title,iconCls,url,width,height,callback, tools);<br>
 * tools格式[iconCls:"",handler:function(){}];<br>
 */
function openWindow(arg1, arg2, arg3, arg4, arg5, arg6, arg7){
	// 参数处理，形成重载
	var title="业务窗口";
	var url;
	var iconCls="icon-application-double";
	var width;
	var height;
	var callback;
	var tools=[];
	if(arguments.length==3){
		title=arg1;
		url=arg2;
		var typeconf=getDefalutWindowWHByType(arg3);
		width=typeconf.width;
		height=typeconf.height;
	}else if(arguments.length==4){
		title=arg1;
		url=arg2;
		callback=arg4;
		var typeconf=getDefalutWindowWHByType(arg3);
		width=typeconf.width;
		height=typeconf.height;
	}else if(arguments.length==5){
		title=arg1;
		iconCls=arg2;
		url=arg3;
		callback=arg5;
		var typeconf=getDefalutWindowWHByType(arg4);
		width=typeconf.width;
		height=typeconf.height;
	}else if(arguments.length==6){
		title=arg1;
		iconCls=arg2;
		url=arg3;
		width=arg4;
		height=arg5;
		callback=arg6;
	}else if(arguments.length==7){
		title=arg1;
		iconCls=arg2;
		url=arg3;
		width=arg4;
		height=arg5;
		callback=arg6;
		tools=arg7;
	}
	
	// 进行高度和宽度的自修正，嵌套的情况
	var parentH=$(window).height()-2;
	var parentW=$(window).width()-2;
	if(width>parentW){
		width=parentW;
	}
	if(height>parentH){
		height=parentH;
	}
	
	var windowid = randomString(16); // 窗口唯一id
	var con_id="con_"+windowid;// 容器id
	var frameid = "frm_"+windowid;// frameId
	
	// body上附加window窗口容器
	var divstr = "<div id=\""+con_id+"\"></div>";
	$("body").append(divstr);
	
	// 窗口参数
	var mapOpt = new HashMap();
	mapOpt.put("title",title);
	mapOpt.put("width",width);
	mapOpt.put("height",height);
	mapOpt.put("iconCls",iconCls);
	mapOpt.put("minimizable",false);
	mapOpt.put("modal",true);
	mapOpt.put("inline",true);
	mapOpt.put("tools",tools);
	mapOpt.put("autoCloseOnEsc",false);// 不使用扩展的esc关闭窗口，而是使用子页面的esc关闭

	if(null==callback||""==callback||typeof(callback)=="undefined"){
		mapOpt.put("onClose",function(){$("#"+con_id).window('destroy');});
	}else{
		mapOpt.put("onClose",function(){
			var data=$("#" + frameid)[0].contentWindow.__data;
			callback(data);// mod.yjc.2015.07.31必须先执行回调，在销毁对象，否则IE存在回调执行出错的问题。
			$("#"+con_id).window('destroy');
		});
	}
	// 内容
	
	if ("string" == typeof url) {
		mapOpt.put("content", "<iframe id='" + frameid
				+ "' scrolling=\"auto\" frameborder=\"0\" " + "src=\"" + url
				+ "\" " + "style=\"width:100%;height:100%;\"></iframe>");
		
		// 增加debug模式
		if(GlobalVars.DEBUG_MODE){
			mapOpt.put("headerContextMenu",[{text: "请求路径", iconCls: "icon-bug-link", disabled: false, onclick: function () {MsgBox.alert(url);}}]);
		}
	} else {
		// 默认处理URL-增加业务信息jbjgid,jbjgfw
		var parasMap = url.paras;
		if (!parasMap.containsKey("__jbjgid")) {
			url.addPara("__jbjgid", getJbjgid("00000000"));
		}
		if (!parasMap.containsKey("__jbjgqxfw")) {
			url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		}
		if (!parasMap.containsKey("__yhid")) {
			url.addPara("__yhid", getPageYhid());
		}
		mapOpt.put("content", "<iframe id='" + frameid
				+ "' scrolling=\"auto\" frameborder=\"0\" "
				+ "src=\"taglib.do?method=fwdPageFrame&frameid="+frameid+"\" "
				+ "style=\"width:100%;height:100%;\"></iframe>");
		
		// 增加debug模式
		if(GlobalVars.DEBUG_MODE){
			mapOpt.put("headerContextMenu",[{text: "请求路径", iconCls: "icon-bug-link", disabled: false, onclick: function () {var jspPath = $("#" + frameid)[0].contentWindow.getJspPath();MsgBox.alert("请求路径：<br/>"+url.getURLString()+"<hr/>JSP页面：<br/>"+jspPath);}},
			                                {text: "请求数据", iconCls: "icon-bug-edit", disabled: false, onclick: function () {MsgBox.showJsonData(url.getParas());}}]);
		}
	}

	// 创建window
	$("#"+con_id).window(mapOpt.values);
	
	$("#" + frameid)[0].contentWindow.__con_id = con_id;// 传递参数,id
	if ("string" != typeof url) {
		$("#" + frameid)[0].contentWindow.__requrl = url;// 传递参数，-可以支持传递大数据量数据
		$("#" + frameid)[0].contentWindow.__opener_type = "window";
	}
};

/**
 * -------------------------------------------------------begin<br>
 * 标签对象--结合jsp的标签共同使用。-- 随时可以扩充标签对象
 */
// 标签对象的类型数组
var JQueryObjType = {
	tree : "tree",// 树
	layout : "layout",// 布局
	tab : "tab",// tab
	menu : "menu",// 菜单
	splitbutton : "splitbutton",// 分割按钮
	menubutton : "menubutton",// 菜单按钮
	linkbutton : "linkbutton",// 普通按钮
	hiddenbutton : "hiddenbutton",// 无权限按钮
	form : "form",// form表单
	text : "text",// 文本展示控件
	textinput : "textinput",// 文本输入框
	dropdownlist : "dropdownlist",// 下拉框控件
	multidropdownlist : "multidropdownlist",// 多选下拉框
	checkboxlist: "checkboxlist",// 复选框
	radiobuttonlist : "radiobuttonlist",// 单选按钮
	hiddeninput : "hiddeninput",// 隐藏框
	querygrid : "querygrid",// 查询表格
	filebox : "filebox",// 文件选择框
	printer : "printer",// 打印对象
	ueditor : "ueditor",// 富文本编辑组件
	loadpanel : "loadpanel",// 加载控件
	textarea : "textarea", // 文本域标签
	echarts : "echarts", // 图表标签
	multiselectbox : "multiselectbox"
};

// 获取标签对象
function getObject(para) {
	var obj;
	if(typeof(para) == "string"){
		var paraArr=para.split(".");
		var id="";
		for(var i=0,n=paraArr.length;i<n;i++){
			if(i==n-1){
				id=id+"#"+paraArr[i];
			}else{
				id=id+"#"+paraArr[i]+" ";
			}
		}
		obj = $(id);// 字符串的化，去获取
	}else{
		obj=para;
	}
	var type = obj.attr("obj_type");// 书写的标签上均增加了obj_type属性来记录标签的类型
	if (JQueryObjType.tree == type) {// Tree树
		return new TreeObj(obj);
	} else if (JQueryObjType.layout == type) {
		return new LayoutObj(obj);
	} else if (JQueryObjType.tab == type) {
		return new TabObj(obj);
	}  else if (JQueryObjType.menu == type) {
		return new MenuObj(obj);
	}  else if (JQueryObjType.splitbutton == type||JQueryObjType.menubutton == type||JQueryObjType.linkbutton == type
			||JQueryObjType.hiddenbutton == type) {
		return new ButtonObj(obj, type);
	} else if(JQueryObjType.form == type){
		return new FormObj(obj);
	}else if(JQueryObjType.textinput == type){
		return new TextInputObj(obj);
	}else if(JQueryObjType.text == type){
		return new TextObj(obj);
	}else if(JQueryObjType.dropdownlist == type){
		return new DropdownListObj(obj);
	}else if(JQueryObjType.multidropdownlist == type){
		return new MultiDropdownListObj(obj);
	}else if(JQueryObjType.checkboxlist == type){
		return new CheckboxListObj(obj);
	}else if(JQueryObjType.radiobuttonlist == type){
		return new RadiobuttonListObj(obj);
	}else if(JQueryObjType.hiddeninput == type){
		return new HiddenInputObj(obj);
	}else if(JQueryObjType.querygrid == type){
		return new QueryGridObj(obj);
	}else if(JQueryObjType.filebox == type){
		return new FileBoxObj(obj);
	}else if(JQueryObjType.printer == type){
		return new PrinterObj(obj);
	}else if(JQueryObjType.ueditor == type){
		return new UEditorObj(obj);
	}else if(JQueryObjType.loadpanel == type){
		return new LoadPanelObj(obj);
	}else if(JQueryObjType.textarea == type){
		return new TextareaObj(obj);
	}else if(JQueryObjType.multiselectbox == type){
		return new MultiSelectBoxObj(obj);
	}else if(JQueryObjType.echarts == type){
		return obj.data("echartsObj");// 返回插件对象
	}else {
		throw new Error("该标签["+para+"]不支持此方法获取其对象。");
	}
};
// 为简便使用get=getObject;
var get = getObject;

/**
 * -------------------------------------------------------end<br>
 */

/**
 * 重新优化此方法
 */
function chkObjNull(obj){
	if (typeof (obj) == "undefined"){
		return true; // 未定义
	}else if(typeof(obj) == "string"){
		if(null==obj||""==obj){
			return true;
		}else{
			return false;
		}
	}else{
		if(null == obj){
			return true;
		}else{
			return false;
		}
	}
};

/**
 * 系统统一管理的文件下载-mod.yjc.2016年6月24日-兼容模式下载
 */
function downloadSysFile(wjbs){
	if(chkObjNull(wjbs)){
		alert("文件唯一标识为空");
		return;
	}
	var url = new URL("taglib.do", "downloadSysFile");
	url.addPara("wjbs", wjbs);
	downloadFile2Form(url);
};

/**
 * 打开pdf文件
 * 
 * @return
 */
function openPdfFile(arg1, arg2, arg3, arg4) {
	var title = "PDF文件查看";
	var url;
	var width = 1000;
	var height = 550;
	if (arguments.length == 1) {
		url = arg1;
	} else if (arguments.length == 2) {
		title = arg1;
		url = arg2;
	} else if (arguments.length == 3) {
		title = arg1;
		url = arg2;
		var typeconf = getDefalutWindowWHByType(arg3);
		width = typeconf.width;
		height = typeconf.height;
	} else if (arguments.length == 4) {
		title = arg1;
		url = arg2;
		width = arg3;
		height = arg4;
	} else {
		alert("参数传递个数不正确");
		return;
	}
	
	if ("string" != typeof url) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
	}
	
	var pdfviewer = "./frame/plugins/pdfjs/web/viewer.html";
	var urlStr = GlobalVars.BASE_PATH + url.getRealURLString();
	var pdfviewHref = pdfviewer + "?file=" + encodeURIComponent(urlStr);
	openTopWindow(title, "icon-text-dropcaps", pdfviewHref, width, height, null);
};

/**
 * 图片文件查看
 * 
 * @return
 */
function openImageFile(arg1, arg2, arg3, arg4) {
	var title = "图片文件查看";
	var url;
	var width = 1000;
	var height = 550;
	if (arguments.length == 1) {
		url = arg1;
	} else if (arguments.length == 2) {
		title = arg1;
		url = arg2;
	} else if (arguments.length == 3) {
		title = arg1;
		url = arg2;
		var typeconf = getDefalutWindowWHByType(arg3);
		width = typeconf.width;
		height = typeconf.height;
	} else if (arguments.length == 4) {
		title = arg1;
		url = arg2;
		width = arg3;
		height = arg4;
	} else {
		alert("参数传递个数不正确");
		return;
	}

	// 图片路径
	if ("string" != typeof url) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
	}
	var urlStr = GlobalVars.BASE_PATH + url.getRealURLString();

	var winUrl = new URL("taglib.do", "fwdImageViewPage");
	winUrl.addPara("imagesrc", urlStr);
	openTopWindow(title, "icon-image", winUrl, width, height, null);
}

/**
 * 图片文件查看【文件中只有图片可以在线预览，其余的都不行】
 * 
 * @return
 */
function openZipFile(arg1, arg2, arg3, arg4) {
	var title = "压缩文件查看";
	var url;
	var width = 1000;
	var height = 550;
	if (arguments.length == 1) {
		url = arg1;
	} else if (arguments.length == 2) {
		title = arg1;
		url = arg2;
	} else if (arguments.length == 3) {
		title = arg1;
		url = arg2;
		var typeconf = getDefalutWindowWHByType(arg3);
		width = typeconf.width;
		height = typeconf.height;
	} else if (arguments.length == 4) {
		title = arg1;
		url = arg2;
		width = arg3;
		height = arg4;
	} else {
		alert("参数传递个数不正确");
		return;
	}

	// 图片路径
	if ("string" != typeof url) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
	}
	var data = AjaxUtil.syncBizRequest(url)
	if (!AjaxUtil.checkIsGoOn(data)) {
		return;
	}
	var dataMap = new HashMap(data);
	var zipsessionkey = dataMap.get("zipsessionkey");
	var winUrl = new URL("taglib.do", "fwdZipViewPage");
	winUrl.addPara("zipsessionkey", zipsessionkey);
	openTopWindow(title, "icon-page-white-zip", winUrl, width, height, null);
}

/**
 * 申请临时授权
 * 
 * @return
 */
function applyTempRight(functionid, callback){
	var url = new URL("taglib.do", "fwdApplyTempRightPage");
	url.addPara("functionid", functionid);
	openWindow("您没有直接操作该功能的权限，请申请临时授权", "icon-user-go", url, 500, 200, callback);
};

/**
 * 通过form进行数据的导出-文件下载
 * 
 * @return
 */
function downloadFile2Form(url) {
	if ("string" != typeof url) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		url = url.getRealURLString();
	}
		
    var submitDataForm = document.createElement("FORM");
    submitDataForm.method = "POST";
    submitDataForm = document.body.appendChild(submitDataForm);
    var urlArray = url.split("&");
    for (var i = 1; i < urlArray.length; i++) {
        var urlEle = urlArray[i];
        var o = document.createElement("INPUT");
        o.type = "hidden";
        var key = urlEle.split("=")[0];
        o.name = key;
        o.id = key;
        o.value = decodeURIComponent(urlEle.substr(key.length + 1));
        submitDataForm.appendChild(o);
    }
    submitDataForm.action = urlArray[0];
    submitDataForm.target = "_self";
    submitDataForm.onsubmit = function() {
        return false; // 防止重复提交
    };
    submitDataForm.submit();
    submitDataForm.outerHTML = "";
};

/**
 * 文件上传对话框窗口--主要用于大文件的上传操作
 * <p>
 * title:窗口标题；<br>
 * fileType:文件类型；默认*.*;例如：*.jpg;*.gif<br>
 * fileDesc:文件描述； fileSize:文件大小，默认不限制; height：打开窗口高度，宽度不允许调整；
 * jspPage：对于页面的自定义参数；jsp页面；页面内容为：<ef:form title="上传设置" name="formSet">
 * saveController：保存controoler-.do; saveMethod:保存方法； mapparas:参数：hashMap格式；
 * callback:回调函数；
 * </p>
 */
function openFileUploadWindow(title, fileType, fileDesc, fileSize, height, jspPage, saveController, saveMethod, mapparas, callback){
	if(chkObjNull(title)){
		title = "上传文件";
	}
	if(chkObjNull(fileType)){
		fileType = "*.*";
	}
	if(chkObjNull(fileDesc)){
		fileDesc = "所有文件";
	}
	if(chkObjNull(fileSize)){// kb
		fileSize = 0;
	}
	if(chkObjNull(height)){
		height = 360;
	}
	if(chkObjNull(jspPage)){
		jspPage = null;// 从根目录找例如test/jsp/test.jsp
	}
	if(chkObjNull(saveController)){
		alert("saveController参数为空");
		return;
	}
	if(chkObjNull(saveMethod)){
		alert("saveMethod参数为空");
		return;
	}
	if(chkObjNull(mapparas)){
		// hashMap格式的参数信息
		mapparas = new HashMap();
	}
	
	// 组装传递的参数
	var allparas = new HashMap();
	allparas.put("__filetype", fileType);
	allparas.put("__filedesc",fileDesc);
	allparas.put("__sizelimit", fileSize);
	allparas.put("__jsppage", jspPage);
	allparas.put("__savecontroller", saveController);
	allparas.put("__savemethod", saveMethod);
	allparas.put("__paras", JSON.stringify(mapparas.values));
	
	// 打开上传页面
	var url = new URL("taglib.do", "fwdbigSizeFileUploadWindow");
	url.addMap(allparas);
	openWindow(title, "icon-folder", url, 570, height, function(data) {
		if(!chkObjNull(callback)){
			callback(data);
		}
	});
};

/**
 * 判断是否为IE11浏览器的方法
 * 
 * @return
 */
function isIE11() {
	if (!!window.ActiveXObject || "ActiveXObject" in window) {
		return true;
	} else {
		return false;
	}
};

/**
 * 根据窗口分辨率设置窗口大小
 */
function setWindowTypeSizeByScreenSize() {
	var sH = screen.height;
	var sW = screen.width;
	
	// 大窗口
	var big_width = CookieUtil.get("sys_win_type_big_width");
	if (chkObjNull(big_width) || 0 == Number(big_width)) {
		if(sW >= 1920){
			big_width = 360;
		}else if(sW >= 1600){
			big_width = 150;
		}else{
			big_width = 0;
		}
		CookieUtil.set("sys_win_type_big_width", big_width, 36500);
	}
	var big_height = CookieUtil.get("sys_win_type_big_height");
	if (chkObjNull(big_height) || 0 == Number(big_height)) {
		if(sH >= 1080){
			big_height = 120;
		}else if(sH >= 900){
			big_height = 50;
		}else{
			big_height = 0;
		}
		CookieUtil.set("sys_win_type_big_height", big_height, 36500);
	}

	// 普通
	var normal_width = CookieUtil.get("sys_win_type_normal_width");
	if (chkObjNull(normal_width) || 0 == Number(normal_width)) {
		if(sW >= 1600){
			normal_width = 100;
		}else{
			normal_width = 0;
		}
		CookieUtil.set("sys_win_type_normal_width", normal_width, 36500);
	}
	var normal_height = CookieUtil.get("sys_win_type_normal_height");
	if (chkObjNull(normal_height) || 0 == Number(normal_height)) {
		if(sH >= 900){
			normal_height = 30;
		}else{
			normal_height = 0;
		}
		CookieUtil.set("sys_win_type_normal_height", normal_height, 36500);
	}
	
	// 小窗口（小窗口一般情况无需调整）
	var small_width = CookieUtil.get("sys_win_type_small_width");
	if (chkObjNull(small_width) || 0 == Number(small_width)) {
		small_width = 0;
		CookieUtil.set("sys_win_type_small_width", small_width, 36500);
	}
	
	var small_height = CookieUtil.get("sys_win_type_small_height");
	if (chkObjNull(small_height) || 0 == Number(small_height)) {
		small_height = 0;
		CookieUtil.set("sys_win_type_small_height", small_height, 36500);
	}
};

/**
 * 打开图片模板的编辑器 <br>
 * option:json格式:{imgurl:"",items:{xm:{xmbh:"",xmmc:"",xsys:"",ztdx:12,xmkd:1,xmgd:1,xmdj:1,xmzj:1,ctbz:"1",ztlx:"",xmxh:1},xb:{....}}}<br>
 * saveAction：保存操作方法，参数：option,返回true则关闭页面否则不关闭。<br>
 * closeAction:关闭操作的回调函数<br>
 * selItems:右击选择项，可以预定义。<br>
 */
function openImgModelEditor(option, saveAction, closeAction, selItems) {
	$("body").data("__img_model_editor_saveaction", saveAction); // 保存事件进行注入到body中
	$("body").data("__img_model_editor_option", option);
	var isSave = "true";
	if (chkObjNull(saveAction)) {
		isSave = "false";
	}
	if(chkObjNull(selItems)){
		selItems = [];
	}
	var url = new URL("taglib.do", "fwdImgModelEditor");
	url.addPara("issave", isSave);
	url.addListData("selitems", selItems);
	openWindow("图片模板编辑", "icon-layout-edit", url, "big",
	function(data) {
		if (!chkObjNull(closeAction)) {
			closeAction(data);
		}
		$("body").removeData("__img_model_editor_saveaction");
		$("body").removeData("__img_model_editor_option");
	});
};

/**
 * 全屏操作工具类
 */
var FullScreenUtil = {
	/**
	 * 全屏(重载)：<br>
	 * FullScreenUtil.full();全屏窗口<br>
	 * FullScreenUtil.full(element);全屏制定dom元素
	 */
	full: function(element) {
		if (chkObjNull(element)) {
			element = document.documentElement; // 全屏窗口
		}
		var requestMethod = element.requestFullScreen || // W3C
		element.webkitRequestFullScreen || // Chrome等
		element.mozRequestFullScreen || // FireFox
		element.msRequestFullScreen; // IE11
		if (requestMethod) {
			requestMethod.call(element);
		} else if (typeof window.ActiveXObject !== "undefined") { // for-IE
			var wscript = new ActiveXObject("WScript.Shell");
			if (wscript !== null) {
				wscript.SendKeys("{F11}");
			}
		}
	},
	/**
	 * 退出全屏
	 */
	exit: function() {
		var exitMethod = document.exitFullscreen || // W3C
		document.mozCancelFullScreen || // Chrome等
		document.webkitExitFullscreen || // FireFox
		document.webkitExitFullscreen; // IE11
		if (exitMethod) {
			exitMethod.call(document);
		} else if (typeof window.ActiveXObject !== "undefined") { // forIE
			var wscript = new ActiveXObject("WScript.Shell");
			if (wscript !== null) {
				wscript.SendKeys("{F11}");
			}
		}
	},
	/**
	 * 注册全屏变更的事件<br>
	 * regChng(callback);窗口的变更事件<br>
	 * regChng(el,callback);具体元素的变更<br>
	 * callback(isfull):isfull-boolean:是否全屏
	 */
	regChng: function(arg1, arg2) {
		var el;
		var callback;
		if (arguments.length == 1) {
			el = document;
			callback = arg1;
		} else if (arguments.length == 2) {
			el = arg1;
			callback = arg2;
		} else {
			alert("ERR:参数错误");
			return;
		}
		el.addEventListener("fullscreenchange",
		function() {
			callback && callback(document.fullscreen);
		});
		el.addEventListener("webkitfullscreenchange",
		function() {
			callback && callback(document.webkitIsFullScreen);
		});
		el.addEventListener("mozfullscreenchange",
		function() {
			callback && callback(document.mozFullScreen);
		});
		el.addEventListener("msfullscreenchange",
		function() {
			callback && callback(document.msFullscreenElement);
		});
	}
};

/**
 * 展示引导
 * <p>
 * 使用：http://www.jq22.com/jquery-info1320<br>
 * data-step="1" data-intro="这里是分布引导的内容……" data-position="right"
 * </p>
 * 
 * @return
 */
function startIntro() {
	if ($(".introjs-overlay").length > 0) {// 如果已经启动，再次调用则自动下一步
		var nextBtn = $(".introjs-nextbutton");
		if (nextBtn.length <= 0) {
			$(".introjs-skipbutton").click();
		} else {
			if (nextBtn.hasClass("introjs-disabled")) {
				$(".introjs-skipbutton").click();
			} else {
				nextBtn.click();
			}
		}
		return;
	}
	introJs().setOptions({
		'overlayOpacity': 0.5,
		'prevLabel': '&larr; 上一步',
		'nextLabel': '下一步 &rarr;',
		'skipLabel': '跳过',
		'doneLabel': '完成'
	}).start();
};