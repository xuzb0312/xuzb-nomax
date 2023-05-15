<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="父功能信息" dataSource="fgninfo">
		<ef:textinput name="fgn" label="父功能" readonly="true" />
		<ef:textinput name="fgnmc" label="父功能名称" readonly="true" colspan="4" />
	</ef:form>
	<ef:form title="子功能信息" name="formDoc">
		<ef:textinput name="gnid" label="功能ID"
			value='<%=(String)request.getAttribute("defalutgnid") %>'
			required="true" />
		<ef:textinput name="gnmc" label="功能名称" colspan="4" required="true" />
		<ef:dropdownList name="gnlx" dsCode="dsgnlx" label="功能类型"
			required="true"></ef:dropdownList>
		<ef:textinput name="gntb" label="功能图标" colspan="4" />
		<ef:textinput name="gnsj" label="功能事件" colspan="6" />
		<ef:textinput name="bz" label="备注" colspan="6" />
	</ef:form>
	<ef:form title="配置信息" name="formConfig">
		<ef:blank />
		<ef:checkboxList name="sfpzbdgn" rowcount="1" colspan="5" value="1">
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
		<ef:text colspan="5" color="red" value="注：对于区域合并其他情况，请自行调整数据库配置。" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("gnid").focus();
	}

	//保存
	function btnSaveClick() {
		var fgn = getObject("fgn").getValue();
		if (!getObject("formDoc").chkFormData(true)) {
			return false;
		}

		var url = new URL("debug.do", "saveChildrenNodeAdd");
		url.addPara("fgn", fgn);
		url.addForm("formDoc");
		url.addForm("formConfig");

		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			var map = new HashMap();
			map.put("gnid", get("gnid").getValue());
			map.put("gnmc", get("gnmc").getValue());
			closeWindow(map);
		}
	}

	//清空
	function btnClearClick() {
		getObject("formDoc").clear();
		getObject("formConfig").clear();
		getObject("sfpzbdgn").setValue("1");
		getObject("gnid").focus();
	}
</script>