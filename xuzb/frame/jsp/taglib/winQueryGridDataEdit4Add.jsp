<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="com.grace.frame.util.DataSet"%>
<%@ page import="com.grace.frame.util.StringUtil"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%
	DataSet ds = (DataSet) request.getAttribute("dscolumns");
%>
<ef:body>
	<ef:form title="信息新增" rowcount="4" name="formdata">
		<%
			for (int i = 0, n = ds.size(); i < n; i++) {
						String type = ds.getString(i, "type");
						if ("textinput".equals(type)) {
							String name = ds.getString(i, "name");
							String label = ds.getString(i, "label");
							String mask = ds.getString(i, "mask");
							String sourcemask = ds.getString(i, "sourcemask");
							String datatype = ds.getString(i, "datatype");
							boolean readonly = ds.getBoolean(i, "readonly");
							if ("null".equals(mask)) {
								mask = "";
							}
							if ("null".equals(sourcemask)) {
								sourcemask = "";
							}
							if ("null".equals(datatype)) {
								datatype = "";
							}
							if (!StringUtil.chkStrNull(mask)
									&& !StringUtil.chkStrNull(sourcemask)
									&& !StringUtil.chkStrNull(datatype)) {
		%>
		<ef:textinput name="<%=name %>" label="<%=label %>" mask="<%=mask %>"
			sourceMask="<%=sourcemask %>" dataType="<%=datatype %>" colspan="4"
			readonly="<%=readonly %>" />
		<%
			} else if (StringUtil.chkStrNull(mask)
									&& !StringUtil.chkStrNull(sourcemask)
									&& !StringUtil.chkStrNull(datatype)) {
		%>
		<ef:textinput name="<%=name %>" label="<%=label %>"
			sourceMask="<%=sourcemask %>" dataType="<%=datatype %>" colspan="4"
			readonly="<%=readonly %>" />
		<%
			} else if (!StringUtil.chkStrNull(mask)
									&& StringUtil.chkStrNull(sourcemask)
									&& !StringUtil.chkStrNull(datatype)) {
		%>
		<ef:textinput name="<%=name %>" label="<%=label %>" mask="<%=mask %>"
			dataType="<%=datatype %>" colspan="4" readonly="<%=readonly %>" />
		<%
			} else if (!StringUtil.chkStrNull(mask)
									&& !StringUtil.chkStrNull(sourcemask)
									&& StringUtil.chkStrNull(datatype)) {
		%>
		<ef:textinput name="<%=name %>" label="<%=label %>" mask="<%=mask %>"
			sourceMask="<%=sourcemask %>" colspan="4" readonly="<%=readonly %>" />
		<%
			} else if (StringUtil.chkStrNull(mask)
									&& StringUtil.chkStrNull(sourcemask)
									&& !StringUtil.chkStrNull(datatype)) {
		%>
		<ef:textinput name="<%=name %>" label="<%=label %>"
			dataType="<%=datatype %>" colspan="4" readonly="<%=readonly %>" />
		<%
			} else if (!StringUtil.chkStrNull(mask)
									&& StringUtil.chkStrNull(sourcemask)
									&& StringUtil.chkStrNull(datatype)) {
		%>
		<ef:textinput name="<%=name %>" label="<%=label %>" mask="<%=mask %>"
			colspan="4" readonly="<%=readonly %>" />
		<%
			} else if (StringUtil.chkStrNull(mask)
									&& !StringUtil.chkStrNull(sourcemask)
									&& StringUtil.chkStrNull(datatype)) {
		%>
		<ef:textinput name="<%=name %>" label="<%=label %>"
			sourceMask="<%=sourcemask %>" colspan="4" readonly="<%=readonly %>" />
		<%
			} else if (StringUtil.chkStrNull(mask)
									&& StringUtil.chkStrNull(sourcemask)
									&& StringUtil.chkStrNull(datatype)) {
		%>
		<ef:textinput name="<%=name %>" label="<%=label %>" colspan="4"
			readonly="<%=readonly %>" />
		<%
			}
						} else if ("checkboxlist".equals(type)) {
							String name = ds.getString(i, "name");
							String label = ds.getString(i, "label");
							boolean readonly = ds.getBoolean(i, "readonly");
		%>
		<ef:checkboxList name="<%=name %>" label="<%=label %>" colspan="4"
			readonly="<%=readonly %>">
			<ef:data key="1" value="是" />
		</ef:checkboxList>
		<%
			} else if ("dropdownlist".equals(type)) {
							String name = ds.getString(i, "name");
							String label = ds.getString(i, "label");
							String dsCode = ds.getString(i, "dscode");
							boolean readonly = ds.getBoolean(i, "readonly");
		%>
		<ef:dropdownList name="<%=name %>" label="<%=label %>"
			dsCode="<%=dsCode %>" colspan="4" readonly="<%=readonly %>"></ef:dropdownList>
		<%
			} else if ("multidropdownlist".equals(type)) {
							String name = ds.getString(i, "name");
							String label = ds.getString(i, "label");
							String dsCode = ds.getString(i, "dscode");
							boolean readonly = ds.getBoolean(i, "readonly");
		%>
		<ef:multiDropdownList name="<%=name %>" label="<%=label %>"
			dsCode="<%=dsCode %>" colspan="4" readonly="<%=readonly %>"></ef:multiDropdownList>
		<%
			} else if ("hiddeninput".equals(type)) {
							String name = ds.getString(i, "name");
		%>
		<ef:hiddenInput name="<%=name%>" />
		<%
			}
					}
		%>
		<ef:text color="red" value="注：该信息只是将数据添加到界面上的数据窗口中，并不保存到数据库。" />
	</ef:form>
	<ef:buttons>
		<ef:button value="确定" onclick="btnOkClick();"></ef:button>
		<ef:button value="清空" onclick="btnClearClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	//焦点
	function onLoadComplete() {
		getObject("formdata").focus();
	}

	function btnClearClick() {
		getObject("formdata").clear();
	}
	function btnOkClick() {
		var data = getObject("formdata").getMapData();
		closeWindow(data);
	}
</script>