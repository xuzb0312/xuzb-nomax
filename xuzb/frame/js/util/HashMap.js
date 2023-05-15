/**
 * 用来存储hashMap对象
 * 
 * @author yjc
 */
// 创建一个Map
function HashMap(jsondata) {
	if ("string" == typeof jsondata) {
		this.values = eval("(" + jsondata + ")");
	} else if ("object" == typeof jsondata) {
		this.values = jsondata;
	} else if ("undefined" != typeof jsondata) {
		throw new Error("对于HashMap的初始化数据jsondata只允许为json字符串和{}格式的数据对象");
	} else {
		this.values = {};// 可以做json对象
	}
};

// 清空一个map
HashMap.prototype.clear = function() {
	this.values = {};
};

// 判断是否存在关键字
HashMap.prototype.containsKey = function(key) {
	return typeof (this.values[key]) != "undefined";
};

// 判断是否存在值
HashMap.prototype.containsValue = function(value) {
	for ( var key in this.values) {
		var val = this.values[key];
		if (val == value) {
			return true;
		}
	}
	return false;
};

// 返回一个数值
HashMap.prototype.get = function(key) {
	if (this.containsKey(key)) {
		return this.values[key];
	} else {
		return null;
	}
};

// 查看是否为空
HashMap.prototype.isEmpty = function() {
	return !this.size();
};

// 返回key的数组
HashMap.prototype.keySet = function() {
	var result = [];
	for ( var key in this.values) {
		result.push(key);
	}
	return result;
};

// 放入一个值
HashMap.prototype.put = function(key, value) {
	this.values[key] = value;
	return value;
};

// 合并一个Map
HashMap.prototype.combine = function(map) {
	var values = map.values;
	for ( var key in values) {
		var value = values[key];
		this.values[key] = value;
	}
};

// 移除一个值
HashMap.prototype.remove = function(key) {
	var value = this.containsKey(key) ? this.get(key) : undefined;
	if (value || (typeof (value) == "number" && value == 0)) {
		delete this.values[key];
	}
	return value;
};

// 获取hashMap的值
HashMap.prototype.size = function() {
	var count = 0;
	for ( var key in this.values) {
		count = count + 1;
	}
	return count;
};