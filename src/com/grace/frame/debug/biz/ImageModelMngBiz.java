package com.grace.frame.debug.biz;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.zip.ZipInputStream;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.tools.zip.ZipEntry;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.FileIOUtil;
import com.grace.frame.util.ImageMaker;
import com.grace.frame.util.SecUtil;
import com.grace.frame.util.SeqUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.workflow.Biz;

/**
 * 图片模板
 * 
 * @author yjc
 */
public class ImageModelMngBiz extends Biz{
	/**
	 * 查询【图片模板】信息
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public final DataMap queryImageModelInfo(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		String mbmc = para.getString("mbmc");
		if (StringUtil.chkStrNull(mbmc)) {
			mbmc = "%";
		} else {
			mbmc = "%" + mbmc + "%";
		}

		sqlBF.setLength(0);
		sqlBF.append(" select a.mbid, a.mbbh, a.mbmc, a.mbms, a.mbzt, ");
		sqlBF.append("        wm_concat(b.jbjgid) pzdq ");
		sqlBF.append("   from fw.image_model a, ");
		sqlBF.append("        fw.image_model_config b ");
		sqlBF.append("  where a.mbid = b.mbid(+) ");
		sqlBF.append("    and (a.mbbh like ? or a.mbmc like ?) ");
		sqlBF.append("  group by a.mbid, a.mbbh, a.mbmc, a.mbms, a.mbzt ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbmc);
		this.sql.setString(2, mbmc);
		DataSet dsImageModel = this.sql.executeQuery();

		// 排序操作
		dsImageModel.sort("mbbh");

		// 数据返回
		DataMap rdm = new DataMap();
		rdm.put("dsimagemodel", dsImageModel);
		return rdm;
	}

	/**
	 * 保存【图片模板】信息新增
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public final DataMap saveImageModelAdd(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		String mbbh = para.getTrimString("mbbh");
		String mbmc = para.getTrimString("mbmc");
		String mbms = para.getTrimString("mbms");
		CommonsMultipartFile mbnr = (CommonsMultipartFile) para.get("mbnr");

		// 判断为空
		if (StringUtil.chkStrNull(mbbh)) {
			throw new BizException("【模板编号】不允许为空");
		}
		if (StringUtil.chkStrNull(mbmc)) {
			throw new BizException("【模板名称】不允许为空");
		}
		if (null == mbnr) {
			throw new BizException("【模板内容】不允许为空");
		}

		// 获取Id
		String mbid = SeqUtil.getId("fw.sq_mbid");

		// 保存操作
		sqlBF.setLength(0);
		sqlBF.append(" insert into fw.image_model ");
		sqlBF.append("   (mbid, mbbh, mbmc, mbms, mbnr, ");
		sqlBF.append("    mbzt) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, empty_blob(), ");
		sqlBF.append("    '1') ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbid);
		this.sql.setString(2, mbbh);
		this.sql.setString(3, mbmc);
		this.sql.setString(4, mbms);
		this.sql.executeUpdate();

		// 图片保存
		sqlBF.setLength(0);
		sqlBF.append(" select mbnr ");
		sqlBF.append("   from fw.image_model ");
		sqlBF.append("  where mbid = ? ");
		sqlBF.append("    for update ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbid);
		DataSet dsImage = this.sql.executeQuery();
		if (dsImage.size() > 0) {
			Blob mbnr_blob = dsImage.getBlob(0, "mbnr");
			FileIOUtil.saveCommonsMultipartFileToBlob(mbnr, mbnr_blob);
		}

		DataMap rdm = new DataMap();
		rdm.put("mbbh", mbbh);
		return rdm;
	}

	/**
	 * 【图片模板】信息修改
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public final DataMap fwdImageModelModify(final DataMap para) throws Exception {
		String mbid = para.getTrimString("mbid");

		// 判断为空
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("【模板ID】不允许为空");
		}

		this.sql.setSql(" select mbid, mbbh, mbmc, mbms from fw.image_model where mbid = ? ");
		this.sql.setString(1, mbid);
		DataSet dsImageModel = this.sql.executeQuery();

		if (dsImageModel.size() <= 0) {
			throw new BizException("图片模板[模板ID:" + mbid + "]数据不存在！");
		}

		DataMap rdm = new DataMap();
		rdm.put("dmimagemodel", dsImageModel.getRow(0));
		return rdm;
	}

	/**
	 * 保存【图片模板】信息修改
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public final DataMap saveImageModelModify(final DataMap para) throws Exception {
		String mbid = para.getTrimString("mbid");
		String mbbh = para.getTrimString("mbbh");
		String mbmc = para.getTrimString("mbmc");
		String mbms = para.getTrimString("mbms");

		// 判断为空
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("【模板ID】不允许为空");
		}
		if (StringUtil.chkStrNull(mbbh)) {
			throw new BizException("【模板编号】不允许为空");
		}
		if (StringUtil.chkStrNull(mbmc)) {
			throw new BizException("【模板名称】不允许为空");
		}
		this.sql.setSql(" update fw.image_model set mbbh = ?, mbmc = ?, mbms = ? where mbid = ? ");
		this.sql.setString(1, mbbh);
		this.sql.setString(2, mbmc);
		this.sql.setString(3, mbms);
		this.sql.setString(4, mbid);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 【图片模板】信息删除
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public final DataMap saveImageModelDelete(final DataMap para) throws Exception {
		String mbid = para.getTrimString("mbid");

		// 判断为空
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("【模板ID】不允许为空");
		}

		this.sql.setSql(" delete from fw.image_model where mbid = ? ");
		this.sql.setString(1, mbid);
		this.sql.executeUpdate();

		this.sql.setSql(" delete from fw.image_model_config where mbid = ? ");
		this.sql.setString(1, mbid);
		this.sql.executeUpdate();

		this.sql.setSql(" delete from fw.image_model_detl where mbid = ? ");
		this.sql.setString(1, mbid);
		this.sql.executeUpdate();

		return null;
	}

	/**
	 * 进入图片格式地区配置页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	public final DataMap fwdImageModelConfigSet(final DataMap para) throws Exception {
		String mbid = para.getString("mbid");
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("模板ID为空");
		}

		// 已经配置的地区
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.setLength(0);
		sqlBF.append(" select wm_concat(a.jbjgid) jbjgids, ? mbid ");
		sqlBF.append("   from fw.image_model_config a ");
		sqlBF.append("  where a.mbid = ? ");
		sqlBF.append("    and exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type b ");
		sqlBF.append("          where a.jbjgid = b.jbjgid ");
		sqlBF.append("            and b.dbid = ?) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbid);
		this.sql.setString(2, mbid);
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
	public final DataMap saveImageModelConfigSet(final DataMap para) throws Exception {
		String mbid = para.getString("mbid");
		String jbjgids = para.getString("jbjgids");
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("模板ID为空");
		}

		// 将本地区的配置信息全部删除
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" delete from fw.image_model_config a ");
		sqlBF.append("  where a.mbid = ? ");
		sqlBF.append("    and exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type b ");
		sqlBF.append("          where a.jbjgid = b.jbjgid ");
		sqlBF.append("            and b.dbid = ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbid);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 插入
		if (!StringUtil.chkStrNull(jbjgids)) {
			sqlBF.setLength(0);
			sqlBF.append(" insert into fw.image_model_config ");
			sqlBF.append("   (jbjgid, mbid) ");
			sqlBF.append("   select jbjgid, ? mbid ");
			sqlBF.append("     from fw.sys_agency b ");
			sqlBF.append("    where exists (select 'x' ");
			sqlBF.append("             from fw.agency_biz_type c ");
			sqlBF.append("            where b.jbjgid = c.jbjgid ");
			sqlBF.append("              and c.dbid = ?) ");
			sqlBF.append("      and "
					+ StringUtil.replaceC2QCQ("b.jbjgid", jbjgids));

			this.sql.setSql(sqlBF.toString());
			this.sql.setString(1, mbid);
			this.sql.setString(2, GlobalVars.SYS_DBID);
			this.sql.executeUpdate();
		}

		return null;
	}

	/**
	 * 模板信息
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-2
	 * @since V1.0
	 */
	public final DataMap getImageModelOptions(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();

		String mbid = para.getString("mbid");
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("模板ID为空");
		}

