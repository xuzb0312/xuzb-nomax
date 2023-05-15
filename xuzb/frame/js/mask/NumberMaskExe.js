/**
 * 验证事件处理操作
 * 
 * @return
 */
function NumberMaskExe() {
    this.$target = null;
}
/**
 * 当输入框失去焦点时
 */
NumberMaskExe.prototype.onBlur = function(jEvent) {
	this.$target = $(jEvent.target);
	try {
		var textinputobj = getObject(this.$target);
		var value = textinputobj.getValue();
		var valueNm = Number(value);
		if (isNaN(valueNm)) {
			// 不是数字，清空输入框，然后焦点再回去
			textinputobj.setValue(0);
			textinputobj.focus();
		} else {
			textinputobj.setValue(valueNm); // 把数据再设置回去
		}
	} catch(e) {
		// 对于grid中的元素来说，无法getObject会报错，此处进行处理。
		var value = this.$target.val();
		if (chkObjNull(value)) {
			value = 0;
		} else {
			var mask = this.$target.attr("mask");
			if (chkObjNull(mask)) {
				mask = "###########################0.00";
			}
			value = NumberUtil.getRealValue(value, mask);
		}
		var valueNm = Number(value);
		if (isNaN(valueNm)) {
			valueNm = 0;
		}
		valueNm = NumberUtil.getShowValueByMask(mask, valueNm.toString());
		this.$target.val(valueNm);
	}
};

/**
 * 在输入框点击右键时 阻止浏览器弹出默认的右键菜单!
 */
NumberMaskExe.prototype.onContextMenu = function(jEvent) {
	// 只读不作处理
	this.$target = $(jEvent.target);
	if (this.$target.prop("readonly")) {
		return;
	}
    jEvent.preventDefault();
};

// 数字mask的主要实现方法.
NumberMaskExe.prototype.onKeyDown = function(jEvent) {
    this.$target = $(jEvent.target);

    // 阻止默认事件，对按键的处理全部由onKeyDown方法处理
    if (this.$target.prop("readonly")) {
        return;
    }
    this.pressKeyCode = jEvent.which;
    if (jEvent.ctrlKey && this.pressKeyCode == 86) { // ctrl+v
    } else if (jEvent.ctrlKey && this.pressKeyCode == 67) { // ctrl+c
    } else if (jEvent.ctrlKey && this.pressKeyCode == 88) { // ctrl+x
    } else if (this.pressKeyCode == 190 || this.pressKeyCode == 110) { // .
    } else if (this.pressKeyCode == 189 || this.pressKeyCode == 109) { // -
    } else if (this.pressKeyCode == 46) { // delete
    } else if (this.pressKeyCode == 8) { // backspace
    } else if (this.isNumber(this.pressKeyCode)) { // 数字0-9
    } else if (this.isKeyValid(this.pressKeyCode)) { // 其他键盘功能键
    } else { // 其他键
        jEvent.preventDefault();
    }
};

// 判断键入的是不是数字.
// 参数：pressKeyCode 从键盘输入值.
// 返回：true 是数字；false 不是数字.
NumberMaskExe.prototype.isNumber = function(pressKeyCode) {
    if (pressKeyCode == null || typeof(pressKeyCode) != "number") {
        return false;
    }
    if (NumberMaskConfig.NUMBER_KEY == null || NumberMaskConfig.NUMBER_KEY.length == 0) {
        return false;
    }
    var nmcnkLen = NumberMaskConfig.NUMBER_KEY.length;
    for (var i = 0; i < nmcnkLen; i++) {
        if (NumberMaskConfig.NUMBER_KEY[i] == pressKeyCode) {
            return true;
        }
    }
    return false;
};

// 判断键入的是不是数字.
// 参数：pressKeyCode 从键盘输入值.
// 返回：true 是数字；false 不是数字.
NumberMaskExe.prototype.isKeyValid = function(pressKeyCode) {
    if (pressKeyCode == null || typeof(pressKeyCode) != "number") {
        return false;
    }
    if (NumberMaskConfig.WORK_KEY == null || NumberMaskConfig.WORK_KEY.length == 0) {
        return false;
    }
    var nmcwkLen = NumberMaskConfig.WORK_KEY.length;
    for (var i = 0; i < nmcwkLen; i++) {
        if (NumberMaskConfig.WORK_KEY[i] == pressKeyCode) {
            return true;
        }
    }
    return false;
};

/**
 * 获得焦点时事件
 */
NumberMaskExe.prototype.onFocus = function(jEvent) {
    this.$target = $(jEvent.target);

    // 只读不作处理
    if (this.$target.prop("readonly")) {
        jEvent.preventDefault();
        return;
    }
    var mask = this.$target.attr("mask");
    if (chkObjNull(mask)) {
        mask = "###########################0.00";
    }
    // 分析后缀
    if (mask.slice( - 1) == "%") {
        var value = this.$target.val();
        var leg = value.length;

        if (leg > 2) {
            var begin = 0;
            var end = leg - 1;
            var target = jEvent.target;
            target.setSelectionRange ? target.setSelectionRange(begin, end) : target.createTextRange && (range = target.createTextRange(), range.collapse(!0), range.moveEnd("character", end), range.moveStart("character", begin), range.select());
        }

    }
};