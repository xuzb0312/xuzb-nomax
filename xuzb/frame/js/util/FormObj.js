/**
 * 表单对象
 * 
 * @author yjc
 * @param obj
 * @return
 */
function FormObj(obj) {
	this.obj = obj;
	this.type = "form";
}

/**
 * 可以储存数据的子标签，类型
 */
var FORM_CAN_CACHE_DATE_TAG = new HashMap( {
	textinput : null,// 文本输入框
	dropdownlist : null,// 单选下拉框
	multidropdownlist : null,// 多选下拉框
	checkboxlist : null,// 复选框
	radiobuttonlist : null,// 单选框
	hiddeninput : null,// 隐藏框
	filebox : null,// 文件框
	ueditor : null, // 富文本
	textarea : null, // 文本域
	multiselectbox : null
});

/**
 * 加载数据-data为json对象
 * 
 * @param data
 * @return
 */
FormObj.prototype.setData = function(data, isCover) {
	return this.setMapData(new HashMap(data), isCover);
}

/**
 * 加载数据-参数为map
 * 
 * @param data
 * @return
 */
FormObj.prototype.setMapData = function(map, isCover) {
	// 加载数据前，首先进行数据清空--如果传入了isCover=true为覆盖模式的话，则不清空数据，直接进行覆盖
	if (!chkObjNull(isCover) && isCover == true) {
	} else {
		this.clearData();// 清空数据
	}
	this.obj.find("[obj_type]").each(function() {
		var obj_type = $(this).attr("obj_type");
		var id = $(this).attr("id");
		if (chkObjNull(obj_type) || chkObjNull(id) || "filebox" == obj_type) {
			// 如果这两项中有一项的无法获取的，则认为无需该项设置数据-filebox没有set方法
			return;
		}
		if (!FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
			// 如果form下该标签不能存储数据，则不管他
			return;
		}
		if (map.containsKey(id)) {
			getObject($(this)).setValue(map.get(id));
		}
	});
};

/**
 * form置焦点-对form的第一个元素进行置入焦点
 * 
 * @param data
 * @return
 */
FormObj.prototype.focus = function() {
	var isok = true;
	this.obj.find("[obj_type]").each(function() {
		if (isok) {
			var obj_type = $(this).attr("obj_type");
			var id = $(this).attr("id");
			if (chkObjNull(obj_type) || chkObjNull(id)) {
				return;
			}
			if (!FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
				return;
			}
			getObject($(this)).focus();
			isok = false;
		}
	});
	return true;
};

/**
 * 设置标签下的所有输入框的只读属性
 * 
 * @return
 */
FormObj.prototype.setReadOnly = function(readonly) {
	this.obj.find("[obj_type]").each(function() {
		var obj_type = $(this).attr("obj_type");
		var id = $(this).attr("id");
		if (chkObjNull(obj_type) || chkObjNull(id)) {
			// 如果这两项中有一项的无法获取的，则认为无需该项设置数据
			return;
		}
		if (!FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
			// 如果form下该标签不能存储数据，则不管他
			return;
		}
		if ("hiddeninput" == obj_type) {
			return;
		}
		getObject($(this)).setReadOnly(readonly);
	});
};

/**
 * 获取数据，返回json数据
 * 
 * @param data
 * @return
 */
FormObj.prototype.getData = function() {
	return this.getMapData().values;
}

/**
 * 获取数据，返回map数据
 * 
 * @param data
 * @return
 */
FormObj.prototype.getMapData = function() {
	var map = new HashMap();
	this.obj.find("[obj_type]").each(function() {
		var obj_type = $(this).attr("obj_type");
		var id = $(this).attr("id");
		if (chkObjNull(obj_type) || chkObjNull(id)) {
			// 如果这两项中有一项的无法获取的，则认为无需该项数据无需获取
			return;
		}
		if (!FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
			// 如果form下该标签不能存储数据，则不管他
			return;
		}
		if ("filebox" == obj_type) {
			map.put("__file_" + id, getObject($(this)).getValue());
			return;
		}
		map.put(id, getObject($(this)).getValue());
	});
	return map;
}

/**
 * 清空数据-增加clear方便记忆。
 * 
 * @return
 */
