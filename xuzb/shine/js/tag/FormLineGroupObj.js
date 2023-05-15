/**
 * 表单行组--add.yjc.2017年12月7日
 */
function FormLineGroupObj(obj) {
	this.obj = obj;
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
FormLineGroupObj.prototype.setLabelColor = function(color) {
	return this.obj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
FormLineGroupObj.prototype.setLabelValue = function(value) {
	return this.obj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
FormLineGroupObj.prototype.getLabelValue = function() {
	var labelValue = this.obj.text();
	return labelValue;
};
