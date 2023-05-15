/**
 * 复选框js对象
 */
function MultiDropdownListObj(obj) {
	this.obj = obj;
	var objid = this.obj.attr("id");
	var form = this.obj.parents("form").first();
	this.labelobj = form.find("#" + objid + "_label");// label的对象
}

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
MultiDropdownListObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 改变文本值颜色
 * 
 * @param color
 * @return
 */
MultiDropdownListObj.prototype.setColor = function(color) {
	return this.obj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
MultiDropdownListObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
MultiDropdownListObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
MultiDropdownListObj.prototype.isValid = function() {
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
MultiDropdownListObj.prototype.validate = function() {
	var isvalid = this.isValid();
	if (!isvalid) {
		this.obj.removeClass("mulitdropdownlist");
		this.obj.addClass("mulitdropdownlist-invlalid");
	} else {
		this.obj.removeClass("mulitdropdownlist-invlalid");
		this.obj.addClass("mulitdropdownlist");
	}
	return isvalid;
};

/**
 * 设置设置是否必录
 */
MultiDropdownListObj.prototype.setRequired = function(required) {
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
MultiDropdownListObj.prototype.chkValue = function() {
	return this.validate();
};

/**
 * 处理其默认事件问题，不提供给外部调用-在控件加载完成后，自动附加事件操作
 */
MultiDropdownListObj.prototype.initEvent = function() {
	// 失去焦点时验证
	this.obj.bind('blur', function() {
		var objtmp = getObject($(this));
		objtmp.setValue(objtmp.getValue());
	});

	// 单击事件-弹出选择列表
	this.obj.bind("click", function(e) {
		MultiDropdownListObjUtil.createFloatPanel(this);
	});
	this.obj.bind("mousedown.multiddl", function(e) {
		e.stopPropagation ? e.stopPropagation() : e.cancelBubble = true;
	});

	var target = this.obj.get(0);

	$(document).bind("mousedown.multiddl", function(e) {
		MultiDropdownListObjUtil.destroyFloatPanel(target);
	});

	// 焦点进入
	this.obj.bind("focus", function(e) {

	});
	// 屏蔽键盘的按键
	this.obj.bind("keydown", function(e) {
		if (e.which == 9) {// 不屏蔽tab
				return;
			}
			if (e.which == 13) {// 回车-弹出选择框
				MultiDropdownListObjUtil.createFloatPanel(this);
			}
			if (e.ctrlKey && e.which == 67) {
				return;
			}
			e.preventDefault();
		});
	this.obj.bind("beforepaste", function(e) {
		e.preventDefault();
	});
	this.obj.bind("contextmenu", function(e) {
		if (getObject($(this)).getReadOnly()) {
			return;
		}
		e.preventDefault();
	});

	// 默认加载完成后，进行一次验证
	this.validate();
};

/**
 * 工具方法-不对外提供
 */
var MultiDropdownListObjUtil = {
	// 创建浮动面板（多选）
	createFloatPanel : function(target) {
		if (getObject($(target)).getReadOnly()) {
			return;
		}
		var multiPanel = $.data(target, 'multidropdownlistpanel');
		if (!multiPanel) {
			var targetid = $(target).attr("id");
			// 面板的html代码
			var innerHtml = "";
			innerHtml = innerHtml
					+ "<div style=\"position:absolute;z-index:110000;display:block;left:-500;width:170px;top:-500px;\">";
			innerHtml = innerHtml
					+ "	<div class=\"combo-panel panel-body panel-body-noheader\" style=\"width:170px;height:230px;\">";
			innerHtml = innerHtml
					+ "		<div style=\"color:#99BBE8;background:#EEF4FF;";
			if ("v2" == GlobalVars.VIEW_TYPE) {
				innerHtml = innerHtml
						+ "padding:7px 5px;border-bottom:1px solid #3398DE;\">";
			} else {
				innerHtml = innerHtml + "padding:5px;\">";
			}
			innerHtml = innerHtml
					+ "			<a class=\"mulitddl-a\" href=\"#\" _type=\"all\">全选</a>";
			innerHtml = innerHtml
					+ "			<a class=\"mulitddl-a\" href=\"#\" _type=\"unall\">全不选</a>";
			innerHtml = innerHtml
					+ "			<a class=\"mulitddl-a\" href=\"#\" _type=\"other\">反选</a>";
			innerHtml = innerHtml
					+ "			<a class=\"mulitddl-a\" href=\"#\" _type=\"confirm\">确定</a>";
			innerHtml = innerHtml + "			<div style=\"clear:both;\"></div>";
			innerHtml = innerHtml + "		</div>";
			innerHtml = innerHtml
					+ "		<div id=\"opts_con_"
					+ targetid
					+ "\" style=\"padding:3px 10px 3px 10px;height:197px;overflow:auto;";
			if ("v2" == GlobalVars.VIEW_TYPE) {
				innerHtml = innerHtml + "line-height:23px;\">";
			} else {
				innerHtml = innerHtml + "line-height:18px;\">";
			}

			// 开始组装选项
			var opts = $(target).attr("data-opt");
			var optsList = new List(opts);
			var optsJson = optsList.values;
			var maxlenValue = 9;
			for ( var j = 0, m = optsJson.length; j < m; j++) {
				var optJson = optsJson[j];
				var key = optJson.key;
				var value = optJson.value;
				if (value.length > maxlenValue) {
					maxlenValue = value.length;
				}
				innerHtml = innerHtml + "<input type=\"checkbox\" id=\"opts_"
						+ targetid + "_" + key + "\" name=\"opts_" + targetid
						+ "\" value=\"" + key
						+ "\"><span style=\"cursor:pointer;\" _key=\"" + key
						+ "\">" + value + "</span>";

				if (j != m - 1) {
					innerHtml = innerHtml + "<br>";
				}
			}
			innerHtml = innerHtml + "		</div>";
			innerHtml = innerHtml + "	</div>";
			innerHtml = innerHtml + "</div>";

			multiPanel = $(innerHtml).appendTo("body");// 得到对象

			// 获取目标输入框的位置，及宽度
			var width = $(target).width();
			var height = $(target).height();
			var top = $(target).offset().top;
			var left = $(target).offset().left;
			var docH = $(document).height();

			// 根据以上的数据生成panel应该的宽度和位置
			if (maxlenValue <= 9) {
				if (width < 170) {
					width = 170;
				}
			} else {
				var autoWidht = 170 + Math.ceil((maxlenValue - 9) * 12.4);
				if (width < autoWidht) {
					width = autoWidht;
				}
			}

			// 先把宽度设置上
			width = width + 5;

			var panelH;
			if ("v2" == GlobalVars.VIEW_TYPE) {
				panelH = optsJson.length * 23;
			} else {
				panelH = optsJson.length * 20;
			}

			if (panelH > 197) {
				panelH = 197;
			}
			multiPanel.css("width", width + "px");
			multiPanel.find(".combo-panel").each(function() {
				$(this).css("width", width + "px");
				if ("v2" == GlobalVars.VIEW_TYPE) {
					$(this).css("height", (panelH + 38) + "px");
				} else {
					$(this).css("height", (panelH + 33) + "px");
				}

			});

			multiPanel.find("#opts_con_" + targetid).each(function() {
				$(this).css("height", panelH + "px");
			});

			// 浮动框优先在底部浮动
			top = top + height + 3;
			if (top + 230 > docH) {// 如果下边放不开该控件，则放到顶部
				// 获取panel高度
				var corrVar = 197 - (optsJson.length * 20);// 修正量
				if (corrVar < 0) {
					corrVar = 0;
				}
				top = top - height - 234 + corrVar;
			}
			multiPanel.css("left", left + "px");
			multiPanel.css("top", top + "px");

			// value绑定
			var targetValue = getObject($(target)).getValue();
			var targetValueArr = targetValue.split(",");
			for ( var i = 0, n = targetValueArr.length; i < n; i++) {
				var valKey = targetValueArr[i];
				multiPanel.find(
						"[id = opts_" + targetid + "_" + valKey + "]:checkbox")
						.attr("checked", true);
			}

			// 阻止向上冒泡
			multiPanel.bind("mousedown.multiddl",
					function(e) {
						e.stopPropagation ? e.stopPropagation()
								: e.cancelBubble = true;
					});

			// 单个选项的单击事件
			multiPanel.find("span[_key]").each(
					function() {
						$(this).bind(
								"click",
								function(e) {
									var optsid = "#opts_" + targetid + "_"
											+ $(this).attr("_key");
									multiPanel.find(optsid).attr("checked",
											!$(optsid).attr("checked"));
									updateValue();// 更新值
								});
					});
			multiPanel.find("[name = opts_" + targetid + "]:checkbox").each(
					function() {
						$(this).bind("click", function(e) {
							updateValue();// 更新值
							});
					});
			// 事件的绑定-全选、反选、确认、全部的事件绑定
			multiPanel
					.find(".mulitddl-a")
					.each(
							function() {
								if ("all" == $(this).attr("_type")) {
									$(this).bind(
											"click",
											function() {
												// 全选
												multiPanel.find(
														"[name = opts_"
																+ targetid
																+ "]:checkbox")
														.attr("checked", true);
												updateValue();// 更新值
											});
								} else if ("unall" == $(this).attr("_type")) {
									$(this)
											.bind(
													"click",
													function() {
														// 全不选
														multiPanel
																.find(
																		"[name = opts_"
																				+ targetid
																				+ "]:checkbox")
																.attr(
																		"checked",
																		false);
														updateValue();// 更新值
													});
								} else if ("other" == $(this).attr("_type")) {
									$(this)
											.bind(
													"click",
													function() {
														// 反选
														multiPanel
																.find(
																		"[name = opts_"
																				+ targetid
																				+ "]:checkbox")
																.each(
																		function() {
																			$(
																					this)
																					.attr(
																							"checked",
																							!$(
																									this)
																									.attr(
																											"checked"));
																		});
														updateValue();// 更新值
													});
								} else if ("confirm" == $(this).attr("_type")) {
									$(this)
											.bind(
													"click",
													function() {
														updateValue();// 更新值
														MultiDropdownListObjUtil
																.destroyFloatPanel(target);
														getObject($(target))
																.focus();
													});
								}
							});

			function updateValue() {
				// 确认
				var result = new Array();
				multiPanel.find("[name = opts_" + targetid + "]:checkbox")
						.each(function() {
							if ($(this).is(":checked")) {
								result.push($(this).attr("value"));
							}
						});
				var rvalue = result.join(",");
				var objTemp = getObject($(target));
				objTemp.setValue(rvalue);
				var onchangeName = objTemp.obj.attr("onchange");
				if (!chkObjNull(onchangeName)) {
					eval(onchangeName);
				}
			}
			// 绑定部分事件
			$.data(target, "multidropdownlistpanel", multiPanel);
		}
	},
	// 销毁浮动面板
	destroyFloatPanel : function(target) {
		var multiPanel = $.data(target, 'multidropdownlistpanel');
		if (multiPanel) {
			multiPanel.remove();
			$.removeData(target, 'multidropdownlistpanel');
		}
	}
};

/**
 * 置焦点
 * 
 * @return
 */
MultiDropdownListObj.prototype.focus = function() {
	return this.obj.focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
MultiDropdownListObj.prototype.setReadOnly = function(readonly) {
	var readonlynow = this.getReadOnly();
	if (readonly == readonlynow) {
		return;
	}
	if (readonly) {
		this.obj.attr("readonly", "readonly");
		this.obj.css("background", "#F9F9F9");
	} else {
		this.obj.removeAttr("readonly");
		this.obj.css("background", "#FFFFFF");
	}
};

/**
 * 获取只读
 * 
 * @param readonly
 * @return
 */
MultiDropdownListObj.prototype.getReadOnly = function() {
	var readonlynow = ("readonly" == this.obj.attr("readonly"));
	return readonlynow;
};

/**
 * 设置文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
MultiDropdownListObj.prototype.setValue = function(value) {
	if (chkObjNull(value)) {
		this.clear();
		return;
	}
	var opts = this.obj.attr("data-opt");
	var optsList = new List(opts);
	var optsJson = optsList.values;
	var valueText = "";
	var arrValue = value.split(",");
	for ( var i = 0, n = arrValue.length; i < n; i++) {
		var oneV = arrValue[i];
		for ( var j = 0, m = optsJson.length; j < m; j++) {
			var optJson = optsJson[j];
			if (oneV == optJson.key) {
				valueText = valueText + optJson.value + ",";
				break;
			}
			if (j == m - 1) {
				valueText = valueText + oneV + ",";
			}
		}
	}

	if (valueText.length > 0) {
		valueText = valueText.substr(0, valueText.length - 1);
	}

	this.obj.attr("_value", value);
	this.obj.val(valueText);
	this.validate();
	return true;
};

/**
 * 获取文本内容---根据type进行特殊数据处理操作
 * 
 * @return
 */
MultiDropdownListObj.prototype.getValue = function() {
	return this.obj.attr("_value");// 使用隐藏的value所谓真实的值
};

/**
 * 获取文本内容--文字
 * 
 * @return
 */
MultiDropdownListObj.prototype.getText = function() {
	return this.obj.val();
};

/**
 * 数据清空
 */
MultiDropdownListObj.prototype.clear = function() {
	this.obj.attr("_value", "");
	this.obj.val("");
};

/**
 * 重置下来框数据
 * 
 * @param json
 * @return
 */
MultiDropdownListObj.prototype.reloadOpt = function(json) {
	this.clear();// 首先情况数据当前选择的值
	if ("object" == typeof jsondata) {
		json = JSON.stringify(json);
	}
	this.obj.attr("data-opt", JSON.stringify(json));// 重置opt
};

/**
 * 获取选项-选项为json格式为[key-value]
 */
MultiDropdownListObj.prototype.getOpt = function() {
	var opts = this.obj.attr("data-opt");
	var optsList = new List(opts);
	var optsJson = optsList.values;
	return optsJson;
};

/**
 * 选项全选
 */
MultiDropdownListObj.prototype.selectAll = function() {
	var opts = this.getOpt();
	var value = "";
	for ( var i = 0, n = opts.length; i < n; i++) {
		value = value + opts[i].key + ",";
	}
	if (value.length > 0) {
		value = value.substr(0, value.length - 1);
	}
	this.setValue(value);
	return value;
};
