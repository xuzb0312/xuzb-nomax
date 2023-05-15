/**
 * 文本输入框
 */
function TextInputObj(obj) {
	this.obj = obj;
	this.id = this.obj.attr("id");
	var form = this.obj.parents(".layui-form").first();
	this.labelobj = form.find("#" + this.id + "_label");// label的对象
	this.obj_data = this.obj.data("obj_data");// 后台写过来的-标签数据HashMap数据
};

/**
 * 增加静态成员变量，由于laydate对于0000-00日期的处理存在局限，通过该方法判断0000此种日期类型，进行置空，又修改到laydate的源代码
 * <br>
 * private
 * 
 * @return
 */
TextInputObj.dealDateAllZero = function(str, format) {
	if (chkObjNull(str)) {
		return "";
	}
	var yearIndex = format.search("yyyy");
	var monthIndex = format.search("MM");
	var dayIndex = format.search("dd");
	var hourIndex = format.search("HH");
	var minuteIndex = format.search("mm");
	var secondIndex = format.search("ss");

	var yearZero = true;
	var monthZero = true;
	var dayZero = true;
	var hourZero = true;
	var minuteZero = true;
	var secondZero = true;
	if (yearIndex > 0) {
		if ("0000" != str.substr(yearIndex, 4)) {
			yearZero = false;
		}
	}
	if (monthIndex > 0) {
		if ("00" != str.substr(monthIndex, 2)) {
			monthZero = false;
		}
	}
	if (dayIndex > 0) {
		if ("00" != str.substr(dayIndex, 2)) {
			dayZero = false;
		}
	}
	if (hourIndex > 0) {
		if ("00" != str.substr(hourIndex, 2)) {
			hourZero = false;
		}
	}
	if (minuteIndex > 0) {
		if ("00" != str.substr(minuteIndex, 2)) {
			minuteZero = false;
		}
	}
	if (secondIndex > 0) {
		if ("00" != str.substr(secondIndex, 2)) {
			secondZero = false;
		}
	}
	if (yearZero && monthZero && dayZero && hourZero && minuteZero
			&& secondZero) {
		return "";
	}
	return str;
};

/**
 * 验证类型【默认支持的-后续扩充】
 */
TextInputObj.validType = {
	// 只允许输入英文字母或数字
	engNum : {
		fn : function(value) {
			return /^[0-9a-zA-Z]*$/.test(value);
		},
		tips : '请输入英文字母或数字'
	},
	// 只允许汉字、英文字母或数字
	chsEngNum : {
		fn : function(value) {
			return /^([\u4E00-\uFA29]|[\uE7C7-\uE7F3]|[a-zA-Z0-9])*$/
					.test(value);
		},
		tips : '只允许汉字、英文字母或数字。'
	},
	// 只允许汉字、英文字母、数字及下划线
	code : {
		fn : function(value) {
			return /^[\u0391-\uFFE5\w]+$/.test(value);
		},
		tips : '只允许汉字、英文字母、数字及下划线.'
	},
	// 指定字符最小长度
	minLength : {
		fn : function(value, param) {
			return value.trim().length >= Number(param[0]);
		},
		tips : "最少输入 {0} 个字符."
	},
	// 指定字符最大长度
	maxLength : {
		fn : function(value, param) {
			return value.trim().length <= Number(param[0]);
		},
		tips : "最多输入 {0} 个字符."
	}
};

/**
 * 对于自定义的检测类型可以进行注册，注册完成后则可以正常使用--开放的<br>
 * chkFunc(value, param, obj):value当前元素值，para检测参数，obj：textInputObj对象
 * 
 * @return
 */
TextInputObj.registerValidType = function(name, tips, chkFunc) {
	TextInputObj.validType[name] = {
		tips : tips,
		fn : chkFunc
	};
};

/**
 * 初始化操作--不提供给外部使用，框架自己内部调用
 * 
 * @return
 */
