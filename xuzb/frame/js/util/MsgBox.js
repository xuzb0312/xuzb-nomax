/**
 * 消息提示框的封装
 */
var MsgBox = {
	/**
	 * 对json数据进行展示,json格式
	 */
	showJsonData : function(json) {
		$.easyui.showOption(json, {
			title : "数据内容"
		});

		/**
		 * 对于调试模式需要在控制台输出详细的调试信息
		 */
		if (GlobalVars.DEBUG_MODE) {
			console.log(json);
		}
	},
	/**
	 * 对Map数据进行展示,map格式
	 */
	showMapData : function(map) {
		MsgBox.showJsonData(map.values);
	},

	/**
	 * 消息提示
	 */
	alert : function(msg) {
		try {
			$.messager.alert("提示信息", msg, "info");
		} catch (e) {
			alert(msg);
		}
	},

	/**
	 * 底部提示框
	 */
	tipMsg : function(msg) {
		clearTimeout(top.window.timer);// 清除定时器
		if (top.window.$("#sys_tip_msg_div").length <= 0) {
			var divstr = "<div id=\"sys_tip_msg_div\" class=\"sys_tip_msg_div\"></div>";
			top.window.$("body").append(divstr);
		}

		// 提示信息
		top.window.$("#sys_tip_msg_div").html(msg);

		// 显示提示框
		top.window.$("#sys_tip_msg_div").fadeIn(300);
		top.window.timer = setTimeout(function() {
			top.window.$("#sys_tip_msg_div").fadeOut(1000);
		}, 3000);

		top.window.$("#sys_tip_msg_div").live("mouseover", function() {
			clearTimeout(top.window.timer);
		});
		top.window.$("#sys_tip_msg_div").live("mouseout", function() {
			top.window.timer = setTimeout(function() {
				top.window.$("#sys_tip_msg_div").fadeOut(1000);
			}, 3000);
		});
	},

	/**
	 * 采集数据弹窗
	 */
	prompt : function(title, label, tipmsg, required, callback) {
		var url = new URL("taglib.do", "fwdPromptWindow");
		url.addPara("tipmsg", tipmsg);
		url.addPara("required", required);
		url.addPara("label", label);
		openWindow(title, "icon-pencil-go", url, "small", function(data) {
			if ("undefined" == typeof data) {
				return;
			}
			if ("undefined" != typeof callback && null != callback
					&& "" != callback) {
				callback(data);
			}
		});
	}
};