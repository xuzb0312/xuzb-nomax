/**
 * 构造函数
 */
function DateTimeMaskExe() {
	this.$target = null;
}

/**
 * 判断指定位置是否可以输入
 * 
 * @param position
 *            指定的位置
 * @return
 */
DateTimeMaskExe.prototype.canInput = function(position) {
	try {
		var maskString = this.$target.attr("maskString");

		if (position < 0 || position >= maskString.length) {
			return false;
		}
		var currentChar = maskString.substr(position, 1);
		if (currentChar == "M" || currentChar == "d" || currentChar == "y"
				|| currentChar == "h" || currentChar == "m"
				|| currentChar == "s") {
			return true;
		} else {
			return false;
		}
	} catch (oE) {
		throw new Error("DateTimeMaskExe.canInput-Error" + oE.message);
	}
};

/**
 * 校验指定位置范围的值是否符合日期格式.
 * 
 * @param dateTimeString
 * @param position
 * @return
 */
DateTimeMaskExe.prototype.check = function(dateTimeString, position) {
	if (dateTimeString == null || dateTimeString.length == 0 || position < 0
			|| position > dateTimeString.length) {
		return false;
	}

	var yearIndex = Number(this.$target.attr("yearIndex"));
	var monthIndex = Number(this.$target.attr("monthIndex"));
	var dayIndex = Number(this.$target.attr("dayIndex"));
	var hourIndex = Number(this.$target.attr("hourIndex"));
	var minuteIndex = Number(this.$target.attr("minuteIndex"));
	var secondIndex = Number(this.$target.attr("secondIndex"));
	if (yearIndex >= 0 && position >= yearIndex && position < yearIndex + 4) {
		var year = new Number(dateTimeString.substr(yearIndex, 4));
		if (isNaN(year) || year < DateTimeMaskConfig.defaultLeastYear
				|| year > DateTimeMaskConfig.defaultMaxYear) {
			return false;
		}
	} else if (monthIndex >= 0 && position >= monthIndex
			&& position < monthIndex + 2) {
		var month = new Number(dateTimeString.substr(monthIndex, 2));
		if (isNaN(month) || month > 12 || month < 0) {
			return false;
		}
	} else if (dayIndex >= 0 && position >= dayIndex && position < dayIndex + 2) {
		var day = new Number(dateTimeString.substr(dayIndex, 2));
		if (isNaN(day) || day > 31 || day < 0) {
			return false;
		}
	} else if (hourIndex >= 0 && position >= hourIndex
			&& position < hourIndex + 2) {
		var hour = new Number(dateTimeString.substr(hourIndex, 2));
		if (isNaN(hour) || hour > 23 || hour < 0) {
			return false;
		}
	} else if (minuteIndex >= 0 && position >= minuteIndex
			&& position < minuteIndex + 2) {
		var minute = new Number(dateTimeString.substr(minuteIndex, 2));
		if (isNaN(minute) || minute > 59 || minute < 0) {
			return false;
		}
	} else if (secondIndex >= 0 && position >= secondIndex
			&& position < secondIndex + 2) {
		var second = new Number(dateTimeString.substr(secondIndex, 2));
		if (isNaN(second) || second > 59 || second < 0) {
			return false;
		}
	} else {
		return true;
	}
	return true;
};

