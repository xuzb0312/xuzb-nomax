<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="经办机构信息" name="formjbjg">
		<ef:textinput name="jbjgid" label="经办机构ID" required="true"
			onchange="jbjgidChng();" />
		<ef:textinput name="jbjgbh" label="经办机构编号" required="true" />
		<ef:textinput name="jbjgjc" label="经办机构简称" required="true" />

		<ef:dropdownList name="jglx" code="JGLX" required="true" label="机构类型"
			value="A" readonly="true"></ef:dropdownList>
		<ef:textinput name="jbjgmc" label="经办机构名称" required="true" colspan="4" />

		<ef:textinput name="ssxzqhdm" label="所属行政区划代码" />
		<ef:textinput name="jgfzr" label="机构负责人" />
		<ef:textinput name="lxdh" label="联系电话" />

		<ef:textinput name="yzbm" label="邮政编码" mask="yzbm" />
		<ef:textinput name="jgdz" label="机构地址" colspan="4" />

		<ef:dropdownList name="sjjbjgid" label="上级经办机构" dsCode="dsjbjg"></ef:dropdownList>
		<ef:textinput name="bz" label="备注" colspan="4" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("jbjgid").focus();
	}
	//经办机构信息变更的情况
	function jbjgidChng() {
		var jbjgbh = getObject("jbjgbh").getValue();
		if (chkObjNull(jbjgbh)) {
			getObject("jbjgbh").setValue(getObject("jbjgid").getValue());
		}
	}
	//清空
	function btnClearClick() {
		getObject("formjbjg").clear();
		getObject("jglx").setValue("A");
		getObject("jbjgid").focus();
	}
	//保存
	function btnSaveClick() {
		if (!getObject("formjbjg").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "saveSysAgencyAdd");
		url.addForm("formjbjg");
		var data = AjaxUtil.syncBizRequest(url);//同步提交
		if (data) {
			alert("保存成功");
			closeWindow(getObject("jbjgid").getValue());
		}
	}
</script>