		this.sql.setSql(" select mbnr from fw.image_model where mbid = ? ");
		this.sql.setString(1, mbid);
		DataSet dsImage = this.sql.executeQuery();
		if (dsImage.size() <= 0) {
			throw new BizException("模板ID" + mbid + "的模板在系统中不存在");
		}
		Blob mbnr = dsImage.getBlob(0, "mbnr");

		sqlBF.setLength(0);
		sqlBF.append(" select xmbh, xmmc, xsys, ztdx, xmkd, ");
		sqlBF.append("        xmgd, xmdj, xmzj, ctbz, ztlx, ");
		sqlBF.append("        xmxh ");
		sqlBF.append("   from fw.image_model_detl ");
		sqlBF.append("  where mbid = ? ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbid);
		DataSet dsDetl = this.sql.executeQuery();

		// 获取图片的base64
		int zbnrLen = (new BigDecimal(mbnr.length())).intValue();
		byte[] imgByte = mbnr.getBytes(1, zbnrLen);
		String imgType = "image/png";
		try {
			MagicMatch match = Magic.getMagicMatch(imgByte);
			imgType = match.getMimeType();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String imgBase64 = "data:" + imgType + ";base64,"
				+ SecUtil.base64Encode(imgByte);

		DataMap items = new DataMap();
		for (int i = 0, n = dsDetl.size(); i < n; i++) {
			String xmbh = dsDetl.getString(i, "xmbh");
			items.put(xmbh, dsDetl.getRow(i));
		}

		DataMap rdm = new DataMap();
		rdm.put("imgurl", imgBase64);
		rdm.put("items", items);
		return rdm;
	}

	/**
	 * 保存设计
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-2
	 * @since V1.0
	 */
	public final DataMap saveImageModelDesign(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		String items = para.getString("items");
		String mbid = para.getString("mbid");
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("模板ID为空");
		}
		DataMap dmItems = DataMap.fromObject(items);

		// 清空原有设计
		this.sql.setSql(" delete from fw.image_model_detl where mbid = ? ");
		this.sql.setString(1, mbid);
		this.sql.executeUpdate();

		// 插入设计
		sqlBF.setLength(0);
		sqlBF.append(" insert into fw.image_model_detl ");
		sqlBF.append("   (mbid, xmbh, xmmc, xsys, ztdx, ");
		sqlBF.append("    xmkd, xmgd, xmdj, xmzj, ctbz, ");
		sqlBF.append("    ztlx, xmxh) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?) ");
		this.sql.setSql(sqlBF.toString());
		Object[] keys = dmItems.keySet().toArray();
		for (int i = 0, n = keys.length; i < n; i++) {
			String key = (String) keys[i];
			DataMap dmOne = dmItems.getDataMap(key);

			String xmbh = dmOne.getString("xmbh");
			String xmmc = dmOne.getString("xmmc");
			String xsys = dmOne.getString("xsys");
			int ztdx = dmOne.getInt("ztdx");
			int xmkd = dmOne.getInt("xmkd");
			int xmgd = dmOne.getInt("xmgd");
			int xmdj = dmOne.getInt("xmdj");
			int xmzj = dmOne.getInt("xmzj");
			String ctbz = dmOne.getString("ctbz");
			String ztlx = dmOne.getString("ztlx");
			int xmxh = dmOne.getInt("xmxh");

			this.sql.setString(1, mbid);
			this.sql.setString(2, xmbh);
			this.sql.setString(3, xmmc);
			this.sql.setString(4, xsys);
			this.sql.setInt(5, ztdx);

			this.sql.setInt(6, xmkd);
			this.sql.setInt(7, xmgd);
			this.sql.setInt(8, xmdj);
			this.sql.setInt(9, xmzj);
			this.sql.setString(10, ctbz);

			this.sql.setString(11, ztlx);
			this.sql.setInt(12, xmxh);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		return null;
	}

	/**
	 * 下载系统的选中图片模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public final DataMap downloadSelectImageModel(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		String mbid = para.getString("mbid");
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("模板ID为空");
		}

		// 文件-查询
		this.sql.setSql(" select mbbh, mbmc, mbms, mbnr, mbzt from fw.image_model where mbid = ? ");
		this.sql.setString(1, mbid);
		DataSet dsModel = this.sql.executeQuery();
		if (dsModel.size() <= 0) {
			throw new BizException("根据模板ID未能查询到信息");
		}
		String mbbh = dsModel.getString(0, "mbbh");
		String mbmc = dsModel.getString(0, "mbmc");
		String mbms = dsModel.getString(0, "mbms");
		String mbzt = dsModel.getString(0, "mbzt");
		Blob mbnr_blob = dsModel.getBlob(0, "mbnr");
		String mbnr = FileIOUtil.getImageStrByBlob(mbnr_blob);

		// 查询配置地区
		this.sql.setSql(" select wm_concat(jbjgid) pzdq from fw.image_model_config a where mbid = ? ");
		this.sql.setString(1, mbid);
		DataSet dsTemp = this.sql.executeQuery();
		String pzdq = dsTemp.getString(0, "pzdq");

		sqlBF.setLength(0);
		sqlBF.append(" select xmbh, xmmc, xsys, ztdx, xmkd, ");
		sqlBF.append("        xmgd, xmdj, xmzj, ctbz, ztlx, ");
		sqlBF.append("        xmxh ");
		sqlBF.append("   from fw.image_model_detl ");
		sqlBF.append("  where mbid = ? ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbid);
		DataSet dsDetl = this.sql.executeQuery();

		DataMap dmModelInfo = new DataMap();
		dmModelInfo.put("mbid", mbid);
		dmModelInfo.put("mbbh", mbbh);
		dmModelInfo.put("mbmc", mbmc);
		dmModelInfo.put("mbms", mbms);
		dmModelInfo.put("mbzt", mbzt);
		dmModelInfo.put("mbnr", mbnr);
		dmModelInfo.put("pzdq", pzdq);
		dmModelInfo.put("dsdetl", dsDetl);
		String mbInfoJson = dmModelInfo.toJsonString();

		byte[] byteMbInfo = mbInfoJson.getBytes("UTF-8");

		// 文件名称
		String fileName = mbid + "_" + mbbh + ".ift";

		DataMap rdm = new DataMap();
		rdm.put("bytembinfo", byteMbInfo);
		rdm.put("filename", fileName);
		return rdm;
	}

	/**
	 * 下载系统的所有打印模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public final DataMap downloadAllImageModel(final DataMap para) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		org.apache.tools.zip.ZipOutputStream zos = new org.apache.tools.zip.ZipOutputStream(baos);// 这个地方不用java.util.zip.ZipOutputStream,因为它不能处理中文名字
		zos.setEncoding("GBK");// 解决Linux压缩文件乱码问题
		// 文件-查询
		this.sql.setSql(" select mbid from fw.image_model ");
		DataSet dsModel = this.sql.executeQuery();

		// 循环处理
		for (int i = 0, n = dsModel.size(); i < n; i++) {
			DataMap rdmOne = this.downloadSelectImageModel(dsModel.get(i));
			byte[] byteGsnr = (byte[]) rdmOne.get("bytembinfo");
			String fileName = rdmOne.getString("filename");
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
	 * 文件上传-图片模板
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-17
	 * @since V1.0
	 */
	public final DataMap uploadImageModelFile(final DataMap para) throws Exception {
		CommonsMultipartFile tpmb = (CommonsMultipartFile) para.get("tpmb");
		// 获取文件格式
		String fileName = tpmb.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1)
			.toLowerCase();
		String mbbh = "";
		if ("ift".equalsIgnoreCase(fileExt)) {
			String tpmbnr = new String(tpmb.getBytes(), "UTF-8");
			// 解析文件内容进行保存
			mbbh = this.parseTpmbFile(tpmbnr);
		} else if ("zip".equalsIgnoreCase(fileExt)) {
			ZipInputStream zin = new ZipInputStream(tpmb.getInputStream());
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
					this.parseTpmbFile(dygsnr);
				}
			}
			zin.closeEntry();
		} else {
			throw new BizException("上传的文件类型，系统不支持解析。");
		}

		DataMap rdm = new DataMap();
		rdm.put("mbbh", mbbh);
		return rdm;
	}

	/**
	 * 文件解析操作
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-17
	 * @since V1.0
	 */
	private String parseTpmbFile(String tpmbnr) throws Exception {
		if (StringUtil.chkStrNull(tpmbnr)) {
			throw new BizException("传入的文件内容为空。");
		}
		DataMap para = DataMap.fromObject(tpmbnr);

		// 需要解析出的结果
		String mbbh = para.getString("mbbh");
		String mbmc = para.getString("mbmc");
		String mbms = para.getString("mbms");
		String mbzt = para.getString("mbzt");
		String pzdq = para.getString("pzdq");
		DataSet dsDetl = para.getDataSet("dsdetl");
		String mbnr = para.getString("mbnr");
		String mbid = SeqUtil.getId("fw.sq_mbid");

		// 重复数据先进行删除操作
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" delete from fw.image_model_config a ");
		sqlBF.append("  where exists (select 'x' ");
		sqlBF.append("           from fw.image_model b ");
		sqlBF.append("          where b.mbbh = ? ");
		sqlBF.append("            and a.mbid = b.mbid) ");
		sqlBF.append("    and " + StringUtil.replaceC2QCQ("a.jbjgid", pzdq));
		sqlBF.append(" and exists (select 'x' ");
		sqlBF.append("           from fw.agency_biz_type c ");
		sqlBF.append("          where a.jbjgid = c.jbjgid ");
		sqlBF.append("            and c.dbid = ?) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbbh);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		this.sql.executeUpdate();

		// 对于该格式类型编号，如果没有地区配置了，则删除
		sqlBF.setLength(0);
		sqlBF.append(" delete from fw.image_model a ");
		sqlBF.append("  where a.mbbh = ? ");
		sqlBF.append("    and not exists ");
		sqlBF.append("  (select 'x' from fw.image_model_config b where a.mbid = b.mbid) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbbh);
		this.sql.executeUpdate();

		sqlBF.setLength(0);
		sqlBF.append(" delete from fw.image_model_detl a ");
		sqlBF.append("  where not exists (select 'x' from fw.image_model b where a.mbid = b.mbid) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.executeUpdate();

		// 保存打印格式
		sqlBF.setLength(0);
		sqlBF.append(" insert into fw.image_model ");
		sqlBF.append("   (mbid, mbbh, mbmc, mbms, mbnr, ");
		sqlBF.append("    mbzt) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, empty_blob(), ");
		sqlBF.append("    ?) ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbid);
		this.sql.setString(2, mbbh);
		this.sql.setString(3, mbmc);
		this.sql.setString(4, mbms);
		this.sql.setString(5, mbzt);
		this.sql.executeUpdate();

		// 图片保存
		sqlBF.setLength(0);
		sqlBF.append(" select mbnr ");
		sqlBF.append("   from fw.image_model ");
		sqlBF.append("  where mbid = ? ");
		sqlBF.append("    for update ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, mbid);
		DataSet dsImage = this.sql.executeQuery();
		if (dsImage.size() > 0) {
			Blob mbnr_blob = dsImage.getBlob(0, "mbnr");
			FileIOUtil.saveByteToBlob(SecUtil.base64Decode(mbnr), mbnr_blob);
		}

		// 插入设计
		sqlBF.setLength(0);
		sqlBF.append(" insert into fw.image_model_detl ");
		sqlBF.append("   (mbid, xmbh, xmmc, xsys, ztdx, ");
		sqlBF.append("    xmkd, xmgd, xmdj, xmzj, ctbz, ");
		sqlBF.append("    ztlx, xmxh) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?, ?, ?, ?, ");
		sqlBF.append("    ?, ?) ");
		this.sql.setSql(sqlBF.toString());
		for (int i = 0, n = dsDetl.size(); i < n; i++) {
			DataMap dmOne = dsDetl.get(i);

			String xmbh = dmOne.getString("xmbh");
			String xmmc = dmOne.getString("xmmc");
			String xsys = dmOne.getString("xsys");
			int ztdx = dmOne.getInt("ztdx");
			int xmkd = dmOne.getInt("xmkd");
			int xmgd = dmOne.getInt("xmgd");
			int xmdj = dmOne.getInt("xmdj");
			int xmzj = dmOne.getInt("xmzj");
			String ctbz = dmOne.getString("ctbz");
			String ztlx = dmOne.getString("ztlx");
			int xmxh = dmOne.getInt("xmxh");

			this.sql.setString(1, mbid);
			this.sql.setString(2, xmbh);
			this.sql.setString(3, xmmc);
			this.sql.setString(4, xsys);
			this.sql.setInt(5, ztdx);

			this.sql.setInt(6, xmkd);
			this.sql.setInt(7, xmgd);
			this.sql.setInt(8, xmdj);
			this.sql.setInt(9, xmzj);
			this.sql.setString(10, ctbz);

			this.sql.setString(11, ztlx);
			this.sql.setInt(12, xmxh);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		// 进行地区配置
		DataMap pdm = new DataMap();
		pdm.put("mbid", mbid);
		pdm.put("jbjgids", pzdq);
		this.saveImageModelConfigSet(pdm);

		// 数据返回
		return mbbh;
	}

	/**
	 * 图片模板查看
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-5
	 * @since V1.0
	 */
	public final DataMap fwdImageModelView(final DataMap para) throws Exception {
		String mbid = para.getString("mbid");
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("模板ID为空");
		}

		// 图片生成
		ImageMaker im = new ImageMaker(mbid);

		// 生成事例数据
		this.sql.setSql(" select xmbh, xmmc from fw.image_model_detl where mbid = ? ");
		this.sql.setString(1, mbid);
		DataSet dsDetl = this.sql.executeQuery();
		for (int i = 0, n = dsDetl.size(); i < n; i++) {
			String xmbh = dsDetl.getString(i, "xmbh");
			String xmmc = dsDetl.getString(i, "xmmc");
			im.putPara(xmbh, xmmc + "【事例】");
		}

		DataMap rdm = new DataMap();
		rdm.put("printer", im.getPrintHtml(true));
		return rdm;
	}

	/**
	 * 图片模板查看
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-5
	 * @since V1.0
	 */
	public final DataMap fwdImageModelView4PDF(final DataMap para) throws Exception {
		String mbid = para.getString("mbid");
		if (StringUtil.chkStrNull(mbid)) {
			throw new BizException("模板ID为空");
		}

		// 图片生成
		ImageMaker im = new ImageMaker(mbid);

		// 生成事例数据
		this.sql.setSql(" select xmbh, xmmc from fw.image_model_detl where mbid = ? ");
		this.sql.setString(1, mbid);
		DataSet dsDetl = this.sql.executeQuery();
		for (int i = 0, n = dsDetl.size(); i < n; i++) {
			String xmbh = dsDetl.getString(i, "xmbh");
			String xmmc = dsDetl.getString(i, "xmmc");
			im.putPara(xmbh, xmmc + "【事例】");
		}

		// pdf查看
		FileIOUtil.writeImg2PdfToResponse("模板查看", im.create(true), this.getResponse());
		return null;
	}
}
