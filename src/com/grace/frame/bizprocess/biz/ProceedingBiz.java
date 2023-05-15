package com.grace.frame.bizprocess.biz;

import com.grace.frame.bizprocess.ProceedingHandler;
import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.*;
import com.grace.frame.workflow.Biz;
import net.sf.json.JSONObject;

import java.io.ObjectInputStream;
import java.sql.Blob;
import java.util.Date;

/**
 * 待办事项相关的业务逻辑
 * 
 * @author yjc
 */
public class ProceedingBiz extends Biz{
	/**
	 * 获取首页的待办事项信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public final DataMap queryProceeding4MainPageDbsx(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.sxid, a.sxmc, a.fqr, a.fqsj, a.czcssx, ");
		sqlBF.append("        a.czjgsx, a.sxdyjbjgid, b.ywlb, a.gzrbz ");
		sqlBF.append("   from fw.proceeding a, ");
		sqlBF.append("        fw.func b ");
		sqlBF.append("  where nvl(sxzt, '0') = '0' ");
		sqlBF.append("    and nvl(a.czr, ?) = ? ");
		sqlBF.append("    and a.sxdygnid = b.gnid ");
		sqlBF.append("    and b.dbid = ? ");
		if (!"A".equals(this.getSysUser().getYhlx())) {
			sqlBF.append("    and (exists (select 'x' ");
			sqlBF.append("            from fw.user_func d ");
			sqlBF.append("           where b.gnid = d.gnid ");
			sqlBF.append("             and d.yhid = ?) or exists (select 'x' ");
			sqlBF.append("            from fw.user_role e, fw.role_func f ");
			sqlBF.append("           where e.jsid = f.jsid ");
			sqlBF.append("             and f.gnid = b.gnid ");
			sqlBF.append("             and e.yhid = ?)) ");
		}
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, this.getYhid());
		this.sql.setString(2, this.getYhid());
		this.sql.setString(3, GlobalVars.SYS_DBID);
		if (!"A".equals(this.getSysUser().getYhlx())) {
			this.sql.setString(4, this.getYhid());
			this.sql.setString(5, this.getYhid());
		}
		DataSet dsDbsx = this.sql.executeQuery();

		// 待办事项的数据处理
		Date now = DateUtil.getDBTime();
		DataSet dsDbsxView = new DataSet();
		for (int i = 0, n = dsDbsx.size(); i < n; i++) {
			Date fqsj = dsDbsx.getDate(i, "fqsj");
			int czcssx = dsDbsx.getInt(i, "czcssx");
			int czjgsx = dsDbsx.getInt(i, "czjgsx");
			String sxid = dsDbsx.getString(i, "sxid");
			String sxmc = dsDbsx.getString(i, "sxmc");
			String fqr = dsDbsx.getString(i, "fqr");
			String sxdyjbjgid = dsDbsx.getString(i, "sxdyjbjgid");
			String gzrbz = dsDbsx.getString(i, "gzrbz");

			// 判断数据权限
			String ywlb = dsDbsx.getString(i, "ywlb");
			if (!StringUtil.chkStrNull(ywlb)) {
				String ywjbjgqx = GlobalVars.AGENCY_BIZ_TYPE_MAP.get(ywlb);
				if (StringUtil.chkStrNull(ywjbjgqx)) {// 如果为空，则对与该节点不进行展示
					continue;
				}
				if (!"A".equals(this.getSysUser().getYhlx())) {// 超级管理员不进行数据权限的判断
					String yhjbjgqx = this.getSysUser()
						.getAllInfoDM()
						.getString("yhjbjgqxfw");
					ywjbjgqx = StringUtil.mixed2Str(ywjbjgqx, yhjbjgqx);
				}
				if ("*".equals(sxdyjbjgid)) {
					if (StringUtil.chkStrNull(ywjbjgqx)) {
						continue;
					}
				} else {
					if (StringUtil.chkStrNull(StringUtil.mixed2Str(ywjbjgqx, sxdyjbjgid))) {
						continue;
					}
				}
			}

			// 数据处理
			String sxzt = "√正常";
			String sxztsm = "";
			int sxztdm = 10;
			// 获取当前耗费的天数
			int dqhfts = 0;
			if ("1".equals(gzrbz)) {
				dqhfts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now) + 1;
			} else {
				dqhfts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now) + 1;
			}

			// 警告时限处理
			if (czjgsx > 0) {
				if (dqhfts > czjgsx) {
					sxzt = "<span style=\"color:blue;\">☀警告</span>";
					sxztdm = 5;
				}
			}

			// 超时时限处理
			String jzrq_str = "";
			String fqsj_str = DateUtil.dateToString(fqsj, "yyyyMMddhhmmss");
			if (czcssx > 0) {
				if ("1".equals(gzrbz)) {
					if (dqhfts > czcssx) {
						sxzt = "<span style=\"color:red;\">★超时</span>";
						sxztdm = 1;
						sxztsm = "超时" + (dqhfts - czcssx) + "个工作日";
					} else {
						sxztsm = "剩余" + (czcssx - dqhfts) + "个工作日";
					}
					jzrq_str = DateUtil.dateToString(DateUtil.addDayExceptHoliday(fqsj, czcssx - 1), "yyyyMMdd");
				} else {
					if (dqhfts > czcssx) {
						sxzt = "<span style=\"color:red;\">★超时</span>";
						sxztdm = 1;
						sxztsm = "超时" + (dqhfts - czcssx) + "天";
					} else {
						sxztsm = "剩余" + (czcssx - dqhfts) + "天";
					}
					jzrq_str = DateUtil.dateToString(DateUtil.addDay(fqsj, czcssx - 1), "yyyyMMdd");
				}
			}

			// 操作
			String cz = "<a style=\"color:red;text-decoration:none;\" href=\"javascript:void(0);\" onclick=\"doProceedingBiz('"
					+ sxid
					+ "');\">经办</a> <a style=\"color:red;text-decoration:none;\" href=\"javascript:void(0);\" onclick=\"nullifyProceedingBiz('"
					+ sxid + "');\">忽略</a>";
			dsDbsxView.addRow();
			dsDbsxView.put(dsDbsxView.size() - 1, "sxid", sxid);
			dsDbsxView.put(dsDbsxView.size() - 1, "sxzt", sxzt);
			dsDbsxView.put(dsDbsxView.size() - 1, "sxztsm", sxztsm);
			dsDbsxView.put(dsDbsxView.size() - 1, "sxztdm", sxztdm);
			dsDbsxView.put(dsDbsxView.size() - 1, "sxmc", sxmc);
			dsDbsxView.put(dsDbsxView.size() - 1, "fqr", fqr);
			dsDbsxView.put(dsDbsxView.size() - 1, "fqsj", fqsj_str);
			dsDbsxView.put(dsDbsxView.size() - 1, "jzrq", jzrq_str);
			dsDbsxView.put(dsDbsxView.size() - 1, "cz", cz);
		}

		// 发起人解析
		SysUser.genYhxxDataSet(dsDbsxView, "fqr", "fqrbh", "fqr");
		dsDbsxView.sort("fqsj");
		dsDbsxView.sort("sxztdm");

		DataMap rdm = new DataMap();
		rdm.put("dsdbsx", dsDbsxView);
		return rdm;
	}

	/**
	 * 业务忽略
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public final DataMap saveProceedingBizNullify(final DataMap para) throws Exception {
		String sxid = para.getString("sxid");
		if (StringUtil.chkStrNull(sxid)) {
			throw new BizException("传入的事项ID为空。");
		}

		// 作废待办事项的逻辑
		ProceedingHandler.nullify(sxid, this.getYhid(), DateUtil.getDBTime(), "待办事项业务忽略");

		return null;
	}

	/**
	 * 待办事项查询
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public final DataMap queryMyProceedingInfo(final DataMap para) throws Exception {
		DataMap dm = this.queryProceeding4MainPageDbsx(para);
		dm.putAll(this.queryMyProceedingBjsx4MainPage(para));
		return dm;
	}

	/**
	 * 查询办结事项
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public final DataMap queryMyProceedingBjsx4MainPage(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select sxid, sxmc, fqr, fqsj, to_char(czsj, 'yyyymmddhh24miss') czsj, czcssx, gzrbz ");
		sqlBF.append("   from fw.proceeding ");
		sqlBF.append("  where czr = ? ");
		sqlBF.append("    and sxzt = '1' ");
		sqlBF.append("  order by czsj desc ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, this.getYhid());
		DataSet dsBjsx = this.sql.executeQuery(0, 100);

		// 发起人解析
		SysUser.genYhxxDataSet(dsBjsx, "fqr", "fqrbh", "fqrxm");

		// 循环
		for (int i = 0, n = dsBjsx.size(); i < n; i++) {
			int czcssx = dsBjsx.getInt(i, "czcssx");
			Date fqsj = dsBjsx.getDate(i, "fqsj");
			String czsj = dsBjsx.getString(i, "czsj");
			String gzrbz = dsBjsx.getString(i, "gzrbz");
			String fqsj_str = DateUtil.dateToString(fqsj, "yyyyMMddhhmmss");
			String jzrq_str = "";
			String blqk = "提前办结";
			if (czcssx > 0) {
				if ("1".equals(gzrbz)) {
					jzrq_str = DateUtil.dateToString(DateUtil.addDayExceptHoliday(fqsj, czcssx - 1), "yyyyMMdd");
				} else {
					jzrq_str = DateUtil.dateToString(DateUtil.addDay(fqsj, czcssx - 1), "yyyyMMdd");
				}
				if (czsj.compareTo(jzrq_str + "235959") > 0) {
					blqk = "延期办结";
				}
			}
			dsBjsx.put(i, "fqsj", fqsj_str);
			dsBjsx.put(i, "jzrq", jzrq_str);
			dsBjsx.put(i, "blqk", blqk);
		}

		dsBjsx.sortdesc("czsj");
		DataMap dm = new DataMap();
		dm.put("dsbjsx", dsBjsx);
		return dm;
	}

	/**
	 * 选择系统用户
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap fwdChooseSysUser(final DataMap para) throws Exception {
		String yhbh = para.getString("yhbh");
		StringBuffer sqlBF = new StringBuffer();
		if (StringUtil.chkStrNull(yhbh)) {
			yhbh = "%";
		} else {
			yhbh = "%" + yhbh + "%";
		}

		sqlBF.setLength(0);
		sqlBF.append(" select yhid, yhbh, yhmc ");
		sqlBF.append("   from fw.sys_user a ");
		sqlBF.append("  where (a.yhbh like ? ");
		sqlBF.append("     or a.yhmc like ? ");
		sqlBF.append("     or a.zjhm like ? ");
		sqlBF.append("     or a.yhmcpy like ?) ");
		sqlBF.append("   and a.yhlx in ('A', 'B') ");
		sqlBF.append("   and a.yhzt = '1' ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhbh);
		this.sql.setString(2, yhbh);
		this.sql.setString(3, yhbh);
		this.sql.setString(4, yhbh);
		DataSet dsUser = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("dsuser", dsUser);
		return dm;
	}





	/**
	 * 查询用户办结信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-12
	 * @since V1.0
	 */
	public final DataMap queryUserBjsxInfo(final DataMap para) throws Exception {
		String fqr = para.getString("fqr");
		String czr = para.getString("czr");
		String zfr = para.getString("zfr");
		String sxzt = para.getString("sxzt");
		String sxmc = para.getString("sxmc");
		String qsfqrq = para.getString("qsfqrq");
		String zzfqrq = para.getString("zzfqrq");
		String qsczrq = para.getString("qsczrq");
		String zzczrq = para.getString("zzczrq");
		String qszfrq = para.getString("qszfrq");
		String zzzfrq = para.getString("zzzfrq");

		if (StringUtil.chkStrNull(sxzt)) {
			sxzt = "1,2";
		}
		if (StringUtil.chkStrNull(sxmc)) {
			sxmc = "%";
		} else {
			sxmc = "%" + sxmc + "%";
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select sxid, sxmc, sxzt, fqr, fqsj, czr, to_char(czsj, 'yyyymmddhh24miss') czsj, czsm, zfr, to_char(zfsj, 'yyyymmddhh24miss') zfsj, zfyy, czcssx, gzrbz ");
		sqlBF.append("   from fw.proceeding ");
		sqlBF.append("  where sxmc like ? ");
		sqlBF.append("    and sxzt in (" + StringUtil.replaceC2QCQ(sxzt) + ") ");
		if (!StringUtil.chkStrNull(fqr)) {
			sqlBF.append("    and fqr = '" + fqr + "' ");
		}
		if (!StringUtil.chkStrNull(zfr)) {
			sqlBF.append("    and zfr = '" + zfr + "' ");
		}
		if (!StringUtil.chkStrNull(czr)) {
			sqlBF.append("    and czr = '" + czr + "' ");
		}
		if (!StringUtil.chkStrNull(qsfqrq)) {
			sqlBF.append("    and to_char(fqsj, 'yyyymmdd') >= '" + qsfqrq
					+ "' ");
		}
		if (!StringUtil.chkStrNull(zzfqrq)) {
			sqlBF.append("    and to_char(fqsj, 'yyyymmdd') <= '" + zzfqrq
					+ "' ");
		}
		if (!StringUtil.chkStrNull(qsczrq)) {
			sqlBF.append("    and to_char(czsj, 'yyyymmdd') >= '" + qsczrq
					+ "' ");
		}
		if (!StringUtil.chkStrNull(zzczrq)) {
			sqlBF.append("    and to_char(czsj, 'yyyymmdd') <= '" + zzczrq
					+ "' ");
		}
		if (!StringUtil.chkStrNull(qszfrq)) {
			sqlBF.append("    and to_char(zfsj, 'yyyymmdd') >= '" + qszfrq
					+ "' ");
		}
		if (!StringUtil.chkStrNull(zzzfrq)) {
			sqlBF.append("    and to_char(zfsj, 'yyyymmdd') <= '" + zzzfrq
					+ "' ");
		}
		sqlBF.append("    and rownum <= 1000 ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, sxmc);
		DataSet dsBjsx = this.sql.executeQuery();
		// 循环
		for (int i = 0, n = dsBjsx.size(); i < n; i++) {
			int czcssx = dsBjsx.getInt(i, "czcssx");
			Date fqsj = dsBjsx.getDate(i, "fqsj");
			String gzrbz = dsBjsx.getString(i, "gzrbz");
			String fqsj_str = DateUtil.dateToString(fqsj, "yyyyMMddhhmmss");
			String jzrq_str = "";
			if (czcssx > 0) {
				if ("1".equals(gzrbz)) {
					jzrq_str = DateUtil.dateToString(DateUtil.addDayExceptHoliday(fqsj, czcssx - 1), "yyyyMMdd");
				} else {
					jzrq_str = DateUtil.dateToString(DateUtil.addDay(fqsj, czcssx - 1), "yyyyMMdd");
				}
			}
			dsBjsx.put(i, "fqsj", fqsj_str);
			dsBjsx.put(i, "jzrq", jzrq_str);
		}

		SysUser.genYhxxDataSet(dsBjsx, "fqr", "fqrbh", "fqrxm");
		SysUser.genYhxxDataSet(dsBjsx, "czr", "czrbh", "czrxm");
		SysUser.genYhxxDataSet(dsBjsx, "zfr", "zfrbh", "zfrxm");

		DataMap dm = new DataMap();
		dm.put("dssx", dsBjsx);
		return dm;
	}

	/**
	 * 获取参数
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-12
	 * @since V1.0
	 */
	public final DataMap getDoProceedingPara(final DataMap para) throws Exception {
		String sxid = para.getString("sxid");
		if (StringUtil.chkStrNull(sxid)) {
			throw new BizException("传入的事项ID为空。");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.sxdygnid, a.sxdyjbjgid, a.ymcs, a.dbcs, b.ywlb, ");
		sqlBF.append("        b.gnsj, a.sxmc ");
		sqlBF.append("   from fw.proceeding a, ");
		sqlBF.append("        fw.func b ");
		sqlBF.append("  where nvl(sxzt, '0') = '0' ");
		sqlBF.append("    and nvl(a.czr, ?) = ? ");
		sqlBF.append("    and b.gnlx in ('C', 'D') ");
		sqlBF.append("    and a.sxdygnid = b.gnid ");
		sqlBF.append("    and b.dbid = ? ");
		sqlBF.append("    and a.sxid = ? ");
		if (!"A".equals(this.getSysUser().getYhlx())) {
			sqlBF.append("    and (exists (select 'x' ");
			sqlBF.append("            from fw.user_func d ");
			sqlBF.append("           where b.gnid = d.gnid ");
			sqlBF.append("             and d.yhid = ?) or exists (select 'x' ");
			sqlBF.append("            from fw.user_role e, fw.role_func f ");
			sqlBF.append("           where e.jsid = f.jsid ");
			sqlBF.append("             and f.gnid = b.gnid ");
			sqlBF.append("             and e.yhid = ?)) ");
		}
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, this.getYhid());
		this.sql.setString(2, this.getYhid());
		this.sql.setString(3, GlobalVars.SYS_DBID);
		this.sql.setString(4, sxid);
		if (!"A".equals(this.getSysUser().getYhlx())) {
			this.sql.setString(5, this.getYhid());
			this.sql.setString(6, this.getYhid());
		}
		DataSet dsDbsx = this.sql.executeQuery();
		if (dsDbsx.size() <= 0) {
			throw new BizException("该待办事项已经办结或者你没有权限操作该待办业务。");
		}

		String sxdyjbjgid = dsDbsx.getString(0, "sxdyjbjgid");
		String ywlb = dsDbsx.getString(0, "ywlb");
		String sxjbjgid = "";
		String sxjbjgqxfw = "";
		if (!StringUtil.chkStrNull(ywlb)) {
			sxjbjgqxfw = GlobalVars.AGENCY_BIZ_TYPE_MAP.get(ywlb);
			if (StringUtil.chkStrNull(sxjbjgqxfw)) {// 如果为空，则对与该节点不进行展示
				throw new BizException("您没有权限操作该待办业务。");
			}
			if (!"A".equals(this.getSysUser().getYhlx())) {// 超级管理员不进行数据权限的判断
				String yhjbjgqx = this.getSysUser()
					.getAllInfoDM()
					.getString("yhjbjgqxfw");
				sxjbjgqxfw = StringUtil.mixed2Str(sxjbjgqxfw, yhjbjgqx);
			}

			sxjbjgid = sxdyjbjgid;
			if ("*".equals(sxdyjbjgid)) {
				if (StringUtil.chkStrNull(sxjbjgqxfw)) {
					throw new BizException("您没有权限操作该待办业务。");
				}
				String[] arr_sxjbjgqxfw = sxjbjgqxfw.split(",");
				sxjbjgid = arr_sxjbjgqxfw[0];
			} else {
				if (StringUtil.chkStrNull(StringUtil.mixed2Str(sxjbjgqxfw, sxdyjbjgid))) {
					throw new BizException("您没有权限操作该待办业务。");
				}
			}
		}
		String gnsj = dsDbsx.getString(0, "gnsj");
		String sxmc = dsDbsx.getString(0, "sxmc");

		Blob ymcs = (Blob) dsDbsx.getObject(0, "ymcs");
		ObjectInputStream ymcs_inputstream = new ObjectInputStream(ymcs.getBinaryStream());
		DataMap ymcs_dm = (DataMap) ymcs_inputstream.readObject();
		ymcs_inputstream.close();

		Blob dbcs = (Blob) dsDbsx.getObject(0, "dbcs");
		ObjectInputStream dbcs_inputstream = new ObjectInputStream(dbcs.getBinaryStream());
		DataMap dbcs_dm = (DataMap) dbcs_inputstream.readObject();
		dbcs_inputstream.close();

		DataMap dm = new DataMap();
		dm.put("sxjbjgid", sxjbjgid);
		dm.put("sxjbjgqxfw", sxjbjgqxfw);
		dm.put("gnsj", gnsj);
		dm.put("sxmc", sxmc);
		dm.put("ymcs", ymcs_dm);
		dm.put("dbcs", dbcs_dm);
		return dm;
	}

	/**
	 * 业务流程详细信息查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-19
	 * @since V1.0
	 */
	public final DataMap fwdProceedingBPDetailsView(final DataMap para) throws Exception {
		String sxid = para.getString("sxid");
		StringBuffer sqlBF = new StringBuffer();
		if (StringUtil.chkStrNull(sxid)) {
			throw new BizException("传入的事项ID为空。");
		}

		DataSet dsSxxx = new DataSet();
		DataSet dsDebugPara = new DataSet();

		// 获取业务流程信息
		sqlBF.setLength(0);
		sqlBF.append(" select sxid, sxmc, sxdygnid, sxdyjbjgid, czztid, ");
		sqlBF.append("        sxzt, fqr, fqsj, czr, czsj, ");
		sqlBF.append("        zfr, zfsj, zfyy, bz, ymcs, ");
		sqlBF.append("        dbcs, czcssx, czjgsx, sjsxid, ");
		sqlBF.append("        czsm, gzrbz, lctwjbs ");
		sqlBF.append("   from fw.proceeding ");
		sqlBF.append("  where sxid = ? ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, sxid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("该事项在数据库中不存在");
		}
		String lctwjbs = dsTemp.getString(0, "lctwjbs");
		if (GlobalVars.DEBUG_MODE) {
			this.buildDebugParaDs(dsDebugPara, "sxid", String.valueOf(dsTemp.getObject(0, "sxid")));
			this.buildDebugParaDs(dsDebugPara, "sxmc", String.valueOf(dsTemp.getObject(0, "sxmc")));
			this.buildDebugParaDs(dsDebugPara, "sxdygnid", String.valueOf(dsTemp.getObject(0, "sxdygnid")));
			this.buildDebugParaDs(dsDebugPara, "sxdyjbjgid", String.valueOf(dsTemp.getObject(0, "sxdyjbjgid")));
			this.buildDebugParaDs(dsDebugPara, "czztid", String.valueOf(dsTemp.getObject(0, "czztid")));

			this.buildDebugParaDs(dsDebugPara, "sxzt", String.valueOf(dsTemp.getObject(0, "sxzt")));
			this.buildDebugParaDs(dsDebugPara, "fqr", String.valueOf(dsTemp.getObject(0, "fqr")));
			this.buildDebugParaDs(dsDebugPara, "fqsj", DateUtil.dateToString(dsTemp.getDate(0, "fqsj"), "yyyy年MM月dd日 hh:mm:ss"));
			this.buildDebugParaDs(dsDebugPara, "czr", String.valueOf(dsTemp.getObject(0, "sxid")));
			this.buildDebugParaDs(dsDebugPara, "czsj", DateUtil.dateToString(dsTemp.getDate(0, "czsj"), "yyyy年MM月dd日 hh:mm:ss"));

			this.buildDebugParaDs(dsDebugPara, "czsm", String.valueOf(dsTemp.getObject(0, "czsm")));
			this.buildDebugParaDs(dsDebugPara, "zfr", String.valueOf(dsTemp.getObject(0, "zfr")));
			this.buildDebugParaDs(dsDebugPara, "zfsj", DateUtil.dateToString(dsTemp.getDate(0, "zfsj"), "yyyy年MM月dd日 hh:mm:ss"));
			this.buildDebugParaDs(dsDebugPara, "zfyy", String.valueOf(dsTemp.getObject(0, "zfyy")));
			this.buildDebugParaDs(dsDebugPara, "bz", String.valueOf(dsTemp.getObject(0, "bz")));

			this.buildDebugParaDs(dsDebugPara, "sjsxid", String.valueOf(dsTemp.getObject(0, "sjsxid")));
			this.buildDebugParaDs(dsDebugPara, "czcssx", String.valueOf(dsTemp.getObject(0, "czcssx")));
			this.buildDebugParaDs(dsDebugPara, "czjgsx", String.valueOf(dsTemp.getObject(0, "czjgsx")));
			this.buildDebugParaDs(dsDebugPara, "gzrbz", String.valueOf(dsTemp.getObject(0, "gzrbz")));
			this.buildDebugParaDs(dsDebugPara, "lctwjbs", lctwjbs);

			Blob ymcs = (Blob) dsTemp.getObject(0, "ymcs");
			ObjectInputStream ymcs_inputstream = new ObjectInputStream(ymcs.getBinaryStream());
			DataMap ymcs_dm = (DataMap) ymcs_inputstream.readObject();
			ymcs_inputstream.close();

			Blob dbcs = (Blob) dsTemp.getObject(0, "dbcs");
			ObjectInputStream dbcs_inputstream = new ObjectInputStream(dbcs.getBinaryStream());
			DataMap dbcs_dm = (DataMap) dbcs_inputstream.readObject();
			dbcs_inputstream.close();

			this.buildDebugParaDs(dsDebugPara, "ymcs", JSONObject.fromObject(ymcs_dm)
				.toString());
			this.buildDebugParaDs(dsDebugPara, "dbcs", JSONObject.fromObject(dbcs_dm)
				.toString());
		}

		// 构建核心条数的数据
		int youbiao = 10000;// 游标
		this.buildSxxxDs(dsSxxx, dsTemp.getString(0, "sxid"), dsTemp.getString(0, "sxmc"), dsTemp.getString(0, "sxzt"), dsTemp.getString(0, "fqr"), DateUtil.dateToString(dsTemp.getDate(0, "fqsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "czr"), DateUtil.dateToString(dsTemp.getDate(0, "czsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "czsm"), dsTemp.getString(0, "zfr"), DateUtil.dateToString(dsTemp.getDate(0, "zfsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "zfyy"), youbiao--);

		// 向前找
		String sjsxid = dsTemp.getString(0, "sjsxid");
		do {
			if (StringUtil.chkStrNull(sjsxid)) {
				break;
			}
			sqlBF.setLength(0);
			sqlBF.append(" select sxid, sxmc, sxzt, fqr, fqsj, ");
			sqlBF.append("        czr, czsj, zfr, zfsj, zfyy, ");
			sqlBF.append("        sjsxid, czsm ");
			sqlBF.append("   from fw.proceeding ");
			sqlBF.append("  where sxid = ? ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, sjsxid);
			dsTemp = this.sql.executeQuery();
			if (dsTemp.size() <= 0) {
				break;
			}
			this.buildSxxxDs(dsSxxx, dsTemp.getString(0, "sxid"), dsTemp.getString(0, "sxmc"), dsTemp.getString(0, "sxzt"), dsTemp.getString(0, "fqr"), DateUtil.dateToString(dsTemp.getDate(0, "fqsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "czr"), DateUtil.dateToString(dsTemp.getDate(0, "czsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "czsm"), dsTemp.getString(0, "zfr"), DateUtil.dateToString(dsTemp.getDate(0, "zfsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "zfyy"), youbiao--);
			sjsxid = dsTemp.getString(0, "sjsxid");
		} while (true);

		// 向后找
		// 游标归位
		youbiao = 10000;
		String sxidTemp = sxid;
		do {
			sqlBF.setLength(0);
			sqlBF.append(" select sxid, sxmc, sxzt, fqr, fqsj, ");
			sqlBF.append("        czr, czsj, zfr, zfsj, zfyy, ");
			sqlBF.append("        sjsxid, czsm ");
			sqlBF.append("   from fw.proceeding ");
			sqlBF.append("  where sjsxid = ? ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, sxidTemp);
			dsTemp = this.sql.executeQuery();
			if (dsTemp.size() <= 0) {
				break;
			}
			this.buildSxxxDs(dsSxxx, dsTemp.getString(0, "sxid"), dsTemp.getString(0, "sxmc"), dsTemp.getString(0, "sxzt"), dsTemp.getString(0, "fqr"), DateUtil.dateToString(dsTemp.getDate(0, "fqsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "czr"), DateUtil.dateToString(dsTemp.getDate(0, "czsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "czsm"), dsTemp.getString(0, "zfr"), DateUtil.dateToString(dsTemp.getDate(0, "zfsj"), "yyyy年MM月dd日 hh:mm:ss"), dsTemp.getString(0, "zfyy"), ++youbiao);
			sxidTemp = dsTemp.getString(0, "sxid");
		} while (1 == 1);

		SysUser.genYhxxDataSet(dsSxxx, "fqr", "fqrbh", "fqrxm");
		SysUser.genYhxxDataSet(dsSxxx, "czr", "czrbh", "czrxm");
		SysUser.genYhxxDataSet(dsSxxx, "zfr", "zfrbh", "zfrxm");

		// 返回数据
		dsSxxx.sort("xh");
		DataMap dm = new DataMap();
		dm.put("dssxxx", dsSxxx);
		dm.put("dsdebugpara", dsDebugPara);
		dm.put("sxid", sxid);
		dm.put("lctwjbs", lctwjbs);// 流程图文件标识
		return dm;
	}

	/**
	 * 构建事项信息DS
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-19
	 * @since V1.0
	 */
	private DataSet buildSxxxDs(DataSet ds, String sxid, String sxmc,
			String sxzt, String fqr, String fqsj, String czr, String czsj,
			String czsm, String zfr, String zfsj, String zfyy, int xh) throws Exception {
		if (null == ds) {
			ds = new DataSet();
		}
		ds.addRow();
		int row = ds.size() - 1;
		ds.put(row, "sxid", sxid);
		ds.put(row, "sxmc", sxmc);
		ds.put(row, "sxzt", sxzt);
		ds.put(row, "fqr", fqr);
		ds.put(row, "fqsj", fqsj);
		ds.put(row, "czr", czr);
		ds.put(row, "czsj", czsj);
		ds.put(row, "czsm", czsm);
		ds.put(row, "zfr", zfr);
		ds.put(row, "zfsj", zfsj);
		ds.put(row, "zfyy", zfyy);
		ds.put(row, "xh", xh);

		return ds;
	}

	/**
	 * 构建debug参数列表
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-19
	 * @since V1.0
	 */
	private DataSet buildDebugParaDs(DataSet ds, String xmbh, String xmz) throws Exception {
		if (null == ds) {
			ds = new DataSet();
		}
		ds.addRow();
		int row = ds.size() - 1;
		ds.put(row, "xmbh", xmbh);
		ds.put(row, "xmz", xmz);
		return ds;
	}
}
