/**
 * 数据加载面板
 */
function LoadPanelObj(obj) {
	this.obj = obj;
	this.id = this.obj.attr("id");
};

/**
 * loadPage前需要先预处理obj对象
 * 
 * @return
 */
LoadPanelObj.prototype.initObject = function(panelId) {
	var flag = true;// 是否继续加载
	var yPanelId = this.obj.attr("_panelid");
	if (!chkObjNull(yPanelId) && !chkObjNull(panelId)) {
		// 都不为空时
		this.obj.attr("id", this.id + "_hidden_" + yPanelId);
		this.obj.css("width", "0px");
		this.obj.css("height", "0px");

		var tempObj = $("#" + this.id + "_hidden_" + panelId);
		if (tempObj.length > 0) {
			this.obj = tempObj;
			this.obj.attr("id", this.id);
			this.obj.css("width", "100%");
			this.obj.css("height", "99%");
			flag = false;
		} else {
			var newObj = $("<iframe obj_type=\"loadpanel\" id=\""
					+ this.id
					+ "\" scrolling=\"auto\" frameborder=\"0\" style=\"width: 100%; height: 99%;\" src=\"\"></iframe>");
			newObj.attr("_panelid", panelId);
			newObj.insertBefore(this.obj);
			this.obj = newObj;
		}
	} else if (chkObjNull(yPanelId) && !chkObjNull(panelId)) {
		// 原来为空，新不为空
		var tempObj = $("#" + this.id + "_hidden_" + panelId);
		if (tempObj.length > 0) {
			this.obj.remove();
			this.obj = tempObj;
			this.obj.attr("id", this.id);
			this.obj.css("width", "100%");
			this.obj.css("height", "99%");
			flag = false;
		} else {
			this.obj.attr("_panelid", panelId);
		}
	} else if (!chkObjNull(yPanelId) && chkObjNull(panelId)) {
		// 原来不为空，现在为空
		this.obj.attr("id", this.id + "_hidden_" + yPanelId);
		this.obj.css("width", "0px");
		this.obj.css("height", "0px");

		var newObj = $("<iframe obj_type=\"loadpanel\" id=\""
				+ this.id
				+ "\" scrolling=\"auto\" frameborder=\"0\" style=\"width: 100%; height: 99%;\" src=\"\"></iframe>");
		newObj.insertBefore(this.obj);
		this.obj = newObj;
	} else {
		// 都为空时do-nothing
	}
	return flag;
};

/**
 * 加载页面数据
 */
LoadPanelObj.prototype.loadPage = function(url, panelId) {
	// 首先处理调试信息
	if (GlobalVars.DEBUG_MODE) {
		var objTmp = this.obj;
		$("#" + this.id + "_debug_msg_url").hide();
		$("#" + this.id + "_debug_msg_data").hide();
		$("#" + this.id + "_debug_msg_url").off("click");
		$("#" + this.id + "_debug_msg_data").off("click");
		if (!chkObjNull(url)) {
			if ("string" == typeof url) {
				$("#" + this.id + "_debug_msg_url").on("click", function() {
					MsgBox.alert(url);
				});
				$("#" + this.id + "_debug_msg_url").show();
			} else {
				$("#" + this.id + "_debug_msg_url").on(
						"click",
						function() {
							var jspPath = objTmp[0].contentWindow.getJspPath();
							MsgBox.alert("请求路径：<br/>" + url.getURLString()
									+ "<hr/>JSP页面：<br/>" + jspPath);
						});
				$("#" + this.id + "_debug_msg_data").on("click", function() {
					MsgBox.showJsonData(url.getParas());
				});
				$("#" + this.id + "_debug_msg_url").show();
				$("#" + this.id + "_debug_msg_data").show();
			}
		}
	} else {
		// 预处理--返回是否继续加载-缓存处理--只有非调试模式才会启用缓存
		if (!this.initObject(panelId)) {
			return true;
		}
	}
	// 加载页面
	if ("string" != typeof url) {
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
	}
	this.obj.data("requrl", url);// url放到该对象的data中

	if ("string" == typeof url) {
		this.obj.attr("src", url);// 直接指向该地址
	} else {
		var frameid = randomString(16); // 窗口唯一id
		var src = "taglib.do?method=fwdPageFrame&panelid=" + this.id
				+ "&frameid=" + frameid;
		this.obj.attr("src", src);
	}
};

/**
 * 关闭
 * 
 * @return
 */
LoadPanelObj.prototype.close = function() {
	this.obj.removeAttr("_panelid");// 首先将属性移除-不进行缓存
	this.loadPage("");// 加载空页
};

/**
 * 清空页面数据--兼容性设置--后续减少使用clear,直接使用close关闭当前页
 */
LoadPanelObj.prototype.clear = LoadPanelObj.prototype.close;

/**
 * 清空缓存
 */
LoadPanelObj.prototype.clearCache = function() {
	if (!GlobalVars.DEBUG_MODE) {
		var parent = this.obj.parent();// 父节点
		parent.find("iframe[obj_type='loadpanel']").remove();// 所有子节点的loadpanel全部移除

		// 重新构建
		var newObj = $("<iframe obj_type=\"loadpanel\" id=\""
				+ this.id
				+ "\" scrolling=\"auto\" frameborder=\"0\" style=\"width: 100%; height: 99%;\" src=\"\"></iframe>");
		newObj.appendTo(parent);
		this.obj = newObj;
		this.clear();
	} else {
		this.clear();
	}
};
