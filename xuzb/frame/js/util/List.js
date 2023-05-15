/**
 * 用来存储List对象
 * 
 * @author yjc
 */
// 创建list对象
function List(jsondata) {
	if ("string" == typeof jsondata) {
		this.values = eval("(" + jsondata + ")");
	} else if ("object" == typeof jsondata) {
		this.values = jsondata;
	} else if ("undefined" != typeof jsondata) {
		throw new Error("对于List的初始化数据jsondata只允许为json字符串和{}格式的数据对象");
	} else {
		this.values = [];// 可以做json对象
	}
}

// 数据情况
List.prototype.clear = function() {
	this.values = [];
}

// 大小
List.prototype.size = function() {
	return this.values.length;
}

// 增加一行数据,返回总行数
List.prototype.add = function(data) {
	return this.values.push(data);
}

// 返回一行数据
List.prototype.get = function(i) {
	return this.values[i];
}

// 合并
List.prototype.combine = function(list) {
	var values = list.values;
	for ( var i = 0, n = values.length; i < n; i++) {
		this.add(values[i]);
	}
}