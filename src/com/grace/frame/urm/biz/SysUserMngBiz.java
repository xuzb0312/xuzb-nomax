package com.grace.frame.urm.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.AgencyUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.SecUtil;
import com.grace.frame.util.SeqUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 用户管理Biz
 * 
 * @author yjc
 */
public class SysUserMngBiz extends Biz{

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
		sqlBF.append(" select yhid, yhbh, yhlx, yhmc, zjlx, ");
		sqlBF.append("        zjhm, ssjbjgid, ssjgmc, ssjgbm, nvl(yhzt, '1') yhzt, ");
		sqlBF.append("        zxrq, to_char(zxjbsj, 'yyyymmddhh24miss') zxjbsj, zxyy ");
		sqlBF.append("   from fw.sys_user a ");
		sqlBF.append("  where a.yhbh like ? ");
		sqlBF.append("     or a.yhmc like ? ");
		sqlBF.append("     or a.zjhm like ? ");
		sqlBF.append("     or a.yhmcpy like ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhbh);
		this.sql.setString(2, yhbh);
		this.sql.setString(3, yhbh);
		this.sql.setString(4, yhbh);
		DataSet dsUser = this.sql.executeQuery();

		dsUser.sort("yhbh");// 增加默认排序

		// 获取经办机构mcDs
		this.sql.setSql(" select jbjgid code, jbjgmc content from fw.sys_agency ");
		DataSet dsJbjg = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("dsuser", dsUser);
		dm.put("dsjbjg", dsJbjg);
		return dm;
	}

