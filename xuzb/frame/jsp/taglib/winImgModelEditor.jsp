<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%@ page import="com.grace.frame.util.DataSet"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String isSave = (String) request.getAttribute("issave");
	DataSet dsItems = (DataSet) request.getAttribute("selitems");
	String borderColor = "#95B8E7";
	if ("v2".equalsIgnoreCase(GlobalVars.VIEW_TYPE)) {
		borderColor = "#ddd";
	}
%>
<style type="text/css">
.fieldset {
	border: none;
	padding: 0;
	border-top: 1px #95B8E7 solid;
	padding-left: 5px;
}

.imgitem {
	position: absolute;
	top: 0px;
	left: 0px;
	width: 50px;
	height: 20px;
	border: 1px #95B8E7 dotted;
	background: rgba(250, 250, 250, 0.8);
	cursor: move;
	overflow: hidden;
	z-index: 100;
}

.imgitemresize {
	position: absolute;
	width: 10px;
	height: 10px;
	right: 0;
	bottom: 0;
	cursor: se-resize;
	z-index: 1000;
}
</style>
<ef:body>
	<ef:layout>
		<ef:centerLayoutPanel border="false">
			<div id="imgcon"
				style="border-left: 1px <%=borderColor%> solid; border-top: 1px <%=borderColor%> solid; border-bottom: 1px <%=borderColor%> solid; overflow: auto; padding: 5px;">
				<div id="imgshow"
					style="border: 1px #95B8E7 dotted; margin: 0px auto; position: relative;">
				</div>
			</div>
		</ef:centerLayoutPanel>
		<ef:rightLayoutPanel width="260" border="false">
			<ef:layout>
				<ef:centerLayoutPanel border="false">
					<ef:tab name="tab">
						<ef:tabPage title="新增" iconCls="icon-note-add">
							<ef:form border="false" rowcount="3" name="formadd">
								<ef:textinput name="xmbh" label="编号" colspan="3" required="true" />
								<ef:textinput name="xmmc" label="名称" colspan="3" />
								<ef:textinput name="xsys" label="字体颜色" colspan="3"
									validType="color" />
								<ef:textinput name="ztdx" label="字体大小" colspan="3"
									dataType="number" mask="##########0" value="12" />
								<ef:textinput name="ztlx" label="字体类型" colspan="3" />
								<ef:checkboxList name="ctbz" label="是否粗体" rowcount="1"
									colspan="3">
									<ef:data key="1" value="是" />
								</ef:checkboxList>
								<ef:textinput name="xmkd" label="项目宽度" colspan="3"
									dataType="number" mask="##########0" value="85" />
								<ef:textinput name="xmgd" label="项目高度" colspan="3"
									dataType="number" mask="##########0" value="20" />
								<ef:textinput name="xmdj" label="项目顶距" colspan="3"
									dataType="number" mask="##########0" value="10" />
								<ef:textinput name="xmzj" label="项目左距" colspan="3"
									dataType="number" mask="##########0" value="10" />
								<ef:textinput name="xmxh" label="项目序号" colspan="3"
									dataType="number" mask="##########0" value="1" />
							</ef:form>
							<ef:buttons closebutton="false">
								<ef:button value="新增" onclick="btnAddClick();"></ef:button>
							</ef:buttons>
						</ef:tabPage>
						<ef:tabPage title="修改" iconCls="icon-note-edit" selected="true">
							<fieldset class="fieldset">
								<legend>
									信息项目列表
								</legend>
							</fieldset>
							<ef:form border="false" rowcount="2">
								<ef:dropdownList name="xmlist" colspan="2"
									onchange="xmListChng();"></ef:dropdownList>
							</ef:form>
							<fieldset class="fieldset">
								<legend>
									信息项属性
								</legend>
							</fieldset>
							<ef:form border="false" rowcount="3" name="formmodify">
								<ef:textinput name="xmbh" label="编号" colspan="3" required="true"
									onchange="btnConfirmClick();" />
								<ef:textinput name="xmmc" label="名称" colspan="3"
									onchange="btnConfirmClick();" />
								<ef:textinput name="xsys" label="字体颜色" colspan="3"
									onchange="btnConfirmClick();" validType="color" />
								<ef:textinput name="ztdx" label="字体大小" colspan="3"
									dataType="number" mask="##########0"
									onchange="btnConfirmClick();" />
								<ef:textinput name="ztlx" label="字体类型" colspan="3"
									onchange="btnConfirmClick();" />
								<ef:dropdownList name="ctbz" label="是否粗体" colspan="3"
									onchange="btnConfirmClick();">
									<ef:data key="1" value="粗体[bold]" />
								</ef:dropdownList>
								<ef:textinput name="xmkd" label="项目宽度" colspan="3"
									dataType="number" mask="##########0"
									onchange="btnConfirmClick();" />
								<ef:textinput name="xmgd" label="项目高度" colspan="3"
									dataType="number" mask="##########0"
									onchange="btnConfirmClick();" />
								<ef:textinput name="xmdj" label="项目顶距" colspan="3"
									dataType="number" mask="##########0"
									onchange="btnConfirmClick();" />
								<ef:textinput name="xmzj" label="项目左距" colspan="3"
									dataType="number" mask="##########0"
									onchange="btnConfirmClick();" />
								<ef:textinput name="xmxh" label="项目序号" colspan="3"
									dataType="number" mask="##########0"
									onchange="btnConfirmClick();" />
								<ef:text color="red" value="注：双击图片模板，快速新增项目" colspan="3" />
							</ef:form>
							<ef:buttons closebutton="false">
								<ef:button value="确认" onclick="btnConfirmClick(true);"
									name="btnConfirm"></ef:button>
								<ef:button value="删除" onclick="btnDelClick();" name="btnDel"></ef:button>
							</ef:buttons>
						</ef:tabPage>
					</ef:tab>
				</ef:centerLayoutPanel>
				<ef:bottomLayoutPanel height="50">
					<ef:buttons closebutton="true">
						<%
							if ("true".equals(isSave)) {
						%>
						<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
						<%
							}
						%>
					</ef:buttons>
				</ef:bottomLayoutPanel>
			</ef:layout>
		</ef:rightLayoutPanel>
	</ef:layout>
	<ef:menu name="selitem" width="100">
		<%
			for (int i = 0, n = dsItems.size(); i < n; i++) {
						String key = dsItems.getString(i, "key");
						String value = dsItems.getString(i, "value");
						String clickE = "menuAdd('" + key + "', '" + value
								+ "');";
						String showValue = value + ":" + key;
		%>
		<ef:menuItem value="<%=showValue%>" onclick="<%=clickE %>" iconCls="icon-bullet-star"></ef:menuItem>
		<%
			}
		%>
	</ef:menu>
