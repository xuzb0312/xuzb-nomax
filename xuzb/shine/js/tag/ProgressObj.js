/**
 * 进度条对象
 */
function ProgressObj(obj) {
	this.obj = obj;
	this.obj_data = this.obj.data("obj_data");// 后台写过来的-标签数据HashMap数据
};

/**
 * 初始化
 * 
 * @return
 */
ProgressObj.prototype.init = function(mapData) {
	// 参数初始化
	this.obj_data = mapData;
	this.obj.data("obj_data", mapData);

	var objid = this.obj.attr("id");
	layui.use("element", function() {
		var element = layui.element;
		element.render("progress", objid);
	});
};

/**
 * 设置百分比
 * 
 * @param percent
 * @return
 */
ProgressObj.prototype.setPercent = function(percent) {
	if (percent < 0) {
		percent = 0;
	}
	if (percent > 100) {
		percent = 100;
	}

	this.obj_data.put("percent", percent);
	this.obj.data("obj_data", this.obj_data);
	var objid = this.obj.attr("id");
	layui.use("element", function() {
		var element = layui.element;
		element.progress(objid, percent + "%");
	});
};

/**
 * 获取百分比
 * 
 * @return
 */
ProgressObj.prototype.getPercent = function() {
	return this.obj_data.get("percent");
};

/**
 * 原有基础上增加-默认1
 * 
 * @param value
 * @return
 */
ProgressObj.prototype.add = function(value) {
	if (chkObjNull(value)) {
		value = 1;
	}
	var percent = this.getPercent() + value;
	this.setPercent(percent);
};