DateTimeMaskExe.prototype.formatDateTime = function(jEvent, dateTimeString) {
	var $target = $(jEvent.target);
	var maskString = $target.attr("maskString");

	// 输入日期字符串与当前格式不吻合，矫正.
	if (dateTimeString == null || dateTimeString.length == 0
			|| dateTimeString.length != maskString.length) {
		dateTimeString = this.getZero(maskString.length);
	}

	var yearIndex = Number($target.attr("yearIndex"));
	var monthIndex = Number($target.attr("monthIndex"));
	var dayIndex = Number($target.attr("dayIndex"));
	var hourIndex = Number($target.attr("hourIndex"));
	var minuteIndex = Number($target.attr("minuteIndex"));
	var secondIndex = Number($target.attr("secondIndex"));

	var result = maskString;
	var yearString = "";
	var monthString = "";
	var dayString = "";
	var hourString = "";
	var minuteString = "";
	var secondString = "";

	if (yearIndex >= 0) {
		var year = new Number(dateTimeString.substr(yearIndex, 4));
		if (isNaN(year) || year < DateTimeMaskConfig.defaultLeastYear) {
			yearString = DateTimeMaskConfig.defaultLeastYear.toString();
		} else if (year > DateTimeMaskConfig.defaultMaxYear) {
			yearString = DateTimeMaskConfig.defaultMaxYear.toString();
		} else {
			yearString = year.toString();
		}

		if (yearString.length < 4) {
			yearString = this.getZero(4 - yearString.length) + yearString;
		}
		result = this.replaceString(result, "yyyy", yearString);
	}

	if (monthIndex >= 0) {
		var month = new Number(dateTimeString.substr(monthIndex, 2));

		if (isNaN(month) || month > 12 || month <= 0) {
			if (yearString != "0000") {
				monthString = "01";
			} else {
				monthString = "00";
			}
		} else {
			monthString = month.toString();
		}
		if (monthString.length < 2) {
			monthString = this.getZero(2 - monthString.length) + monthString;
		}
		result = this.replaceString(result, "MM", monthString);
	}
	if (dayIndex >= 0) {
		var day = new Number(dateTimeString.substr(dayIndex, 2));

		if (isNaN(day) || day > 31 || day <= 0) {
			if (yearString != "0000" && monthString != "00") {
				dayString = "01";
			} else {
				dayString = "00";
			}
		} else {
			dayString = day.toString();
		}
		if (dayString.length < 2) {
			dayString = this.getZero(2 - dayString.length) + dayString;
		}
		result = this.replaceString(result, "dd", dayString);
	}
	if (hourIndex >= 0) {
		var hour = new Number(dateTimeString.substr(hourIndex, 2));

		if (isNaN(hour) || hour > 23 || hour <= 0) {
			hourString = "00";
		} else {
			hourString = hour.toString();
		}
		if (hourString.length < 2) {
			hourString = this.getZero(2 - hourString.length) + hourString;
		}
		result = this.replaceString(result, "hh", hourString);
	}
	if (minuteIndex >= 0) {
		var minute = new Number(dateTimeString.substr(minuteIndex, 2));

		if (isNaN(minute) || minute > 59 || minute <= 0) {
			minuteString = "00";
		} else {
			minuteString = minute.toString();
		}
		if (minuteString.length < 2) {
			minuteString = this.getZero(2 - minuteString.length) + minuteString;
		}
		result = this.replaceString(result, "mm", minuteString);
	}
	if (secondIndex >= 0) {
		var second = new Number(dateTimeString.substr(secondIndex, 2));

		if (isNaN(second) || second > 59 || second <= 0) {
			secondString = "00";
		} else {
			secondString = second.toString();
		}
		if (secondString.length < 2) {
			secondString = this.getZero(2 - secondString.length) + secondString;
		}
		result = this.replaceString(result, "ss", secondString);
	}
	return result;
};

