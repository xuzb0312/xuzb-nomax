<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="经办机构信息" name="formjbjg" dataSource="dsjbjg">
		<ef:textinput name="jbjgid" label="经办机构ID" required="true"
			readonly="true" />
		<ef:textinput name="jbjgbh" label="经办机构编号" required="true" />
		<ef:textinput name="jbjgjc" label="经办机构简称" required="true" />

		<ef:dropdownList name="jglx" code="JGLX" required="true" label="机构类型"
			readonly="true"></ef:dropdownList>
		<ef:textinput name="jbjgmc" label="经办机构名称" required="true" colspan="4" />

		<ef:textinput name="ssxzqhdm" label="所属行政区划代码" />
		<ef:textinput name="jgfzr" label="机构负责人" />
		<ef:textinput name="lxdh" label="联系电话" />

		<ef:textinput name="yzbm" label="邮政编码" mask="yzbm" />
		<ef:textinput name="jgdz" label="机构地址" colspan="4" />

		<ef:dropdownList name="sjjbjgid" label="上级经办机构" dsCode="dssjjbjg"></ef:dropdownList>
		<ef:textinput name="bz" label="备注" colspan="4" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("jbjgbh").focus();
	}

	//保存
	function btnSaveClick() {
		if (!getObject("formjbjg").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveSysAgencyModify");
		url.addForm("formjbjg");
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (data) {
			alert("保存成功");
			closeWindow(true);
		}
	}
</script>