</ef:body>
<script type="text/javascript">
var _imgOption;
var _saveAction;
function onLoadComplete() {
	var tepOption = {
		imgurl: null,
		items: {}
	};
	_imgOption = $.extend(true, tepOption, window.parent.$("body").data("__img_model_editor_option"));
	_saveAction = window.parent.$("body").data("__img_model_editor_saveaction");
	calImgWH(initEditor);
}

//初始化Editor
function initEditor() {
	//设置容器的大小
	dealImgConSize();
	$("#imgshow").css("width", (_imgOption.width) + "px");
	$("#imgshow").css("height", (_imgOption.height) + "px");
	//背景
	$("#imgshow").css("background-image", "url('" + _imgOption.imgurl + "')");

	//修改form的信息
	loadOpt();
	syncModifyForm();

	//模板渲染
	var items = getImgItemArr();
	for (var i = 0,
	n = items.length; i < n; i++) {
		syncItem2Img(items[i]);
	}

	//绑定快捷的新增元素方法--双机增加项目
	$("#imgshow").on("dblclick",
	function(e) {
		var xmbh = "";
		for (var i = 0; true; i++) {
			xmbh = "item" + i;
			if (!chkItemBhExist(xmbh)) {
				break;
			}
		}
		getObject("formadd.xmbh").setValue(xmbh);
		getObject("formadd.xmmc").setValue("新项目");
		getObject("formadd.xmzj").setValue(e.clientX - $(this).offset().left - 5);
		getObject("formadd.xmdj").setValue(e.clientY - $(this).offset().top - 5);
		btnAddClick();
	});

	$("#imgshow").on("contextmenu",
		function(e) {
			e.preventDefault();//阻止事件响应
			var selitem = getObject("selitem");
			selitem.show(e.pageX, e.pageY);
			var dataMap = new HashMap();
			dataMap.put("xmzj", e.clientX - $(this).offset().left - 5);
			dataMap.put("xmdj", e.clientY - $(this).offset().top - 5);
			selitem.setMapData(dataMap);
	});
}

