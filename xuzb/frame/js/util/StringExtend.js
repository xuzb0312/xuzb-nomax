/**
 * 本文件主要用于对String的属性进行扩展
 * @author yjc
 */

/**
 * 字符串去除空格的函数针对于String 类型有效
 */
String.prototype.trim = function() {
	return this.replace(/(^[\s]*)|([\s]*$)/g, "");
};

/** 去除左边空格 */
String.prototype.lTrim = function() {
	return this.replace(/(^[\s]*)/g, "");
};

/** 去除右边空格 */
String.prototype.rTrim = function() {
	return this.replace(/([\s]*$)/g, "");
};

/**
 * 判断str结束
 */
String.prototype.endWith = function(str) {
	if (this == null || str == null) {
		return false;
	}
	if (this.length < str.length) {
		return false;
	} else if (this == str) {
		return true;
	} else if (this.substring(this.length - str.length) == str) {
		return true;
	}
	return false;
};

/**
 * 判断是否以str开始
 */
String.prototype.startWith = function(str) {
	if (this == null || str == null) {
		return false;
	}
	if (this.length < str.length) {
		return false;
	} else if (this == str) {
		return true;
	} else if (this.substr(0, str.length) == str) {
		return true;
	}
	return false;
};

/**
 * 包含的判断
 * 
 * @param str
 * @return
 */
String.prototype.contains = function(str) {
	if (this == null) {
		return false;
	}
	if (str == null) {
		return true;
	}

	if (this.length < str.length) {
		return false;
	} else if (this == str) {
		return true;
	} else if (this.indexOf(str) != -1) {
		return true;
	}
	return false;
};

/**
 * 全部替换
 * 
 * @param FindText
 * @param RepText
 * @return
 */
String.prototype.replaceAll = function(FindText, RepText) {
	regExp = new RegExp(FindText, "g");
	return this.replace(regExp, RepText);
};