var DateTimeMaskConfig = {
	defaultFormatString : "yyyy-MM-dd",
	defaultLeastYear : 0000,
	defaultMaxYear : 9999,

	// 可用的功能键值数组.
	WORK_KEY : [ 8, 9, 13, 33, 34, 35, 36, 37, 38, 39, 40, 46, 144 ],
	// 数字键值数组.
	NUMBER_KEY : [ 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 96, 97, 98, 99, 100,
			101, 102, 103, 104, 105 ]
};

function DateTimeMask(obj) {
	if (!obj) {
		return;
	}
	this.$ele = $(obj);
	var formatString = this.$ele.attr("mask");
	if (chkObjNull(formatString)) {
		formatString = DateTimeMaskConfig.defaultFormatString;
	}

	this.$ele.css("ime-mode", "disabled");

	this.initMask(formatString);
	this.initEvent();
};

/**
 * 绑定事件 1. keydown 2. beforepaste 3. beforecut 4. contextmenu 5. focus 6. blur
 */
DateTimeMask.prototype.initEvent = function() {
	this.$ele.bind("keydown", this._onKeyDown);
	this.$ele.bind("beforepaste", this._onBeforePaste);
	this.$ele.bind("beforecut", this._onBeforeCut);
	this.$ele.bind("contextmenu", this._onContextMenu);
	this.$ele.bind("focus", this._onFocus);
	this.$ele.bind("blur", this._onBlur);
};

/**
 * 初始化格式参数.
 * 
 * @param formatString
 *            格式化字符串
 */
DateTimeMask.prototype.initMask = function(formatString) {
	// 如果存在，则必须是yyyy（4个y）、MM（2个M）、dd（2个d）、hh（2个h）、mm（2个m）、ss（2个s）
	if ((formatString.indexOf("y") >= 0 && formatString.indexOf("y") != formatString
			.lastIndexOf("y") - 3)
			|| (formatString.indexOf("M") >= 0 && formatString.indexOf("M") != formatString
					.lastIndexOf("M") - 1)
			|| (formatString.indexOf("d") >= 0 && formatString.indexOf("d") != formatString
					.lastIndexOf("d") - 1)
			|| (formatString.indexOf("h") >= 0 && formatString.indexOf("h") != formatString
					.lastIndexOf("h") - 1)
			|| (formatString.indexOf("m") >= 0 && formatString.indexOf("m") != formatString
					.lastIndexOf("m") - 1)
			|| (formatString.indexOf("s") >= 0 && formatString.indexOf("s") != formatString
					.lastIndexOf("s") - 1)) {
		formatString = DateTimeMaskConfig.defaultFormatString;
	}

	// 给元素本身附件mask、maskString、yearIndex、monthIndex、dayIndex、hourIndex、minuteIndex、secondIndex属性
	this.$ele.attr("mask", formatString);
	this.$ele.attr("maskString", formatString);
	this.$ele.attr("yearIndex", formatString.search("yyyy"));
	this.$ele.attr("monthIndex", formatString.search("MM"));
	this.$ele.attr("dayIndex", formatString.search("dd"));
	this.$ele.attr("hourIndex", formatString.search("hh"));
	this.$ele.attr("minuteIndex", formatString.search("mm"));
	this.$ele.attr("secondIndex", formatString.search("ss"));
};

/**
 * 事件绑定函数
 */
DateTimeMask.prototype._onKeyDown = function(jEvent) {
	var dtme = new DateTimeMaskExe();
	dtme.onKeyDown(jEvent);
};
DateTimeMask.prototype._onBeforePaste = function(jEvent) {
	var dtme = new DateTimeMaskExe();
	dtme.onBeforePaste(jEvent);
};
DateTimeMask.prototype._onBeforeCut = function(jEvent) {
	var dtme = new DateTimeMaskExe();
	dtme.onBeforeCut(jEvent);
};
DateTimeMask.prototype._onContextMenu = function(jEvent) {
	var dtme = new DateTimeMaskExe();
	dtme.onContextMenu(jEvent);
};
DateTimeMask.prototype._onFocus = function(jEvent) {
	var dtme = new DateTimeMaskExe();
	dtme.onFocus(jEvent);
};
DateTimeMask.prototype._onBlur = function(jEvent) {
	var dtme = new DateTimeMaskExe();
	dtme.onBlur(jEvent);
};