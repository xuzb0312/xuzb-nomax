<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String id = (String) request.getAttribute("id");
	String uiid = (String) request.getAttribute("uiid");
	String filetype = (String) request.getAttribute("filetype");
	String sizelimit = (String) request.getAttribute("sizelimit");
	String autoupload = (String) request.getAttribute("autoupload");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<title>文件上传</title>
		<link rel="stylesheet" type="text/css"
			href="./frame/plugins/bootstrap/3.3.7/css/bootstrap.min.css?v=3.3.7" />
		<link rel="stylesheet" type="text/css"
			href="./frame/plugins/bootstrap/fileinput/css/fileinput.min.css?v=4.4.1" />

		<script src="./frame/plugins/bootstrap/jquery/jquery.min.js?v=2.2.1"
			type="text/javascript"></script>
		<script
			src="./frame/plugins/bootstrap/fileinput/js/plugins/sortable.min.js"
			type="text/javascript"></script>
		<script src="./frame/plugins/bootstrap/fileinput/js/fileinput.min.js"
			type="text/javascript"></script>
		<script src="./frame/plugins/bootstrap/fileinput/js/locales/zh.js"
			type="text/javascript"></script>
		<script src="./frame/plugins/bootstrap/3.3.7/js/bootstrap.min.js"
			type="text/javascript"></script>
		<script type="text/javascript">
	var __data;//存储回调返回数据
	//关闭窗口的方法-只对window窗口有效。
	function closeWindow(data) {
		__data = data;
		var con_id = window.__con_id;
		window.parent.$("#" + con_id).window("close");
	}
</script>
	</head>
	<body>
		<div style="padding: 5px 10px;">
			<input id="file" type="file" class="file" />
		</div>
	</body>
	<script type="text/javascript">
	var id = "<%=id%>";
	var uiid = "<%=uiid%>";
	var filetype = "<%=filetype%>";
	var sizelimit = "<%=sizelimit%>";
	var autoupload = "<%=autoupload%>";
	var autoupload = ("true" == autoupload.toLowerCase());
	var sizelimit = new Number(sizelimit);
	var fileOpt = {
		language : "zh",
		dropZoneTitle: '拖拽文件到这里 …',
		uploadUrl : "taglib.do?method=uploadFile4FileBox&uiid=" + uiid,
		maxFileSize : sizelimit,
		maxFilesNum : 1,
		maxFileCount : 1,
		browseClass : "btn btn-primary",
		enctype : "multipart/form-data",
		allowedPreviewTypes: ['image', 'html', 'text']
	};
	if (filetype == "*.*" || filetype == null || filetype == "") {
	} else {
		var arrFileType = filetype.split(";");
		for ( var i = 0, n = arrFileType.length; i < n; i++) {
			arrFileType[i] = arrFileType[i].split(".")[1];
		}
		fileOpt["allowedFileExtensions"] = arrFileType;
	}
	$("#file").fileinput(fileOpt);//初始化
	$("#file").on("fileuploaded", function(event, data, previewId, index) {
		closeWindow( {
			id : id,
			name : data.filenames[0]
		});
	});

	if (autoupload) {//自动上传
		$("#file").on('filebatchselected', function(event, data, id, index) {
			$(this).fileinput("upload");
		});
	}
</script>
</html>