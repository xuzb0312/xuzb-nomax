/**
 * 创建文本展示的对象
 * 
 * @param obj
 * @return
 */
function TextObj(obj) {
	this.obj = obj;
}

/**
 * 改变颜色
 * 
 * @param color
 * @return
 */
TextObj.prototype.setColor = function(color) {
	return this.obj.css("color", color);
}

/**
 * 设置文本内容
 * 
 * @return
 */
TextObj.prototype.setValue = function(value) {
	return this.obj.text(value);
}

/**
 * 获取文本内容
 * 
 * @return
 */
TextObj.prototype.getValue = function() {
	return this.obj.text();
}

/**
 * 清空数据
 * 
 * @return
 */
TextObj.prototype.clear = function() {
	return this.setValue("");
}