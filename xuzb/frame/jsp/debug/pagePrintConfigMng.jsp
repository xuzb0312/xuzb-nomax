<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="查询条件" name="formQuery">
		<ef:textinput name="gsmc" label="格式名称" colspan="4"
			prompt="请输入格式类型编号、名称进行模糊检索..." />
		<ef:buttons colspan="2" closebutton="false">
			<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridPrint" title="打印格式信息">
		<ef:columnText name="gsid" label="格式ID" hidden="true" />
		<ef:columnText name="gslxbh" label="格式类型编号" width="10" />
		<ef:columnText name="gsmc" label="格式名称" width="12" />
		<ef:columnText name="gsms" label="格式描述" width="15" />
		<ef:columnCheckBox label="格式有效标志" name="gszt" width="5" />
		<ef:columnText name="pzdq" label="配置地区" width="25" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="查看表样" iconCls="icon-application-view-gallery"
			onclick="btnViewClick();"></ef:button>
		<ef:button value="新增格式模板" iconCls="icon-add" onclick="btnAddClick();"></ef:button>
		<ef:button value="上传格式模板" iconCls="icon-page-add"
			onclick="btnUploadClick();"></ef:button>
		<ef:button value="修改模板内容" iconCls="icon-report-edit"
			onclick="btnModifyClick();"></ef:button>
		<ef:button value="更改地区配置" iconCls="icon-cog-edit"
			onclick="btnSetClick();"></ef:button>
		<ef:button value="删除格式模板" iconCls="icon-delete"
			onclick="btnDelClick();"></ef:button>
		<ef:button value="下载选中模板" onclick="btnDownloadSelectClick();"
			iconCls="icon-page"></ef:button>
		<ef:button value="下载所有模板" onclick="btnDownloadAllClick();"
			iconCls="icon-page-white-zip"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		get("gsmc").focus();
	}

	function btnDownloadAllClick(){
		var url = new URL("debug.do", "downloadAllPrintModel");
		downloadFile2Form(url);	
	}

	//下载选中文件
	function btnDownloadSelectClick(){
		var grid = getObject("gridPrint");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		
		var url = new URL("debug.do", "downloadSelectPrintModel");
		url.addPara("gsid", grid.getCell(grid.getSelectedRow(), "gsid"));
		downloadFile2Form(url);	
	}
	
	//清空
	function btnClearClick() {
		get("formQuery").clear();
		get("gridPrint").clear();
		get("gsmc").focus();
	}
	//查询
	function btnQueryClick() {
		var gsmc = get("gsmc").getValue();
		var url = new URL("debug.do", "queryPrintConfigInfo");
		url.addPara("gsmc", gsmc);
		AjaxUtil.asyncRefreshBizData(url, "gridPrint:dsprint");
	}
	//增加
	function btnAddClick() {
		var url = new URL("debug.do", "fwdPrintModelAdd");
		openWindow("新增打印格式", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			get("gsmc").setValue(data);
			btnQueryClick();
		});
	}

	//上传打印格式
	function btnUploadClick(){
		var url = new URL("debug.do", "fwdPrintModelUpload");
		openWindow("上传打印格式", url, "small", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			get("gsmc").setValue(data);
			btnQueryClick();
		});
	}
	
	//修改
	function btnModifyClick() {
		var grid = getObject("gridPrint");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		
		var url = new URL("debug.do", "fwdPrintModelModify");
		url.addPara("gsid", grid.getCell(grid.getSelectedRow(), "gsid"));
		openWindow("修改打印格式", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			btnQueryClick();
		});
	}
	//设置
	function btnSetClick() {
		var grid = getObject("gridPrint");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		
		var url = new URL("debug.do", "fwdPrintConfigSet");
		url.addPara("gsid", grid.getCell(grid.getSelectedRow(), "gsid"));
		openWindow("更改地区配置", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			btnQueryClick();
		});
	}
	//删除
	function btnDelClick() {
		var grid = getObject("gridPrint");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		
		var url = new URL("debug.do", "deletePrintModel");
		url.addPara("gsid", grid.getCell(grid.getSelectedRow(), "gsid"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			btnQueryClick();
		}
	}

	//查看
	function btnViewClick(){
		var grid = getObject("gridPrint");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		
		var url = new URL("debug.do", "fwdPrintModelView");
		url.addPara("gsid", grid.getCell(grid.getSelectedRow(), "gsid"));
		openWindow("打印模板查看", url, "big");
	}
</script>