FormObj.prototype.clear = function() {
	this.clearData();
}

/**
 * 清空数据
 * 
 * @return
 */
FormObj.prototype.clearData = function() {
	this.obj.find("[obj_type]").each(function() {
		var obj_type = $(this).attr("obj_type");
		var id = $(this).attr("id");
		if (chkObjNull(obj_type) || chkObjNull(id)) {
			// 如果这两项中有一项的无法获取的，则认为无需该项设置数据
			return;
		}
		if (!FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
			// 如果form下该标签不能存储数据，则不管他
			return;
		}
		getObject($(this)).clear();
	});
}

/**
 * form数据检测
 * 
 * @return
 */
FormObj.prototype.chkFormData = function(isshowboxmsg) {
	var isok = true;
	this.obj.find("[obj_type]").each(function() {
		var obj_type = $(this).attr("obj_type");
		var id = $(this).attr("id");
		if (chkObjNull(obj_type) || chkObjNull(id)) {
			// 如果这两项中有一项的无法获取的，则认为无需该项设置数据
			return;
		}
		if (!FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
			// 如果form下该标签不能存储数据，则不管他
			return;
		}
		// 由于不正确的标签一直存在提醒，所以只要找到不合格的，就不再往下找了。
		if (isok) {
			var isthistagok = getObject($(this)).chkValue();
			isok = isok && isthistagok;
			if (!isthistagok) {
				if (!chkObjNull(isshowboxmsg) && true == isshowboxmsg) {
					// 当传入了参数且为box提示时，展示提示框
					var alertInfo = "表单中存在不符合要求的数据项，请检查。";
					try {
						var tagLabel = getObject($(this)).getLabelValue();
						alertInfo = "【" + tagLabel + "】信息项不符合要求，请检查。"
					} catch (exception) {
						if (GlobalVars.DEBUG_MODE) {
							alertInfo = alertInfo + "**内部异常：" + exception.name + ":" + exception.message + "**";
						}
					}
					alert(alertInfo);
				}
				getObject($(this)).focus();
			}
		}
	});
	return isok;
};
/**
 * 方法重命名，简化写法
 */
FormObj.prototype.check = FormObj.prototype.chkFormData;

/**
 * form的显示
 */
FormObj.prototype.expand = function() {
	this.obj.parent().children("legend").children(".form_title_tools").removeClass("form_title_tools_expand").attr("title", "折叠");
	this.obj.show();
};

/**
 * form隐藏
 */
FormObj.prototype.collapse = function() {
	this.obj.parent().children("legend").children(".form_title_tools").addClass("form_title_tools_expand").attr("title", "展开");
	this.obj.hide();
};

/**
 * form的显示--部分
 */
FormObj.prototype.expandPart = function(key) {
	var btnObj = this.obj.find(".form_collapse_btn[_key=" + key + "]");
	var index = btnObj.attr("_index");
	if (btnObj.length > 0) {
		var collapseTitle = btnObj.attr("_collapseTitle");
		this.obj.find("tr[_collapsePart=" + index + "]").show();
		btnObj.attr("_collapsed", "false");
		btnObj.attr("title", collapseTitle);
		btnObj.find("span").text(collapseTitle);
		btnObj.find("i").removeClass("form_title_tools_expand");
	}
};

/**
 * form隐藏--部分
 */
FormObj.prototype.collapsePart = function(key) {
	var btnObj = this.obj.find(".form_collapse_btn[_key=" + key + "]");
	var index = btnObj.attr("_index");
	if (btnObj.length > 0) {
		var expandTitle = btnObj.attr("_expandTitle");
		this.obj.find("tr[_collapsePart=" + index + "]").hide();
		btnObj.attr("_collapsed", "true");
		btnObj.attr("title", expandTitle);
		btnObj.find("span").text(expandTitle);
		btnObj.find("i").addClass("form_title_tools_expand");
	}
};

/**
 * 数据窗口中的数据格式的自修正；对于设置datesource的的在创建tag时，数据已经被放到value上，当系统加载完成后，将value进行一次修正。<br>
 * 修正只针对与文本输入狂textinput,对于其他的标签无需修正；
 * 
 * @param isshowboxmsg
 * @return
 */
