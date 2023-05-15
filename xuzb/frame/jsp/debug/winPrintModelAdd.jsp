<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="打印格式模板信息" name="formPrint">
		<ef:textinput name="gslxbh" label="格式类型编号" required="true" />
		<ef:textinput name="gsmc" label="格式名称" required="true" />
		<ef:radiobuttonList label="格式状态" name="gszt" value="1" colspan="2"
			required="true" rowcount="2">
			<ef:data key="1" value="有效" />
			<ef:data key="0" value="无效" />
		</ef:radiobuttonList>
		<ef:textinput name="gsms" label="格式描述" colspan="6" />
		<ef:ueditor name="gsnr" label="格式内容" height="250" required="true"
			colspan="6"
			toolbars="'source','undo','redo','bold','italic','underline','strikethrough','link','unlink','forecolor','backcolor','fontfamily','fontsize','justifyleft','justifycenter','justifyright','paragraph','insertorderedlist','insertunorderedlist','print','preview',,'inserttable','deletetable','mergeright','mergedown','splittorows','splittocols','splittocells','mergecells','insertcol','insertrow','deletecol','deleterow','insertparagraphbeforetable','pagebreak','fullscreen', 'word'" />
		<ef:blank />
		<ef:text colspan="5" color="red"
			value="注：设置关键字时使用@{关键字}的方式，例如：@{xm};循环输出使用@$loop包裹，循环起始使用{@..}标识KEY值-使用:间隔输出多少行换页，循环对象使用  $ {关键字} 标识;例如：@$loop{@grxx:20} ...$ {xm} @$loop。其他情况：启用jatools插件打印的，需要安装插件，在制作模板时通过div标签id=page(i)来指定打印页，必须连续。如：&lt;div id=&quot;page1&quot;&gt;打印内容&lt;/div&gt;&lt;div id=&quot;page2&quot;&gt;打印内容&lt;/div&gt;" />
	</ef:form>
	<ef:buttons>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		get("gslxbh").focus();
	}
	//清空
	function btnClearClick() {
		get("formPrint").clear();
		get("gslxbh").focus();
	}
	//保存
	function btnSaveClick() {
		var form = get("formPrint");
		if (!form.chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "savePrintModelAdd");
		url.addForm("formPrint");
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("新增成功。");
			closeWindow(get("gslxbh").getValue());
		}
	}
</script>