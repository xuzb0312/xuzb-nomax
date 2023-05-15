package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.CodeUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

public class FuncConfigMngBiz extends Biz{

	/**
	 * 选择业务领域
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdChooseYwlxFunc(final DataMap para) throws Exception {
		String ywlyid = para.getString("ywlyid", "");
		ywlyid = "%" + ywlyid + "%";
		this.sql.setSql("select gnid ywlyid, gnmc ywlymc, gntb, bz from fw.func_doc a where a.gnlx = 'A' and (gnid like ? or gnmc like ?) ");
		this.sql.setString(1, ywlyid);
		this.sql.setString(2, ywlyid);
		DataSet ds = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsfunc", ds);
		return rdm;
	}

	/**
	 * 判断功能id是否已经在系统中存在
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	private void chkGnidIsExists(String gnid) throws Exception {
		if ("root".equalsIgnoreCase(gnid)) {
			throw new BizException("root为功能权限保留字，默认根节点，不允许新增。");
		}
		// 检测
		this.sql.setSql(" select gnid from fw.func_doc where gnid = ? ");
		this.sql.setString(1, gnid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该功能ID" + gnid + "已经在系统中存在不允许重复新增");
		}
	}

	/**
	 * 业务领域新增
	 * 
	 * @author yjc
	 * @date 创建时间 2017-2-8
	 * @since V1.0
	 */
	public final DataMap fwdYwlxFuncAdd(final DataMap para) throws Exception {
		DataMap rdm = new DataMap();

		// 业务类别
		this.sql.setSql(" select ywlb code, ywlb content from fw.agency_biz_type where dbid = ? group by ywlb ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		rdm.put("dsywlb", this.sql.executeQuery());

		return rdm;
	}

	/**
	 * 业务领域新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public final DataMap saveYwlxFuncAdd(final DataMap para) throws Exception {
		String ywlyid = para.getString("ywlyid");
		String ywlymc = para.getString("ywlymc");
		String gntb = para.getString("gntb");
		String bz = para.getString("bz");
		String sfpzbdgn = para.getString("sfpzbdgn");
		int sxh = para.getInt("sxh");
		String ywlb = para.getString("ywlb");
		StringBuffer sqlBF = new StringBuffer();

		if (StringUtil.chkStrNull(ywlyid)) {
			throw new BizException("传入的业务领域ID为空");
		}
		if (StringUtil.chkStrNull(ywlymc)) {
			throw new BizException("传入的业务领域名称为空");
		}

		// 检测
		this.chkGnidIsExists(ywlyid);

		// 插入执行
		sqlBF.setLength(0);
		sqlBF.append(" insert into fw.func_doc ");
		sqlBF.append("   (gnid, gnmc, fgn, gnsj, gnlx, ");
		sqlBF.append("    gntb, bz) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, 'root', null, 'A', ");
		sqlBF.append("    ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, ywlyid);
		this.sql.setString(2, ywlymc);
		this.sql.setString(3, gntb);
		this.sql.setString(4, bz);
		this.sql.executeUpdate();

		// config的配置
		if ("1".equals(sfpzbdgn)) {
			sqlBF.setLength(0);
			sqlBF.append(" insert into fw.func ");
			sqlBF.append("   (dbid, gnid, gnmc, fgn, gnsj,  ");
			sqlBF.append("    gnlx, sxh, gntb, bz, ywlb) ");
			sqlBF.append("   select ?, gnid, gnmc, fgn, gnsj,  ");
			sqlBF.append("          gnlx, ?, gntb, bz, ? ");
			sqlBF.append("     from fw.func_doc ");
			sqlBF.append("    where gnid = ? ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setInt(2, sxh);
			this.sql.setString(3, ywlb);
			this.sql.setString(4, ywlyid);
			this.sql.executeUpdate();
		}

		return null;
	}

	/**
	 * 进入一个功能权限的doc操作页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public final DataMap fwdOneFuncDocMng(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		DataMap rdm = new DataMap();

		String gnid = para.getString("gnid");
		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("传入的功能ID为空");
		}

		// 获取基本信息
		sqlBF.setLength(0);
		sqlBF.append(" select gnid, gnmc, fgn, gnsj, gnlx,  ");
		sqlBF.append("        gntb, bz ");
		sqlBF.append("   from fw.func_doc ");
		sqlBF.append("  where gnid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gnid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("该功能节点在系统中不存在");
		}
		rdm.put("funcinfo", dsTemp.getRow(0));

		// 配置信息
		sqlBF.setLength(0);
		sqlBF.append(" select a.dbid, b.dbmc, a.sxh, a.ywlb ");
		sqlBF.append("   from fw.func a, fw.dbid_info b ");
		sqlBF.append("  where a.dbid = b.dbid ");
		sqlBF.append("    and a.gnid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gnid);
		dsTemp = this.sql.executeQuery();
		rdm.put("configinfo", dsTemp);

		return rdm;
	}

	/**
	 * 子功能新增页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public final DataMap fwdChildrenNodeAdd(final DataMap para) throws Exception {
		String fgn = para.getString("fgn");
		DataMap rdm = new DataMap();

		if (StringUtil.chkStrNull(fgn)) {
			throw new BizException("传入的父功能ID为空");
		}

		// 获取基本信息
		this.sql.setSql(" select gnid fgn, gnmc fgnmc, gnlx fgnlx from fw.func_doc where gnid = ? ");
		this.sql.setString(1, fgn);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("该功能节点在系统中不存在");
		}
		rdm.put("fgninfo", dsTemp.getRow(0));
		String fgnlx = dsTemp.getString(0, "fgnlx");

		// 计算默认新增子功能的功能ID
		String gnid = "";
		if (!"A".equals(fgnlx)) {
			// 获取父功能最大
			this.sql.setSql(" select nvl(max(gnid), '') maxgnid from fw.func_doc where fgn = ? ");
			this.sql.setString(1, fgn);
			dsTemp = this.sql.executeQuery();
			String maxgnid = dsTemp.getString(0, "maxgnid");
			if (StringUtil.chkStrNull(maxgnid)) {
				gnid = fgn + "01";
			} else {
				String allWords = "0123456789abcdefghijkmnopqrstuvwxyz";
				String endMax = maxgnid.substring(maxgnid.length() - 2);
				if ("zz".equals(endMax)) {
					gnid = "";
				} else {
					char max0 = endMax.charAt(0);
					char max1 = endMax.charAt(1);
					int index0 = allWords.indexOf(max0);
					int index1 = allWords.indexOf(max1);
					int newindex0 = index0;
					int newindex1 = index1 + 1;
					if (newindex1 > 34) {
						newindex0 = newindex0 + 1;
					}
					char[] arrchar = { allWords.charAt(newindex0), allWords.charAt(newindex1) };
					gnid = fgn + new String(arrchar);
				}
			}

		}
		rdm.put("defalutgnid", gnid);

		// 功能类型的ds
		if ("A".equals(fgnlx)) {
			rdm.put("dsgnlx", CodeUtil.getDsByDmbh("GNLX", "B", ""));
		} else if ("B".equals(fgnlx)) {
			rdm.put("dsgnlx", CodeUtil.getDsByDmbh("GNLX", "C", ""));
		} else if ("C".equals(fgnlx)) {
			DataSet dsGnlx = new DataSet();
			dsGnlx.addRow();
			dsGnlx.put(dsGnlx.size() - 1, "code", "C");
			dsGnlx.put(dsGnlx.size() - 1, "content", "功能菜单");
			dsGnlx.addRow();
			dsGnlx.put(dsGnlx.size() - 1, "code", "D");
			dsGnlx.put(dsGnlx.size() - 1, "content", "功能按钮");
			rdm.put("dsgnlx", dsGnlx);
		} else if ("D".equals(fgnlx)) {
			rdm.put("dsgnlx", CodeUtil.getDsByDmbh("GNLX", "D", ""));
		}

		// 业务类别
		this.sql.setSql(" select ywlb code, ywlb content from fw.agency_biz_type where dbid = ? group by ywlb ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		rdm.put("dsywlb", this.sql.executeQuery());

		return rdm;
	}

	/**
	 * 保存子节点
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public final DataMap saveChildrenNodeAdd(final DataMap para) throws Exception {
		String fgn = para.getString("fgn");
		String gnid = para.getString("gnid");
		String gnmc = para.getString("gnmc");
		String gnlx = para.getString("gnlx");
		String gntb = para.getString("gntb");
		String gnsj = para.getString("gnsj");
		String bz = para.getString("bz");
		String sfpzbdgn = para.getString("sfpzbdgn");
		int sxh = para.getInt("sxh");
		String ywlb = para.getString("ywlb");
		String qyhb = para.getString("qyhb");

		if (StringUtil.chkStrNull(fgn)) {
			throw new BizException("传入的父功能为空");
		}
		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("传入的功能ID为空");
		}
		if (StringUtil.chkStrNull(gnmc)) {
			throw new BizException("传入的功能名称为空");
		}
		if (StringUtil.chkStrNull(gnlx)) {
			throw new BizException("传入的功能类型为空");
		}

		// 检查该功能是否已经存在
		this.sql.setSql(" select gnid from fw.func_doc where gnid = ? ");
		this.sql.setString(1, gnid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该功能ID在fw.func_doc中已经存在，不允许重复新增。");
		}
		this.sql.setSql(" select gnid from fw.func where gnid = ? and dbid = ? ");
		this.sql.setString(1, gnid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该功能ID在fw.func中已经存在，不允许重复新增,请核实。");
		}

		// 新增doc
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.func_doc ");
		sqlBF.append("   (gnid, gnmc, fgn, gnsj, gnlx, ");
		sqlBF.append("    gntb, bz) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gnid);
		this.sql.setString(2, gnmc);
		this.sql.setString(3, fgn);
		this.sql.setString(4, gnsj);
		this.sql.setString(5, gnlx);

		this.sql.setString(6, gntb);
		this.sql.setString(7, bz);
		this.sql.executeUpdate();

		// config的配置
		if ("1".equals(sfpzbdgn)) {
			sqlBF.setLength(0);
			sqlBF.append(" insert into fw.func ");
			sqlBF.append("   (dbid, gnid, gnmc, fgn, gnsj,  ");
			sqlBF.append("    gnlx, sxh, gntb, bz, ywlb) ");
			sqlBF.append("   select ?, gnid, gnmc, fgn, gnsj,  ");
			sqlBF.append("          gnlx, ?, gntb, bz, ? ");
			sqlBF.append("     from fw.func_doc ");
			sqlBF.append("    where gnid = ? ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setInt(2, sxh);
			this.sql.setString(3, ywlb);
			this.sql.setString(4, gnid);
			this.sql.executeUpdate();

			// 区域合并的处理
			if ("1".equals(qyhb) && "C".equals(gnlx)) {
				this.sql.setSql(" insert into fw.func_union(dbid, gnid, gnmc, jbjgfw) values(?, ?, ?, ?) ");
				this.sql.setString(1, GlobalVars.SYS_DBID);
				this.sql.setString(2, gnid);
				this.sql.setString(3, gnmc);
				this.sql.setString(4, "*");
				this.sql.executeUpdate();
			}
		}
		return null;
	}

	/**
	 * 查看func_union的配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public final DataMap fwdFuncUnionView(final DataMap para) throws Exception {
		String dbid = para.getString("dbid");
		String gnid = para.getString("gnid");
		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("传入的功能ID为空");
		}
		if (StringUtil.chkStrNull(dbid)) {
			throw new BizException("传入的DBID为空");
		}
		this.sql.setSql(" select gnmc, jbjgfw from fw.func_union where gnid = ? and dbid = ? ");
		this.sql.setString(1, gnid);
		this.sql.setString(2, dbid);
		DataSet dsUnion = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsunion", dsUnion);
		return rdm;
	}

	/**
	 * 节点修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public final DataMap fwdChildrenNodeModify(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		DataMap rdm = new DataMap();

		String gnid = para.getString("gnid");
		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("传入的功能ID为空");
		}

		// 获取基本信息
		sqlBF.setLength(0);
		sqlBF.append(" select gnid, gnmc, fgn, gnsj, gnlx,  ");
		sqlBF.append("        gntb, bz ");
		sqlBF.append("   from fw.func_doc ");
		sqlBF.append("  where gnid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gnid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("该功能节点在系统中不存在");
		}
		rdm.put("docinfo", dsTemp.getRow(0));

		// 获取父节点的功能类型
		String fgnlx = "";
		this.sql.setSql(" select gnlx from fw.func_doc where gnid = ? ");
		this.sql.setString(1, dsTemp.getString(0, "fgn"));
		dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			fgnlx = dsTemp.getString(0, "gnlx");
		}

		// 配置信息
		DataMap dmConfig = new DataMap();
		this.sql.setSql(" select sxh, ywlb from fw.func where gnid = ? and dbid = ? ");
		this.sql.setString(1, gnid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			dmConfig.put("sfpzbdgn", "1");
			dmConfig.put("sxh", dsTemp.getInt(0, "sxh"));
			dmConfig.put("ywlb", dsTemp.getString(0, "ywlb"));
			this.sql.setSql(" select gnid from fw.func_union where gnid = ? and dbid = ? ");
			this.sql.setString(1, gnid);
			this.sql.setString(2, GlobalVars.SYS_DBID);
			dsTemp = this.sql.executeQuery();
			if (dsTemp.size() > 0) {
				dmConfig.put("qyhb", "1");
			} else {
				dmConfig.put("qyhb", "0");
			}
		} else {
			dmConfig.put("sfpzbdgn", "");
			dmConfig.put("sxh", 0);
			dmConfig.put("ywlb", "");
			dmConfig.put("qyhb", "");
		}

		rdm.put("configinfo", dmConfig);

		// 功能类型的ds
		if ("".equals(fgnlx)) {
			rdm.put("dsgnlx", CodeUtil.getDsByDmbh("GNLX", "A", ""));
		} else if ("A".equals(fgnlx)) {
			rdm.put("dsgnlx", CodeUtil.getDsByDmbh("GNLX", "B", ""));
		} else if ("B".equals(fgnlx)) {
			rdm.put("dsgnlx", CodeUtil.getDsByDmbh("GNLX", "C", ""));
		} else if ("C".equals(fgnlx)) {
			DataSet dsGnlx = new DataSet();
			dsGnlx.addRow();
			dsGnlx.put(dsGnlx.size() - 1, "code", "C");
			dsGnlx.put(dsGnlx.size() - 1, "content", "功能菜单");
			dsGnlx.addRow();
			dsGnlx.put(dsGnlx.size() - 1, "code", "D");
			dsGnlx.put(dsGnlx.size() - 1, "content", "功能按钮");
			rdm.put("dsgnlx", dsGnlx);
		} else if ("D".equals(fgnlx)) {
			rdm.put("dsgnlx", CodeUtil.getDsByDmbh("GNLX", "D", ""));
		}

		// 业务类别
		this.sql.setSql(" select ywlb code, ywlb content from fw.agency_biz_type where dbid = ? group by ywlb ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		rdm.put("dsywlb", this.sql.executeQuery());

		return rdm;
	}

	/**
	 * 保存子节点修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public final DataMap saveChildrenNodeModify(final DataMap para) throws Exception {
		String gnid = para.getString("gnid");
		String gnmc = para.getString("gnmc");
		String gnlx = para.getString("gnlx");

		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("传入的功能ID为空");
		}
		if (StringUtil.chkStrNull(gnmc)) {
			throw new BizException("传入的功能名称为空");
		}
		if (StringUtil.chkStrNull(gnlx)) {
			throw new BizException("传入的功能类型为空");
		}

		// 获取父功能节点
		this.sql.setSql(" select fgn from fw.func_doc where gnid = ? ");
		this.sql.setString(1, gnid);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("该功能ID在fw.func_doc中不存在，无法修改。");
		}
		String fgn = dsTemp.getString(0, "fgn");

		// 为不影响其他地区的配置信息，只对本地区的配置信息进行调整
		// 清空本地数据
		this.sql.setSql(" delete from fw.func_doc where gnid = ? ");
		this.sql.setString(1, gnid);
		this.sql.executeUpdate();

		this.sql.setSql(" delete from fw.func where gnid = ? and dbid = ? ");
		this.sql.setString(1, gnid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		this.sql.setSql(" delete from fw.func_union where gnid = ? and dbid = ? ");
		this.sql.setString(1, gnid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 本地新增
		para.put("fgn", fgn);
		this.saveChildrenNodeAdd(para);
		return null;
	}

	/**
	 * 删除节点
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public final DataMap saveChildrenNodeDelete(final DataMap para) throws Exception {
		String gnid = para.getString("gnid");

		if (StringUtil.chkStrNull(gnid)) {
			throw new BizException("传入的功能ID为空");
		}
		this.deleteFuncNode(gnid);
		return null;
	}

	/**
	 * 删除递归方法
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	private void deleteFuncNode(String gnid) throws AppException {
		this.sql.setSql(" select gnid from fw.func_doc where fgn = ? ");
		this.sql.setString(1, gnid);
		DataSet ds = this.sql.executeQuery();
		for (int i = 0, n = ds.size(); i < n; i++) {
			String gnidT = ds.getString(i, "gnid");
			this.deleteFuncNode(gnidT);
		}

		// 删除该节点信息
		this.sql.setSql(" delete from fw.func_doc where gnid = ? ");
		this.sql.setString(1, gnid);
		this.sql.executeUpdate();

		this.sql.setSql(" delete from fw.func where gnid = ? ");
		this.sql.setString(1, gnid);
		this.sql.executeUpdate();

		this.sql.setSql(" delete from fw.func_union where gnid = ? ");
		this.sql.setString(1, gnid);
		this.sql.executeUpdate();
	}

	/**
	 * func
	 * 
	 * @author yjc
	 * @date 创建时间 2016-12-27
	 * @since V1.0
	 */
	public final DataMap resetFrameFuncSetting(final DataMap para) throws Exception {
		if ("000".equals(GlobalVars.SYS_DBID)) {
			throw new BizException("无法为框架重置Func,如新建DBID请修改appPara.xml的DBID参数后重启服务！");
		}

		// func
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.func ");
		sqlBF.append("   (dbid, gnid, gnmc, fgn, gnsj, gnlx, sxh, gntb, bz, ywlb) ");
		sqlBF.append("   select ? dbid, gnid, gnmc, fgn, gnsj, gnlx, sxh, gntb, bz, ywlb ");
		sqlBF.append("     from fw.func ");
		sqlBF.append("    where dbid = '000' ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// insert func_union
		sqlBF.setLength(0);
		sqlBF.append(" insert into fw.func_union ");
		sqlBF.append("   (dbid, gnid, gnmc, jbjgfw, bz) ");
		sqlBF.append("   select ? dbid, gnid, gnmc, jbjgfw, bz ");
		sqlBF.append("     from fw.func_union ");
		sqlBF.append("    where dbid = '000' ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		return null;
	}
}
