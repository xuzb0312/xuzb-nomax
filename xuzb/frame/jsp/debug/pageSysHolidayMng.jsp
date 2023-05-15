<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%
	String bgcolor = "#FB0";
	if ("v2".equalsIgnoreCase(GlobalVars.VIEW_TYPE)) {
		bgcolor = "#3398DE";
	}
	String tipmsg = "注：<span style='color:" + bgcolor
			+ ";font-size:25px;'>■</span> 标识当天为节假日。可以通过点击设置或取消当天为节假日。";
%>
<%--样式 --%>
<style type="text/css">
<!--
.claCon {
	width: 200px;
	height: 200px;
	float: left;
	margin: 5px;
	float: left;
}

.claHead {
	margin: 3px;
	padding: 5px;
	border-bottom: 1px solid #ccc;
	font-size: 12px;
	font-weight: bold;
}

.claTd {
	border: 1px solid #E5E5E5;
	height: 20px;
}

.claA {
	display: block;
	width: 100%;
	height: 100%;
	text-decoration: none;
	padding-top: 1px;
}

.claASel {
	background: <%=bgcolor%>;
	color: #FFF;
}

.claAUnsel {
	background: #FFF;
	color: #000;
}
-->
</style>
<ef:body>
	<ef:hiddenInput name="dqnd"
		value='<%=(String)request.getAttribute("dqnd") %>' />
	<ef:form rowcount="4" border="false">
		<ef:textinput name="nd" label="年度" required="true" dataType="date"
			mask="yyyy" sourceMask="yyyy" />
		<ef:buttons closebutton="false" colspan="2">
			<ef:button value="查询" onclick="btnQueryClick();"></ef:button>
			<ef:button value="清空" onclick="btnClearClick();"></ef:button>
		</ef:buttons>
	</ef:form>
	<div
		style="border: 1px solid #95B8E7; width: auto; margin: 5px 2px; padding: 5px; text-align: center;">
		<%
			for (int i = 1, n = 12; i <= n; i++) {
		%>
		<div id="claCon_<%=i%>" class="claCon">
			<h6 id="claHead_<%=i%>" class="claHead">
			</h6>
			<table width="100%">
				<thead>
					<tr>
						<th width="15%" style="height: 20px;">
							<span style="color: #FF9966;">日</span>
						</th>
						<th width="14%">
							一
						</th>
						<th width="14%">
							二
						</th>
						<th width="14%">
							三
						</th>
						<th width="14%">
							四
						</th>
						<th width="14%">
							五
						</th>
						<th width="15%">
							<span style="color: #FF9966;">六</span>
						</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_1" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_2" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_3" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_4" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_5" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_6" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_7" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_8" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_9" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_10" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_11" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_12" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_13" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_14" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_15" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_16" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_17" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_18" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_19" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_20" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_21" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_22" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_23" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_24" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_25" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_26" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_27" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_28" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_29" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_30" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_31" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_32" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_33" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_34" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_35" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_36" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_37" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_38" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_39" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_40" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="14%" class="claTd">
							<a id="calA_<%=i%>_41" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
						<td width="15%" class="claTd">
							<a id="calA_<%=i%>_42" class="claA claAUnsel"
								href="javascript:void(0);"></a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<%
			}
		%>
		<div style="clear: both;"></div>
	</div>
	<ef:attention>
		<%=tipmsg%>
	</ef:attention>
	<ef:buttons>
		<ef:button value="设置所有的周末为节假日" iconCls="icon-calendar-edit"
			onclick="btnBatchSetWeekendClick();"></ef:button>
		<ef:button value="保存" onclick="btnSaveClick();"></ef:button>
	</ef:buttons>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		getObject("nd").setValue(getObject("dqnd").getValue());
		btnQueryClick();
	}

	function btnClearClick() {
		getObject("nd").clear();
		calendarClear();
		getObject("nd").setReadOnly(false);
		getObject("nd").focus();
	}

	function calendarClear() {
		$(".claHead").text("");
		$(".claHead").attr("_value", "");
		$(".claA").text("");
		$(".claA").attr("_value", "");
		$(".claA").each(function() {
			unselCalendarCell($(this));
		});
	}

	//根据年度生成日历
	function genCalendar(nd) {
		if (chkObjNull(nd)) {
			calendarClear();
		}

		//按月生成
		for ( var i = 0; i < 12; i++) {
			var cDate = new Date(nd, i, 1);
			var MshowIndexI = i + 1;
			$("#claHead_" + MshowIndexI).text(
					DateUtil.getDateString(cDate, "yyyy年MM月"));
			var cDateValueStr = DateUtil.getDateString(cDate, "yyyyMM");
			$("#claHead_" + MshowIndexI).attr("_value", cDateValueStr);

			var mLastDate = new Date(nd, i + 1, 0);
			var dates = mLastDate.getDate();
			var day = cDate.getDay();

			for ( var j = 0; j < dates; j++) {
				var DshowIndexJ = j + day + 1;
				var DshowValueJ = j + 1;
				$("#calA_" + MshowIndexI + "_" + DshowIndexJ).text(DshowValueJ);

				if (DshowValueJ > 9) {
					$("#calA_" + MshowIndexI + "_" + DshowIndexJ).attr(
							"_value", cDateValueStr + DshowValueJ);
				} else {
					$("#calA_" + MshowIndexI + "_" + DshowIndexJ).attr(
							"_value", cDateValueStr + "0" + DshowValueJ);
				}
			}
		}
	}

	//查询
	function btnQueryClick() {
		var nd = getObject("nd").getValue();
		if (chkObjNull(nd)) {
			alert("请先选择年度！");
			return;

		}
		getObject("nd").setReadOnly(true);
		genCalendar();//清空
		genCalendar(nd);

		var url = new URL("debug.do", "querySysHolidayInfo");
		url.addPara("nd", nd);
		AjaxUtil.asyncBizRequest(url, function(data) {
			if (chkObjNull(data)) {
				return;
			}
			var dataMap = new HashMap(data);
			var jjrqs = dataMap.get("jjrqs");
			setSelRq(jjrqs);
		});
	}

	//绑定事件
	$(function() {
		$(".claA").click(function() {
			if ($(this).hasClass("claAUnsel")) {
				selCalendarCell($(this));
			} else {
				unselCalendarCell($(this));
			}
		});
	});

	//批量设置周末为节假日
	function btnBatchSetWeekendClick() {
		for ( var i = 1; i <= 12; i++) {
			selCalendarCell($("#calA_" + i + "_1"));
			selCalendarCell($("#calA_" + i + "_7"));
			selCalendarCell($("#calA_" + i + "_8"));
			selCalendarCell($("#calA_" + i + "_14"));
			selCalendarCell($("#calA_" + i + "_15"));
			selCalendarCell($("#calA_" + i + "_21"));
			selCalendarCell($("#calA_" + i + "_22"));
			selCalendarCell($("#calA_" + i + "_28"));
			selCalendarCell($("#calA_" + i + "_29"));
			selCalendarCell($("#calA_" + i + "_35"));
			selCalendarCell($("#calA_" + i + "_36"));
			selCalendarCell($("#calA_" + i + "_42"));
		}
	}

	//选中日期
	function selCalendarCell(obj) {
		if (chkObjNull(obj.text())) {
			return;
		}

		if (obj.hasClass("claAUnsel")) {
			obj.removeClass("claAUnsel");
			obj.addClass("claASel");
		}
	}

	//取消选中
	function unselCalendarCell(obj) {
		if (chkObjNull(obj.text())) {
			if (obj.hasClass("claASel")) {
				obj.removeClass("claASel");
				obj.addClass("claAUnsel");
			}
			return;
		}
		if (obj.hasClass("claASel")) {
			obj.removeClass("claASel");
			obj.addClass("claAUnsel");
		}
	}

	//获取选中的日期
	function getSelRq() {
		var rq = "";
		$(".claA").each(function() {
			if ($(this).hasClass("claASel")) {
				rq = rq + $(this).attr("_value") + ",";
			}
		});
		if (rq.length > 0) {
			rq = rq.substr(0, rq.length - 1);
		}
		return rq;
	}
	function setSelRq(rq) {
		//清空
		$(".claA").each(function() {
			unselCalendarCell($(this));
		});
		var rqArr = rq.split(",");
		for ( var i = 0, n = rqArr.length; i < n; i++) {
			var oneRq = rqArr[i];
			selCalendarCell($(".claA[_value='" + oneRq + "']"));
		}
	}

	function btnSaveClick() {
		var nd = getObject("nd").getValue();
		if (chkObjNull(nd)) {
			alert("请先选择年度！");
			return;

		}
		var url = new URL("debug.do", "saveSysHolidayInfo");
		url.addPara("nd", nd);
		url.addPara("jjrqs", getSelRq());
		var data = AjaxUtil.syncBizRequest(url)
		if (AjaxUtil.checkIsGoOn(data)) {
			alert("保存成功。");
		}
	}
</script>
