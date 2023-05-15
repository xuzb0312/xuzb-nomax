package com.grace.frame.polling;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.Sql;

/**
 * 轮询机制处理类
 * 
 * @author yjc
 */
public class PollingHandler{
	/**
	 * 初始化系统轮询缓存信息
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public DataSet initSysCachePollingInfo() throws AppException {
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();

		// 从数据库中读取轮询配置信息
		sqlBF.setLength(0);
		sqlBF.append(" select lxmc, lxbiz, lxff, lxcs, nvl(qssj, 0) qssj, nvl(zzsj, 23) zzsj, sjjg, sysdate zhzxsj ");
		sqlBF.append("   from fw.polling_config ");
		sqlBF.append("  where dbid = ? ");
		sqlBF.append("    and sjjg is not null ");// 对于未配置时间间隔的将永不执行
		sqlBF.append("    and sjjg > 0 ");// 时间间隔大于0的
		sql.setSql(sqlBF.toString());
		sql.setString(1, GlobalVars.SYS_DBID);
		DataSet ds = sql.executeQuery();

		// 起始终止时间的特殊处理：保证时间必须在0~23之间
		for (int i = 0, n = ds.size(); i < n; i++) {
			int qssj = ds.getInt(i, "qssj");
			int zzsj = ds.getInt(i, "zzsj");
			if (qssj > 23) {
				qssj = 23;
			}
			if (qssj < 0) {
				qssj = 0;
			}
			if (zzsj > 23) {
				zzsj = 23;
			}
			if (zzsj < 0) {
				zzsj = 0;
			}

			ds.put(i, "qssj", qssj);
			ds.put(i, "zzsj", zzsj);
		}
		GlobalVars.POLLING_CONFIG_DS = ds;
		
		// 进行数据库轮询多服务器管控信息的校正
		sql.setSql(" update fw.polling_mng set maxnum = maxnum + 1 where appid = ? ");
		sql.setString(1, GlobalVars.APP_ID);
		int rowE = sql.executeUpdate();
		if (rowE <= 0) {
			sql.setSql(" insert into fw.polling_mng(appid, maxnum) values(?, ?) ");
			sql.setString(1, GlobalVars.APP_ID);
			sql.setInt(2, 0);
			sql.executeUpdate();
		}

		sql.setSql(" select maxnum from fw.polling_mng where appid = ? ");
		sql.setString(1, GlobalVars.APP_ID);
		DataSet dsTemp = sql.executeQuery();
		GlobalVars.POLLING_MNG_MAXNUM = dsTemp.getInt(0, "maxnum");

		return ds;
	}

}
