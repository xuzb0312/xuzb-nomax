/**
 * 文本输入框的对象
 * 
 * @author yjc
 * @param obj
 * @return
 */
function TextInputObj(obj) {
	this.obj = obj;
	var objid = this.obj.attr("id");
	var form = this.obj.parents("form").first();
	this.labelobj = form.find("#" + objid + "_label");// label的对象
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
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
TextInputObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 设置验证规则
 */
TextInputObj.prototype.setValidType = function(validType) {
	if (chkObjNull(validType)) {
		validType = null;
	}
	var opts = this.obj.validatebox("options");
	opts.validType = validType;
	this.validate();
	return true;
};

/**
 * 设置设置是否必录
 */
TextInputObj.prototype.setRequired = function(required) {
	if (chkObjNull(required)) {
		required = false;
	}
	var opts = this.obj.validatebox("options");
	opts.required = required;
	this.validate();
	return true;
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
TextInputObj.prototype.isValid = function() {
	return this.obj.validatebox("isValid");
};

/**
 * 验证控件的数据合法行，并返回是否合法
 * 
 * @return
 */
TextInputObj.prototype.validate = function() {
	this.obj.validatebox("validate");
	return this.obj.validatebox("isValid");
};

/**
 * 检测数据合法性
 * 
 * @return
 */
TextInputObj.prototype.chkValue = function() {
	return this.validate();
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
	if (readonly) {
		this.obj.attr("readonly", "readonly");
		this.obj.css("background", "#F9F9F9");
	} else {
		this.obj.removeAttr("readonly");
		this.obj.css("background", "#FFFFFF");
	}
};

/**
 * 获取只读
 * 
 * @param readonly
 * @return
 */
TextInputObj.prototype.getReadOnly = function() {
	var readonlynow = ("readonly" == this.obj.attr("readonly"));
	return readonlynow;
};

/**
 * 设置文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
TextInputObj.prototype.setValue = function(value, valuemask) {
	var dataType = this.obj.attr("dataType");
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
		var mask = this.obj.attr("mask");
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
			var mask = this.obj.attr("mask");
			if (chkObjNull(mask)) {
				mask = "yyyy-MM-dd";
			}
			var sourcemask = valuemask;
			if (chkObjNull(sourcemask)) {
				sourcemask = this.obj.attr("sourceMask");
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
	this.obj.validatebox("setValue", valueTmp);

	// 对于使用mask插件的类型
	if ("string" == dataType) {
		var mask = this.obj.attr("mask");
		if (!chkObjNull(mask)) {
			var maskMap = new HashMap(TextInputObjStringMaskCof);
			if (maskMap.containsKey(mask)) {
				this.obj.mask(maskMap.get(mask));
			} else {
				this.obj.mask(mask);
			}
		}
	}

	return true;
};

/**
 * 获取文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
TextInputObj.prototype.getValue = function(targetmask) {
	var dataType = this.obj.attr("dataType");
	var value = this.obj.validatebox("getValue");
	if ("string" == dataType) { // 返回字符串
		// 字符串，原数据什么样，目标数据就什么样
		var mask = this.obj.attr("mask");
		// 字符串没有什么特殊的mask设置,如果需要的话，可以参照mask插件的清空进行书写
		if (!chkObjNull(mask)) {
			value = this.obj.mask();
		}
	} else if ("number" == dataType) { // 返回number
		if (chkObjNull(value)) {
			value = 0;
		} else {
			var mask = this.obj.attr("mask");
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
			var mask = this.obj.attr("mask");
			if (chkObjNull(mask)) {
				mask = "yyyy-MM-dd";
			}
			var sourcemask = targetmask;
			if (chkObjNull(sourcemask)) {
				sourcemask = this.obj.attr("sourceMask");
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

/**
 * string类型的mask对应表-此处做的是非严格验证，注意实际准确性验证
 */
var TextInputObjStringMaskCof = {
	yzbm : "999999",// 邮政编码
	sfzhm : "99999999999999999X",// 身份证号码
	color : "#HHHHHH",// 颜色
	sjhm : "999-9999-9999"// 手机号码
};
/**
 * 设置mask
 */
TextInputObj.prototype.dealMask = function() {
	var dataType = this.obj.attr("dataType");
	var mask = this.obj.attr("mask");
	if ("string" == dataType) { // 返回字符串
		// 字符串没有什么特殊的mask设置,如果需要的话，可以参照mask插件的清空进行书写
		if (chkObjNull(mask)) {
			return;
		}
		// 特殊定值放到此处
		var maskMap = new HashMap(TextInputObjStringMaskCof);
		if (maskMap.containsKey(mask)) {
			this.obj.mask(maskMap.get(mask));
		} else {
			this.obj.mask(mask);
		}
	} else if ("number" == dataType) { // number型的进行自定义
		if (chkObjNull(mask)) {
			mask = "###########################0.00";
		}
		// 数字型的不进行mask输入中，调整而是在blur的时候进行验证调整
		new NumberMask(this.obj);

		// 快捷键操作
		TextInputUtil.dealNumberShortcutKeys(this.obj);
	} else if ("date" == dataType) { // date型的进行自定义
		if (chkObjNull(mask)) {
			mask = "yyyy-MM-dd";
		}
		// 增加上双击事件，展示日期选择框
		new CalendarObj(this.obj);

		new DateTimeMask(this.obj);// 设置mask

		// 快捷键操作
		TextInputUtil.dealDateShortcutKeys(this.obj);

	} else {
		throw new Error("dataType类型不合法");
	}
};

/**
 * 存在搜索框的情况下，对搜索框的设置不能点击
 */
TextInputObj.prototype.setSearchBtnDisabled = function(disabled) {
	var id = this.obj.attr("id");
	var parent = this.obj.parents("table").first();
	var sbtn = parent.find("#searchBtn_" + id);
	var sbtn_disabled = parent.find("#searchBtn_" + id + "_disabled");
	if (sbtn.length <= 0) {
		return false;
	}
	if (sbtn_disabled.length <= 0) {
		return false;
	}
	if (disabled) {
		sbtn.hide();
		sbtn_disabled.show();
	} else {
		sbtn_disabled.hide();
		sbtn.show();
	}
	return true;
};

/**
 * 一些常用的工具，发送电子邮件
 * 
 * @return
 */
TextInputObj.prototype.sendEmail = function() {
	var emailAddress = this.getValue();
	if (chkObjNull(emailAddress)) {
		alert("电子邮件地址为空，无法发送邮件。");
		return;
	}
	if (!$.string.isEmail(emailAddress)) {
		alert("电子邮件地址格式不正确，无法发送电子邮件。");
		return;
	}

	var url = new URL("taglib.do", "fwdSendEmailPage4TextInput");
	url.addPara("address", emailAddress);
	openTopWindow("发送邮件", "icon-email-edit", url, "big", null);
};

/**
 * 工具方法
 */
var TextInputUtil = {
	/**
	 * 处理数字类型的输入框的快捷键
	 */
	dealNumberShortcutKeys : function(obj) {
		obj.bind("keydown", function(e) {
			if (e.altKey && e.which == 38) {
				// 增加1 alt+up
				var txtObj = get($(e.target));
				if (txtObj.getReadOnly()) {
					return;
				}
				var value = txtObj.getValue()
				txtObj.setValue(value + 1);
			} else if (e.altKey && e.which == 40) {
				// 减少1 alt+down
				var txtObj = get($(e.target));
				if (txtObj.getReadOnly()) {
					return;
				}
				var value = txtObj.getValue()
				txtObj.setValue(value - 1);
			}
		});
	},
	/**
	 * 处理日期类型的输入框的快捷键
	 */
	dealDateShortcutKeys : function(obj) {
		obj.bind("keydown", function(e) {
			if (e.altKey && e.which == 81) {
				// 当前时间alt+Q
				var txtObj = get($(e.target));
				if (txtObj.getReadOnly()) {
					return;
				}
				var value = txtObj.getValue()
				if (chkObjNull(value)) {
					var smask = TextInputUtil.getSourceMask4Date(txtObj);
					txtObj.setValue(DateUtil.getDateString(new Date(), smask));
				}
			} else if (e.ctrlKey && e.which == 81) {
				// 强制当前时间ctrl+Q
				var txtObj = get($(e.target));
				if (txtObj.getReadOnly()) {
					return;
				}
				var smask = TextInputUtil.getSourceMask4Date(txtObj);
				txtObj.setValue(DateUtil.getDateString(new Date(), smask));
			} else if (e.altKey && e.which == 38) {
				// 增加一天alt+up
				var txtObj = get($(e.target));
				if (txtObj.getReadOnly()) {
					return;
				}
				var value = txtObj.getValue()
				var smask = TextInputUtil.getSourceMask4Date(txtObj);
				txtObj.setValue(TextInputUtil.addDays4Date(value, smask, 1));
			} else if (e.altKey && e.which == 40) {
				// 减少一天alt+down
				var txtObj = get($(e.target));
				if (txtObj.getReadOnly()) {
					return;
				}
				var value = txtObj.getValue()
				var smask = TextInputUtil.getSourceMask4Date(txtObj);
				txtObj.setValue(TextInputUtil.addDays4Date(value, smask, -1));
			}
		});
	},
	/**
	 * 获取日期的sourseMask
	 */
	getSourceMask4Date : function(txtObj) {
		var smask = txtObj.obj.attr("sourceMask");
		if (chkObjNull(smask)) {
			smask = "yyyyMMdd";
		}
		return smask;
	},
	/**
	 * 增减天数工具方法
	 */
	addDays4Date : function(value, mask, days) {
		value = new DateObj(value, mask);
		if (value.isZero()) {
			value = new Date();
		} else {
			value = value.getDate();
		}
		value.setDate(value.getDate() + days);
		return DateUtil.getDateString(value, mask);
	}
};