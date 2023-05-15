package com.grace.frame.login;

import java.util.ArrayList;
import java.util.HashMap;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.TreeBuilder;

/**
 * 子系统的业务菜单树
 * 
 * @author yjc
 */
public class SubSysMenuTree extends TreeBuilder{

	/**
	 * 创建系统的业务菜单树
	 */
	@SuppressWarnings("unchecked")
	public void entry(DataMap para) throws Exception {
		String fgn = para.getString("fgn");
		if (StringUtil.chkStrNull(fgn)) {
			throw new AppException("传入的父功能ID为空");
		}

		// 判断session中是否已经存在，存在则不进行重新构建了
		if (!GlobalVars.DEBUG_MODE
				&& this.getSysUser()
					.getAllInfoDM()
					.containsKey("subsysmenutree_data_" + fgn)) {
			this.setTreedataList((ArrayList<HashMap<String, Object>>) this.getSysUser()
				.getAllInfoDM()
				.get("subsysmenutree_data_" + fgn));
			return;
		}

		// 对调试工具菜单，单独组装菜单
		if (GlobalVars.DEBUG_MODE && fgn.startsWith("debug")) {
			this.createDebugMenuTree(fgn);
			return;
		}

		// 参数
		DataMap pdm = new DataMap();
		pdm.put("fgn", fgn);
		if ("A".equals(this.getSysUser().getYhlx())) {// 超级管理员
			this.createSubSysMenuTree4AUser(pdm);
		} else if ("B".equals(this.getSysUser().getYhlx())) {// 普通用户
			this.createSubSysMenuTree4BUser(pdm);
		} else {
			throw new BizException("该用户无法获取功能菜单列表");
		}

		// 对该子功能tree的data进行缓存，后续则不再进行组装。
		if (!GlobalVars.DEBUG_MODE) {
			this.getSysUser()
				.getAllInfoDM()
				.put("subsysmenutree_data_" + fgn, this.getTreedataList());
		}
	}