DateTimeMaskExe.prototype.formatDateTimeAuto = function(jEvent, dateTimeString) {
	var $target = $(jEvent.target);
	var maskString = $target.attr("maskString");

	// 输入日期字符串与当前格式不吻合，矫正.
	if (dateTimeString == null || dateTimeString.length == 0
			|| dateTimeString.length != maskString.length) {
		dateTimeString = this.getZero(maskString.length);
	}

	var yearIndex = Number($target.attr("yearIndex"));
	var monthIndex = Number($target.attr("monthIndex"));
	var dayIndex = Number($target.attr("dayIndex"));
	var hourIndex = Number($target.attr("hourIndex"));
	var minuteIndex = Number($target.attr("minuteIndex"));
	var secondIndex = Number($target.attr("secondIndex"));

	var result = maskString;
	var yearString = "";
	var monthString = "";
	var dayString = "";
	var hourString = "";
	var minuteString = "";
	var secondString = "";
	if (yearIndex >= 0) {
		var year = new Number(dateTimeString.substr(yearIndex, 4));
		if (isNaN(year) || year < DateTimeMaskConfig.defaultLeastYear) {
			yearString = DateTimeMaskConfig.defaultLeastYear.toString();
		} else if (year > DateTimeMaskConfig.defaultMaxYear) {
			yearString = DateTimeMaskConfig.defaultMaxYear.toString();
		} else {
			yearString = year.toString();
		}

		if (yearString.length < 4) {
			yearString = this.getZero(4 - yearString.length) + yearString;
		}
		result = this.replaceString(result, "yyyy", yearString);
	}

	if (monthIndex >= 0) {
		var month = new Number(dateTimeString.substr(monthIndex, 2));
		if (isNaN(month) || month >= 20 || month <= 0) {
			monthString = month.toString();
		} else if (month > 12) {
			monthString = "10";
		} else {
			monthString = month.toString();
		}
		if (monthString.length < 2) {
			monthString = this.getZero(2 - monthString.length) + monthString;
		}
		result = this.replaceString(result, "MM", monthString);
	}
	if (dayIndex >= 0) {
		var day = new Number(dateTimeString.substr(dayIndex, 2));
		if (isNaN(day) || day >= 40 || day <= 0) {
			dayString = day.toString();
		} else if (day > 31 && day < 40) {
			dayString = "30";
		} else {
			dayString = day.toString();
		}
		if (dayString.length < 2) {
			dayString = this.getZero(2 - dayString.length) + dayString;
		}
		result = this.replaceString(result, "dd", dayString);
	}
	if (hourIndex >= 0) {
		var hour = new Number(dateTimeString.substr(hourIndex, 2));

		if (isNaN(hour) || hour >= 30 || hour <= 0) {
			hourString = hour.toString();
		} else if (hour > 23 && hour < 30) {
			hourString = "20";
		} else {
			hourString = hour.toString();
		}
		if (hourString.length < 2) {
			hourString = this.getZero(2 - hourString.length) + hourString;
		}
		result = this.replaceString(result, "hh", hourString);
	}
	if (minuteIndex >= 0) {
		var minute = new Number(dateTimeString.substr(minuteIndex, 2));

		minuteString = minute.toString();

		if (minuteString.length < 2) {
			minuteString = this.getZero(2 - minuteString.length) + minuteString;
		}
		result = this.replaceString(result, "mm", minuteString);
	}
	if (secondIndex >= 0) {
		var second = new Number(dateTimeString.substr(secondIndex, 2));

		secondString = second.toString();

		if (secondString.length < 2) {
			secondString = this.getZero(2 - secondString.length) + secondString;
		}
		result = this.replaceString(result, "ss", secondString);
	}
	return result;
};

/**
 * 获得指定长度的零串
 * 
 * @param zeroLength
 * @return
 */
DateTimeMaskExe.prototype.getZero = function(zeroLength) {
	if (zeroLength == null || zeroLength == 0) {
		return "";
	}
	var temp = "";
	for ( var i = 0; i < zeroLength; i++) {
		temp += "0";
	}
	return temp;
};

/**
 * 判断键入的是不是功能键.
 * 
 * @param pressKeyCode
 *            键盘输入值
 * @return
 */
