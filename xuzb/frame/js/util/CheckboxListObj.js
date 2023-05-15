/**
 * 复选狂对象
 */
function CheckboxListObj(obj) {
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
CheckboxListObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
CheckboxListObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
CheckboxListObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
CheckboxListObj.prototype.isValid = function() {
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
CheckboxListObj.prototype.validate = function() {
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
CheckboxListObj.prototype.setRequired = function(required) {
	if (chkObjNull(required)) {
		required = false;
	}
	if (required) {
		this.obj.attr("_required", "true")
	} else {
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
CheckboxListObj.prototype.chkValue = function() {
	return this.validate();
};

/**
 * 事件初始化
 * 
 * @return
 */
CheckboxListObj.prototype.initEvent = function() {
	var target = this.obj.get(0);
	this.obj.find("[name=chk_opts_" + this.obj.attr("id") + "]:checkbox").each(
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
CheckboxListObj.prototype.setReadOnly = function(readonly) {
	this.obj.find("[name=chk_opts_" + this.obj.attr("id") + "]:checkbox").each(
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
CheckboxListObj.prototype.setValue = function(value) {
	this.clear();
	if (chkObjNull(value)) {
		return;
	}

	var arrValue = value.split(",");
	for ( var i = 0, n = arrValue.length; i < n; i++) {
		var oneV = arrValue[i];
		this.obj.find("[name=chk_opts_" + this.obj.attr("id") + "]:checkbox")
				.each(function() {
					if (oneV == $(this).attr("value")) {
						$(this).attr("checked", true);
					}
				});
	}
	this.validate();
	return true;
};

/**
 * 全部选择选项
 * 
 * @return
 */
CheckboxListObj.prototype.selectAll = function() {
	this.obj.find("[name=chk_opts_" + this.obj.attr("id") + "]:checkbox").each(
			function() {
				$(this).attr("checked", true);
			});
	this.validate();
	return this.getValue();
};

/**
 * 获取文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
CheckboxListObj.prototype.getValue = function() {
	var result = new Array();
	this.obj.find("[name=chk_opts_" + this.obj.attr("id") + "]:checkbox").each(
			function() {
				if ($(this).is(":checked")) {
					result.push($(this).attr("value"));
				}
			});
	return result.join(",");// 使用隐藏的value所谓真实的值
};

/**
 * 获取展示文本
 * 
 * @return
 */
CheckboxListObj.prototype.getText = function() {
	var result = new Array();
	this.obj.find("[name=chk_opts_" + this.obj.attr("id") + "]:checkbox").each(
			function() {
				if ($(this).is(":checked")) {
					result.push($(this).attr("_content"));
				}
			});
	return result.join(",");// 使用隐藏的value所谓真实的值
};

/**
 * 数据清空
 */
CheckboxListObj.prototype.clear = function() {
	this.obj.find("[name=chk_opts_" + this.obj.attr("id") + "]:checkbox").each(
			function() {
				$(this).attr("checked", false);
			});
};

/**
 * 置焦点
 * 
 * @return
 */
CheckboxListObj.prototype.focus = function() {
	return this.obj.find(
			"[name=chk_opts_" + this.obj.attr("id") + "]:checkbox:first")
			.focus();
};
