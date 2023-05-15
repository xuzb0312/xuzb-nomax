package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.listener.AppListener;
import com.grace.frame.listener.AppStartUpHandler;
import com.grace.frame.listener.StartUpHandler;
import com.grace.frame.util.BizCacheUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.ReadXmlUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.SysLogUtil;
import com.grace.frame.workflow.Biz;

/**
 * 系统缓存管理
 * 
 * @author yjc
 */
public class ResetSysCacheBiz extends Biz{
	/**
	 * 刷新本地服务配置数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdResetSysCache(final DataMap para) throws Exception {
		DataMap dmCache = new DataMap();
		dmCache.put("local_config_map", GlobalVars.LOCAL_CONFIG_MAP.size());
		dmCache.put("polling_config_ds", GlobalVars.POLLING_CONFIG_DS.size());
		dmCache.put("service_reg_info_map", GlobalVars.SERVICE_REG_INFO_MAP.size());
		dmCache.put("service_list_map", GlobalVars.SERVICE_LIST_MAP.size());
		dmCache.put("service_right_map", GlobalVars.SERVICE_RIGHT_MAP.size());

		dmCache.put("code_map", GlobalVars.CODE_MAP.size());
		dmCache.put("text_iconcls_map", GlobalVars.TEXT_ICONCLS_MAP.size());
		dmCache.put("agency_map", GlobalVars.AGENCY_MAP.size());
		dmCache.put("agency_biz_type_map", GlobalVars.AGENCY_BIZ_TYPE_MAP.size());

		DataMap dm = new DataMap();
		dm.put("cache", dmCache);
		return dm;
	}

	/**
	 *重置缓存
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap resetSysCache(final DataMap para) throws Exception {
		// 系统框架层缓存数据
		System.out.println("*【系统开始重置框架底层缓存数据】...");
		try {
			StartUpHandler suh = new AppStartUpHandler();
			suh.deal(null);
		} catch (Exception e) {
			e.printStackTrace();
			SysLogUtil.logError(AppListener.class, "系统开始重置框架底层缓存数据时出现异常:"
					+ e.getMessage(), e);
		}

		// 加载业务级缓存数据
		System.out.println("==========================================");
		System.out.println("*【系统开始重置业务级缓存数据】...");
		try {
			DataSet dsPlugins = ReadXmlUtil.readXml4KeyValue("startupPlugins.xml");
			for (int i = 0, n = dsPlugins.size(); i < n; i++) {
				String bizSuhName = dsPlugins.getString(i, "value");
				Class<?> bizSuhClass = Class.forName(bizSuhName);
				StartUpHandler bizSuh = (StartUpHandler) bizSuhClass.newInstance();
				bizSuh.deal(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SysLogUtil.logError(AppListener.class, "系统重置加载业务级插件列表时出现异常:"
					+ e.getMessage(), e);
		}

		// SQL缓存重置
		System.out.println("==========================================");
		System.out.println("*【系统开始重置SQL缓存池数据】...");
		Sql.clearCache();
		
		// BIZ缓存重置
		System.out.println("==========================================");
		System.out.println("*【系统开始重置Biz缓存池数据】...");
		BizCacheUtil.clear();

		return null;
	}
}
