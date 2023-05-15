/**
 * 文件上传对象
 */
function FileBoxObj(obj) {
	this.obj = obj;
	this.id = this.obj.attr("id");
	this.uiid = this.obj.attr("_uiid");
	this.autoUpload = ("true" == this.obj.attr("_autoUpload"));
	this.required = ("true" == this.obj.attr("_required"));
	this.fileType = this.obj.attr("_fileType");
	this.fileDesc = this.obj.attr("_fileDesc");
	this.sizeLimit = new Number(this.obj.attr("_sizeLimit")); // 文件最大
	this.onSelectFile = this.obj.attr("_onselectfile");
	var form = this.obj.parents("form").first();// 由于一个页面可能存在多个同名标签，所以限定范围
	this.labelobj = form.find("#" + this.id + "_label");// label的对象
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
FileBoxObj.prototype.init = function() {
	var tagid = this.id;

	// 文件清空事件
	$("#" + this.id + "_clear_btn").bind("click", function(e) {
		getObject(tagid).clear();
	});

	// 文件选择
	$("#" + this.id + "_select").bind("click", function(e) {
		if (!getObject(tagid).getReadOnly()) {
			getObject(tagid).openSelectFileWindow4H5();
		}
	});

	// 文件选择-H5方式
	$("#" + this.id + "_flash_select_btn").bind("click", function(e) {
		if (!getObject(tagid).getReadOnly()) {
			getObject(tagid).openSelectFileWindow();
		}
	});
	
	// 文件选择-传统方式文件选择
	$("#" + this.id + "_tra_select_btn").bind("click", function(e) {
		if (!getObject(tagid).getReadOnly()) {
			getObject(tagid).openSelectFileWindow4Tra();
		}
	});
};

/**
 * 打开文件选择的页面HTML5方式新版支持
 * 
 * @param color
 * @return
 */
FileBoxObj.prototype.openSelectFileWindow4H5 = function() {
	var tagid = this.id;
	var onSelectFileTmp = this.onSelectFile;
	var url = new URL("taglib.do", "fwdSelectFileWindow4H5");
	url.addPara("id", this.id);
	url.addPara("uiid", this.uiid);
	url.addPara("autoupload", this.autoUpload);
	url.addPara("required", this.required);
	url.addPara("filetype", this.fileType);
	url.addPara("filedesc", this.fileDesc);
	url.addPara("sizelimit", this.sizeLimit);
	this.sizeLimit = new Number(this.obj.attr("_sizeLimit")); // 文件最大
	openTopWindow("选择文件", "icon-folder", url.getRealURLString(), 900, 500, function(data) {
		if (!chkObjNull(data)) {
			var fileobj = getObject(data.id);
			fileobj.obj.val(data.name);
			fileobj.validate();

			// 触发选中文件事件
			if (!chkObjNull(onSelectFileTmp)) {
				onSelectFileTmp = new Function("fileName", "return "
						+ onSelectFileTmp + "(fileName);");
				onSelectFileTmp(data.name);
			}
		}
	});
};

/**
 * 打开文件选择的页面
 * 
 * @param color
 * @return
 */
FileBoxObj.prototype.openSelectFileWindow = function() {
	var tagid = this.id;
	var onSelectFileTmp = this.onSelectFile;
	var url = new URL("taglib.do", "fwdSelectFileWindow");
	url.addPara("id", this.id);
	url.addPara("uiid", this.uiid);
	url.addPara("autoupload", this.autoUpload);
	url.addPara("required", this.required);
	url.addPara("filetype", this.fileType);
	url.addPara("filedesc", this.fileDesc);
	url.addPara("sizelimit", this.sizeLimit);
	this.sizeLimit = new Number(this.obj.attr("_sizeLimit")); // 文件最大
	openTopWindow("选择文件", "icon-folder", url, 470, 360, function(data) {
		if (!chkObjNull(data)) {
			var fileobj = getObject(data.id);
			fileobj.obj.val(data.name);
			fileobj.validate();

			// 触发选中文件事件
			if (!chkObjNull(onSelectFileTmp)) {
				onSelectFileTmp = new Function("fileName", "return "
						+ onSelectFileTmp + "(fileName);");
				onSelectFileTmp(data.name);
			}
		}
	});
};

/**
 * 打开文件选择的页面
 * 
 * @param color
 * @return
 */
FileBoxObj.prototype.openSelectFileWindow4Tra = function() {
	var tagid = this.id;
	var onSelectFileTmp = this.onSelectFile;
	var url = new URL("taglib.do", "fwdSelectFileWindow4Tra");
	url.addPara("id", this.id);
	url.addPara("uiid", this.uiid);
	url.addPara("autoupload", this.autoUpload);
	url.addPara("required", this.required);
	url.addPara("filetype", this.fileType);
	url.addPara("filedesc", this.fileDesc);
	url.addPara("sizelimit", this.sizeLimit);
	this.sizeLimit = new Number(this.obj.attr("_sizeLimit")); // 文件最大
	openTopWindow("选择文件", "icon-folder", url, 470, 360, function(data) {
		if (!chkObjNull(data)) {
			var fileobj = getObject(data.id);
			fileobj.obj.val(data.name);
			fileobj.validate();

			// 触发选中文件事件
			if (!chkObjNull(onSelectFileTmp)) {
				onSelectFileTmp = new Function("fileName", "return "
						+ onSelectFileTmp + "(fileName);");
				onSelectFileTmp(data.name);
			}
		}
	});
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
FileBoxObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
FileBoxObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
FileBoxObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
FileBoxObj.prototype.isValid = function() {
	if (this.required) {
		var value = this.getValue();
		if (chkObjNull(value)) {
			return false;
		}
	}

	return true;
};

/**
 * 验证控件的数据合法行，并返回是否合法
 * 
 * @return
 */
FileBoxObj.prototype.validate = function() {
	var isvalid = this.isValid();
	if (isvalid) {
		this.obj.css("border", "1px solid #95B8E7");
	} else {
		this.obj.css("border", "1px solid #ff8400");
	}
	return isvalid;
};

/**
 * 设置设置是否必录
 */
FileBoxObj.prototype.setRequired = function(required) {
	if (chkObjNull(required)) {
		required = false;
	}
	this.required = required;
	if (required) {
		this.obj.attr("_required", "true")
	} else {
		this.obj.attr("_required", "false")
	}
	this.validate();
	return true;
};

/**
 * 检测数据合法性
 * 
 * @return
 */
FileBoxObj.prototype.chkValue = function() {
	return this.validate();
};

/**
 * 置焦点
 * 
 * @return
 */
FileBoxObj.prototype.focus = function() {
	return $("#" + this.id + "_select").focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
FileBoxObj.prototype.setReadOnly = function(readonly) {
	var selObj = $("#" + this.id + "_select");
	selObj.data("readonly", readonly); // readonly放入data中
	if (readonly) {
		selObj.splitbutton("disable");
	} else {
		selObj.splitbutton("enable");
	}
	return true;
};

/**
 * 获取只读
 * 
 * @param readonly
 * @return
 */
FileBoxObj.prototype.getReadOnly = function() {
	var selObj = $("#" + this.id + "_select");
	var readonly = selObj.data("readonly"); // readonly放入data中
	if (chkObjNull(readonly)) {
		// 如果获取不到，则是非只读的
		return false;
	}
	return readonly;
};

/**
 * 获取文本内容
 * 
 * @return
 */
FileBoxObj.prototype.getValue = function() {
	if (!chkObjNull(this.getFilePath())) {
		return this.uiid; // value值为后台的uiid
	} else {
		return ""; // value值为后台的uiid
	}
};

/**
 * 文件下载
 */
FileBoxObj.prototype.downloadFile = function() {
	var url = this.getDownloadPath();
	if (chkObjNull(url)) {
		return "";
	}
	downloadFile2Form(url);
};

/**
 * 获取文件下载地址
 * 
 * @return
 */
FileBoxObj.prototype.getDownloadPath = function() {
	if (chkObjNull(this.getFilePath())) {
		return "";
	}
	var url = new URL("taglib.do", "downloadUploadFile4FileBox");
	url.addPara("uiid", this.uiid);
	url.addPara("__jbjgid", getJbjgid("00000000"));
	url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
	url.addPara("__yhid", getPageYhid());
	url.addPara("__random", randomString(5));
	return url.getRealURLString();
};

/**
 * 文件类型
 * 
 * @return
 */
FileBoxObj.prototype.getFilePath = function() {
	return this.obj.val(); // 获取文件路径
};

/**
 * 数据清空
 */
FileBoxObj.prototype.clear = function() {
	if (!chkObjNull(this.getFilePath())) {
		this.obj.val(""); // 数据清空
		// 服务端如果存在数据的则进行清空
		var url = new URL("taglib.do", "clearUploadFile4FileBox");
		url.addPara("uiid", this.uiid);
		AjaxUtil.asyncRequest(url, function(data) {
			if (AjaxUtil.checkException(data)) {
				AjaxUtil.showException(data);
				return;
			}
		});

		this.validate();
		return true;
	}
	return false;
};