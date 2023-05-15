package com.grace.frame.util;

import java.util.HashMap;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;

/**
 * 经办机构信息相关的业务操作工具类
 * 
 * @author yjc
 */
public class AgencyUtil{
	/**
	 * 根据dataset中的jbjgid列，查询出jbjgbh、jbjgmc，返回到原dataset中
	 * 
	 * @author m.yuan
	 * @date 创建时间 2015-9-24
	 * @since V1.0
	 */
	public static DataSet genJbjgxxDataSet(DataSet vds, String column_jbjgid,
			String column_jbjgbh, String column_jbjgmc) throws Exception {
		DataSet dsTemp = new DataSet();
		String jbjgid, jbjgbh, jbjgmc;
		HashMap<String, String[]> jbjgxxMap = new HashMap<String, String[]>();
		String[] jbjgStr;
		Sql sql = new Sql();
		int len;
		// 检验参数
		if (StringUtil.chkStrNull(column_jbjgid)) {
			throw new AppException("传入的[ID列]为空");
		}
		if (StringUtil.chkStrNull(column_jbjgbh)) {
			throw new AppException("传入的[编号列]为空");
		}
		if (StringUtil.chkStrNull(column_jbjgmc)) {
			throw new AppException("传入的[名称列]为空");
		}
		if (null == vds) {
			throw new AppException("传入的条件[vds]为空");
		}

		// 若传入的vds中没有数据则直接返回
		len = vds.size();
		if (vds.size() < 1) {
			return vds;
		}

		// 处理数据
		for (int j = 0; j < len; j++) {
			jbjgid = vds.getString(j, column_jbjgid);
			jbjgbh = jbjgid;
			jbjgmc = jbjgid;
			if (jbjgxxMap.containsKey(jbjgid)) {
				jbjgStr = jbjgxxMap.get(jbjgid);
				jbjgbh = jbjgStr[0];
				jbjgmc = jbjgStr[1];

				vds.put(j, column_jbjgbh, jbjgbh);
				vds.put(j, column_jbjgmc, jbjgmc);
			} else {
				sql.setSql("select jbjgid, jbjgbh, jbjgmc from fw.sys_agency where jbjgid = ? ");
				sql.setString(1, jbjgid);
				dsTemp = sql.executeQuery();
				if (dsTemp.size() > 0) {
					jbjgbh = dsTemp.getString(0, "jbjgbh");
					jbjgmc = dsTemp.getString(0, "jbjgmc");
				}
				vds.put(j, column_jbjgbh, jbjgbh);
				vds.put(j, column_jbjgmc, jbjgmc);

				jbjgStr = new String[] { jbjgbh, jbjgmc };
				jbjgxxMap.put(jbjgid, jbjgStr);
			}
		}

		return vds;
	}

	/**
	 * 根据jbjgid获取经办机构信息
	 * 
	 * @author yjc
	 * @date 创建时间 2016-12-2
	 * @since V1.0
	 */
	public static DataMap getAgencyInfo(String jbjgid) throws Exception {
		Sql sql = new Sql();
		sql.setSql("select * from fw.sys_agency where jbjgid = ? ");
		sql.setString(1, jbjgid);
		DataSet dsAgency = sql.executeQuery();
		if (dsAgency.size() <= 0) {
			throw new BizException("根据经办机构ID" + jbjgid
					+ "在fw.sys_agency表中没有查询到相关的经办机构信息。");
		}
		return dsAgency.getRow(0);
	}

	/**
	 * 获取经办机构名称
	 * 
	 * @author yjc
	 * @date 创建时间 2016-12-2
	 * @since V1.0
	 */
	public static String getJbjgmc(String jbjgid) throws Exception {
		return AgencyUtil.getAgencyInfo(jbjgid).getString("jbjgmc");
	}

	/**
	 * 获取经办机构编号
	 * 
	 * @author yjc
	 * @date 创建时间 2016-12-2
	 * @since V1.0
	 */
	public static String getJbjgbh(String jbjgid) throws Exception {
		return AgencyUtil.getAgencyInfo(jbjgid).getString("jbjgbh");
	}

	/**
	 * 获取经办机构简称
	 * 
	 * @author yjc
	 * @date 创建时间 2016-12-2
	 * @since V1.0
	 */
	public static String getJbjgjc(String jbjgid) throws Exception {
		return AgencyUtil.getAgencyInfo(jbjgid).getString("jbjgjc");
	}

	/**
	 * 获取经办机构上级经办机构
	 * 
	 * @author yjc
	 * @date 创建时间 2016-12-2
	 * @since V1.0
	 */
	public static String getSjjbjgid(String jbjgid) throws Exception {
		return AgencyUtil.getAgencyInfo(jbjgid).getString("sjjbjgid");
	}
}
