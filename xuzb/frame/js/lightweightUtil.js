/**
 * 涉及到的util的js类有。
 * <br>
 * HashMap/List/URL/AjaxUtil/commonJsUtil/CookieUtil/NumberUtil/StringExtend/DateUtil
 * <br>
 * 
 * 该js并不直接使用，而是经过压缩后使用.min.js文件
 */

/**
 * 工具方法的轻量级封装，对于只使用后台业务操作可可以只引入该js-完成轻量级的业务请求操作
 */
/**
 * 用来存储hashMap对象
 * 
 * @author yjc
 */
// 创建一个Map
function HashMap(jsondata) {
	if ("string" == typeof jsondata) {
		this.values = eval("(" + jsondata + ")");
	} else if ("object" == typeof jsondata) {
		this.values = jsondata;
	} else if ("undefined" != typeof jsondata) {
		throw new Error("对于HashMap的初始化数据jsondata只允许为json字符串和{}格式的数据对象");
	} else {
		this.values = {}; // 可以做json对象
	}
};

// 清空一个map
HashMap.prototype.clear = function() {
	this.values = {};
};

// 判断是否存在关键字
HashMap.prototype.containsKey = function(key) {
	return typeof(this.values[key]) != "undefined";
};

// 判断是否存在值
HashMap.prototype.containsValue = function(value) {
	for (var key in this.values) {
		var val = this.values[key];
		if (val == value) {
			return true;
		}
	}
	return false;
};

// 返回一个数值
HashMap.prototype.get = function(key) {
	if (this.containsKey(key)) {
		return this.values[key];
	} else {
		return null;
	}
};

// 查看是否为空
HashMap.prototype.isEmpty = function() {
	return ! this.size();
};

// 返回key的数组
HashMap.prototype.keySet = function() {
	var result = [];
	for (var key in this.values) {
		result.push(key);
	}
	return result;
};

// 放入一个值
HashMap.prototype.put = function(key, value) {
	this.values[key] = value;
	return value;
};

// 合并一个Map
HashMap.prototype.combine = function(map) {
	var values = map.values;
	for (var key in values) {
		var value = values[key];
		this.values[key] = value;
	}
};

// 移除一个值
HashMap.prototype.remove = function(key) {
	var value = this.containsKey(key) ? this.get(key) : undefined;
	if (value || (typeof(value) == "number" && value == 0)) {
		delete this.values[key];
	}
	return value;
};

// 获取hashMap的值
HashMap.prototype.size = function() {
	var count = 0;
	for (var key in this.values) {
		count = count + 1;
	}
	return count;
};

/**
 * 用来存储List对象
 * 
 * @author yjc
 */
// 创建list对象
function List(jsondata) {
	if ("string" == typeof jsondata) {
		this.values = eval("(" + jsondata + ")");
	} else if ("object" == typeof jsondata) {
		this.values = jsondata;
	} else if ("undefined" != typeof jsondata) {
		throw new Error("对于List的初始化数据jsondata只允许为json字符串和{}格式的数据对象");
	} else {
		this.values = []; // 可以做json对象
	}
};

// 数据情况
List.prototype.clear = function() {
	this.values = [];
};

// 大小
List.prototype.size = function() {
	return this.values.length;
};

// 增加一行数据,返回总行数
List.prototype.add = function(data) {
	return this.values.push(data);
};

// 返回一行数据
List.prototype.get = function(i) {
	return this.values[i];
};

// 合并
List.prototype.combine = function(list) {
	var values = list.values;
	for (var i = 0,
	n = values.length; i < n; i++) {
		this.add(values[i]);
	}
};

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

/**
 * 获取pdf打印的url地址
 */
URL.prototype.getPdfURLString = function() {
	var pdfviewer = "./frame/plugins/pdfjs/web/viewer.html";
	var urlStr = GlobalVars.BASE_PATH + this.getRealURLString();
	var pdfviewHref = pdfviewer + "?rd=" + randomString(6) + "&file=" + encodeURIComponent(urlStr);
	return pdfviewHref;
};

URL.prototype.getParas = function() {
	return this.paras.values;
};

URL.prototype.addPara = function(key, value) {
	if ("undefined" == typeof value) {
		return false;
	} else {
		return this.paras.put(key, value);
	}
};

URL.prototype.addMap = function(map) {
	this.paras.combine(map);
};

/**
 * 增加所有的grid的数据
 * 
 * @return
 */
