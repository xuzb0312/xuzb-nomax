/**
 * 日期时间对象
 * 
 * @author yjc
 */
var DateObj = function(value, formatString) {
	this.value = value;
	this.formatString = formatString;
};

DateObj.prototype.getYearIndex = function() {
	return this.formatString.search("yyyy");
};
DateObj.prototype.getMonthIndex = function() {
	return this.formatString.search("MM");
};
DateObj.prototype.getDayIndex = function() {
	return this.formatString.search("dd");
};
DateObj.prototype.getMinIndex = function() {
	return this.formatString.search("mm");
};
DateObj.prototype.getHourIndex = function() {
	return this.formatString.search("hh");
};
DateObj.prototype.getSecIndex = function() {
	return this.formatString.search("ss");
};
DateObj.prototype.getYear = function() {
	var year = "0000";
	var yearIndex = this.getYearIndex();

	if (yearIndex >= 0) {
		year = this.value.substr(yearIndex, 4);
	}
	var yearNumber = new Number(year);
	if (isNaN(yearNumber)) {
		return 0;
	} else {
		if (yearNumber < 1800) {
			yearNumber = 1800;
		}
		return yearNumber;
	}
};

// 获得月份.
DateObj.prototype.getMonth = function() {
	var month = "00";
	var monthIndex = this.getMonthIndex();
	if (monthIndex >= 0) {
		month = this.value.substr(monthIndex, 2);
	}
	var monthNumber = new Number(month);

	if (isNaN(monthNumber)) {
		return 0;
	} else {
		return monthNumber;
	}
};

// 获得日子.
DateObj.prototype.getDay = function() {
	var day = "00";
	var dayIndex = this.getDayIndex()
	if (dayIndex >= 0) {
		day = this.value.substr(dayIndex, 2);
	}
	var dayNumber = new Number(day);
	if (isNaN(dayNumber) || dayNumber == 0) {
		return 1;
	} else {
		return dayNumber;
	}
};

// 获得小时份.
DateObj.prototype.getHour = function() {
	var hour = "00";
	var hourIndex = this.getHourIndex();
	if (hourIndex >= 0) {
		hour = this.value.substr(hourIndex, 2);
	}
	var hourNumber = new Number(hour);
	if (isNaN(hourNumber)) {
		return 0;
	} else {
		return hourNumber;
	}
};

// 获得分钟.
DateObj.prototype.getMinute = function() {
	var minute = "00";
	var minIndex = this.getMinIndex();
	if (minIndex >= 0) {
		minute = this.value.substr(minIndex, 2);
	}
	var minuteNumber = new Number(minute);
	if (isNaN(minuteNumber)) {
		return 0;
	} else {
		return minuteNumber;
	}
};

// 获得秒.
DateObj.prototype.getSecond = function() {
	var second = 0;
	var secondIndex = this.getSecIndex()
	if (secondIndex >= 0) {
		second = this.value.substr(secondIndex, 2);
	}
	var secondNumber = new Number(second);
	if (isNaN(secondNumber)) {
		return 0;
	} else {
		return secondNumber;
	}
};

DateObj.prototype.getDate = function() {
	if (this.value == null || this.value == "") {
		return null;
	}
	var month = 0;
	if (this.getMonth() != 0) {
		month = this.getMonth() - 1;
	}
	var date = new Date(this.getYear(), month, this.getDay(), this.getHour(),
			this.getMinute(), this.getSecond());
	return date;
};

DateObj.prototype.isZero = function() {
	if (this.value == null || this.value == "") {
		return true;
	}
	if (this.getYear() <= 1800 && (this.getMonth()) == 0
			&& (this.getDay() - 1) == 0 && this.getHour() == 0
			&& this.getMinute() == 0 && this.getSecond() == 0) {
		return true;
	}
	return false;
};

DateObj.prototype.compareTo = function(anotherDateObj) {
	this.date = this.getDate();
	var anotherDate = anotherDateObj.getDate();
	this.date = this.date.valueOf();
	anotherDate = anotherDate.valueOf();
	if (this.date > anotherDate) {
		return -1;
	} else if (this.date == anotherDate) {
		return 0;
	} else {
		return 1;
	}
};

DateObj.prototype.compareMonthDiff = function(anotherDateObj) {
	this.year = this.getYear();
	this.Month = this.getMonth();
	this.day = this.getDay();

	var anyear = anotherDateObj.getYear();
	var anMonth = anotherDateObj.getMonth();
	var anday = anotherDateObj.getDay();

	var months = (anyear - this.year) * 12 + (anMonth - this.Month);

	if (this.day == anday) {
		return months;
	} else if (this.day == this.getLastDayOfMonth()
			&& anday == anotherDateObj.getLastDayOfMonth()) {
		return months;
	} else {
		months += (anday - this.day) / 31.00;
		return Math.floor(months);
	}
};
DateObj.prototype.getLastDayOfMonth = function() {
	var year = this.getYear();
	var month = this.getMonth();
	var day = this.getDay();

	if ((month == 1) || (month == 3) || (month == 5) || (month == 7)
			|| (month == 8) || (month == 10) || (month == 12)) {
		return 31;
	} else if ((month == 4) || (month == 6) || (month == 9) || (month == 11)) {
		return 30;
	} else if (month == 2) {
		if ((year % 4) != 0) {
			return 28;
		} else {
			if ((year % 100 == 0) && (year % 400 != 0)) {
				return 28;
			} else {
				return 29;
			}
		}
	}
};