/**
 * 文本域标签信息*create.yjc.2017年12月6日
 */
function TextareaObj(obj) {
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
TextareaObj.prototype.init = function(mapData) {
	// 参数初始化
	this.obj_data = mapData;
	this.obj.data("obj_data", mapData);
	this.validate();// 启动一次验证

	// 事件绑定
	this.obj.bind('blur', function() {
		var objtmp = getObject($(this));
		if (!objtmp.validate()) {
			var label = objtmp.getLabelValue();
			if (chkObjNull(label)) {
				label = "该";
			} else {
				label = "【" + label + "】"
			}
			MsgBoxUtil.errTips(label + "项目不允许为空。");
		}
	});
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
TextareaObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 改变文本值颜色
 * 
 * @param color
 * @return
 */
TextareaObj.prototype.setColor = function(color) {
	return this.obj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
TextareaObj.prototype.setLabelValue = function(value) {
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
TextareaObj.prototype.getLabelValue = function() {
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
TextareaObj.prototype.isValid = function() {
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
TextareaObj.prototype.validate = function() {
	var isvalid = this.isValid();
	if (!isvalid) {
		this.obj.addClass("layui-form-danger");
	} else {
		this.obj.removeClass("layui-form-danger");
	}
	return isvalid;
};

/**
 * 设置设置是否必录
 */
TextareaObj.prototype.setRequired = function(required) {
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
TextareaObj.prototype.chkValue = function() {
	var isvalid = this.validate();
	if (!isvalid) {
		this.focus();
	}
	return isvalid;
};

/**
 * 置焦点
 * 
 * @return
 */
TextareaObj.prototype.focus = function() {
	return this.obj.focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
TextareaObj.prototype.setReadOnly = function(readonly) {
	var readonlynow = this.getReadOnly();
	if (readonly == readonlynow) {
		return;
	}
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
TextareaObj.prototype.getReadOnly = function() {
	var readonlynow = ("readonly" == this.obj.attr("readonly"));
	return readonlynow;
};

/**
 * 设置文本内容
 * 
 * @return
 */
TextareaObj.prototype.setValue = function(value) {
	this.obj.val(value);
	this.chkValue();
	return true;
};

/**
 * 获取文本内容
 * 
 * @return
 */
TextareaObj.prototype.getValue = function() {
	return this.obj.val();
};

/**
 * 数据清空
 */
TextareaObj.prototype.clear = function() {
	this.setValue("");
};