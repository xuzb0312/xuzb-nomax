/**
 * Tree树对象-Obj
 * 
 * @author yjc
 * @return
 */
function TreeObj(obj) {
	this.obj = obj;
}

/**
 * 加载数据-data:加载树的数据-data为数组数据
 */
TreeObj.prototype.loadData = function(data) {
	return this.obj.tree("loadData", data);
}

/**
 * 清空树
 */
TreeObj.prototype.clear = function() {
	this.loadData( []);
}

/**
 * 加载远程构建树
 * 
 * @param className
 *            类
 * @param map
 *            hashmap格式参数
 * @return
 */
TreeObj.prototype.loadRemotData = function(className, map) {
	var url = new URL("taglib.do", "initTreeData");
	url.addMap(map);
	url.addPara("__classname", className);
	url.addPara("__jbjgid", getJbjgid("00000000"));
	url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
	url.addPara("__yhid", getPageYhid());
	var data = AjaxUtil.syncRequest(url);
	if (AjaxUtil.checkException(data)) {
		AjaxUtil.showException(data);
		return;
	}
	var list = new List(data);// 数组数据
	return this.loadData(list.values);
}

/**
 * 异步加载远程构建树
 * 
 * @param className
 *            类
 * @param map
 *            hashmap格式参数
 * @return
 */
TreeObj.prototype.asyncLoadRemotData = function(className, map, callback) {
	var treeObj = this;
	var url = new URL("taglib.do", "initTreeData");
	url.addMap(map);
	url.addPara("__classname", className);
	url.addPara("__jbjgid", getJbjgid("00000000"));
	url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
	url.addPara("__yhid", getPageYhid());

	// 正在加载
	this.loadData( [ {
		"id" : "loading",
		"text" : "正在加载...",
		"iconCls" : "icon-arrow-refresh-small"
	} ]);
	AjaxUtil.asyncRequest(url, function(data) {
		if (AjaxUtil.checkException(data)) {
			// 出错
			AjaxUtil.showException(data);
			treeObj.clear();
			return;
		}
		var list = new List(data)
		treeObj.loadData(list.values);
		if ("undefined" != typeof callback && null != callback
				&& "" != callback) {
			callback(treeObj);
		}
	});
	return true;
}

/**
 * 获取选中的节点的node信息 <br>
 * node信息包含：<br>
 * id：绑定到节点的标识值。 <br>
 * text：显示的文字。<br>
 * checked：是否节点被选中。<br>
 * attributes：绑定到节点的自定义属性。<br>
 * target：目标的 DOM 对象。
 * 
 * @return
 */
TreeObj.prototype.getSelected = function() {
	return this.obj.tree("getSelected");
}

/**
 * 返回的是选中的Node的数组
 * 
 * @return
 */
TreeObj.prototype.getChecked = function() {
	return this.obj.tree("getChecked");
}

/**
 * 获取value-选中的。
 * 
 * @return
 */
TreeObj.prototype.getCheckedValue = function() {
	var nodes = this.getChecked();
	var value = '';
	for ( var i = 0; i < nodes.length; i++) {
		if (value != '') {
			value += ',';
		}
		value += nodes[i].id;
	}
	return value;
}

/**
 * 获取Text-选中的。
 * 
 * @return
 */
TreeObj.prototype.getCheckedText = function() {
	var nodes = this.getChecked();
	var text = '';
	for ( var i = 0; i < nodes.length; i++) {
		if (text != '') {
			text += ',';
		}
		text += nodes[i].text;
	}
	return text;
}

/**
 * 折叠所有节点
 * 
 * @return
 */
TreeObj.prototype.collapseAll = function() {
	return this.obj.tree("collapseAll");
}

/**
 * 展开所有节点
 * 
 * @return
 */
TreeObj.prototype.expandAll = function() {
	return this.obj.tree("expandAll");
}

/**
 * 找到指定的节点并返回此节点对象。
 * 
 * @return
 */
TreeObj.prototype.find = function(id) {
	return this.obj.tree("find", id);
}

/**
 * 根据target选中一个节点
 * 
 * @return
 */
TreeObj.prototype.select = function(target) {
	return this.obj.tree("select", target);
}

/**
 * 根据id选中一个节点
 * 
 * @param id
 * @return
 */
