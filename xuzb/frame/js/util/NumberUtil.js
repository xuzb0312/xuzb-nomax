/**
 * 新增类，用来处理数字之间的状态转换。
 */
var NumberUtil = {
	// 获取四舍五入的值-以解决四舍五入精度问题
	round : function(value, how) {
		var num = new Number(value);
		var num1 = new Number(num.toFixed(10));
		return parseFloat(num1.toFixed(how));
	},

	// 根据带%号的值，获取实际的值。返回String类型的值。
	// 这个方法只是获取带%的数字，但是没有去掉分隔符。所以如果使用要跟getValue一起使用。
	getValueWithSuffix : function(value) {
		if (value == "") {
			alert("输入参数【" + value + "】中不存在%,请检查");
			return;
		}
		value = value.substr(0, value.length - 1);

		if (value.indexOf(".") > 2) {
			value = value.substring(0, value.indexOf(".") - 2) + "." + value.substring(value.indexOf(".") - 2, value.indexOf(".")) + value.substr(value.indexOf(".") + 1);
		} else if (value.indexOf(".") == 2) {
			value = "0." + value.substring(0, 2) + value.substr(3);
		} else if (value.indexOf(".") == 1) {
			value = "0.0" + value.substring(0, 1) + value.substr(2);
		} else if (value.indexOf(".") == 0) {
			value = "0.00" + value.substr(1);
		} else if (value.indexOf(".") == -1) {
			if (value.length > 2) {
				value = value.substr(0, value.length - 2) + "." + value.substr(value.length - 2);
			} else if (value.length == 2) {
				value = "0." + value.substr(0);
			} else {
				value = "0.0" + value.substr(0);
			}
		}
		return value;
	},

	// 根据带,的值获取真实的值，返回String类型的值。value是不带%的值。
	// 获取String类型的去掉分隔符以后的值。
	getValue : function(value, formatString) {
		if (value == "") {
			return "0";
		}

		var integerMaskString = "";
		var SEPERATE_CHAR = "";

		if (formatString.indexOf(".") < 0) {
			integerMaskString = formatString;
		} else {
			integerMaskString = formatString.substr(0, formatString.indexOf("."));
		}

		var imsLen = integerMaskString.length;
		for ( var i = 0; i < imsLen; i++) {
			var temp = integerMaskString.substr(i, 1);
			if (temp == "#") {
				continue;
			} else if (temp == "0") {
				continue;
			} else {
				SEPERATE_CHAR = temp || "";// 数字间的分割符号.
			}
		}
		if (value.indexOf(SEPERATE_CHAR) < 0) {
			return value;
		} else {
			var reg = new RegExp(SEPERATE_CHAR, "gi");
			return value.replace(reg, "");
		}
	},

	getRealValue : function(value, format) {
		if (!format) {
			format = "###################0.00";
		}
		if (value.substr(value.length - 1) == "%") {
			return parseFloat(NumberUtil.getValue(NumberUtil.getValueWithSuffix(value), format));
		} else {
			return parseFloat(NumberUtil.getValue(value, format));
		}
	},

	/**
	 * 根据obj，对初始值进行加工：例如由1111.11222 ->11,111.222%
	 */
	getShowValueByMask : function(mask, value) {
		if (value == null || value == "") {
			value = "";
		}
		/**
		 * 给批设置使用
		 */
		if (value.indexOf("%") > 0) {
			var vnumber = value.substr(0, value.length - 1);
			if (!isNaN(vnumber)) {
				value = parseFloat(vnumber) / 100;
				value = NumberUtil.getShowValueByMask(mask, value.toString());
			}
			return value;
		}
		/**
		 * 给批设置使用
		 */
		if (value.indexOf(",") > 0) {
			return value;
		}
		var nLen = value.length;
		for ( var i = 0; i < nLen; i++) {
			var temp = value.substr(i, 1);
			if ((temp < "0" || temp > "9") && temp != "." && temp != "-") {
				return "";
			}
		}
		if (mask.indexOf("%") > 0) {
			if (value != "") {
				value = Math.round((parseFloat(value) * 100) * 10000) / 10000;
				value = value.toString();
			}
		}
		/**
		 * 处理正负号.
		 */
		var sign = "";
		if (value.substr(0, 1) == "-") {
			sign = "-";
			value = value.substr(1);
		}

		if (isNaN(value)) {
			return "";
		}

		var MAX_FORMAT_LENGTH = 0;
		var FORMAT_LENGTH = 0;
		var FLOAT_LENGTH = 0;
		var SEPERATE_CHAR = "";
		var suffix = "";

		/**
		 * formatString=mask;
		 */
		if (mask.substring(0, 1) == "+") {
			mask = mask.substring(1, mask.length);
		}
		if (mask.substring(mask.length - 1, mask.length) == "%") {
			suffix = "%";
			mask = mask.substring(0, mask.length - 1);
		}
		var integerMaskString = "";
		var floatMaskString = "";
		if (mask.indexOf(".") < 0) {
			integerMaskString = mask;
		} else {
			integerMaskString = mask.substr(0, mask.indexOf("."));
			floatMaskString = mask.substr(mask.indexOf(".") + 1);
		}
		if (integerMaskString == null) {
			integerMaskString = "";
		}
		if (floatMaskString == null) {
			floatMaskString = "";
		}

		var countOfZero = 0;
		var countOfExtra = 0;

		var imsLen = integerMaskString.length;
		for ( var i = 0; i < imsLen; i++) {
			var temp = integerMaskString.substr(i, 1);
			if (temp == "#") {
				countOfExtra++;
				continue;
			} else if (temp == "0") {
				countOfZero++;
				continue;
			} else {
				/**
				 * 数字间的分割符号.
				 */
				SEPERATE_CHAR = temp || "";
			}
		}
		/**
		 * 整数部分的格式长度.
		 */
		FORMAT_LENGTH = countOfZero;
		/**
		 * 整数部分的最大长度.
		 */
		MAX_FORMAT_LENGTH = countOfZero + countOfExtra;

		if (SEPERATE_CHAR == null) {
			SEPERATE_CHAR = "";
		}
		countOfZero = 0;
		countOfExtra = 0;
		var fmsLen = floatMaskString.length;
		for ( var i = 0; i < fmsLen; i++) {
			var temp = floatMaskString.substr(i, 1);
			if (temp == "#") {
				countOfExtra++;
				continue;
			} else if (temp == "0") {
				countOfZero++;
				continue;
			}
		}
		/**
		 * 小数的长度.
		 */
		FLOAT_LENGTH = countOfExtra + countOfZero;

		/**
		 * 处理精度问题-mod.yjc.2020.09.18
		 */
		if (FLOAT_LENGTH > 0) {
			try {
				var floatPow = Math.pow(10, FLOAT_LENGTH);
				var number_value = Math.round(parseFloat(value) * floatPow) / floatPow;
				value = number_value.toString();
			} catch (floatEx) {
			}
		}

		var integerNumbers = "";
		var floatNumbers = "";
		if (value.indexOf(".") < 0) {
			integerNumbers = value;
		} else {
			var number = value.split(".");
			if (number.length == 1) {
				integerNumbers = number[0];
			} else if (number.length == 2) {
				integerNumbers = number[0];
				floatNumbers = number[1];
			} else {
				return value;
			}
		}
		while (integerNumbers.length > 1 && integerNumbers.substr(0, 1) == "0") {
			integerNumbers = integerNumbers.substr(1);
		}

		var count = 0;
		if (integerNumbers.length > MAX_FORMAT_LENGTH) {
			/**
			 * 如果数字超出显示范围，割掉前面多余的数字.
			 */
			integerNumbers = integerNumbers.substr(integerNumbers.length - MAX_FORMAT_LENGTH);
		}
		if (integerNumbers.length < FORMAT_LENGTH) {
			/**
			 * 如果数字小于最小个数，那么追加
			 */
			count = FORMAT_LENGTH - integerNumbers.length;
			for ( var i = 0; i < count; i++) {
				integerNumbers += "0";
			}
		}
		if (floatNumbers.length > FLOAT_LENGTH) {
			floatNumbers = floatNumbers.substr(0, FLOAT_LENGTH);
		} else {
			count = FLOAT_LENGTH - floatNumbers.length;
			for ( var i = 0; i < count; i++) {
				floatNumbers += "0";
			}
		}
		/**
		 * 计算分隔符的个数.
		 */
		var seperateCount = Math.round(integerNumbers.length / 3);
		var result = "";
		for ( var i = 0; i < seperateCount; i++) {
			result = SEPERATE_CHAR + integerNumbers.substring(integerNumbers.length - 3, integerNumbers.length) + result;
			integerNumbers = integerNumbers.substring(0, integerNumbers.length - 3);
		}
		if ((integerNumbers == null || integerNumbers.length == 0) && SEPERATE_CHAR != "") {
			result = result.substring(1, result.length);
		} else {
			result = integerNumbers + result;
		}

		if (FLOAT_LENGTH > 0) {
			return sign + result + "." + floatNumbers + suffix;
		} else {
			return sign + result + suffix;
		}
	},

	checkNumber : function(str, mask) {
		var number;
		if (str == "") {
			return true;
		}
		if (str.indexOf('%') > -1 && mask.indexOf('%') > -1) {
			if (str.indexOf('.') > -1 && mask.indexOf('.') > -1) {
				return NumberUtil.checkNumberValid(str, mask);
			} else if (str.indexOf('.') == -1 && mask.indexOf('.') == -1) {
				return NumberUtil.checkNumberValid(str, mask);
			} else {
				alert("输入的值跟格式不匹配,期望输入值应该符合：" + mask);
				return false;
			}
		} else if (str.indexOf('%') == -1 && mask.indexOf('%') == -1) {
			if (str.indexOf('.') > -1 && mask.indexOf('.') > -1) {
				return NumberUtil.checkNumberValid(str, mask);
			} else if (str.indexOf('.') == -1 && mask.indexOf('.') == -1) {
				return NumberUtil.checkNumberValid(str, mask);
			} else {
				alert("输入的值跟格式不匹配,期望输入值应该符合：" + mask);
				return false;
			}
		} else {
			alert("输入的值跟格式不匹配,期望输入值应该符合：" + mask);
			return false;
		}
	},

	checkNumberValid : function(numbers, mask) {
		if (numbers == null) {
			return false;
		}
		if (numbers == "") {
			return true;
		}
		var nLen = numbers.length;
		for ( var i = 0; i < nLen; i++) {
			var temp = numbers.substr(i, 1);
			if ((temp < "0" || temp > "9") && temp != "." && temp != "-" && temp != '%' && temp != ",") {
				alert("数字串中存在非法字符,期望输入值应该符合：" + mask);
				return false;
			}
		}

		var integerNumbers = "";
		var floatNumbers = "";
		if (numbers.indexOf(".") < 0) {
			integerNumbers = numbers;
		} else {
			var number = numbers.split(".");
			if (number.length == 1) {
				integerNumbers = number[0];
			} else if (number.length == 2) {
				integerNumbers = number[0];
				floatNumbers = number[1];
			} else {
				alert("数字传中存在多个小数点,期望输入值应该符合：" + mask);
				return false;
			}
		}
		if (integerNumbers.length > 3 && integerNumbers.indexOf(',') == -1 && mask.indexOf(",") > -1) {
			alert("整数部分中缺少分割符,期望输入值应该符合：" + mask);
			return false;
		}

		while (integerNumbers.indexOf(',') > -1) {
			if (integerNumbers.length - integerNumbers.lastIndexOf(",") - 1 == 3) {
				integerNumbers = integerNumbers.substr(0, integerNumbers.lastIndexOf(","));
			} else {
				alert("数字串中的分隔符的位置不对,期望输入值应该符合：" + mask);
				return false;
			}
		}
		if (integerNumbers.length > 1 && integerNumbers.substr(0, 1) == "0") {
			alert("整数的第一个字符为0,期望输入值应该符合：" + mask);
			return false;
		}
		if (mask.substring(0, 1) == "+") {
			mask = mask.substring(1, mask.length);
		}

		if (mask.substring(mask.length - 1, mask.length) == "%") {
			mask = mask.substring(0, mask.length - 1);
		}
		var integerMaskString = "";
		var floatMaskString = "";
		if (mask.indexOf(".") < 0) {
			integerMaskString = mask;
		} else {
			integerMaskString = mask.substr(0, mask.indexOf("."));
			floatMaskString = mask.substr(mask.indexOf(".") + 1);
		}

		if (integerNumbers.length > integerMaskString.length) {
			alert("整数长度超过允许的最大长度,期望输入值应该符合：" + mask);
			return false;
		}

		/**
		 * 去掉value中的分割符
		 */
		var tnumbers = "";
		for ( var i = 0; i < nLen; i++) {
			var temp = numbers.substr(i, 1);
			if (temp != ",") {
				tnumbers = tnumbers + temp;
			}
		}
		numbers = tnumbers;

		/**
		 * 处理正负号.
		 */
		var sign = "";
		if (numbers.substr(0, 1) == "-") {
			sign = "-";
			numbers = numbers.substr(1);
		}
		if (numbers.substr(numbers.length - 1, 1) == "%") {
			numbers = numbers.substring(0, numbers.length - 1);
		}

		if (isNaN(numbers)) {
			alert("数字传不是一个合法的数字,期望输入值应该符合：" + mask);
			return false;
		}

		return true;
	}
};

