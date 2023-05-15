<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:attention>
		<span style="color: #c41d7f;">1.系统缓存重置成功后，为保证系统的正常使用，请重新登录一次。 <br />
			2.系统缓存的重置只针对框架底层，在系统启动时加载的数据，进行重置。 <br />
			3.如果配置信息做了较大范围的变更的，请重启服务器进行数据的加载。 </span>
	</ef:attention>
	<ef:buttons>
		<ef:button value="重置系统缓存" onclick="resetCache();"
			iconCls="icon-arrow-rotate-clockwise"></ef:button>
	</ef:buttons>
	<ef:form title="缓存数据量信息" rowcount="4" dataSource="cache">
		<ef:textinput name="local_config_map" label="全局的本地化配置信息"
			readonly="true" dataType="number" mask="###############0" />
		<ef:textinput name="polling_config_ds" label="全局的轮询配置信息"
			readonly="true" dataType="number" mask="###############0" />
		<ef:textinput name="service_reg_info_map" label="服务注册配置信息"
			readonly="true" dataType="number" mask="###############0" />
		<ef:textinput name="service_list_map" label="本地服务信息" readonly="true"
			dataType="number" mask="###############0" />
		<ef:textinput name="service_right_map" label="服务权限缓存信息"
			readonly="true" dataType="number" mask="###############0" />

		<ef:textinput name="code_map" label="CODE代码信息" readonly="true"
			dataType="number" mask="###############0" />
		<ef:textinput name="text_iconcls_map" label=" 中文含义与图标对应缓存信息"
			readonly="true" dataType="number" mask="###############0" />
		<ef:textinput name="agency_map" label="经办机构信息" readonly="true"
			dataType="number" mask="###############0" />
		<ef:textinput name="agency_biz_type_map" label="业务类型经办机构对应关系信息"
			readonly="true" dataType="number" mask="###############0" />
	</ef:form>
</ef:body>
<script type="text/javascript">
	function resetCache() {
		var url = new URL("debug.do", "resetSysCache");
		var data = AjaxUtil.syncBizRequest(url);
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("缓存重新加载成功。");
			AjaxUtil.asyncRefreshPage();
		}
	}
</script>