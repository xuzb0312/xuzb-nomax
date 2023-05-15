<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="子功能信息" name="formDoc" dataSource="docinfo">
		<ef:textinput name="gnid" label="功能ID" required="true" readonly="true" />
		<ef:textinput name="gnmc" label="功能名称" colspan="4" required="true" />
		<ef:dropdownList name="gnlx" dsCode="dsgnlx" label="功能类型"
			required="true"></ef:dropdownList>
		<ef:textinput name="gntb" label="功能图标" colspan="4" />
		<ef:textinput name="gnsj" label="功能事件" colspan="6" />
		<ef:textinput name="bz" label="备注" colspan="6" />
	</ef:form>
	<ef:form title="配置信息" name="formConfig" dataSource="configinfo">
		<ef:blank />
		<ef:checkboxList name="sfpzbdgn" rowcount="1" colspan="5">
			<ef:data key="1" value="是否一并将该功能配置到本地区" />
		</ef:checkboxList>
		<ef:textinput name="sxh" dataType="number" mask="#########0"
			label="顺序号" />
		<ef:dropdownList name="ywlb" dsCode="dsywlb" label="业务类别"></ef:dropdownList>
		<ef:dropdownList name="qyhb" label="区域合并">
			<ef:data key="1" value="合并" />
			<ef:data key="0" value="不合并" />
		</ef:dropdownList>
		<ef:blank />
		<ef:text colspan="5" color="red" value="注：1.该功能会清空原有的节点配置信息，然后重新配置。" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("gnmc").focus();
	}

	//保存
	function btnSaveClick() {
		if (!getObject("formDoc").chkFormData(true)) {
			return false;
		}

		var url = new URL("debug.do", "saveChildrenNodeModify");
		url.addForm("formDoc");
		url.addForm("formConfig");

		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("修改成功。");
			closeWindow(true);
		}
	}
</script>