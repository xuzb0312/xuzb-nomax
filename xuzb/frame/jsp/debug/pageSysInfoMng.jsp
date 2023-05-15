<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<ef:body>
	<ef:tab fixed="true">
		<ef:tabPage title="系统配置信息" selected="true" iconCls="icon-book">
			<ef:form title="系统配置信息" dataSource="sysconfinfo">
				<ef:textinput label="DBID" name="dbid" readonly="true" />
				<ef:textinput label="DB名称" name="dbmc" readonly="true" />
				<ef:textinput label="APPID" name="appid" readonly="true" />

				<ef:textinput label="系统名称" name="appname" readonly="true" />
				<ef:textinput label="系统ICON路径" name="appicon" readonly="true"
					colspan="4" />

				<ef:textinput label="运行模式" name="debugmode" readonly="true" />
				<ef:textinput label="轮询启用情况" name="isstartpolling" readonly="true" />
				<ef:textinput label="服务启用情况" name="isstartservice" readonly="true" />

				<ef:textinput label="框架程序版本号" name="frameversion" readonly="true" />
				<ef:textinput label="业务系统程序版本号" name="appversion" readonly="true" />
				<ef:textinput label="框架数据库版本号" name="dbframeversion" readonly="true" />

				<ef:textinput label="业务系统数据库版本号" name="dbappversion" readonly="true" />
			</ef:form>
			<ef:echarts name="memorychart"
				style="width:70%;min-height:320px;margin:0px auto;border-bottom:1px solid #95B8E7;padding:5px;" />
		</ef:tabPage>
		<ef:tabPage title="服务器信息" iconCls="icon-server">
			<ef:form title="服务器信息" dataSource="serverinfo">
				<ef:textinput label="操作系统" name="sundesktop" readonly="true" />
				<ef:textinput label="操作系统的名称" name="osname" readonly="true" />
				<ef:textinput label="操作系统的构架" name="osarch" readonly="true" />

				<ef:textinput label="操作系统的版本" name="osversion" readonly="true" />
				<ef:textinput label="用户的账户名称" name="username" readonly="true" />
				<ef:textinput label="文件分隔符" name="fileseparator" readonly="true" />

				<ef:textinput label="行分隔符" name="lineseparator" readonly="true" />
				<ef:textinput label="用户的主目录" name="userhome" readonly="true"
					colspan="4" />

				<ef:textinput label="路径分隔符" name="pathseparator" readonly="true" />
				<ef:textinput label="用户的当前工作目录" name="userdir" readonly="true"
					colspan="4" />

				<ef:textinput label="Java的运行环境版本" name="javaversion" readonly="true" />
				<ef:textinput label="默认的临时文件路径" name="javaiotmpdir" readonly="true"
					colspan="4" />

				<ef:textinput name="cpus" label="CPU个数" dataType="number"
					mask="#######0" readonly="true" />
				<ef:textinput label="操作系统IP" name="osip" readonly="true" />
				<ef:textinput label="操作系统MAC" name="osmac" readonly="true" />

				<ef:textinput name="servername" label="服务器名称" readonly="true" />
				<ef:textinput name="serverport" label="服务器端口" readonly="true" />
				<ef:textinput name="remoteaddr" label="远程地址" readonly="true" />

				<ef:textinput name="remotehost" label="远程主机" readonly="true" />
				<ef:textinput name="protocol" label="服务协议" readonly="true" />
				<ef:textinput name="contextpath" label="请求主路径" readonly="true" />

				<ef:textinput name="clientip" label="客户端IP" readonly="true" />
				<ef:textinput name="totalmemory" label="JVM内存总量(MB) "
					readonly="true" dataType="number" mask="########0.00" />
				<ef:textinput name="freememory" label="JVM空闲内存量(MB)" readonly="true"
					dataType="number" mask="########0.00" />

				<ef:textinput name="maxmemory" label="JVM试图使用最大内存量 (MB)"
					readonly="true" dataType="number" mask="########0.00" />
				<ef:textinput name="dbtime" label="数据库时间" dataType="date"
					mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss"
					readonly="true" />
				<ef:textinput name="servertime" label="服务器时间" dataType="date"
					mask="yyyy-MM-dd hh:mm:ss" sourceMask="yyyyMMddhhmmss"
					readonly="true" />
			</ef:form>
			<ef:echarts name="memoryhis"
				style="width:70%;min-height:320px;margin:0px auto;border-bottom:1px solid #95B8E7;padding:5px;" />
		</ef:tabPage>
		<ef:tabPage title="参数配置信息" iconCls="icon-table-gear">
			<ef:queryGrid name="gridPara" title="参数配置信息" dataSource="dssyspara">
				<ef:columnText name="csbh" label="参数编号" width="10" />
				<ef:columnText name="csmc" label="参数名称" width="20" />
				<ef:columnText name="csz" label="参数值" width="15" />
				<ef:columnText name="cssm" label="参数说明" width="20" />
			</ef:queryGrid>
		</ef:tabPage>
	</ef:tab>
