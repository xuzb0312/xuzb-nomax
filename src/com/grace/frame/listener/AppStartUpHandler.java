package com.grace.frame.listener;

import java.io.File;
import java.util.HashMap;

import javax.servlet.ServletContextEvent;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.FileIOUtil;
import com.grace.frame.util.ReadXmlUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.SysLogUtil;
import com.grace.frame.util.ueditor.UeditorUtil;

/**
 * 系统启动时，需要处理的事情
 * 
 * @author yjc
 */
public class AppStartUpHandler implements StartUpHandler{

	/**
	 * 系统启动时，系统框架需要操作的事项
	 */
	public void deal(ServletContextEvent arg) throws Exception {
		Sql sql = new Sql();// 连接数据库

		// 加载系统预配置参数
		System.out.println("***系统正在加载预配置项...");
		DataSet dsParas = ReadXmlUtil.readXml4KeyValue("appParas.xml");
		for (int i = 0, n = dsParas.size(); i < n; i++) {
			String key = dsParas.getString(i, "key");
			String value = dsParas.getString(i, "value");
			// 设置预配置项
			if ("dbid".equalsIgnoreCase(key)) {
				GlobalVarsUtil.setSYS_DBID(value);
			} else if ("appid".equalsIgnoreCase(key)) {
				GlobalVarsUtil.setAPP_ID(value);
			} else if ("appname".equalsIgnoreCase(key)) {
				GlobalVarsUtil.setAPP_NAME(value);// 系统名称
			} else if ("appicon".equalsIgnoreCase(key)) {
				GlobalVarsUtil.setAPP_ICON(value);// 系统图标
			} else if ("debug_mode".equalsIgnoreCase(key)) {
				GlobalVarsUtil.setDEBUG_MODE("true".equalsIgnoreCase(value));
			} else if ("is_start_polling".equalsIgnoreCase(key)) {
				GlobalVars.IS_START_POLLING = "true".equalsIgnoreCase(value);// 设置是否启动轮询
			} else if ("is_start_service".equalsIgnoreCase(key)) {
				GlobalVars.IS_START_SERVICE = "true".equalsIgnoreCase(value);// 设置是否启动服务
			} else if ("app_version".equalsIgnoreCase(key)) {
				GlobalVars.APP_VERSION = value.toLowerCase();// 业务系统程序版本号
			} else if ("sep_login_verification_class".equals(key)) {// 单独登录验证，Class;该类必须继承子超类LoginCheckSupport
				if (null == value) {
					value = "";
				}
				value = value.trim();
				GlobalVars.SEP_LOGIN_VERIFICATION_CLASS = value;
			} else if ("configinwar".equalsIgnoreCase(key)) {// 数据库配置信息是否在数据库中
				GlobalVars.CONFIGINWAR = "true".equalsIgnoreCase(value);
			} else if ("view_type".equalsIgnoreCase(key)) {
				GlobalVars.VIEW_TYPE = value.toLowerCase();
			} else if ("enabled_redis".equalsIgnoreCase(key)) {// 是否启用redis缓存
				GlobalVars.ENABLED_REDIS = "true".equalsIgnoreCase(value);
			} else if ("enabled_websocket".equalsIgnoreCase(key)) {// 是否启用websocket
				GlobalVars.ENABLED_WEBSOCKET = "true".equalsIgnoreCase(value);
			}
		}
		// 由于设置configfilepath必须在app_id设置之后，所以此处要进行重新调配configinwar信息
		GlobalVarsUtil.setCONFIGINWAR(GlobalVars.CONFIGINWAR);

		// 根据DBID从数据库中加载dbName
		System.out.println("***正在初始化数据库连接，并加载数据库名称...");

		// 调整dbid的操作-mod.yjc.2017年6月5日-根据生产环境的dbid情况自动调整为当前的实际dbid
		if (!GlobalVarsUtil.adjustSYS_DBID_NAME()) {
			GlobalVarsUtil.reloadSYS_DBName();// 没有调整，则根据appPara.xml的dbid从数据库加载dbName参数
		}

		// 加载数据库中的appname
		System.out.println("***系统正在设置系统名称[APP_NAME]...");
		GlobalVarsUtil.adjustAppName();

		// 加载数据框架版本号和业务系统版本号
		System.out.println("***系统正在校验版本号...");
		sql.setSql(" select lower(appid) appid, lower(version) version from fw.sys_version ");
		DataSet dsVersion = sql.executeQuery();
		HashMap<String, String> mapVersion = new HashMap<String, String>();
		for (int i = 0, n = dsVersion.size(); i < n; i++) {
			String appid = dsVersion.getString(i, "appid");
			String version = dsVersion.getString(i, "version");
			mapVersion.put(appid, version);
		}

		// 数据库版本号
		GlobalVars.DB_FRAME_VERSION = mapVersion.get(GlobalVars.FRAME_PROJECT_NAME);
		GlobalVars.DB_APP_VERSION = mapVersion.get(GlobalVars.APP_ID.toLowerCase());
		if (!GlobalVars.FRAME_VERSION.equalsIgnoreCase(GlobalVars.DB_FRAME_VERSION)) {
			System.out.println("**********************************************************************");
			System.out.println("                               严重警告                                                                                ");
			System.out.println("框架：【数据库版本号】和【系统程序版本号】不一致，系统将无法对外提供服务。 ");
			System.out.println("**********************************************************************");
		}
		if (GlobalVars.FRAME_PROJECT_NAME.equalsIgnoreCase(GlobalVars.APP_ID)) {
			// 如果是框架，对于业务版本号，与框架完全一致
			GlobalVars.DB_APP_VERSION = GlobalVars.DB_FRAME_VERSION;
			GlobalVars.APP_VERSION = GlobalVars.FRAME_VERSION;
		}

		// 业务版本号
		if (!GlobalVars.APP_VERSION.equalsIgnoreCase(GlobalVars.DB_APP_VERSION)) {
			System.out.println("**********************************************************************");
			System.out.println("                               严重警告                                                                                ");
			System.out.println("业务系统：【数据库版本号】和【系统程序版本号】不一致，系统将无法对外提供服务。 ");
			System.out.println("**********************************************************************");
		}
		SysLogUtil.logInfo("框架程序版本号:" + GlobalVars.FRAME_VERSION + ",框架数据库版本号:"
				+ GlobalVars.DB_FRAME_VERSION + ",业务系统程序版本号:"
				+ GlobalVars.APP_VERSION + ",业务系统数据库版本号:"
				+ GlobalVars.DB_APP_VERSION);// 对启动版本号的情况进行记录

		// 加载系统常用配置参数
		GlobalVarsUtil.reloadSYS_BASE_PATH();// 基本路径地址
		GlobalVarsUtil.reloadNO_CHK_SAME_REFERE();// 加载同源认证例外

		// 系统加载本地化数据--执行前首先设置dbid
		System.out.println("***系统正在加载本地化配置数据...");
		GlobalVarsUtil.reloadLOCAL_CONFIG_MAP();

		// 如果要启动轮询服务，则加载对应的轮询服务配置数据
		if (GlobalVars.IS_START_POLLING) {
			// 系统加载轮询配置数据--执行前首先设置dbid
			System.out.println("***系统正在加载轮询配置数据...");
			GlobalVarsUtil.reloadPOLLING_CONFIG_DS();
		}

		// 如果要对外提供服务，则加载对应服务映射关系
		if (GlobalVars.IS_START_SERVICE) {
			// 系统加载前首先设置dbid--执行前首先设置dbid
			System.out.println("***系统正在加载对外提供服务列表...");
			GlobalVarsUtil.reloadSERVICE_LIST_MAP();
		}
		GlobalVarsUtil.clearSERVICE_RIGHT_MAP();// 清空，缓存服务权限列表

		// 使用其他grace.easyFrame系统提供服务的注册信息列表加载
		// 系统加载前首先设置dbid--执行前首先设置dbid
		System.out.println("***系统正在服务使用列表[其他grace.easyFrame系统的服务注册信息]...");
		GlobalVarsUtil.reloadSERVICE_REG_INFO_MAP();

		// 系统启动加载code代码数据
		System.out.println("***系统正在加载数据库代码数据...");
		GlobalVarsUtil.reloadCODE_MAP();

		// 系统启动加载界面图标与文本对应关系
		System.out.println("***系统正在加载界面图标与文本对应关系...");
		GlobalVarsUtil.reloadTEXT_ICONCLS_MAP();

		// 系统启动加载经办机构信息
		System.out.println("***系统正在加载经办机构信息...");
		GlobalVarsUtil.reloadAGENCY_MAP();

		// 系统启动加载业务类型与经办机构对应关系
		System.out.println("***系统正在加载业务类型与经办机构对应关系...");
		GlobalVarsUtil.reloadAGENCY_BIZ_TYPE_MAP();

		// 初始化富文本编辑器文件缓存数据
		System.out.println("***初始化富文本编辑器文件缓存数据...");
		String tempPath = this.getClass().getResource("/").getPath();
		tempPath = tempPath.substring(1, tempPath.lastIndexOf("/"));
		tempPath = tempPath.substring(0, tempPath.lastIndexOf("/"));
		tempPath = tempPath.substring(0, tempPath.lastIndexOf("/"));
		tempPath = tempPath.replace("/", File.separator);
		tempPath = tempPath + File.separator + "ueditor" + File.separator;
		FileIOUtil.deleteDirectory(tempPath);
		UeditorUtil.CACHEFILE.clear();

		// 启动websocket-server
		if (GlobalVars.ENABLED_WEBSOCKET) {
			System.out.println("***系统正在启动WebSocket服务器...");
			GlobalVarsUtil.restartWebsocektServer();
		}
	}
}
