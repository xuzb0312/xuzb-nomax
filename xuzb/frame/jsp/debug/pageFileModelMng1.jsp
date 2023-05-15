<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="查询条件" name="formQuery">
		<ef:textinput name="wjbs" label="文件标识" />
		<ef:textinput name="wjmc" label="文件名称" />
		<ef:textinput name="wjgs" label="文件格式" />
		<ef:buttons closebutton="false">
			<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridFile" title="文件模板" multi="true">
		<ef:columnText name="wjbs" label="文件标识" width="10" />
		<ef:columnText name="wjmc" label="文件名称" width="12" />
		<ef:columnText name="wjgs" label="文件格式" width="8" />
		<ef:columnText name="cjsj" label="创建时间" dataType="date"
			sourceMask="yyyyMMddhhmmss" mask="yyyy-MM-dd hh:mm:ss" width="10" />
		<ef:columnText name="bz" label="备注" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="查看" onclick="btnViewClick();" iconCls="icon-eye"></ef:button>
		<ef:button value="新增" onclick="btnAddClick();"></ef:button>
		<ef:button value="修改" onclick="btnModifyClick();"></ef:button>
		<ef:button value="下载" onclick="btnDownloadClick();"
			iconCls="icon-down"></ef:button>
		<ef:button value="下载系统全部文档" onclick="btnDownloadAllClick();"
			iconCls="icon-page-white-zip"></ef:button>
		<ef:button value="删除" onclick="btnDelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("wjbs").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formQuery").clear();
		getObject("gridFile").clear();
		getObject("wjbs").focus();
	}

	//查询
	function btnQueryClick() {
		var url = new URL("debug.do", "queryFileModel");
		url.addForm("formQuery");
		AjaxUtil.asyncRefreshBizData(url, "gridFile:dsfile");
	}

	//新增
	function btnAddClick() {
		var url = new URL("debug.do", "fwdFileModelAdd1");
		openWindow("新增文件模板", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			getObject("formQuery").clear();
			getObject("wjbs").setValue(data);
			btnQueryClick();
		});
	}

	//删除
	function btnDelClick() {
		var grid = getObject("gridFile");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		if (!confirm("是否确认对选中的文件模板进行删除？")) {
			return;
		}

		var url = new URL("debug.do", "deleteFileModel");
		url.addQueryGridSelectData("gridFile", "wjbs");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			btnQueryClick();
		}
	}

	//下载
	function btnDownloadClick(){
		var grid = getObject("gridFile");
		if (!grid.isSelectOneRow()) {
			alert("请先选择一行数据");
			return;
		}
		var row = grid.getDefalutSelectedRow();
		var wjbs = grid.getCell(row, "wjbs");
		downloadSysFile(wjbs);
	}

	//查看
	function btnViewClick(){
		var grid = getObject("gridFile");
		if (!grid.isSelectOneRow()) {
			alert("请先选择一行数据");
			return;
		}
		var row = grid.getDefalutSelectedRow();
		var wjbs = grid.getCell(row, "wjbs");
		var wjgs = grid.getCell(row, "wjgs");
		if("pdf" != wjgs.toLowerCase() && "txt" != wjgs.toLowerCase() 
				&& "jpg" != wjgs.toLowerCase() 
				&& "jpeg" != wjgs.toLowerCase()
				&& "bmp" != wjgs.toLowerCase()
				&& "png" != wjgs.toLowerCase()){
			alert("当前只能在线查阅PDF,TXT,JPG,JPEG,BMP,PNG格式的文档。");
			return;
		}
		var url = new URL("debug.do","fwdFileModelView");
		url.addPara("wjbs", wjbs);
		openPdfFile("系统PDF文件查看", url, "big");
	}

	//下载所有的文档
	function btnDownloadAllClick(){
		if(!confirm("确定需要下载系统中所有的系统文档吗（文件较多可能会下载较慢）？")){
			return;
		}
		var url = new URL("debug.do", "downloadAllFileModel");
		downloadFile2Form(url);
	}

	//文件修改操作
	function btnModifyClick(){
		var grid = getObject("gridFile");
		if (!grid.isSelectOneRow()) {
			alert("请先选择一行数据");
			return;
		}
		var row = grid.getDefalutSelectedRow();
		var wjbs = grid.getCell(row, "wjbs");
		var url = new URL("debug.do","fwdFileModelModify");
		url.addPara("wjbs", wjbs);
		openWindow("修改文件模板", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			btnQueryClick();
		});
	}
</script>