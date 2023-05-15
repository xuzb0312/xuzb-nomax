/**
 * 工具方法-新版标签库的工具方法 2017年12月4日 yjc<br>
 * *******************************************
 */

/**
 * 标签对象--结合jsp的标签共同使用。-- 随时可以扩充标签对象
 */
// 标签对象的类型数组
var ShineObjType = {
	button: "ButtonObj",
	// 按钮
	textarea: "TextareaObj",
	// 文本域
	checkbox: "CheckboxObj",
	// 开关，单选框
	formlinegroup: "FormLineGroupObj",
	// 表单行组
	checkboxlist: "CheckboxListObj",
	// 复选框
	radiobuttonlist: "RadioButtonListObj",
	// 单选框
	dropdownlist: "DropdownListObj",
	// 单选下拉框
	textinput: "TextInputObj",
	// 文本输入框
	form: "FormObj",
	// form表单
	index: "IndexObj",
	// 目录索引
	progress: "ProgressObj",
	// 进度条
	tab: "TabObj" // 选项卡
};

/**
 * 获取标签对象
 */
function getObject(para) {
	var obj;
	if (typeof(para) == "string") {
		var paraArr = para.split(".");
		var id = "";
		for (var i = 0,
		n = paraArr.length; i < n; i++) {
			if (i == n - 1) {
				id = id + "#" + paraArr[i];
			} else {
				id = id + "#" + paraArr[i] + " ";
			}
		}
		obj = $(id); // 字符串的化，去获取
	} else {
		obj = para;
	}
	var type = obj.attr("obj_type"); // 书写的标签上均增加了obj_type属性来记录标签的类型
	var tagObj = ShineObjType[type];
	if (typeof tagObj == "undefined") {
		throw new Error("该标签[" + para + "]不支持此方法获取其对象。");
	} else {
		var objFunc = new Function("obj", "return new " + tagObj + "(obj);");
		var tag = objFunc(obj);
		tag.type = type;
		return tag;
	}
};

// 为简便使用get=getObject;
var get = getObject;

/**
 * 重新优化此方法
 */
function chkObjNull(obj) {
	if (typeof(obj) == "undefined") {
		return true; // 未定义
	} else if (typeof(obj) == "string") {
		if (null == obj || "" == obj) {
			return true;
		} else {
			return false;
		}
	} else {
		if (null == obj) {
			return true;
		} else {
			return false;
		}
	}
};

/**
 * 获取随机数-字符串
 */
function randomString(len) {　　len = len || 32;　　
	var chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";　　
	var maxPos = chars.length;　　
	var str = "";　　
	for (var i = 0; i < len; i++) {　　　　str += chars.charAt(Math.floor(Math.random() * maxPos));　　
	}　　
	return str;
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
		if (chkObjNull(defalut)) {
			throw new Error("页面中不存在jbjgid参数");
		}
		return defalut;
	}
	return $("#__jbjgid").val();
};

// 获取经办机构权限范围
function getJbjgqxfw(defalut) {
	if ($("#__jbjgqxfw").length <= 0) {
		if (chkObjNull(defalut)) {
			throw new Error("页面中不存在__jbjgqxfw参数");
		}
		return defalut;
	}
	return $("#__jbjgqxfw").val();
};

//获取页面YHID
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
	MsgBoxUtil.alert(jsppath, -1);
	return true;
};

// 获取JSP路径
function getJspPath() {
	if ($("#__jsppath").length <= 0) {
		return false;
	}
	return $("#__jsppath").val();
};

/**
 * JSON高亮展示
 */
function syntaxHighlight(json) {
	json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
	json = json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
	function(match) {
		var cls = 'number';
		if (/^"/.test(match)) {
			if (/:$/.test(match)) {
				cls = 'key';
			} else {
				cls = 'string';
			}
		} else if (/true|false/.test(match)) {
			cls = 'boolean';
		} else if (/null/.test(match)) {
			cls = 'null';
		}
		return '<span class="' + cls + '">' + match + '</span>';
	});
	var lines = json.split("\n");
	for (var i = 0,
	n = lines.length; i < n; i++) {
		lines[i] = lines[i].replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	}
	return lines.join("<br>");;
};

