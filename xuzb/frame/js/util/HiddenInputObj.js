/**
 * 隐藏框的数据对象
 */
function HiddenInputObj(obj) {
	this.obj = obj;
}

/**
 * 获取值
 * 
 * @return
 */
HiddenInputObj.prototype.getValue = function() {
	return this.obj.val();
}

/**
 * 设置值
 * 
 * @param value
 * @return
 */
HiddenInputObj.prototype.setValue = function(value) {
	return this.obj.val(value);
}

/**
 * 清空值
 * 
 * @param value
 * @return
 */
HiddenInputObj.prototype.clear = function() {
	return this.setValue("");
}

/**
 * 数据检测
 * 
 * @param value
 * @return
 */
HiddenInputObj.prototype.chkValue = function() {
	return true;// 默认全部合法，没有检测，兼容formObj的操作
}

/**
 * 置焦点
 * 
 * @param value
 * @return
 */
HiddenInputObj.prototype.focus = function() {
	return true;// 兼容formObj的操作
}