FormObj.prototype.selfCorrFormat = function() {
	var isSelfCorrFormat = this.obj.attr("isSelfCorrFormat");// 是否完成了自我修正，默认没有完成
	if (!chkObjNull(isSelfCorrFormat) && "true" == isSelfCorrFormat) {
		return;
	}

	// 这是进行的textinput的自动修正
	this.obj.find("[obj_type='textinput']").each(function() {
		var id = $(this).attr("id");
		if (chkObjNull(id)) {
			// id为空，则无法修正
			return;
		}
		var textinput = new TextInputObj($(this));
		// 这个需要对控件的mask，以及数据类型进行修正设置
		textinput.dealMask();

		// 对数据进行自动的修正
		textinput.setValue($(this).val());

	});

	// 这是进行的dropdownlist进行事件设置
	this.obj.find("[obj_type='dropdownlist']").each(function() {
		var id = $(this).attr("id");
		if (chkObjNull(id)) {
			// id为空，则无法修正
			return;
		}
		var dropdownlist = new DropdownListObj($(this));

		// 进行事件监听的初始化
		dropdownlist.initEvent();
	});

	// 这是进行的dropdownlist进行事件设置
	this.obj.find("[obj_type='multidropdownlist']").each(function() {
		var id = $(this).attr("id");
		if (chkObjNull(id)) {
			// id为空，则无法修正
			return;
		}
		var mulitdropdownlist = new MultiDropdownListObj($(this));

		// 进行事件监听的初始化
		mulitdropdownlist.initEvent();
	});

	// 这是进行的checkboxlist进行初始化
	this.obj.find("[obj_type='checkboxlist']").each(function() {
		var id = $(this).attr("id");
		if (chkObjNull(id)) {
			// id为空，则无法修正
			return;
		}
		var checkboxlist = new CheckboxListObj($(this));
		checkboxlist.initEvent();
	});

	// 这是进行的checkboxlist进行初始化
	this.obj.find("[obj_type='radiobuttonlist']").each(function() {
		var id = $(this).attr("id");
		if (chkObjNull(id)) {
			// id为空，则无法修正
			return;
		}
		var radiobuttonlist = new RadiobuttonListObj($(this));
		radiobuttonlist.initEvent();
	});

	// 这是进行的dropdownlist进行事件设置
	this.obj.find("[obj_type='textarea']").each(function() {
		var id = $(this).attr("id");
		if (chkObjNull(id)) {
			// id为空，则无法修正
			return;
		}
		var textarea = new TextareaObj($(this));

		// 进行事件监听的初始化
		textarea.initEvent();
	});

	// 这是进行的multiselectbox进行初始化
	this.obj.find("[obj_type='multiselectbox']").each(function() {
		var id = $(this).attr("id");
		if (chkObjNull(id)) {
			// id为空，则无法修正
			return;
		}
		var multiselectbox = new MultiSelectBoxObj($(this));
		multiselectbox.init();
	});

	this.obj.attr("isSelfCorrFormat", "true");

	// 对于折叠展开操作进行设置
	this.obj.parent().children("legend").children(".form_title_tools").click(function() {
		var formObj = $(this).parent().next();
		if (formObj.is(':hidden')) {
			get(formObj).expand();
		} else {
			get(formObj).collapse();
		}
	});

	// 内部行的折叠
	var formObj = this.obj;
	this.obj.find(".form_collapse_btn").click(function() {
		var thisObj = $(this);
		var index = thisObj.attr("_index");
		var collapsed = thisObj.attr("_collapsed");
		if ("true" == collapsed) {
			var collapseTitle = thisObj.attr("_collapseTitle");
			formObj.find("tr[_collapsePart=" + index + "]").show();
			thisObj.attr("_collapsed", "false");
			thisObj.attr("title", collapseTitle);
			thisObj.find("span").text(collapseTitle);
			thisObj.find("i").removeClass("form_title_tools_expand");
		} else {
			var expandTitle = thisObj.attr("_expandTitle");
			formObj.find("tr[_collapsePart=" + index + "]").hide();
			thisObj.attr("_collapsed", "true");
			thisObj.attr("title", expandTitle);
			thisObj.find("span").text(expandTitle);
			thisObj.find("i").addClass("form_title_tools_expand");
		}
	});
};
