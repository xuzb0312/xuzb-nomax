<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String watermark = (String)request.getAttribute("wm");//水印
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
			<title>打印</title>
			<style type="text/css">
/*通用调整样式*/
table {
	border-collapse: collapse;
	border-spacing: 0;
}

/*换页符的样式*/
.print-page-break-div {
	text-align: center;
	font-size: 12px;
	color: blue;
}

<%if ("true".equalsIgnoreCase(watermark)) {%> 
	body {
		background: url('frame/imgs/preview_bg.png') repeat;
	}
<%}%>

/*打印时该区域不进行打印*/
@media print {
	.print-page-break-div {
		display: none;
	}
<%if ("true".equalsIgnoreCase(watermark)) {%> 
	body {
		background: #FFFFFF;
	}
<%}%>
}
</style>
			<script type="text/javascript"
				src="frame/js/jquery-1.7.2.min.js?version=1.7.2"></script>
			<script type="text/javascript"
				src="frame/plugins/ueditor/ueditor.parse.min.js"></script>
			<script type="text/javascript"
				src="frame/plugins/html2canvas/html2canvas.min.js"></script>
			<script type="text/javascript"
				src="frame/plugins/html2canvas/html2canvas.svg.min.js"></script>
			<script type="text/javascript"
				src="frame/plugins/lodop/LodopFuncs.js"></script>
	</head>
	<body>
	</body>
	<script type="text/javascript">
<%if ("true".equalsIgnoreCase(watermark)) {%> 
		//屏蔽右击
		document.body.oncontextmenu = document.body.ondragstart = document.body.onselectstart = document.body.onbeforecopy = function() {
			return false;
		};
		document.body.onselect = document.body.oncopy = document.body.onmouseup = function() {
			document.selection.empty();
		};
<%}%>
		$(function(){
			//IE下会报个错误，但是不影响使用。
			var printerid="<%=(String) request.getAttribute("printerid")%>";
			$("body").html("<div id=\"printer_content\">"+window.parent.$("#"+printerid).data("printdata")+"</div>");
			uParse("#printer_content", {rootPath: "frame/plugins/ueditor/"});
        });

		function downloadImg() {
			html2canvas($("#printer_content"), {
				onrendered: function(canvas) {
					var url = canvas.toDataURL();
					//以下代码为下载此图片功能
					var triggerDownload = $("<a>").attr("href", url).attr("download", "打印图片.png").appendTo("body");
					triggerDownload[0].click();
					triggerDownload.remove();
				}
			});
		};

		//使用打印插件进行打印，打印模板中必须存在page#的div模板标签，方可进行打印
		function printByjatools(para, ischoosePrinter, isPreview) {
			if ($("#jatoolsPrinter").length <= 0) {
				// 插入object插件
				$("body").append("<OBJECT  ID=\"jatoolsPrinter\" CLASSID=\"CLSID:B43D3361-D075-4BE2-87FE-057188254255\" codebase=\"jatoolsPrinter.cab#version=8,6,0,0\"></OBJECT>");
			}
			para.copyrights = "杰创软件拥有版权  www.jatools.com";
			para.documents = document;
			if (isPreview) {
				document.getElementById("jatoolsPrinter").printPreview(para, ischoosePrinter);
			} else {
				document.getElementById("jatoolsPrinter").print(para, ischoosePrinter);
			}
		}

		//lodop打印操作
		var LODOP;
		function printByLodop(para){
			if (typeof (obj) == "undefined"){
				loadLodop(function(){
					LODOP = getLodop();
					//增加授权信息
					//LODOP.SET_LICENSES("公司名称","序列号","","");
					lodopPrint(LODOP, para);
				});
			}else{
				lodopPrint(LODOP, para);
			}
		}

		function lodopPrint(lodop, para) {
			if (typeof(para) == "undefined") {
				para = {};
			}
			lodop.PRINT_INITA(0, 0, "100%", "100%", "系统打印");
			lodop.SET_PRINT_STYLE("Alignment", 1);

			var pagePercent = para.pagePercent;
			if (typeof(pagePercent) == "undefined") {
			    pagePercent = "Full-Page";
			}
			lodop.SET_PRINT_MODE("PRINT_PAGE_PERCENT", pagePercent);
			if (typeof(para.printDirection) != "undefined") {
				this.LODOP.SET_PRINT_PAGESIZE(para.printDirection, 0, 0, "");
			}
			String.prototype.replaceAll = function(s1, s2) {
				var reg = new RegExp(s1, "gm");
				return this.replace(new RegExp(s1, "gm"), s2);　　
			};
			var printContent = $("#printer_content").html();
			printContent = printContent.replaceAll("<div class=\"print-page-break-div\">-----------\\*该处自动分页\\*-----------<\\/div><div style=\"page-break-after: always;\"><\\/div>", "<div style=\"page-break-after: always;\">&nbsp;</div>");
			lodop.ADD_PRINT_HTM(0, 0, "100%", "100%", printContent);
			//打印前的其他操作
			if(!para.onPrePrint(lodop)){
				return;
			}
			var isPrintSuccess = false;
			if (para.preview) {
				lodop.PREVIEW();
				isPrintSuccess = true;
			} else {
				if (para.ischoosePrinter) {
					isPrintSuccess = lodop.PRINTA();
				}else{
					isPrintSuccess = lodop.PRINT();
				}
			}
			para.onComplete(isPrintSuccess);//打印完成
		}
	</script>
</html>
