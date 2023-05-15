/**
 * 文本域
 */
function TextareaObj(obj) {
	this.obj = obj;
	var objid = this.obj.attr("id");
	var form = this.obj.parents("form").first();
	this.labelobj = form.find("#" + objid + "_label");// label的对象
};

/**
 * 改变label的颜色
 * 
 * @param color
 * @return
 */
TextareaObj.prototype.setLabelColor = function(color) {
	return this.labelobj.css("color", color);
};

/**
 * 改变文本值颜色
 * 
 * @param color
 * @return
 */
TextareaObj.prototype.setColor = function(color) {
	return this.obj.css("color", color);
};

/**
 * 设置label文本内容
 * 
 * @return
 */
TextareaObj.prototype.setLabelValue = function(value) {
	return this.labelobj.text(value);
};

/**
 * 获取label文本内容
 * 
 * @return
 */
TextareaObj.prototype.getLabelValue = function() {
	return this.labelobj.text();
};

/**
 * 验证是否合法数据
 * 
 * @return
 */
TextareaObj.prototype.isValid = function() {
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
TextareaObj.prototype.validate = function() {
	var isvalid = this.isValid();
	if (!isvalid) {
		this.obj.removeClass("textareatag");
		this.obj.addClass("textareatag-invlalid");
	} else {
		this.obj.removeClass("textareatag-invlalid");
		this.obj.addClass("textareatag");
	}
	return isvalid;
};

/**
 * 设置设置是否必录
 */
TextareaObj.prototype.setRequired = function(required) {
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
TextareaObj.prototype.chkValue = function() {
	return this.validate();
};

/**
 * 置焦点
 * 
 * @return
 */
TextareaObj.prototype.focus = function() {
	return this.obj.focus();
};

/**
 * 设置只读
 * 
 * @param readonly
 * @return
 */
TextareaObj.prototype.setReadOnly = function(readonly) {
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
TextareaObj.prototype.getReadOnly = function() {
	var readonlynow = ("readonly" == this.obj.attr("readonly"));
	return readonlynow;
};

/**
 * 设置文本内容
 * 
 * @return
 */
TextareaObj.prototype.setValue = function(value) {
	this.obj.val(value);
	this.validate();
	return true;
};

/**
 * 获取文本内容
 * 
 * @return
 */
TextareaObj.prototype.getValue = function() {
	return this.obj.val();
};

/**
 * 数据清空
 */
TextareaObj.prototype.clear = function() {
	this.setValue("");
};

/**
 * 处理其默认事件问题，不提供给外部调用-在控件加载完成后，自动附加事件操作
 */
TextareaObj.prototype.initEvent = function() {
	// 失去焦点时验证
	this.obj.bind('blur', function() {
		var objtmp = getObject($(this));
		objtmp.validate();
	});

	// 单击事件-弹出选择列表
	var opts = this.obj.attr("data-opt");
	if(!chkObjNull(opts)){
		var optsList = new List(opts);
		var optsJson = optsList.values;
		if(optsJson.length > 0){
			this.obj.bind("dblclick", function(e) {
				if(getObject($(this)).getReadOnly()){
					return;
				}
				TextareaUtil.createFloatPanel(this);
			});
			this.obj.bind("mousedown.textarea", function(e) {
				e.stopPropagation ? e.stopPropagation() : e.cancelBubble = true;
			});

			var target = this.obj.get(0);
			$(document).bind("mousedown.textarea", function(e) {
				TextareaUtil.destroyFloatPanel(target);
			});
		}
	}
	// 默认加载完成后，进行一次验证
	this.validate();
};

/**
 * 工具方法-不对外提on个
 */
var TextareaUtil = {
	// 修改批注配置
	modifyNoteConfig : function(pzbh) {
		if (chkObjNull(pzbh)) {
			return;
		}
		var url = new URL("taglib.do", "fwdNoteConfigModify");
		url.addPara("pzbh", pzbh);
		openTopWindow("典型批注信息修改", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			MsgBox.alert("典型批注维护成功，刷新页面生效");
		});
	},
	// 创建浮动面板（多选）
	createFloatPanel : function(target) {
		var txtPanel = $.data(target, 'textareapanel');
		if (!txtPanel) {
			var targetid = $(target).attr("id");
			// 面板的html代码
			var innerHtml = "";
			innerHtml = innerHtml
					+ "<div style=\"position:absolute;z-index:110000;display:block;left:-500;width:170px;top:-500px;\">";
			innerHtml = innerHtml
					+ "	<div class=\"combo-panel panel-body panel-body-noheader\" style=\"width:170px;height:230px;\">";
			innerHtml = innerHtml
					+ "		<div id=\"opts_con_"
					+ targetid
					+ "\" style=\"padding:3px 10px 3px 10px;height:197px;overflow:auto;line-height:18px;\">";
			// 开始组装选项
			var opts = $(target).attr("data-opt");
			var optsList = new List(opts);
			var optsJson = optsList.values;
			var maxlenValue = 9;
			for ( var j = 0, m = optsJson.length; j < m; j++) {
				var optJson = optsJson[j];
				var value = optJson.pznr;
				if (value.length > maxlenValue) {
					maxlenValue = value.length;
				}
				innerHtml = innerHtml
						+ "<div style=\"cursor:pointer;clear:both;\" _value=\"" + value
						+ "\">" + value + "</div>";
			}
			innerHtml = innerHtml + "		</div>";
			innerHtml = innerHtml + "	</div>";
			innerHtml = innerHtml + "</div>";

			txtPanel = $(innerHtml).appendTo("body");// 得到对象

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

			var panelH = optsJson.length * 20;
			if (panelH > 197) {
				panelH = 197;
			}
			txtPanel.css("width", width + "px");
			txtPanel.find(".combo-panel").each(function() {
				$(this).css("width", width + "px");
				$(this).css("height", (panelH + 8) + "px");
			});

			txtPanel.find("#opts_con_" + targetid).each(function() {
				$(this).css("height", panelH + "px");
			});

			// 浮动框优先在底部浮动
			top = top + height + 3;
			if (top + 230 > docH) {// 如果下边放不开该控件，则放到顶部
				// 获取panel高度
				var corrVar = 197 - (optsJson.length * 20);// 修正量
				if(corrVar < 0){
					corrVar = 0;
				}
				top = top - height - 209 + corrVar;
			}
			txtPanel.css("left", left + "px");
			txtPanel.css("top", top + "px");

			// 阻止向上冒泡
			txtPanel.bind("mousedown.textarea",
					function(e) {
						e.stopPropagation ? e.stopPropagation()
								: e.cancelBubble = true;
					});

			// 单个选项的单击事件
			txtPanel.find("div[_value]").each(function() {
				$(this).bind("click", function(e) {
					getObject($(target)).setValue($(this).attr("_value"));
					TextareaUtil.destroyFloatPanel(target);
					getObject($(target)).focus();
				});
			});
			// 鼠标事件
			txtPanel.find("div[_value]").each(function() {
				$(this).bind("mouseover", function(e) {
					$(this).css("background", "#FFE48D");
				});
			});
			txtPanel.find("div[_value]").each(function() {
				$(this).bind("mouseout", function(e) {
					$(this).css("background", "#FFFFFF");
				});
			});
			// 绑定部分事件
			$.data(target, "textareapanel", txtPanel);
		}
	},
	// 销毁浮动面板
	destroyFloatPanel : function(target) {
		var txtPanel = $.data(target, 'textareapanel');
		if (txtPanel) {
			txtPanel.remove();
			$.removeData(target, 'textareapanel');
		}
	}
};