//右击选择新增
function menuAdd(key, value){
	var selitem = getObject("selitem");
	var dataMap = selitem.getMapData();
	selitem.clearMapData();
	
	getObject("formadd.xmbh").setValue(key);
	getObject("formadd.xmmc").setValue(value);
	getObject("formadd.xmzj").setValue(dataMap.get("xmzj"));
	getObject("formadd.xmdj").setValue(dataMap.get("xmdj"));
	btnAddClick();
}

//新增元素
function syncItem2Img(itemOpt, srcxmbh) {
	var xmbh = itemOpt.xmbh;
	var xmmc = itemOpt.xmmc;
	var xsys = itemOpt.xsys;
	var ztdx = itemOpt.ztdx;
	var ztlx = itemOpt.ztlx;
	var ctbz = itemOpt.ctbz;
	var xmkd = itemOpt.xmkd;
	var xmgd = itemOpt.xmgd;
	var xmdj = itemOpt.xmdj;
	var xmzj = itemOpt.xmzj;
	var xmxh = itemOpt.xmxh;
	if (chkObjNull(xmbh)) {
		MsgBox.alert("项目编号不允许为空");
		return;
	}
	if (chkObjNull(srcxmbh)) {
		srcxmbh = itemOpt.xmbh;
	}

	//原来的显示元素移除掉
	$("#imgshow").find("[_xmbh='" + srcxmbh + "']").remove();

	var resizeObj = $("<div class=\"imgitemresize\"></div>");
	var itemObj = $("<div class=\"imgitem\" tabIndex=\"1\"></div>");
	if (!chkObjNull(xmxh)) {
		itemObj.css("z-index", xmxh);
		itemObj.attr("tabIndex", xmxh);
	}
	if (!chkObjNull(xsys)) {
		itemObj.css("color", xsys);
	}
	if (!chkObjNull(ztdx)) {
		itemObj.css("font-size", ztdx + "px");
	}
	if (!chkObjNull(ztdx)) {
		itemObj.css("font-size", ztdx + "px");
	}
	if (!chkObjNull(ztlx)) {
		itemObj.css("font-family", ztlx);
	}
	if (!chkObjNull(ztdx)) {
		itemObj.css("font-size", ztdx + "px");
	}
	if (!chkObjNull(ztdx) && "1" == ctbz) {
		itemObj.css("font-weight", "bold");
	}
	if (!chkObjNull(xmkd)) {
		itemObj.css("width", xmkd + "px");
	}
	if (!chkObjNull(xmgd)) {
		itemObj.css("height", xmgd + "px");
	}
	if (!chkObjNull(xmdj)) {
		itemObj.css("top", xmdj + "px");
	}
	if (!chkObjNull(xmzj)) {
		itemObj.css("left", xmzj + "px");
	}
	if (chkObjNull(xmmc)) {
		xmmc = "";
	} else {
		xmmc = ":" + xmmc;
	}
	itemObj.append(xmbh + xmmc);
	itemObj.append(resizeObj);

	//数据
	itemObj.attr("_xmbh", xmbh);

	//绑定事件
	resizeObj.on("mousedown",
	function(e) {
		var e = e || window.event;
		var left = e.clientX;
		var top = e.clientY;
		var width = itemObj.width();
		var height = itemObj.height();
		var itemPosition = itemObj.position();
		$(document).on("mousemove",
		function(e) {
			var e = e || window.event;
			var disX = e.clientX - left;
			var disY = e.clientY - top;
			var r_w = width + disX;
			var r_h = height + disY;
			r_w = chkW(r_w, itemPosition.left);
			r_h = chkH(r_h, itemPosition.top);
			itemObj.css("width", r_w + "px");
			itemObj.css("height", r_h + "px");
		});
		$(document).on("mouseup",
		function() {
			$(document).off("mousemove");
			$(document).off("mouseup");
			var xmbh = itemObj.attr("_xmbh");
			modifyItem2Options(xmbh, {
				xmkd: itemObj.width(),
				xmgd: itemObj.height()
			});
			syncModifyForm(xmbh);
		});
		return false;
	});

	itemObj.on("mousedown",
	function(e) {
		var e = e || window.event;
		var left = e.clientX;
		var top = e.clientY;
		var width = itemObj.width();
		var height = itemObj.height();
		var itemPosition = itemObj.position();
		$(document).on("mousemove",
		function(e) {
			var e = e || window.event;
			var disX = e.clientX - left;
			var disY = e.clientY - top;
			var r_l = itemPosition.left + disX;
			var r_t = itemPosition.top + disY;
			r_l = chkLeft(r_l, width);
			r_t = chkTop(r_t, height);
			itemObj.css("left", r_l + "px");
			itemObj.css("top", r_t + "px");
		});
		$(document).on("mouseup",
		function() {
			$(document).off("mousemove");
			$(document).off("mouseup");
			var xmbh = itemObj.attr("_xmbh");
			var dqPos = itemObj.position();
			modifyItem2Options(xmbh, {
				xmzj: dqPos.left,
				xmdj: dqPos.top
			});

			syncModifyForm(xmbh);
		});
	});

	itemObj.on("click",
	function() {
		getObject("tab").selectByTitle("修改");
		var xmbh = itemObj.attr("_xmbh");
		syncModifyForm(xmbh);
		itemFocus(xmbh);
	});
	itemObj.keydown(function(e) {
		if (e.which == 46) {
			btnDelClick(); // 删除
			e.preventDefault();
			return;
		}
		var xmbh = itemObj.attr("_xmbh");
		if(e.ctrlKey && e.which == 67){ // 复制信息
			getObject("formadd").setData(getItemByOptions(xmbh));
			e.preventDefault();
			return;
		}
		var itemPosition = itemObj.position();
		var width = itemObj.width();
		var height = itemObj.height();
		if (e.which == 37) {
			itemObj.css("left", chkLeft(itemPosition.left - 1, width) + "px");
		} else if (e.which == 38) {
			itemObj.css("top", chkTop(itemPosition.top - 1, height) + "px");
		} else if (e.which == 39) {
			itemObj.css("left", chkLeft(itemPosition.left + 1, width) + "px");
		} else if (e.which == 40) {
			itemObj.css("top", chkTop(itemPosition.top + 1, height) + "px");
		}
		
		var dqPos = itemObj.position();
		modifyItem2Options(xmbh, {
			xmzj: dqPos.left,
			xmdj: dqPos.top
		});
		syncModifyForm(xmbh);
		e.preventDefault();
	});
	itemObj.on("dblclick",
	function(e) {
		e.preventDefault();
		return false;
	});

	itemObj.on("contextmenu",
	function(e) {
		e.preventDefault();
		return false;
	});

	//放入容器
	$("#imgshow").append(itemObj);
}

