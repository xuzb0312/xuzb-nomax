package com.grace.frame.debug.biz;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.DateUtil;
import com.grace.frame.workflow.Biz;

/**
 * 系统信息查看
 * 
 * @author yjc
 */
public class SysInfoMngBiz extends Biz{

	/**
	 * 系统信息查看界面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap fwdSysInfoMng(final DataMap para) throws Exception {
		// 获取数据库参数配置信息
		StringBuffer sqlBF = new StringBuffer();

		sqlBF.setLength(0);
		sqlBF.append(" select a.csbh, b.csmc, a.csz, b.cssm ");
		sqlBF.append("   from fw.sys_para a, ");
		sqlBF.append("        fw.sys_para_doc b ");
		sqlBF.append("  where a.dbid = ? ");
		sqlBF.append("    and a.csbh = b.csbh ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsSysPara = this.sql.executeQuery();

		// 返回信息
		DataMap dm = new DataMap();
		dm.put("serverinfo", this.getServerInfo(para));
		dm.put("sysconfinfo", this.getSysConfInfo(para));
		dm.put("dssyspara", dsSysPara);
		return dm;
	}

	/**
	 * @author yjc
	 * @date 创建时间 2018-3-23
	 * @since V1.0
	 */
	public final DataMap getSysMemoryInfo(final DataMap para) throws Exception {
		long free = this.getFreememory();
		long total = this.getTotalmemory();

		DataMap rdm = new DataMap();
		rdm.put("use", total - free);
		rdm.put("free", free);
		rdm.put("time", DateUtil.dateToString(new Date(), "hh:mm:ss"));
		return rdm;
	}

	/**
	 * 获取空闲内存MB
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-23
	 * @since V1.0
	 */
	private long getFreememory() {
		return (Runtime.getRuntime().freeMemory() / 1048576);
	}

	/**
	 * 获取总共内存MB
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-23
	 * @since V1.0
	 */
	private long getTotalmemory() {
		return (Runtime.getRuntime().totalMemory() / 1048576);
	}

