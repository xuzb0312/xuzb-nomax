package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 典型批注的管理
 * 
 * @author yjc
 */
public class NoteMngBiz extends Biz{

	/**
	 * 典型批注一个的管理
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public final DataMap fwdOneNoteMng(final DataMap para) throws Exception {
		String pzbh = para.getString("pzbh");
		if (StringUtil.chkStrNull(pzbh)) {
			throw new BizException("传入的批注编号为空");
		}

		// 批注信息
		this.sql.setSql(" select pzbh, pzmc, pzsm from fw.note_list where pzbh = ? ");
		this.sql.setString(1, pzbh);
		DataSet dsList = this.sql.executeQuery();
		if (dsList.size() <= 0) {
			throw new BizException("根据传入的批注编号" + pzbh + "没有查询到批注信息");
		}

		// 批注配置信息
		this.sql.setSql(" select pznr, xh from fw.note_config where dbid = ? and pzbh = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, pzbh);
		DataSet dsConfig = this.sql.executeQuery();
		dsConfig.sort("xh");

		DataMap rdm = new DataMap();
		rdm.put("dmlist", dsList.getRow(0));
		rdm.put("dsconfig", dsConfig);
		return rdm;
	}

	/**
	 * 保存note_list的新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public final DataMap saveNoteListAdd(final DataMap para) throws Exception {
		String pzbh = para.getString("pzbh");
		String pzmc = para.getString("pzmc");
		String pzsm = para.getString("pzsm");

		DataSet dsConfig = para.getDataSet("gridConfig");

		if (StringUtil.chkStrNull(pzbh)) {
			throw new BizException("传入的批注编号为空。");
		}
		if (StringUtil.chkStrNull(pzmc)) {
			throw new BizException("传入的批注名称为空。");
		}
		if (dsConfig == null) {
			throw new BizException("传入的批注配置信息为空。");
		}

		// 数据检查
		this.sql.setSql(" select pzbh from fw.note_list where pzbh = ? ");
		this.sql.setString(1, pzbh);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("传入的批注编号在系统中已经存在，不允许重复新增");
		}

		// 插入note_list
		this.sql.setSql(" insert into fw.note_list (pzbh, pzmc, pzsm) values (?, ?, ?) ");
		this.sql.setString(1, pzbh);
		this.sql.setString(2, pzmc);
		this.sql.setString(3, pzsm);
		this.sql.executeUpdate();

		// 插入code
		this.sql.setSql(" insert into fw.note_config (dbid, pzbh, pznr, xh) values (?, ?, ?, ?) ");
		for (int i = 0, n = dsConfig.size(); i < n; i++) {
			String pznr = dsConfig.getString(i, "pznr");
			int xh = dsConfig.getInt(i, "xh");
			if (StringUtil.chkStrNull(pznr)) {
				throw new BizException("传入的批注内容为空");
			}
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, pzbh);
			this.sql.setString(3, pznr);
			this.sql.setInt(4, xh);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		return null;
	}

	/**
	 * list修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public final DataMap saveNoteListModify(final DataMap para) throws Exception {
		String pzbh = para.getString("pzbh");
		String pzmc = para.getString("pzmc");
		String pzsm = para.getString("pzsm");

		if (StringUtil.chkStrNull(pzbh)) {
			throw new BizException("传入的批注编号为空。");
		}
		if (StringUtil.chkStrNull(pzmc)) {
			throw new BizException("传入的批注名称为空。");
		}

		// 更新操作
		this.sql.setSql(" update fw.note_list set pzmc = ?, pzsm = ? where pzbh = ? ");
		this.sql.setString(1, pzmc);
		this.sql.setString(2, pzsm);
		this.sql.setString(3, pzbh);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * config信息的修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public final DataMap saveNoteConfigModify(final DataMap para) throws Exception {
		String pzbh = para.getString("pzbh");
		DataSet dsConfig = para.getDataSet("gridConfig");
		if (StringUtil.chkStrNull(pzbh)) {
			throw new BizException("传入的批注编号为空。");
		}
		if (dsConfig == null) {
			throw new BizException("传入的批注配置信息为空。");
		}

		// 删除--先删后插
		this.sql.setSql(" delete from fw.note_config where dbid = ? and pzbh = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, pzbh);
		this.sql.executeUpdate();

		// 插入code
		this.sql.setSql(" insert into fw.note_config (dbid, pzbh, pznr, xh) values (?, ?, ?, ?) ");
		for (int i = 0, n = dsConfig.size(); i < n; i++) {
			String pznr = dsConfig.getString(i, "pznr");
			int xh = dsConfig.getInt(i, "xh");
			if (StringUtil.chkStrNull(pznr)) {
				throw new BizException("传入的批注内容为空");
			}
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, pzbh);
			this.sql.setString(3, pznr);
			this.sql.setInt(4, xh);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		return null;
	}

	/**
	 * 删除note
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public final DataMap saveNoteListDel(final DataMap para) throws Exception {
		String pzbh = para.getString("pzbh");
		if (StringUtil.chkStrNull(pzbh)) {
			throw new BizException("传入的批注编号为空。");
		}

		// 删除--先删后插
		this.sql.setSql(" delete from fw.note_config where dbid = ? and pzbh = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, pzbh);
		this.sql.executeUpdate();

		this.sql.setSql(" select pzbh from fw.note_config where pzbh = ? ");
		this.sql.setString(1, pzbh);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() <= 0) {
			this.sql.setSql(" delete from fw.note_list where pzbh = ? ");
			this.sql.setString(1, pzbh);
			this.sql.executeUpdate();
		}
		return null;
	}

}