DateTimeMaskExe.prototype.isKeyValid = function(pressKeyCode) {
	if (pressKeyCode == null || typeof (pressKeyCode) != "number") {
		return false;
	}
	if (DateTimeMaskConfig.WORK_KEY == null
			|| DateTimeMaskConfig.WORK_KEY.length == 0) {
		return false;
	}

	var dtmcwkLen = DateTimeMaskConfig.WORK_KEY.length;
	for ( var i = 0; i < dtmcwkLen; i++) {
		if (DateTimeMaskConfig.WORK_KEY[i] == pressKeyCode) {
			return true;
		}
	}
	return false;
};

/**
 * 判断键入的是不是数字.
 * 
 * @param pressKeyCode
 *            键盘输入值
 * @return
 */
DateTimeMaskExe.prototype.isNumber = function(pressKeyCode) {
	if (pressKeyCode == null || typeof (pressKeyCode) != "number") {
		return false;
	}
	if (DateTimeMaskConfig.NUMBER_KEY == null
			|| DateTimeMaskConfig.NUMBER_KEY.length == 0) {
		return false;
	}

	var dtmcnkLen = DateTimeMaskConfig.NUMBER_KEY.length;
	for ( var i = 0; i < dtmcnkLen; i++) {
		if (DateTimeMaskConfig.NUMBER_KEY[i] == pressKeyCode) {
			return true;
		}
	}
	return false;
};

/**
 * 判断当前的值是不是一个合法的时间
 * 
 * @return
 */
DateTimeMaskExe.prototype.isValidDateTime = function(jEvent) {
	var $target = $(jEvent.target);
	var eleValue = String($target.val());
	if (!eleValue) {
		return false;
	}

	if (eleValue.length > $target.attr("mask").length) {
		return false;
	}

	var yearIndex = Number($target.attr("yearIndex"));
	var monthIndex = Number($target.attr("monthIndex"));
	var dayIndex = Number($target.attr("dayIndex"));
	var hourIndex = Number($target.attr("hourIndex"));
	var minuteIndex = Number($target.attr("minuteIndex"));
	var secondIndex = Number($target.attr("secondIndex"));
	if (yearIndex >= 0) {
		var year = new Number(eleValue.substr(yearIndex, 4));
		if (isNaN(year) || year == 0
				|| year < DateTimeMaskConfig.defaultLeastYear
				|| year > DateTimeMaskConfig.defaultMaxYear) {
			return false;
		}
	}
	if (monthIndex >= 0) {
		var month = new Number(eleValue.substr(monthIndex, 2));
		if (isNaN(month) || month <= 0 || month > 12) {
			return false;
		}
	}
	if (dayIndex >= 0) {
		var day = new Number(eleValue.substr(dayIndex, 2));
		if (isNaN(day) || day <= 0 || day > 31) {
			return false;
		}
	}
	// 增加对每月日期合法性的判断 2005-03-21
	if (day > 28 && day < 31) {
		if (month == 2) {
			if (day != 29) {
				alert(year + "年" + month + "月无" + day + "日。");
				return false;
			} else {
				if ((year % 4) != 0) {
					alert(year + "年" + month + "月无" + day + "日。");
					return false;
				} else {
					if ((year % 100 == 0) && (year % 400 != 0)) {
						alert(year + "年" + month + "月无" + day + "日。");
						return false;
					}
				}
			}
		}
	} else if (day == 31) {
		if ((month == 2) || (month == 4) || (month == 6) || (month == 9)
				|| (month == 11)) {
			alert(month + "月无" + day + "日");
			return false;
		}
	}

	if (hourIndex >= 0) {
		var hour = new Number(eleValue.substr(hourIndex, 2));
		if (isNaN(hour) || hour < 0 || hour > 23) {
			return false;
		}
	}
	if (minuteIndex >= 0) {
		var minute = new Number(eleValue.substr(minuteIndex, 2));
		if (isNaN(minute) || minute < 0 || minute > 59) {
			return false;
		}
	}
	if (secondIndex >= 0) {
		var second = new Number(eleValue.substr(secondIndex, 2));
		if (isNaN(second) || second < 0 || second > 59) {
			return false;
		}
	}
	return true;
};

