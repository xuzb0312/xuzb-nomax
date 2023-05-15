/**
 * 复选框--create.by.yjc.2017年12月7日 /
 */
function CheckboxListObj(obj) {
	this.obj = obj;
	this.id = this.obj.attr("id");
	var form = this.obj.parents(".layui-form").first();
	this.labelobj = form.find("#" + this.id + "_label");// label的对象
	this.obj_data = this.obj.data("obj_data");// 后台写过来的-标签数据HashMap数据
};

/**
 * 初始化操作--不提供给外部使用，框架自己内部调用
 * 
 * @return
 */
CheckboxListObj.prototype.init = function(mapData) {
	// 参数初始化
	this.obj.data("obj_data", mapData);
	this.obj_data = mapData;
	this.chkValue();
};

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
 * 改变文本值颜色
 * 
 * @param color
 * @return
 */
CheckboxListObj.prototype.setColor = function(color) {
	return this.obj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
CheckboxListObj.prototype.setLabelValue = function(value) {
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
CheckboxListObj.prototype.getLabelValue = function() {
	var labelValue = this.labelobj.html();
	if (this.obj_data.get("required")) {
		labelValue = labelValue.split("<span")[0];// 拿到真实文本
	}
	return labelValue;
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
CheckboxListObj.prototype.isValid = function() {
	if (this.obj_data.get("required")) {// 必录
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
		this
				.setLabelValue(this.getLabelValue()
						+ " <span class=\"layui-icon\" style=\"color:#FF5722;\" title=\"该信息项不允许为空！\">&#xe611;</span> ")
	} else {
		this.setLabelValue(this.getLabelValue());
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
	var lableValue = this.getLabelValue();
	this.obj_data.put("required", required);
	this.setLabelValue(lableValue);
	this.chkValue();
	return true;
};

/**
 * 检测数据合法性--检测不通过，会置焦点
 * 
 * @return
 */
CheckboxListObj.prototype.chkValue = function(showErrMsg) {
	if (chkObjNull(showErrMsg)) {
		showErrMsg = false;
	} else {
		showErrMsg = true;
	}
	var isvalid = this.validate();
	if (!isvalid) {
		if (showErrMsg) {
			var label = this.getLabelValue();
			if (chkObjNull(label)) {
				label = "该";
			} else {
				label = "【" + label + "】"
			}
			MsgBoxUtil.errTips(label + "项目不允许为空。");
		}
	}
	return isvalid;
};

/**
 * 置焦点--无法置焦点，为了标签统一，增加该方法
 * 
 * @return
 */
CheckboxListObj.prototype.focus = function() {
	return true;
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
CheckboxListObj.prototype.setReadOnly = function(readonly) {
	var readonlynow = this.getReadOnly();
	if (readonly == readonlynow) {
		return;
	}
	this.obj_data.put("readonly", readonly);
	if (readonly) {
		this.obj.find("input").attr("disabled", "disabled");
		this.obj.find("div.layui-form-checkbox").addClass(
				"layui-checkbox-disbaled layui-disabled");
	} else {
		this.obj.find("input").removeAttr("disabled");
		this.obj.find("div.layui-form-checkbox").removeClass(
				"layui-checkbox-disbaled layui-disabled");
	}
};

/**
 * 获取只读
 * 
 * @param readonly
 * @return
 */
CheckboxListObj.prototype.getReadOnly = function() {
	return this.obj_data.get("readonly");
};

/**
 * 设置文本内容
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
						$(this)[0].checked = true;
						$(this).next().addClass("layui-form-checked");
					}
				});
	}
	this.validate();
	return true;
};

/**
 * 获取文本内容
 * 
 * @return
 */
CheckboxListObj.prototype.getValue = function() {
	var result = new Array();
	this.obj.find("[name=chk_opts_" + this.obj.attr("id") + "]:checkbox").each(
			function() {
				if ($(this)[0].checked) {
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
				if ($(this)[0].checked) {
					result.push($(this).attr("title"));
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
				$(this)[0].checked = false;
				$(this).next().removeClass("layui-form-checked");
			});
	this.validate();
};