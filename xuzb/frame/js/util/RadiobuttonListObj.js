/**
 * 复选狂对象
 */
function RadiobuttonListObj(obj) {
	this.obj = obj;
	var objid = this.obj.attr("id");
	var form = this.obj.parents("form").first();
	this.labelobj = form.find("#" + objid + "_label");// label的对象
}

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
RadiobuttonListObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
RadiobuttonListObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
RadiobuttonListObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
RadiobuttonListObj.prototype.isValid = function() {
	var required = this.obj.attr("_required");
	if ("true" == required) {
		var value = this.getValue();
		if (chkObjNull(value)) {
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
RadiobuttonListObj.prototype.validate = function() {
	var isvalid = this.isValid();
	if (!isvalid) {
		this.obj.addClass("checkboxlist-invlalid");
	} else {
		this.obj.removeClass("checkboxlist-invlalid");
	}
	return isvalid;
};

/**
 * 设置设置是否必录
 */
RadiobuttonListObj.prototype.setRequired = function(required) {
	if (chkObjNull(required)) {
		required = false;
	}
	if(required){
		this.obj.attr("_required", "true")
	}else{
		this.obj.attr("_required", "false")
	}
	this.validate();
	return true;
};

/**
 * 检测数据合法性
 * 
 * @return
 */
RadiobuttonListObj.prototype.chkValue = function() {
	return this.validate();
};

/**
 * 事件初始化
 * 
 * @return
 */
RadiobuttonListObj.prototype.initEvent = function() {
	var target = this.obj.get(0);
	this.obj.find("[name=rbl_opts_" + this.obj.attr("id") + "]:radio").each(
			function() {
				$(this).bind("click", function() {
					getObject($(target)).chkValue();
				});
			});
	this.chkValue();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
RadiobuttonListObj.prototype.setReadOnly = function(readonly) {
	this.obj.find("[name=rbl_opts_" + this.obj.attr("id") + "]:radio").each(
			function() {
				if (readonly) {
					$(this).attr("readonly", "readonly");
					$(this).attr("disabled", "disabled");
				} else {
					$(this).removeAttr("readonly");
					$(this).removeAttr("disabled");
				}
			});
};

/**
 * 设置文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
RadiobuttonListObj.prototype.setValue = function(value) {
	this.clear();
	if (chkObjNull(value)) {
		return;
	}
	this.obj.find("[name=rbl_opts_" + this.obj.attr("id") + "]:radio").each(
			function() {
				if (value == $(this).attr("value")) {
					$(this).attr("checked", true);
				} else {
					$(this).attr("checked", false);
				}
			});
	this.validate();
	return true;
};

/**
 * 获取文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
RadiobuttonListObj.prototype.getValue = function() {
	var value = "";
	this.obj.find("[name=rbl_opts_" + this.obj.attr("id") + "]:radio").each(
			function() {
				if ($(this).is(":checked")) {
					value = $(this).attr("value");
				}
			});
	return value;
};

/**
 * 获取展示文本
 * 
 * @return
 */
RadiobuttonListObj.prototype.getText = function() {
	var value = "";
	this.obj.find("[name=rbl_opts_" + this.obj.attr("id") + "]:radio").each(
			function() {
				if ($(this).is(":checked")) {
					value = $(this).attr("_content");
				}
			});
	return value;
};

/**
 * 数据清空
 */
RadiobuttonListObj.prototype.clear = function() {
	this.obj.find("[name=rbl_opts_" + this.obj.attr("id") + "]:radio").each(
			function() {
				$(this).attr("checked", false);
			});
};

/**
 * 置焦点
 * 
 * @return
 */
RadiobuttonListObj.prototype.focus = function() {
	return this.obj.find(
			"[name=rbl_opts_" + this.obj.attr("id") + "]:radio:first")
			.focus();
};