//新增
function btnAddClick() {
	if (!getObject("formadd").chkFormData(true)) {
		return
	}
	//基础数据新增
	var jsonData = getObject("formadd").getData();
	if (chkItemBhExist(jsonData.xmbh)) {
		MsgBox.alert("项目已经存在，无法再次新增");
		return;
	}
	//新增
	jsonData = dealSizeAndPos(jsonData);
	addItem2Options(jsonData);
	loadOpt();
	syncModifyForm(jsonData.xmbh);
	syncItem2Img(jsonData);
	itemFocus(jsonData.xmbh);

	//处理默认
	var jsonDataTemp = $.extend(true, {},
	jsonData);
	jsonDataTemp.xmdj = jsonDataTemp.xmdj + 5;
	jsonDataTemp.xmzj = jsonDataTemp.xmzj + 5;
	jsonDataTemp.xmxh = jsonDataTemp.xmxh + 1;
	jsonDataTemp.xmbh = "";
	jsonDataTemp.xmmc = "";
	jsonDataTemp = dealSizeAndPos(jsonDataTemp);
	getObject("formadd").setData(jsonDataTemp);
}

//处理位置和大小信息
function dealSizeAndPos(jsonData) {
	jsonData.xmzj = chkLeft(jsonData.xmzj, jsonData.xmkd);
	jsonData.xmdj = chkTop(jsonData.xmdj, jsonData.xmgd);

	if (jsonData.xmzj < 0) {
		jsonData.xmzj = 0;
		jsonData.xmkd = chkW(jsonData.xmkd, 0);
	}
	if (jsonData.xmdj < 0) {
		jsonData.xmdj = 0;
		jsonData.xmgd = chkH(jsonData.xmgd, 0);
	}
	return jsonData;
}

