package com.grace.frame.localize;

import java.util.HashMap;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.Sql;

/**
 * 对于本地化的操作处理类
 * 
 * @author yjc
 */
public class LocalHandler{

	/**
	 * 初始化系统本地化缓存信息
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public HashMap<String, String> initSysCacheLocalInfo() throws AppException {
		Sql sql = new Sql();

		// 从数据库中读取本地化配置信息
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.jbjgid, a.bzjm, a.bdhm ");
		sqlBF.append("   from fw.local_config a ");
		sqlBF.append("  where (exists (select 'x' ");
		sqlBF.append("            from fw.agency_biz_type b ");
		sqlBF.append("           where a.jbjgid = b.jbjgid ");
		sqlBF.append("             and b.dbid = ?) or a.jbjgid = ?) ");
		sql.setSql(sqlBF.toString());
		sql.setString(1, GlobalVars.SYS_DBID);
		sql.setString(2, GlobalVars.SYS_DBID);
		DataSet ds = sql.executeQuery();

		HashMap<String, String> localMap = new HashMap<String, String>();

		// 循环处理到map中便于数据匹配
		for (int i = 0, n = ds.size(); i < n; i++) {
			String key = ds.getString(i, "jbjgid").trim() + ":"
					+ ds.getString(i, "bzjm").trim();
			String value = ds.getString(i, "bdhm").trim();
			localMap.put(key, value);
		}
		GlobalVars.LOCAL_CONFIG_MAP = localMap;
		return localMap;
	}

	/**
	 * 获取本地化名称;获取不到返回null
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public String getLocalBdhm(String jbjgid, String bzjm) throws AppException {
		String jbjg_key = jbjgid.trim() + ":" + bzjm.trim();// 经办机构级别
		if (GlobalVars.LOCAL_CONFIG_MAP.containsKey(jbjg_key)) {
			return GlobalVars.LOCAL_CONFIG_MAP.get(jbjg_key);
		} else {
			String dbid_key = GlobalVars.SYS_DBID + ":" + bzjm.trim();// dbid级别
			if (GlobalVars.LOCAL_CONFIG_MAP.containsKey(dbid_key)) {
				return GlobalVars.LOCAL_CONFIG_MAP.get(dbid_key);
			} else {
				return null;
			}
		}
	}
}
