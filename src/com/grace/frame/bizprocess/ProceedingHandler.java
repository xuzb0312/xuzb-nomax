package com.grace.frame.bizprocess;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Date;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.DateUtil;
import com.grace.frame.util.SeqUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.StringUtil;

/**
 * 待办事项操作控制句柄
 * 
 * @author yjc
 */
public class ProceedingHandler{

	/**
	 * 待办事项创建 <br>
	 * sxmc-事项名称； <br>
	 * sxdygnid-事项对应功能ID;<br>
	 * sxdyjbjgid-事项对应经办机构ID;<br>
	 * czztid-操作主体ID;<br>
	 * fqsj-发起时间；<br>
	 * fqr-发起人； <br>
	 * czr-操作人；<br>
	 * ymcs-blob进入页面的默认参数；<br>
	 * dbcs-blob待办操作的参数； <br>
	 * czcssx(天)-操作超时时限(包含当天)（--超过时限的需要进行标识）。<br>
	 * czjgsx(天)-操作警告时限（包含当天--例如：2天办结，则业务发起日为1天，第二天就要进行办结）。传入0或负数则标识不计算时限<br>
	 * gzrbz 工作日标志<br>
	 * lctwjbs 流程图文件标识
	 * 
	 * @author yjc
	 * @throws BizException
	 * @throws AppException
	 * @throws IOException
	 * @throws SQLException
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public static String create(String sxmc, String sxdygnid,
			String sxdyjbjgid, String czztid, String fqr, Date fqsj,
			String czr, DataMap ymcs, DataMap dbcs, int czcssx, int czjgsx,
			boolean gzrbz, String lctwjbs) throws BizException, AppException, IOException, SQLException {
		if (StringUtil.chkStrNull(sxmc)) {
			throw new BizException("传入待办事项名称为空");
		}
		if (StringUtil.chkStrNull(sxdygnid)) {
			throw new BizException("传入待办事项对应功能ID为空");
		}
		if (StringUtil.chkStrNull(czztid)) {
			throw new BizException("传入操作主体ID为空");
		}
		if (StringUtil.chkStrNull(sxdyjbjgid)) {
			sxdyjbjgid = "*";// 不控经办机构
		}
		if (StringUtil.chkStrNull(fqr)) {
			throw new BizException("传入发起人为空");
		}
		if (null == fqsj) {
			throw new BizException("传入发起时间为空");
		}
		if (null == ymcs) {
			ymcs = new DataMap();
		}
		if (null == dbcs) {
			dbcs = new DataMap();
		}
		if (czjgsx > czcssx) {
			throw new BizException("操作警告时限应该小于操作超时时限。");
		}
		if (czjgsx < 0) {
			czjgsx = 0;
		}
		if (czcssx < 0) {
			czcssx = 0;
		}

		// 插入数据
		Sql sql = new Sql();
		String sxid = SeqUtil.getId("fw.sq_sxid");

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.proceeding ");
		sqlBF.append("   (sxid, sxmc, sxdygnid, sxdyjbjgid, czztid, ");
		sqlBF.append("    sxzt, fqr, fqsj, czr, czsj, ");
		sqlBF.append("    zfr, zfsj, zfyy, bz, ymcs, ");
		sqlBF.append("    dbcs, czcssx, czjgsx, gzrbz, lctwjbs) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    '0', ?, ?, ?, null, ");
		sqlBF.append("    null, null, null, null, empty_blob(), ");
		sqlBF.append("    empty_blob(), ?, ?, ?, ?) ");
		sql.setSql(sqlBF.toString());
		sql.setString(1, sxid);
		sql.setString(2, sxmc);
		sql.setString(3, sxdygnid);
		sql.setString(4, sxdyjbjgid);
		sql.setString(5, czztid);

		sql.setString(6, fqr);
		sql.setDate(7, fqsj);
		sql.setString(8, czr);
		sql.setInt(9, czcssx);
		sql.setInt(10, czjgsx);
		sql.setString(11, gzrbz ? "1" : "0");// 工作日标志
		sql.setString(12, lctwjbs);// 流程图文件标识

		sql.executeUpdate();

		// 保存参数内容
		sql.setSql(" select ymcs, dbcs from fw.proceeding where sxid = ? for update ");
		sql.setString(1, sxid);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() > 0) {
			Blob blob_ymcs = (Blob) dsTemp.getBlob(0, "ymcs");
			Blob blob_dbcs = (Blob) dsTemp.getBlob(0, "dbcs");

			// 页面参数--对象序列化
			ObjectOutputStream outputstream = new ObjectOutputStream(blob_ymcs.setBinaryStream(0));
			outputstream.writeObject(ymcs);
			outputstream.flush();
			outputstream.close();

			// 待办参数
			outputstream = new ObjectOutputStream(blob_dbcs.setBinaryStream(0));
			outputstream.writeObject(dbcs);
			outputstream.flush();
			outputstream.close();
		}

		return sxid;
	}

	/**
	 * 创建代办事项--工作日来进行计算
	 * 
	 * @author yjc
	 * @date 创建时间 2016-3-5
	 * @since V1.0
	 */
	public static String create(String sxmc, String sxdygnid,
			String sxdyjbjgid, String czztid, String fqr, Date fqsj,
			String czr, DataMap ymcs, DataMap dbcs, int czcssx, int czjgsx) throws Exception {
		return ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, fqr, fqsj, czr, ymcs, dbcs, czcssx, czjgsx, true, null);
	}

	/**
	 * 创建代办事项--工作日来进行计算
	 * 
	 * @author yjc
	 * @date 创建时间 2016-3-5
	 * @since V1.0
	 */
	public static String create(String sxmc, String sxdygnid,
			String sxdyjbjgid, String czztid, String fqr, Date fqsj,
			String czr, DataMap ymcs, DataMap dbcs, int czcssx, int czjgsx,
			String lctwjbs) throws Exception {
		return ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, fqr, fqsj, czr, ymcs, dbcs, czcssx, czjgsx, true, lctwjbs);
	}

	/**
	 * 创建待办事项的重载 --无操作人
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public static String create(String sxmc, String sxdygnid,
			String sxdyjbjgid, String czztid, String fqr, Date fqsj,
			DataMap ymcs, DataMap dbcs, int czcssx, int czjgsx) throws Exception {
		return ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, fqr, fqsj, null, ymcs, dbcs, czcssx, czjgsx);
	}

	/**
	 * 创建待办事项的重载 --无操作时限
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public static String create(String sxmc, String sxdygnid,
			String sxdyjbjgid, String czztid, String fqr, Date fqsj,
			String czr, DataMap ymcs, DataMap dbcs) throws Exception {
		return ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, fqr, fqsj, czr, ymcs, dbcs, 0, 0);
	}

	/**
	 * 创建待办事项的重载 --无操作时限,误操作人
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public static String create(String sxmc, String sxdygnid,
			String sxdyjbjgid, String czztid, String fqr, Date fqsj,
			DataMap ymcs, DataMap dbcs) throws Exception {
		return ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, fqr, fqsj, null, ymcs, dbcs, 0, 0);
	}

	/**
	 * 待办事项办结
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public static void finish(String sxid, String czr, Date czsj, String czsm) throws Exception {
		if (StringUtil.chkStrNull(sxid)) {
			throw new BizException("传入待办事项ID为空");
		}
		if (StringUtil.chkStrNull(czr)) {
			throw new BizException("传入操作人为空");
		}
		if (null == czsj) {
			throw new BizException("传入操作时间为空");
		}
		if (StringUtil.chkStrNull(czsm)) {
			czsm = "事项办结";
		}

		Sql sql = new Sql();
		sql.setSql(" update fw.proceeding set czr = ?, czsj = ?, czsm = ?, sxzt = '1' where sxid = ? and sxzt = '0' ");
		sql.setString(1, czr);
		sql.setDate(2, czsj);
		sql.setString(3, czsm);
		sql.setString(4, sxid);
		sql.executeUpdate();
	}

	/**
	 * 待办事项办结
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public static void finish(String sxdygnid, String czztid, String czr,
			Date czsj, String czsm) throws Exception {
		if (StringUtil.chkStrNull(sxdygnid)) {
			throw new BizException("传入事项对应功能ID为空");
		}
		if (StringUtil.chkStrNull(czztid)) {
			throw new BizException("传入操作主体为空");
		}
		if (StringUtil.chkStrNull(czr)) {
			throw new BizException("传入操作人为空");
		}
		if (null == czsj) {
			throw new BizException("传入操作时间为空");
		}
		if (StringUtil.chkStrNull(czsm)) {
			czsm = "事项办结";
		}

		Sql sql = new Sql();
		sql.setSql(" update fw.proceeding set czr = ?, czsj = ?, czsm = ?, sxzt = '1' where sxdygnid = ? and czztid = ? and sxzt = '0' ");
		sql.setString(1, czr);
		sql.setDate(2, czsj);
		sql.setString(3, czsm);
		sql.setString(4, sxdygnid);
		sql.setString(5, czztid);
		sql.executeUpdate();
	}

	/**
	 * 待办事项作废
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public static void nullify(String sxid, String zfr, Date zfsj, String zfyy) throws Exception {
		if (StringUtil.chkStrNull(sxid)) {
			throw new BizException("传入待办事项ID为空");
		}
		if (StringUtil.chkStrNull(zfr)) {
			throw new BizException("传入作废人为空");
		}
		if (null == zfsj) {
			throw new BizException("传入作废时间为空");
		}

		Sql sql = new Sql();
		sql.setSql(" update fw.proceeding set zfr = ?, zfsj = ?, sxzt = '2', zfyy = ? where sxid = ? and sxzt = '0' ");
		sql.setString(1, zfr);
		sql.setDate(2, zfsj);
		sql.setString(3, zfyy);
		sql.setString(4, sxid);
		sql.executeUpdate();
	}

	/**
	 * 待办事项作废
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public static void nullify(String sxdygnid, String czztid, String zfr,
			Date zfsj, String zfyy) throws Exception {
		if (StringUtil.chkStrNull(sxdygnid)) {
			throw new BizException("传入事项对应功能ID为空");
		}
		if (StringUtil.chkStrNull(czztid)) {
			throw new BizException("传入操作主体为空");
		}
		if (StringUtil.chkStrNull(zfr)) {
			throw new BizException("传入作废人为空");
		}
		if (null == zfsj) {
			throw new BizException("传入作废时间为空");
		}

		Sql sql = new Sql();
		sql.setSql(" update fw.proceeding set zfr = ?, zfsj = ?, sxzt = '2', zfyy = ? where sxdygnid = ? and czztid = ? and sxzt = '0' ");
		sql.setString(1, zfr);
		sql.setDate(2, zfsj);
		sql.setString(3, zfyy);
		sql.setString(4, sxdygnid);
		sql.setString(5, czztid);
		sql.executeUpdate();
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String nextBySxid(String ysxid, String sxmc, String sxdygnid,
			String sxdyjbjgid, String czr, Date czsj, DataMap ymcs,
			DataMap dbcs, String czsm) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select czztid, fqsj, czcssx, czjgsx, gzrbz, lctwjbs from fw.proceeding where sxid = ? ");
		sql.setString(1, ysxid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String czztid = ds.getString(0, "czztid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		// 事项办理完结
		ProceedingHandler.finish(ysxid, czr, czsj, czsm);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs, dbcs, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String nextBySxid(String ysxid, String sxmc, String sxdygnid,
			String czr, Date czsj, DataMap ymcs, DataMap dbcs, String czsm) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select czztid, fqsj, czcssx, czjgsx, sxdyjbjgid, gzrbz, lctwjbs from fw.proceeding where sxid = ? ");
		sql.setString(1, ysxid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String czztid = ds.getString(0, "czztid");
		String sxdyjbjgid = ds.getString(0, "sxdyjbjgid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}
		// 事项办理完结
		ProceedingHandler.finish(ysxid, czr, czsj, czsm);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs, dbcs, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String nextBySxid(String ysxid, String sxmc, String sxdygnid,
			String czr, Date czsj, String czsm) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select czztid, fqsj, czcssx, czjgsx, sxdyjbjgid, ymcs, dbcs, gzrbz, lctwjbs from fw.proceeding where sxid = ? ");
		sql.setString(1, ysxid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String czztid = ds.getString(0, "czztid");
		String sxdyjbjgid = ds.getString(0, "sxdyjbjgid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		Blob ymcs = (Blob) ds.getObject(0, "ymcs");
		ObjectInputStream ymcs_inputstream = new ObjectInputStream(ymcs.getBinaryStream());
		DataMap ymcs_dm = (DataMap) ymcs_inputstream.readObject();
		ymcs_inputstream.close();

		Blob dbcs = (Blob) ds.getObject(0, "dbcs");
		ObjectInputStream dbcs_inputstream = new ObjectInputStream(dbcs.getBinaryStream());
		DataMap dbcs_dm = (DataMap) dbcs_inputstream.readObject();
		dbcs_inputstream.close();

		// 事项办理完结
		ProceedingHandler.finish(ysxid, czr, czsj, czsm);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs_dm, dbcs_dm, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String nextByGnid(String ysxdygnid, String czztid,
			String sxmc, String sxdygnid, String sxdyjbjgid, String czr,
			Date czsj, DataMap ymcs, DataMap dbcs, String czsm) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select sxid, fqsj, czcssx, czjgsx, gzrbz, lctwjbs from fw.proceeding where sxdygnid = ? and czztid = ? and sxzt = '0' ");
		sql.setString(1, ysxdygnid);
		sql.setString(2, czztid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		String ysxid = ds.getString(0, "sxid");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		// 事项办理完结
		ProceedingHandler.finish(ysxdygnid, czztid, czr, czsj, czsm);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs, dbcs, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String nextByGnid(String ysxdygnid, String czztid,
			String sxmc, String sxdygnid, String czr, Date czsj, DataMap ymcs,
			DataMap dbcs, String czsm) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select sxid, czztid, fqsj, czcssx, czjgsx, sxdyjbjgid, gzrbz, lctwjbs from fw.proceeding where sxdygnid = ? and czztid = ? and sxzt = '0' ");
		sql.setString(1, ysxdygnid);
		sql.setString(2, czztid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String sxdyjbjgid = ds.getString(0, "sxdyjbjgid");
		String ysxid = ds.getString(0, "sxid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		// 事项办理完结
		ProceedingHandler.finish(ysxdygnid, czztid, czr, czsj, czsm);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs, dbcs, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String nextByGnid(String ysxdygnid, String czztid,
			String sxmc, String sxdygnid, String czr, Date czsj, String czsm) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select sxid, czztid, fqsj, czcssx, czjgsx, sxdyjbjgid, ymcs, dbcs, gzrbz, lctwjbs from fw.proceeding where sxdygnid = ? and czztid = ? and sxzt = '0' ");
		sql.setString(1, ysxdygnid);
		sql.setString(2, czztid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String sxdyjbjgid = ds.getString(0, "sxdyjbjgid");
		String ysxid = ds.getString(0, "sxid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		Blob ymcs = (Blob) ds.getObject(0, "ymcs");
		ObjectInputStream ymcs_inputstream = new ObjectInputStream(ymcs.getBinaryStream());
		DataMap ymcs_dm = (DataMap) ymcs_inputstream.readObject();
		ymcs_inputstream.close();

		Blob dbcs = (Blob) ds.getObject(0, "dbcs");
		ObjectInputStream dbcs_inputstream = new ObjectInputStream(dbcs.getBinaryStream());
		DataMap dbcs_dm = (DataMap) dbcs_inputstream.readObject();
		dbcs_inputstream.close();

		// 事项办理完结
		ProceedingHandler.finish(ysxdygnid, czztid, czr, czsj, czsm);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs_dm, dbcs_dm, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String backBySxid(String ysxid, String sxmc, String sxdygnid,
			String sxdyjbjgid, String czr, Date czsj, DataMap ymcs,
			DataMap dbcs, String ywhtyy) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select czztid, fqsj, czcssx, czjgsx, gzrbz, lctwjbs from fw.proceeding where sxid = ? ");
		sql.setString(1, ysxid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String czztid = ds.getString(0, "czztid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		// 事项办理完结
		if (StringUtil.chkStrNull(ywhtyy)) {
			ywhtyy = "业务回退";
		}
		ProceedingHandler.nullify(ysxid, czr, czsj, ywhtyy);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs, dbcs, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String backBySxid(String ysxid, String sxmc, String sxdygnid,
			String czr, Date czsj, DataMap ymcs, DataMap dbcs, String ywhtyy) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select czztid, fqsj, czcssx, czjgsx, sxdyjbjgid, gzrbz, lctwjbs from fw.proceeding where sxid = ? ");
		sql.setString(1, ysxid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String czztid = ds.getString(0, "czztid");
		String sxdyjbjgid = ds.getString(0, "sxdyjbjgid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		// 事项办理完结
		if (StringUtil.chkStrNull(ywhtyy)) {
			ywhtyy = "业务回退";
		}
		ProceedingHandler.nullify(ysxid, czr, czsj, ywhtyy);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs, dbcs, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String backBySxid(String ysxid, String sxmc, String sxdygnid,
			String czr, Date czsj, String ywhtyy) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select czztid, fqsj, czcssx, czjgsx, sxdyjbjgid, ymcs, dbcs, gzrbz, lctwjbs from fw.proceeding where sxid = ? ");
		sql.setString(1, ysxid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String czztid = ds.getString(0, "czztid");
		String sxdyjbjgid = ds.getString(0, "sxdyjbjgid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		Blob ymcs = (Blob) ds.getObject(0, "ymcs");
		ObjectInputStream ymcs_inputstream = new ObjectInputStream(ymcs.getBinaryStream());
		DataMap ymcs_dm = (DataMap) ymcs_inputstream.readObject();
		ymcs_inputstream.close();

		Blob dbcs = (Blob) ds.getObject(0, "dbcs");
		ObjectInputStream dbcs_inputstream = new ObjectInputStream(dbcs.getBinaryStream());
		DataMap dbcs_dm = (DataMap) dbcs_inputstream.readObject();
		dbcs_inputstream.close();

		// 事项办理完结
		if (StringUtil.chkStrNull(ywhtyy)) {
			ywhtyy = "业务回退";
		}
		ProceedingHandler.nullify(ysxid, czr, czsj, ywhtyy);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs_dm, dbcs_dm, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String backByGnid(String ysxdygnid, String czztid,
			String sxmc, String sxdygnid, String sxdyjbjgid, String czr,
			Date czsj, DataMap ymcs, DataMap dbcs, String ywhtyy) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select sxid, fqsj, czcssx, czjgsx, gzrbz, lctwjbs from fw.proceeding where sxdygnid = ? and czztid = ? and sxzt = '0' ");
		sql.setString(1, ysxdygnid);
		sql.setString(2, czztid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		String ysxid = ds.getString(0, "sxid");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}
		// 事项办理完结
		if (StringUtil.chkStrNull(ywhtyy)) {
			ywhtyy = "业务回退";
		}
		ProceedingHandler.nullify(ysxdygnid, czztid, czr, czsj, ywhtyy);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs, dbcs, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String backByGnid(String ysxdygnid, String czztid,
			String sxmc, String sxdygnid, String czr, Date czsj, DataMap ymcs,
			DataMap dbcs, String ywhtyy) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select sxid, czztid, fqsj, czcssx, czjgsx, sxdyjbjgid, gzrbz, lctwjbs from fw.proceeding where sxdygnid = ? and czztid = ? and sxzt = '0' ");
		sql.setString(1, ysxdygnid);
		sql.setString(2, czztid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String sxdyjbjgid = ds.getString(0, "sxdyjbjgid");
		String ysxid = ds.getString(0, "sxid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		// 事项办理完结
		if (StringUtil.chkStrNull(ywhtyy)) {
			ywhtyy = "业务回退";
		}
		ProceedingHandler.nullify(ysxdygnid, czztid, czr, czsj, ywhtyy);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs, dbcs, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}

	/**
	 * 针对于一个业务有多个业务节点，需要生成多个业务待办事项的情况
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-18
	 * @since V1.0
	 */
	public static String backByGnid(String ysxdygnid, String czztid,
			String sxmc, String sxdygnid, String czr, Date czsj, String ywhtyy) throws Exception {
		// 查询原有事项信息
		Sql sql = new Sql();
		sql.setSql(" select sxid, czztid, fqsj, czcssx, czjgsx, sxdyjbjgid, ymcs, dbcs, gzrbz, lctwjbs from fw.proceeding where sxdygnid = ? and czztid = ? and sxzt = '0' ");
		sql.setString(1, ysxdygnid);
		sql.setString(2, czztid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			return "";// 由于待办事项只是辅助措施，不能影响业务的正常进行，所以此处直接返回空
		}
		String sxdyjbjgid = ds.getString(0, "sxdyjbjgid");
		String ysxid = ds.getString(0, "sxid");
		int czcssx = ds.getInt(0, "czcssx");
		int czjgsx = ds.getInt(0, "czjgsx");
		Date fqsj = ds.getDate(0, "fqsj");
		String gzrbz = ds.getString(0, "gzrbz");
		String lctwjbs = ds.getString(0, "lctwjbs");

		Date now = czsj;
		if (!fqsj.after(now)) {
			if ("1".equals(gzrbz)) {// 按照工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDateExceptHoliday(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			} else {// 非工作日的方式计算
				int dqxhts = (int) DateUtil.getDaysDiffBetweenTwoDate(fqsj, now);// 截至到目前的消耗天数
				czcssx = czcssx - dqxhts;
				czjgsx = czjgsx - dqxhts;
			}
		}

		Blob ymcs = (Blob) ds.getObject(0, "ymcs");
		ObjectInputStream ymcs_inputstream = new ObjectInputStream(ymcs.getBinaryStream());
		DataMap ymcs_dm = (DataMap) ymcs_inputstream.readObject();
		ymcs_inputstream.close();

		Blob dbcs = (Blob) ds.getObject(0, "dbcs");
		ObjectInputStream dbcs_inputstream = new ObjectInputStream(dbcs.getBinaryStream());
		DataMap dbcs_dm = (DataMap) dbcs_inputstream.readObject();
		dbcs_inputstream.close();

		// 事项办理完结
		if (StringUtil.chkStrNull(ywhtyy)) {
			ywhtyy = "业务回退";
		}
		ProceedingHandler.nullify(ysxdygnid, czztid, czr, czsj, ywhtyy);

		// 创建新的待办事项
		String xsxid = ProceedingHandler.create(sxmc, sxdygnid, sxdyjbjgid, czztid, czr, czsj, null, ymcs_dm, dbcs_dm, czcssx, czjgsx, "1".equals(gzrbz), lctwjbs);

		// 更新事项ID
		sql.setSql(" update fw.proceeding set sjsxid = ? where sxid = ? ");
		sql.setString(1, ysxid);
		sql.setString(2, xsxid);
		sql.executeUpdate();

		return xsxid;
	}
}
