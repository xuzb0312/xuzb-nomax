<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="命令脚本">
		<ef:textinput name="mljb" colspan="6" required="true"
			prompt="请输入命令脚本,格式为[BIZ.METHOD:CS1=12:CS2=22],注：实际业务并不发生（除本身BIZ存在提交的情况外）；执行完成后this.sql.rollback" />
	</ef:form>
	<ef:buttons closebutton="false">
		<ef:button value="执行(RUN)" iconCls="icon-resultset-last"
			onclick="btnRunClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
	<ef:form title="执行结果">
		<ef:textarea name="zxjg" colspan="6" height="300" />
	</ef:form>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("mljb").focus();
	}

	function btnClearClick() {
		getObject("mljb").clear();
		getObject("zxjg").clear();
		getObject("mljb").focus();
	}

	function btnRunClick() {
		var mljb = getObject("mljb").getValue();
		if (chkObjNull(mljb)) {
			alert("命令脚本为空，无法执行！");
			getObject("mljb").focus();
			return;
		}

		showLoading();
		var url = new URL("debug.do", "runBizClassDebug");
		url.addPara("mljb", mljb);
		AjaxUtil
				.asyncBizRequest(
						url,
						function(data) {
							hideLoading();
							var mapData = new HashMap(data);
							var jData = mapData.values;
							var zxjg = "【命令脚本】：" + jData.mljb + "\r\n";
							zxjg = zxjg + "【业务类名】：" + jData.bizname + "\r\n";
							zxjg = zxjg + "【业务方法】：" + jData.methodname + "\r\n";
							zxjg = zxjg + "【业务参数】：\r\n" + jData.para + "\r\n";
							zxjg = zxjg + "【静态参数】：\r\n" + jData.bizpara + "\r\n";
							zxjg = zxjg + "【错误代码】：" + jData.errcode + "\r\n";
							zxjg = zxjg + "【错误摘要】：" + jData.errmsg + "\r\n";
							zxjg = zxjg + "【错误详情】：\r\n" + jData.errmsgdetl;
							zxjg = zxjg + "【执行结果】：\r\n"
									+ JSON.stringify(jData.result) + "\r\n";
							zxjg = zxjg
									+ "---------------------------------------------------------------------\r\n"
									+ "---------------------------------------------------------------------\r\n"
									+ "【！！！业务已回滚！！！】";
							getObject("zxjg").setValue(zxjg);
						});
	}
</script>
