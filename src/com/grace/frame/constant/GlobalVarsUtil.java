package com.grace.frame.constant;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Set;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.server.WebSocketServer;

import com.grace.frame.exception.AppException;
import com.grace.frame.localize.LocalHandler;
import com.grace.frame.polling.PollingHandler;
import com.grace.frame.service.ServiceProvideHandler;
import com.grace.frame.service.ServiceUserRegHandler;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.ReadXmlUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.StringUtil;
import com.grace.frame.websocket.WebSocketCfgUtil;

/**
 * 对于系统级框架的全局变量提供对外接口，进行数据值的变更
 * 
 * @author yjc
 */
public class GlobalVarsUtil{

	/**
	 * 设置configinwar参数
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void setCONFIGINWAR(boolean para) throws AppException {
		GlobalVars.CONFIGINWAR = para;
		if (para) {// 如果para为true,则情况path
			GlobalVars.CONFIGFILEPATH = "";
		} else {
			GlobalVars.CONFIGFILEPATH = "grace" + File.separator
					+ GlobalVars.APP_ID;
		}
	}

	/**
	 * 根据实际情况自动调整dbid逻辑 <br>
	 * <p>
	 * 系统启动时，首先判断dbid_info表的数据行数，如果等于1则使用该dbid启动，<br>
	 * 如果大于1或等于0则根据appPara. xml的配置进行启动;<br>
	 * 返回是否进行了调整操作
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2017-6-5
	 * @since V1.0
	 */
	public static boolean adjustSYS_DBID_NAME() throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select dbid, dbmc from fw.dbid_info where rownum <= 2 ");
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() != 1) {// 如果行数不是1，不进行调整直接返回
			return false;
		} else {
			// 进行调整
			GlobalVars.SYS_DBID = dsTemp.getString(0, "dbid");
			GlobalVars.SYS_DBNAME = dsTemp.getString(0, "dbmc");
			System.out.println("***系统自动调整DBID为：" + GlobalVars.SYS_DBID + "["
					+ GlobalVars.SYS_DBNAME + "]");
			return true;
		}
	}

	/**
	 * 调整系统的名称
	 * 
	 * @author yjc
	 * @date 创建时间 2018-7-27
	 * @since V1.0
	 */
	public static void adjustAppName() throws AppException {
		Sql sql = new Sql();
		// 先使用dbid上的--应对不同地区系统名称不一致的问题
		sql.setSql(" select appname from fw.dbid_info where dbid = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() > 0) {
			String appName = dsTemp.getString(0, "appname");
			if (!StringUtil.chkStrNull(appName)) {
				GlobalVarsUtil.setAPP_NAME(appName);
				return;
			}
		}

		// 再使用version上的
		sql.setSql(" select appname from fw.sys_version where appid = ?");
		sql.setString(1, GlobalVars.APP_ID);
		dsTemp = sql.executeQuery();
		if (dsTemp.size() > 0) {
			String appNameDB = dsTemp.getString(0, "appname");
			if (!StringUtil.chkStrNull(appNameDB)) {
				GlobalVarsUtil.setAPP_NAME(appNameDB);
				return;
			}
		}
	}

	/**
	 * 设置SYS_DBID参数
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void setSYS_DBID(String para) throws AppException {
		if (StringUtil.chkStrNull(para)) {
			throw new AppException("setSYS_DBID的para参数为空", "GlobalVarsUtil");
		}
		GlobalVars.SYS_DBID = para;
		GlobalVars.SYS_DBNAME = "";// 清空DBName-需用从数据库中查询，进行重置
	}

	/**
	 * 设置SYS_DBNAME参数
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void reloadSYS_DBName() throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select dbmc from fw.dbid_info where dbid = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() > 0) {
			GlobalVars.SYS_DBNAME = dsTemp.getString(0, "dbmc");
		}
	}

	/**
	 * 设置APP_ID参数
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void setAPP_ID(String para) throws AppException {
		if (StringUtil.chkStrNull(para)) {
			throw new AppException("APP_ID的para参数为空", "GlobalVarsUtil");
		}
		GlobalVars.APP_ID = para;
	}

	/**
	 * 设置APP_NAME参数
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void setAPP_NAME(String para) throws AppException {
		if (StringUtil.chkStrNull(para)) {
			throw new AppException("APP_NAME的para参数为空", "GlobalVarsUtil");
		}
		GlobalVars.APP_NAME = para;
	}

	/**
	 * 设置APP_ICON参数
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void setAPP_ICON(String para) throws AppException {
		if (StringUtil.chkStrNull(para)) {
			throw new AppException("APP_ICON的para参数为空", "GlobalVarsUtil");
		}
		GlobalVars.APP_ICON = para;
	}

	/**
	 * 设置DEBUG_MODE调试模式的参数，true为调试模式，false为运行模式
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void setDEBUG_MODE(boolean para) {
		GlobalVars.DEBUG_MODE = para;
	}

	/**
	 * 不提供对外直接设置本地化的功能，只提供重新从数据库中读取本地化配置的方法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public static void reloadLOCAL_CONFIG_MAP() throws AppException {
		LocalHandler lh = new LocalHandler();
		lh.initSysCacheLocalInfo();// 重新初始化缓存
	}

	/**
	 * 是否启动轮询服务
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void setIS_START_POLLING(boolean para) throws AppException {
		if (para) {
			GlobalVarsUtil.reloadPOLLING_CONFIG_DS();
		} else {
			GlobalVars.POLLING_CONFIG_DS.clear();
		}
		GlobalVars.IS_START_POLLING = para;
	}

	/**
	 * 是否启动对外服务
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void setIS_START_SERVICE(boolean para) throws AppException {
		if (para) {
			GlobalVarsUtil.reloadSERVICE_LIST_MAP();
		} else {
			GlobalVars.SERVICE_LIST_MAP.clear();
		}
		GlobalVars.IS_START_SERVICE = para;
	}

	/**
	 * 不提供对外直接设置轮询配置的功能，只提供重新从数据库中读取轮询配置的方法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public static void reloadPOLLING_CONFIG_DS() throws AppException {
		PollingHandler ph = new PollingHandler();
		ph.initSysCachePollingInfo();
	}

	/**
	 * 重新加载其他grace.easyFrame系统的服务注册信息--将来提供个服务使用方使用的。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public static void reloadSERVICE_REG_INFO_MAP() throws AppException {
		ServiceUserRegHandler surh = new ServiceUserRegHandler();
		surh.initSysCacheServiceUserRegInfo();
	}

	/**
	 * 重新加载系统提供的服务列表；-本地服务的映射关系
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public static void reloadSERVICE_LIST_MAP() throws AppException {
		ServiceProvideHandler sph = new ServiceProvideHandler();
		sph.initSysCacheServiceProvideInfo();
	}

	/**
	 * 清空服务权限map
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public static void clearSERVICE_RIGHT_MAP() throws AppException {
		GlobalVars.SERVICE_RIGHT_MAP.clear();
	}

	/**
	 * code代码表的加载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-25
	 * @since V1.0
	 */
	public static void reloadCODE_MAP() throws AppException {
		GlobalVars.CODE_MAP.clear();// 清空

		// 加载code_MAP
		Sql sql = new Sql();
		sql.setSql(" select dmbh, code, content, nvl(xh, 99999) xh from fw.code_config where dbid = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet ds = sql.executeQuery();

		// 组装map
		for (int i = 0, n = ds.size(); i < n; i++) {
			String dmbh = ds.getString(i, "dmbh").toUpperCase();
			String code = ds.getString(i, "code").toUpperCase();
			String content = ds.getString(i, "content");
			int xh = ds.getInt(i, "xh");

			if (GlobalVars.CODE_MAP.containsKey(dmbh)) {
				GlobalVars.CODE_MAP.get(dmbh)
					.put(code, new Object[] { content, xh });
			} else {
				HashMap<String, Object[]> maptemp = new HashMap<String, Object[]>();
				maptemp.put(code, new Object[] { content, xh });
				GlobalVars.CODE_MAP.put(dmbh, maptemp);
			}
		}
	}

	/**
	 * 加载图标与文本对应关系表
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public static void reloadTEXT_ICONCLS_MAP() throws AppException {
		GlobalVars.TEXT_ICONCLS_MAP.clear();

		DataSet ds = ReadXmlUtil.readXml4KeyValue("icon.xml");
		for (int i = 0, n = ds.size(); i < n; i++) {
			String key = ds.getString(i, "key");
			String value = ds.getString(i, "value");
			GlobalVars.TEXT_ICONCLS_MAP.put(key, value);
		}
	}

	/**
	 * 重新加载经办机构信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public static void reloadAGENCY_MAP() throws AppException {
		GlobalVars.AGENCY_MAP.clear();

		// 重新加载
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.jbjgid, a.jbjgbh, a.jbjgmc, a.jbjgjc ");
		sqlBF.append("   from fw.sys_agency a ");
		sqlBF.append("  where exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type b ");
		sqlBF.append("          where a.jbjgid = b.jbjgid ");
		sqlBF.append("            and b.dbid = ?) ");
		sql.setSql(sqlBF.toString());
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsTmp = sql.executeQuery();

		for (int i = 0, n = dsTmp.size(); i < n; i++) {
			String jbjgid = dsTmp.getString(i, "jbjgid");
			String jbjgbh = dsTmp.getString(i, "jbjgbh");
			String jbjgmc = dsTmp.getString(i, "jbjgmc");
			String jbjgjc = dsTmp.getString(i, "jbjgjc");
			String[] arrJg = { jbjgbh, jbjgmc, jbjgjc };
			GlobalVars.AGENCY_MAP.put(jbjgid, arrJg);
		}
	}

	/**
	 * 重新加载经办机构与业务类型对应表信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-2
	 * @since V1.0
	 */
	public static void reloadAGENCY_BIZ_TYPE_MAP() throws AppException {
		GlobalVars.AGENCY_BIZ_TYPE_MAP.clear();

		// 重新加载
		Sql sql = new Sql();
		sql.setSql(" select ywlb, jbjgid from fw.agency_biz_type where dbid = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsTmp = sql.executeQuery();
		dsTmp = dsTmp.sort("jbjgid");
		dsTmp = dsTmp.sort("ywlb");
		for (int i = 0, n = dsTmp.size(); i < n; i++) {
			String ywlb = dsTmp.getString(i, "ywlb");
			String jbjgid = dsTmp.getString(i, "jbjgid");

			if (GlobalVars.AGENCY_BIZ_TYPE_MAP.containsKey(ywlb)) {
				String yjbjgids = GlobalVars.AGENCY_BIZ_TYPE_MAP.get(ywlb);
				GlobalVars.AGENCY_BIZ_TYPE_MAP.put(ywlb, yjbjgids + ","
						+ jbjgid);
			} else {
				GlobalVars.AGENCY_BIZ_TYPE_MAP.put(ywlb, jbjgid);
			}
		}
	}

	/**
	 * 设置SYS_BASE_PATH参数
	 * 
	 * @param para
	 * @throws AppException
	 */
	public static void reloadSYS_BASE_PATH() throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select csz from fw.sys_para where csbh = 'sys_base_path' and dbid = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() <= 0) {
			GlobalVars.SYS_BASE_PATH = "";
		} else {
			String csz = dsTemp.getString(0, "csz");
			if (StringUtil.chkStrNull(csz)) {
				GlobalVars.SYS_BASE_PATH = "";
			} else {
				GlobalVars.SYS_BASE_PATH = csz;
			}
		}
	}

	/**
	 * 加载无需认证同源的源域名信息，全部小写
	 * 
	 * @author yjc
	 * @date 创建时间 2017-11-22
	 * @since V1.0
	 */
	public static void reloadNO_CHK_SAME_REFERE() throws AppException {
		GlobalVars.NO_CHK_SAME_REFERE.clear();

		Sql sql = new Sql();
		sql.setSql(" select csz from fw.sys_para where csbh = 'sys_no_chk_same_refere' and dbid = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() <= 0) {
			return;
		}
		String csz = dsTemp.getString(0, "csz");
		if (StringUtil.chkStrNull(csz)) {
			return;
		}

		csz = csz.toLowerCase();
		String[] arrCsz = csz.split(",");
		for (String refere : arrCsz) {
			if (StringUtil.chkStrNull(refere)) {
				continue;
			}
			refere = refere.trim();
			GlobalVars.NO_CHK_SAME_REFERE.put(refere, null);
		}
	}
	
	/**
	 * 重新启动websocket Server
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-9-3
	 * @since V1.0
	 */
	public static void restartWebsocektServer() throws AppException {
		GlobalVarsUtil.stopWebsocektServer();// 停止
		GlobalVarsUtil.startWebsocektServer();// 启动
	}

	/**
	 * 启动websocket Server
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-9-3
	 * @since V1.0
	 */
	public static void startWebsocektServer() throws AppException {
		DataSet dsServer = WebSocketCfgUtil.readCfg();
		WebSocketImpl.DEBUG = GlobalVars.DEBUG_MODE;// 是否为调试模式
		for (int i = 0, n = dsServer.size(); i < n; i++) {
			String name = dsServer.getString(i, "name");
			int port = dsServer.getInt(i, "port");
			String server_class = dsServer.getString(i, "server_class");
			try {
				System.out.println("***正在启动：" + name + "(" + port
						+ ")WebSocket Server服务-->" + server_class + "。");
				// 实例化服务器监听程序
				Class<?> sClass = Class.forName(server_class);
				WebSocketServer ws = (WebSocketServer) sClass.newInstance();
				ws.setAddress(new InetSocketAddress(port));
				ws.start();// 启动服务

				// 加入服务器MAP
				GlobalVars.WEBSOCKET_SERVER_MAP.put(name, ws);
			} catch (Exception e) {
				System.err.println("WebSocket Server:" + name + " 启动失败");
				e.printStackTrace();
			}
		}
	}

	/**
	 * 停止websocket Server
	 * 
	 * @author yjc
	 * @date 创建时间 2019-9-3
	 * @since V1.0
	 */
	public static void stopWebsocektServer() {
		Set<String> set = GlobalVars.WEBSOCKET_SERVER_MAP.keySet();
		String[] array = new String[set.size()];
		array = set.toArray(array);
		for (String sname : array) {
			try {
				WebSocketServer server = GlobalVars.WEBSOCKET_SERVER_MAP.get(sname);
				server.stop();
			} catch (Exception e) {
				System.err.println("WebSocket Server:" + sname + " 关闭失败");
			}
		}
		GlobalVars.WEBSOCKET_SERVER_MAP.clear();
	}
}
