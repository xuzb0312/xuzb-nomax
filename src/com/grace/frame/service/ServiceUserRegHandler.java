package com.grace.frame.service;

import java.util.HashMap;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.Sql;

/**
 * 服务使用方的注册信息出来类
 * 
 * @author yjc
 */
public class ServiceUserRegHandler{
	/**
	 * 初始化服务提供方缓存信息
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-5-23
	 * @since V1.0
	 */
	public HashMap<String, Object[]> initSysCacheServiceUserRegInfo() throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select fwmc, url, yhbh, pwd, timeout from fw.service_reg where dbid = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet ds = sql.executeQuery();

		// 循环处理参数
		HashMap<String, Object[]> map = new HashMap<String, Object[]>();
		for (int i = 0, n = ds.size(); i < n; i++) {
			String fwmc = ds.getString(i, "fwmc");
			String url = ds.getString(i, "url");
			String yhbh = ds.getString(i, "yhbh");
			String yhmy = ds.getString(i, "pwd");// 该处pwd是用户密钥-mod.yjc.2017年4月30日
			int timeout = ds.getInt(i, "timeout");
			Object[] arrobj = new Object[] { url, yhbh, yhmy, timeout };
			map.put(fwmc, arrobj);
		}

		GlobalVars.SERVICE_REG_INFO_MAP = map;
		return map;
	}
}
