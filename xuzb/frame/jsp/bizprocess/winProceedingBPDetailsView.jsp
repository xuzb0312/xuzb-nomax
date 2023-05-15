<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="easyFrame.tld" prefix="ef"%>
<%@ page import="com.grace.frame.constant.GlobalVars"%>
<%@page import="com.grace.frame.util.DataSet"%>
<%
	DataSet ds = (DataSet) request.getAttribute("dssxxx");
	String dqsxid = (String) request.getAttribute("sxid");
	String lctwjbs = (String) request.getAttribute("lctwjbs");
%>
<ef:body>
	<ef:layout>
		<ef:centerLayoutPanel border="false">
			<ef:tab>
				<ef:tabPage title="业务经办过程" iconCls="icon-layout-content">
					<div style="padding: 5px 15px;">
						<%
							for (int i = 0, n = ds.size(); i < n; i++) {
													String sxid = ds.getString(i, "sxid");
													String sxmc = ds.getString(i, "sxmc");
													String sxzt = ds.getString(i, "sxzt");
													String fqrxm = ds.getString(i, "fqrxm");
													String fqsj = ds.getString(i, "fqsj");
													if (null == fqsj) {
														fqsj = "";
													}
													String czrxm = ds.getString(i, "czrxm");
													String czsj = ds.getString(i, "czsj");
													if (null == czsj) {
														czsj = "";
													}
													String czsm = ds.getString(i, "czsm");
													if (null == czsm) {
														czsm = "";
													}
													String zfrxm = ds.getString(i, "zfrxm");
													String zfsj = ds.getString(i, "zfsj");
													if (null == zfsj) {
														zfsj = "";
													}
													String zfyy = ds.getString(i, "zfyy");
													if (null == zfyy) {
														zfyy = "";
													}
													String background = "#F8F8FF";
													if (sxid.equals(dqsxid)) {
														background = "#F5F5DC";
													}

													if (i == 0) {
						%>
						<div
							style="border: 1px solid #95B8E7; width: 155px; margin: 5px auto; padding: 5px; border-radius: 12px; box-shadow: #666 0px 0px 10px; background: #FFF8DC;">
							<div
								style="border-bottom: 1px solid #95B8E7; text-align: center; font-weight: bold; padding-bottom: 5px;">
								业务开始
							</div>
							<div
								style="text-align: left; padding-top: 5px; text-align: center;">
								<%=fqsj%>
							</div>
						</div>
						<div
							style="text-align: center; margin: 5px auto; width: 300px; font-size: 16px;">
							&darr;
						</div>
						<%
							}

													if ("1".equals(sxzt)) {
						%>
						<div
							style="border: 1px solid #95B8E7; width: 230px; margin: 5px auto; padding: 5px; border-radius: 8px; box-shadow: #666 0px 0px 10px; background: <%=background%>;">
							<div
								style="border-bottom: 1px solid #95B8E7; text-align: center; font-weight: bold; padding-bottom: 5px;">
								<%=sxmc%>
							</div>
							<div style="text-align: left; padding-top: 5px;">
								操作人：<%=czrxm%>
								<br />
								操作时间：<%=czsj%>
								<br />
								操作说明：<%=czsm%>
							</div>
						</div>
						<%
							} else if ("2".equals(sxzt)) {
						%>
						<div
							style="border: 1px solid red; width: 230px; margin: 5px auto; padding: 5px; border-radius: 8px; box-shadow: #666 0px 0px 10px; background: <%=background%>;">
							<div
								style="border-bottom: 1px solid #95B8E7; text-align: center; font-weight: bold; padding-bottom: 5px;">
								<div style="color: red; text-align: left;">
									作废待办事项:
								</div>
								<%=sxmc%>
							</div>
							<div style="text-align: left; padding-top: 5px;">
								作废人：<%=zfrxm%>
								<br />
								作废时间：<%=zfsj%>
								<br />
								作废原因：<%=zfyy%>
							</div>
						</div>
						<%
							} else {
						%>
						<div
							style="border: 1px solid #0000FF; width: 230px; margin: 5px auto; padding: 5px; border-radius: 8px; box-shadow: #666 0px 0px 10px; background: <%=background%>;">
							<div
								style="border-bottom: 1px solid #95B8E7; text-align: center; font-weight: bold; padding-bottom: 5px;">
								<div style="color: red; text-align: left;">
									待办:
								</div>
								<%=sxmc%>
							</div>
							<div style="text-align: left; padding-top: 5px;">
								发起人：<%=fqrxm%>
								<br />
								发起时间：<%=fqsj%>
							</div>
						</div>
						<%
							}
													if (i < n - 1) {
						%>
						<div
							style="text-align: center; margin: 5px auto; width: 300px; font-size: 16px;">
							&darr;
						</div>
						<%
							}
													if (i == n - 1 && !"0".equals(sxzt)) {
						%>
						<div
							style="text-align: center; margin: 5px auto; width: 300px; font-size: 16px;">
							&darr;
						</div>
						<div
							style="border: 1px solid #95B8E7; width: 155px; margin: 5px auto; padding: 5px; border-radius: 12px; box-shadow: #666 0px 0px 10px; background: #FFF8DC;">
							<div
								style="border-bottom: 1px solid #95B8E7; text-align: center; font-weight: bold; padding-bottom: 5px;">
								业务结束
							</div>
							<div
								style="text-align: left; padding-top: 5px; text-align: center;">
								<%=czsj%><%=zfsj%>
							</div>
						</div>
						<%
							}

												}
						%>
					</div>
				</ef:tabPage>
				<%
					if (null != lctwjbs && !"".equals(lctwjbs)) {
				%>

				<ef:tabPage title="业务流程图" iconCls="icon-arrow-divide">
					<div style="width: 100%; height: auto; text-align: center;">
						<img src="taglib.do?method=downloadSysFile&wjbs=<%=lctwjbs%>" />
					</div>
				</ef:tabPage>
				<%
					}
				%>
				<ef:tabPage title="业务经办历史" iconCls="icon-tag-blue">
					<ef:queryGrid name="gridSx" dataSource="dssxxx">
						<ef:columnText name="sxmc" label="事项名称" width="25" />
						<ef:columnDropDown label="事项状态" name="sxzt" width="4" code="SXZT"></ef:columnDropDown>
						<ef:columnText name="fqrbh" label="发起人编号" width="7" />
						<ef:columnText name="fqrxm" label="发起人姓名" width="5" />
						<ef:columnText name="fqsj" label="发起时间" align="ceneter" width="10" />
						<ef:columnText name="czrbh" label="操作人编号" width="7" />
						<ef:columnText name="czrxm" label="操作人姓名" width="5" />
						<ef:columnText name="czsj" label="操作时间" align="ceneter" width="10" />
						<ef:columnText name="zfrbh" label="作废人编号" width="7" />
						<ef:columnText name="zfrxm" label="作废人姓名" width="5" />
						<ef:columnText name="zfsj" label="作废时间" dataType="date"
							align="ceneter" width="10" />
						<ef:columnText name="zfyy" label="作废原因" width="15" />
					</ef:queryGrid>
				</ef:tabPage>
				<%
					if (GlobalVars.DEBUG_MODE) {
				%>
				<ef:tabPage title="调试信息" iconCls="icon-bug">
					<ef:queryGrid name="gridDebug" dataSource="dsdebugpara">
						<ef:columnText name="xmbh" label="项目编号" width="6" />
						<ef:columnText name="xmz" label="项目值" width="50" />
					</ef:queryGrid>
				</ef:tabPage>
				<%
					}
				%>
			</ef:tab>
		</ef:centerLayoutPanel>
		<ef:bottomLayoutPanel height="50">
			<ef:buttons></ef:buttons>
		</ef:bottomLayoutPanel>
	</ef:layout>
</ef:body>