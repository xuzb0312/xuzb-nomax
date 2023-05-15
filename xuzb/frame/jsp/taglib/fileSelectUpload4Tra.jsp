<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String id = (String) request.getAttribute("id");
	String uiid = (String) request.getAttribute("uiid");
	String required = (String) request.getAttribute("required");
	String filetype = (String) request.getAttribute("filetype");
	String filedesc = (String) request.getAttribute("filedesc");
	String sizelimit = (String) request.getAttribute("sizelimit");
	String autoupload = (String) request.getAttribute("autoupload");

	String requiredStr = "1、该文件为非必填项；";
	if ("true".equalsIgnoreCase(required)) {
		requiredStr = "1、该文件为必填项；";
	}
	String filetypeStr = "2、文件类型为：" + filedesc + "(" + filetype + ")；";
	String sizelimitStr = "3、文件大小无限制(保证资源有效利用，请压缩文件后上传）；";
	if (!"0".equalsIgnoreCase(sizelimit)) {
		sizelimitStr = "3、上传文件的尺寸最大为" + sizelimit + "KB；";
	}
	String autouploadStr = "4、点击上传按钮文件暂存到服务器。";
	if ("true".equalsIgnoreCase(autoupload)) {
		autouploadStr = "4、选择文件后，文件会自动上传暂存到服务器。";
	}
%>
<ef:body>
	<div
		style="border: 1px solid #95B8E7; margin: 5px 2px; padding: 5px; text-align: center;">
		<form method="post" id="formfile" enctype="multipart/form-data"
			action="taglib.do?method=uploadFile4FileBox4Tra&uiid=<%=uiid%>&id=<%=id%>">
			<input type="hidden" name="con_id" id="con_id">
			<%
				if ("true".equalsIgnoreCase(autoupload)) {
			%>
			<input type="file" name="file" id="file"
				style="width: 98%; font-size: 13px; border: 1px solid #95B8E7; padding: 3px; background: #FFF;"
				onchange="autoUploadClick();" />
			<%
				} else {
			%>
			<input type="file" name="file" id="file"
				style="width: 98%; font-size: 13px; border: 1px solid #95B8E7; padding: 3px; background: #FFF;" />
			<%
				}
			%>
		</form>
	</div>
	<%
		if (!"true".equalsIgnoreCase(autoupload)) {
	%>
	<ef:buttons>
		<ef:button name="uploadbtn" value="上传" iconCls="icon-accept"
			onclick="uploadClick();"></ef:button>
	</ef:buttons>
	<%
		}
	%>
	<ef:form title="注意事项">
		<ef:blank />
		<ef:text color="red" value="<%=requiredStr%>" colspan="5" />
		<ef:blank />
		<ef:text color="red" value="<%=filetypeStr%>" colspan="5" />
		<ef:blank />
		<ef:text color="red" value="<%=sizelimitStr%>" colspan="5" />
		<ef:blank />
		<ef:text color="red" value="<%=autouploadStr%>" colspan="5" />
	</ef:form>
</ef:body>
<script type="text/javascript">
	var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
	var sizeLimit = <%=sizelimit%>;
	var filetype = "<%=filetype%>";
	function onLoadComplete() {
		$("#con_id").val(window.__con_id);
		$("#file").click();
	}

	function autoUploadClick(){
		var fileName = $("#file").val();
		if (fileName == null || fileName == "") {
			return;
		}

		uploadClick();
	}
	
	function uploadClick() {
		var fileName = $("#file").val();
		if (fileName == null || fileName == "") {
			alert("请先选择上传文件！！");
			$("#file").click();
			return;
		}
		if ("*.*" == filetype) {
		} else {
			var extName = fileName.substring(fileName.lastIndexOf('.') + 1);
			extName = "*." + extName;
			var arrType = filetype.split(";");
			var allFlag = false;
			for (var i = 0, n = arrType.length; i < n; i++) {
				if (extName == arrType[i]) {
					allFlag = true;
				}
			}
			if (!allFlag) {
				alert("文件【"+fileName+"】不符合系统上传文件类型["+filetype+"]！！");
				$("#file").val("");
				$("#file").click();
				return false;
			}
		}
		var size = getFileSize($("#file")[0]);
		if (size > sizeLimit && sizeLimit > 0) {
			alert("选择的文件【"+fileName+" 大小:"+size+"KB】大于最大文件上传限制["+sizeLimit+"KB]，不允许上传");
			$("#file").val("");
			$("#file").click();
			return false;
		}
		if(isIE || isIE11()){
			MsgBox.alert("正在上传,请稍候...");
		}else{
			showLoading();
		}
		
		$("#formfile").submit();
	}

	function getFileSize(target) {
		var fileSize = 0;
		if (isIE && !target.files) {
			try{
				var filePath = target.value;
				var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
				var file = fileSystem.GetFile(filePath);
				fileSize = file.Size;
			}catch (oE) {
				//获取文件大小出现异常-则不进行限制了
			}
		} else {
			fileSize = target.files[0].size;
		}
		var size = fileSize / 1024;
		size = NumberUtil.round(size, 0);
		return size;
	}
</script>