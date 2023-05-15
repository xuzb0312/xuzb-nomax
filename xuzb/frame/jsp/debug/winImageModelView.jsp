<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:printer name="printer" height="450" />
	<ef:buttons>
		<ef:button value="打印" onclick="btnPrintClick();"></ef:button>
		<ef:button value="打印(jatools插件-事例)" iconCls="icon-print"
			onclick="btnPrintPluginsClick();"></ef:button>
		<ef:button value="打印(Lodop插件-事例)" iconCls="icon-print"
			onclick="btnPrintLodopPluginsClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//打印
	function btnPrintClick() {
		getObject("printer").print();
	}

	//插件打印
	function btnPrintPluginsClick() {
		getObject("printer").printByJatools( {
			orientation : "1",
			paperName : "a4",
			topMargin : 0,
			leftMargin : 0,
			bottomMargin : 0,
			rightMargin : 0
		}, false, true);
	}

	function btnPrintLodopPluginsClick() {
		getObject("printer").printByLodop(1, true, true);
	}
</script>