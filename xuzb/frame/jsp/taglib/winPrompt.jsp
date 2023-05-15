<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form border="false" rowcount="4" name="formCjz">
		<ef:text value="${tipmsg}" colspan="4" tiptag="true" align="center" />
		<ef:textarea name="cjz" colspan="4" height="80" label="${label}"
			required="${required}" />
	</ef:form>
	<ef:buttons closebutton="false">
		<ef:button value="确定" onclick="btnOkClick();"></ef:button>
		<ef:button value="取消" onclick="btnCancelClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		get("cjz").focus();
	}

	function btnCancelClick() {
		closeWindow();
	}

	function btnOkClick() {
		if (!get("formCjz").check(true)) {
			return;
		}
		var cjz = get("cjz").getValue();
		closeWindow(cjz);
	}
</script>