TextInputObj.prototype.init = function(mapData) {
	// 参数初始化
	this.obj_data = mapData;
	this.obj.data("obj_data", mapData);

	// 对于值，进行初始设置
	this.setValue(this.obj.val());

	// 其他的初始化
	var dataType = this.obj_data.get("datatype");
	var mask = this.obj_data.get("mask");
	if ("string" == dataType) { // 字符串不进行mask设置，通过validateType进行验证
	} else if ("number" == dataType) { // number型的进行自定义
		if (chkObjNull(mask)) {
			mask = "###########################0.00";
		}
		// 数字型的不进行mask输入中，调整而是在blur的时候进行验证调整
		this.obj.attr("mask", mask);
		new NumberMask(this.obj);
	} else if ("date" == dataType) { // date型的进行自定义
		if (chkObjNull(mask)) {
			mask = "yyyy-MM-dd";
		}
		// 设置mask格式
		this.obj.attr("mask", mask);
		new DateTimeMask(this.obj);// 设置mask

		// 日期框弹出
		var laydateMask = mask.replaceAll("h", "H");// hh的转化
		var laydateType = "date";
		if (laydateMask.contains("d") && laydateMask.contains("H")) {
			laydateType = "datetime";
		} else if (laydateMask.contains("d") && !laydateMask.contains("H")) {
			laydateType = "date";
		} else if (!laydateMask.contains("d") && laydateMask.contains("H")) {
			laydateType = "time";
		} else if (!laydateMask.contains("M")) {
			laydateType = "year";
		} else if (!laydateMask.contains("d")) {
			laydateType = "month";
		}
		var tagobj = this.obj;
		layui.use('laydate', function() {
			var laydate = layui.laydate;
			laydate.render( {
				elem : tagobj[0],
				type : laydateType,
				format : laydateMask,
				trigger : "dblclick"
			});
		});
	} else {
		throw new Error("dataType类型不合法");
	}

	// 事件绑定
	this.obj.bind('blur', function() {
		var objtmp = getObject($(this));
		// 延迟250毫秒执行，目的是在选择事件的时候，或触发blur的检测，但是此时值还未回填造成提示错误提前，引起误导
			setTimeout(function() {
				// 控制是否展示提示信息，只在blur的时候展示
					objtmp.validate(true);
				}, 200);
		});

};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
TextInputObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 改变文本值颜色
 * 
 * @param color
 * @return
 */