URL.prototype.addListData = function(key, data, colNames) {
	if (chkObjNull(key)) {
		alert("传入的关键字为空");
		return false;
	}
	if (chkObjNull(colNames)) {
		this.addPara("__grid_" + key, JSON.stringify({
			griddata: data
		}));
		return true;
	}

	// 新数据
	var colArr = colNames.split(",");
	var dataNew = [];
	for (var i = 0,
	n = data.length; i < n; i++) {
		var rowJson = {};
		for (var j = 0,
		m = colArr.length; j < m; j++) {
			var colName = colArr[j];
			var rowDataJson = data[i];
			rowJson[colName] = rowDataJson[colName];
		}
		dataNew.push(rowJson);
	}
	this.addPara("__grid_" + key, JSON.stringify({
		griddata: dataNew
	}));

	return true;
};

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
					alert("请求出错：[" + status + "]" + xhr.status + ":" + xhr.statusText);
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
					alert("请求出错：[" + status + "]" + xhr.status + ":" + xhr.statusText);
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

/**
 * 将一些通用的小的js函数放到该js文件中，对于一整套js逻辑建议单独放到一个文件中进行管理-依赖于jquery
 * 
 * @author yjc
 */
// 关闭浏览器页面，不给弹窗提示
function closeBrowser() {
	if ($.browser.msie) { // ie浏览器
		window.opener = null;
		window.open("", "_self"); // 强制消除关闭提示
		window.close();
	} else { // 其他浏览器转向空白页
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

// 获取页面YHID
function getPageYhid() {
	if ($("#__yhid").length <= 0) {
		return "";
	}
	return $("#__yhid").val();
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
function randomString(len) {　　len = len || 32;　　
	var chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';　　
	var maxPos = chars.length;　　
	var str = '';　　
	for (var i = 0; i < len; i++) {　　　　str += chars.charAt(Math.floor(Math.random() * maxPos));　　
	}　　
	return str;
};

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
 * 系统统一管理的文件下载-mod.yjc.2016年6月24日-兼容模式下载
 */
function downloadSysFile(wjbs) {
	if (chkObjNull(wjbs)) {
		alert("文件唯一标识为空");
		return;
	}
	var url = new URL("taglib.do", "downloadSysFile");
	url.addPara("wjbs", wjbs);
	downloadFile2Form(url);
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
 * 判断是否为IE11浏览器的方法
 * 
 * @return
 */
function isIE11() {
	if ( !! window.ActiveXObject || "ActiveXObject" in window) {
		return true;
	} else {
		return false;
	}
};

/*******************************************************************************
 * 对于cookie的操作的封装，依赖与jquery
 * 
 * @author yjc
 */
var CookieUtil = {
	// 设置cookie，optins设置cookie的参数，可以设置有效时间，单位天,
	// 使用方法例如：CookieUtil.set('name','lucy',1);//设置name=lucy有效期一天
	set: function(name, value, days) {
		var options = {
			expires: 365
		}; // 默认cookie的有效期为一年
		if ("undefined" != typeof days && null != days && "" != days) {
			options = {
				expires: days
			};
		}
		if (value === null) {
			value = '';
			options.expires = -1;
		}
		var expires = '';
		if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
			var date;
			if (typeof options.expires == 'number') {
				date = new Date();
				date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
			} else {
				date = options.expires;
			}
			expires = '; expires=' + date.toUTCString();
		}
		var path = options.path ? '; path=' + options.path: '';
		var domain = options.domain ? '; domain=' + options.domain: '';
		var secure = options.secure ? '; secure': '';
		document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
	},
	// 获取cookie
	get: function(name) {
		var cookieValue = null;
		if (document.cookie && document.cookie != '') {
			var cookies = document.cookie.split(';');
			for (var i = 0; i < cookies.length; i++) {
				var cookie = jQuery.trim(cookies[i]);
				if (cookie.substring(0, name.length + 1) == (name + '=')) {
					cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
					break;
				}
			}
		}
		return cookieValue;
	}
};

/**
 * date的操作工具类
 * 
 * @author yjc
 */
var DateUtil = {
	getDateString: function(date, format) {
		var yearString = date.getFullYear().toString();
		var monthString = (date.getMonth() + 1).toString();
		var dayString = date.getDate().toString();
		var hourString = date.getHours().toString();
		var minuteString = date.getMinutes().toString();
		var secondString = date.getSeconds().toString();

		var result = format;
		if (yearString.length < 4) {
			yearString = this.getZero(4 - yearString.length) + yearString;
		}
		result = this.replaceString(result, "yyyy", yearString);
		if (monthString.length < 2) {
			monthString = this.getZero(2 - monthString.length) + monthString;
		}
		result = this.replaceString(result, "MM", monthString);
		if (dayString.length < 2) {
			dayString = this.getZero(2 - dayString.length) + dayString;
		}
		result = this.replaceString(result, "dd", dayString);
		if (hourString.length < 2) {
			hourString = this.getZero(2 - hourString.length) + hourString;
		}
		result = this.replaceString(result, "hh", hourString);
		if (minuteString.length < 2) {
			minuteString = this.getZero(2 - minuteString.length) + minuteString;
		}
		result = this.replaceString(result, "mm", minuteString);
		if (secondString.length < 2) {
			secondString = this.getZero(2 - secondString.length) + secondString;
		}
		result = this.replaceString(result, "ss", secondString);
		return result;
	},
	// 检查日期是否合法
	checkDate: function(str, formatString) {
		var yearIndex = formatString.search("yyyy");
		var monthIndex = formatString.search("MM");
		var dayIndex = formatString.search("dd");
		var hourIndex = formatString.search("hh");
		var minuteIndex = formatString.search("mm");
		var secondIndex = formatString.search("ss");

		if (str == "") {
			return true;
		}
		if (str == null || str.length <= 0) {
			alert("字符串跟期望的输入值长度不匹配,期望输入值应该符合：" + formatString);
			return false;
		}
		if (str.length != formatString.length) {
			alert("字符串跟期望的输入值长度不匹配,期望输入值应该符合：" + formatString);
			return false;
		}

		for (var i = 0; i < formatString.length; i++) {
			var temp = formatString.substr(i, 1);
			if (temp == "y" || temp == "M" || temp == "d" || temp == "h" || temp == "m" || temp == "s") {
				if ((str.substr(i, 1) < "0" || str.substr(i, 1) > "9")) {
					alert("字符串中存在非法字符,期望输入值应该符合：" + formatString);
					return false;
				}
			}
			if (temp == "-" || temp == ":" || temp == " ") {
				if (str.substr(i, 1) != temp) {
					alert("字符串中存在非法字符,期望输入值应该符合：" + formatString);
					return false;
				}
			}
		}

		var allZero = true;
		for (var i = 0; i < str.length; i++) {
			var temp = str.substr(i, 1);
			if (temp != "0" && temp != "-" && temp != ":" && temp != " ") {
				allZero = false;
			}
		}
		if (allZero) {
			return false;
		}

		if (yearIndex >= 0) {
			var year = new Number(str.substr(yearIndex, 4));
			if (isNaN(year) || year == 0 || year < DateTimeMaskConfig.defaultLeastYear || year > DateTimeMaskConfig.defaultMaxYear) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		if (monthIndex >= 0) {
			var month = new Number(str.substr(monthIndex, 2));
			if (isNaN(month) || month <= 0 || month > 12) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		if (dayIndex >= 0) {
			var day = new Number(str.substr(dayIndex, 2));
			if (isNaN(day) || day <= 0 || day > 31) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		// 增加对每月日期合法性的判断 2005-03-21
		if (day > 28 && day < 31) {
			if (month == 2) {
				if (day != 29) {
					alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString + "。详细为：" + year + "年" + month + "月无" + day + "日");
					return false;
				} else {
					if ((year % 4) != 0) {
						alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString + "。详细为：" + year + "年" + month + "月无" + day + "日");
						return false;
					} else {
						if ((year % 100 == 0) && (year % 400 != 0)) {
							alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString + "。详细为：" + year + "年" + month + "月无" + day + "日");
							return false;
						}
					}
				}
			}
		} else if (day == 31) {
			if ((month == 2) || (month == 4) || (month == 6) || (month == 9) || (month == 11)) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString + "。详细为：" + month + "月无" + day + "日");
				return false;
			}
		}
		if (hourIndex >= 0) {
			var hour = new Number(str.substr(hourIndex, 2));
			if (isNaN(hour) || hour < 0 || hour > 23) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		if (minuteIndex >= 0) {
			var minute = new Number(str.substr(minuteIndex, 2));
			if (isNaN(minute) || minute < 0 || minute > 59) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		if (secondIndex >= 0) {
			var second = new Number(str.substr(secondIndex, 2));
			if (isNaN(second) || second < 0 || second > 59) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		return true;
	},

	// 比较两个时间之间的月份数,在比较之前需要开发人员自己来验证时间串的合法性
	getMonthDifferenceBetweenTwoDate: function(startTime, smask, endTime, emask) {
		var sobj = new DateObj(startTime, smask);
		var eobj = new DateObj(endTime, emask);
		return sobj.compareMonthDiff(eobj);
	},
	// 比较两个时间,在比较之前需要开发人员自己来验证时间串的合法性；返回值：-1，0，1
	compareDate: function(startTime, smask, endTime, emask) {
		var sobj = new DateObj(startTime, smask);
		var eobj = new DateObj(endTime, emask);
		return sobj.compareTo(eobj);
	},
	// 将一个value从旧mask的形式，转成新mask的形式，返回值是String。
	changeFormat: function(dataTime, oldMask, newMask) {
		var dateObj = new DateObj(dataTime, oldMask);
		return DateUtil.getDateString(dateObj.getDate(), newMask);
	},
	getObjValue: function(obj) {
		var value = new DateObj(obj.value, obj.mask);
		return value.getDate();
	},
	// 获得指定长度的零串.
	getZero: function(zeroLength) {
		if (zeroLength == null || zeroLength == 0) {
			return "";
		}
		var temp = "";
		for (var i = 0; i < zeroLength; i++) {
			temp += "0";
		}
		return temp;
	},
	replaceString: function(objString, sourceString, reString) {
		if (objString == null || objString.length == 0 || sourceString == null || sourceString.length == 0 || objString.search(sourceString) < 0) {
			return objString;
		}
		var frontStr = objString.substr(0, objString.search(sourceString));
		var lastStr = objString.substr(objString.search(sourceString) + sourceString.length);

		return (frontStr || "") + (reString || "") + (lastStr || "");
	},

	getShowValue: function(obj, dateTimeString) {
		if (dateTimeString == "") {
			return "";
		}
		if (!this.checkDate(dateTimeString, obj.mask)) { // 必须校验是否是有效的日期值，否则的话得到的日志值跟真正的不一样
			return "";
		}
		var result = obj.maskString;
		var yearString = "";
		var monthString = "";
		var dayString = "";
		var hourString = "";
		var minuteString = "";
		var secondString = "";

		if (obj.yearIndex >= 0) {
			var year = new Number(dateTimeString.substr(obj.yearIndex, 4));
			if (isNaN(year) || year < DateTimeMaskConfig.defaultLeastYear) {
				yearString = DateTimeMaskConfig.defaultLeastYear.toString();
			} else if (year > DateTimeMaskConfig.defaultMaxYear) {
				yearString = DateTimeMaskConfig.defaultMaxYear.toString();
			} else {
				yearString = year.toString();
			}

			if (yearString.length < 4) {
				yearString = this.getZero(4 - yearString.length) + yearString;
			}
			result = this.replaceString(result, "yyyy", yearString);
		}

		if (obj.monthIndex >= 0) {
			var month = new Number(dateTimeString.substr(obj.monthIndex, 2));

			if (isNaN(month) || month > 12 || month <= 0) {
				if (yearString != "0000") {
					monthString = "01";
				} else {
					monthString = "00";
				}
			} else {
				monthString = month.toString();
			}
			if (monthString.length < 2) {
				monthString = this.getZero(2 - monthString.length) + monthString;
			}
			result = this.replaceString(result, "MM", monthString);
		}
		if (obj.dayIndex >= 0) {
			var day = new Number(dateTimeString.substr(obj.dayIndex, 2));

			if (isNaN(day) || day > 31 || day <= 0) {
				if (yearString != "0000" && monthString != "00") {
					dayString = "01";
				} else {
					dayString = "00";
				}
			} else {
				dayString = day.toString();
			}
			if (dayString.length < 2) {
				dayString = this.getZero(2 - dayString.length) + dayString;
			}
			result = this.replaceString(result, "dd", dayString);
		}
		if (obj.hourIndex >= 0) {
			var hour = new Number(dateTimeString.substr(obj.hourIndex, 2));

			if (isNaN(hour) || hour > 23 || hour <= 0) {
				hourString = "00";
			} else {
				hourString = hour.toString();
			}
			if (hourString.length < 2) {
				hourString = this.getZero(2 - hourString.length) + hourString;
			}
			result = this.replaceString(result, "hh", hourString);
		}
		if (obj.minuteIndex >= 0) {
			var minute = new Number(dateTimeString.substr(obj.minuteIndex, 2));

			if (isNaN(minute) || minute > 59 || minute <= 0) {
				minuteString = "00";
			} else {
				minuteString = minute.toString();
			}
			if (minuteString.length < 2) {
				minuteString = this.getZero(2 - minuteString.length) + minuteString;
			}
			result = this.replaceString(result, "mm", minuteString);
		}
		if (obj.secondIndex >= 0) {
			var second = new Number(dateTimeString.substr(obj.secondIndex, 2));
			if (isNaN(second) || second > 59 || second <= 0) {
				secondString = "00";
			} else {
				secondString = second.toString();
			}
			if (secondString.length < 2) {
				secondString = this.getZero(2 - secondString.length) + secondString;
			}
			result = this.replaceString(result, "ss", secondString);
		}
		return result;
	},
	getShowValueByMask: function(mask, value) {
		if (value == "") {
			return "";
		}
		if (!this.checkDate(value, mask)) { // 必须校验是否是有效的日期值，否则的话得到的日志值跟真正的不一样
			return "";
		}
		var result = mask;
		var yearString = "";
		var monthString = "";
		var dayString = "";
		var hourString = "";
		var minuteString = "";
		var secondString = "";

		var yearIndex = mask.search("yyyy");
		var monthIndex = mask.search("MM");
		var dayIndex = mask.search("dd");
		var hourIndex = mask.search("hh");
		var minuteIndex = mask.search("mm");
		var secondIndex = mask.search("ss");

		if (yearIndex >= 0) {
			var year = new Number(value.substr(yearIndex, 4));
			if (isNaN(year) || year < DateTimeMaskConfig.defaultLeastYear) {
				yearString = DateTimeMaskConfig.defaultLeastYear.toString();
			} else if (year > DateTimeMaskConfig.defaultMaxYear) {
				yearString = DateTimeMaskConfig.defaultMaxYear.toString();
			} else {
				yearString = year.toString();
			}

			if (yearString.length < 4) {
				yearString = this.getZero(4 - yearString.length) + yearString;
			}
			result = this.replaceString(result, "yyyy", yearString);
		}

		if (monthIndex >= 0) {
			var month = new Number(value.substr(monthIndex, 2));

			if (isNaN(month) || month > 12 || month <= 0) {
				if (yearString != "0000") {
					monthString = "01";
				} else {
					monthString = "00";
				}
			} else {
				monthString = month.toString();
			}
			if (monthString.length < 2) {
				monthString = this.getZero(2 - monthString.length) + monthString;
			}
			result = this.replaceString(result, "MM", monthString);
		}
		if (dayIndex >= 0) {
			var day = new Number(value.substr(dayIndex, 2));

			if (isNaN(day) || day > 31 || day <= 0) {
				if (yearString != "0000" && monthString != "00") {
					dayString = "01";
				} else {
					dayString = "00";
				}
			} else {
				dayString = day.toString();
			}
			if (dayString.length < 2) {
				dayString = this.getZero(2 - dayString.length) + dayString;
			}
			result = this.replaceString(result, "dd", dayString);
		}
		if (hourIndex >= 0) {
			var hour = new Number(value.substr(hourIndex, 2));

			if (isNaN(hour) || hour > 23 || hour <= 0) {
				hourString = "00";
			} else {
				hourString = hour.toString();
			}
			if (hourString.length < 2) {
				hourString = this.getZero(2 - hourString.length) + hourString;
			}
			result = this.replaceString(result, "hh", hourString);
		}
		if (minuteIndex >= 0) {
			var minute = new Number(value.substr(minuteIndex, 2));

			if (isNaN(minute) || minute > 59 || minute <= 0) {
				minuteString = "00";
			} else {
				minuteString = minute.toString();
			}
			if (minuteString.length < 2) {
				minuteString = this.getZero(2 - minuteString.length) + minuteString;
			}
			result = this.replaceString(result, "mm", minuteString);
		}
		if (secondIndex >= 0) {
			var second = new Number(value.substr(secondIndex, 2));
			if (isNaN(second) || second > 59 || second <= 0) {
				secondString = "00";
			} else {
				secondString = second.toString();
			}
			if (secondString.length < 2) {
				secondString = this.getZero(2 - secondString.length) + secondString;
			}
			result = this.replaceString(result, "ss", secondString);
		}
		return result;
	}
};

/**
 * 本文件主要用于对String的属性进行扩展
 * 
 * @author yjc
 */

/**
 * 字符串去除空格的函数针对于String 类型有效
 */
String.prototype.trim = function() {
	return this.replace(/(^[\s]*)|([\s]*$)/g, "");
};

/** 去除左边空格 */
String.prototype.lTrim = function() {
	return this.replace(/(^[\s]*)/g, "");
};

/** 去除右边空格 */
String.prototype.rTrim = function() {
	return this.replace(/([\s]*$)/g, "");
};

/**
 * 判断str结束
 */
String.prototype.endWith = function(str) {
	if (this == null || str == null) {
		return false;
	}
	if (this.length < str.length) {
		return false;
	} else if (this == str) {
		return true;
	} else if (this.substring(this.length - str.length) == str) {
		return true;
	}
	return false;
};

/**
 * 判断是否以str开始
 */
String.prototype.startWith = function(str) {
	if (this == null || str == null) {
		return false;
	}
	if (this.length < str.length) {
		return false;
	} else if (this == str) {
		return true;
	} else if (this.substr(0, str.length) == str) {
		return true;
	}
	return false;
};

/**
 * 包含的判断
 * 
 * @param str
 * @return
 */
String.prototype.contains = function(str) {
	if (this == null) {
		return false;
	}
	if (str == null) {
		return true;
	}

	if (this.length < str.length) {
		return false;
	} else if (this == str) {
		return true;
	} else if (this.indexOf(str) != -1) {
		return true;
	}
	return false;
};

/**
 * 新增类，用来处理数字之间的状态转换。
 */
var NumberUtil = {
	// 获取四舍五入的值-以解决四舍五入精度问题
	round: function(value, how) {
		var num = new Number(value);
		var num1 = new Number(num.toFixed(10));
		return parseFloat(num1.toFixed(how));
	},

	// 根据带%号的值，获取实际的值。返回String类型的值。
	// 这个方法只是获取带%的数字，但是没有去掉分隔符。所以如果使用要跟getValue一起使用。
	getValueWithSuffix: function(value) {
		if (value == "") {
			alert("输入参数【" + value + "】中不存在%,请检查");
			return;
		}
		value = value.substr(0, value.length - 1);

		if (value.indexOf(".") > 2) {
			value = value.substring(0, value.indexOf(".") - 2) + "." + value.substring(value.indexOf(".") - 2, value.indexOf(".")) + value.substr(value.indexOf(".") + 1);
		} else if (value.indexOf(".") == 2) {
			value = "0." + value.substring(0, 2) + value.substr(3);
		} else if (value.indexOf(".") == 1) {
			value = "0.0" + value.substring(0, 1) + value.substr(2);
		} else if (value.indexOf(".") == 0) {
			value = "0.00" + value.substr(1);
		} else if (value.indexOf(".") == -1) {
			if (value.length > 2) {
				value = value.substr(0, value.length - 2) + "." + value.substr(value.length - 2);
			} else if (value.length == 2) {
				value = "0." + value.substr(0);
			} else {
				value = "0.0" + value.substr(0);
			}
		}
		return value;
	},

	// 根据带,的值获取真实的值，返回String类型的值。value是不带%的值。
	// 获取String类型的去掉分隔符以后的值。
	getValue: function(value, formatString) {
		if (value == "") {
			return "0";
		}

		var integerMaskString = "";
		var SEPERATE_CHAR = "";

		if (formatString.indexOf(".") < 0) {
			integerMaskString = formatString;
		} else {
			integerMaskString = formatString.substr(0, formatString.indexOf("."));
		}

		var imsLen = integerMaskString.length;
		for (var i = 0; i < imsLen; i++) {
			var temp = integerMaskString.substr(i, 1);
			if (temp == "#") {
				continue;
			} else if (temp == "0") {
				continue;
			} else {
				SEPERATE_CHAR = temp || ""; // 数字间的分割符号.
			}
		}
		if (value.indexOf(SEPERATE_CHAR) < 0) {
			return value;
		} else {
			var reg = new RegExp(SEPERATE_CHAR, "gi");
			return value.replace(reg, "");
		}
	},

	getRealValue: function(value, format) {
		if (!format) {
			format = "###################0.00";
		}
		if (value.substr(value.length - 1) == "%") {
			return parseFloat(NumberUtil.getValue(NumberUtil.getValueWithSuffix(value), format));
		} else {
			return parseFloat(NumberUtil.getValue(value, format));
		}
	},

	// 根据obj，对初始值进行加工：例如由1111.11222 ->11,111.222%
	getShowValueByMask: function(mask, value) {
		if (value == null || value == "") {
			value = "";
		}
		if (value.indexOf("%") > 0) { // 20100805:给批设置使用
			var vnumber = value.substr(0, value.length - 1);
			if (!isNaN(vnumber)) {
				value = parseFloat(vnumber) / 100;
				value = NumberUtil.getShowValueByMask(mask, value.toString());
			}
			return value;
		}
		if (value.indexOf(",") > 0) { // 20110105:给批设置使用
			return value;
		}
		var nLen = value.length;
		for (var i = 0; i < nLen; i++) {
			var temp = value.substr(i, 1);
			if ((temp < "0" || temp > "9") && temp != "." && temp != "-") {
				return "";
			}
		}
		if (mask.indexOf("%") > 0) {
			if (value != "") {
				value = Math.round((parseFloat(value) * 100) * 10000) / 10000;
				value = value.toString();
			}
		}
		// 处理正负号.
		var sign = "";
		if (value.substr(0, 1) == "-") {
			sign = "-";
			value = value.substr(1);
		}

		if (isNaN(value)) {
			return "";
		}
		var integerNumbers = "";
		var floatNumbers = "";
		if (value.indexOf(".") < 0) {
			integerNumbers = value;
		} else {
			var number = value.split(".");
			if (number.length == 1) {
				integerNumbers = number[0];
			} else if (number.length == 2) {
				integerNumbers = number[0];
				floatNumbers = number[1];
			} else {
				return value;
			}
		}
		while (integerNumbers.length > 1 && integerNumbers.substr(0, 1) == "0") {
			integerNumbers = integerNumbers.substr(1);
		}

		var MAX_FORMAT_LENGTH = 0;
		var FORMAT_LENGTH = 0;
		var FLOAT_LENGTH = 0;
		var SEPERATE_CHAR = "";
		var suffix = "";

		// formatString=mask;
		if (mask.substring(0, 1) == "+") {
			mask = mask.substring(1, mask.length);
		}
		if (mask.substring(mask.length - 1, mask.length) == "%") {
			suffix = "%";
			mask = mask.substring(0, mask.length - 1);
		}
		var integerMaskString = "";
		var floatMaskString = "";
		if (mask.indexOf(".") < 0) {
			integerMaskString = mask;
		} else {
			integerMaskString = mask.substr(0, mask.indexOf("."));
			floatMaskString = mask.substr(mask.indexOf(".") + 1);
		}
		if (integerMaskString == null) {
			integerMaskString = "";
		}
		if (floatMaskString == null) {
			floatMaskString = "";
		}

		var countOfZero = 0;
		var countOfExtra = 0;

		var imsLen = integerMaskString.length;
		for (var i = 0; i < imsLen; i++) {
			var temp = integerMaskString.substr(i, 1);
			if (temp == "#") {
				countOfExtra++;
				continue;
			} else if (temp == "0") {
				countOfZero++;
				continue;
			} else {
				SEPERATE_CHAR = temp || ""; // 数字间的分割符号.
			}
		}
		// 整数部分的格式长度.
		FORMAT_LENGTH = countOfZero;
		// 整数部分的最大长度.
		MAX_FORMAT_LENGTH = countOfZero + countOfExtra;

		if (SEPERATE_CHAR == null) {
			SEPERATE_CHAR = "";
		}
		countOfZero = 0;
		countOfExtra = 0;
		var fmsLen = floatMaskString.length;
		for (var i = 0; i < fmsLen; i++) {
			var temp = floatMaskString.substr(i, 1);
			if (temp == "#") {
				countOfExtra++;
				continue;
			} else if (temp == "0") {
				countOfZero++;
				continue;
			}
		}
		// 小数的长度.
		FLOAT_LENGTH = countOfExtra + countOfZero;

		var count = 0;
		if (integerNumbers.length > MAX_FORMAT_LENGTH) {
			// 如果数字超出显示范围，割掉前面多余的数字.
			integerNumbers = integerNumbers.substr(integerNumbers.length - MAX_FORMAT_LENGTH);
		}
		if (integerNumbers.length < FORMAT_LENGTH) {
			// 如果数字小于最小个数，那么追加
			count = FORMAT_LENGTH - integerNumbers.length;
			for (var i = 0; i < count; i++) {
				integerNumbers += "0";
			}
		}
		if (floatNumbers.length > FLOAT_LENGTH) {
			floatNumbers = floatNumbers.substr(0, FLOAT_LENGTH);
		} else {
			count = FLOAT_LENGTH - floatNumbers.length;
			for (var i = 0; i < count; i++) {
				floatNumbers += "0";
			}
		}
		// 计算分隔符的个数.
		var seperateCount = Math.round(integerNumbers.length / 3);
		var result = "";
		for (var i = 0; i < seperateCount; i++) {
			result = SEPERATE_CHAR + integerNumbers.substring(integerNumbers.length - 3, integerNumbers.length) + result;
			integerNumbers = integerNumbers.substring(0, integerNumbers.length - 3);
		}
		if ((integerNumbers == null || integerNumbers.length == 0) && SEPERATE_CHAR != "") {
			result = result.substring(1, result.length);
		} else {
			result = integerNumbers + result;
		}

		if (FLOAT_LENGTH > 0) {
			return sign + result + "." + floatNumbers + suffix;
		} else {
			return sign + result + suffix;
		}
	},

	checkNumber: function(str, mask) {
		var number;
		if (str == "") {
			return true;
		}
		if (str.indexOf('%') > -1 && mask.indexOf('%') > -1) {
			if (str.indexOf('.') > -1 && mask.indexOf('.') > -1) {
				return NumberUtil.checkNumberValid(str, mask);
			} else if (str.indexOf('.') == -1 && mask.indexOf('.') == -1) {
				return NumberUtil.checkNumberValid(str, mask);
			} else {
				alert("输入的值跟格式不匹配,期望输入值应该符合：" + mask);
				return false;
			}
		} else if (str.indexOf('%') == -1 && mask.indexOf('%') == -1) {
			if (str.indexOf('.') > -1 && mask.indexOf('.') > -1) {
				return NumberUtil.checkNumberValid(str, mask);
			} else if (str.indexOf('.') == -1 && mask.indexOf('.') == -1) {
				return NumberUtil.checkNumberValid(str, mask);
			} else {
				alert("输入的值跟格式不匹配,期望输入值应该符合：" + mask);
				return false;
			}
		} else {
			alert("输入的值跟格式不匹配,期望输入值应该符合：" + mask);
			return false;
		}
	},

	checkNumberValid: function(numbers, mask) {
		if (numbers == null) {
			return false;
		}
		if (numbers == "") {
			return true;
		}
		var nLen = numbers.length;
		for (var i = 0; i < nLen; i++) {
			var temp = numbers.substr(i, 1);
			if ((temp < "0" || temp > "9") && temp != "." && temp != "-" && temp != '%' && temp != ",") {
				alert("数字串中存在非法字符,期望输入值应该符合：" + mask);
				return false;
			}
		}

		var integerNumbers = "";
		var floatNumbers = "";
		if (numbers.indexOf(".") < 0) {
			integerNumbers = numbers;
		} else {
			var number = numbers.split(".");
			if (number.length == 1) {
				integerNumbers = number[0];
			} else if (number.length == 2) {
				integerNumbers = number[0];
				floatNumbers = number[1];
			} else {
				alert("数字传中存在多个小数点,期望输入值应该符合：" + mask);
				return false;
			}
		}
		if (integerNumbers.length > 3 && integerNumbers.indexOf(',') == -1 && mask.indexOf(",") > -1) {
			alert("整数部分中缺少分割符,期望输入值应该符合：" + mask);
			return false;
		}

		while (integerNumbers.indexOf(',') > -1) {
			if (integerNumbers.length - integerNumbers.lastIndexOf(",") - 1 == 3) {
				integerNumbers = integerNumbers.substr(0, integerNumbers.lastIndexOf(","));
			} else {
				alert("数字串中的分隔符的位置不对,期望输入值应该符合：" + mask);
				return false;
			}
		}
		if (integerNumbers.length > 1 && integerNumbers.substr(0, 1) == "0") {
			alert("整数的第一个字符为0,期望输入值应该符合：" + mask);
			return false;
		}
		if (mask.substring(0, 1) == "+") {
			mask = mask.substring(1, mask.length);
		}

		if (mask.substring(mask.length - 1, mask.length) == "%") {
			mask = mask.substring(0, mask.length - 1);
		}
		var integerMaskString = "";
		var floatMaskString = "";
		if (mask.indexOf(".") < 0) {
			integerMaskString = mask;
		} else {
			integerMaskString = mask.substr(0, mask.indexOf("."));
			floatMaskString = mask.substr(mask.indexOf(".") + 1);
		}

		if (integerNumbers.length > integerMaskString.length) {
			alert("整数长度超过允许的最大长度,期望输入值应该符合：" + mask);
			return false;
		}

		var tnumbers = ""; // 去掉value中的分割符
		for (var i = 0; i < nLen; i++) {
			var temp = numbers.substr(i, 1);
			if (temp != ",") {
				tnumbers = tnumbers + temp;
			}
		}
		numbers = tnumbers;

		// 处理正负号.
		var sign = "";
		if (numbers.substr(0, 1) == "-") {
			sign = "-";
			numbers = numbers.substr(1);
		}
		if (numbers.substr(numbers.length - 1, 1) == "%") {
			numbers = numbers.substring(0, numbers.length - 1);
		}

		if (isNaN(numbers)) {
			alert("数字传不是一个合法的数字,期望输入值应该符合：" + mask);
			return false;
		}

		return true;
	}
};

/**
 * @Description: 解决JS计算浮点乘法时的精度问题
 * @errorExample: 0.045 * 1293.00
 * @param arg1
 * @param arg2
 * @return
 */
function accMul(arg1, arg2) {
	var m = 0,
	s1 = arg1.toString(),
	s2 = arg2.toString();
	try {
		m += s1.split(".")[1].length;
	} catch(e) {}

	try {
		m += s2.split(".")[1].length;
	} catch(e) {}

	return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
};

Number.prototype.mul = function(arg) {
	return accMul(arg, this);
};

/**
 * @Description: 解决JS计算浮点加法时的精度问题
 * @errorExample: 0.7+0.1
 * @param arg1
 * @param arg2
 * @return
 */
function accAdd(arg1, arg2) {
	var r1, r2, m;
	try {
		r1 = arg1.toString().split(".")[1].length;
	} catch(e) {
		r1 = 0;
	}

	try {
		r2 = arg2.toString().split(".")[1].length;
	} catch(e) {
		r2 = 0;
	}

	m = Math.pow(10, Math.max(r1, r2));
	return (arg1 * m + arg2 * m) / m;
};

Number.prototype.add = function(arg) {
	return accAdd(arg, this);
};

/**
 * @Description: 解决JS计算浮点除法时的精度问题
 * @errorExample: 0.3/0.1
 * @param arg1
 * @param arg2
 * @return
 */
function accDiv(arg1, arg2) {
	var t1 = 0,
	t2 = 0,
	r1, r2;
	try {
		t1 = arg1.toString().split(".")[1].length;
	} catch(e) {}
	try {
		t2 = arg2.toString().split(".")[1].length;
	} catch(e) {}
	with(Math) {
		r1 = Number(arg1.toString().replace(".", ""));
		r2 = Number(arg2.toString().replace(".", ""));
		return (r1 / r2) * pow(10, t2 - t1);
	}
};

Number.prototype.div = function(arg) {
	return accDiv(this, arg);
};

/**
 * @Description: 解决JS计算浮点减法时的精度问题
 * @errorExample: 0.3/0.1
 * @param arg1
 * @param arg2
 * @return
 */
function accSub(arg1, arg2) {
	var r1, r2, m, n;
	try {
		r1 = arg1.toString().split(".")[1].length;
	} catch(e) {
		r1 = 0;
	}
	try {
		r2 = arg2.toString().split(".")[1].length;
	} catch(e) {
		r2 = 0;
	}
	m = Math.pow(10, Math.max(r1, r2));
	n = (r1 >= r2) ? r1: r2;
	return ((arg1 * m - arg2 * m) / m).toFixed(n);
};

Number.prototype.sub = function(arg) {
	return accSub(this, arg);
};