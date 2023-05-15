package com.grace.frame.urm.biz;

import java.util.HashMap;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.CodeUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TreeBuilder;

/**
 * 角色权限tree,用于编辑使用
 * 
 * @author yjc
 */
public class SysRoleFuncTree extends TreeBuilder{
	private HashMap<String, String> roleLeafMap = new HashMap<String, String>();
	private HashMap<String, DataSet> funcMap = new HashMap<String, DataSet>();

	/**
	 * 创建tree
	 */
	public void entry(DataMap para) throws Exception {
		String jsid = para.getString("jsid");
		if (StringUtil.chkStrNull(jsid)) {
			throw new BizException("传入的角色Id为空");
		}
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select gnid ");
		sqlBF.append("   from fw.func a ");
		sqlBF.append("  where a.dbid = ? ");
		sqlBF.append("    and not exists (select 'x' ");
		sqlBF.append("           from fw.func b ");
		sqlBF.append("          where a.dbid = b.dbid ");
		sqlBF.append("            and a.gnid = b.fgn) ");
		sqlBF.append("    and exists (select 'x' ");
		sqlBF.append("           from fw.role_func c ");
		sqlBF.append("          where a.gnid = c.gnid ");
		sqlBF.append("            and c.jsid = ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, jsid);
		DataSet dsLeafGn = this.sql.executeQuery();

		for (int i = 0, n = dsLeafGn.size(); i < n; i++) {
			this.roleLeafMap.put(dsLeafGn.getString(i, "gnid"), null);
		}

		dsLeafGn.clear();// 节省资源
		dsLeafGn = null;

		// 进行数据缓存，防止递归中的数据库频繁的连接
		if ("A".equals(this.getSysUser().getYhlx())) {
			sqlBF.setLength(0);
			sqlBF.append(" select gnid, gnmc, sxh, gnlx, fgn ");
			sqlBF.append("   from fw.func ");
			sqlBF.append("  where dbid = ? ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
		} else {
			sqlBF.setLength(0);
			sqlBF.append(" select a.gnid, a.gnmc, a.sxh, a.gnlx, a.fgn ");
			sqlBF.append("   from fw.func a ");
			sqlBF.append("  where a.dbid = ? ");
			sqlBF.append("    and (exists (select 'x' ");
			sqlBF.append("           from fw.user_func b ");
			sqlBF.append("          where a.gnid = b.gnid ");
			sqlBF.append("            and b.yhid = ?) ");
			sqlBF.append(" or exists(select 'x' ");
			sqlBF.append("   from fw.role_func e, fw.user_role f ");
			sqlBF.append("  where e.jsid = f.jsid ");
			sqlBF.append("    and f.yhid = ? ");
			sqlBF.append("    and a.gnid = e.gnid)) ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, this.getYhid());
			this.sql.setString(3, this.getYhid());
		}
		DataSet dsFunc = this.sql.executeQuery();
		for (int i = 0, n = dsFunc.size(); i < n; i++) {
			DataSet dsOneFunc;
			String fgn = dsFunc.getString(i, "fgn");
			if (this.funcMap.containsKey(fgn)) {
				dsOneFunc = this.funcMap.get(fgn);
				dsOneFunc.addRow(dsFunc.getRow(i));
			} else {
				dsOneFunc = new DataSet();
				dsOneFunc.addRow(dsFunc.getRow(i));
			}
			this.funcMap.put(fgn, dsOneFunc);
		}
		dsFunc.clear();// 节省资源
		dsFunc = null;

		// 查询业务领域-全用户权限管理(超级管理员-普通管理员)
		if ("A".equals(this.getSysUser().getYhlx())) {
			// 超级管理员
			sqlBF.setLength(0);
			sqlBF.append(" select gnid, gnmc, sxh, gnlx ");
			sqlBF.append("   from fw.func ");
			sqlBF.append("  where gnlx = 'A' ");
			sqlBF.append("    and dbid = ? ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
		} else {
			// 普通用户
			sqlBF.setLength(0);
			sqlBF.append(" select a.gnid, a.gnmc, a.sxh, a.gnlx ");
			sqlBF.append("   from fw.func a ");
			sqlBF.append("  where a.gnlx = 'A' ");
			sqlBF.append("    and a.dbid = ? ");
			sqlBF.append("    and (exists (select 'x' ");
			sqlBF.append("           from fw.user_func b ");
			sqlBF.append("          where a.gnid = b.gnid ");
			sqlBF.append("            and b.yhid = ?) ");
			sqlBF.append(" or exists(select 'x' ");
			sqlBF.append("   from fw.role_func e, fw.user_role f ");
			sqlBF.append("  where e.jsid = f.jsid ");
			sqlBF.append("    and f.yhid = ? ");
			sqlBF.append("    and a.gnid = e.gnid)) ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, this.getYhid());
			this.sql.setString(3, this.getYhid());
		}
		DataSet dsYwlx = this.sql.executeQuery();
		dsYwlx.sort("gnid").sort("sxh");

		for (int i = 0, n = dsYwlx.size(); i < n; i++) {
			String gnid = dsYwlx.getString(i, "gnid");
			String gnmc = dsYwlx.getString(i, "gnmc");
			String gnlx = dsYwlx.getString(i, "gnlx");
			String text = gnmc + "<span style=\"color:blue;\">&nbsp;（"
					+ CodeUtil.discode("GNLX", gnlx, gnlx) + "）</span>";

			DataMap pdm = new DataMap();
			pdm.put("fgn", gnid);
			this.addNode("createSubNode", pdm, gnid, text, true, null, this.roleLeafMap.containsKey(gnid), null);
		}
	}

	/**
	 * 创建子节点
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public void createSubNode(DataMap para) throws Exception {
		String fgn = para.getString("fgn");
		DataSet dsYwlx = this.funcMap.get(fgn);
		if (null == dsYwlx) {
			return;
		}
		dsYwlx.sort("gnid").sort("sxh");
		for (int i = 0, n = dsYwlx.size(); i < n; i++) {
			String gnid = dsYwlx.getString(i, "gnid");
			String gnmc = dsYwlx.getString(i, "gnmc");
			String gnlx = dsYwlx.getString(i, "gnlx");
			String text = gnmc + "<span style=\"color:blue;\">&nbsp;（"
					+ CodeUtil.discode("GNLX", gnlx, gnlx) + "）</span>";
			DataMap pdm = new DataMap();
			pdm.put("fgn", gnid);
			this.addNode("createSubNode", pdm, gnid, text, false, null, this.roleLeafMap.containsKey(gnid), null);
		}
	}
}
