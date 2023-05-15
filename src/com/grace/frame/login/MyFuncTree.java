package com.grace.frame.login;

import java.util.HashMap;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TreeBuilder;

/**
 * 我的功能树
 * 
 * @author yjc
 */
public class MyFuncTree extends TreeBuilder{

	/**
	 * 创建系统的业务菜单树
	 */
	public void entry(DataMap para) throws Exception {
		// 查询我的功能
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.gnid, a.gnmc, a.gnsj, max(b.tjsj) tjsj, a.gntb, a.ywlb ");
		sqlBF.append("   from fw.func a, fw.my_func b ");
		sqlBF.append("  where a.gnlx = 'C' ");
		sqlBF.append("    and a.dbid = ? ");
		sqlBF.append("    and a.gnid = b.gnid ");
		sqlBF.append("    and b.yhid = ? ");
		if (!"A".equals(this.getSysUser().getYhlx())) {// 不是超级管理员时
			sqlBF.append("    and (exists (select 'x' ");
			sqlBF.append("                   from fw.user_func d ");
			sqlBF.append("                  where a.gnid = d.gnid ");
			sqlBF.append("                    and d.yhid = b.yhid) or exists ");
			sqlBF.append("         (select 'x' ");
			sqlBF.append("            from fw.user_role e, fw.role_func f ");
			sqlBF.append("           where e.jsid = f.jsid ");
			sqlBF.append("             and f.gnid = a.gnid ");
			sqlBF.append("             and e.yhid = b.yhid)) ");
		}
		sqlBF.append("  group by a.gnid, a.gnmc, a.gnsj, a.gntb, a.ywlb ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, this.getYhid());
		DataSet ds = this.sql.executeQuery();
		ds.sortdesc("tjsj");
		for (int i = 0, n = ds.size(); i < n; i++) {
			String gnid = ds.getString(i, "gnid");
			String gnmc = ds.getString(i, "gnmc");
			String gnsj = ds.getString(i, "gnsj");
			String gntb = ds.getString(i, "gntb");
			String ywlb = ds.getString(i, "ywlb");

			// 创建节点
			this.createOneNodeFunc(gnid, gnmc, gnsj, gntb, ywlb);
		}
	}

	/**
	 * 创建一个节点。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	private void createOneNodeFunc(String gnid, String gnmc, String gnsj,
			String gntb, String ywlb) throws Exception {
		DataMap pdm = new DataMap();
		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("传入的功能ID为空");
		}
		if (StringUtil.chkStrNull(gnmc)) {
			throw new BizException("传入的功能名称为空");
		}
		if (StringUtil.chkStrNull(ywlb) || StringUtil.chkStrNull(gnsj)) {
			// 如果未配置业务类别，则不进行数据权限的控制
			this.addNode(null, null, gnid, gnmc, false, gntb, this.createAttributeDm(gnsj, "", ""));
		} else {
			// 以下是均配置了业务类别的。
			String ywjbjgqxfw = GlobalVars.AGENCY_BIZ_TYPE_MAP.get(ywlb);
			if (!"A".equals(this.getSysUser().getYhlx())) {// 不是超级管理员时
				String yhqxfw = this.getSysUser()
					.getAllInfoDM()
					.getString("yhjbjgqxfw");// 用户权限范围
				ywjbjgqxfw = StringUtil.mixed2Str(ywjbjgqxfw, yhqxfw);
			}
			if (StringUtil.chkStrNull(ywjbjgqxfw)) {// 如果为空，则对与该节点不进行展示
				return;
			}

			String[] arr_Ywjbjgqxfw = ywjbjgqxfw.split(",");
			HashMap<String, String> jbjgNodeMap = new HashMap<String, String>();
			for (int i = 0, n = arr_Ywjbjgqxfw.length; i < n; i++) {
				jbjgNodeMap.put(arr_Ywjbjgqxfw[i], GlobalVars.AGENCY_MAP.get(arr_Ywjbjgqxfw[i])[2]);
			}

			// 查询func_union数据
			this.sql.setSql(" select gnmc, jbjgfw from fw.func_union where dbid = ? and gnid = ? ");
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, gnid);
			DataSet dsUnion = this.sql.executeQuery();
			for (int i = 0, n = dsUnion.size(); i < n; i++) {
				String union_gnmc = dsUnion.getString(i, "gnmc");
				String union_jbjgfw = dsUnion.getString(i, "jbjgfw");
				if ("*".equals(union_jbjgfw)) {// 如果有*的则不进行拆分了，直接合并
					jbjgNodeMap.clear();
					jbjgNodeMap.put(ywjbjgqxfw, gnmc);
					break;
				} else {
					union_jbjgfw = StringUtil.mixed2Str(union_jbjgfw, ywjbjgqxfw);
					if (StringUtil.chkStrNull(union_jbjgfw)) {
						return;
					}
					String[] arrUnion_jbjgfw = union_jbjgfw.split(",");
					for (int j = 0, m = arrUnion_jbjgfw.length; j < m; j++) {
						jbjgNodeMap.remove(arrUnion_jbjgfw[j]);
					}
					jbjgNodeMap.put(union_jbjgfw, union_gnmc);
				}
			}

			// 判断map的数量
			Object[] objKeysArr = jbjgNodeMap.keySet().toArray();
			if (objKeysArr.length <= 0) {
				return;
			} else if (objKeysArr.length == 1) {
				String jbjgqxfw = (String) objKeysArr[0];
				String jbjgid = this.getDefalutJbjgid(jbjgqxfw);
				this.addNode(null, null, gnid, gnmc, false, gntb, this.createAttributeDm(gnsj, jbjgid, jbjgqxfw));
			} else {// 超过1个的
				pdm.clear();
				pdm.put("gnid", gnid);
				pdm.put("nodemap", jbjgNodeMap);
				pdm.put("gnsj", gnsj);
				this.addNode("createMulitiUnionNode", pdm, gnid, gnmc, false, gntb, this.createAttributeDm("", "", ""));
			}
		}
	}

	/**
	 * 节点合并处理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public void createMulitiUnionNode(DataMap para) throws Exception {
		String gnid = para.getString("gnid");
		HashMap<String, String> jbjgNodeMap = (HashMap<String, String>) para.get("nodemap");
		String gnsj = para.getString("gnsj");
		// 增加union的节点
		Object[] objKeysArr = jbjgNodeMap.keySet().toArray();
		// 排序
		DataSet dsUnionNode = new DataSet();
		for (int i = 0, n = objKeysArr.length; i < n; i++) {
			String jbjgqxfw = (String) objKeysArr[i];
			dsUnionNode.addRow();
			dsUnionNode.put(dsUnionNode.size() - 1, "jbjgqxfw", jbjgqxfw);
		}
		dsUnionNode.sort("jbjgqxfw");
		for (int i = 0, n = dsUnionNode.size(); i < n; i++) {
			String jbjgqxfw = dsUnionNode.getString(i, "jbjgqxfw");
			String jbjgid = this.getDefalutJbjgid(jbjgqxfw);
			this.addNode(null, null, gnid + "-union-" + i, jbjgNodeMap.get(jbjgqxfw), false, null, this.createAttributeDm(gnsj, jbjgid, jbjgqxfw));
		}
	}

	/**
	 * 创建属性dm
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	private DataMap createAttributeDm(String gnsj, String jbjgid,
			String jbjgqxfw) throws Exception {
		DataMap attrDm = new DataMap();
		attrDm.put("gnsj", gnsj);
		attrDm.put("jbjgid", jbjgid);
		attrDm.put("jbjgqxfw", jbjgqxfw);
		return attrDm;
	}

	/**
	 * 根据jbjgfw获取默认的经办机构id
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-30
	 * @since V1.0
	 */
	private String getDefalutJbjgid(String jbjgfw) throws AppException {
		if (StringUtil.chkStrNull(jbjgfw)) {
			return "";// 如果权限范围为空，则返回默认的经办机构ID也为空。
		}

		// 用户的配置所属经办机构
		String yhssjbjgid = this.getSysUser()
			.getAllInfoDM()
			.getString("ssjbjgid");
		String[] jbjgfwarr = jbjgfw.split(",");

		if (StringUtil.chkStrNull(yhssjbjgid)) {
			return jbjgfwarr[0];// 返回第一条
		}

		for (int i = 0, n = jbjgfwarr.length; i < n; i++) {
			if (yhssjbjgid.equals(jbjgfwarr[i])) {
				return yhssjbjgid;
			}
		}
		return jbjgfwarr[0];
	}
}
