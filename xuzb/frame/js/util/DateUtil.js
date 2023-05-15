/**
 * date的操作工具类
 * 
 * @author yjc
 */
var DateUtil = {
	getDateString : function(date, format) {
		var yearString = date.getFullYear().toString();
		var monthString = (date.getMonth() + 1).toString();
		var dayString = date.getDate().toString();
		var hourString = date.getHours().toString();
		var minuteString = date.getMinutes().toString();
		var secondString = date.getSeconds().toString();

		var result = format;
		if (yearString.length < 4) {
			yearString = this.getZero(4 - yearString.length) + yearString;
		}
		result = this.replaceString(result, "yyyy", yearString);
		if (monthString.length < 2) {
			monthString = this.getZero(2 - monthString.length) + monthString;
		}
		result = this.replaceString(result, "MM", monthString);
		if (dayString.length < 2) {
			dayString = this.getZero(2 - dayString.length) + dayString;
		}
		result = this.replaceString(result, "dd", dayString);
		if (hourString.length < 2) {
			hourString = this.getZero(2 - hourString.length) + hourString;
		}
		result = this.replaceString(result, "hh", hourString);
		if (minuteString.length < 2) {
			minuteString = this.getZero(2 - minuteString.length) + minuteString;
		}
		result = this.replaceString(result, "mm", minuteString);
		if (secondString.length < 2) {
			secondString = this.getZero(2 - secondString.length) + secondString;
		}
		result = this.replaceString(result, "ss", secondString);
		return result;
	},
	// 检查日期是否合法
	checkDate : function(str, formatString) {
		var yearIndex = formatString.search("yyyy");
		var monthIndex = formatString.search("MM");
		var dayIndex = formatString.search("dd");
		var hourIndex = formatString.search("hh");
		var minuteIndex = formatString.search("mm");
		var secondIndex = formatString.search("ss");

		if (str == "") {
			return true;
		}
		if (str == null || str.length <= 0) {
			alert("字符串跟期望的输入值长度不匹配,期望输入值应该符合：" + formatString);
			return false;
		}
		if (str.length != formatString.length) {
			alert("字符串跟期望的输入值长度不匹配,期望输入值应该符合：" + formatString);
			return false;
		}

		for ( var i = 0; i < formatString.length; i++) {
			var temp = formatString.substr(i, 1);
			if (temp == "y" || temp == "M" || temp == "d" || temp == "h"
					|| temp == "m" || temp == "s") {
				if ((str.substr(i, 1) < "0" || str.substr(i, 1) > "9")) {
					alert("字符串中存在非法字符,期望输入值应该符合：" + formatString);
					return false;
				}
			}
			if (temp == "-" || temp == ":" || temp == " ") {
				if (str.substr(i, 1) != temp) {
					alert("字符串中存在非法字符,期望输入值应该符合：" + formatString);
					return false;
				}
			}
		}

		var allZero = true;
		for ( var i = 0; i < str.length; i++) {
			var temp = str.substr(i, 1);
			if (temp != "0" && temp != "-" && temp != ":" && temp != " ") {
				allZero = false;
			}
		}
		if (allZero) {
			return false;
		}

		if (yearIndex >= 0) {
			var year = new Number(str.substr(yearIndex, 4));
			if (isNaN(year) || year == 0
					|| year < DateTimeMaskConfig.defaultLeastYear
					|| year > DateTimeMaskConfig.defaultMaxYear) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		if (monthIndex >= 0) {
			var month = new Number(str.substr(monthIndex, 2));
			if (isNaN(month) || month <= 0 || month > 12) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		if (dayIndex >= 0) {
			var day = new Number(str.substr(dayIndex, 2));
			if (isNaN(day) || day <= 0 || day > 31) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		// 增加对每月日期合法性的判断 2005-03-21
		if (day > 28 && day < 31) {
			if (month == 2) {
				if (day != 29) {
					alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString + "。详细为："
							+ year + "年" + month + "月无" + day + "日");
					return false;
				} else {
					if ((year % 4) != 0) {
						alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString
								+ "。详细为：" + year + "年" + month + "月无" + day
								+ "日");
						return false;
					} else {
						if ((year % 100 == 0) && (year % 400 != 0)) {
							alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString
									+ "。详细为：" + year + "年" + month + "月无" + day
									+ "日");
							return false;
						}
					}
				}
			}
		} else if (day == 31) {
			if ((month == 2) || (month == 4) || (month == 6) || (month == 9)
					|| (month == 11)) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString + "。详细为："
						+ month + "月无" + day + "日");
				return false;
			}
		}
		if (hourIndex >= 0) {
			var hour = new Number(str.substr(hourIndex, 2));
			if (isNaN(hour) || hour < 0 || hour > 23) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		if (minuteIndex >= 0) {
			var minute = new Number(str.substr(minuteIndex, 2));
			if (isNaN(minute) || minute < 0 || minute > 59) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		if (secondIndex >= 0) {
			var second = new Number(str.substr(secondIndex, 2));
			if (isNaN(second) || second < 0 || second > 59) {
				alert("字符串跟期望的输入值不匹配,期望输入值应该符合：" + formatString);
				return false;
			}
		}
		return true;
	},

	// 比较两个时间之间的月份数,在比较之前需要开发人员自己来验证时间串的合法性
	getMonthDifferenceBetweenTwoDate : function(startTime, smask, endTime,
			emask) {
		var sobj = new DateObj(startTime, smask);
		var eobj = new DateObj(endTime, emask);
		return sobj.compareMonthDiff(eobj);
	},
	// 比较两个时间,在比较之前需要开发人员自己来验证时间串的合法性；返回值：-1，0，1
	compareDate : function(startTime, smask, endTime, emask) {
		var sobj = new DateObj(startTime, smask);
		var eobj = new DateObj(endTime, emask);
		return sobj.compareTo(eobj);
	},
	// 将一个value从旧mask的形式，转成新mask的形式，返回值是String。
	changeFormat : function(dataTime, oldMask, newMask) {
		var dateObj = new DateObj(dataTime, oldMask);
		if (dateObj.isZero()) {
			return "";
		}
		return DateUtil.getDateString(dateObj.getDate(), newMask);
	},
	getObjValue : function(obj) {
		var value = new DateObj(obj.value, obj.mask);
		return value.getDate();
	},
	// 获得指定长度的零串.
	getZero : function(zeroLength) {
		if (zeroLength == null || zeroLength == 0) {
			return "";
		}
		var temp = "";
		for ( var i = 0; i < zeroLength; i++) {
			temp += "0";
		}
		return temp;
	},
	replaceString : function(objString, sourceString, reString) {
		if (objString == null || objString.length == 0 || sourceString == null
				|| sourceString.length == 0
				|| objString.search(sourceString) < 0) {
			return objString;
		}
		var frontStr = objString.substr(0, objString.search(sourceString));
		var lastStr = objString.substr(objString.search(sourceString)
				+ sourceString.length);

		return (frontStr || "") + (reString || "") + (lastStr || "");
	},

	getShowValue : function(obj, dateTimeString) {
		if (dateTimeString == "") {
			return "";
		}
		if (!this.checkDate(dateTimeString, obj.mask)) {// 必须校验是否是有效的日期值，否则的话得到的日志值跟真正的不一样
		return "";
	}
	var result = obj.maskString;
	var yearString = "";
	var monthString = "";
	var dayString = "";
	var hourString = "";
	var minuteString = "";
	var secondString = "";

	if (obj.yearIndex >= 0) {
		var year = new Number(dateTimeString.substr(obj.yearIndex, 4));
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

	if (obj.monthIndex >= 0) {
		var month = new Number(dateTimeString.substr(obj.monthIndex, 2));

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
	if (obj.dayIndex >= 0) {
		var day = new Number(dateTimeString.substr(obj.dayIndex, 2));

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
	if (obj.hourIndex >= 0) {
		var hour = new Number(dateTimeString.substr(obj.hourIndex, 2));

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
	if (obj.minuteIndex >= 0) {
		var minute = new Number(dateTimeString.substr(obj.minuteIndex, 2));

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
	if (obj.secondIndex >= 0) {
		var second = new Number(dateTimeString.substr(obj.secondIndex, 2));
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
},
getShowValueByMask : function(mask, value) {
	if (value == "") {
		return "";
	}
	if (!this.checkDate(value, mask)) {// 必须校验是否是有效的日期值，否则的话得到的日志值跟真正的不一样
		return "";
	}
	var result = mask;
	var yearString = "";
	var monthString = "";
	var dayString = "";
	var hourString = "";
	var minuteString = "";
	var secondString = "";

	var yearIndex = mask.search("yyyy");
	var monthIndex = mask.search("MM");
	var dayIndex = mask.search("dd");
	var hourIndex = mask.search("hh");
	var minuteIndex = mask.search("mm");
	var secondIndex = mask.search("ss");

	if (yearIndex >= 0) {
		var year = new Number(value.substr(yearIndex, 4));
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
		var month = new Number(value.substr(monthIndex, 2));

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
		var day = new Number(value.substr(dayIndex, 2));

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
		var hour = new Number(value.substr(hourIndex, 2));

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
		var minute = new Number(value.substr(minuteIndex, 2));

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
		var second = new Number(value.substr(secondIndex, 2));
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
}
}
