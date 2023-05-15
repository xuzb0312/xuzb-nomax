<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:form title="导出选项">
		<ef:radiobuttonList name="dcms" value="1" rowcount="2" colspan="6">
			<ef:data key="1" value="界面数据导出" />
			<ef:data key="2" value="原始数据导出" />
		</ef:radiobuttonList>
	</ef:form>
	<ef:form title="导出文件类型" rowcount="1">
		<ef:radiobuttonList name="wjlx" value="xls" rowcount="6" colspan="1">
			<ef:data key="xls" value="Excel" />
			<ef:data key="dbf" value="DBF" />
			<ef:data key="txt" value="TXT" />
			<ef:data key="xml" value="XML" />
			<ef:data key="json" value="JSON" />
			<ef:data key="pdf" value="PDF" />
		</ef:radiobuttonList>
	</ef:form>
	<ef:buttons>
		<ef:button value="导出" iconCls="icon-accept" onclick="exportData();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//导出数据
	function exportData() {
		var dcms = getObject("dcms").getValue();
		var wjlx = getObject("wjlx").getValue();
		var data = {
			dcms : dcms,
			wjlx : wjlx
		};
		closeWindow(data);
	}
</script>