package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 轮询服务相关的业务逻辑
 * 
 * @author yjc
 */
public class PollingConfigBiz extends Biz{

	/**
	 * 进入轮询管理业务界面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdPollingConfigMng(final DataMap para) throws Exception {
		DataMap dmInfo = new DataMap();
		if (GlobalVars.IS_START_POLLING) {
			dmInfo.put("sm", "启动");
		} else {
			dmInfo.put("sm", "未启动");
		}

		DataMap rdm = new DataMap();
		rdm.put("forminfo", dmInfo);
		return rdm;
	}

	/**
	 * 刷新轮询配置数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap refreshGridPollingConfig(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.lxmc, a.lxbiz, a.lxff, a.lxcs, a.qssj, ");
		sqlBF.append("        a.zzsj, a.sjjg, b.sm ");
		sqlBF.append("   from fw.polling_config a, ");
		sqlBF.append("        fw.polling_doc b ");
		sqlBF.append("  where a.dbid = ? ");
		sqlBF.append("    and a.lxmc = b.lxmc ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsPolling = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dspolling", dsPolling);
		return rdm;
	}

	/**
	 * 刷新轮询文档数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap refreshGridPollingDoc(final DataMap para) throws Exception {
		this.sql.setSql(" select lxmc, lxbiz, lxff, lxcs, sm from fw.polling_doc ");
		DataSet dsPolling = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dspolling", dsPolling);
		return rdm;
	}

	/**
	 * 保存轮询文档新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap savePollingDocAdd(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		String lxbiz = para.getString("lxbiz");// 轮询Biz
		String lxff = para.getString("lxff");// 轮询方法
		String lxcs = para.getString("lxcs");// 轮询参数
		String sm = para.getString("sm");// 说明

		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}
		if (StringUtil.chkStrNull(lxbiz)) {
			throw new BizException("传入的轮询BIZ不允许为空");
		}
		if (StringUtil.chkStrNull(lxff)) {
			throw new BizException("传入的轮询方法不允许为空");
		}

		// 检查lxmc是否已经存在
		this.sql.setSql(" select lxmc from fw.polling_doc where lxmc = ? ");
		this.sql.setString(1, lxmc);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("轮询名称已经存在不允许重复新增");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.polling_doc ");
		sqlBF.append("   (lxmc, lxbiz, lxff, lxcs, sm) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, lxmc);
		this.sql.setString(2, lxbiz);
		this.sql.setString(3, lxff);
		this.sql.setString(4, lxcs);
		this.sql.setString(5, sm);

		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 进入轮询文档修改页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdPollingDocModify(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}
		this.sql.setSql(" select lxmc, lxbiz, lxff, lxcs, sm from fw.polling_doc where lxmc = ? ");
		this.sql.setString(1, lxmc);
		DataSet dsPolling = this.sql.executeQuery();
		if (dsPolling.size() <= 0) {
			throw new BizException("轮询名称为" + lxmc + "的轮询文档信息不存在。");
		}

		DataMap rdm = new DataMap();
		rdm.put("formpolling", dsPolling.getRow(0));
		return rdm;
	}

	/**
	 * 保存轮询文档修改信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap savePollingDocModify(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		String lxbiz = para.getString("lxbiz");// 轮询Biz
		String lxff = para.getString("lxff");// 轮询方法
		String lxcs = para.getString("lxcs");// 轮询参数
		String sm = para.getString("sm");// 说明

		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}
		if (StringUtil.chkStrNull(lxbiz)) {
			throw new BizException("传入的轮询BIZ不允许为空");
		}
		if (StringUtil.chkStrNull(lxff)) {
			throw new BizException("传入的轮询方法不允许为空");
		}

		// 检测是否可以修改
		this.sql.setSql(" select dbid from fw.polling_config where lxmc = ? ");
		this.sql.setString(1, lxmc);
		DataSet dsPolling = this.sql.executeQuery();
		if (dsPolling.size() > 0) {
			throw new BizException("轮询名称为" + lxmc
					+ "的轮询文档信息在Polling_cofig中已经配置给了"
					+ dsPolling.getString(0, "dbid") + ",不允许修改。");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" update fw.polling_doc a ");
		sqlBF.append("    set a.lxbiz = ?, a.lxff = ?, a.lxcs = ?, a.sm = ? ");
		sqlBF.append("  where a.lxmc = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, lxbiz);
		this.sql.setString(2, lxff);
		this.sql.setString(3, lxcs);
		this.sql.setString(4, sm);
		this.sql.setString(5, lxmc);

		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 进入轮询文档修改页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap deletePollingDoc(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}

		// 检测是否可以删除
		this.sql.setSql(" select dbid from fw.polling_config where lxmc = ? ");
		this.sql.setString(1, lxmc);
		DataSet dsPolling = this.sql.executeQuery();
		if (dsPolling.size() > 0) {
			throw new BizException("轮询名称为" + lxmc
					+ "的轮询文档信息在Polling_cofig中已经配置给了"
					+ dsPolling.getString(0, "dbid") + ",不允许删除。");
		}

		// 执行删除
		this.sql.setSql(" delete from fw.polling_doc where lxmc = ?");
		this.sql.setString(1, lxmc);
		this.sql.executeUpdate();
		return null;
	}

	/**
	 * 获取doc
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdChoosePollingDoc(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc", "");
		lxmc = "%" + lxmc + "%";

		this.sql.setSql(" select lxmc, lxbiz, lxff, lxcs, sm from fw.polling_doc where lxmc like ? or lxbiz like ? or lxff like ? ");
		this.sql.setString(1, lxmc);
		this.sql.setString(2, lxmc);
		this.sql.setString(3, lxmc);
		DataSet dsPolling = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dspolling", dsPolling);
		return rdm;
	}

	/**
	 * 保存轮询配置新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap savePollingConfigAdd(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		String lxcs = para.getString("lxcs");// 轮询参数
		int qssj = para.getInt("qssj");
		int zzsj = para.getInt("zzsj");
		int sjjg = para.getInt("sjjg");

		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}

		// 检查lxmc是否已经存在
		this.sql.setSql(" select lxmc from fw.polling_config where lxmc = ? and dbid = ? ");
		this.sql.setString(1, lxmc);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("轮询配置信息已经存在不允许重复配置。");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.polling_config ");
		sqlBF.append("   (dbid, lxmc, lxbiz, lxff, lxcs, ");
		sqlBF.append("    qssj, zzsj, sjjg) ");
		sqlBF.append("   select ?, lxmc, lxbiz, lxff, ?, ");
		sqlBF.append("          ?, ?, ? ");
		sqlBF.append("     from fw.polling_doc ");
		sqlBF.append("    where lxmc = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, lxcs);
		this.sql.setInt(3, qssj);
		this.sql.setInt(4, zzsj);
		this.sql.setInt(5, sjjg);

		this.sql.setString(6, lxmc);
		this.sql.executeUpdate();

		// 如果要启动轮询服务，则加载对应的轮询服务配置数据
		if (GlobalVars.IS_START_POLLING) {
			GlobalVarsUtil.reloadPOLLING_CONFIG_DS();
		}

		return null;
	}

	/**
	 * 删除轮询配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap deletePollingConfig(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}

		// 执行删除
		this.sql.setSql(" delete from fw.polling_config where lxmc = ? and dbid = ?");
		this.sql.setString(1, lxmc);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();
		
		// 如果要启动轮询服务，则加载对应的轮询服务配置数据
		if (GlobalVars.IS_START_POLLING) {
			GlobalVarsUtil.reloadPOLLING_CONFIG_DS();
		}
		
		return null;
	}

	/**
	 * 进入轮询修改页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdPollingConfigModify(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select dbid, lxmc, lxbiz, lxff, lxcs, qssj, zzsj, sjjg ");
		sqlBF.append("   from fw.polling_config ");
		sqlBF.append("  where lxmc = ? ");
		sqlBF.append("    and dbid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, lxmc);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsPolling = this.sql.executeQuery();
		if (dsPolling.size() <= 0) {
			throw new BizException("轮询名称为" + lxmc + "的轮询配置信息不存在。");
		}

		DataMap rdm = new DataMap();
		rdm.put("formpolling", dsPolling.getRow(0));
		return rdm;
	}

	/**
	 * 保存轮询文档修改信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap savePollingConfigModify(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		String lxcs = para.getString("lxcs");// 轮询参数
		int qssj = para.getInt("qssj");
		int zzsj = para.getInt("zzsj");
		int sjjg = para.getInt("sjjg");

		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" update fw.polling_config a ");
		sqlBF.append("    set a.lxcs = ?, a.qssj = ?, a.zzsj = ?, a.sjjg = ? ");
		sqlBF.append("  where a.lxmc = ? ");
		sqlBF.append("    and a.dbid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, lxcs);
		this.sql.setInt(2, qssj);
		this.sql.setInt(3, zzsj);
		this.sql.setInt(4, sjjg);
		this.sql.setString(5, lxmc);
		this.sql.setString(6, GlobalVars.SYS_DBID);

		this.sql.executeUpdate();
		
		// 如果要启动轮询服务，则加载对应的轮询服务配置数据
		if (GlobalVars.IS_START_POLLING) {
			GlobalVarsUtil.reloadPOLLING_CONFIG_DS();
		}

		return null;
	}

	/**
	 * 手动执行
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-4
	 * @since V1.0
	 */
	public final DataMap runPollingConfigByHand(final DataMap para) throws Exception {
		String lxmc = para.getString("lxmc");// 轮询名称
		if (StringUtil.chkStrNull(lxmc)) {
			throw new BizException("传入的轮询名称不允许为空");
		}

		// 执行删除
		this.sql.setSql(" select lxbiz, lxff, lxcs from fw.polling_config where lxmc = ? and dbid = ?");
		this.sql.setString(1, lxmc);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsPolling = this.sql.executeQuery();
		if (dsPolling.size() <= 0) {
			throw new BizException("轮询名称为" + lxmc + "的轮询配置信息不存在。");
		}
		String lxbiz = dsPolling.getString(0, "lxbiz");// 轮询biz
		String lxff = dsPolling.getString(0, "lxff");// 轮询方法
		String lxcs = dsPolling.getString(0, "lxcs");// 轮询参数

		para.remove("lxmc");// 移除名称
		para.put("lxcs", lxcs);// 放入轮询参数

		// 执行轮询
		DataMap rdm = this.doBizMethod(lxbiz, lxff, para);
		return rdm;
	}

}
