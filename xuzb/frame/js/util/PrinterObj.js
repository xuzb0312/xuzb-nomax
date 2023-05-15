/**
 * 打印对象
 */
function PrinterObj(obj) {
	this.type = "printer";
	this.obj = obj;
	this.id = this.obj.attr("id");
	this.conobj = $("#" + this.id + "_con");
};

/**
 * 初始化
 * 
 * @return
 */
PrinterObj.prototype.init = function() {
	this.conobj.html("");// 清空内部数据
	var height = this.conobj.height();
	// 获取数据
	var printdata = this.obj.data("printdata");
	var watermark = this.obj.attr("_watermark");
	var frameid = this.id + "_frame";
	var framestr = "<iframe id='" + frameid
			+ "' scrolling=\"auto\" frameborder=\"0\" "
			+ "src=\"taglib.do?method=fwdPrinterPageFrame&printerid=" + this.id
			+ "&wm=" + watermark + "&frameid=" + randomString(6) + "\" "
			+ "style=\"width:100%;height:" + height + "px;\"></iframe>";
	this.conobj.html(framestr);
};

/**
 * 重新设置数据
 */
PrinterObj.prototype.setPrintData = function(data) {
	this.obj.data("printdata", data);
	this.init();
}

/**
 * 初始化
 * 
 * @return
 */
PrinterObj.prototype.print = function() {
	if ($.browser.msie) {// ie浏览器
		var frameid = this.id + "_frame";
		window.frames[frameid].focus();
		window.frames[frameid].print();
	} else if (isIE11()) { // IE11的特殊判断
		var frameid = this.id + "_frame";
		window.frames[frameid].focus();
		window.frames[frameid].print();
	} else {
		var frame = $("#" + this.id + "_frame").get(0);
		frame.focus();
		frame.contentWindow.print();
	}
};

/**
 * 插件打印（jatools） <br>
 * settings: <br>
 * {paperName:'a4',// 选择a4纸张进行打印<br>
 * orientation:2,// 选择横向打印,1为纵向，2为横向<br>
 * topMargin:100, leftMargin:100, bottomMargin:100, rightMargin:100 ,//
 * 设置上下左距页边距为10毫米，注意，单位是 1/10毫米<br>
 * printer:'OKi5530'}//设置到打印机 'OKi5530'<br>
 * ischoosePrinter:是否展示选择打印机的窗口<br>
 * isPreview:是否预览<br>
 * 
 * @return
 */
PrinterObj.prototype.printByJatools = function(settings, ischoosePrinter,
		isPreview) {
	var para = {};
	if (!chkObjNull(settings)) {
		para.settings = settings;
	}
	if (chkObjNull(ischoosePrinter)) {
		ischoosePrinter = true;
	}
	if (chkObjNull(isPreview)) {
		isPreview = false;
	}
	if ($.browser.msie || isIE11()) {// ie浏览器-暂时无法导出，暂时这么写。
		try {
			var frameid = this.id + "_frame";
			window.frames[frameid].printByjatools(para, ischoosePrinter,
					isPreview);
		} catch (oE) {
			alert("打印失败：" + oE.message + "，请检查是否正确安装了打印插件【点击确定下载安装插件，重启浏览器后再试】");
			PrinterObj.downloadJatoolsPlugins();
		}
	} else {
		this.print();// 非IE浏览器无法进行打印（插件仅支持IE浏览器）
	}
};

/**
 * Lodop打印 <br>
 * printDirection:1纵向、2横向<br>
 * preview:true预览，false直接打印<br>
 * ischoosePrinter:true选择打印机，false否<br>
 * pagePercent:打印缩放方式，值：“Full-Width” –宽度按纸张的整宽缩放； “Full-Height”–高度按纸张的整高缩放：
 * “Full-Page” –按整页缩放，也就是既按整宽又按整高缩放；默认：Full-Page
 * 
 * @return
 */
PrinterObj.prototype.printByLodop = function(printDirection, ischoosePrinter,
		preview, pagePercent, onPrePrint, onComplete) {
	var para = {};
	if (!chkObjNull(printDirection)) {
		para.printDirection = printDirection;
	}
	if (!chkObjNull(preview)) {
		para.preview = preview;
	} else {
		para.preview = true;
	}
	if (chkObjNull(pagePercent)) {
		para.pagePercent = "Full-Page";
	} else {
		para.pagePercent = pagePercent;
	}
	if (chkObjNull(onPrePrint)) {
		para.onPrePrint = function(lodop) {
			return true;
		};
	} else {
		para.onPrePrint = onPrePrint;
	}
	if (chkObjNull(onComplete)) {
		para.onComplete = function(isPrintSuccess) {
		};
	} else {
		para.onComplete = onComplete;
	}

	if (!chkObjNull(ischoosePrinter)) {
		para.ischoosePrinter = ischoosePrinter;
	} else {
		para.preview = false;
	}

	if ($.browser.msie || isIE11()) {// ie浏览器
		var frameid = this.id + "_frame";
		window.frames[frameid].printByLodop(para);
	} else {
		var frame = $("#" + this.id + "_frame").get(0);
		frame.contentWindow.printByLodop(para);
	}
};

/**
 * 下载打印插件
 * 
 * @return
 */
PrinterObj.downloadJatoolsPlugins = function() {
	downloadFile2Form("frame/plugins/jatoolsPrinter/setup.exe");
};

/**
 * 将打印的文件转换图片文件进行下载
 */
PrinterObj.prototype.downloadImage = function() {
	if ($.browser.msie || isIE11()) {// ie浏览器-暂时无法导出，暂时这么写。
		alert("IE浏览器无法下载，请使用火狐或者谷歌浏览器重试！");
	} else {
		var frame = $("#" + this.id + "_frame").get(0);
		frame.contentWindow.downloadImg();
	}
};

/**
 * 将打印的文件转换为Html文件进行下载
 * 
 * @return
 */
PrinterObj.prototype.downloadHtml = function() {
	var url = new URL("taglib.do", "downLoadHtml4Printer");
	url.addPara("__jbjgid", getJbjgid("00000000"));
	url.addPara("__jbjgqxfw", getJbjgqxfw("00000000"));
	url.addPara("__yhid", getPageYhid());
	url = url.getRealURLString();

	// 下载文件
	var submitDataForm = document.createElement("FORM");
	submitDataForm.method = "POST";
	submitDataForm = document.body.appendChild(submitDataForm);
	var urlArray = url.split("&");
	for ( var i = 1; i < urlArray.length; i++) {
		var urlEle = urlArray[i];
		var o = document.createElement("INPUT");
		o.type = "hidden";
		var key = urlEle.split("=")[0];
		o.name = key;
		o.id = key;
		o.value = decodeURIComponent(urlEle.substr(key.length + 1));
		submitDataForm.appendChild(o);
	}

	// 放入打印数据
	var o = document.createElement("INPUT");
	o.type = "hidden";
	o.name = "printdata";
	o.id = "printdata";
	o.value = this.obj.data("printdata");
	submitDataForm.appendChild(o);

	submitDataForm.action = urlArray[0];
	submitDataForm.target = "_self";
	submitDataForm.onsubmit = function() {
		return false; // 防止重复提交
	};
	submitDataForm.submit();
	submitDataForm.outerHTML = "";
};