//整个页面的等待mask-展示
function showLoading() {
	if ($("#sys_loading_mask_div").length <= 0) {
		var divstr = "<div id=\"sys_loading_mask_div\" class=\"sys-loading-mask\"><input id='sys_loading_mask_div_focus_input' style=\"position: absolute;left:-200px;top:-200px;\" type=\"text\"/></div>";
		$("body").append(divstr);
	} else {
		$("#sys_loading_mask_div").show();
	}
	$("#sys_loading_mask_div_focus_input").focus();
};

//整个页面的等待mask-取消
function hideLoading() {
	if ($("#sys_loading_mask_div").length > 0) {
		$("#sys_loading_mask_div").hide();
	}
};

/**
 * 提示使用
 */
var MsgBoxUtil = {
	errTips: function(msg) { // 错误提示信息，框架内部调用
		if (chkObjNull(msg)) {
			return;
		}
		msg = msg + "";
		layui.use("layer",
		function() {
			var layer = layui.layer;
			layer.ready(function() {
				var time = 2500;
				if (msg.length > 20) {
					time = 5000;
				}
				layer.msg(msg, {
					icon: 5,
					shift: 6,
					time: time
				});
			});
		});
	},
	tips: function(msg) {
		if (chkObjNull(msg)) {
			return;
		}
		msg = msg + "";
		layui.use("layer",
		function() {
			var layer = layui.layer;
			layer.ready(function() {
				layer.msg(msg);
			});
		});
	},
	tagErrTips: function(msg, tagObj) { // 依附与信息框的错误信息提示：tagObj为jquery对象
		if (chkObjNull(msg)) {
			return;
		}
		msg = msg + "";
		layui.use("layer",
		function() {
			var layer = layui.layer;
			layer.ready(function() {
				layer.tips(msg, tagObj[0], {
					tips: [3, "#FF5722"]
				});
			});
		});
	},
	alertLayerIndex: null,
	// 提示层index
	alert: function(msg, icon) { // alert 提示
		if (chkObjNull(msg)) {
			return;
		}
		if (chkObjNull(icon)) {
			icon = 7;
		}
		msg = msg + "";
		layui.use("layer",
		function() {
			var layer = layui.layer;
			layer.ready(function() {
				if (icon >= 0) {
					MsgBoxUtil.alertLayerIndex = layer.alert(msg, {
						icon: icon
					},
					function(index) {
						layer.close(index);
						MsgBoxUtil.alertLayerIndex = null;
					});
				} else {
					MsgBoxUtil.alertLayerIndex = layer.alert(msg,
					function(index) {
						layer.close(index);
						MsgBoxUtil.alertLayerIndex = null;
					});
				}
			});
		});
	},
	load: function(icon) { // 加载框-返回index;icon:0,1,2,3:icon=3时无法手工关闭，除非重新刷新页面
		layui.use("layer",
		function() {
			var layer = layui.layer;
			layer.ready(function() {
				if (chkObjNull(icon)) {
					return layer.load(2);
				} else if (3 == icon) {
					return layer.msg("加载中,请稍候...", {
						icon: 16,
						shade: 0.3,
						time: 60000
					});
				} else {
					return layer.load(icon);
				}
			});
		});
	},
	hideLoad: function(index) { // 隐藏加载框-如果没有传递index则关闭所有的load
		layui.use("layer",
		function() {
			var layer = layui.layer;
			layer.ready(function() {
				if (chkObjNull(index)) {
					layer.closeAll("loading");
				} else {
					layer.close(index);
				}
			});
		});
	}
};
/**
 * 弹出窗口工具类
 */