TextInputObj.prototype.setColor = function(color) {
	return this.obj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
TextInputObj.prototype.setLabelValue = function(value) {
	if (this.obj_data.get("required")) {
		value = value + "<span style=\"color:red;\">*</span>";
	}
	return this.labelobj.html(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
TextInputObj.prototype.getLabelValue = function() {
	var labelValue = this.labelobj.html();
	if (this.obj_data.get("required")) {
		labelValue = labelValue.split("<span")[0];// 拿到真实文本
	}
	return labelValue;
};

/**
 * 隐藏操作
 */
TextInputObj.prototype.hide = function(readonly) {
	if ("formlinegroup" == this.obj_data.get("itempos")) {
		this.obj.parent().hide();
	} else {
		this.obj.parent().parent().hide();
	}

};

/**
 * 展示操作
 */
TextInputObj.prototype.show = function(readonly) {
	if ("formlinegroup" == this.obj_data.get("itempos")) {
		this.obj.parent().show();
	} else {
		this.obj.parent().parent().show();
	}
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
TextInputObj.prototype.isValid = function(showerrTips) {
	if (!chkObjNull(showerrTips) && showerrTips) {
		showerrTips = true;
	} else {
		showerrTips = false;
	}
	var label = this.getLabelValue();
	if (chkObjNull(label)) {
		label = "该";
	} else {
		label = "【" + label + "】"
	}
	var value = this.getValue();
	if (this.obj_data.get("required")) {// 必录
		if (chkObjNull(value)) {
			showerrTips ? MsgBoxUtil.errTips(label + "项目不允许为空") : null;
			return false;
		}
	} else {
		if (chkObjNull(value)) {
			return true;
		}
	}
	var validtype = this.obj_data.get("validtype");
	if (chkObjNull(validtype)) {
		return true;
	}
	var arrValidtype = validtype.split("|");
	for ( var i = 0, n = arrValidtype.length; i < n; i++) {
		var arrOneVal = arrValidtype[i].split(":");
		var vKey = arrOneVal[0];
		var param = null;
		if (arrOneVal.length > 1) {
			param = arrOneVal.splice(1, arrOneVal.length - 1);
		}
		var vOptions = TextInputObj.validType[vKey];
		if (typeof vOptions == "undefined") {
			MsgBoxUtil.errTips("ERR:验证类型[" + vKey + "]未定义!")
			continue;
		}
		// 执行定义的验证规则-检测不通过测抛出
		if (!vOptions.fn(value, param, this)) {
			if (showerrTips) {
				var tipsMsg = vOptions.tips;
				if (!chkObjNull(param)) {
					for ( var i = 0, n = param.length; i < n; i++) {
						tipsMsg = tipsMsg.replaceAll("\\{" + i + "\\}",
								param[i]);
					}
				}
				MsgBoxUtil.tagErrTips(tipsMsg, this.obj);
			}
			return false;
		}
	}
	return true;
};

/**
 * 验证控件的数据合法行，并返回是否合法
 * 
 * @return
 */
TextInputObj.prototype.validate = function(showerrTips) {
	var isvalid = this.isValid(showerrTips);
	if (!isvalid) {
		this.obj.addClass("layui-form-danger");
	} else {
		this.obj.removeClass("layui-form-danger");
	}
	return isvalid;
};

/**
 * 设置是否必录
 */
TextInputObj.prototype.setRequired = function(required) {
	if (chkObjNull(required)) {
		required = false;
	}
	var lableValue = this.getLabelValue();
	this.obj_data.put("required", required);
	this.setLabelValue(lableValue);
	this.validate();
	return true;
};

/**
 * 检测数据合法性--检测不通过，会置焦点
 * 
 * @return
 */
TextInputObj.prototype.chkValue = function(showerrTips) {
	var isvalid = this.validate(showerrTips);
	return isvalid;
};

/**
 * 置焦点
 * 
 * @return
 */
TextInputObj.prototype.focus = function() {
	return this.obj.focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
TextInputObj.prototype.setReadOnly = function(readonly) {
	var readonlynow = this.getReadOnly();
	if (readonly == readonlynow) {
		return;
	}
	this.obj_data.put("readonly", readonly);
	if (readonly) {
		this.obj.attr("readonly", "readonly");
		this.obj.css("background", "#FCFCFC");
		this.obj.attr("placeholder", "");
	} else {
		this.obj.removeAttr("readonly");
		this.obj.css("background", "#FFFFFF");
		this.obj.attr("placeholder", this.obj_data.get("placeholder"));
	}
};

/**
 * 获取只读
 * 
 * @param readonly
 * @return
 */
TextInputObj.prototype.getReadOnly = function() {
	return this.obj_data.get("readonly");
};

/**
 * 设置文本内容
 * 
 * @return
 */
TextInputObj.prototype.setValue = function(value, valuemask) {
	var dataType = this.obj_data.get("datatype");
	var mask = this.obj_data.get("mask");
	var valueTmp = value;
	if ("string" == dataType) { // value值为字符串
		// 字符串，原数据什么样，目标数据就什么样
		if (chkObjNull(valueTmp)) {
			valueTmp = "";
		}
	} else if ("number" == dataType) { // value值也是number
		if (chkObjNull(valueTmp)) {
			valueTmp = 0;
		}
		if (chkObjNull(mask)) {
			mask = "###########################0.00";
		}
		var valueTmpNm = Number(valueTmp);
		if (isNaN(valueTmpNm)) {
			alert("传入的值不是number类型的数据无法进行value设置");
			return;
		}
		valueTmp = NumberUtil.getShowValueByMask(mask, valueTmpNm.toString());
	} else if ("date" == dataType) { // value值为字符串
		// -需要借助DateUtil来实现时间格式的转换
		/**
		 * 对于日期的格式，eFrame框架强制规定：<br>
		 * 程序处理格式一律为yyyyMMddhhmmss<br>
		 * 前台展示格式一律为yyyy-MM-dd hh:mm:ss<br>
		 * 又细分为几种类型：yyyy(年份),yyyyMM(年月),yyyyMMdd(日期),yyyyMMddHHmmss(时间);
		 */
		if (chkObjNull(valueTmp)) {
			valueTmp = "";
		} else {
			if (chkObjNull(mask)) {
				mask = "yyyy-MM-dd";
			}
			var sourcemask = valuemask;
			if (chkObjNull(sourcemask)) {
				sourcemask = this.obj_data.get("sourcemask");
			}
			if (chkObjNull(sourcemask)) {
				sourcemask = "yyyyMMdd";
			}
			valueTmp = DateUtil.changeFormat(valueTmp, sourcemask, mask);
		}
	} else {
		throw new Error("dataType类型不合法");
	}
	this.obj.attr("title", valueTmp);// 增加鼠标放上的提示
	this.obj.val(valueTmp);
	this.validate();
	return true;
};

/**
 * 获取文本内容
 * 
 * @return
 */
TextInputObj.prototype.getValue = function(targetmask) {
	var dataType = this.obj_data.get("datatype");
	var mask = this.obj_data.get("mask");
	var value = this.obj.val();
	if ("string" == dataType) { // 返回字符串-无特殊操作
	} else if ("number" == dataType) { // 返回number
		if (chkObjNull(value)) {
			value = 0;
		} else {
			if (chkObjNull(mask)) {
				mask = "###########################0.00";
			}
			value = NumberUtil.getRealValue(value, mask);
		}
	} else if ("date" == dataType) { // 返回字符串
		// -需要借助DateUtil来实现时间格式的转换
		if (chkObjNull(value)) {
			value = "";
		} else {
			if (chkObjNull(mask)) {
				mask = "yyyy-MM-dd";
			}
			var sourcemask = targetmask;
			if (chkObjNull(sourcemask)) {
				sourcemask = this.obj_data.get("sourcemask");
			}
			if (chkObjNull(sourcemask)) {
				sourcemask = "yyyyMMdd";
			}
			value = DateUtil.changeFormat(value, mask, sourcemask);
		}
	} else {
		throw new Error("dataType类型不合法");
	}
	return value;
};

/**
 * 数据清空
 */
TextInputObj.prototype.clear = function() {
	this.setValue("");
};