	/**
	 * 进入用户新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap fwdSysUserAdd(final DataMap para) throws Exception {
		DataMap dm = new DataMap();
		dm.put("dsjbjg", this.getJbjgDsWithDataRight());
		return dm;
	}

	/**
	 * 获取经办机构DS
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	private DataSet getJbjgDsWithDataRight() throws Exception {
		StringBuffer sqlBF = new StringBuffer();

		// 获取dsJbjg-所有
		sqlBF.setLength(0);
		sqlBF.append(" select a.jbjgid code, a.jbjgmc content ");
		sqlBF.append("   from fw.sys_agency a, fw.agency_biz_type b ");
		sqlBF.append("  where a.jbjgid = b.jbjgid ");
		sqlBF.append("    and b.dbid = ? ");
		if (!"A".equals(this.getSysUser().getYhlx())) {
			sqlBF.append("    and exists (select 'x' ");
			sqlBF.append("           from fw.user_data_right d ");
			sqlBF.append("          where a.jbjgid = d.jbjgid ");
			sqlBF.append("            and d.yhid = '" + this.getYhid() + "') ");
		}
		sqlBF.append("  group by a.jbjgid, a.jbjgmc ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsJbjg = this.sql.executeQuery();
		dsJbjg.sort("code");
		return dsJbjg;
	}

	/**
	 * 保存用户新增。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap saveSysUserAdd(final DataMap para) throws Exception {
		String yhbh = para.getString("yhbh");// 用户编号
		String yhlx = para.getString("yhlx");// 用户类型
		String password = para.getString("password");// 用户密码
		String yhmc = para.getString("yhmc");// 用户名称
		String zjlx = para.getString("zjlx");// 证件类型
		String zjhm = para.getString("zjhm");// 证件号码
		String ssjbjgid = para.getString("ssjbjgid");// 所属经办机构
		String ssjgmc = para.getString("ssjgmc");// 所属机构名称
		String ssjgbm = para.getString("ssjgbm");// 所属机构部门
		String sjqxpz = para.getString("sjqxpz", "0");// 是否同时配置数据权限
		String sjhm = para.getString("sjhm");// 手机号码
		String dzyx = para.getString("dzyx");// 电子邮箱

		if (StringUtil.chkStrNull(yhbh)) {
			throw new BizException("用户编号为空");
		}
		if (StringUtil.chkStrNull(yhlx)) {
			throw new BizException("用户类型为空");
		}
		if (StringUtil.chkStrNull(password)) {
			throw new BizException("用户密码为空");
		}
		if (StringUtil.chkStrNull(yhmc)) {
			throw new BizException("用户名称为空");
		}
		String yhmcpy = StringUtil.getPy(yhmc);

		// 检查用户编号是否已经存在了
			this.sql.setSql(" select yhid from fw.sys_user where yhbh = ? ");
		this.sql.setString(1, yhbh);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该用户编号已经存在无法再次新增");
		}

		// 密码加密
		String encodePwd = SecUtil.encodeStrByMd5(password);
		String yhid = SeqUtil.getId("fw.sq_yhid");// 用户ID
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.sys_user ");
		sqlBF.append("   (yhid, yhbh, yhlx, password, yhmc, ");
		sqlBF.append("    zjlx, zjhm, ssjbjgid, ssjgmc, ssjgbm, ");
		sqlBF.append("    yhzt, zxrq, zxjbsj, zxyy, yhmcpy, ");
		sqlBF.append("    sjhm, dzyx) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?, ?, ?, ?, ");
		sqlBF.append("    '1', null, null, null, ?, ");
		sqlBF.append("    ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhid);
		this.sql.setString(2, yhbh);
		this.sql.setString(3, yhlx);
		this.sql.setString(4, encodePwd);
		this.sql.setString(5, yhmc);

		this.sql.setString(6, zjlx);
		this.sql.setString(7, zjhm);
		this.sql.setString(8, ssjbjgid);
		this.sql.setString(9, ssjgmc);
		this.sql.setString(10, ssjgbm);

		this.sql.setString(11, yhmcpy);
		this.sql.setString(12, sjhm);
		this.sql.setString(13, dzyx);
		this.sql.executeUpdate();

		// 数据权限配置
		if ("1".equals(sjqxpz) && !StringUtil.chkStrNull(ssjbjgid)) {
			this.sql.setSql(" insert into fw.user_data_right(jbjgid, yhid) values (?, ?) ");
			this.sql.setString(1, ssjbjgid);
			this.sql.setString(2, yhid);
			this.sql.executeUpdate();
		}

		// 记录日志
		this.log("SYS-A-YHXZ", "用户新增", "A", yhid, "新增用户ID:" + yhid + ",用户编号:"
				+ yhbh + ",用户名称:" + yhmc, "yhid=" + yhid + ",yhbh=" + yhbh
				+ ",yhmc=" + yhmc + ",yhlx:" + yhlx + ",sjqxpz:" + sjqxpz);

		DataMap rdm = new DataMap();
		rdm.put("yhid", yhid);
		rdm.put("yhbh", yhbh);
		rdm.put("yhmc", yhmc);
		return rdm;
	}

	/**
	 * 保存权限
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap saveSysUserGnqxMng(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		String gnids = para.getString("gnids");

		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("用户ID为空");
		}

		// 首先删除所有的权限配置
		if ("A".equals(this.getSysUser().getYhlx())) {
			this.sql.setSql(" delete from fw.user_func where yhid = ? ");
			this.sql.setString(1, yhid);
			this.sql.executeUpdate();
		} else {
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" delete from fw.user_func ");
			sqlBF.append("  where yhid = ? ");
			sqlBF.append("    and gnid in (select a.gnid ");
			sqlBF.append("                   from fw.func a ");
			sqlBF.append("                  where a.dbid = ? ");
			sqlBF.append("                    and (exists (select 'x' ");
			sqlBF.append("                                   from fw.user_func b ");
			sqlBF.append("                                  where a.gnid = b.gnid ");
			sqlBF.append("                                    and b.yhid = ?) or exists ");
			sqlBF.append("                         (select 'x' ");
			sqlBF.append("                            from fw.role_func e, fw.user_role f ");
			sqlBF.append("                           where e.jsid = f.jsid ");
			sqlBF.append("                             and f.yhid = ? ");
			sqlBF.append("                             and a.gnid = e.gnid))) ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, yhid);
			this.sql.setString(2, GlobalVars.SYS_DBID);
			this.sql.setString(3, this.getYhid());
			this.sql.setString(4, this.getYhid());
			this.sql.executeUpdate();
		}

		// 只有gnid不为空的时候，才允许插入功能。
		if (!StringUtil.chkStrNull(gnids)) {
			// 查询叶子节点
			String[] arrGnids = gnids.split(",");
			this.sql.setSql(" insert into fw.user_func(yhid, gnid) values (?, ?) ");
			for (int i = 0, n = arrGnids.length; i < n; i++) {
				this.sql.setString(1, yhid);
				this.sql.setString(2, arrGnids[i]);
				this.sql.addBatch();
			}
			this.sql.executeBatch();

			// 自动生成上级的节点-自动纠正
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" insert into fw.user_func ");
			sqlBF.append("   (yhid, gnid) ");
			sqlBF.append("   select ? yhid, b.fgn gnid ");
			sqlBF.append("     from fw.func b ");
			sqlBF.append("    where b.dbid = ? ");
			sqlBF.append("      and exists (select 'x' ");
			sqlBF.append("             from fw.user_func d ");
			sqlBF.append("            where b.gnid = d.gnid ");
			sqlBF.append("              and d.yhid = ?) ");
			sqlBF.append("      and b.fgn <> 'root' ");
			sqlBF.append("      and b.gnlx <> 'A' ");
			sqlBF.append("      and not exists (select 'x' ");
			sqlBF.append("             from fw.user_func c ");
			sqlBF.append("            where b.fgn = c.gnid ");
			sqlBF.append("              and c.yhid = ?) ");
			sqlBF.append("    group by b.fgn ");
			do {// 循环执行，直到自动纠正完成
				this.sql.setSql(sqlBF.toString());
				this.sql.setString(1, yhid);
				this.sql.setString(2, GlobalVars.SYS_DBID);
				this.sql.setString(3, yhid);
				this.sql.setString(4, yhid);
				int rows = this.sql.executeUpdate();
				if (rows <= 0) {
					break;
				}
			} while (true);
		}

		// 日志记录
		this.log("SYS-A-GNQXBC", "功能权限保存", "A", yhid, "保存用户ID:" + yhid
				+ "的用户功能权限", "yhid=" + yhid);

		return null;
	}

	/**
	 * 进入用户数据权限管理的界面，将用户当前的数据权限展示出来
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserSjqxMng(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		DataMap rdm = this.queryeSysUserSjqx(para);
		rdm.put("yhid", yhid);
		return rdm;
	}

	/**
	 * 查询用户的数据权限信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap queryeSysUserSjqx(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 可以进入该页面的均为普通业务用户
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.jbjgid, a.jbjgbh, a.jbjgmc, sjjbjgid ");
		sqlBF.append("   from fw.sys_agency a, fw.user_data_right b ");
		sqlBF.append("  where a.jbjgid = b.jbjgid ");
		sqlBF.append("    and b.yhid = ? ");
		sqlBF.append("    and exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type c ");
		sqlBF.append("          where a.jbjgid = b.jbjgid ");
		sqlBF.append("            and c.dbid = ?) ");
		sqlBF.append("  group by a.jbjgid, a.jbjgbh, a.jbjgmc, sjjbjgid ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsSjqx = this.sql.executeQuery();

		AgencyUtil.genJbjgxxDataSet(dsSjqx, "sjjbjgid", "sjjbjgbh", "sjjbjgmc");// 上级经办机构信息

		dsSjqx.sort("jbjgbh");

		DataMap rdm = new DataMap();
		rdm.put("sjqxinfo", dsSjqx);
		return rdm;
	}

	/**
	 * 数据权限删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap deleteSysUserSjqx(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		DataSet dsSjqx = para.getDataSet("gridSjqx");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (null == dsSjqx) {
			throw new BizException("传入的数据权限为空");
		}

		// 数据权限删除
		this.sql.setSql(" delete from fw.user_data_right where jbjgid = ? and yhid = ? ");
		for (int i = 0, n = dsSjqx.size(); i < n; i++) {
			String jbjgid = dsSjqx.getString(i, "jbjgid");

			this.sql.setString(1, jbjgid);
			this.sql.setString(2, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-A-SJQXSC", "用户数据权限删除", "A", yhid, "保存用户ID:" + yhid
				+ "的用户数据权限删除", "yhid=" + yhid);

		return null;
	}

	/**
	 * 数据权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserSjqxAdd(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 可以进入该页面的均为普通业务用户
		StringBuffer sqlBF = new StringBuffer();
		if ("A".equals(this.getSysUser().getYhlx())) {
			sqlBF.append(" select a.jbjgid, a.jbjgbh, a.jbjgmc, a.sjjbjgid ");
			sqlBF.append("   from fw.sys_agency a ");
			sqlBF.append("  where exists (select 'x' ");
			sqlBF.append("           from fw.agency_biz_type c ");
			sqlBF.append("          where a.jbjgid = a.jbjgid ");
			sqlBF.append("            and c.dbid = ?) ");
			sqlBF.append("    and not exists (select 'x' ");
			sqlBF.append("           from fw.user_data_right b ");
			sqlBF.append("          where a.jbjgid = b.jbjgid ");
			sqlBF.append("            and b.yhid = ?) ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, yhid);
		} else {
			// 普通用户
			sqlBF.append(" select a.jbjgid, a.jbjgbh, a.jbjgmc, a.sjjbjgid ");
			sqlBF.append("   from fw.sys_agency a ");
			sqlBF.append("  where exists (select 'x' ");
			sqlBF.append("           from fw.agency_biz_type c ");
			sqlBF.append("          where a.jbjgid = a.jbjgid ");
			sqlBF.append("            and c.dbid = ?) ");
			sqlBF.append("    and not exists (select 'x' ");
			sqlBF.append("           from fw.user_data_right b ");
			sqlBF.append("          where a.jbjgid = b.jbjgid ");
			sqlBF.append("            and b.yhid = ?) ");
			sqlBF.append("    and exists (select 'x' ");
			sqlBF.append("           from fw.user_data_right d ");
			sqlBF.append("          where a.jbjgid = d.jbjgid ");
			sqlBF.append("            and d.yhid = ?) ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, yhid);
			this.sql.setString(3, this.getYhid());
		}

		DataSet dsSjqx = this.sql.executeQuery();

		AgencyUtil.genJbjgxxDataSet(dsSjqx, "sjjbjgid", "sjjbjgbh", "sjjbjgmc");// 上级经办机构信息

		dsSjqx.sort("jbjgbh");

		DataMap rdm = new DataMap();
		rdm.put("sjqxinfo", dsSjqx);
		rdm.put("yhid", yhid);
		return rdm;
	}

	/**
	 * 数据权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap saveSysUserSjqxAdd(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		DataSet dsSjqx = para.getDataSet("gridSjqx");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (null == dsSjqx) {
			throw new BizException("传入的数据权限为空");
		}

		// 数据权限删除
		this.sql.setSql(" insert into fw.user_data_right(jbjgid, yhid) values (?, ?) ");
		for (int i = 0, n = dsSjqx.size(); i < n; i++) {
			String jbjgid = dsSjqx.getString(i, "jbjgid");

			this.sql.setString(1, jbjgid);
			this.sql.setString(2, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-A-SJQXXZ", "用户数据权限新增", "A", yhid, "保存用户ID:" + yhid
				+ "的用户数据权限新增", "yhid=" + yhid);

		return null;
	}

	/**
	 * 特殊权限管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserTsqxMng(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		DataMap rdm = this.queryeSysUserTsqx(para);
		rdm.put("yhid", yhid);
		return rdm;
	}

	/**
	 * 查询用户的特殊权限信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap queryeSysUserTsqx(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 可以进入该页面的均为普通业务用户
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.tsqxid, a.tsqxmc, a.bz ");
		sqlBF.append("   from fw.special_right a, fw.user_sp_right b ");
		sqlBF.append("  where a.tsqxid = b.tsqxid ");
		sqlBF.append("    and b.yhid = ? ");
		sqlBF.append("    and a.dbid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTsqx = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("tsqxinfo", dsTsqx);
		return rdm;
	}

	/**
	 * 特殊权限删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap deleteSysUserTsqx(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		DataSet dsTsqx = para.getDataSet("gridTsqx");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (null == dsTsqx) {
			throw new BizException("传入的特殊权限为空");
		}

		// 数据权限删除
		this.sql.setSql(" delete from fw.user_sp_right where tsqxid = ? and yhid = ? ");
		for (int i = 0, n = dsTsqx.size(); i < n; i++) {
			String tsqxid = dsTsqx.getString(i, "tsqxid");

			this.sql.setString(1, tsqxid);
			this.sql.setString(2, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-A-TSQXSC", "用户特殊权限删除", "A", yhid, "保存用户ID:" + yhid
				+ "的特殊权限删除", "yhid=" + yhid);

		return null;
	}

	/**
	 * 特殊权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserTsqxAdd(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 可以进入该页面的均为普通业务用户
		StringBuffer sqlBF = new StringBuffer();
		if ("A".equals(this.getSysUser().getYhlx())) {
			sqlBF.append(" select tsqxid, tsqxmc, bz ");
			sqlBF.append("   from fw.special_right a ");
			sqlBF.append("  where a.dbid = ? ");
			sqlBF.append("    and not exists (select 'x' ");
			sqlBF.append("           from fw.user_sp_right b ");
			sqlBF.append("          where a.tsqxid = b.tsqxid ");
			sqlBF.append("            and b.yhid = ?) ");
			this.sql.setSql(sqlBF.toString());

			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, yhid);
		} else {
			// 普通用户
			sqlBF.append(" select tsqxid, tsqxmc, bz ");
			sqlBF.append("   from fw.special_right a ");
			sqlBF.append("  where a.dbid = ? ");
			sqlBF.append("    and not exists (select 'x' ");
			sqlBF.append("           from fw.user_sp_right b ");
			sqlBF.append("          where a.tsqxid = b.tsqxid ");
			sqlBF.append("            and b.yhid = ?) ");
			sqlBF.append("    and exists (select 'x' ");
			sqlBF.append("           from fw.user_sp_right d ");
			sqlBF.append("          where a.tsqxid = d.tsqxid ");
			sqlBF.append("            and d.yhid = ?) ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, yhid);
			this.sql.setString(3, this.getYhid());
		}
		DataSet dsTsqx = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("tsqxinfo", dsTsqx);
		rdm.put("yhid", yhid);
		return rdm;
	}

	/**
	 * 数据权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap saveSysUserTsqxAdd(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		DataSet dsTsqx = para.getDataSet("gridTsqx");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (null == dsTsqx) {
			throw new BizException("传入的特殊权限为空");
		}

		// 数据权限删除
		this.sql.setSql(" insert into fw.user_sp_right(tsqxid, yhid) values (?, ?) ");
		for (int i = 0, n = dsTsqx.size(); i < n; i++) {
			String tsqxid = dsTsqx.getString(i, "tsqxid");

			this.sql.setString(1, tsqxid);
			this.sql.setString(2, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-A-TSQXXZ", "用户特殊权限新增", "A", yhid, "保存用户ID:" + yhid
				+ "的用户特殊权限新增", "yhid=" + yhid);

		return null;
	}

	/**
	 * 服务访问权限管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserFwfwqxMng(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		DataMap rdm = this.queryeSysUserFwfwqx(para);
		rdm.put("yhid", yhid);
		return rdm;
	}

	/**
	 * 查询用户的服务访问权限信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap queryeSysUserFwfwqx(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 可以进入该页面的均为普通业务用户
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.setLength(0);
		sqlBF.append(" select a.fwmc, a.fwff, a.biz, a.bizff ");
		sqlBF.append("   from fw.service_config a, fw.service_right b ");
		sqlBF.append("  where a.fwmc = b.fwmc ");
		sqlBF.append("    and a.fwff = b.fwff ");
		sqlBF.append("    and a.dbid = ? ");
		sqlBF.append("    and b.yhid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, yhid);
		DataSet dsFwfwqx = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("fwfwqxinfo", dsFwfwqx);
		return rdm;
	}

	/**
	 * 服务访问权限删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap deleteSysUserFwfwqx(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		DataSet dsFwfwqx = para.getDataSet("gridFwfwqx");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (null == dsFwfwqx) {
			throw new BizException("传入的服务访问权限为空");
		}

		// 数据权限删除
		this.sql.setSql(" delete from fw.service_right where fwmc = ? and fwff = ? and yhid = ? ");
		for (int i = 0, n = dsFwfwqx.size(); i < n; i++) {
			String fwmc = dsFwfwqx.getString(i, "fwmc");
			String fwff = dsFwfwqx.getString(i, "fwff");

			this.sql.setString(1, fwmc);
			this.sql.setString(2, fwff);
			this.sql.setString(3, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();
		
		// 对于服务权限缓存数据，进行清空
		GlobalVarsUtil.clearSERVICE_RIGHT_MAP();

		// 日志记录
		this.log("SYS-A-FWFWQXSC", "服务访问权限删除", "A", yhid, "保存用户ID:" + yhid
				+ "的服务访问权限删除", "yhid=" + yhid);

		return null;
	}

	/**
	 * 服务访问权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserFwfwqxAdd(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 可以进入该页面的均为普通业务用户
		StringBuffer sqlBF = new StringBuffer();
		if ("A".equals(this.getSysUser().getYhlx())) {
			sqlBF.append(" select a.fwmc, a.fwff, a.biz, a.bizff ");
			sqlBF.append("   from fw.service_config a ");
			sqlBF.append("  where a.dbid = ? ");
			sqlBF.append("    and not exists (select 'x' ");
			sqlBF.append("           from fw.service_right b ");
			sqlBF.append("          where a.fwmc = b.fwmc ");
			sqlBF.append("            and a.fwff = b.fwff ");
			sqlBF.append("            and b.yhid = ?) ");
			this.sql.setSql(sqlBF.toString());

			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, yhid);
		} else {
			// 普通用户
			sqlBF.append(" select a.fwmc, a.fwff, a.biz, a.bizff ");
			sqlBF.append("   from fw.service_config a ");
			sqlBF.append("  where a.dbid = ? ");
			sqlBF.append("    and not exists (select 'x' ");
			sqlBF.append("           from fw.service_right b ");
			sqlBF.append("          where a.fwmc = b.fwmc ");
			sqlBF.append("            and a.fwff = b.fwff ");
			sqlBF.append("            and b.yhid = ?) ");
			sqlBF.append("    and exists (select 'x' ");
			sqlBF.append("           from fw.service_right d ");
			sqlBF.append("          where a.fwmc = d.fwmc ");
			sqlBF.append("            and a.fwff = d.fwff ");
			sqlBF.append("            and d.yhid = ?) ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, yhid);
			this.sql.setString(3, this.getYhid());
		}
		DataSet dsFwfwqx = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("fwfwqxinfo", dsFwfwqx);
		rdm.put("yhid", yhid);
		return rdm;
	}

	/**
	 * 数据权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap saveSysUserFwfwqxAdd(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		DataSet dsFwfwqx = para.getDataSet("gridFwfwqx");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (null == dsFwfwqx) {
			throw new BizException("传入的服务访问权限为空");
		}

		// 数据权限删除
		this.sql.setSql(" insert into fw.service_right(fwmc, fwff, yhid) values (?, ?, ?) ");
		for (int i = 0, n = dsFwfwqx.size(); i < n; i++) {
			String fwmc = dsFwfwqx.getString(i, "fwmc");
			String fwff = dsFwfwqx.getString(i, "fwff");

			this.sql.setString(1, fwmc);
			this.sql.setString(2, fwff);
			this.sql.setString(3, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();
		
		// 对于服务权限缓存数据，进行清空
		GlobalVarsUtil.clearSERVICE_RIGHT_MAP();

		// 日志记录
		this.log("SYS-A-FWFWQXXZ", "服务访问权限新增", "A", yhid, "保存用户ID:" + yhid
				+ "的用户服务访问权限新增", "yhid=" + yhid);

		return null;
	}

	/**
	 * 角色管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserJsglMng(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		DataMap rdm = this.queryeSysUserJsgl(para);
		rdm.put("yhid", yhid);
		return rdm;
	}

	/**
	 * 查询用户的角色管理信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap queryeSysUserJsgl(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 可以进入该页面的均为普通业务用户
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.jsid, a.jsmc, a.bz ");
		sqlBF.append("   from fw.sys_role a, fw.user_role b ");
		sqlBF.append("  where a.jsid = b.jsid ");
		sqlBF.append("    and b.yhid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhid);
		DataSet dsJsgl = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("jsglinfo", dsJsgl);
		return rdm;
	}

	/**
	 * 角色管理删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap deleteSysUserJsgl(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		DataSet dsJsgl = para.getDataSet("gridJsgl");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (null == dsJsgl) {
			throw new BizException("传入的角色管理为空");
		}

		// 数据权限删除
		this.sql.setSql(" delete from fw.user_role where jsid = ? and yhid = ? ");
		for (int i = 0, n = dsJsgl.size(); i < n; i++) {
			String jsid = dsJsgl.getString(i, "jsid");

			this.sql.setString(1, jsid);
			this.sql.setString(2, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-A-YHJSSC", "用户角色删除", "A", yhid, "用户ID:" + yhid + "的角色删除", "yhid="
				+ yhid);

		return null;
	}

	/**
	 * 角色管理新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserJsglAdd(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 可以进入该页面的均为普通业务用户
		StringBuffer sqlBF = new StringBuffer();
		if ("A".equals(this.getSysUser().getYhlx())) {
			sqlBF.append(" select a.jsid, a.jsmc, a.bz ");
			sqlBF.append("   from fw.sys_role a ");
			sqlBF.append("  where not exists (select 'x' ");
			sqlBF.append("           from fw.user_role b ");
			sqlBF.append("          where a.jsid = b.jsid ");
			sqlBF.append("            and b.yhid = ?) ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, yhid);
		} else {
			// 普通用户
			sqlBF.append(" select a.jsid, a.jsmc, a.bz ");
			sqlBF.append("   from fw.sys_role a ");
			sqlBF.append("  where not exists (select 'x' ");
			sqlBF.append("           from fw.user_role b ");
			sqlBF.append("          where a.jsid = b.jsid ");
			sqlBF.append("            and b.yhid = ?) ");
			sqlBF.append("    and exists (select 'x' ");
			sqlBF.append("           from fw.user_role b ");
			sqlBF.append("          where a.jsid = b.jsid ");
			sqlBF.append("            and b.yhid = ?) ");
			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, yhid);
			this.sql.setString(2, this.getYhid());
		}
		DataSet dsJsgl = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("jsglinfo", dsJsgl);
		rdm.put("yhid", yhid);
		return rdm;
	}

	/**
	 * 数据权限新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap saveSysUserJsglAdd(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		DataSet dsJsgl = para.getDataSet("gridJsgl");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (null == dsJsgl) {
			throw new BizException("传入的角色管理为空");
		}

		// 数据权限删除
		this.sql.setSql(" insert into fw.user_role(jsid, yhid) values (?, ?) ");
		for (int i = 0, n = dsJsgl.size(); i < n; i++) {
			String jsid = dsJsgl.getString(i, "jsid");

			this.sql.setString(1, jsid);
			this.sql.setString(2, yhid);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 日志记录
		this.log("SYS-A-YHJSXZ", "用户角色新增", "A", yhid, "用户ID:" + yhid
				+ "的用户角色新增", "yhid=" + yhid);

		return null;
	}

	/**
	 * 用户信息管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserGrxxglMng(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select yhid, yhbh, yhlx, yhmc, zjlx, ");
		sqlBF.append("        zjhm, ssjbjgid, ssjgmc, ssjgbm, nvl(yhzt, '1') yhzt, ");
		sqlBF.append("        zxrq, to_char(zxjbsj, 'yyyymmddhh24miss') zxjbsj, zxyy, sjhm, dzyx, ");
		sqlBF.append("        password yhmy ");// add.yjc.2017年4月30日-服务的验证方式调整，使用签名的方式，该处密文密码为提供给使用放的密钥
		sqlBF.append("   from fw.sys_user ");
		sqlBF.append("  where yhid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhid);
		DataSet dsUser = this.sql.executeQuery();
		if (dsUser.size() <= 0) {
			throw new BizException("用户ID为" + yhid + "的用户在系统中不存在。");
		}

		DataMap dmUser = dsUser.getRow(0);
		String ssjbjgid = dmUser.getString("ssjbjgid");
		if (!StringUtil.chkStrNull(ssjbjgid)) {
			this.sql.setSql(" select jbjgmc from fw.sys_agency where jbjgid = ? ");
			this.sql.setString(1, ssjbjgid);
			DataSet dsTemp = this.sql.executeQuery();
			if (dsTemp.size() > 0) {
				dmUser.put("ssjbjgid", dsTemp.getString(0, "jbjgmc"));
			}
		}

		DataMap rdm = new DataMap();
		rdm.put("userinfo", dmUser);
		rdm.put("yhlx", dmUser.getString("yhlx"));// 用户类型，该处特殊处理
		return rdm;
	}

	/**
	 * 密码重置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap saveSysUserPwdReset(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		String pwd = para.getString("pwd");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (StringUtil.chkStrNull(pwd)) {
			throw new BizException("用户密码为空");
		}

		// 密码加密
		String encodePwd = SecUtil.encodeStrByMd5(pwd);

		this.sql.setSql(" update fw.sys_user set password = ? where yhid = ? ");
		this.sql.setString(1, encodePwd);
		this.sql.setString(2, yhid);
		this.sql.executeUpdate();

		// 日志记录
		this.log("SYS-A-YHMMCZ", "用户密码重置", "A", yhid, "用户ID:" + yhid
				+ "的用户密码重置", "yhid=" + yhid);

		return null;
	}

	/**
	 * 撤销注销操作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap cancelSysUserDestroy(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		// 获取注销信息
		this.sql.setSql(" select yhzt, zxrq, to_char(zxjbsj, 'yyyymmddhh24miss') zxjbsj, zxyy from fw.sys_user where yhid = ? ");
		this.sql.setString(1, yhid);
		DataSet dsUser = this.sql.executeQuery();
		if (dsUser.size() <= 0) {
			throw new BizException("用户ID为" + yhid + "的用户在系统中不存在。");
		}
		String yhzt = dsUser.getString(0, "yhzt");
		String zxrq = dsUser.getString(0, "zxrq");
		String zxjbsj = dsUser.getString(0, "zxjbsj");
		String zxyy = dsUser.getString(0, "zxyy");
		if ("1".equals(yhzt)) {
			throw new BizException("该用户的用户状态为正常，无法撤销用户注销");
		}

		// 撤销注销
		this.sql.setSql(" update fw.sys_user set yhzt = '1', zxrq = null, zxjbsj = null, zxyy = null where yhid = ? ");
		this.sql.setString(1, yhid);
		this.sql.executeUpdate();

		// 记录日志
		this.log("SYS-A-CXYHZX", "撤销用户注销", "A", yhid, "撤销用户(用户ID:" + yhid
				+ ")注销", "yzxrq=" + zxrq + ",yzxjbsj=" + zxjbsj + ",yzxyy="
				+ zxyy);

		return null;
	}

	/**
	 * 用户注销操作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap saveSysUserDestroy(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		String zxrq = para.getString("zxrq");
		String zxyy = para.getString("zxyy");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}
		if (StringUtil.chkStrNull(zxrq)) {
			throw new BizException("传入的注销日期为空");
		}

		// 获取注销信息
		this.sql.setSql(" select yhzt from fw.sys_user where yhid = ? ");
		this.sql.setString(1, yhid);
		DataSet dsUser = this.sql.executeQuery();
		if (dsUser.size() <= 0) {
			throw new BizException("用户ID为" + yhid + "的用户在系统中不存在。");
		}
		String yhzt = dsUser.getString(0, "yhzt");
		if (!"1".equals(yhzt)) {
			throw new BizException("该用户的用户状态不是正常，无法注销用户");
		}

		// 注销
		this.sql.setSql(" update fw.sys_user set yhzt = '0', zxrq = ?, zxjbsj = sysdate, zxyy = ? where yhid = ? ");
		this.sql.setString(1, zxrq);
		this.sql.setString(2, zxyy);
		this.sql.setString(3, yhid);
		this.sql.executeUpdate();

		// 记录日志
		this.log("SYS-A-YHZX", "用户注销", "A", yhid, "用户(用户ID:" + yhid + ")注销", "zxrq="
				+ zxrq + ",zxyy=" + zxyy + ",yhid=" + yhid);

		return null;
	}

	/**
	 * 用户信息修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap fwdSysUserInfoModify(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("传入的用户ID为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select yhid, yhbh, yhlx, yhmc, zjlx, ");
		sqlBF.append("        zjhm, ssjbjgid, ssjgmc, ssjgbm, sjhm, ");
		sqlBF.append("        dzyx ");
		sqlBF.append("   from fw.sys_user ");
		sqlBF.append("  where yhid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhid);
		DataSet dsUser = this.sql.executeQuery();
		if (dsUser.size() <= 0) {
			throw new BizException("用户ID为" + yhid + "的用户在系统中不存在。");
		}

		// 是否可以更改用户类别-如果为超级管理员，则可以更改，否则不可更
		boolean ggyhlx = true;
		if ("A".equals(this.getSysUser().getYhlx())) {
			ggyhlx = false;
		}

		DataMap rdm = new DataMap();
		rdm.put("userinfo", dsUser.getRow(0));
		rdm.put("dsjbjg", this.getJbjgDsWithDataRight());
		rdm.put("ggyhlx", ggyhlx);
		return rdm;
	}

	/**
	 * 保存用户信息修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap saveSysUserModify(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");// 用户ID
		String yhbh = para.getString("yhbh");// 用户编号
		String yhlx = para.getString("yhlx");// 用户类型
		String yhmc = para.getString("yhmc");// 用户名称
		String zjlx = para.getString("zjlx");// 证件类型
		String zjhm = para.getString("zjhm");// 证件号码
		String ssjbjgid = para.getString("ssjbjgid");// 所属经办机构
		String ssjgmc = para.getString("ssjgmc");// 所属机构名称
		String ssjgbm = para.getString("ssjgbm");// 所属机构部门
		String sjhm = para.getString("sjhm");
		String dzyx = para.getString("dzyx");
		String sjqxpz = para.getString("sjqxpz", "0");// 是否同时配置数据权限

		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("用户ID为空");
		}
		if (StringUtil.chkStrNull(yhbh)) {
			throw new BizException("用户编号为空");
		}
		if (StringUtil.chkStrNull(yhlx)) {
			throw new BizException("用户类型为空");
		}
		if (StringUtil.chkStrNull(yhmc)) {
			throw new BizException("用户名称为空");
		}
		String yhmcpy = StringUtil.getPy(yhmc);// 用户名称拼音

		// 检查用户编号是否已经存在了
		this.sql.setSql(" select yhid from fw.sys_user where yhbh = ? and yhid <> ? ");
		this.sql.setString(1, yhbh);
		this.sql.setString(2, yhid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该用户编号已经存在无法再次新增");
		}

		// 密码加密
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" update fw.sys_user ");
		sqlBF.append("    set yhbh     = ?, ");
		sqlBF.append("        yhmc     = ?, ");
		sqlBF.append("        yhmcpy     = ?, ");
		sqlBF.append("        yhlx     = ?, ");
		sqlBF.append("        zjlx     = ?, ");
		sqlBF.append("        zjhm     = ?, ");
		sqlBF.append("        ssjbjgid = ?, ");
		sqlBF.append("        ssjgmc   = ?, ");
		sqlBF.append("        ssjgbm   = ?, ");
		sqlBF.append("        sjhm   = ?, ");
		sqlBF.append("        dzyx   = ? ");
		sqlBF.append("  where yhid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhbh);
		this.sql.setString(2, yhmc);
		this.sql.setString(3, yhmcpy);
		this.sql.setString(4, yhlx);
		this.sql.setString(5, zjlx);

		this.sql.setString(6, zjhm);
		this.sql.setString(7, ssjbjgid);
		this.sql.setString(8, ssjgmc);
		this.sql.setString(9, ssjgbm);
		this.sql.setString(10, sjhm);
		this.sql.setString(11, dzyx);
		this.sql.setString(12, yhid);
		this.sql.executeUpdate();

		// 数据权限配置
		if ("1".equals(sjqxpz) && !StringUtil.chkStrNull(ssjbjgid)) {
			// 原有配置全部删除
			this.sql.setSql(" delete from fw.user_data_right where yhid = ? ");
			this.sql.setString(1, yhid);
			this.sql.executeUpdate();

			// 新增
			this.sql.setSql(" insert into fw.user_data_right(jbjgid, yhid) values (?, ?) ");
			this.sql.setString(1, ssjbjgid);
			this.sql.setString(2, yhid);
			this.sql.executeUpdate();
		}

		// 记录日志
		this.log("SYS-A-YHJBXXXG", "修改用户基本信息", "A", yhid, "修改用户ID:" + yhid
				+ "的信息：用户编号:" + yhbh + ",用户名称:" + yhmc, "yhid=" + yhid
				+ ",yhbh=" + yhbh + ",yhmc=" + yhmc + ",yhlx:" + yhlx
				+ ",sjqxpz:" + sjqxpz);
		return null;
	}

	/**
	 * 操作日志查询
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-10
	 * @since V1.0
	 */
	public final DataMap querySysUserBizLog(final DataMap para) throws Exception {
		String yhid = para.getString("yhid");
		String qsrq = para.getString("qsrq");
		String zzrq = para.getString("zzrq");

		if (StringUtil.chkStrNull(yhid)) {
			throw new BizException("用户ID为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.czlx, ");
		sqlBF.append("        a.czmc, ");
		sqlBF.append("        a.czsm, ");
		sqlBF.append("        to_char(a.czsj, 'yyyymmddhh24miss') czsj, ");
		sqlBF.append("        a.czip, ");
		sqlBF.append("        b.yhbh czybh, ");
		sqlBF.append("        b.yhmc czymc ");
		sqlBF.append("   from fw.biz_log a, fw.sys_user b ");
		sqlBF.append("  where a.czyid = b.yhid(+) ");
		sqlBF.append("    and a.ztlx = 'A' ");
		sqlBF.append("    and a.ztid = ? ");
		if (!StringUtil.chkStrNull(qsrq)) {
			sqlBF.append("    and to_char(a.czsj, 'yyyymmdd') >= '" + qsrq
					+ "' ");
		}
		if (!StringUtil.chkStrNull(zzrq)) {
			sqlBF.append("    and to_char(a.czsj, 'yyyymmdd') <= '" + zzrq
					+ "' ");
		}
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, yhid);
		DataSet dsLog = this.sql.executeQuery();
		dsLog.sortdesc("czsj");

		DataMap rdm = new DataMap();
		rdm.put("dslog", dsLog);
		return rdm;
	}

}
