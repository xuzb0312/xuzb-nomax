package com.grace.frame.debug.biz;

import java.io.ByteArrayOutputStream;
import java.sql.Clob;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.Printer;
import com.grace.frame.util.SeqUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.ueditor.UeditorUtil;
import com.grace.frame.workflow.Biz;

/**
 * 打印格式配置管理
 * 
 * @author yjc
 */
public class PrintConfigMngBiz extends Biz{
	/**
	 * 获取打印格式信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public final DataMap queryPrintConfigInfo(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		String gsmc = para.getString("gsmc");
		if (StringUtil.chkStrNull(gsmc)) {
			gsmc = "%";
		} else {
			gsmc = "%" + gsmc + "%";
		}

		sqlBF.setLength(0);
		sqlBF.append(" select a.gsid, a.gslxbh, a.gsmc, a.gsms, a.gszt, ");
		sqlBF.append("        wm_concat(b.jbjgid) pzdq ");
		sqlBF.append("   from fw.print_model a, ");
		sqlBF.append("        fw.print_config b ");
		sqlBF.append("  where a.gsid = b.gsid(+) ");
		sqlBF.append("    and (a.gslxbh like ? or a.gsmc like ?) ");
		sqlBF.append("  group by a.gsid, a.gslxbh, a.gsmc, a.gsms, a.gszt ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gsmc);
		this.sql.setString(2, gsmc);
		DataSet dsPrint = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsprint", dsPrint);
		return rdm;
	}

	/**
	 * 保存打印格式模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap savePrintModelAdd(final DataMap para) throws Exception {
		String gsid = para.getString("gsid", SeqUtil.getId("fw.sq_gsid"));// 打印格式ID
		String gslxbh = para.getString("gslxbh");// 格式类型编号
		String gsmc = para.getString("gsmc");// 格式名称
		String gsms = para.getString("gsms");// 格式描述
		String gsnr = para.getString("gsnr");// 格式内容
		String gszt = para.getString("gszt");// 格式状态
		StringBuffer sqlBF = new StringBuffer();

		if (StringUtil.chkStrNull(gsid)) {
			throw new BizException("打印格式ID为空");
		}
		if (StringUtil.chkStrNull(gslxbh)) {
			throw new BizException("格式类型编号为空");
		}
		if (StringUtil.chkStrNull(gsmc)) {
			throw new BizException("格式名称为空");
		}
		if (StringUtil.chkStrNull(gsnr)) {
			throw new BizException("格式内容为空");
		}
		if (StringUtil.chkStrNull(gszt)) {
			throw new BizException("格式状态为空");
		}

		gsnr = gsnr.trim();
		gsnr = UeditorUtil.dealEditorContentImgVideoFile(gsnr, this.getRequest());// 文本处理

		// 新增格式内容
		sqlBF.setLength(0);
		sqlBF.append(" insert into fw.print_model ");
		sqlBF.append("   (gsid, gslxbh, gsmc, gsms, gsnr, gszt) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, empty_clob(), ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gsid);
		this.sql.setString(2, gslxbh);
		this.sql.setString(3, gsmc);
		this.sql.setString(4, gsms);
		this.sql.setString(5, gszt);
		this.sql.executeUpdate();

		// colb数据写入
		sqlBF.setLength(0);
		sqlBF.append(" select gsnr ");
		sqlBF.append("   from fw.print_model ");
		sqlBF.append("  where gsid = ? ");
		sqlBF.append("    for update ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gsid);
		this.sql.executeUpdateClob(gsnr);

		return null;
	}

	/**
	 * 进入打印模板修改界面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap fwdPrintModelModify(final DataMap para) throws Exception {
		String gsid = para.getString("gsid");
		if (StringUtil.chkStrNull(gsid)) {
			throw new BizException("打印格式ID为空");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select gsid, gslxbh, gsmc, gsms, gsnr, gszt ");
		sqlBF.append("   from fw.print_model a ");
		sqlBF.append("  where a.gsid = ? ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gsid);
		DataSet ds = this.sql.executeQuery();
		if (ds.size() <= 0) {
			throw new BizException("打印格式ID=" + gsid + "在系统数据库中不存在");
		}

		// 转换格式内容
		Clob clob = ds.getClob(0, "gsnr");
		ds.put(0, "gsnr", StringUtil.Colb2String(clob));

		DataMap dm = new DataMap();
		dm.put("printinfo", ds.getRow(0));
		return dm;
	}

	/**
	 * 修改打印格式-先删后插
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap savePrintModelModify(final DataMap para) throws Exception {
		String gsid = para.getString("gsid");
		if (StringUtil.chkStrNull(gsid)) {
			throw new BizException("打印格式ID为空");
		}

		// 删除
		this.sql.setSql(" delete from fw.print_model where gsid = ? ");
		this.sql.setString(1, gsid);
		this.sql.executeUpdate();

		// 新增
		DataMap pdm = para.clone();
		pdm.put("gsid", gsid);// 格式ID传递而非自动生成
		this.savePrintModelAdd(pdm);

		return null;
	}

	/**
	 * 删除修改打印格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap deletePrintModel(final DataMap para) throws Exception {
		String gsid = para.getString("gsid");
		if (StringUtil.chkStrNull(gsid)) {
			throw new BizException("打印格式ID为空");
		}

		// 删除config
		this.sql.setSql(" delete from fw.print_config where gsid = ? ");
		this.sql.setString(1, gsid);
		this.sql.executeUpdate();

		// 删除model
		this.sql.setSql(" delete from fw.print_model where gsid = ? ");
		this.sql.setString(1, gsid);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 进入打印格式地区配置页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap fwdPrintConfigSet(final DataMap para) throws Exception {
		String gsid = para.getString("gsid");
		if (StringUtil.chkStrNull(gsid)) {
			throw new BizException("打印格式ID为空");
		}

		// 已经配置的地区
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.setLength(0);
		sqlBF.append(" select wm_concat(a.jbjgid) jbjgids, ? gsid ");
		sqlBF.append("   from fw.print_config a ");
		sqlBF.append("  where a.gsid = ? ");
		sqlBF.append("    and exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type b ");
		sqlBF.append("          where a.jbjgid = b.jbjgid ");
		sqlBF.append("            and b.dbid = ?) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gsid);
		this.sql.setString(2, gsid);
		this.sql.setString(3, GlobalVars.SYS_DBID);
		DataSet dsTemp = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("configinfo", dsTemp.getRow(0));
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
		StringBuffer sqlBF = new StringBuffer();

		// 获取dsJbjg-所有
		sqlBF.setLength(0);
		sqlBF.append(" select distinct a.jbjgid code, a.jbjgmc content ");
		sqlBF.append("   from fw.sys_agency a, fw.agency_biz_type b ");
		sqlBF.append("  where a.jbjgid = b.jbjgid ");
		sqlBF.append("    and b.dbid = ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsJbjg = this.sql.executeQuery();
		dsJbjg.sort("code");
		return dsJbjg;
	}

	/**
	 * 保存地区配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap savePrintConfigSet(final DataMap para) throws Exception {
		String gsid = para.getString("gsid");
		String jbjgids = para.getString("jbjgids");
		if (StringUtil.chkStrNull(gsid)) {
			throw new BizException("打印格式ID为空");
		}

		// 将本地区的配置信息全部删除
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" delete from fw.print_config a ");
		sqlBF.append("  where a.gsid = ? ");
		sqlBF.append("    and exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type b ");
		sqlBF.append("          where a.jbjgid = b.jbjgid ");
		sqlBF.append("            and b.dbid = ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gsid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 插入
		if (!StringUtil.chkStrNull(jbjgids)) {
			sqlBF.setLength(0);
			sqlBF.append(" insert into fw.print_config ");
			sqlBF.append("   (jbjgid, gsid) ");
			sqlBF.append("   select jbjgid, ? gsid ");
			sqlBF.append("     from fw.sys_agency b ");
			sqlBF.append("    where exists (select 'x' ");
			sqlBF.append("             from fw.agency_biz_type c ");
			sqlBF.append("            where b.jbjgid = c.jbjgid ");
			sqlBF.append("              and c.dbid = ?) ");
			sqlBF.append("      and "
					+ StringUtil.replaceC2QCQ("b.jbjgid", jbjgids));

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, gsid);
			this.sql.setString(2, GlobalVars.SYS_DBID);
			this.sql.executeUpdate();
		}

		return null;
	}

	/**
	 * 打印格式-查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap fwdPrintModelView(final DataMap para) throws Exception {
		String gsid = para.getString("gsid");
		if (StringUtil.chkStrNull(gsid)) {
			throw new BizException("打印格式ID为空");
		}

		Printer printer = new Printer(gsid);
		String htmlGs = printer.getHtmlContent();

		DataMap rdm = new DataMap();
		rdm.put("printer", htmlGs);
		return rdm;
	}

	/**
	 * 文件分隔符--打印格式的
	 */
	private static String PRINT_FILE_SEP = "##[\tpft\t]##";

