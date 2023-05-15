package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.AgencyUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 经办机构信息管理
 * 
 * @author yjc
 */
public class SysAgencyMngBiz extends Biz{
	/**
	 * 进入经办机构信息管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap fwdSysAgencyMng(final DataMap para) throws Exception {

		// 默认查询本DBID下的。
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select jbjgid, jbjgbh, jbjgmc, jbjgjc, ssxzqhdm, ");
		sqlBF.append("        jglx, jgfzr, lxdh, jgdz, yzbm, ");
		sqlBF.append("        bz, sjjbjgid ");
		sqlBF.append("   from fw.sys_agency a ");
		sqlBF.append("  where exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type b ");
		sqlBF.append("          where a.jbjgid = b.jbjgid ");
		sqlBF.append("            and b.dbid = ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsJbjg = this.sql.executeQuery();

		// 解析上级经办机构ID
		AgencyUtil.genJbjgxxDataSet(dsJbjg, "sjjbjgid", "sjjbjgbh", "sjjbjgmc");

		// 排序
		dsJbjg.sort("jbjgbh");

		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dsjbjg", dsJbjg);

		return rdm;
	}

	/**
	 * 查询经办机构信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public final DataMap querySysAgencyInfo(final DataMap para) throws Exception {
		String jbjgmc = para.getString("jbjgmc", "");
		if (jbjgmc == null) {
			jbjgmc = "";
		}
		jbjgmc = "%" + jbjgmc + "%";

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select jbjgid, jbjgbh, jbjgmc, jbjgjc, ssxzqhdm, ");
		sqlBF.append("        jglx, jgfzr, lxdh, jgdz, yzbm, ");
		sqlBF.append("        bz, sjjbjgid ");
		sqlBF.append("   from fw.sys_agency a ");
		sqlBF.append("  where (jbjgid like ? or jbjgbh like ? or jbjgmc like ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, jbjgmc);
		this.sql.setString(2, jbjgmc);
		this.sql.setString(3, jbjgmc);
		DataSet dsJbjg = this.sql.executeQuery();

		// 解析上级经办机构ID
		AgencyUtil.genJbjgxxDataSet(dsJbjg, "sjjbjgid", "sjjbjgbh", "sjjbjgmc");

		// 排序
		dsJbjg.sort("jbjgbh");
		
		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dsjbjg", dsJbjg);

		return rdm;
	}

	/**
	 * 经办机构信息的新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap saveSysAgencyAdd(final DataMap para) throws Exception {
		String jbjgid = para.getString("jbjgid");// 经办机构ID
		String jbjgbh = para.getString("jbjgbh");// 经办机构编号
		String jbjgmc = para.getString("jbjgmc");// 经办机构名称
		String jbjgjc = para.getString("jbjgjc");// 经办机构简称
		String ssxzqhdm = para.getString("ssxzqhdm");// 所属行政区划代码
		String jglx = para.getString("jglx");// 机构类型
		String jgfzr = para.getString("jgfzr");// 机构负责人
		String lxdh = para.getString("lxdh");// 联系电话
		String jgdz = para.getString("jgdz");// 机构地址
		String yzbm = para.getString("yzbm");// 邮政编码
		String bz = para.getString("bz");// 备注
		String sjjbjgid = para.getString("sjjbjgid");// 上级经办机构ID

		if (StringUtil.chkStrNull(jbjgid)) {
			throw new BizException("传入的经办机构ID为空");
		}
		if (StringUtil.chkStrNull(jbjgbh)) {
			throw new BizException("传入的经办机构编号为空");
		}
		if (StringUtil.chkStrNull(jbjgmc)) {
			throw new BizException("传入的经办机构名称为空");
		}
		if (StringUtil.chkStrNull(jbjgjc)) {
			throw new BizException("传入的经办机构简称为空");
		}

		// 检测是否已经存在
		this.sql.setSql(" select jbjgid from fw.sys_agency where jbjgid = ? ");
		this.sql.setString(1, jbjgid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该经办机构ID已经存在无法新增");
		}

		// 插入经办机构
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.sys_agency ");
		sqlBF.append("   (jbjgid, jbjgbh, jbjgmc, jbjgjc, ssxzqhdm, ");
		sqlBF.append("    jglx, jgfzr, lxdh, jgdz, yzbm, ");
		sqlBF.append("    bz, sjjbjgid) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, jbjgid);
		this.sql.setString(2, jbjgbh);
		this.sql.setString(3, jbjgmc);
		this.sql.setString(4, jbjgjc);
		this.sql.setString(5, ssxzqhdm);

		this.sql.setString(6, jglx);
		this.sql.setString(7, jgfzr);
		this.sql.setString(8, lxdh);
		this.sql.setString(9, jgdz);
		this.sql.setString(10, yzbm);

		this.sql.setString(11, bz);
		this.sql.setString(12, sjjbjgid);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 信息修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap fwdSysAgencyModify(final DataMap para) throws Exception {
		String jbjgid = para.getString("jbjgid");

		if (StringUtil.chkStrNull(jbjgid)) {
			throw new BizException("传入的经办机构ID为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select jbjgid, jbjgbh, jbjgmc, jbjgjc, ssxzqhdm, ");
		sqlBF.append("        jglx, jgfzr, lxdh, jgdz, yzbm, ");
		sqlBF.append("        bz, sjjbjgid ");
		sqlBF.append("   from fw.sys_agency a ");
		sqlBF.append("  where jbjgid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, jbjgid);
		DataSet dsJbjg = this.sql.executeQuery();
		if (dsJbjg.size() <= 0) {
			throw new BizException("该经办机构信息在系统中不存在。");
		}

		// 上级经办机构
		this.sql.setSql(" select jbjgid code, '[' || jbjgbh || ']' || jbjgmc content from fw.sys_agency where jbjgid <> ? ");
		this.sql.setString(1, jbjgid);
		DataSet dsSjJbjg = this.sql.executeQuery();
		dsSjJbjg.sort("content");

		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dsjbjg", dsJbjg.getRow(0));
		rdm.put("dssjjbjg", dsSjJbjg);
		return rdm;
	}

	/**
	 * 经办机构信息的修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap saveSysAgencyModify(final DataMap para) throws Exception {
		String jbjgid = para.getString("jbjgid");// 经办机构ID
		String jbjgbh = para.getString("jbjgbh");// 经办机构编号
		String jbjgmc = para.getString("jbjgmc");// 经办机构名称
		String jbjgjc = para.getString("jbjgjc");// 经办机构简称
		String ssxzqhdm = para.getString("ssxzqhdm");// 所属行政区划代码
		String jglx = para.getString("jglx");// 机构类型
		String jgfzr = para.getString("jgfzr");// 机构负责人
		String lxdh = para.getString("lxdh");// 联系电话
		String jgdz = para.getString("jgdz");// 机构地址
		String yzbm = para.getString("yzbm");// 邮政编码
		String bz = para.getString("bz");// 备注
		String sjjbjgid = para.getString("sjjbjgid");// 上级经办机构ID

		if (StringUtil.chkStrNull(jbjgid)) {
			throw new BizException("传入的经办机构ID为空");
		}
		if (StringUtil.chkStrNull(jbjgbh)) {
			throw new BizException("传入的经办机构编号为空");
		}
		if (StringUtil.chkStrNull(jbjgmc)) {
			throw new BizException("传入的经办机构名称为空");
		}
		if (StringUtil.chkStrNull(jbjgjc)) {
			throw new BizException("传入的经办机构简称为空");
		}

		// 检测是否已经存在
		this.sql.setSql(" delete from fw.sys_agency where jbjgid = ? ");
		this.sql.setString(1, jbjgid);
		this.sql.executeUpdate();

		// 插入经办机构
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.sys_agency ");
		sqlBF.append("   (jbjgid, jbjgbh, jbjgmc, jbjgjc, ssxzqhdm, ");
		sqlBF.append("    jglx, jgfzr, lxdh, jgdz, yzbm, ");
		sqlBF.append("    bz, sjjbjgid) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, jbjgid);
		this.sql.setString(2, jbjgbh);
		this.sql.setString(3, jbjgmc);
		this.sql.setString(4, jbjgjc);
		this.sql.setString(5, ssxzqhdm);

		this.sql.setString(6, jglx);
		this.sql.setString(7, jgfzr);
		this.sql.setString(8, lxdh);
		this.sql.setString(9, jgdz);
		this.sql.setString(10, yzbm);

		this.sql.setString(11, bz);
		this.sql.setString(12, sjjbjgid);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 经办机构信息删除，不对是否可以删除进行校验，直接删除，需要调整的在数据库进行调整。此处不予关心
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap deleteSysAgency(final DataMap para) throws Exception {
		String jbjgid = para.getString("jbjgid");

		if (StringUtil.chkStrNull(jbjgid)) {
			throw new BizException("传入的经办机构ID为空");
		}

		this.sql.setSql(" delete from fw.sys_agency where jbjgid = ? ");
		this.sql.setString(1, jbjgid);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 进入经办机构新增页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap fwdSysAgencyAdd(final DataMap para) throws Exception {
		this.sql.setSql(" select jbjgid code, '[' || jbjgbh || ']' || jbjgmc content from fw.sys_agency ");
		DataSet dsJbjg = this.sql.executeQuery();
		dsJbjg.sort("content");

		DataMap rdm = new DataMap();
		rdm.put("dsjbjg", dsJbjg);
		return rdm;
	}
}