/**
 * @Description: 解决JS计算浮点乘法时的精度问题
 * @errorExample: 0.045 * 1293.00
 * @param arg1
 * @param arg2
 * @return
 */
function accMul(arg1, arg2) {
	var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
	try {
		m += s1.split(".")[1].length;
	} catch (e) {
	}

	try {
		m += s2.split(".")[1].length;
	} catch (e) {
	}

	return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
};

Number.prototype.mul = function(arg) {
	return accMul(arg, this);
};

/**
 * @Description: 解决JS计算浮点加法时的精度问题
 * @errorExample: 0.7+0.1
 * @param arg1
 * @param arg2
 * @return
 */
function accAdd(arg1, arg2) {
	var r1, r2, m;
	try {
		r1 = arg1.toString().split(".")[1].length;
	} catch (e) {
		r1 = 0;
	}

	try {
		r2 = arg2.toString().split(".")[1].length;
	} catch (e) {
		r2 = 0;
	}

	m = Math.pow(10, Math.max(r1, r2));
	return (arg1 * m + arg2 * m) / m;
};

Number.prototype.add = function(arg) {
	return accAdd(arg, this);
};

/**
 * @Description: 解决JS计算浮点除法时的精度问题
 * @errorExample: 0.3/0.1
 * @param arg1
 * @param arg2
 * @return
 */
