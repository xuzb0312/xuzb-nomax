<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	String filetype = (String) request.getAttribute("__filetype");
	String filedesc = (String) request.getAttribute("__filedesc");
	String sizelimit = (String) request.getAttribute("__sizelimit");
	String jsppage = (String) request.getAttribute("__jsppage");
	String savecontroller = (String) request.getAttribute("__savecontroller");
	String savemethod = (String) request.getAttribute("__savemethod");
	String paras = (String) request.getAttribute("__paras");

	String filetypeStr = "1、文件类型为：" + filedesc + "(" + filetype + ")；";
	String sizelimitStr = "2、文件大小无限制(保证资源有效利用，请压缩文件后上传）；";
	if (!"0".equalsIgnoreCase(sizelimit)) {
		sizelimitStr = "2、上传文件的尺寸最大为" + sizelimit + "KB；";
	}
	String uploader = savecontroller + ";jsessionid="
			+ pageContext.getSession().getId() + "?method="
			+ savemethod;
	boolean ishaveform = false;
	if (null != jsppage && !"".equals(jsppage)
			&& !"null".equals(jsppage)) {
		if (!jsppage.startsWith("/")) {
			jsppage = "/" + jsppage;
		}

		ishaveform = true;
	}
%>
<ef:body>
	<ef:hiddenInput name="pgbarid" />
	<ef:hiddenInput name="htjdbz" value="0" />
	<div style="border: 1px solid #95B8E7; margin: 5px 2px; padding: 5px;">
		<input type="file" name="file" id="file" />
		<div id="fileList" style="margin-bottom: 5px; margin-left: 50px;">
		</div>
	</div>
	<ef:form>
		<ef:blank />
		<ef:text color="red" value="<%=filetypeStr%>" colspan="5" />
		<ef:blank />
		<ef:text color="red" value="<%=sizelimitStr%>" colspan="5" />
	</ef:form>
	<%
		if (ishaveform) {
	%>
	<jsp:include page="<%=jsppage %>"></jsp:include>
	<%
		}
	%>
	<ef:buttons>
		<ef:button name="uploadbtn" value="上传" iconCls="icon-accept"
			onclick="uploadClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
var filetype = "<%=filetype%>";
var filedesc = "<%=filedesc%>";
var sizelimit = "<%=sizelimit%>";
var sizelimit = new Number(sizelimit);
var paras = <%=paras%>;
paras = new HashMap(paras);
//页面加载完成后触发
function onLoadComplete() {
  $("#file").uploadify({
      auto: false,
      buttonText: "文件浏览 . . .",
      fileObjName: "file",
      fileSizeLimit: sizelimit,
      fileTypeDesc: filedesc,
      fileTypeExts: filetype,
      method: "post",
      multi: false,
      removeTimeout: 1,
      swf: "frame/plugins/uploadify/uploadify.swf",
      uploader: "<%=uploader%>",
      height: 30,
      width: 523,
      queueID: "fileList",
      queueSizeLimit: 1,
      successTimeout: 7200,
      onUploadSuccess: function(file, data, response) {
        var pgBarId = getObject("pgbarid").getValue();
   	  	if (!ProgressBarUtil.isDestroy(pgBarId)) {
   	  		var pgBarObj = ProgressBarUtil.getBar(pgBarId);
   	  		pgBarObj.destroy();
		}
      	if (AjaxUtil.checkException(data)) {
				AjaxUtil.showException(data);
				return;
			}
		if(!response){
			//后台无响应
			alert("后台业务正在操作,请稍候...");
		}
		var mapData = new HashMap();
		mapData.put("file", file);
		mapData.put("data", data);
		mapData.put("response", response);
      	closeWindow(mapData);
		},
      onUploadStart:function(file){
          try{
            <%if (ishaveform) {%>
              	paras.combine(getObject("formSet").getMapData());
            	<%}%>
            paras.put("__pgbarid", getObject("pgbarid").getValue());    
          	paras.put("__jbjgid", getJbjgid("00000000"));
          	paras.put("__jbjgqxfw", getJbjgqxfw("00000000"));
          	paras.put("__yhid", getPageYhid());
              $("#file").uploadify("settings", "formData", paras.values);
				getObject("uploadbtn").disable();
          }catch (e) {
			}
		},
		onUploadProgress:function(file, bytesUploaded, bytesTotal, totalBytesUploaded, totalBytesTotal){
			var pgBarId = getObject("pgbarid").getValue();
			var htjdbz = getObject("htjdbz").getValue();//后台进度标志
			if("0" == htjdbz){
				if (!ProgressBarUtil.isDestroy(pgBarId)) {
					var pgPercent =  100;
					if(totalBytesTotal > 0){
						pgPercent = NumberUtil.round((totalBytesUploaded * 100) / totalBytesTotal, 0)
					}
					var pgBarObj = ProgressBarUtil.getBar(pgBarId);
					pgBarObj.setMsg("文件正在上传中....");
					pgBarObj.setPercent(pgPercent);
					if(pgPercent >= 100){
						getObject("htjdbz").setValue("1");//使用后台进度
						setTimeout(setProgressBarInfo, 500);
					}
				}
			}
		}
  });

  <%if (ishaveform) {%>
    	getObject("formSet").focus();
  	<%}%>
}
function uploadClick() {
	<%if (ishaveform) {%>
	 	if(!getObject("formSet").chkFormData(true)){
			return;
		}
	 	<%}%>
	var num = $("#file").data("uploadify").queueData.queueLength;
	if(num <=0 ){
		alert("请先选择上传文件");
		return;
	}

	//进度条
	var pgBar = new ProgressBar("正在上传文件...", "确定取消该业务操作吗？");
	var pgBarId = pgBar.id;
	pgBar.setMsg("文件正在上传中...");
	getObject("pgbarid").setValue(pgBarId);

	//上传
  	$("#file").uploadify("upload", "*");

  	getObject("uploadbtn").disable();
}

//更新进度条
function setProgressBarInfo() {
	var pgBarId = getObject("pgbarid").getValue();
	var jdUrl = new URL("taglib.do", "getProgressBarInfo");
	jdUrl.addPara("pbid", pgBarId);
	var data = AjaxUtil.syncRequest(jdUrl);
	try {
		var dataMap = new HashMap(data);
		var pgmsg = dataMap.get("msg");
		var pgPercent = dataMap.get("percent");

		// 更该进度条信息
		if (!ProgressBarUtil.isDestroy(pgBarId)) {
			var pgBarObj = ProgressBarUtil.getBar(pgBarId);
			pgBarObj.setMsg(pgmsg);
			pgBarObj.setPercent(pgPercent);
			setTimeout(setProgressBarInfo, 2000);
		}
	} catch (e) {
		// 发生了异常不予处理
	}
}
</script>