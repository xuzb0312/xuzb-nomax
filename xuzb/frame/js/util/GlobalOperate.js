/**
 * 这个文件主要放全局相关的一些操作
 */
/**
 * easyui解析完成后，相关的操作，全局的 <br>
 * 说明（事件执行顺序）：<br>
 * 1.首先是执行pageFrame页面本身的parser.onComplete事件；<br>
 * 2.再次执行加载数据的jquery的$(function(){});<br>
 * 3.再次执行加载数据的parser.onComplete事件；<br>
 * 4.最后执行页面定义的onLoadComplete事件；<br>
 * 由以上可以看出，onLoadComplete最晚执行，所以程序应该全部放到onLoadComplete中执行，以保证数据均加载完成。
 * 
 */
$.parser.onComplete = function(context) {
	$.mask.definitions['X'] = '[0123456789X]'; // 身份证号码的最后一位
	$.mask.definitions['H'] = '[A-Fa-f0-9]'; // 16进制数
	// 对于form数据，在所有操作完成后，进行格式自修正
	$("body").find("[obj_type='form']:not([isSelfCorrFormat='true'])").each(function() {
		var formObj = new FormObj($(this));
		/**
		 * 数据格式自修正--form:从datasource中将数据取出后，没有进行特殊处理，均交给此处，进行格式化修正。
		 */
		formObj.selfCorrFormat();
	});
	// 增加提示处理
	$('.input-tip-msg-icon').tooltip({
		hideEvent: 'none',
		position: 'top',
		content: function() {
			var tipMsg = $(this).attr("_tipmsg");
			return $("<span style=\"color:#fff\">" + tipMsg + "</span>");
		},
		onShow: function() {
			var t = $(this);
			t.tooltip("tip").focus().unbind().bind("blur",
			function() {
				t.tooltip("hide");
			});
			t.tooltip("tip").css({
				backgroundColor: "#777",
				borderColor: "#777",
				maxWidth: "220px"
			});
		}
	});
};

/**
 * 处理按钮快捷键
 */
(function(win, undefined) {
	$(win.document).keydown(function(e) {
		if (e.altKey) {
			var whichStr = e.which.toString();
			$("a[_shortcutkey='" + whichStr + "']").each(function() {
				var obj = $(this);
				if (obj.hasClass("l-btn-disabled")) {
					// 只读则返回
					return;
				}
				obj.focus();
				obj.click();
			});
		}
	});
})(window);