(function(win, undefined) {
	/**
	 * 窗口的类型
	 */
	var WindowType = {
		big: "big",
		// 大窗口
		normal: "normal",
		// 正常，中等的
		small: "small" // 小窗口
	};

	/**
	 * 根据窗口类型获取默认的窗口大小
	 * 
	 * @return
	 */
	function getDefalutWindowWHByType(type) {
		var map = new HashMap();
		// 由于不同的电脑，分辨率不一致，所以通过cookies进行微调，适应不同分辨率。
		// ctrl+alt+F2打开cookie设置窗口
		var widthdiff = CookieUtil.get("sys_win_type_" + type + "_width");
		if (chkObjNull(widthdiff)) {
			widthdiff = 0;
		} else {
			var widthdiff = Number(widthdiff);
			if (isNaN(widthdiff)) {
				widthdiff = 0;
			}
		}
		var heightdiff = CookieUtil.get("sys_win_type_" + type + "_height");
		if (chkObjNull(heightdiff)) {
			heightdiff = 0;
		} else {
			var heightdiff = Number(heightdiff);
			if (isNaN(heightdiff)) {
				heightdiff = 0;
			}
		}

		if (WindowType.big == type) {
			map.put("width", 1000 + widthdiff);
			map.put("height", 580 + heightdiff);
		} else if (WindowType.normal == type) {
			map.put("width", 900 + widthdiff);
			map.put("height", 520 + heightdiff);
		} else if (WindowType.small == type) {
			map.put("width", 500 + widthdiff);
			map.put("height", 200 + heightdiff);
		} else {
			throw new Error("输入的窗口类型不正确（big,normal,small）");
		}
		return map.values;
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
			if (sW >= 1920) {
				big_width = 360;
			} else if (sW >= 1600) {
				big_width = 150;
			} else {
				big_width = 0;
			}
			CookieUtil.set("sys_win_type_big_width", big_width, 36500);
		}
		var big_height = CookieUtil.get("sys_win_type_big_height");
		if (chkObjNull(big_height) || 0 == Number(big_height)) {
			if (sH >= 1080) {
				big_height = 120;
			} else if (sH >= 900) {
				big_height = 50;
			} else {
				big_height = 0;
			}
			CookieUtil.set("sys_win_type_big_height", big_height, 36500);
		}

		// 普通
		var normal_width = CookieUtil.get("sys_win_type_normal_width");
		if (chkObjNull(normal_width) || 0 == Number(normal_width)) {
			if (sW >= 1600) {
				normal_width = 100;
			} else {
				normal_width = 0;
			}
			CookieUtil.set("sys_win_type_normal_width", normal_width, 36500);
		}
		var normal_height = CookieUtil.get("sys_win_type_normal_height");
		if (chkObjNull(normal_height) || 0 == Number(normal_height)) {
			if (sH >= 900) {
				normal_height = 30;
			} else {
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
	 * 原则每个页面只允许开一个window页; <br>
	 * 创建几种重载机制：<br>
	 * 1.openWindow(title,url,windowtype);<br>
	 * 2.openWindow(title,url,windowtype,callback);<br>
	 * 3.openWindow(title,icon,url,windowtype,callback);<br>
	 * 4.openWindow(title,icon,url,width,height,callback);<br>
	 * 5.openWindow(title,icon,url,width,height,callback, tools);<br>
	 * tools格式[icon:{},title:"",handler:function(){}];<br>
	 * icon格式{name:'&#xe60c;',color:'red'}
	 */
	function openWindow(arg1, arg2, arg3, arg4, arg5, arg6, arg7) {
		// 参数处理，形成重载
		var title = "业务窗口";
		var url;
		var icon = null;
		var width;
		var height;
		var callback;
		var tools = [];
		if (arguments.length == 3) {
			title = arg1;
			url = arg2;
			var typeconf = getDefalutWindowWHByType(arg3);
			width = typeconf.width;
			height = typeconf.height;
		} else if (arguments.length == 4) {
			title = arg1;
			url = arg2;
			callback = arg4;
			var typeconf = getDefalutWindowWHByType(arg3);
			width = typeconf.width;
			height = typeconf.height;
		} else if (arguments.length == 5) {
			title = arg1;
			icon = arg2;
			url = arg3;
			callback = arg5;
			var typeconf = getDefalutWindowWHByType(arg4);
			width = typeconf.width;
			height = typeconf.height;
		} else if (arguments.length == 6) {
			title = arg1;
			icon = arg2;
			url = arg3;
			width = arg4;
			height = arg5;
			callback = arg6;
		} else if (arguments.length == 7) {
			title = arg1;
			icon = arg2;
			url = arg3;
			width = arg4;
			height = arg5;
			callback = arg6;
			tools = arg7;
		}

		// 进行高度和宽度的自修正，嵌套的情况
		var parentH = $(window).height() - 2;
		var parentW = $(window).width() - 2;
		if (width > parentW) {
			width = parentW;
		}
		if (height > parentH) {
			height = parentH;
		}

		// 处理标题
		if (!chkObjNull(icon)) {
			title = "<i class=\"layui-icon\" style=\"color: " + icon.color + ";margin-right:3px;\">" + icon.name + "</i> " + title;
		}
		var urlStr = null;
		if ("string" == typeof url) {
			urlStr = url;
		} else {
			urlStr = "shinetag.do?method=fwdPageFrame&r=" + randomString(16);
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
		var layerIndex = null;
		// 打开窗口
		layui.use("layer",
		function() {
			var layer = layui.layer;
			layer.ready(function() {
				layerIndex = layer.open({
					type: 2,
					title: title,
					shadeClose: false,
					shade: 0.03,
					maxmin: true,
					scrollbar: false,
					// 开启最大化最小化按钮
					area: [width + "px", height + "px"],
					anim: Math.floor(Math.random() * 3),
					content: urlStr,
					end: function() {
						var data = $("body").data("layer_data_" + layerIndex);
						$("body").removeData("layer_data_" + layerIndex);
						if (!chkObjNull(callback)) {
							callback(data);
						}
					},
					success: function(dom, i) {
						for (var i = tools.length - 1; i >= 0; i--) { // 工具栏
							var oneTool = tools[i];
							var toolIcon = oneTool.icon;
							var toolTitle = oneTool.title;
							var toolHandler = oneTool.handler;
							var objTool = $("<a href=\"javascript:void(0);\" title=\"" + toolTitle + "\"><i class=\"layui-icon\" style=\"color:" + toolIcon.color + ";\">" + toolIcon.name + "</i></a>");
							objTool.on("click", toolHandler);
							$(dom).find(".layui-layer-setwin").first().prepend(objTool);
						}

						// 调试模式信息
						if (GlobalVars.DEBUG_MODE) {
							var objTool = $("<a href=\"javascript:void(0);\" title=\"调试信息\"><i class=\"layui-icon\" style=\"color:#393D49;\">&#xe64c;</i></a>");
							objTool.on("click",
							function() {
								if ("string" == typeof url) {
									MsgBoxUtil.alert("请求路径：<br>" + url, -1);
								} else {
									var jspPath = $(dom).find(".layui-layer-content").find("iframe")[0].contentWindow.getJspPath();
									var jsonStr = JSON.stringify(url.getParas(), null, "\t");
									var jsonTipStr = "";
									if (jsonStr.length > 1000) {
										jsonStr = jsonStr.substr(0, 900) + "...";
										console.log("详情请求数据：");
										console.log(url.getParas());
										jsonTipStr = "<span style=\"color:red;\">【请求数据量较大，请按F12查看详细数据信息】</span>";
									}
									var debugInfo = "请求路径：<br>" + url.getURLString() + "<br>";
									debugInfo = debugInfo + "请求页面：<br>" + jspPath + "<br>";
									debugInfo = debugInfo + "请求数据：<br><pre class=\"codepre\">" + syntaxHighlight(jsonStr) + "</pre>";
									debugInfo = debugInfo + jsonTipStr;
									MsgBoxUtil.alert(debugInfo, -1);;

								}
							});
							$(dom).find(".layui-layer-setwin").first().prepend(objTool);
						}
					}
				});
			});
		});
		// 将参数放到body中
		if ("string" != typeof url) {
			$("body").data("layer_data_in_" + layerIndex, url);
		}
		return layerIndex;
	};

	/**
	 * 顶层窗口打开
	 */
	function openTopWindow(arg1, arg2, arg3, arg4, arg5, arg6, arg7) {
		try {
			// 有些浏览器进行顶层div弹窗拦截，对于这种情况，使用嵌入方式打开，如果设置cookie
			if ("1" == CookieUtil.get("sys_top_win_open_type")) { // 使用嵌入方式打开
				throw new Error("Cookie设置嵌入方式打开");
			}
			// 先使用顶层打开，报出跨域问题后，使用嵌入的window打开
			if (arguments.length == 3) {
				return top.window.WinUtil.open(arg1, arg2, arg3);
			} else if (arguments.length == 4) {
				return top.window.WinUtil.open(arg1, arg2, arg3, arg4);
			} else if (arguments.length == 5) {
				return top.window.WinUtil.open(arg1, arg2, arg3, arg4, arg5);
			} else if (arguments.length == 6) {
				return top.window.WinUtil.open(arg1, arg2, arg3, arg4, arg5, arg6);
			} else if (arguments.length == 7) {
				return top.window.WinUtil.open(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
			}
		} catch(oE) {
			console.log("顶层窗口无法打开：" + oE.message);
			if (arguments.length == 3) {
				return openWindow(arg1, arg2, arg3);
			} else if (arguments.length == 4) {
				return openWindow(arg1, arg2, arg3, arg4);
			} else if (arguments.length == 5) {
				return openWindow(arg1, arg2, arg3, arg4, arg5);
			} else if (arguments.length == 6) {
				return openWindow(arg1, arg2, arg3, arg4, arg5, arg6);
			} else if (arguments.length == 7) {
				return openWindow(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
			}
		}
	};

	/**
	 * 关闭窗口
	 */
	function closeWindow(data) {
		var index = getIndex(); // 先得到当前iframe层的索引
		parent.$("body").data("layer_data_" + index, data);
		parent.layer.close(index); // 再执行关闭
	};

	/**
	 * 最大化(如果传递了index则对index的对象操作，否则认为窗体本身)
	 */
	function fullWindow(index) {
		if (chkObjNull(index)) {
			var index = getIndex();
			parent.layer.full(index);
		} else {
			layer.full(index);
		}
	};

	/**
	 * 最小化(如果传递了index则对index的对象操作，否则认为窗体本身)
	 */
	function minWindow(index) {
		if (chkObjNull(index)) {
			var index = getIndex();
			parent.layer.min(index);
		} else {
			layer.full(index);
		}
	};

	/**
	 * 重置窗口大小(如果传递了index则对index的对象操作，否则认为窗体本身)
	 */
	function restoreWindow(index) {
		if (chkObjNull(index)) {
			var index = getIndex();
			parent.layer.restore(index);
		} else {
			layer.full(index);
		}
	};

	/**
	 * 获取当前窗口的索引
	 * 
	 * @return
	 */
	function getIndex() {
		var index = parent.layer.getFrameIndex(win.name);
		return index;
	};

	// 执行窗口大小参数初始化--加载完成后执行
	$(function() {
		setWindowTypeSizeByScreenSize();
	});

	// 开放的工具方法
	var WinUtil = {
		open: openWindow,
		// 打开
		openTop: openTopWindow,
		// 置顶打开
		close: closeWindow,
		// 关闭
		full: fullWindow,
		// 最大化
		min: minWindow,
		// 最小化
		restore: restoreWindow,
		// 重置
		getIndex: getIndex // 获取索引
	};

	// 将工具方法放出去
	win.WinUtil = WinUtil;
})(window);

// 输出版权信息
(function(win, undefined) {
	function logCopyRight() {
		console.log("Join us");
		console.log("加入我们，请将简历发送至 hr@nomax.top(邮件标题请以“姓名-应聘XX职位”命名)");
		console.log("copyRights@诺码信科技(nomax.cn)");
	};
	var Util = {
		print: logCopyRight
	};

	win.CopyRightUtil = Util;
})(window);

// URL工具类支持
(function(win, undefined) {
	/**
	 * 对于请求路径的包装其中包含了请求的地址和数据-可以直接是字符串路径，也可以2个参数，controller+metho
	 * 
	 * @author yjc
	 */
	function URL(urlstr, method) {
		if (arguments.length == 1) {
			this.urlStr = urlstr;
		} else if (arguments.length == 2) {
			this.urlStr = urlstr + "?method=" + method; // 请求路径
		} else {
			alert("传入的参数个数不正确");
			throw new Error("传入的参数个数不正确");
		}
		this.paras = new HashMap(); // 数据
	};

	/**
	 * 获取请求路径
	 */
	URL.prototype.getURLString = function() {
		return this.urlStr;
	};

	/**
	 * 获取请求路径-携带参数数据的。
	 */
	URL.prototype.getRealURLString = function() {
		var parasStr = "";
		var keys = this.paras.keySet();
		for (var i = 0,
		n = keys.length; i < n; i++) {
			parasStr = parasStr + "&" + keys[i] + "=" + this.paras.get(keys[i]);
		}
		return this.urlStr + parasStr;
	};

	URL.prototype.getParas = function() {
		return this.paras.values;
	};

	URL.prototype.addPara = function(key, value) {
		if ("undefined" == typeof value) {
			return this.paras.put(key, getObject(key).getValue());
		} else {
			return this.paras.put(key, value);
		}
	};

	URL.prototype.addMap = function(map) {
		this.paras.combine(map);
	};

	/**
	 * 增加form
	 * 
	 * @param form
	 * @return
	 */
	URL.prototype.addForm = function(form) {
		this.addMap(getObject(form).getMapData()); // 增加form参数
	};

	// 接口公布出去
	win.URL = URL;
})(window);

/**
 * 对于后台的请求封装的操作工具类，依赖与jquery
 * 
 * @author yjc
 */
var AjaxUtil = {
	// 同步发送请求
	syncRequest: function(url) {
		var responseText = false;
		$.ajax({
			type: "post",
			async: false,
			url: url.getURLString(),
			data: url.getParas(),
			dataType: "text",
			success: function(data) {
				responseText = data;
			},
			complete: function(xhr, status) {
				if ("success" != status) {
					MsgBoxUtil.alert("请求出错：[" + status + "]" + xhr.status + ":" + xhr.statusText);
				}
			}
		});
		return responseText;
	},

	// 异步发送请求
	asyncRequest: function(url, callback) {
		$.ajax({
			type: "post",
			async: true,
			url: url.getURLString(),
			data: url.getParas(),
			dataType: "text",
			success: function(responseText) {
				if ("undefined" != typeof callback && null != callback && "" != callback) {
					callback(responseText);
				}
			},
			complete: function(xhr, status) {
				if ("success" != status) {
					MsgBoxUtil.alert("请求出错：[" + status + "]" + xhr.status + ":" + xhr.statusText);
				}
			}
		});
	},

	// 同步发送请求-业务请求，默认加载上数据jbjgid和jbjgqxfw-自动反馈结果的验证
	syncBizRequest: function(url, callback) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		var data = AjaxUtil.syncRequest(url);
		if (AjaxUtil.checkException(data)) {
			AjaxUtil.showException(data);
			return false;
		}

		if ("undefined" != typeof callback && null != callback && "" != callback) {
			callback(data);
		}

		// 如果返回的数据为空，但是没有异常的则返回true
		if (chkObjNull(data)) {
			return true;
		}
		return data;
	},

	// 在syncBizRequest调用后执行，判断是否发生异常，是否继续运行。
	checkIsGoOn: function(data) {
		if (typeof(data) == "boolean") {
			if (data) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	},

	// 异步发送请求-业务请求，默认加载上数据jbjgid和jbjgqxfw-自动反馈结果的验证
	asyncBizRequest: function(url, callback) {
		url.addPara("__jbjgid", getJbjgid("00000000"));
		url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
		url.addPara("__yhid", getPageYhid());
		AjaxUtil.asyncRequest(url,
		function(data) {
			if (AjaxUtil.checkException(data)) {
				AjaxUtil.showException(data);
				return false;
			}
			if ("undefined" != typeof callback && null != callback && "" != callback) {
				callback(data);
			}
		});
	},

	// 检查请求结果是发生了异常
	checkException: function(responseText) {
		if ("string" == typeof responseText) { // 解决火狐浏览器下的相关问题。-yjc.2015年8月28日
			if (responseText.indexOf("<!--\r\n//errmsgsign_20150603_grace.easyFrame\r\n-->") >= 0) {
				return true; // 存在异常
			} else {
				return false; // 无异常
			}
		} else {
			return false;
		}
	},

	// 处理异常信息
	showException: function(reponseText) {
		if ($("#sys_errmsg_con_div").length <= 0) {
			$("body").append("<div id=\"sys_errmsg_con_div\"></div>");
		}
		$("#sys_errmsg_con_div").html(reponseText);
	}
};