	/**
	 * 获取系统配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	private final DataMap getSysConfInfo(final DataMap para) throws Exception {
		DataMap sysConfDm = new DataMap();
		DataSet dsTemp;

		// DBID
		sysConfDm.put("dbid", GlobalVars.SYS_DBID);// dbid
		this.sql.setSql(" select dbmc from fw.dbid_info where dbid = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			sysConfDm.put("dbmc", dsTemp.getString(0, "dbmc"));
		}

		// APP
		sysConfDm.put("appid", GlobalVars.APP_ID);
		sysConfDm.put("appname", GlobalVars.APP_NAME);
		sysConfDm.put("appicon", GlobalVars.APP_ICON);

		// 配置
		sysConfDm.put("debugmode", GlobalVars.DEBUG_MODE ? "调试模式" : "运行模式");
		sysConfDm.put("isstartpolling", GlobalVars.IS_START_POLLING ? "启用" : "未启用");
		sysConfDm.put("isstartservice", GlobalVars.IS_START_SERVICE ? "启用" : "未启用");

		// 版本信息
		sysConfDm.put("frameversion", GlobalVars.FRAME_VERSION);// 框架程序版本号
		sysConfDm.put("appversion", GlobalVars.APP_VERSION);// 业务系统程序版本号
		sysConfDm.put("dbframeversion", GlobalVars.DB_FRAME_VERSION);// 框架数据库版本号
		sysConfDm.put("dbappversion", GlobalVars.DB_APP_VERSION);// 业务系统数据库版本号

		return sysConfDm;
	}

	/**
	 * 获取服务器信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	private final DataMap getServerInfo(final DataMap para) throws Exception {
		HttpServletRequest request = (HttpServletRequest) para.get("request");

		// 获取服务器信息
		Properties props = System.getProperties();
		DataMap serverDm = new DataMap();
		serverDm.put("javaversion", props.getProperty("java.version"));
		serverDm.put("javaiotmpdir", props.getProperty("java.io.tmpdir"));
		serverDm.put("osname", props.getProperty("os.name"));
		serverDm.put("osarch", props.getProperty("os.arch"));
		serverDm.put("osversion", props.getProperty("os.version"));

		serverDm.put("fileseparator", props.getProperty("file.separator"));
		serverDm.put("pathseparator", props.getProperty("path.separator"));
		String linesep = props.getProperty("line.separator");
		if ("\r\n".equals(linesep)) {
			serverDm.put("lineseparator", "\\r\\n");
		} else if ("\n".equals(linesep)) {
			serverDm.put("lineseparator", "\\n");
		} else {
			serverDm.put("lineseparator", linesep);
		}
		serverDm.put("username", props.getProperty("user.name"));
		serverDm.put("userhome", props.getProperty("user.home"));

		serverDm.put("userdir", props.getProperty("user.dir"));
		serverDm.put("sundesktop", props.getProperty("sun.desktop"));
		serverDm.put("cpus", Runtime.getRuntime().availableProcessors());

		// OS的IP和mac
		DataMap dmIpMac = this.getIpMac();
		serverDm.put("osip", dmIpMac.getString("osip"));
		serverDm.put("osmac", dmIpMac.getString("osmac"));

		// 部署服务器情况
		serverDm.put("servername", request.getServerName());
		serverDm.put("serverport", request.getServerPort());
		serverDm.put("remoteaddr", request.getRemoteAddr());
		serverDm.put("remotehost", request.getRemoteHost());
		serverDm.put("protocol", request.getProtocol());
		serverDm.put("contextpath", request.getContextPath());
		serverDm.put("clientip", this.getIp());

		// 虚拟机总量
		serverDm.put("totalmemory", (Runtime.getRuntime().totalMemory() / 1048576));
		serverDm.put("freememory", (Runtime.getRuntime().freeMemory() / 1048576));
		serverDm.put("maxmemory", (Runtime.getRuntime().totalMemory() / 1048576));
		serverDm.put("dbtime", DateUtil.dateToString(DateUtil.getDBTime(), "yyyyMMddhhmmss"));
		serverDm.put("servertime", DateUtil.dateToString(new Date(), "yyyyMMddhhmmss"));

		return serverDm;
	}

	/**
	 * 获取IP和map
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	private DataMap getIpMac() throws Exception {
		Properties props = System.getProperties();
		String os = props.getProperty("os.name");
		InetAddress address;
		if (os != null && os.toLowerCase().indexOf("linux") > -1) {
			address = this.getIpAddressOnLinux();
			if (null == address) {
				address = InetAddress.getLocalHost();
			}
		} else {
			address = InetAddress.getLocalHost();
		}
		NetworkInterface ni = NetworkInterface.getByInetAddress(address);
		ni.getInetAddresses().nextElement().getAddress();
		byte[] mac = ni.getHardwareAddress();
		String sIP = address.getHostAddress();
		String sMAC = "";
		if (null != mac) {
			Formatter formatter = new Formatter();
			for (int i = 0; i < mac.length; i++) {
				sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i], (i < mac.length - 1) ? "-" : "")
					.toString();
			}
		}
		DataMap dm = new DataMap();
		dm.put("osip", sIP);
		dm.put("osmac", sMAC);
		return dm;
	}

	/**
	 * 根据网卡获取本机配置的ip地址.在linux平台下 Author: Create Date: 2012.6.26
	 * 
	 * @return linux ip
	 * @throws SocketException
	 */
	private InetAddress getIpAddressOnLinux() throws SocketException {
		Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
			if (netInterface.isLoopback() || netInterface.isVirtual()
					|| netInterface.isPointToPoint() || !netInterface.isUp()) {
				continue;
			} else {
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address) {
						return ip;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 框架表数据量统计
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdFrameTablesInfo(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select 'fw.' || lower(table_name) tablename, comments ");
		sqlBF.append("   from sys.all_tab_comments ");
		sqlBF.append("  where owner = 'FW' ");

		this.sql.setSql(sqlBF.toString());
		DataSet dsTable = this.sql.executeQuery();
		for (int i = 0, n = dsTable.size(); i < n; i++) {
			this.sql.setSql(" select count(*) hs from "
					+ dsTable.getString(i, "tablename"));
			DataSet dsTmp = this.sql.executeQuery();
			dsTable.put(i, "hs", dsTmp.getInt(0, "hs"));
		}
		dsTable.sortdesc("hs");

		DataMap rdm = new DataMap();
		rdm.put("dstableinfo", dsTable);
		return rdm;
	}

}