/**
 * 移动光标到指定的位置
 * 
 * @param position
 *            位置
 * @param direction
 *            向后
 * @param selectAll
 *            表示是否选择这个位置之后的文字
 * @return
 */
DateTimeMaskExe.prototype.moveCursor = function(position, direction, selectAll) {
	try {
		if (!direction) {
			direction = 0;
		}

		var ele = this.$target[0];
		var eleValue = this.$target.val();
		var maskString = this.$target.attr("maskString");

		// 如果位置非法，把光标移到最后.
		if (position < 0 || position > eleValue.length) {
			position = eleValue.length;
		}

		if (direction > 0) {
			// 找当前位置后（包括当前位置）的第一个可编辑位置.
			var esemsLen = maskString.length;
			while (!this.canInput(position) && position < esemsLen) {
				position++;
			}
		} else if (direction < 0) {
			// 找当前位置前（包括当前位置）的第一个可编辑位置.
			while (!this.canInput(position - 1) && position > 0) {
				position--;
			}
		} else if (position == maskString.length) {
			// 移动到开头
			position = 0;
		} else {
			// 绝对位置.
		}

		var npt = this.$target[0], range = null;
		if (npt.setSelectionRange) {
			npt.selectionStart = position;
			npt.selectionEnd = position;
		} else if (npt.createTextRange) {
			range = npt.createTextRange();
			range.moveStart("character", position);
			if (!selectAll) {
				range.collapse();
			}
			range.select();
		}
	} catch (oE) {
		throw new Error("DateTimeMaskExe.moveCursor-Error" + oE.message);
	}
};

DateTimeMaskExe.prototype.onKeyDown = function(jEvent) {
	this.$target = $(jEvent.target);

	// 只读不作处理
	if (this.$target.prop("readonly")) {
		return;
	}

	// 取当前值、当前按键
	this.elementValue = this.$target.val() || "";
	this.pressKeyCode = jEvent.which;

	// 获得当前选中的字符.
	if (window.getSelection) {
		this.selectText = window.getSelection().toString();
	} else if (document.selection) {
		this.selectText = document.selection.createRange().text;
	}

	var npt = this.$target[0], begin = null, end = null, range = null;
	if (npt.setSelectionRange) {
		begin = npt.selectionStart;
		end = npt.selectionEnd;
	} else if (document.selection && document.selection.createRange) {
		range = document.selection.createRange()
		begin = 0 - range.duplicate().moveStart("character", -1e5);
		end = begin + range.text.length;
	}

	this.currentPosition = begin;

	if (this.selectText == null) {
		this.selectText = "";
	}

	if (jEvent.ctrlKey && this.pressKeyCode == 86) {
		// ctrl+v
		this.onPaste(jEvent);
	} else if (jEvent.ctrlKey && this.pressKeyCode == 67) {
		// ctrl+c
	} else if (jEvent.ctrlKey && this.pressKeyCode == 88) {
		// ctrl+x
		this.onCut(jEvent);
	} else if (this.pressKeyCode == 46) {
		// delete: 逐个向后删除
		this.dealDelete(jEvent);
	} else if (this.pressKeyCode == 8) {
		// backspace -->
		this.dealBackSpace(jEvent);
	} else if (this.isNumber(this.pressKeyCode)) {
		// 数字0-9
		this.dealInputNumber(jEvent);
	} else if (this.isKeyValid(this.pressKeyCode)) {
		// 其他键盘功能键
	} else {
		// 其他键
		jEvent.preventDefault();
	}
};

// 剪切
DateTimeMaskExe.prototype.onBeforeCut = function(jEvent) {
	this.$target = $(jEvent.target);
};

// 粘贴
DateTimeMaskExe.prototype.onBeforePaste = function(jEvent) {
	this.$target = $(jEvent.target);
};

