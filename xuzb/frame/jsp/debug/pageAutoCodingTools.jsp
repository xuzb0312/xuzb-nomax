<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:tab name="tbMain">
		<ef:tabPage title="开始设置">
			<ef:form title="查询条件" name="formQuery">
				<ef:textinput name="tablename" label="表名" required="true"
					prompt="请输入表名进行查询列，例如：fw.sys_para" colspan="4" />
				<ef:buttons colspan="2" closebutton="false">
					<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
				</ef:buttons>
			</ef:form>
			<ef:queryGrid name="gridColm" title="数据列" edit="true"
				editType="smart" exportFile="false">
				<ef:columnText name="name" label="字段名称" width="5" />
				<ef:columnText name="label" label="标签名称" width="10" />
				<ef:columnDropDown name="datatype" label="数据类型" width="5">
					<ef:data key="string" value="String" />
					<ef:data key="date" value="Date" />
					<ef:data key="number" value="Number" />
				</ef:columnDropDown>
				<ef:columnCheckBox name="required" label="必录" width="4" />
				<ef:columnText name="code" label="CODE名称" width="7" />
				<ef:columnCheckBox name="isquery" label="查询条件" width="4" />
				<ef:columnCheckBox name="ispk" label="主键" width="4" />
				<ef:columnCheckBox name="isbizpk" label="业务主键" width="4" />
				<ef:columnText name="length" label="长度" dataType="number"
					mask="#########0" width="4" />
			</ef:queryGrid>
			<ef:form title="设置" name="formSet">
				<ef:textinput name="biztable" label="业务表" required="true" />
				<ef:textinput name="bizkey" label="业务关键字" required="true" />
				<ef:textinput name="bizkeydesc" label="业务关键字描述" required="true" />
			</ef:form>
			<ef:attention>
				<span style="color: #c41d7f;">注：该代码生成工具，作为开发辅助功能，对于自动生成的代码需要认真核实，补充完整，以保证其符合业务规则与要求。</span>
			</ef:attention>
			<ef:buttons>
				<ef:button value="生成" iconCls="icon-book-next" onclick="genCode();"></ef:button>
			</ef:buttons>
		</ef:tabPage>
		<ef:tabPage title="JSP代码">
			<div id="jspcode" style="padding: 10px;">
				<pre class="brush:html;toolbar:false" id="jspcon">
				</pre>
			</div>
		</ef:tabPage>
		<ef:tabPage title="Controller代码">
			<div id="controllercode" style="padding: 10px;">
				<pre class="brush:java;toolbar:false" id="controllercon">
				</pre>
			</div>
		</ef:tabPage>
		<ef:tabPage title="Biz代码">
			<div id="bizcode" style="padding: 10px;">
				<pre class="brush:java;toolbar:false" id="bizcon">
				</pre>
			</div>
		</ef:tabPage>
	</ef:tab>
</ef:body>
<script type="text/javascript"
	src="frame/plugins/ueditor/ueditor.parse.min.js"></script>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("tablename").focus();
	}

	function btnQueryClick() {
		if (!getObject("formQuery").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "queryColmInfo4AutoCoding");
		url.addForm("formQuery");
		AjaxUtil.asyncRefreshBizData(url, "gridColm:dscolm,formSet:dmset");
	}
	function genCode() {
		if (!getObject("formSet").chkFormData(true)) {
			return;
		}
		var url = new URL("debug.do", "genCode");
		url.addForm("formSet");
		url.addQueryGridAllData("gridColm");
		AjaxUtil.asyncBizRequest(url, function(data) {
			getObject("tbMain").selectByTitle("JSP代码");

			var dataMap = new HashMap(data);
			$("#jspcode").html("<pre class=\"brush:html;toolbar:false\">" + dataMap.get("jspcode") + "</pre>");
			$("#controllercode").html("<pre class=\"brush:java;toolbar:false\">" + dataMap.get("controllercode") + "</pre>");
			$("#bizcode").html("<pre class=\"brush:java;toolbar:false\">" + dataMap.get("bizcode") + "</pre>");

			uParse("#jspcode", {
				rootPath : "frame/plugins/ueditor/"
			});

			uParse("#controllercode", {
				rootPath : "frame/plugins/ueditor/"
			});

			uParse("#bizcode", {
				rootPath : "frame/plugins/ueditor/"
			});
		});
	}
</script>
