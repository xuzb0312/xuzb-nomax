/**
 * LayoutObj布局对象-Obj
 * 
 * @author yjc
 * @return
 */
function LayoutObj(obj) {
	this.obj = obj;
}

/**
 * 收起面板-左侧
 * 
 * @return
 */
LayoutObj.prototype.collapseLeft = function() {
	return this.obj.layout("collapse", "west");
}

/**
 * 收起面板-右侧
 * 
 * @return
 */
LayoutObj.prototype.collapseRight = function() {
	return this.obj.layout("collapse", "east");
}

/**
 * 收起面板-头部
 * 
 * @return
 */
LayoutObj.prototype.collapseTop = function() {
	return this.obj.layout("collapse", "north");
}

/**
 * 收起面板-底侧
 * 
 * @return
 */
LayoutObj.prototype.collapseBottom = function() {
	return this.obj.layout("collapse", "south");
}

/**
 * 展开面板-左侧
 * 
 * @return
 */
LayoutObj.prototype.expandLeft = function() {
	return this.obj.layout("expand", "west");
}

/**
 * 展开面板-右侧
 * 
 * @return
 */
LayoutObj.prototype.expandRight = function() {
	return this.obj.layout("expand", "east");
}

/**
 * 展开面板-头部
 * 
 * @return
 */
LayoutObj.prototype.expandTop = function() {
	return this.obj.layout("expand", "north");
}

/**
 * 展开面板-底侧
 * 
 * @return
 */
LayoutObj.prototype.expandBottom = function() {
	return this.obj.layout("expand", "south");
}