/*******************************************************************************
 * 对于cookie的操作的封装，依赖与jquery
 * 
 * @author yjc
 */
var CookieUtil = {
	// 设置cookie，optins设置cookie的参数，可以设置有效时间，单位天,
	// 使用方法例如：CookieUtil.set('name','lucy',1);//设置name=lucy有效期一天
	set : function(name, value, days) {
		var options = {
			expires : 365
		};// 默认cookie的有效期为一年
		if ("undefined" != typeof days && null != days && "" != days) {
			options = {
				expires : days
			};
		}
		if (value === null) {
			value = '';
			options.expires = -1;
		}
		var expires = '';
		if (options.expires
				&& (typeof options.expires == 'number' || options.expires.toUTCString)) {
			var date;
			if (typeof options.expires == 'number') {
				date = new Date();
				date.setTime(date.getTime()
						+ (options.expires * 24 * 60 * 60 * 1000));
			} else {
				date = options.expires;
			}
			expires = '; expires=' + date.toUTCString();
		}
		var path = options.path ? '; path=' + options.path : '';
		var domain = options.domain ? '; domain=' + options.domain : '';
		var secure = options.secure ? '; secure' : '';
		document.cookie = [ name, '=', encodeURIComponent(value), expires,
				path, domain, secure ].join('');
	},
	// 获取cookie
	get : function(name) {
		var cookieValue = null;
		if (document.cookie && document.cookie != '') {
			var cookies = document.cookie.split(';');
			for ( var i = 0; i < cookies.length; i++) {
				var cookie = jQuery.trim(cookies[i]);
				if (cookie.substring(0, name.length + 1) == (name + '=')) {
					cookieValue = decodeURIComponent(cookie
							.substring(name.length + 1));
					break;
				}
			}
		}
		return cookieValue;
	}
};