//确认
function btnConfirmClick(focus) {
	if (!getObject("formmodify").chkFormData(true)) {
		return
	}
	var xmbh = getObject("xmlist").getValue();
	if (chkObjNull(xmbh)) {
		alert("请先选择修改的项目");
		getObject("xmlist").focus();
		return;
	}
	//基础数据新增
	var jsonData = getObject("formmodify").getData();
	jsonData = dealSizeAndPos(jsonData);
	modifyItem2Options(xmbh, jsonData);

	loadOpt();
	syncModifyForm(jsonData.xmbh);
	syncItem2Img(jsonData, xmbh);
	if(focus){
		itemFocus(jsonData.xmbh);
	}
}

//删除
function btnDelClick() {
	var xmbh = getObject("xmlist").getValue();
	if (chkObjNull(xmbh)) {
		alert("请先选择修改的项目");
		getObject("xmlist").focus();
		return;
	}
	delItem2Options(xmbh);
	loadOpt();
	syncModifyForm();

	//删除操作
	$("#imgshow").find("[_xmbh='" + xmbh + "']").remove();
}

//下拉选择框
function xmListChng() {
	var xmbh = getObject("xmlist").getValue();
	syncModifyForm(xmbh);
	itemFocus(xmbh);
}

//修改item-opt
function modifyItem2Options(xmbh, json) {
	if (!chkItemBhExist(xmbh)) {
		MsgBox.alert("项目编号不存在，请检查");
		return false;
	}
	var srcJson = delItem2Options(xmbh);
	json = $.extend(true, {},
	srcJson, json);
	addItem2Options(json);
}

//删除item-opt
function delItem2Options(xmbh) {
	if (!chkItemBhExist(xmbh)) {
		MsgBox.alert("项目编号不存在，请检查");
		return false;
	}
	var srcJson = getItemByOptions(xmbh);
	delete _imgOption.items[xmbh];
	return srcJson;
}

//获取options
function getItemByOptions(xmbh) {
	if (!chkItemBhExist(xmbh)) {
		MsgBox.alert("项目编号不存在，请检查");
		return false;
	}
	var srcJson = _imgOption.items[xmbh];
	return srcJson;
}

//新增item-opt
function addItem2Options(json) {
	if (chkItemBhExist(json.xmbh)) {
		MsgBox.alert("项目编号已经存在，请检查");
		return false;
	}
	_imgOption.items[json.xmbh] = json;
	return true;
}

