package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 参数管理
 * 
 * @author yjc
 */
public class SysParaMngBiz extends Biz{
	/**
	 * 考试参数管理
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-7
	 * @since V1.0
	 */
	public final DataMap fwdSysParaMng(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select lower(a.csbh) csbh, ");
		sqlBF.append("        a.csmc, ");
		sqlBF.append("        (select b.csz ");
		sqlBF.append("           from fw.sys_para b ");
		sqlBF.append("          where b.csbh = a.csbh ");
		sqlBF.append("            and b.dbid = ?) csz, ");
		sqlBF.append("        a.cssm, ");
		sqlBF.append("        lower(nvl(a.cssjlx, 'string')) cssjlx, ");
		sqlBF.append("        a.cssjym, ");
		sqlBF.append("        a.cssjcode, ");
		sqlBF.append("        a.xh ");
		sqlBF.append("   from fw.sys_para_doc a ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsPara = this.sql.executeQuery();
		dsPara.sort("xh");

		DataMap rdm = new DataMap();
		rdm.put("dspara", dsPara);
		return rdm;
	}

	/**
	 * 保存参数
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-7
	 * @since V1.0
	 */
	public final DataMap saveSysPara(final DataMap para) throws Exception {
		String dbid = GlobalVars.SYS_DBID;

		// 策略先删后插
		this.sql.setSql(" delete from fw.sys_para where dbid = ? ");
		this.sql.setString(1, dbid);
		this.sql.executeUpdate();

		// 查询参数编号
		this.sql.setSql(" select lower(csbh) csbh from fw.sys_para_doc ");
		DataSet dsParaDoc = this.sql.executeQuery();

		// 插入操作
		this.sql.setSql(" insert into fw.sys_para (dbid, csbh, csz) values (?, ?, ?) ");
		for (int i = 0, n = dsParaDoc.size(); i < n; i++) {
			String csbh = dsParaDoc.getString(i, "csbh");
			String csz = para.getString(csbh, "");// 从para中获取
			if (StringUtil.chkStrNull(csz)) {
				continue;
			}
			// 新增
			this.sql.setString(1, dbid);
			this.sql.setString(2, csbh);
			this.sql.setString(3, csz);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 加载系统常用配置参数
		GlobalVarsUtil.reloadSYS_BASE_PATH();// 基本路径地址
		GlobalVarsUtil.reloadNO_CHK_SAME_REFERE();// 不验证同源的域名

		return null;
	}
}
