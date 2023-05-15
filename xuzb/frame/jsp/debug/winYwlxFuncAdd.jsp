<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="业务领域信息" name="formFunc">
		<ef:textinput name="ywlyid" label="业务领域ID" required="true"
			validType="engNum" colspan="6" />
		<ef:textinput name="ywlymc" label="业务领域名称" required="true" colspan="6" />
		<ef:textinput name="gntb" label="功能图标" colspan="6" />
		<ef:textinput name="bz" label="备注" colspan="6" />
	</ef:form>
	<ef:form title="配置信息" name="formConfig">
		<ef:blank />
		<ef:checkboxList name="sfpzbdgn" rowcount="1" colspan="5" value="1">
			<ef:data key="1" value="是否一并将该业务领域配置到本地区" />
		</ef:checkboxList>
		<ef:textinput name="sxh" dataType="number" mask="#########0"
			label="顺序号" colspan="3" />
		<ef:dropdownList name="ywlb" dsCode="dsywlb" label="业务类别" colspan="3"></ef:dropdownList>
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>

<script type="text/javascript">
	function onLoadComplete() {
		getObject("ywlyid").focus();
	}

	//保存方法
	function btnSaveClick() {
		var form = getObject("formFunc");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveYwlxFuncAdd");
		url.addForm("formFunc");
		url.addForm("formConfig");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			var map = getObject("formFunc").getMapData();
			closeWindow(map);
		}
	}
	//清空
	function btnClearClick() {
		getObject("formFunc").clear();
	}
</script>