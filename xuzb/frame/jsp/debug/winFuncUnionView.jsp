<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:queryGrid name="gridunion" dataSource="dsunion" title="区域合并信息"
		height="5">
		<ef:columnText name="gnmc" label="功能名称" width="10" />
		<ef:columnText name="jbjgfw" label="经办机构范围" width="23" />
	</ef:queryGrid>
</ef:body>