TreeObj.prototype.selectById = function(id) {
	var target = this.find(id).target;
	return this.select(target);
}

/**
 * 根据target折叠该节点
 * 
 * @return
 */
TreeObj.prototype.collapse = function(target) {
	return this.obj.tree("collapse", target);
}

/**
 * 根据target展开该节点
 * 
 * @return
 */
TreeObj.prototype.expand = function(target) {
	return this.obj.tree("expand", target);
}

/**
 * 根据target从根部展开一个指定的节点。
 * 
 * @return
 */
TreeObj.prototype.expandTo = function(target) {
	return this.obj.tree("expandTo", target);
}

/**
 * 滚动到指定节点。该方法自版本 1.3.4 起可用。
 * 
 * @return
 */
TreeObj.prototype.scrollTo = function(target) {
	return this.obj.tree("scrollTo", target);
}

/**
 * 根据id折叠该节点
 * 
 * @return
 */
TreeObj.prototype.collapseById = function(id) {
	var target = this.find(id).target;
	return this.collapse(target);
}

/**
 * 根据id展开该节点
 * 
 * @return
 */
TreeObj.prototype.expandById = function(id) {
	var target = this.find(id).target;
	return this.expand(target);
}

/**
 * 根据id从根部展开一个指定的节点。
 * 
 * @return
 */
TreeObj.prototype.expandToById = function(id) {
	var target = this.find(id).target;
	return this.expandTo(target);
}

/**
 * 滚动到指定节点。该方法自版本 1.3.4 起可用。
 * 
 * @return
 */
TreeObj.prototype.scrollToById = function(id) {
	var target = this.find(id).target;
	return this.scrollTo(target);
}

/**
 * 折叠，打开节点
 * 
 * @return
 */
TreeObj.prototype.toggle = function(target) {
	return this.obj.tree("toggle", target);
}

/**
 * 折叠，打开节点-id
 * 
 * @return
 */
TreeObj.prototype.toggleById = function(id) {
	var target = this.find(id).target;
	return this.toggle(target);
}

/**
 * 移除
 * 
 * @return
 */
TreeObj.prototype.removeById = function(id) {
	var target = this.find(id).target;
	return this.remove(target);
}

/**
 * 移除
 * 
 * @return
 */
TreeObj.prototype.remove = function(target) {
	return this.obj.tree("remove", target);
}

/**
 * 移除,返回移除节点数据
 * 
 * @return
 */
TreeObj.prototype.popById = function(id) {
	var target = this.find(id).target;
	return this.pop(target);
}

/**
 * 移除,返回移除节点数据
 * 
 * @return
 */
TreeObj.prototype.pop = function(target) {
	return this.obj.tree("pop", target);
}

/**
 * 叶子节点
 * 
 * @return
 */
TreeObj.prototype.isLeaf = function(target) {
	return this.obj.tree("isLeaf", target);
}

/**
 * 叶子节点
 * 
 * @return
 */
TreeObj.prototype.isLeafById = function(id) {
	var target = this.find(id).target;
	return this.isLeaf(target);
}

/**
 * 增加子节点 mapData格式为标准节点字段内容[id、text、iconCls、checked，等等]
 * 
 * @return
 */
TreeObj.prototype.append = function(targetId, mapData) {
	var target = this.find(targetId).target;
	this.obj.tree("append", {
		parent : target,
		data : [ mapData.values ]
	});
	return true;
}

/**
 * 插入节点 mapData格式为标准节点字段内容[后边插入的节点]
 * 
 * @return
 */
TreeObj.prototype.insertAfter = function(targetId, mapData) {
	var target = this.find(targetId).target;
	this.obj.tree("insert", {
		after : target,
		data : mapData.values
	});
	return true;
}

/**
 * 插入节点 mapData格式为标准节点字段内容[前边插入的节点]
 * 
 * @return
 */
TreeObj.prototype.insertBefore = function(targetId, mapData) {
	var target = this.find(targetId).target;
	this.obj.tree("insert", {
		before : target,
		data : mapData.values
	});
	return true;
}

/**
 * 更新节点 mapData格式为标准节点字段内容[前边插入的节点]
 * 
 * @return
 */
TreeObj.prototype.update = function(targetId, mapData) {
	var target = this.find(targetId).target;
	mapData.put("target", target);
	this.obj.tree("update", mapData.values);
	return true;
}