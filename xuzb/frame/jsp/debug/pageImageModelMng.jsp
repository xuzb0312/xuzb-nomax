<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form name="formQuery" title="查询条件">
		<ef:textinput name="mbmc" label="模板名称" colspan="4"
			prompt="请输入格式编号、名称进行模糊检索..." />
		<ef:buttons closebutton="false" colspan="2">
			<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridImageModel" title="图片模板信息">
		<ef:columnText name="mbid" label="模板ID" width="4" hidden="true" />
		<ef:columnText name="mbbh" label="模板编号" width="6" />
		<ef:columnText name="mbmc" label="模板名称" width="10" />
		<ef:columnText name="mbms" label="模板描述" width="15" />
		<ef:columnCheckBox label="模板状态" name="mbzt" width="5" />
		<ef:columnText name="pzdq" label="配置地区" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="查看模板" iconCls="icon-application-view-gallery"
			onclick="btnViewClick();"></ef:button>
		<ef:button value="查看模板(PDF)" iconCls="icon-application-view-gallery"
			onclick="btnViewPDFClick();"></ef:button>
		<ef:button value="新增" onclick="btnAddClick();"></ef:button>
		<ef:button value="修改" onclick="btnModClick();"></ef:button>
		<ef:button value="设计模板" onclick="btnDgClick();"
			iconCls="icon-chart-bar-edit"></ef:button>
		<ef:button value="更改地区配置" iconCls="icon-cog-edit"
			onclick="btnSetClick();"></ef:button>
		<ef:button value="删除" onclick="btnDelClick();"></ef:button>
		<ef:button value="上传格式模板" iconCls="icon-page-add"
			onclick="btnUploadClick();"></ef:button>
		<ef:button value="下载选中模板" onclick="btnDownloadSelectClick();"
			iconCls="icon-page"></ef:button>
		<ef:button value="下载所有模板" onclick="btnDownloadAllClick();"
			iconCls="icon-page-white-zip"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		btnQueryClick();
	}

	function btnQueryClick() {
		var url = new URL("debug.do", "queryImageModelInfo");
		url.addForm("formQuery");
		AjaxUtil.asyncRefreshData(url, "gridImageModel:dsimagemodel");
	}

	function btnClearClick() {
		getObject("formQuery").clear();
		getObject("gridImageModel").clear();
		getObject("formQuery").focus();
	}

	//新增
	function btnAddClick() {
		var url = new URL("debug.do", "fwdImageModelAdd");
		openWindow("新增【图片模板】", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			btnClearClick();

			var mapData = new HashMap(data);
			getObject("mbmc").setValue(mapData.get("mbmc"));

			btnQueryClick();
		});
	}

	//删除
	function btnDelClick() {
		var grid = getObject("gridImageModel");
		if (!grid.isSelectRow()) {
			alert("请先选择一条【图片模板】信息");
			return;
		}
		if (!confirm("确认要删除【图片模板】信息吗？")) {
			return;
		}

		var rowid = grid.getSelectedRow();
		var url = new URL("debug.do", "saveImageModelDelete");
		url.addPara("mbid", grid.getCell(rowid, "mbid"));
		AjaxUtil.syncBizRequest(url, function(data) {
			alert("删除成功");
			btnQueryClick();
		});
	}

	//修改
	function btnModClick() {
		var grid = getObject("gridImageModel");
		if (!grid.isSelectRow()) {
			alert("请先选择一条【图片模板】信息");
			return;
		}

		var rowid = grid.getSelectedRow();
		var url = new URL("debug.do", "fwdImageModelModify");
		url.addPara("mbid", grid.getCell(rowid, "mbid"));
		openWindow("修改【图片模板】信息", url, "normal", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			btnQueryClick();
		});
	}

	//设置
	function btnSetClick() {
		var grid = getObject("gridImageModel");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		
		var url = new URL("debug.do", "fwdImageModelConfigSet");
		url.addPara("mbid", grid.getCell(grid.getSelectedRow(), "mbid"));
		openWindow("更改地区配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			btnQueryClick();
		});
	}

	//获取图片信息
	function getImageOptions(mbid){
		var url = new URL("debug.do", "getImageModelOptions");
		url.addPara("mbid", mbid);
		var data = AjaxUtil.syncBizRequest(url);
		if(AjaxUtil.checkIsGoOn(data)){
			var mapData = new HashMap(data);
			return mapData.values;
		}else{
			return false;
		}
	}

	//查看
	function btnViewClick(){
		var grid = getObject("gridImageModel");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var mbid = grid.getCell(grid.getSelectedRow(), "mbid");
		
		var url = new URL("debug.do", "fwdImageModelView");
		url.addPara("mbid", mbid);
		openWindow("图片模板查看", url, "big");
	}

	function btnViewPDFClick(){
		var grid = getObject("gridImageModel");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var mbid = grid.getCell(grid.getSelectedRow(), "mbid");
		
		var url = new URL("debug.do", "fwdImageModelView4PDF");
		url.addPara("mbid", mbid);
		openPdfFile("图片模板查看", url, "big");
	}

	//设计模板
	function btnDgClick(){
		var grid = getObject("gridImageModel");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var mbid = grid.getCell(grid.getSelectedRow(), "mbid");
		var opts = getImageOptions(mbid);
		if(false == opts){
			return;
		}
		openImgModelEditor(opts, function(opts){
			var url = new URL("debug.do", "saveImageModelDesign");
			url.addPara("items", JSON.stringify(opts.items));
			url.addPara("mbid", mbid);
			var data = AjaxUtil.syncBizRequest(url);
			if(AjaxUtil.checkIsGoOn(data)){
				alert("保存成功");
				return true;
			}
		});
	}

	//下载选中文件
	function btnDownloadSelectClick(){
		var grid = getObject("gridImageModel");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		
		var url = new URL("debug.do", "downloadSelectImageModel");
		url.addPara("mbid", grid.getCell(grid.getSelectedRow(), "mbid"));
		downloadFile2Form(url);	
	}

	//上传打印格式
	function btnUploadClick(){
		var url = new URL("debug.do", "fwdImageModelUpload");
		openWindow("上传打印格式", url, "small", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			get("mbmc").setValue(data);
			btnQueryClick();
		});
	}

	function btnDownloadAllClick(){
		var url = new URL("debug.do", "downloadAllImageModel");
		downloadFile2Form(url);	
	}
</script>