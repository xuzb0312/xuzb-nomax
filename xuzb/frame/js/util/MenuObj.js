/**
 * menu菜单对象
 * 
 * @author yjc
 * @return
 */
function MenuObj(obj) {
	this.obj = obj;
};

/**
 * 隐藏菜单
 * 
 * @param name
 * @return
 */
MenuObj.prototype.hide = function() {
	return this.obj.menu("hide");
};

/**
 * 展示菜单
 * 
 * @param name
 * @return
 */
MenuObj.prototype.show = function(left, top, hideOnUnhover) {
	var map = new HashMap();
	map.put("left", left);
	map.put("top", top);
	if ("boolean" != typeof (hideOnUnhover)) {
		hideOnUnhover = false;
	}
	map.put("hideOnUnhover", hideOnUnhover)
	return this.obj.menu("show", map.values);
};

/**
 * set参数用户-一般的时候用不到
 */
MenuObj.prototype.setMapData = function(map) {
	return this.obj.data("__menuData", map);
};

/**
 * get参数-设置数据-一般的时候用不到
 */
MenuObj.prototype.getMapData = function() {
	return this.obj.data("__menuData");
};

/**
 * 清空数据
 */
MenuObj.prototype.clearMapData = function() {
	return this.obj.removeData("__menuData");
};