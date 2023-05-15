/**
 * 对于请求路径的包装其中包含了请求的地址和数据-可以直接是字符串路径，也可以2个参数，controller+metho
 * 
 * @author yjc
 */
function URL(urlstr, method) {
	if (arguments.length == 1) {
		this.urlStr = urlstr;
	} else if (arguments.length == 2) {
		this.urlStr = urlstr + "?method=" + method;// 请求路径
	} else {
		alert("传入的参数个数不正确");
		throw new Error("传入的参数个数不正确");
	}
	this.paras = new HashMap();// 数据
};

/**
 * 获取请求路径
 */
URL.prototype.getURLString = function() {
	return this.urlStr;
};

/**
 * 获取请求路径-携带参数数据的。
 */
URL.prototype.getRealURLString = function() {
	var parasStr = "";
	var firstLinkChar = "&";
	if (!this.urlStr.contains("?")) {
		firstLinkChar = "?";
	}
	var keys = this.paras.keySet();
	for ( var i = 0, n = keys.length; i < n; i++) {
		if (i == 0) {
			parasStr = firstLinkChar + keys[i] + "=" + this.paras.get(keys[i]);
		} else {
			parasStr = parasStr + "&" + keys[i] + "=" + this.paras.get(keys[i]);
		}
	}
	return this.urlStr + parasStr;
};

/**
 * 获取pdf打印的url地址
 */
URL.prototype.getPdfURLString = function() {
	var pdfviewer = "./frame/plugins/pdfjs/web/viewer.html";
	var urlStr = GlobalVars.BASE_PATH + this.getRealURLString();
	var pdfviewHref = pdfviewer + "?rd=" + randomString(6) + "&file="
			+ encodeURIComponent(urlStr);
	return pdfviewHref;
};

URL.prototype.getParas = function() {
	return this.paras.values;
};

URL.prototype.addPara = function(key, value) {
	if ("undefined" == typeof value) {
		return this.paras.put(key, getObject(key).getValue());
	} else {
		return this.paras.put(key, value);
	}
};

URL.prototype.addFilePara = function(key, value) {
	return this.paras.put("__file_" + key, value);
};

URL.prototype.addMap = function(map) {
	this.paras.combine(map);
};

/**
 * 对于带checkbox的树进行值的获取，并放到url中
 * 
 */
URL.prototype.addTreePara = function(treeName, keyname) {
	var treeObj = getObject(treeName);
	var value = treeObj.getCheckedValue();
	if (chkObjNull(keyname)) {
		this.addPara(treeName, value);
	} else {
		this.addPara(keyname, value);
	}
};

/**
 * 增加form
 * 
 * @param form
 * @return
 */
URL.prototype.addForm = function(form) {
	this.addMap(getObject(form).getMapData());// 增加form参数
};

/**
 * 增加所有的grid的数据
 * 
 * @return
 */
URL.prototype.addListData = function(key, data, colNames) {
	if (chkObjNull(key)) {
		alert("传入的关键字为空");
		return false;
	}
	if (chkObjNull(colNames)) {
		this.addPara("__grid_" + key, JSON.stringify( {
			griddata : data
		}));
		return true;
	}

	// 新数据
	var colArr = colNames.split(",");
	var dataNew = [];
	for ( var i = 0, n = data.length; i < n; i++) {
		var rowJson = {};
		for ( var j = 0, m = colArr.length; j < m; j++) {
			var colName = colArr[j];
			var rowDataJson = data[i];
			rowJson[colName] = rowDataJson[colName];
		}
		dataNew.push(rowJson);
	}
	this.addPara("__grid_" + key, JSON.stringify( {
		griddata : dataNew
	}));

	return true;
};

/**
 * 增加所有的grid的数据
 * 
 * @return
 */
URL.prototype.addQueryGridAllData = function(grid, colNames, key) {
	var data = getObject(grid).getData();
	if (chkObjNull(key)) {
		this.addListData(grid, data, colNames);
	} else {
		this.addListData(key, data, colNames);
	}
};

/**
 * 增加所有的grid的当前数据，经过检索后的
 * 
 * @return
 */
URL.prototype.addQueryGridCurrentData = function(grid, colNames, key) {
	var data = getObject(grid).getCurrentData();
	if (chkObjNull(key)) {
		this.addListData(grid, data, colNames);
	} else {
		this.addListData(key, data, colNames);
	}
};

/**
 * 增加所有的grid的当前数据，经过检索后的
 * 
 * @return
 */
URL.prototype.addQueryGridSelectData = function(grid, colNames, key) {
	var data = getObject(grid).getSelectData();
	if (chkObjNull(key)) {
		this.addListData(grid, data, colNames);
	} else {
		this.addListData(key, data, colNames);

	}
};