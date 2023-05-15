package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * code管理
 * 
 * @author yjc
 */
public class CodeConfigMngBiz extends Biz{
	/**
	 * 一个code管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdOneCodeMng(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		StringBuffer sqlBF = new StringBuffer();
		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空。");
		}

		// codelist信息
		this.sql.setSql(" select dmbh, dmmc, dmsm from fw.code_list where dmbh = ? ");
		this.sql.setString(1, dmbh);
		DataSet dsCodeList = this.sql.executeQuery();
		if (dsCodeList.size() <= 0) {
			throw new BizException("该代码编号的code信息不存在。");
		}

		// code项
		this.sql.setSql(" select code, content, sm from fw.code_doc where dmbh = ? ");
		this.sql.setString(1, dmbh);
		DataSet dsCodeDoc = this.sql.executeQuery();
		dsCodeList = dsCodeList.sort("code");

		// 本地配置信息
		sqlBF.setLength(0);
		sqlBF.append(" select code, content ");
		sqlBF.append("   from fw.code_config ");
		sqlBF.append("  where dbid = ? ");
		sqlBF.append("    and dmbh = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, dmbh);
		DataSet dsConfig = this.sql.executeQuery();

		DataMap dmList = dsCodeList.getRow(0);
		dmList.put("bdpz", dsConfig.toString("code"));
		dmList.put("bdpzhy", dsConfig.toString("content"));

		DataMap rdm = new DataMap();
		rdm.put("codelist", dmList);
		rdm.put("codedoc", dsCodeDoc);
		return rdm;
	}

	/**
	 * 保存codeList的新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap saveCodeListAdd(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		String dmmc = para.getString("dmmc");
		String dmsm = para.getString("dmsm");
		String pzbd = para.getString("pzbd");

		DataSet dsCode = para.getDataSet("gridcode");

		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空。");
		}
		if (StringUtil.chkStrNull(dmmc)) {
			throw new BizException("传入的代码名称为空。");
		}
		if (dsCode == null) {
			throw new BizException("传入的代码明细为空。");
		}

		// 数据检查
		this.sql.setSql(" select dmbh from fw.code_list where dmbh = ? ");
		this.sql.setString(1, dmbh);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("传入的代码编号在系统中已经存在，不允许重复新增");
		}

		// 插入code_list
		this.sql.setSql(" insert into fw.code_list (dmbh, dmmc, dmsm) values (?, ?, ?) ");
		this.sql.setString(1, dmbh);
		this.sql.setString(2, dmmc);
		this.sql.setString(3, dmsm);
		this.sql.executeUpdate();

		// 插入code
		this.sql.setSql(" insert into fw.code_doc (dmbh, code, content, sm) values (?, ?, ?, ?) ");
		for (int i = 0, n = dsCode.size(); i < n; i++) {
			String code = dsCode.getString(i, "code");
			String content = dsCode.getString(i, "content");
			String sm = dsCode.getString(i, "sm");
			if (StringUtil.chkStrNull(code)) {
				throw new BizException("传入的CODE明细中，code为空。");
			}
			if (StringUtil.chkStrNull(content)) {
				throw new BizException("传入的CODE明细中，content为空。");
			}

			this.sql.setString(1, dmbh);
			this.sql.setString(2, code);
			this.sql.setString(3, content);
			this.sql.setString(4, sm);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 配置本地
		if ("1".equals(pzbd)) {
			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" insert into fw.code_config ");
			sqlBF.append("   (dbid, dmbh, code, content, xh) ");
			sqlBF.append("   select ?, dmbh, code, content, null from fw.code_doc where dmbh = ? ");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, dmbh);
			this.sql.executeUpdate();

			// 重新加载缓存
			GlobalVarsUtil.reloadCODE_MAP();
		}

		return null;
	}

	/**
	 * CODELIST修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap fwdCodeListModify(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空。");
		}

		// codelist信息
		this.sql.setSql(" select dmbh, dmmc, dmsm from fw.code_list where dmbh = ? ");
		this.sql.setString(1, dmbh);
		DataSet dsCodeList = this.sql.executeQuery();
		if (dsCodeList.size() <= 0) {
			throw new BizException("该代码编号的code信息不存在。");
		}

		DataMap rdm = new DataMap();
		rdm.put("codelist", dsCodeList.getRow(0));
		return rdm;
	}

	/**
	 * CODELIST修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public final DataMap saveCodeListModify(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		String dmmc = para.getString("dmmc");
		String dmsm = para.getString("dmsm");

		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空。");
		}
		if (StringUtil.chkStrNull(dmmc)) {
			throw new BizException("传入的代码名称为空。");
		}

		// 修改
		this.sql.setSql(" update fw.code_list set dmmc = ? ,dmsm = ? where dmbh = ? ");
		this.sql.setString(1, dmmc);
		this.sql.setString(2, dmsm);
		this.sql.setString(3, dmbh);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * codeLIst删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap deleteCodeList(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空。");
		}

		// config删除
		this.sql.setSql(" delete from fw.code_config where dmbh = ? ");
		this.sql.setString(1, dmbh);
		this.sql.executeUpdate();

		// doc删除
		this.sql.setSql(" delete from fw.code_doc where dmbh = ? ");
		this.sql.setString(1, dmbh);
		this.sql.executeUpdate();

		// list删除
		this.sql.setSql(" delete from fw.code_list where dmbh = ? ");
		this.sql.setString(1, dmbh);
		this.sql.executeUpdate();

		// 重新加载缓存
		GlobalVarsUtil.reloadCODE_MAP();

		return null;
	}

	/**
	 * codeConfig配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap fwdCodeConfigSet(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空。");
		}
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select a.code, a.content ");
		sqlBF.append("   from fw.code_doc a ");
		sqlBF.append("  where a.dmbh = ? ");
		sqlBF.append("    and not exists (select 'x' ");
		sqlBF.append("           from fw.code_config b ");
		sqlBF.append("          where a.dmbh = b.dmbh ");
		sqlBF.append("            and a.code = b.code ");
		sqlBF.append("            and b.dbid = ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, dmbh);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsDoc = this.sql.executeQuery();
		dsDoc.sort("code");

		sqlBF.setLength(0);
		sqlBF.append(" select code, content, xh ");
		sqlBF.append("   from fw.code_config ");
		sqlBF.append("  where dmbh = ? ");
		sqlBF.append("    and dbid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, dmbh);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsConfig = this.sql.executeQuery();
		dsConfig.sort("xh");

		DataMap rdm = new DataMap();
		rdm.put("dsdoc", dsDoc);
		rdm.put("dsconfig", dsConfig);
		rdm.put("dmbh", dmbh);
		return rdm;
	}

	/**
	 * code_config配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap saveCodeConfig(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		DataSet dsCode = para.getDataSet("gridconfig");
		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空。");
		}
		if (dsCode == null) {
			throw new BizException("传入的代码明细为空。");
		}

		// 删除本地原有的code信息
		this.sql.setSql(" delete from fw.code_config where dmbh = ? and dbid = ? ");
		this.sql.setString(1, dmbh);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 新增
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.code_config ");
		sqlBF.append("   (dbid, dmbh, code, content, xh) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		for (int i = 0, n = dsCode.size(); i < n; i++) {
			String code = dsCode.getString(i, "code");
			String content = dsCode.getString(i, "content");
			int xh = dsCode.getInt(i, "xh");

			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, dmbh);
			this.sql.setString(3, code);
			this.sql.setString(4, content);
			this.sql.setInt(5, xh);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 重新加载缓存
		GlobalVarsUtil.reloadCODE_MAP();

		return null;
	}

	/**
	 * 新增CODE-doc
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap saveCodeDocAdd(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		String code = para.getString("code");
		String content = para.getString("content");
		String sm = para.getString("sm");
		String pzbd = para.getString("pzbd");

		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空");
		}
		if (StringUtil.chkStrNull(code)) {
			throw new BizException("传入的代码CODE为空");
		}
		if (StringUtil.chkStrNull(content)) {
			throw new BizException("传入的代码含义为空");
		}

		// 检查是否已经存在
		this.sql.setSql(" select code from fw.code_doc where dmbh = ? and code = ? ");
		this.sql.setString(1, dmbh);
		this.sql.setString(2, code);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该代码已经存在，无法再次新增");
		}

		// 插入
		this.sql.setSql(" insert into fw.code_doc (dmbh, code, content, sm) values (?, ?, ?, ?) ");
		this.sql.setString(1, dmbh);
		this.sql.setString(2, code);
		this.sql.setString(3, content);
		this.sql.setString(4, sm);
		this.sql.executeUpdate();

		// 配置本地
		if ("1".equals(pzbd)) {
			// 获取最大的xh
			this.sql.setSql(" select max(xh) xh from fw.code_config where dbid = ? and dmbh = ? ");
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, dmbh);
			dsTemp = this.sql.executeQuery();
			int xh = dsTemp.getInt(0, "xh");// 获取最大的序号
			xh = xh + 1;// 序号+1

			StringBuffer sqlBF = new StringBuffer();
			sqlBF.append(" insert into fw.code_config ");
			sqlBF.append("   (dbid, dmbh, code, content, xh) ");
			sqlBF.append("   select ?, dmbh, code, content, ? from fw.code_doc where dmbh = ? and code = ?");

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setInt(2, xh);
			this.sql.setString(3, dmbh);
			this.sql.setString(4, code);
			this.sql.executeUpdate();

			// 重新加载缓存
			GlobalVarsUtil.reloadCODE_MAP();
		}

		return null;
	}

	/**
	 * 修改CODE-doc
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap fwdCodeDocModify(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		String code = para.getString("code");

		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空");
		}
		if (StringUtil.chkStrNull(code)) {
			throw new BizException("传入的代码CODE为空");
		}

		// 检查是否已经存在
		this.sql.setSql(" select dmbh, code, content, sm from fw.code_doc where dmbh = ? and code = ? ");
		this.sql.setString(1, dmbh);
		this.sql.setString(2, code);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("该代码不存在");
		}
		DataMap dm = new DataMap();
		dm.put("docinfo", dsTemp.getRow(0));

		return dm;
	}

	/**
	 * 修改CODE-doc
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap saveCodeDocModify(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		String code = para.getString("code");
		String content = para.getString("content");
		String sm = para.getString("sm");

		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空");
		}
		if (StringUtil.chkStrNull(code)) {
			throw new BizException("传入的代码CODE为空");
		}
		if (StringUtil.chkStrNull(content)) {
			throw new BizException("传入的代码含义为空");
		}

		// doc
		this.sql.setSql(" update fw.code_doc set content = ?, sm = ? where dmbh = ? and code = ? ");
		this.sql.setString(1, content);
		this.sql.setString(2, sm);
		this.sql.setString(3, dmbh);
		this.sql.setString(4, code);
		this.sql.executeUpdate();

		// config
		this.sql.setSql(" update fw.code_config set content = ? where dmbh = ? and code = ? ");
		this.sql.setString(1, content);
		this.sql.setString(2, dmbh);
		this.sql.setString(3, code);
		this.sql.executeUpdate();

		// 重新加载缓存
		GlobalVarsUtil.reloadCODE_MAP();

		return null;
	}

	/**
	 * code-doc删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap deleteCodeDocInfo(final DataMap para) throws Exception {
		String dmbh = para.getString("dmbh");
		String code = para.getString("code");

		if (StringUtil.chkStrNull(dmbh)) {
			throw new BizException("传入的代码编号为空");
		}
		if (StringUtil.chkStrNull(code)) {
			throw new BizException("传入的代码CODE为空");
		}

		// config删除
		this.sql.setSql(" delete from fw.code_config where dmbh = ? and code = ? ");
		this.sql.setString(1, dmbh);
		this.sql.setString(2, code);
		this.sql.executeUpdate();

		// doc删除
		this.sql.setSql(" delete from fw.code_doc where dmbh = ? and code = ? ");
		this.sql.setString(1, dmbh);
		this.sql.setString(2, code);
		this.sql.executeUpdate();

		// 重新加载缓存
		GlobalVarsUtil.reloadCODE_MAP();

		return null;
	}

	/**
	 * code
	 * 
	 * @author yjc
	 * @date 创建时间 2016-12-27
	 * @since V1.0
	 */
	public final DataMap resetFrameCodeSetting(final DataMap para) throws Exception {
		if ("000".equals(GlobalVars.SYS_DBID)) {
			throw new BizException("无法为框架重置Func,如新建DBID请修改appPara.xml的DBID参数后重启服务！");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.code_config ");
		sqlBF.append("   (dbid, dmbh, code, content, xh) ");
		sqlBF.append("   select ? dbid, dmbh, code, content, xh ");
		sqlBF.append("     from fw.code_config ");
		sqlBF.append("    where dbid = '000' ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 缓存重置
		GlobalVarsUtil.reloadCODE_MAP();

		return null;
	}
}