// 失去焦点
DateTimeMaskExe.prototype.onBlur = function(jEvent) {
	this.$target = $(jEvent.target);

	var eleVal = this.$target.val();
	var oldVal = this.$target.attr("oldValue");

	if (!this.isValidDateTime(jEvent)) {
		this.$target.val("");
	}
};

// 右键菜单
DateTimeMaskExe.prototype.onContextMenu = function(jEvent) {
	// 只读不作处理
	this.$target = $(jEvent.target);
	if (this.$target.prop("readonly")) {
		return;
	}
	jEvent.preventDefault();
};

// 剪切.
DateTimeMaskExe.prototype.onCut = function(jEvent) {
	this.$target = $(jEvent.target);

	jEvent.preventDefault();

	if (this.selectText.length <= 0) {
		return;
	}

	var frontStr = "";
	var lastStr = "";

	// 如果选中的字符串是以分割符开始，什么也不做.
	if (!this.canInput(this.currentPosition - this.selectText.length)) {
		this.moveCursor(this.currentPosition, 0);
		return;
	}
	var frontStr = this.elementValue.substr(0, this.currentPosition
			- this.selectText.length);
	var lastStr = this.elementValue.substr(this.currentPosition);

	window.clipboardData.clearData("Text");
	window.clipboardData.setData("Text", this.selectText);

	this.selectText = this.dealSelectText(this.selectText, this.currentPosition
			- this.selectText.length);
	var newDateTimeString = (frontStr || "") + this.selectText
			+ (lastStr || "");
	this.$target.val(this.formatDateTime(jEvent, newDateTimeString));
	this.moveCursor(this.currentPosition, 0);
};

// 获得焦点
DateTimeMaskExe.prototype.onFocus = function(jEvent) {
	this.$target = $(jEvent.target);

	var ele = this.$target[0];
	var eleValue = this.$target.val();

	// 只读不作处理
	if (this.$target.prop("readonly")) {
		jEvent.preventDefault();
		return;
	}

	// 保存当前值
	this.$target.attr("oldValue", eleValue);
	if (!eleValue) {
		this.$target.val(this.formatDateTime(jEvent, ""));
	}

	// 查找到第一个可输入位置和不可输入位置
	var foundStart = false;
	var start = 0;
	var end = 0;
	var semsLen = this.$target.attr("maskString").length;
	for ( var i = 0; i < semsLen; i++) {
		if (!foundStart && this.canInput(i)) {
			start = i;
			foundStart = true;
		} else if (foundStart && !this.canInput(i)) {
			end = i;
			break;
		}
	}

	if (ele.setSelectionRange) {
		ele.selectionStart = start;
		ele.selectionEnd = 0 - (semsLen - end);
	} else if (ele.createTextRange) {
		range = ele.createTextRange();
		range.moveStart("character", start);
		range.collapse(true);
		range.select();
	}
};

// 粘贴.
DateTimeMaskExe.prototype.onPaste = function(jEvent) {
	this.$target = $(jEvent.target);

	jEvent.preventDefault();

	var maskString = this.$target.attr("maskString");
	var esemsLen = maskString.length;
	var pasteValue = window.clipboardData.getData("Text");

	// 如果剪贴板中数据为空或比mask短，则什么事都不做；
	if (pasteValue == null || pasteValue.length == 0
			|| pasteValue.length < esemsLen) {
		return;
	}

	// 如果剪贴板中数据比mask长，则按照mask的长度截断
	if (pasteValue.length > esemsLen) {
		pasteValue = pasteValue.substr(0, esemsLen);
	}

	// 校验剪贴板中数据合法性
	for ( var i = 0; i < esemsLen; i++) {
		// 如果当前位置是分割符，但不与格式中的分割符相同.
		if (!this.canInput(i)
				&& pasteValue.substr(i, 1) != maskString.substr(i, 1)) {
			return;
		}
		// 校验每个日期和时间的合法性. 模2为了减少校验次数.
		if ((i % 2) == 0 && !this.check(pasteValue, i)) {
			return;
		}
	}

	// 校验通过则直接赋值
	this.$target.val(this.formatDateTime(jEvent, pasteValue));

	// 移动到首字符
	this.moveCursor(this.$target.val().length);
};

