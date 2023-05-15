<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="查询条件" name="formQuery">
		<ef:textinput name="jbjgmc" label="经办机构名称" prompt="请输入经办机构名称、编号查询..." />
		<ef:buttons closebutton="false" colspan="4">
			<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<ef:queryGrid name="gridJbjg" dataSource="dsjbjg" title="经办机构信息"
		height="15">
		<ef:columnText name="jbjgid" label="经办机构ID" width="5" />
		<ef:columnText name="jbjgbh" label="经办机构编号" width="6" />
		<ef:columnText name="jbjgmc" label="经办机构名称" width="15" />
		<ef:columnText name="jbjgjc" label="经办机构简称" width="8" />
		<ef:columnText name="ssxzqhdm" label="所属行政区划代码" width="8" />
		<ef:columnText name="sjjbjgid" label="上级经办机构ID" width="5"
			hidden="true" />
		<ef:columnText name="sjjbjgbh" label="上级经办机构编号" width="7" />
		<ef:columnText name="sjjbjgmc" label="上级经办机构名称" width="12" />
		<ef:columnDropDown label="机构类型" name="jglx" code="JGLX" hidden="true" />
		<ef:columnText name="jgfzr" label="机构负责人" width="5" />
		<ef:columnText name="lxdh" label="联系电话" width="10" />
		<ef:columnText name="jgdz" label="机构地址" width="12" />
		<ef:columnText name="yzbm" label="邮政编码" width="5" />
		<ef:columnText name="bz" label="备注" width="15" />
	</ef:queryGrid>
	<ef:buttons>
		<ef:button value="新增" onclick="btnAddClick();"></ef:button>
		<ef:button value="修改" onclick="btnModifyClick();"></ef:button>
		<ef:button value="删除" onclick="btnDeleteClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("jbjgmc").focus();
	}

	//清空
	function btnClearClick() {
		getObject("formQuery").clear();
		getObject("gridJbjg").clear();
		getObject("jbjgmc").focus();
	}

	//查询
	function btnQueryClick() {
		var jbjgmc = getObject("jbjgmc").getValue();
		var url = new URL("debug.do", "querySysAgencyInfo");
		url.addPara("jbjgmc", jbjgmc);
		AjaxUtil.asyncRefreshBizData(url, "gridJbjg:dsjbjg");
	}

	//新增
	function btnAddClick() {
		var url = new URL("debug.do", "fwdSysAgencyAdd");
		openWindow("新增本地化", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			getObject("jbjgmc").setValue(data);
			btnQueryClick();
		});
	}

	//修改
	function btnModifyClick() {
		var grid = getObject("gridJbjg");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		var url = new URL("debug.do", "fwdSysAgencyModify");
		url.addPara("jbjgid", grid.getCell(grid.getSelectedRow(), "jbjgid"));
		openWindow("修改经办机构信息", url, "big", function(data) {
			if (chkObjNull(data)) {
				return;
			}
			btnQueryClick();
		});
	}

	//删除
	function btnDeleteClick() {
		var grid = getObject("gridJbjg");
		if (!grid.isSelectRow()) {
			alert("请先选择数据");
			return;
		}
		if (!confirm("是否确认对选中的数据进行删除（删除该经办机构前，请先对其他配置有该经办机构的配置项进行调整。）？")) {
			return;
		}
		if (!confirm("再次确认是否删除，点击确定继续？")) {
			return;
		}
		var url = new URL("debug.do", "deleteSysAgency");
		url.addPara("jbjgid", grid.getCell(grid.getSelectedRow(), "jbjgid"));
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("删除成功");
			btnQueryClick();
		}
	}
</script>