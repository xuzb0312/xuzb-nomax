package com.grace.frame.urm.biz;

import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 一切查询统计相关的操作
 * 
 * @author yjc
 */
public class UrmQueryBiz extends Biz{

	/**
	 * 查询用户信息-进入页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-22
	 * @since V1.0
	 */
	public final DataMap fwdQuerySysUser(final DataMap para) throws Exception {
		// 返回数据
		DataMap rdm = new DataMap();
		rdm.put("dsjbjg", this.getJbjgDs());

		return rdm;
	}

	/**
	 * 获取经办机构DS
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	private DataSet getJbjgDs() throws Exception {
		// 获取dsJbjg-所有
		this.sql.setSql(" select jbjgid code, jbjgmc content from fw.sys_agency ");
		DataSet dsJbjg = this.sql.executeQuery();
		return dsJbjg;
	}

	/**
	 * 查询用户信息-查询
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-22
	 * @since V1.0
	 */
	public final DataMap querySysUserInfo(final DataMap para) throws Exception {
		String ssjbjgid = para.getString("ssjbjgid");
		String yhzt = para.getString("yhzt");
		String yhlx = para.getString("yhlx");

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select yhid, yhbh, yhlx, yhmc, zjlx, ");
		sqlBF.append("        zjhm, ssjbjgid, ssjgmc, ssjgbm, yhzt, ");
		sqlBF.append("        zxrq, zxjbsj, zxyy ");
		sqlBF.append("   from fw.sys_user ");
		sqlBF.append("  where 1 = 1 ");
		if (!StringUtil.chkStrNull(yhzt)) {
			sqlBF.append("    and nvl(yhzt, '1') = '" + yhzt + "' ");
		}
		if (!StringUtil.chkStrNull(ssjbjgid)) {
			sqlBF.append("    and "
					+ StringUtil.replaceC2QCQ("ssjbjgid", ssjbjgid));
		}
		if (!StringUtil.chkStrNull(yhlx)) {
			sqlBF.append("    and yhlx = '" + yhlx + "' ");
		}
		this.sql.setSql(sqlBF.toString());
		DataSet dsUser = this.sql.executeQuery();

		DataMap dm = new DataMap();
		dm.put("dsuser", dsUser);
		return dm;
	}

}
