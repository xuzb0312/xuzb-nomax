var NumberMaskConfig = {
	// 可用的功能键值数组.
	WORK_KEY : [ 8, 9, 13, 33, 34, 35, 36, 37, 38, 39, 40, 46, 144, 109, 189 ],

	// 数字键值数组.
	NUMBER_KEY : [ 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 96, 97, 98, 99, 100,
			101, 102, 103, 104, 105 ]
};

/**
 * 对number数据进行检测，不进行输入中验证，只进行结果验证
 * 
 * @param obj
 * @return
 */
function NumberMask(obj) {
	if (!obj) {
		return;
	}
	this.$ele = $(obj);

	this.$ele.css("ime-mode", "disabled");

	this.initEvent();
}

// 初始化事件.
NumberMask.prototype.initEvent = function() {
	this.$ele.bind("keydown", this._onKeyDown);
	this.$ele.bind("contextmenu", this._onContextMenu);
	this.$ele.bind("focus", this._onFocus);
	this.$ele.bind("blur", this._onBlur);
};

NumberMask.prototype._onKeyDown = function(jEvent) {
	var nme = new NumberMaskExe();
	nme.onKeyDown(jEvent);
};
NumberMask.prototype._onContextMenu = function(jEvent) {
	var nme = new NumberMaskExe();
	nme.onContextMenu(jEvent);
};
NumberMask.prototype._onFocus = function(jEvent) {
	var nme = new NumberMaskExe();
	nme.onFocus(jEvent);
};
NumberMask.prototype._onBlur = function(jEvent) {
	var nme = new NumberMaskExe();
	nme.onBlur(jEvent);
};
