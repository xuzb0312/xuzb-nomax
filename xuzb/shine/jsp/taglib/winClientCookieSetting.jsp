<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="shine.tld" prefix="sh"%>
<sh:body padding="20" minWidth="770">
	<sh:blockquote color="#FFB800">
	用于根据客户端的电脑配置设置最适合使用和页面展示的设置信息。
	</sh:blockquote>
	<sh:form name="formSet" pane="false">
		<sh:textInput name="sys_yhbh" label="默认用户名" placeholder="默认用户登录名" />
		<sh:formLine>
			<sh:formLineGroup label="高度微调值">
				<sh:textInput name="sys_grid_row_diff" label="数据窗口默认高度微调值"
					dataType="number" mask="######0" />
				<sh:formLineGroupMidText value="数据窗口默认高度微调值" color="red" />
			</sh:formLineGroup>
		</sh:formLine>
		<sh:formLine>
			<sh:formLineGroup label="宽度">
				<sh:textInput name="sys_win_type_big_width" label="打开窗口宽度微调（BIG）"
					dataType="number" mask="######0" />
				<sh:formLineGroupMidText value="高度" />
				<sh:textInput name="sys_win_type_big_height" label="打开窗口高度微调（BIG）"
					dataType="number" mask="######0" />
				<sh:formLineGroupMidText value="打开窗口尺寸微调（BIG）" color="red" />
			</sh:formLineGroup>
		</sh:formLine>
		<sh:formLine>
			<sh:formLineGroup label="宽度">
				<sh:textInput name="sys_win_type_normal_width"
					label="打开窗口宽度微调（NORMAL）" dataType="number" mask="######0" />
				<sh:formLineGroupMidText value="高度" />
				<sh:textInput name="sys_win_type_normal_height"
					label="打开窗口高度微调（NORMAL）" dataType="number" mask="######0" />
				<sh:formLineGroupMidText value="打开窗口尺寸微调（NORMAL）" color="red" />
			</sh:formLineGroup>
		</sh:formLine>
		<sh:formLine>
			<sh:formLineGroup label="宽度">
				<sh:textInput name="sys_win_type_small_width"
					label="打开窗口宽度微调（SMALL）" dataType="number" mask="######0" />
				<sh:formLineGroupMidText value="高度" />
				<sh:textInput name="sys_win_type_small_height"
					label="打开窗口高度微调（SMALL）" dataType="number" mask="######0" />
				<sh:formLineGroupMidText value="打开窗口尺寸微调（SMALL）" color="red" />
			</sh:formLineGroup>
		</sh:formLine>
		<sh:formLine itemWidth="430">
			<sh:formLineGroup label="打开方式">
				<sh:dropdownList name="sys_top_win_open_type" label="打开方式"
					tips="浏览器顶层窗口打开方式">
					<sh:data key="1" value="不进行顶层打开（当前Window）" />
					<sh:data key="2" value="顶层打开（TOP.Window）" />
				</sh:dropdownList>
			</sh:formLineGroup>
		</sh:formLine>
		<sh:buttons>
			<sh:button onclick="btnRedoClick();" value="还原默认设置" icon="&#xe640;"
				type="warm"></sh:button>
			<sh:button onclick="btnSaveClick();" value="保存上述设置" icon="&#x1005;"></sh:button>
		</sh:buttons>
	</sh:form>
</sh:body>
<script type="text/javascript">
	function onLoadComplete() {
		var sys_yhbh = CookieUtil.get("sys_yhbh");
		var sys_grid_row_diff = CookieUtil.get("sys_grid_row_diff");

		var sys_win_type_big_width = CookieUtil.get("sys_win_type_big_width");
		var sys_win_type_big_height = CookieUtil.get("sys_win_type_big_height");

		var sys_win_type_normal_width = CookieUtil
				.get("sys_win_type_normal_width");
		var sys_win_type_normal_height = CookieUtil
				.get("sys_win_type_normal_height");

		var sys_win_type_small_width = CookieUtil
				.get("sys_win_type_small_width");
		var sys_win_type_small_height = CookieUtil
				.get("sys_win_type_small_height");
		var sys_top_win_open_type = CookieUtil.get("sys_top_win_open_type");

		var map = new HashMap();
		map.put("sys_yhbh", sys_yhbh);
		map.put("sys_grid_row_diff", sys_grid_row_diff);
		map.put("sys_win_type_big_width", sys_win_type_big_width);
		map.put("sys_win_type_big_height", sys_win_type_big_height);
		map.put("sys_win_type_normal_width", sys_win_type_normal_width);
		map.put("sys_win_type_normal_height", sys_win_type_normal_height);

		map.put("sys_win_type_small_width", sys_win_type_small_width);
		map.put("sys_win_type_small_height", sys_win_type_small_height);

		map.put("sys_top_win_open_type", sys_top_win_open_type);

		getObject("formSet").setMapData(map);

		getObject("sys_yhbh").focus();
	}

	function btnRedoClick() {
		getObject("formSet").clear();
		btnSaveClick();
	}

	function btnSaveClick() {
		CookieUtil.set("sys_yhbh", getObject("sys_yhbh").getValue(), 36500);
		CookieUtil.set("sys_grid_row_diff", getObject("sys_grid_row_diff")
				.getValue(), 36500);
		CookieUtil.set("sys_win_type_big_width", getObject(
				"sys_win_type_big_width").getValue(), 36500);
		CookieUtil.set("sys_win_type_big_height", getObject(
				"sys_win_type_big_height").getValue(), 36500);
		CookieUtil.set("sys_win_type_normal_width", getObject(
				"sys_win_type_normal_width").getValue(), 36500);
		CookieUtil.set("sys_win_type_normal_height", getObject(
				"sys_win_type_normal_height").getValue(), 36500);
		CookieUtil.set("sys_win_type_small_width", getObject(
				"sys_win_type_small_width").getValue(), 36500);
		CookieUtil.set("sys_win_type_small_height", getObject(
				"sys_win_type_small_height").getValue(), 36500);
		CookieUtil.set("sys_top_win_open_type", getObject(
				"sys_top_win_open_type").getValue(), 36500);
		MsgBoxUtil.alert("设置成功！");
	}
</script>