//保存事件
function btnSaveClick() {
	if (chkObjNull(_saveAction)) {
		return;
	}
	if (_saveAction(_imgOption)) {
		closeWindow(true);
	}
}

//获取img的item的数组
function getImgItemArr() {
	var json = [];
	for (var key in _imgOption.items) {
		var itemArr = _imgOption.items[key];
		json.push(itemArr);
	}
	return json;
}

//获取下拉框的opt
function getDDLOpt() {
	var json = [];
	for (var key in _imgOption.items) {
		var itemArr = _imgOption.items[key];
		var itemText = itemArr.xmbh;
		if (!chkObjNull(itemArr.xmmc)) {
			itemText = itemText + ":" + itemArr.xmmc;
		}
		var item = {
			key: key,
			value: itemText
		};
		json.push(item);
	}
	var by = function(name) {
		return function(o, p) {
			var a, b;
			if (typeof o === "object" && typeof p === "object" && o && p) {
				a = o[name];
				b = p[name];
				if (a === b) {
					return 0;
				}
				if (typeof a === typeof b) {
					return a < b ? -1 : 1;
				}
				return typeof a < typeof b ? -1 : 1;
			} else {
				throw ("error");
			}
		}
	}
	json.sort(by("key"));
	return json;
}

//容器大小的适应
function dealImgConSize() {
	$("#imgcon").css("height", $("#imgcon").parent().height() - 12);
}

//检查项目编号是否已经存在
function chkItemBhExist(xmbh) {
	return typeof(_imgOption.items[xmbh]) != "undefined";
}

//计算图片的宽度和高度-加载完成地图后执行
function calImgWH(callback) {
	if (chkObjNull(_imgOption.width)) {
		var img = new Image();
		img.src = _imgOption.imgurl;
		img.onload = function(){
			_imgOption.width = img.width;
			_imgOption.height = img.height;
			callback();
		}
	}
}

//检查宽高左顶
function chkW(w, left) {
	if (w < 10) {
		w = 10;
	}
	if (w + left + 2 > _imgOption.width) {
		return _imgOption.width - left - 2;
	} else {
		return w;
	}
}

function chkLeft(left, w) {
	if (left < 2) {
		left = 2;
	}
	if (w + left + 2 > _imgOption.width) {
		return _imgOption.width - w - 2;
	} else {
		return left;
	}
}

function chkH(h, top) {
	if (h < 5) {
		h = 5;
	}
	if (h + top + 2 > _imgOption.height) {
		return _imgOption.height - top - 2;
	} else {
		return h;
	}
}

function chkTop(top, h) {
	if (top < 2) {
		top = 2;
	}
	if (h + top + 2 > _imgOption.height) {
		return _imgOption.height - h - 2;
	} else {
		return top;
	}
}

//加载opt
function loadOpt() {
	getObject("xmlist").reloadOpt(getDDLOpt());
}

//修改form的信息
function syncModifyForm(xmbh) {
	if (chkObjNull(xmbh)) {
		getObject("xmlist").clear();
		getObject("formmodify").clear();
		getObject("formmodify").setReadOnly(true);
		getObject("btnConfirm").enable();
		getObject("btnDel").enable();
	} else {
		getObject("xmlist").setValue(xmbh);
		getObject("formmodify").setData(getItemByOptions(xmbh));
		getObject("formmodify").setReadOnly(false);
		getObject("btnConfirm").enable();
		getObject("btnDel").enable();
	}
}

//焦点设置focus
function itemFocus(xmbh) {
	if (chkObjNull(xmbh)) {
		return;
	}
	$(".imgitem").css("border", "1px #95B8E7 dotted");
	var itemObj = $("#imgshow").find("[_xmbh='" + xmbh + "']");
	itemObj.css("border", "1px #FF5722 dotted");
	itemObj.focus();
}

//动态调整页面尺寸
$(window).resize(function() {
	setTimeout(function() {
		dealImgConSize();
	},
	300);
});
</script>
