package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

public class AgencyBizTypeMngBiz extends Biz{

	/**
	 * 获取业务类别信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap getAgencyBizTypeInfo(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.dbid, b.dbmc, a.ywlb, wm_concat(jbjgid) jbjgids ");
		sqlBF.append("   from fw.agency_biz_type a, fw.dbid_info b ");
		sqlBF.append("  where a.dbid = b.dbid ");
		sqlBF.append("  group by a.dbid, b.dbmc, a.ywlb ");

		this.sql.setSql(sqlBF.toString());
		DataSet dsType = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dstypeinfo", dsType);
		return rdm;
	}

	/**
	 * 进入业务类型新增界面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap fwdAgencyBizTypeAdd(final DataMap para) throws Exception {
		this.sql.setSql(" select dbid code, dbmc content from fw.dbid_info ");
		DataSet dsDbid = this.sql.executeQuery();

		this.sql.setSql(" select jbjgid code ,jbjgmc content from fw.sys_agency ");
		DataSet dsJbjg = this.sql.executeQuery();
		dsJbjg = dsJbjg.sort("code");

		DataMap rdm = new DataMap();
		rdm.put("dsdbid", dsDbid);
		rdm.put("dsjbjg", dsJbjg);
		return rdm;
	}

	/**
	 * 保存biztype
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap saveAgencyBizTypeAdd(final DataMap para) throws Exception {
		String dbid = para.getString("dbid");
		String ywlb = para.getString("ywlb");
		String jbjgids = para.getString("jbjgids");
		String bz = para.getString("bz");

		if (StringUtil.chkStrNull(dbid)) {
			throw new BizException("DBID不允许为空。");
		}
		if (StringUtil.chkStrNull(ywlb)) {
			throw new BizException("YWLB不允许为空。");
		}
		if (StringUtil.chkStrNull(jbjgids)) {
			throw new BizException("JBJGIDS不允许为空。");
		}

		// 检测是否允许新增
		this.sql.setSql(" select dbid from fw.agency_biz_type where dbid = ? and ywlb = ? ");
		this.sql.setString(1, dbid);
		this.sql.setString(2, ywlb);
		DataSet dsTmp = this.sql.executeQuery();
		if (dsTmp.size() > 0) {
			throw new BizException("该业务类别已经存在，请进行修改。禁止新增。");
		}

		// 插入数据
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.agency_biz_type ");
		sqlBF.append("   (dbid, jbjgid, ywlb, bz) ");
		sqlBF.append("   select ?, jbjgid, ?, ? from fw.sys_agency where "
				+ StringUtil.replaceC2QCQ("jbjgid", jbjgids));

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, dbid);
		this.sql.setString(2, ywlb);
		this.sql.setString(3, bz);
		this.sql.executeUpdate();

		// 系统启动加载经办机构信息
		GlobalVarsUtil.reloadAGENCY_MAP();

		// 系统启动加载业务类型与经办机构对应关系
		GlobalVarsUtil.reloadAGENCY_BIZ_TYPE_MAP();

		return null;
	}

	/**
	 * 进入业务类型修改界面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap fwdAgencyBizTypeModify(final DataMap para) throws Exception {
		String ywlb = para.getString("ywlb");
		String dbid = para.getString("dbid");
		if (StringUtil.chkStrNull(ywlb)) {
			throw new BizException("业务类别为空");
		}
		if (StringUtil.chkStrNull(dbid)) {
			throw new BizException("DBID为空。");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.dbid, b.dbmc, a.ywlb, wm_concat(jbjgid) jbjgids, max(a.bz) bz ");
		sqlBF.append("   from fw.agency_biz_type a, fw.dbid_info b ");
		sqlBF.append("  where a.dbid = b.dbid ");
		sqlBF.append("    and a.dbid = ? ");
		sqlBF.append("    and a.ywlb = ? ");
		sqlBF.append("  group by a.dbid, b.dbmc, a.ywlb ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, dbid);
		this.sql.setString(2, ywlb);
		DataSet dsType = this.sql.executeQuery();
		if (dsType.size() <= 0) {
			throw new BizException("该业务类别在数据库中不存在。");
		}

		this.sql.setSql(" select dbid code, dbmc content from fw.dbid_info ");
		DataSet dsDbid = this.sql.executeQuery();

		this.sql.setSql(" select jbjgid code ,jbjgmc content from fw.sys_agency ");
		DataSet dsJbjg = this.sql.executeQuery();
		dsJbjg = dsJbjg.sort("code");

		DataMap rdm = new DataMap();
		rdm.put("typeinfo", dsType.getRow(0));
		rdm.put("dsdbid", dsDbid);
		rdm.put("dsjbjg", dsJbjg);
		return rdm;
	}

	/**
	 * 保存biztype修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap saveAgencyBizTypeModify(final DataMap para) throws Exception {
		String dbid = para.getString("dbid");
		String ywlb = para.getString("ywlb");
		String jbjgids = para.getString("jbjgids");
		String bz = para.getString("bz");

		if (StringUtil.chkStrNull(dbid)) {
			throw new BizException("DBID不允许为空。");
		}
		if (StringUtil.chkStrNull(ywlb)) {
			throw new BizException("YWLB不允许为空。");
		}

		// 先删除数据
		this.sql.setSql(" delete from fw.agency_biz_type where dbid = ? and ywlb = ? ");
		this.sql.setString(1, dbid);
		this.sql.setString(2, ywlb);
		this.sql.executeUpdate();

		if (StringUtil.chkStrNull(jbjgids)) {
			// 不进行新增，相当于删除。
			return null;
		}

		// 插入数据
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.agency_biz_type ");
		sqlBF.append("   (dbid, jbjgid, ywlb, bz) ");
		sqlBF.append("   select ?, jbjgid, ?, ? from fw.sys_agency where "
				+ StringUtil.replaceC2QCQ("jbjgid", jbjgids));

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, dbid);
		this.sql.setString(2, ywlb);
		this.sql.setString(3, bz);
		this.sql.executeUpdate();

		// 系统启动加载经办机构信息
		GlobalVarsUtil.reloadAGENCY_MAP();

		// 系统启动加载业务类型与经办机构对应关系
		GlobalVarsUtil.reloadAGENCY_BIZ_TYPE_MAP();

		return null;
	}
}