	/**
	 * 下载系统的所有打印模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public final DataMap downloadAllPrintModel(final DataMap para) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		org.apache.tools.zip.ZipOutputStream zos = new org.apache.tools.zip.ZipOutputStream(baos);// 这个地方不用java.util.zip.ZipOutputStream,因为它不能处理中文名字
		zos.setEncoding("GBK");// 解决Linux压缩文件乱码问题
		// 文件-查询
		this.sql.setSql(" select gsid, gslxbh, gsmc, gsms, gsnr, gszt from fw.print_model ");
		DataSet dsModel = this.sql.executeQuery();

		// 循环处理
		for (int i = 0, n = dsModel.size(); i < n; i++) {
			String gsid = dsModel.getString(i, "gsid");
			String gslxbh = dsModel.getString(i, "gslxbh");
			String gsmc = dsModel.getString(i, "gsmc");
			String gsms = dsModel.getString(i, "gsms");
			String gszt = dsModel.getString(i, "gszt");

			// 查询配置地区
			this.sql.setSql(" select wm_concat(jbjgid) pzdq from fw.print_config a where gsid = ? ");
			this.sql.setString(1, gsid);
			DataSet dsTemp = this.sql.executeQuery();
			String pzdq = dsTemp.getString(0, "pzdq");

			String gsnr = StringUtil.Colb2String(dsModel.getClob(i, "gsnr"));
			gsnr = "格式ID:" + gsid + PrintConfigMngBiz.PRINT_FILE_SEP
					+ "格式类型编号:" + gslxbh + PrintConfigMngBiz.PRINT_FILE_SEP
					+ "格式名称:" + gsmc + PrintConfigMngBiz.PRINT_FILE_SEP
					+ "格式描述:" + gsms + PrintConfigMngBiz.PRINT_FILE_SEP
					+ "格式状态:" + gszt + PrintConfigMngBiz.PRINT_FILE_SEP
					+ "配置地区:" + pzdq + PrintConfigMngBiz.PRINT_FILE_SEP
					+ "格式内容:" + PrintConfigMngBiz.PRINT_FILE_SEP + gsnr;

			byte[] byteGsnr = gsnr.getBytes("UTF-8");
			String fileName = gsid + "_" + gslxbh + ".pft";
			ZipEntry ze = new ZipEntry(fileName);
			zos.putNextEntry(ze);
			zos.write(byteGsnr, 0, byteGsnr.length);
		}
		zos.close();
		byte[] zipBytes = baos.toByteArray();

		// 资源释放
		zos.flush();
		zos.close();
		baos.flush();
		baos.close();

		DataMap rdm = new DataMap();
		rdm.put("zipbyte", zipBytes);
		return rdm;
	}

	/**
	 * 下载系统的选中打印模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public final DataMap downloadSelectPrintModel(final DataMap para) throws Exception {
		String gsid = para.getString("gsid");
		if (StringUtil.chkStrNull(gsid)) {
			throw new BizException("打印格式ID为空");
		}

		// 文件-查询
		this.sql.setSql(" select gsid, gslxbh, gsmc, gsms, gsnr, gszt from fw.print_model where gsid = ? ");
		this.sql.setString(1, gsid);
		DataSet dsModel = this.sql.executeQuery();
		if (dsModel.size() <= 0) {
			throw new BizException("根据格式ID未能查询到打印格式信息");
		}
		String gslxbh = dsModel.getString(0, "gslxbh");
		String gsmc = dsModel.getString(0, "gsmc");
		String gsms = dsModel.getString(0, "gsms");
		String gszt = dsModel.getString(0, "gszt");
		String gsnr = StringUtil.Colb2String(dsModel.getClob(0, "gsnr"));

		// 查询配置地区
		this.sql.setSql(" select wm_concat(jbjgid) pzdq from fw.print_config a where gsid = ? ");
		this.sql.setString(1, gsid);
		DataSet dsTemp = this.sql.executeQuery();
		String pzdq = dsTemp.getString(0, "pzdq");

		gsnr = "格式ID:" + gsid + PrintConfigMngBiz.PRINT_FILE_SEP + "格式类型编号:"
				+ gslxbh + PrintConfigMngBiz.PRINT_FILE_SEP + "格式名称:" + gsmc
				+ PrintConfigMngBiz.PRINT_FILE_SEP + "格式描述:" + gsms
				+ PrintConfigMngBiz.PRINT_FILE_SEP + "格式状态:" + gszt
				+ PrintConfigMngBiz.PRINT_FILE_SEP + "配置地区:" + pzdq
				+ PrintConfigMngBiz.PRINT_FILE_SEP + "格式内容:"
				+ PrintConfigMngBiz.PRINT_FILE_SEP + gsnr;

		byte[] byteGsnr = gsnr.getBytes("UTF-8");

		// 文件名称
		String fileName = gsid + "_" + gslxbh + ".pft";

		DataMap rdm = new DataMap();
		rdm.put("btyegsnr", byteGsnr);
		rdm.put("filename", fileName);
		return rdm;
	}

	/**
	 * 文件上传-打印格式
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-17
	 * @since V1.0
	 */
	public final DataMap uploadPrintModelFile(final DataMap para) throws Exception {
		CommonsMultipartFile dygs = (CommonsMultipartFile) para.get("dygs");
		// 获取文件格式
		String fileName = dygs.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1)
			.toLowerCase();
		String gslxbh = "";
		if ("pft".equalsIgnoreCase(fileExt)) {
			String dygsnr = new String(dygs.getBytes(), "UTF-8");
			// 解析文件内容进行保存
			gslxbh = this.parseDygsFile(dygsnr);
		} else if ("zip".equalsIgnoreCase(fileExt)) {
			ZipInputStream zin = new ZipInputStream(dygs.getInputStream());
			java.util.zip.ZipEntry ze;
			int BUFFER = 32528;
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {// 目录则不进行继续检索
				} else {
					int count;
					byte data[] = new byte[BUFFER];
					ByteArrayOutputStream dest = new ByteArrayOutputStream();
					while ((count = zin.read(data, 0, BUFFER)) != -1) {
						dest.write(data, 0, count);
					}
					byte[] wjbtye = dest.toByteArray();
					dest.flush();
					dest.close();

					// 解析操作
					String dygsnr = new String(wjbtye, "UTF-8");
					this.parseDygsFile(dygsnr);
				}
			}
			zin.closeEntry();
		} else {
			throw new BizException("上传的文件类型，系统不支持解析。");
		}

		DataMap rdm = new DataMap();
		rdm.put("gslxbh", gslxbh);
		return rdm;
	}

	/**
	 * 文件解析操作
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-17
	 * @since V1.0
	 */
	private String parseDygsFile(String dygsnr) throws Exception {
		if (StringUtil.chkStrNull(dygsnr)) {
			throw new BizException("传入的文件内容为空。");
		}
		// 需要解析出的结果
		String gslxbh = "";
		String gsmc = "";
		String gsms = "";
		String gszt = "";
		String pzdq = "";
		String gsnr = "";
		int sepLength = PrintConfigMngBiz.PRINT_FILE_SEP.length();

		// 格式ID获取段
		int lnIndex = dygsnr.indexOf(PrintConfigMngBiz.PRINT_FILE_SEP);
		dygsnr = dygsnr.substring(lnIndex + sepLength);

		// 格式类型编号段
		lnIndex = dygsnr.indexOf(PrintConfigMngBiz.PRINT_FILE_SEP);
		String gslxbh_str = dygsnr.substring(0, lnIndex);
		gslxbh = gslxbh_str.replace("格式类型编号:", "");
		dygsnr = dygsnr.substring(lnIndex + sepLength);

		// 格式名称段
		lnIndex = dygsnr.indexOf(PrintConfigMngBiz.PRINT_FILE_SEP);
		String gsmc_str = dygsnr.substring(0, lnIndex);
		gsmc = gsmc_str.replace("格式名称:", "");
		dygsnr = dygsnr.substring(lnIndex + sepLength);

		// 格式名称段
		lnIndex = dygsnr.indexOf(PrintConfigMngBiz.PRINT_FILE_SEP);
		String gsms_str = dygsnr.substring(0, lnIndex);
		gsms = gsms_str.replace("格式描述:", "");
		dygsnr = dygsnr.substring(lnIndex + sepLength);

		// 格式名状态
		lnIndex = dygsnr.indexOf(PrintConfigMngBiz.PRINT_FILE_SEP);
		String gszt_str = dygsnr.substring(0, lnIndex);
		gszt = gszt_str.replace("格式状态:", "");
		dygsnr = dygsnr.substring(lnIndex + sepLength);

		// 配置地区
		lnIndex = dygsnr.indexOf(PrintConfigMngBiz.PRINT_FILE_SEP);
		String pzdq_str = dygsnr.substring(0, lnIndex);
		pzdq = pzdq_str.replace("配置地区:", "");
		dygsnr = dygsnr.substring(lnIndex + sepLength);

		// 格式内容
		lnIndex = dygsnr.indexOf(PrintConfigMngBiz.PRINT_FILE_SEP);
		gsnr = dygsnr.substring(lnIndex + sepLength);

		String gsid = SeqUtil.getId("fw.sq_gsid");

		// 重复数据先进行删除操作
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" delete from fw.print_config a ");
		sqlBF.append("  where exists (select 'x' ");
		sqlBF.append("           from fw.print_model b ");
		sqlBF.append("          where b.gslxbh = ? ");
		sqlBF.append("            and a.gsid = b.gsid) ");
		sqlBF.append("    and " + StringUtil.replaceC2QCQ("a.jbjgid", pzdq));
		sqlBF.append(" and exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type c ");
		sqlBF.append("          where a.jbjgid = c.jbjgid ");
		sqlBF.append("            and c.dbid = ?) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gslxbh);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 对于该格式类型编号，如果没有地区配置了，则删除
		sqlBF.setLength(0);
		sqlBF.append(" delete from fw.print_model a ");
		sqlBF.append("  where a.gslxbh = ? ");
		sqlBF.append("    and not exists ");
		sqlBF.append("  (select 'x' from fw.print_config b where a.gsid = b.gsid) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, gslxbh);
		this.sql.executeUpdate();

		// 保存打印格式
		DataMap pdm = new DataMap();
		pdm.put("gsid", gsid);
		pdm.put("gslxbh", gslxbh);
		pdm.put("gsmc", gsmc);
		pdm.put("gsnr", gsnr);
		pdm.put("gsms", gsms);
		pdm.put("gszt", gszt);
		this.savePrintModelAdd(pdm);

		// 进行地区配置
		pdm.clear();
		pdm.put("gsid", gsid);
		pdm.put("jbjgids", pzdq);
		this.savePrintConfigSet(pdm);

		// 数据返回
		return gslxbh;
	}
}