	/**
	 * 超级管理员进入系统功能进行业务菜单的创建逻辑。--无需判断人员的权限信息。--逻辑修正
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public void createSubSysMenuTree4AUser(DataMap para) throws Exception {
		String fgn = para.getString("fgn");
		if (StringUtil.chkStrNull(fgn)) {
			throw new AppException("传入的父功能ID为空");
		}

		// 获取到子节点
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select gnid, gnmc, gnsj, sxh, gntb, ywlb ");
		sqlBF.append("   from fw.func ");
		sqlBF.append("  where gnlx = 'C' ");
		sqlBF.append("    and dbid = ? ");
		sqlBF.append("    and fgn = ? ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, fgn);
		DataSet ds = this.sql.executeQuery();
		ds.sort("sxh");

		// 循环处理
		for (int i = 0, n = ds.size(); i < n; i++) {
			String gnid = ds.getString(i, "gnid");
			String gnmc = ds.getString(i, "gnmc");
			String gnsj = ds.getString(i, "gnsj");
			String gntb = ds.getString(i, "gntb");
			String ywlb = ds.getString(i, "ywlb");

			// 创建节点
			this.createOneNodeFunc4AUser(gnid, gnmc, gnsj, gntb, ywlb);

		}
	}

	/**
	 * 创建一个节点。--超级管理员使用的。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	private void createOneNodeFunc4AUser(String gnid, String gnmc, String gnsj,
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
			pdm.clear();
			pdm.put("fgn", gnid);
			this.addNode("createSubSysMenuTree4AUser", pdm, gnid, gnmc, false, gntb, this.createAttributeDm(gnsj, "", ""));
		} else {
			// 以下是均配置了业务类别的。
			String ywjbjgqxfw = GlobalVars.AGENCY_BIZ_TYPE_MAP.get(ywlb);
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
				pdm.clear();
				pdm.put("fgn", gnid);
				this.addNode("createSubSysMenuTree4AUser", pdm, gnid, gnmc, false, gntb, this.createAttributeDm(gnsj, jbjgid, jbjgqxfw));
			} else {// 超过1个的
				pdm.clear();
				pdm.put("gnid", gnid);
				pdm.put("nodemap", jbjgNodeMap);
				pdm.put("gnsj", gnsj);
				this.addNode("createSubSysMenuTree4AUserUnionNode", pdm, gnid, gnmc, false, gntb, this.createAttributeDm("", "", ""));
			}
		}
	}

	/**
	 * 超级管理员的合并节点处理操作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public void createSubSysMenuTree4AUserUnionNode(DataMap para) throws Exception {
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
		// 创建子节点
		DataMap pdm = new DataMap();
		pdm.put("fgn", gnid);
		this.createSubSysMenuTree4AUser(pdm);
	}

	/**
	 * 普通用户进入系统功能进行业务菜单的创建逻辑。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public void createSubSysMenuTree4BUser(DataMap para) throws Exception {
		String fgn = para.getString("fgn");
		if (StringUtil.chkStrNull(fgn)) {
			throw new AppException("传入的父功能ID为空");
		}

		// 获取到子节点
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select gnid, gnmc, gnsj, sxh, gntb, ywlb ");
		sqlBF.append("   from fw.func a ");
		sqlBF.append("  where gnlx = 'C' ");
		sqlBF.append("    and dbid = ? ");
		sqlBF.append("    and fgn = ? ");
		sqlBF.append("    and (exists (select 'x' ");
		sqlBF.append("                   from fw.user_func d ");
		sqlBF.append("                  where a.gnid = d.gnid ");
		sqlBF.append("                    and d.yhid = ?) or exists ");
		sqlBF.append("         (select 'x' ");
		sqlBF.append("            from fw.user_role e, fw.role_func f ");
		sqlBF.append("           where e.jsid = f.jsid ");
		sqlBF.append("             and f.gnid = a.gnid ");
		sqlBF.append("             and e.yhid = ?)) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, fgn);
		this.sql.setString(3, this.getSysUser().getYhid());
		this.sql.setString(4, this.getSysUser().getYhid());
		DataSet ds = this.sql.executeQuery();
		ds.sort("sxh");

		// 循环处理
		for (int i = 0, n = ds.size(); i < n; i++) {
			String gnid = ds.getString(i, "gnid");
			String gnmc = ds.getString(i, "gnmc");
			String gnsj = ds.getString(i, "gnsj");
			String gntb = ds.getString(i, "gntb");
			String ywlb = ds.getString(i, "ywlb");

			// 创建节点
			this.createOneNodeFunc4BUser(gnid, gnmc, gnsj, gntb, ywlb);
		}
	}

	/**
	 * 创建一个节点。--普通管理员使用的。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	private void createOneNodeFunc4BUser(String gnid, String gnmc, String gnsj,
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
			pdm.clear();
			pdm.put("fgn", gnid);
			this.addNode("createSubSysMenuTree4BUser", pdm, gnid, gnmc, false, gntb, this.createAttributeDm(gnsj, "", ""));
		} else {
			// 以下是均配置了业务类别的。
			String ywjbjgqxfw = GlobalVars.AGENCY_BIZ_TYPE_MAP.get(ywlb);
			String yhqxfw = this.getSysUser()
				.getAllInfoDM()
				.getString("yhjbjgqxfw");// 用户权限范围
			ywjbjgqxfw = StringUtil.mixed2Str(ywjbjgqxfw, yhqxfw);
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
				pdm.clear();
				pdm.put("fgn", gnid);
				this.addNode("createSubSysMenuTree4BUser", pdm, gnid, gnmc, false, gntb, this.createAttributeDm(gnsj, jbjgid, jbjgqxfw));
			} else {// 超过1个的
				pdm.clear();
				pdm.put("gnid", gnid);
				pdm.put("nodemap", jbjgNodeMap);
				pdm.put("gnsj", gnsj);
				this.addNode("createSubSysMenuTree4BUserUnionNode", pdm, gnid, gnmc, false, gntb, this.createAttributeDm("", "", ""));
			}
		}
	}

	/**
	 * 普通用户的合并节点处理操作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public void createSubSysMenuTree4BUserUnionNode(DataMap para) throws Exception {
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
		// 创建子节点
		DataMap pdm = new DataMap();
		pdm.put("fgn", gnid);
		this.createSubSysMenuTree4BUser(pdm);
	}

	/**
	 * 调试树
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-30
	 * @since V1.0
	 */
	private void createDebugMenuTree(String fgn) throws Exception {
		if ("debug01".equals(fgn)) {// 详细配置
			// 本地化配置
			this.addNode(null, null, "debug0101", "本地化配置", false, null, this.createAttributeDm("url:debug.do?method=fwdLocalConfigMng", "", ""));
			// 轮询配置
			this.addNode(null, null, "debug0102", "轮询配置", false, null, this.createAttributeDm("url:debug.do?method=fwdPollingConfigMng", "", ""));
			// 服务配置
			this.addNode(null, null, "debug0103", "服务配置(提供方)", false, null, this.createAttributeDm("url:debug.do?method=fwdServiceConfigMng", "", ""));
			// 服务注册
			this.addNode(null, null, "debug0104", "服务注册(使用方)", false, null, this.createAttributeDm("url:debug.do?method=fwdServiceRegMng", "", ""));
			// CODE配置
			this.addNode(null, null, "debug0105", "CODE配置", false, null, this.createAttributeDm("url:debug.do?method=fwdCodeConfigMng", "", ""));
			// 典型批注配置
			this.addNode(null, null, "debug0117", "典型批注配置", false, null, this.createAttributeDm("url:debug.do?method=fwdNoteConfigMng", "", ""));
			// 功能权限配置
			this.addNode(null, null, "debug0106", "功能权限配置", false, null, this.createAttributeDm("url:debug.do?method=fwdFuncConfigMng", "", ""));
			// 特殊权限配置
			this.addNode(null, null, "debug0107", "特殊权限配置", false, null, this.createAttributeDm("url:debug.do?method=fwdSpRightConfigMng", "", ""));
			// 经办机构配置
			this.addNode(null, null, "debug0108", "经办机构配置", false, null, this.createAttributeDm("url:debug.do?method=fwdSysAgencyMng", "", ""));
			// 数据库信息
			this.addNode(null, null, "debug0109", "数据库(DBID)管理", false, null, this.createAttributeDm("url:debug.do?method=fwdDbidInfoMng", "", ""));
			// 系统业务类别分类
			this.addNode(null, null, "debug0110", "系统业务类别分类配置", false, null, this.createAttributeDm("url:debug.do?method=fwdAgencyBizTypeMng", "", ""));
			// 打印模板管理
			this.addNode(null, null, "debug0111", "打印模板管理", false, null, this.createAttributeDm("url:debug.do?method=fwdPrintConfigMng", "", ""));
			// 文件模板管理
			this.addNode(null, null, "debug0112", "文件模板管理", false, null, this.createAttributeDm("url:debug.do?method=fwdFileModelMng", "", ""));
			// 系统节假日管理
			this.addNode(null, null, "debug0113", "系统节假日管理", false, null, this.createAttributeDm("url:debug.do?method=fwdSysHolidayMng", "", ""));
			// 系统参数维护
			this.addNode(null, null, "debug0114", "系统参数维护", false, null, this.createAttributeDm("url:debug.do?method=fwdSysParaMng", "", ""));
			// 系统图标库
			this.addNode(null, null, "debug0115", "系统图标库", false, null, this.createAttributeDm("url:debug.do?method=fwdSysIconView", "", ""));
			// 图片模板管理
			this.addNode(null, null, "debug0116", "图片模板管理", false, null, this.createAttributeDm("url:debug.do?method=fwdImageModelMng", "", ""));

			// 系统信息
			this.addNode(null, null, "debug0198", "系统信息", false, null, this.createAttributeDm("url:debug.do?method=fwdSysInfoMng", "", ""));
			// 框架表统计信息
			this.addNode(null, null, "debug0199", "框架表统计信息", false, "icon-calculator", this.createAttributeDm("url:debug.do?method=fwdFrameTablesInfo", "", ""));
		} else if ("debug02".equals(fgn)) {// 引导配置（开始使用，新增DBID等）
			this.addNode(null, null, "debug0201", "开始使用", false, "icon-control-fastforward-blue", this.createAttributeDm("url:debug.do?method=fwdUseBegin", "", ""));
		} else if ("debug03".equals(fgn)) {// 开发工具
			this.addNode(null, null, "debug0301", "系统缓存重置", false, "icon-arrow-rotate-clockwise", this.createAttributeDm("url:debug.do?method=fwdResetSysCache", "", ""));
			this.addNode(null, null, "debug0302", "业务BIZ调试", false, "icon-bug-go", this.createAttributeDm("url:debug.do?method=fwdBizClassDebug", "", ""));
			this.addNode(null, null, "debug0303", "hibernate加密串生成", false, "icon-key-go", this.createAttributeDm("url:debug.do?method=fwdGenHibernateEncodeStr", "", ""));
			this.addNode(null, null, "debug0304", "自动化代码工具", false, "icon-control-repeat-blue", this.createAttributeDm("url:debug.do?method=fwdAutoCodingTools", "", ""));
			this.addNode(null, null, "debug0305", "证件照处理", false, "icon-picture-edit", this.createAttributeDm("url:debug.do?method=fwdIDPhotoCheckPage", "", ""));
			this.addNode(null, null, "debug0306", "Sql格式化工具", false, "icon-text-padding-left", this.createAttributeDm("url:debug.do?method=fwdSqlFormat", "", ""));
		} else {
			throw new BizException("调试模式暂不支持该菜单展示。");
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
