/**
 * 表单操作create.by.yjc.2017年12月15日
 */
function FormObj(obj) {
	this.obj = obj;
};

/**
 * 可以储存数据的子标签，类型
 */
FormObj.FORM_CAN_CACHE_DATE_TAG = new HashMap( {
	textinput : null,// 文本输入框
	dropdownlist : null,// 单选下拉框
	checkboxlist : null,// 复选框
	radiobuttonlist : null,// 单选框
	textarea : null
});

/**
 * 加载数据-data为json对象
 * 
 * @param data
 * @return
 */
FormObj.prototype.setData = function(data, isCover) {
	return this.setMapData(new HashMap(data), isCover);
};

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
		if (chkObjNull(obj_type) || chkObjNull(id)) {
			return;
		}
		if (!FormObj.FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
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
			if (!FormObj.FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
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
		if (!FormObj.FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
			// 如果form下该标签不能存储数据，则不管他
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
};

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
		if (!FormObj.FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
			// 如果form下该标签不能存储数据，则不管他
			return;
		}
		map.put(id, getObject($(this)).getValue());
	});
	return map;
};

/**
 * 清空数据-增加clear方便记忆。
 * 
 * @return
 */
FormObj.prototype.clear = function() {
	this.clearData();
};

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
		if (!FormObj.FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
			// 如果form下该标签不能存储数据，则不管他
			return;
		}
		getObject($(this)).clear();
	});
};

/**
 * form数据检测
 * 
 * @return
 */
FormObj.prototype.chkFormData = function(isshowboxmsg) {
	if (chkObjNull(isshowboxmsg)) {
		isshowboxmsg = true;
	}
	var isok = true;
	this.obj.find("[obj_type]").each(function() {
		var obj_type = $(this).attr("obj_type");
		var id = $(this).attr("id");
		if (chkObjNull(obj_type) || chkObjNull(id)) {
			// 如果这两项中有一项的无法获取的，则认为无需该项设置数据
			return;
		}
		if (!FormObj.FORM_CAN_CACHE_DATE_TAG.containsKey(obj_type)) {
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
						if (!chkObjNull(tagLabel)) {
							alertInfo = "【" + tagLabel + "】信息项不符合要求，请检查。"
						}
					} catch (exception) {
						if (GlobalVars.DEBUG_MODE) {
							alertInfo = alertInfo + "**内部异常：" + exception.name
									+ ":" + exception.message + "**";
						}
					}
					MsgBoxUtil.errTips(alertInfo);
				}
				getObject($(this)).focus();
			}
		}
	});
	return isok;
};

/**
 * 隐藏
 * 
 * @return
 */
FormObj.prototype.hide = function() {
	this.obj.parent().hide();
};

/**
 * 隐藏
 * 
 * @return
 */
FormObj.prototype.show = function() {
	this.obj.parent().show();
};