/**
 * 按钮对象
 * 
 * @author yjc
 * @return
 */
function ButtonObj(obj) {
	this.obj = obj;
	this.hidden = false;
	if (!this.obj.is("button")) {
		this.hidden = true;// 如果不是button说明对应隐藏了
	}
	this.btn_type = "";// 按钮类型
	if (!this.hidden) {
		this.btn_type = this.obj.attr("_btn_type");
		if (this.btn_type = "disabled") {
			this.btn_type = "";
		}
	}
};

/**
 * 是否正常
 */
ButtonObj.prototype.isEnabled = function() {
	if ("disabled" == this.obj.attr("disabled")) {
		return false;
	} else {
		return true;
	}
};

/**
 * 按钮启用；
 * 
 * @return
 */
ButtonObj.prototype.enable = function() {
	if (this.isEnabled()) {
		return;
	}
	this.obj.removeAttr("disabled");
	this.obj.removeClass("layui-btn-disabled");
	if (!chkObjNull(this.btn_type)) {
		this.obj.addClass("layui-btn-" + this.btn_type);
	}
};

/**
 * 按钮禁用；
 * 
 * @return
 */
ButtonObj.prototype.disable = function() {
	if (!this.isEnabled()) {
		return;
	}
	this.obj.attr("disabled", "disabled");
	if (!chkObjNull(this.btn_type)) {
		this.obj.removeClass("layui-btn-" + this.btn_type);
	}
	this.obj.addClass("layui-btn-disabled");
};

/**
 * 启用和不启用的切换
 */
ButtonObj.prototype.toggle = function() {
	if (this.isEnabled()) {
		this.disable();
	} else {
		this.enable();
	}
};

/**
 * 按钮禁用；
 * 
 * @return
 */
ButtonObj.prototype.focus = function() {
	if (this.isEnabled()) {
		this.obj.focus();
	}
};