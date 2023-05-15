/**
 * 进度条对象 <br>
 * title：进度展示标题； <br>
 * cancelCfmMsg:点击取消时的确认信息，如果不传或者为空，则不进行提示。
 */
function ProgressBar(title, cancelCfmMsg) {
	// 首先请求后台，让后台创建一个progressbar-并返回唯一ID
	var creatPbUrl = new URL("taglib.do", "createProgressBar");
	var data = AjaxUtil.syncRequest(creatPbUrl);
	if (AjaxUtil.checkException(data)) {
		AjaxUtil.showException(data);
		return false;
	}
	var pbid = data;
	// 前台创建进度条窗口展示对象
	var htmlStr = "";
	htmlStr = htmlStr + "<div id=\"w_" + pbid
			+ "\" style=\"width:500px;height:200px;\">";
	htmlStr = htmlStr + "<div style=\"padding:10px;\">";
	htmlStr = htmlStr
			+ "<div id=\"msg_"
			+ pbid
			+ "\" style=\"font-size:14px;margin:10px 5px;height:38px;font-weight:bold;\"></div>";
	htmlStr = htmlStr + "<div id=\"p_" + pbid + "\"></div>";
	htmlStr = htmlStr + "</div>";
	htmlStr = htmlStr
			+ "<div style=\"border-top:1px solid #95B8E7;text-align:right;padding:12px 20px 0px 0px;margin-top:10px;\">";
	htmlStr = htmlStr + "<a id =\"btn_" + pbid + "\" href=\"#\">取消</a>";
	htmlStr = htmlStr + "</div>";
	htmlStr = htmlStr + "</div>";

	var winObj = $(htmlStr).appendTo("body");// 将数据放到body上；

	// --easyUI构建展示
	$("#w_" + pbid).window( {
		iconCls : "icon-application-go",
		title : title,
		modal : true,
		closed : false,
		minimizable : false,
		collapsible : false,
		resizable : false,
		maximizable : false,
		closable : false
	});

	// 进度条
	$("#p_" + pbid).progressbar( {
		text : "正在处理... {value}%",
		value : 0
	});

	// 按钮
	$("#btn_" + pbid).linkbutton( {
		iconCls : "icon-cross"
	});
	// 事件
	$("#btn_" + pbid).bind("click", function() {
		if (!chkObjNull(cancelCfmMsg)) {
			if (!confirm(cancelCfmMsg)) {
				return;
			}
		}
		ProgressBarUtil.getBar(pbid).destroy();
	});

	// 给bar设置自有属性
	this.id = pbid;// 给其设置id属性。
	this.windowObj = $("#w_" + pbid);
	this.msgObj = $("#msg_" + pbid);
	this.progressBarObj = $("#p_" + pbid);
	this.buttonObj = $("#btn_" + pbid);

	// 将该对象绑定到win对象上，便于根据ID获取。
	$("#w_" + pbid).data("progressbar", this);

	return this;
};

/**
 * 进度条操作的工具类
 */
var ProgressBarUtil = {
	// 根据pbid获取progressbar
	getBar : function(pbid) {
		var obj = $("#w_" + pbid);
		if (obj.length <= 0) {
			return false;
		}
		return obj.data("progressbar");
	},

	// 判断是否已经被销毁
	isDestroy : function(pbid) {
		var obj = $("#w_" + pbid);
		if (obj.length <= 0) {
			return true;
		}
		return false;
	}
};

/**
 * 设置进度条上的展示文字
 * 
 * @param msg
 * @return
 */
ProgressBar.prototype.setMsg = function(msg) {
	this.msgObj.text(msg);
};

/**
 * 设置百分比-值在0~100之间。
 * 
 * @param percent
 * @return
 */
ProgressBar.prototype.setPercent = function(percent) {
	this.progressBarObj.progressbar("setValue", percent);
};

/**
 * 获取当前的百分比-值在0~100之间。
 * 
 * @param percent
 * @return
 */
ProgressBar.prototype.getPercent = function() {
	return this.progressBarObj.progressbar("getValue");
};

/**
 * 销毁进度条
 * 
 * @param percent
 * @return
 */
ProgressBar.prototype.destroy = function() {
	// 告知后台，进度条取消
	var url = new URL("taglib.do", "destroyProgressBar");
	url.addPara("pbid", this.id);
	var data = AjaxUtil.syncRequest(url);
	if (AjaxUtil.checkException(data)) {
		AjaxUtil.showException(data);
		return false;
	}
	this.windowObj.removeData("progressbar");
	this.windowObj.window("destroy");
	return true;
};