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
	String autouploadStr="4、点击上传按钮文件暂存到服务器。";
	if("true".equalsIgnoreCase(autoupload)){
		autouploadStr="4、选择文件后，文件会自动上传暂存到服务器。";
	}
%>
<ef:body>
	<link rel="stylesheet" type="text/css"
		href="./frame/plugins/uploadify/uploadify.css?v=3.2.1" />
	<script type="text/javascript"
		src="./frame/plugins/uploadify/jquery.uploadify.min.js?v=3.2.1"></script>
	<div style="border: 1px solid #95B8E7; margin: 5px 2px; padding: 5px;">
		<input type="file" name="file" id="file" />
	</div>
	<%
		if (!"true".equalsIgnoreCase(autoupload)) {
	%>
	<ef:buttons>
		<ef:button name="uploadbtn" value="上传" iconCls="icon-accept" onclick="uploadClick();"></ef:button>
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
var id = "<%=id%>";
var uiid = "<%=uiid%>";
var required = "<%=required%>";
var filetype = "<%=filetype%>";
var filedesc = "<%=filedesc%>";
var sizelimit = "<%=sizelimit%>";
var autoupload = "<%=autoupload%>";
var autoupload = ("true" == autoupload.toLowerCase());
var sizelimit = new Number(sizelimit);
//页面加载完成后触发
function onLoadComplete() {
    $("#file").uploadify({
        auto: autoupload,
        buttonText: "文件浏览 . . .",
        fileObjName: "file",
        fileSizeLimit: sizelimit,
        fileTypeDesc: filedesc,
        fileTypeExts: filetype,
        method: "post",
        multi: false,
        removeTimeout: 1,
        swf: "frame/plugins/uploadify/uploadify.swf",
        uploader: "taglib.do;jsessionid=<%=pageContext.getSession().getId()%>?method=uploadFile4FileBox&uiid=" + uiid,
        height: 30,
        width: 423,
        queueSizeLimit: 1,
        successTimeout: 7200,
        onUploadSuccess: function(file, data, response) {
        	if (AjaxUtil.checkException(data)) {
				AjaxUtil.showException(data);
				return;
			}
        	closeWindow({id:id,name:file.name});
		},
        onUploadStart:function(file){
            try{
				getObject("uploadbtn").disable();
            }catch (e) {
			}
		},
		onInit:function(){
		    if ($.browser.msie) {
		    	$(".swfupload").attr("classid","clsid:D27CDB6E-AE6D-11cf-96B8-444553540000");
		    }
		}
    });
}
function uploadClick() {
    $("#file").uploadify("upload", "*");
}
</script>