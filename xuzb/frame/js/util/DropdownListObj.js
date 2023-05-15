/**
 * 下拉框控件对象
 * 
 * @param obj
 * @return
 */
function DropdownListObj(obj) {
	this.obj = obj;
	var objid = this.obj.attr("id");
	var form = this.obj.parents("form").first();// 由于一个页面可能存在多个同名标签，所以限定范围
	this.labelobj = form.find("#" + objid + "_label");// label的对象
	this.readOnlyObj = form.find("#" + objid + "_readonly");// 只读展示对象
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
	this.readOnlyObj.css("color", color);
	this.obj.css("color", color);
	return true;
};

/**
 * 设置label文本内容
 * 
 * @return
 */
DropdownListObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
DropdownListObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
DropdownListObj.prototype.isValid = function() {
	// 对于dropdownlist的验证，仅限于是否允许为空的限制
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
DropdownListObj.prototype.validate = function() {
	var isvalid = this.isValid();
	if (!isvalid) {
		this.obj.removeClass("dropdownlist");
		this.obj.addClass("dropdownlist-invlalid");
	} else {
		this.obj.removeClass("dropdownlist-invlalid");
		this.obj.addClass("dropdownlist");
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
DropdownListObj.prototype.chkValue = function() {
	return this.validate();
};

/**
 * 处理其默认事件问题，不提供给外部调用-在控件加载完成后，自动附加事件操作
 */
DropdownListObj.prototype.initEvent = function() {
	// 失去焦点时验证
	this.obj.bind('blur', function() {
		var objtmp = getObject($(this));
		objtmp.validate();
	});
	// 默认加载完成后，进行一次验证
	this.validate();
};

/**
 * 置焦点
 * 
 * @return
 */
DropdownListObj.prototype.focus = function() {
	return this.obj.focus();
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
	var loadOpt = this.obj.attr("_loadOpt");
	if (readonlynow && "false" == loadOpt) {
		alert("当前只读状态为只读，并未设置加载项，不允许设置为更改改状态。");
		return;
	}
	// 设置展示值
	this.readOnlyObj.val(this.getText());

	if (readonly) {
		this.obj.attr("readonly", "readonly");
		this.obj.css("background", "#F9F9F9");
		this.obj.find("option[value='']").text("");
		this.obj.hide();
		this.readOnlyObj.show();
	} else {
		this.obj.removeAttr("readonly");
		this.obj.css("background", "#FFFFFF");
		this.obj.find("option[value='']").text("--请选择--");
		this.readOnlyObj.hide();
		this.obj.show();
	}
};

/**
 * 获取只读
 * 
 * @param readonly
 * @return
 */
DropdownListObj.prototype.getReadOnly = function() {
	var readonlynow = ("readonly" == this.obj.attr("readonly"));
	return readonlynow;
};

/**
 * 设置文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
DropdownListObj.prototype.setValue = function(value) {
	this.obj.val(value);
	this.validate();
	this.readOnlyObj.val(this.getText());
	return true;
};

/**
 * 获取文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
DropdownListObj.prototype.getValue = function() {
	return this.obj.val();
};


/**
 * 获取文本内容---Text
 * 
 * @return
 */
DropdownListObj.prototype.getText = function() {
	if(chkObjNull(this.getValue())){
		return "";
	}else{
		return this.obj.find("option:selected").text();
	}
};

/**
 * 数据清空
 */
DropdownListObj.prototype.clear = function() {
	this.setValue("");
};

/**
 * 重新加载选项-选项为json格式为[key-value]
 */
DropdownListObj.prototype.reloadOpt = function(json) {
	if (chkObjNull(json)) {
		alert("传入的选项json为空");
		return;
	}

	// 如果只读则，selected的展示值为空
	var nulltip = "--请选择--";
	if (this.getReadOnly()) {
		nulltip = "";
	}

	// 首先清空选项
	this.obj.html("<option value=\"\" selected=\"selected\">" + nulltip
			+ "</option>");
	for ( var i = 0, n = json.length; i < n; i++) {
		var oneOpt = json[i];
		var key = oneOpt.key;
		var value = oneOpt.value;
		this.obj.append("<option value=\"" + key + "\">" + value + "</option>");
	}
	return true;
};

/**
 * 获取选项-选项为json格式为[key-value]
 */
DropdownListObj.prototype.getOpt = function() {
	var jsonList = [];
	this.obj.find("option").each(function() {
		var key = $(this).val();
		var value = $(this).text();
		if (chkObjNull(key)) {
			return;
		}
		var arrJson = {
			key : key,
			value : value
		};
		jsonList.push(arrJson);
	});
	return jsonList;
};

/**
 * 设置默认值的方法：当标签为必录，非只读，且选项只有一个时，则将选项设为该标签的值
 */
DropdownListObj.prototype.setDefalut = function() {
	var required = this.obj.attr("_required");
	var readonly = this.getReadOnly();
	if (!readonly && required) {
		var opts = this.getOpt();
		if (opts.length == 1) {
			this.setValue(opts[0].key);
		}
	}
};