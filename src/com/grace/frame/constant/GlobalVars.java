package com.grace.frame.constant;

import java.util.HashMap;

import org.java_websocket.server.WebSocketServer;

import com.grace.frame.util.DataSet;

/**
 * 框架级的全局变量：使用方，只允许对全局变量的读，不允许写，如果要改动，使用接口
 * 
 * @author yjc
 */
public abstract class GlobalVars{
	public static final String FRAME_PROJECT_NAME = "EFRAME".toLowerCase();// 框架工程名称

	// 系统允许参数配置
	public static String SYS_DBID = "000";// 系统运行的DBID
	public static String SYS_DBNAME = "";// 系统运行的DBID名称
	public static String APP_ID = GlobalVars.FRAME_PROJECT_NAME;// 系统ID
	public static String APP_NAME = "业务系统框架";// 系统名称
	public static String APP_ICON = "frame/imgs/logo_icon.ico";// 系统收藏夹或者快捷方式的icon图标路径，相当与项目来说
	public static boolean DEBUG_MODE = false;// 是否为调试模式
	public static boolean IS_START_POLLING = false;// 是否启动轮询服务
	public static boolean IS_START_SERVICE = false;// 是否启动对外服务程序
	public static String SEP_LOGIN_VERIFICATION_CLASS = "";// 单独登录验证CLASS
	public static String SYS_BASE_PATH = "";// 基本路径地址，主要用于存在验证转向地址的情况配置
	public static String VIEW_TYPE = "v1";// 视图展示类型-当前有v1,v2
	public static boolean ENABLED_REDIS = false;// 是否启用-是否启用redis缓存，如果启用需要配置redis.properties
	public static boolean ENABLED_WEBSOCKET = false;// 是否启用websocket，仅对tomcat6和7进行适配兼容-前端支持html5浏览器进行适配兼容

	// 系统缓存数据
	public static HashMap<String, String> LOCAL_CONFIG_MAP = new HashMap<String, String>();// 全局的本地化配置信息
	public static DataSet POLLING_CONFIG_DS = new DataSet();// 全局的轮询配置信息
	public static int POLLING_MNG_MAXNUM = 0;// 系统轮询管控最大数据--不对外进行发布，内部调控使用。
	public static HashMap<String, Object[]> SERVICE_REG_INFO_MAP = new HashMap<String, Object[]>();// 服务注册配置信息，配置的是非本地服务key=serviceName,value=url,yhbh,pwd,timeout
	public static HashMap<String, String[]> SERVICE_LIST_MAP = new HashMap<String, String[]>();// 服务提供方的服务注册信息；key=serviceName:serviceMethod,value=bizName,bizMethod
	public static HashMap<String, Boolean> SERVICE_RIGHT_MAP = new HashMap<String, Boolean>();// 服务权限缓存信息,系统启动时，不加载，在系统允许过程中逐步丰富
	public static HashMap<String, HashMap<String, Object[]>> CODE_MAP = new HashMap<String, HashMap<String, Object[]>>();// code代码MAP
	public static HashMap<String, String> TEXT_ICONCLS_MAP = new HashMap<String, String>();// 中文含义与图标对应map
	public static HashMap<String, String[]> AGENCY_MAP = new HashMap<String, String[]>();// 经办机构信息的缓存
	public static HashMap<String, String> AGENCY_BIZ_TYPE_MAP = new HashMap<String, String>();// 业务类型经办机构对应关系map
	public static HashMap<String, Object> NO_CHK_SAME_REFERE = new HashMap<String, Object>();// 无需进行验证同源的，源url。--全部小写
	public static HashMap<String, WebSocketServer> WEBSOCKET_SERVER_MAP = new HashMap<String, WebSocketServer>();// websocke-server服务器

	// 系统常量
	// 框架版本号、和业务系统版本号，数据库和程序的版本号不一致的，允许系统启动但不允许登录和业务操作
	public final static String FRAME_VERSION = "2.2.1";// 框架程序版本号
	public static String APP_VERSION = "0.0";// 业务系统程序版本号
	public static String DB_FRAME_VERSION = "0.0";// 框架数据库版本号
	public static String DB_APP_VERSION = "0.0";// 业务系统数据库版本号

	// 系统前台错误提示信息的唯一标识符，将其作为grace.easyFrame框架的保留关键字，禁止修改
	public final static String ERR_MSG_SIGN_WORDS_4_JS_DIS = "<!--\r\n//errmsgsign_20150603_grace.easyFrame\r\n-->";// 防止影响其他代码两端换行

	public static boolean CONFIGINWAR = true;// 数据库配置文件是否在war包内（hibernate.cfg.xml）--注：原始
	// 版本中先将其默认放置在war包中，但提供放在外部的接口，暂不发布
	public static String CONFIGFILEPATH = "";// hibernate.cfg.xml配置文件不在war包中时，存放的bin目录下的路径

}
