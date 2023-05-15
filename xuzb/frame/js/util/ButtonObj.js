/**
 * 按钮对象
 * 
 * @author yjc
 * @return
 */
function ButtonObj(obj, type) {
	this.obj = obj;
	this.type = type;
}

/**
 * 禁用按钮
 * 
 * @return
 */
ButtonObj.prototype.disable = function() {
	if (this.type == JQueryObjType.splitbutton) {
		this.obj.splitbutton("disable");
	} else if (this.type == JQueryObjType.menubutton) {
		this.obj.menubutton("disable");
	} else if (this.type == JQueryObjType.linkbutton) {
		this.obj.linkbutton("disable");
	} else if (this.type == JQueryObjType.hiddenbutton) {
		return true;// 无权限按钮
	} else {
		throw new Error("该标签不支持此方法");
	}
}

/**
 * 启用按钮
 * 
 * @return
 */
ButtonObj.prototype.enable = function() {
	if (this.type == JQueryObjType.splitbutton) {
		this.obj.splitbutton("enable");
	} else if (this.type == JQueryObjType.menubutton) {
		this.obj.menubutton("enable");
	} else if (this.type == JQueryObjType.linkbutton) {
		this.obj.linkbutton("enable");
	} else if (this.type == JQueryObjType.hiddenbutton) {
		return true;// 无权限按钮
	}else {
		throw new Error("该标签不支持此方法");
	}
}

/**
 * 设置焦点
 * 
 * @return
 */
ButtonObj.prototype.focus = function() {
	if (this.type == JQueryObjType.hiddenbutton) {
		return true;// 无权限按钮
	}
	this.obj.focus();
}