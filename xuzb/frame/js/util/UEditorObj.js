/**
 * 富文本编辑器
 * 
 */
function UEditorObj(obj) {
	this.obj = obj;
	var objid = this.obj.attr("id");
	this.containerObj = this.obj.data("container");
	var form = this.obj.parents("form").first();
	this.labelobj = form.find("#" + objid + "_label");// label的对象
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
UEditorObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
UEditorObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
UEditorObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
UEditorObj.prototype.isValid = function() {
	var required = this.obj.attr("_required");
	if ("true" == required) {
		return this.containerObj.hasContents();
	} else {
		return true;
	}
};

/**
 * 验证控件的数据合法行，并返回是否合法
 * 
 * @return
 */
UEditorObj.prototype.validate = function() {
	if (this.isValid()) {
		return true;
	} else {
		this.focus();
		return false;
	}
};

/**
 * 设置设置是否必录
 */
UEditorObj.prototype.setRequired = function(required) {
	if (chkObjNull(required)) {
		required = false;
	}
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
UEditorObj.prototype.chkValue = function() {
	return this.validate();
};

/**
 * 置焦点
 * 
 * @return
 */
UEditorObj.prototype.focus = function() {
	return this.containerObj.focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
UEditorObj.prototype.setReadOnly = function(readonly) {
	if (readonly) {
		this.containerObj.setDisabled();
	} else {
		this.containerObj.setEnabled();
	}
	return true;
};

/**
 * 设置文本内容
 * 
 * @return
 */
UEditorObj.prototype.setValue = function(value) {
	return this.containerObj.setContent(value);
};

/**
 * 获取文本内容
 * 
 * @return
 */
UEditorObj.prototype.getValue = function() {
	return this.containerObj.getContent();
};

/**
 * 数据清空
 */
UEditorObj.prototype.clear = function() {
	return this.setValue("");
};

/**
 * Word转存功能
 */
UEditorObj.prototype.wordParse = function() {
	var editorobj_id = this.obj.attr("id");
	var url = new URL("taglib.do", "fwdWordParsePage");
	openWindow(
			"选择WORD文件",
			"icon-folder",
			url,
			450,
			210,
			function(data) {
				if (!chkObjNull(data)) {
					openTopWindow(
							"WORD文件预览 <span style='color:red;'>您可以通过复制的方式将展示内容复制到富文本编辑框中</span>",
							"taglib.do?method=fwdWordParseResultView&uuid="
									+ data, "big")
				}
			});
};