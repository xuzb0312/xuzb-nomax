/**
 * 多选框
 * 
 * @param obj
 * @return
 */
function MultiSelectBoxObj(obj) {
	this.obj = obj;
	var objid = this.obj.attr("id");
	var form = this.obj.parents("form").first();
	this.labelobj = form.find("#" + objid + "_label");// label的对象
	this.unselectedobj = form.find("#" + objid + "_unselected");
	this.selectedobj = form.find("#" + objid + "_selected");
	this.selectAllBtnobj = form.find("#" + objid + "_selectAllBtn");
	this.selectBtnobj = form.find("#" + objid + "_selectBtn");
	this.unSelectBtnobj = form.find("#" + objid + "_unSelectBtn");
	this.unSelectAllBtnobj = form.find("#" + objid + "_unSelectAllBtn");
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
MultiSelectBoxObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 改变文本值颜色
 * 
 * @param color
 * @return
 */
MultiSelectBoxObj.prototype.setColor = function(color) {
	this.unselectedobj.css("color", color);
	this.selectedobj.css("color", color);
	return true;
};

/**
 * 设置label文本内容
 * 
 * @return
 */
MultiSelectBoxObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
MultiSelectBoxObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
MultiSelectBoxObj.prototype.isValid = function() {
	// 对于dropdownlist的验证，仅限于是否允许为空的限制
	var required = this.obj.attr("_required");
	if ("true" == required) {
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
MultiSelectBoxObj.prototype.validate = function() {
	var isvalid = this.isValid();
	if (!isvalid) {
		this.unselectedobj.removeClass("mulitselectbox");
		this.unselectedobj.addClass("mulitselectbox-invlalid");
		this.selectedobj.removeClass("mulitselectbox");
		this.selectedobj.addClass("mulitselectbox-invlalid");
	} else {
		this.unselectedobj.removeClass("mulitselectbox-invlalid");
		this.unselectedobj.addClass("mulitselectbox");
		this.selectedobj.removeClass("mulitselectbox-invlalid");
		this.selectedobj.addClass("mulitselectbox");
	}
	return isvalid;
};

/**
 * 设置设置是否必录
 */
MultiSelectBoxObj.prototype.setRequired = function(required) {
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
MultiSelectBoxObj.prototype.chkValue = function() {
	return this.validate();
};

/**
 * 处理其默认事件问题，不提供给外部调用-在控件加载完成后，自动附加事件操作
 */
MultiSelectBoxObj.prototype.init = function() {
	this.selectAllBtnobj.click(function() {
		var table = $(this).parents("table").first();
		var objid = table.attr("id");
		var opt = table.find("#" + objid + "_unselected option");
		table.find("#" + objid + "_selected").append(opt);
		getObject(table).chkValue();
	});
	this.selectBtnobj.click(function() {
		var table = $(this).parents("table").first();
		var objid = table.attr("id");
		var opt = table.find("#" + objid + "_unselected option:selected");
		table.find("#" + objid + "_selected").append(opt);
		getObject(table).chkValue();
	});
	this.unSelectAllBtnobj.click(function() {
		var table = $(this).parents("table").first();
		var objid = table.attr("id");
		var opt = table.find("#" + objid + "_selected option");
		table.find("#" + objid + "_unselected").append(opt);
		getObject(table).chkValue();
	});
	this.unSelectBtnobj.click(function() {
		var table = $(this).parents("table").first();
		var objid = table.attr("id");
		var opt = table.find("#" + objid + "_selected option:selected");
		table.find("#" + objid + "_unselected").append(opt);
		getObject(table).chkValue();
	});

	// 默认加载完成后，进行一次验证
	this.validate();
};

/**
 * 置焦点
 * 
 * @return
 */
MultiSelectBoxObj.prototype.focus = function() {
	return this.unselectedobj.focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
MultiSelectBoxObj.prototype.setReadOnly = function(readonly) {
	var readonlynow = this.getReadOnly();
	if (readonly == readonlynow) {
		return;
	}
	if (readonly) {
		this.unselectedobj.attr("readonly", "readonly");
		this.unselectedobj.css("background", "#F9F9F9");
		this.selectedobj.attr("readonly", "readonly");
		this.selectedobj.css("background", "#F9F9F9");

		this.selectAllBtnobj.attr("disabled", "disabled");
		this.selectBtnobj.attr("disabled", "disabled");
		this.unSelectBtnobj.attr("disabled", "disabled");
		this.unSelectAllBtnobj.attr("disabled", "disabled");
	} else {
		this.unselectedobj.removeAttr("readonly");
		this.unselectedobj.css("background", "#FFFFFF");
		this.selectedobj.removeAttr("readonly");
		this.selectedobj.css("background", "#FFFFFF");

		this.selectAllBtnobj.removeAttr("disabled");
		this.selectBtnobj.removeAttr("disabled");
		this.unSelectBtnobj.removeAttr("disabled");
		this.unSelectAllBtnobj.removeAttr("disabled");
	}
};

/**
 * 获取只读
 * 
 * @param readonly
 * @return
 */
MultiSelectBoxObj.prototype.getReadOnly = function() {
	var readonlynow = ("readonly" == this.unselectedobj.attr("readonly"));
	return readonlynow;
};

/**
 * 设置文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
MultiSelectBoxObj.prototype.setValue = function(value) {
	var htmlselect = "";
	var htmlunselect = "";
	var opts = this.obj.attr("data-opt");
	var optsList = new List(opts);
	var optsJson = optsList.values;
	var valueText = "";
	var arrValue = value.split(",");
	for ( var j = 0, m = optsJson.length; j < m; j++) {
		var optJson = optsJson[j];
		var isSelect = false;
		for ( var i = 0, n = arrValue.length; i < n; i++) {
			var oneV = arrValue[i];
			if (oneV == optJson.code) {
				isSelect = true;
			}
		}
		if (isSelect) {
			htmlselect = htmlselect + "<option value=\"" + optJson.code + "\">"
					+ optJson.content + "</option>";
		} else {
			htmlunselect = htmlunselect + "<option value=\"" + optJson.code
					+ "\">" + optJson.content + "</option>";
		}
	}
	this.selectedobj.html(htmlselect);
	this.unselectedobj.html(htmlunselect);

	this.validate();
	return true;
};

/**
 * 全选
 * 
 * @return
 */
MultiSelectBoxObj.prototype.selectAll = function(value) {
	var opt = this.unselectedobj.find("option");
	this.selectedobj.append(opt);
	this.chkValue();
	return this.getValue();
};

/**
 * 获取value
 * 
 * @return
 */
MultiSelectBoxObj.prototype.getValue = function() {
	var seleValArr = [];
	this.selectedobj.find("option").each(function() {
		var key = $(this).val();
		if (chkObjNull(key)) {
			return;
		}
		seleValArr.push(key);
	});

	return seleValArr.join(",");
};

/**
 * 获取文本内容--文字
 * 
 * @return
 */
MultiSelectBoxObj.prototype.getText = function() {
	var seleTextArr = [];
	this.selectedobj.find("option").each(function() {
		var text = $(this).text();
		if (chkObjNull(text)) {
			return;
		}
		seleTextArr.push(text);
	});

	return seleTextArr.join(",");
};

/**
 * 数据清空
 */
MultiSelectBoxObj.prototype.clear = function() {
	this.setValue("");
};