// 处理backspace键.
DateTimeMaskExe.prototype.dealBackSpace = function(jEvent) {
	this.$target = $(jEvent.target);

	jEvent.preventDefault();

	var frontStr = "";
	var lastStr = "";
	var direction = -1;

	var newDateTimeString = "";
	var newPosition = this.currentPosition;

	if (this.selectText.length > 0) {
		// 如果选中的字符串是以分割符开始，什么也不做.
		if (!this.canInput(this.currentPosition - this.selectText.length)) {
			this.moveCursor(this.currentPosition - this.selectText.length, 0);
			return false;
		}
		var frontStr = this.elementValue.substr(0, this.currentPosition
				- this.selectText.length);
		var lastStr = this.elementValue.substr(this.currentPosition);
		this.selectText = this.dealSelectText(this.selectText,
				this.currentPosition - this.selectText.length);
		newDateTimeString = (frontStr || "") + this.selectText
				+ (lastStr || "");
		newPosition -= this.selectText.length
		direction = 0;
	} else {
		// 如果当前位置是格式符.
		if (!this.canInput(this.currentPosition - 1)) {
			frontStr = this.elementValue.substr(0, this.currentPosition - 2);
			lastStr = this.elementValue.substr(this.currentPosition - 1);
			newDateTimeString = (frontStr || "") + "0" + (lastStr || "");
			this.$target.val(this.formatDateTime(jEvent, newDateTimeString));
			this.moveCursor(this.currentPosition - 2, -1);
			return false;
		}

		frontStr = this.elementValue.substr(0, this.currentPosition - 1);
		lastStr = this.elementValue.substr(this.currentPosition);
		newDateTimeString = (frontStr || "") + "0" + (lastStr || "");
		newPosition--;
		direction = 0;
	}
	this.$target.val(this.formatDateTime(jEvent, newDateTimeString));
	this.moveCursor(newPosition, direction);
};

// 处理删除键.
DateTimeMaskExe.prototype.dealDelete = function(jEvent) {
	this.$target = $(jEvent.target);

	jEvent.preventDefault();

	var frontStr = "";
	var lastStr = "";
	var direction = 1; // 默认向正向移动光标.

	var newDateTimeString = "";
	var newPosition = this.currentPosition;

	if (this.selectText.length > 0) {
		// 如果选中的字符串是以分割符开始，什么也不做.
		if (!this.canInput(this.currentPosition - this.selectText.length)) {
			this.moveCursor(this.currentPosition, 0);
			return;
		}
		var frontStr = this.elementValue.substr(0, this.currentPosition
				- this.selectText.length);
		var lastStr = this.elementValue.substr(this.currentPosition);
		this.selectText = this.dealSelectText(this.selectText,
				this.currentPosition - this.selectText.length);
		newDateTimeString = (frontStr || "") + this.selectText
				+ (lastStr || "");
		direction = 0;
	} else {
		// 如果当前位置是格式符.
		if (!this.canInput(this.currentPosition)) {
			this.moveCursor(this.currentPosition + 1, 1);
			return;
		}
		frontStr = this.elementValue.substr(0, this.currentPosition);
		lastStr = this.elementValue.substr(this.currentPosition + 1);
		newDateTimeString = (frontStr || "") + "0" + (lastStr || "");
		newPosition++;
	}

	this.$target.val(this.formatDateTime(jEvent, newDateTimeString));

	this.moveCursor(newPosition, direction);
};

