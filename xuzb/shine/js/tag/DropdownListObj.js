/**
 * 单选下拉框--create.by.yjc.2017年12月11日
 */
function DropdownListObj(obj) {
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
DropdownListObj.prototype.init = function(mapData) {
	// 参数初始化
	this.obj.data("obj_data", mapData);
	this.obj_data = mapData;
	this.validate();
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
DropdownListObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 改变文本值颜色
 * 
 * @param color
 * @return
 */
DropdownListObj.prototype.setColor = function(color) {
	this.obj.next().find("input").css("color", color);
	this.obj.parent().next().css("color", color);
	return true;
};

/**
 * 设置label文本内容
 * 
 * @return
 */
DropdownListObj.prototype.setLabelValue = function(value) {
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
DropdownListObj.prototype.getLabelValue = function() {
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
DropdownListObj.prototype.isValid = function() {
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
DropdownListObj.prototype.validate = function() {
	var isvalid = this.isValid();
	if (!isvalid) {
		var input = this.obj.next().find("input");
		if (input.length > 0) {
			input.addClass("layui-form-danger");
		} else {
			var ddlObj = this;
			setTimeout(function() {
				ddlObj.validate();
			}, 1500);// 如果没有发现说明控件还未渲染完成。1.5秒之后，重新检测(一直递归检测)
		}
	} else {
		this.obj.next().find("input").removeClass("layui-form-danger");
	}
	return isvalid;
};

/**
 * 设置设置是否必录
 */
DropdownListObj.prototype.setRequired = function(required) {
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
 * 检测数据合法性--检测不通过，会置焦点(传递参数的方式，框架内部调用)
 * 
 * @return
 */
DropdownListObj.prototype.chkValue = function(showErrMsg) {
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
			MsgBoxUtil.errTips(label + "项目不符合要求。");
		}
		// 由于存在一些特殊情况，焦点无法回置，所以需要延迟300毫秒进行焦点会址
		var ddlObj = this;
		setTimeout(function() {
			ddlObj.focus();
		}, 300);
	}
	return isvalid;
};

/**
 * 置焦点--无法置焦点，为了标签统一，增加该方法
 * 
 * @return
 */
DropdownListObj.prototype.focus = function() {
	return this.obj.next().find("input").focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
DropdownListObj.prototype.setReadOnly = function(readonly) {
	var readonlynow = this.getReadOnly();
	if (readonly == readonlynow) {
		return;
	}
	this.obj_data.put("readonly", readonly);
	if (readonly) {
		this.obj.parent().hide();
		this.obj.parent().next().show();
	} else {
		this.obj.parent().next().hide();
		this.obj.parent().show();
	}
};

/**
 * 隐藏操作
 */
DropdownListObj.prototype.hide = function(readonly) {
	if ("formlinegroup" == this.obj_data.get("itempos")) {
		this.obj.parent().parent().hide();
	} else {
		this.obj.parent().parent().parent().hide();
	}

};

/**
 * 展示操作
 */
DropdownListObj.prototype.show = function(readonly) {
	if ("formlinegroup" == this.obj_data.get("itempos")) {
		this.obj.parent().parent().show();
	} else {
		this.obj.parent().parent().parent().show();
	}
};

/**
 * 获取只读
 * 
 * @param readonly
 * @return
 */
DropdownListObj.prototype.getReadOnly = function() {
	return this.obj_data.get("readonly");
};

/**
 * 设置文本内容
 * 
 * @return
 */
DropdownListObj.prototype.setValue = function(value) {
	var text = this.getTextByValue(value);
	$(document).off('click');// 先拿掉document的click事件干扰，不然会存在数据修改后，被回置的情况。

	// 选项当前状态的调整
	this.obj.next().find("dd").removeClass("layui-this");
	if (!chkObjNull(value)) {
		this.obj.next().find("dd[lay-value='" + value + "']").addClass(
				"layui-this");
	}

	// 其他项内容的调整同步
	this.obj.next().find("input").val(text);
	this.obj.val(value);

	// 隐藏域值的更新（只读项）
	this.obj.parent().next().val(text).attr("_value", value);

	// 数据检查是否合法
	this.chkValue();
	return true;
};

/**
 * 获取文本内容
 * 
 * @return
 */
DropdownListObj.prototype.getValue = function() {
	var result = this.obj.val();
	return result;
};

/**
 * 获取展示文本
 * 
 * @return
 */
DropdownListObj.prototype.getText = function() {
	return this.getTextByValue(this.getValue());
};

/**
 * 数据清空
 */
DropdownListObj.prototype.clear = function() {
	this.setValue("");
};

/**
 * 同步value值--内部调用，外部不开放
 */
DropdownListObj.prototype.synValue = function() {
	var value = this.getValue();
	var text = this.getTextByValue(value);
	this.obj.parent().next().val(text).attr("_value", value);// 同步值
};

/**
 * 检查值是否一致
 */
DropdownListObj.prototype.isValueChange = function() {
	var dqvalue = this.getValue();
	var ysvalue = this.obj.parent().next().attr("_value");
	if (dqvalue == ysvalue) {
		return false;
	} else {
		return true;
	}
};

/**
 * 根据value值获取文本信息
 */
DropdownListObj.prototype.getTextByValue = function(value) {
	if (chkObjNull(value)) {
		return "";
	}
	var dsOpt = this.getOpt();
	var text = "";
	for ( var i = 0, n = dsOpt.length; i < n; i++) {
		var code = dsOpt[i].code;
		var content = dsOpt[i].content;
		if (value == code) {
			text = content;
			break;
		}
	}
	return text;
};

/**
 * 获取选项-选项为json格式为[code-content]
 */
DropdownListObj.prototype.getOpt = function() {
	var dsOpt = this.obj_data.get("dsopt");
	return dsOpt;
};

/**
 * 设置默认值的方法：当标签为必录，非只读，且选项只有一个时，则将选项设为该标签的值
 */
DropdownListObj.prototype.setDefalut = function() {
	var required = this.obj_data.get("required");
	var readonly = this.getReadOnly();
	if (!readonly && required) {
		var opts = this.getOpt();
		if (opts.length == 1) {
			this.setValue(opts[0].code);
		}
	}
};

/**
 * 重新加载选项-选项为json格式为[code-content]
 */
DropdownListObj.prototype.reloadOpt = function(json, tips) {
	if (chkObjNull(json)) {
		MsgBoxUtil.errTips("传入的选项json为空");
		return;
	}
	// 情况选择
	this.clear();
	if (chkObjNull(tips)) {
		tips = this.obj_data.get("tips");
	}
	this.obj_data.put("tips", tips);

	// 首先清空选项
	this.obj.html("<option value=\"\" selected=\"selected\">" + tips
			+ "</option>");
	for ( var i = 0, n = json.length; i < n; i++) {
		var oneOpt = json[i];
		var key = oneOpt.code;
		var value = oneOpt.content;
		this.obj.append("<option value=\"" + key + "\">" + value + "</option>");
	}

	// 重新渲染
	var formObj = this.obj.parents(".layui-form").first();
	var formId = formObj.attr("id");
	layui.use("form", function() {
		var form = layui.form;
		form.render("select", formId);
	});

	// 对象初始化一下
	this.obj_data.put("dsopt", json);
	this.init(this.obj_data);
	return true;
};