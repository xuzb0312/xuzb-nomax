/**
 * 日历对象，对日历的相关操作的封装-其依赖与laydate插件
 */
function CalendarObj(textinputobj) {
	var id = textinputobj.attr("id");
	if (chkObjNull(id)) {
		return;
	}
	var dataType = textinputobj.attr("dataType");
	if ("date" != dataType) {
		return;
	}

	// 处理函数
	var dealCalendar = function(e) {
		var target = $(e.target);
		var mask = target.attr("mask");
		if (chkObjNull(mask)) {
			mask = "yyyy-MM-dd";
		}
		if ("readonly" == target.attr("readonly")) {
			return;
		}

		/**
		 * 对于只有时间的情况， 则不进行弹出选择框，选择日期
		 */
		if (!mask.contains("d") && mask.contains("h")) {
			return;
		}

		var dtmExe = new DateTimeMaskExe();
		if (!dtmExe.isValidDateTime(e)) {
			// 判断日期是否合法
			target.val("");
		}

		var paraMap = new HashMap();
		paraMap.put("format", mask);
		paraMap.put("istime", true);
		paraMap.put("choose", function(dates) {
			target.focus();
		});
		laydate(paraMap.values);
	};

	/**
	 * 将双击事件保定到该控件上
	 */
	textinputobj.bind("dblclick", dealCalendar);
	textinputobj.bind("keydown", function(e) {
		if (e.which == 13) {
			dealCalendar(e);
		}
	});
}