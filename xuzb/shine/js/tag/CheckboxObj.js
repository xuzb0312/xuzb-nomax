/**
 * 开关、单选框的对象
 */
function CheckboxObj(obj) {
	this.obj = obj;
	this.id = this.obj.attr("id");
	var form = this.obj.parents(".layui-form").first();
	this.labelobj = form.find("#" + this.id + "_label");// label的对象
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
CheckboxObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
CheckboxObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
CheckboxObj.prototype.getLabelValue = function() {
	var labelValue = this.labelobj.text();
	return labelValue;
};

/**
 * 置焦点
 * 
 * @return
 */
CheckboxObj.prototype.focus = function() {
	return this.obj.focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
CheckboxObj.prototype.setReadOnly = function(readonly) {
	var readonlynow = this.getReadOnly();
	if (readonly == readonlynow) {
		return;
	}
	if (readonly) {
		this.obj.attr("readonly", "readonly");
		this.obj.css("background", "#FCFCFC");
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
CheckboxObj.prototype.getReadOnly = function() {
	var readonlynow = ("readonly" == this.obj.attr("readonly"));
	return readonlynow;
};

/**
 * 设置文本内容
 * 
 * @return
 */
CheckboxObj.prototype.setValue = function(value) {
	if ("string" == typeof value) {
		value = value.toLowerCase();
	}
	if (chkObjNull(value)) {
		value = false;
	} else if (0 == value) {
		value = false;
	} else if ("0" == value) {
		value = false;
	} else if ("否" == value) {
		value = false;
	} else if ("关" == value) {
		value = false;
	} else if ("false" == value) {
		value = false;
	} else if ("off" == value) {
		value = false;
	} else if ("no" == value) {
		value = false;
	} else if (false == value) {
		value = false;
	} else {
		value = true;
	}
	var dqValue = this.getValue();
	if ("1" == dqValue && value) {
		return false;
	}
	if ("0" == dqValue && !value) {
		return false;
	}
	var text = (this.obj.attr('lay-text') || '|').split('|');
	// 真正展示的元素紧跟着这个元素后面
	var reElem = this.obj.next();
	var className = "layui-form-checked";
	if ("switch" == this.obj.attr("lay-skin")) {
		className = "layui-form-onswitch";
	}
	// 调整
	if (value) {
		this.obj[0].checked = true;
		reElem.addClass(className).find('em').text(text[0])
	} else {
		this.obj[0].checked = false;
		reElem.removeClass(className).find('em').text(text[1])
	}
	return true;
};

/**
 * 获取文本内容
 * 
 * @return
 */
CheckboxObj.prototype.getValue = function() {
	if (this.obj[0].checked) {// 翻了一下源代码，发现有这个赋值，就这么搞吧
		return "1";
	} else {
		return "0";
	}
};

/**
 * 数据清空
 */
CheckboxObj.prototype.clear = function() {
	this.setValue("");
};
