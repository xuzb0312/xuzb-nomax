<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridtable" dataSource="dstableinfo"
		title="框架表数据量统计结果" height="18">
		<ef:columnText name="tablename" label="表名" width="15" />
		<ef:columnText name="hs" label="数据行数" width="15" sum="true"
			dataType="number" mask="############################0" />
		<ef:columnText name="comments" label="描述信息" width="35" />
	</ef:queryGrid>
	<ef:buttons>
	</ef:buttons>
</ef:body>