function accDiv(arg1, arg2) {
	var t1 = 0, t2 = 0, r1, r2;
	try {
		t1 = arg1.toString().split(".")[1].length;
	} catch (e) {
	}
	try {
		t2 = arg2.toString().split(".")[1].length;
	} catch (e) {
	}
	with (Math) {
		r1 = Number(arg1.toString().replace(".", ""));
		r2 = Number(arg2.toString().replace(".", ""));
		return (r1 / r2) * pow(10, t2 - t1);
	}
};

Number.prototype.div = function(arg) {
	return accDiv(this, arg);
};

/**
 * @Description: 解决JS计算浮点减法时的精度问题
 * @errorExample: 0.3/0.1
 * @param arg1
 * @param arg2
 * @return
 */
function accSub(arg1, arg2) {
	var r1, r2, m, n;
	try {
		r1 = arg1.toString().split(".")[1].length;
	} catch (e) {
		r1 = 0;
	}
	try {
		r2 = arg2.toString().split(".")[1].length;
	} catch (e) {
		r2 = 0;
	}
	m = Math.pow(10, Math.max(r1, r2));
	n = (r1 >= r2) ? r1 : r2;
	return ((arg1 * m - arg2 * m) / m).toFixed(n);
};

Number.prototype.sub = function(arg) {
	return accSub(this, arg);
};