// 处理输入数字时.
DateTimeMaskExe.prototype.dealInputNumber = function(jEvent) {
	this.$target = $(jEvent.target);

	jEvent.preventDefault();

	var frontStr = "";
	var lastStr = "";
	var inputNumber = this.pressKeyCode - 96;
	if (inputNumber < 0) {
		inputNumber = this.pressKeyCode - 48;
	}
	var direction = 1; // 默认向正向移动光标.

	var newDateTimeString = "";
	var checkPosition = this.currentPosition;
	var newPosition = this.currentPosition;

	if (this.selectText.length > 0) {
		// 如果选中的字符串是以分割符开始，什么也不做.
		if (!this.canInput(this.currentPosition - this.selectText.length)) {
			this.moveCursor(this.currentPosition, 0);
			return;
		}

		var frontStr = this.elementValue.substr(0, this.currentPosition
				- this.selectText.length);
		var lastStr = this.elementValue.substr(this.currentPosition);
		this.selectText = this.dealSelectText(this.selectText,
				this.currentPosition - this.selectText.length);
		this.selectText = this.replaceChar(this.selectText, 0, inputNumber);
		checkPosition -= this.selectText.length;

		newDateTimeString = (frontStr || "") + this.selectText
				+ (lastStr || "");
		direction = this.currentPosition - this.selectText.length + 1;
	} else {
		// 如果当前位置是格式符.
		if (!this.canInput(this.currentPosition)) {
			return;
		}
		frontStr = this.elementValue.substr(0, this.currentPosition);
		lastStr = this.elementValue.substr(this.currentPosition + 1);
		newDateTimeString = (frontStr || "") + inputNumber + (lastStr || "");
		newPosition++;
	}
	newDateTimeString = this.formatDateTimeAuto(jEvent, newDateTimeString);
	// 校验数字输入位置日期值的合法性.
	if (!this.check(newDateTimeString, checkPosition)) {
		this.moveCursor(this.currentPosition, 0);
		return;
	} else {
		this.$target.val(this.formatDateTime(jEvent, newDateTimeString));
	}
	this.moveCursor(newPosition, direction);
};

// 处理选中的字符.把可编辑位置置成零.
// 参数：selectText 选中的字符； startIndex 在原始值中的开始位置.
// 返回：处理完成后的字符串.
DateTimeMaskExe.prototype.dealSelectText = function(selectText, startIndex,
		inputStr) {
	if (selectText == null || selectText.length == 0) {
		return "";
	}
	if (startIndex < 0
			|| (startIndex + selectText.length) > this.$target
					.attr("maskString").length) {
		return selectText;
	}
	var count = selectText.length;
	for ( var i = 0; i < count; i++) {
		if (this.canInput(startIndex + i)) {
			selectText = this.replaceChar(selectText, i, "0");
		}
	}
	return selectText;
};

// 替换目标字符串中的特定位置的字符.
// 参数：objString 目标字符串；reIndex 制定的位置；reChar 替换后的字符.
// 返回：替换完成后的字符串.
DateTimeMaskExe.prototype.replaceChar = function(objString, reIndex, reChar) {
	if (objString == null || objString.length == 0 || reIndex < 0
			|| reIndex > objString.length || reChar == null
			|| reChar.length > 1) {
		return objString;
	}
	var frontStr = objString.substr(0, reIndex);
	var lastStr = objString.substr(reIndex + 1);

	return (frontStr || "") + reChar + (lastStr || "");
};

// 替换目标字符串中的特定字符串.
// 参数：objString 目标字符串；sourceString 要替换的字符串；reString 替换后的字符串.
// 返回：替换完成后的字符串.
DateTimeMaskExe.prototype.replaceString = function(objString, sourceString,
		reString) {
	if (objString == null || objString.length == 0 || sourceString == null
			|| sourceString.length == 0 || objString.search(sourceString) < 0) {
		return objString;
	}
	var frontStr = objString.substr(0, objString.search(sourceString));
	var lastStr = objString.substr(objString.search(sourceString)
			+ sourceString.length);

	return (frontStr || "") + (reString || "") + (lastStr || "");
};