/**
 * 选项卡对象
 */
function TabObj(obj) {
	this.obj = obj;
	this.obj_data = this.obj.data("obj_data"); // 后台写过来的-标签数据HashMap数据
};

/**
 * 初始化
 * 
 * @return
 */
TabObj.prototype.init = function(mapData) {
	// 参数初始化
	this.obj_data = mapData;
	this.obj.data("obj_data", mapData);
	var onchange = this.obj_data.get("onchange");
	var ondelete = this.obj_data.get("ondelete");
	var objid = this.obj.attr("id");
	var tabpageNamesJson = this.obj_data.get("tabpagesname");
	var map_tpn = new HashMap(tabpageNamesJson);

	layui.use("element", function() {
		var element = layui.element;
		element.render("tab", objid);

		/**
		 * 定义事件
		 */
		if (!chkObjNull(onchange)) {
			element.on("tab(" + objid + ")", function(data) {
				eval(onchange + "(this, data.index, data.elem);");
			});
		}
		if (!chkObjNull(ondelete)) {
			element.on("tabDelete(" + objid + ")", function(data) {
				var tpName = data.layid;
				map_tpn.remove(tpName);
				eval(ondelete + "(this, data.index, data.elem);");
			});
		}
	});
};

/**
 * 新增tabPage
 * 
 * @return
 */
TabObj.prototype.add = function(name, title, url, callback) {
	if (chkObjNull(name)) {
		throw new Error("name参数为空");
	}
	if (chkObjNull(title)) {
		throw new Error("title参数为空");
	}
	// 判断是否已经存在了，如果已经存在，则直接切换选中
	if (this.exists(name)) {
		this.select(name);
		return;
	}

	var onadd = this.obj_data.get("onadd");
	var objId = this.obj.attr("id");
	var tabObj = this;
	var tabpageNamesJson = this.obj_data.get("tabpagesname");
	var map_tpn = new HashMap(tabpageNamesJson);

	// tab的pageStyle="position:absolute;left:0px;right:0;top:50px;bottom:0px;width:auto;overflow:hidden;box-sizing:border-box;padding:0px;"
	var conten;
	var frameid = "tabCon_" + randomString(16);
	if ("string" == typeof url) {
		content = "<iframe id='" + frameid + "' name='" + frameid
				+ "' scrolling=\"auto\" frameborder=\"0\" " + "src=\"" + url
				+ "\" " + "style=\"width:100%;height:100%;\"></iframe>";
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
		content = "<iframe id='" + frameid + "' name='" + frameid
				+ "' scrolling=\"auto\" frameborder=\"0\" "
				+ "src=\"shinetag.do?method=fwdPageFrame&frameid=" + frameid
				+ "\" " + "style=\"width:100%;height:100%;\"></iframe>";

		var mapIn = new HashMap();
		mapIn.put("url", url);
		mapIn.put("tpname", name);
		mapIn.put("tabname", objId);
		mapIn.put("callback", callback); // 回调函数
		$("body").data("tab_data_in_" + frameid, mapIn);
	}

	layui
			.use(
					"element",
					function() {
						var element = layui.element;
						element.tabAdd(objId, {
							title : title,
							content : content,
							id : name
						});

						/**
						 * 触发add事件
						 */
						if (!chkObjNull(onadd)) {
							eval(onadd + "(name, title);");
						}

						/**
						 * 选中
						 */
						tabObj.select(name);

						/**
						 * name加入map
						 */
						map_tpn.put(name, true);
						/**
						 * 调试模式信息
						 */
						if (GlobalVars.DEBUG_MODE) {
							var tabElem = $('.layui-tab[lay-filter=' + objId + ']');
							var titElem = tabElem.children(".layui-tab-title");
							var liElem = titElem
									.find('>li[lay-id="' + name + '"]');
							var liIndex = liElem.index();
							var tabItems = tabElem.children(
									'.layui-tab-content').children(
									'.layui-tab-item');
							var tabItem = tabItems.eq(liIndex);
							var debugBtn = $("<button class=\"layui-btn layui-btn-xs layui-btn-warm layui-btn-radius\" style=\"position: absolute; bottom: 10px; left: 7px;\"><i class=\"layui-icon\">&#xe64c;</i>调试信息</button>");
							debugBtn
									.on(
											"click",
											function() {
												if ("string" == typeof url) {
													MsgBoxUtil.alert(
															"请求路径：<br>" + url,
															-1);
												} else {
													var jspPath = $(tabItem)
															.find("iframe")[0].contentWindow
															.getJspPath();
													var jsonStr = JSON
															.stringify(
																	url
																			.getParas(),
																	null, "\t");
													var jsonTipStr = "";
													if (jsonStr.length > 1000) {
														jsonStr = jsonStr
																.substr(0, 900)
																+ "...";
														console.log("详情请求数据：");
														console.log(url
																.getParas());
														jsonTipStr = "<span style=\"color:red;\">【请求数据量较大，请按F12查看详细数据信息】</span>";
													}
													var debugInfo = "请求路径：<br>"
															+ url
																	.getURLString()
															+ "<br>";
													debugInfo = debugInfo
															+ "请求页面：<br>"
															+ jspPath + "<br>";
													debugInfo = debugInfo
															+ "请求数据：<br><pre class=\"codepre\">"
															+ syntaxHighlight(jsonStr)
															+ "</pre>";
													debugInfo = debugInfo
															+ jsonTipStr;
													MsgBoxUtil.alert(debugInfo,
															-1);
													;

												}
											});
							tabItem.append(debugBtn);
						}
					});
};

/**
 * 判断是是否存在
 * 
 * @return
 */
TabObj.prototype.exists = function(tabpageName) {
	var tabpageNamesJson = this.obj_data.get("tabpagesname");
	var map_tpn = new HashMap(tabpageNamesJson);
	if (map_tpn.containsKey(tabpageName)) {
		return true;
	} else {
		return false;
	}
};

/**
 * 删除tabPage
 * 
 * @return
 */
TabObj.prototype.close = function(tabpageName) {
	var objId = this.obj.attr("id");
	layui.use("element", function() {
		var element = layui.element;
		element.tabDelete(objId, tabpageName);
	});
};

/**
 * 选择
 * 
 * @return
 */
TabObj.prototype.select = function(tabpageName) {
	var objId = this.obj.attr("id");
	layui.use("element", function() {
		var element = layui.element;
		element.tabChange(objId, tabpageName);
	});
};