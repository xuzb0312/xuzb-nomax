package com.grace.frame.service;

import java.util.HashMap;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.Sql;

/**
 * 服务提供方服务注册列表信息
 * 
 * @author yjc
 */
public class ServiceProvideHandler{

	/**
	 * 初始化服务提供方缓存信息
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-5-23
	 * @since V1.0
	 */
	public HashMap<String, String[]> initSysCacheServiceProvideInfo() throws AppException {
		Sql sql = new Sql();
		sql.setSql(" select fwmc, fwff, biz, bizff from fw.service_config where dbid = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet ds = sql.executeQuery();

		// 循环处理参数
		HashMap<String, String[]> map = new HashMap<String, String[]>();
		for (int i = 0, n = ds.size(); i < n; i++) {
			String fwmc = ds.getString(i, "fwmc");
			String fwff = ds.getString(i, "fwff");
			String biz = ds.getString(i, "biz");
			String bizff = ds.getString(i, "bizff");
			String[] arrstr = new String[] { biz, bizff };
			map.put(fwmc + ":" + fwff, arrstr);
		}

		GlobalVars.SERVICE_LIST_MAP = map;
		return map;
	}
}