</ef:body>
<script type="text/javascript">
	function onLoadComplete() {
		//图表信息
		getObject("memorychart").setOption(
				{
					title : {
						text : '服务器内存占用情况',
						subtext : '单位MB',
						x : 'left'
					},
					tooltip : {
						trigger : 'item',
						formatter : "{b} : {c} ({d}%)"
					},
					legend : {
						orient : 'vertical',
						left : 'right',
						data : [ '空闲内存(MB)', '使用内存(MB)' ]
					},
					series : [ {
						name : '内存情况',
						type : 'pie',
						radius : '75%',
						data : [
								{
									value : getObject("freememory").getValue(),
									name : '空闲内存(MB)'
								},
								{
									value : getObject("totalmemory").getValue()
											- getObject("freememory")
													.getValue(),
									name : '使用内存(MB)'
								} ]
					} ]
				});
		autoRefresh = Math.random();
		getMemoryLine(autoRefresh);
	}
	function getMemoryLine(flag) {
		if (flag != autoRefresh) {
			return;
		}
		var url = new URL("debug.do", "getSysMemoryInfo");
		AjaxUtil.asyncBizRequest(url, function(data) {
			if (chkObjNull(data)) {
				return;
			}
			var map = new HashMap(data);
			if (lineOption.xAxis[0].data.length > 900) {
				lineOption.xAxis[0].data.splice(0, 1);
				lineOption.series[0].data.splice(0, 1);
				lineOption.series[1].data.splice(0, 1);
			}
			lineOption.xAxis[0].data.push(map.get("time"));
			lineOption.series[0].data.push(map.get("use"));
			lineOption.series[1].data.push(map.get("free"));
			getObject("memoryhis").setOption(lineOption);

			setTimeout(function() {
				getMemoryLine(flag);
			}, 3000);
		});
	}
	var autoRefresh;
	var lineOption = {
		calculable : true,
		title : {
			text : "服务器内存信息",
			subtext : "内存资源使用情况",
			x : "center",
			y : "top"
		},
		toolbox : {
			feature : {
				restore : {
					show : true,
					title : "还原"
				}
			},
			show : true
		},
		tooltip : {
			trigger : "axis"
		},
		legend : {
			orient : "horizontal",
			data : [ "使用", "空闲" ],
			left : "center",
			top : "bottom"
		},
		xAxis : [ {
			type : "category",
			axisLine : {
				onZero : false
			},
			axisLabel : {
				formatter : "{value}"
			},
			boundaryGap : false,
			data : []
		} ],
		yAxis : [ {
			type : "value",
			axisLabel : {
				formatter : "{value}MB"
			}
		} ],
		series : [ {
			smooth : true,
			name : "使用",
			type : "line",
			data : []
		}, {
			smooth : true,
			name : "空闲",
			type : "line",
			data : []
		} ]
	};
</script>