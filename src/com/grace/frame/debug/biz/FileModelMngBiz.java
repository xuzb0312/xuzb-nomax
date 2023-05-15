package com.grace.frame.debug.biz;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.urm.biz.StdUserMngBiz;
import com.grace.frame.util.*;
import com.grace.frame.workflow.Biz;
import org.apache.tools.zip.ZipEntry;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;

/**
 * 文件模板管理
 * 
 * @author yjc
 */
public class FileModelMngBiz extends Biz{

	/**
	 * 查询文件模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public final DataMap queryFileModel(final DataMap para) throws Exception {
		StringBuffer sqlBF = new StringBuffer();
		String wjbs = para.getString("wjbs");
		String wjmc = para.getString("wjmc");
		String wjgs = para.getString("wjgs");

		if (StringUtil.chkStrNull(wjbs)) {
			wjbs = "%";
		} else {
			wjbs = "%" + wjbs + "%";
		}
		if (StringUtil.chkStrNull(wjmc)) {
			wjmc = "%";
		} else {
			wjmc = "%" + wjmc + "%";
		}
		if (StringUtil.chkStrNull(wjgs)) {
			wjgs = "%";
		} else {
			wjgs = "%" + wjgs + "%";
		}

		sqlBF.setLength(0);
		sqlBF.append(" select wjbs, wjmc, bz, wjgs, to_char(cjsj, 'yyyymmddhh24miss') cjsj ");
		sqlBF.append("   from fw.file_model ");
		sqlBF.append("  where dbid = ? ");
		sqlBF.append("    and wjbs like ? ");
		sqlBF.append("    and wjmc like ? ");
		sqlBF.append("    and wjgs like ? ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, wjbs);
		this.sql.setString(3, wjmc);
		this.sql.setString(4, wjgs);

		DataSet dsFile = this.sql.executeQuery();

		DataMap rdm = new DataMap();
		rdm.put("dsfile", dsFile);
		return rdm;
	}

	/**
	 * 删除文件模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public final DataMap deleteFileModel(final DataMap para) throws Exception {
		DataSet dsFile = para.getDataSet("gridFile");

		// 文件删除
		this.sql.setSql(" delete from fw.file_model where dbid = ? and wjbs = ? ");
		for (int i = 0, n = dsFile.size(); i < n; i++) {
			String wjbs = dsFile.getString(i, "wjbs");

			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, wjbs);
			this.sql.addBatch();
		}
		this.sql.executeBatch();

		return null;
	}

	/**
	 * 文件修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	public final DataMap saveFileModelModify(final DataMap para) throws Exception {
		CommonsMultipartFile file = (CommonsMultipartFile) para.get("wjnr");
		String wjbs = para.getString("wjbs");
		if (StringUtil.chkStrNull(wjbs)) {
			throw new BizException("文件标识为空。");
		}

		if (null != file) {// 修改文件了，直接删除后插入
			this.sql.setSql(" delete from fw.file_model where dbid = ? and wjbs = ? ");
			this.sql.setString(1, GlobalVars.SYS_DBID);
			this.sql.setString(2, wjbs);
			this.sql.executeUpdate();

			// 然后新增
			this.saveFileModelAdd(para);
		} else {
			// 文件没有更改，则进行信息update
			String wjmc = para.getString("wjmc");
			String bz = para.getString("bz");
			DataSet dsImgCs = para.getDataSet("gridPrintImg");
			StringBuffer dytpcsBF = new StringBuffer();

			if (StringUtil.chkStrNull(wjmc)) {
				throw new BizException("文件名称为空。");
			}

			// 打印图片参数的处理-string
			if (null != dsImgCs && dsImgCs.size() > 0) {
				for (int i = 0, n = dsImgCs.size(); i < n; i++) {
					String csmc = dsImgCs.getString(i, "csmc");
					if (StringUtil.chkStrNull(csmc)) {
						throw new BizException("参数名称不允许为空");
					}
					int tpwz_x = dsImgCs.getInt(i, "tpwz_x");
					int tpwz_y = dsImgCs.getInt(i, "tpwz_y");
					int tpsf = dsImgCs.getInt(i, "tpsf");
					if (tpsf <= 0) {
						throw new BizException("图片缩放比例必须大于0");
					}
					dytpcsBF.append(csmc)
						.append(":")
						.append(tpwz_x)
						.append(":")
						.append(tpwz_y)
						.append(":")
						.append(tpsf)
						.append(",");
				}
				dytpcsBF.setLength(dytpcsBF.length() - 1);
			}

			this.sql.setSql(" update fw.file_model set wjmc = ?, bz = ?, dytpcs = ? where dbid = ? and wjbs = ? ");
			this.sql.setString(1, wjmc);
			this.sql.setString(2, bz);
			this.sql.setString(3, dytpcsBF.toString());
			this.sql.setString(4, GlobalVars.SYS_DBID);
			this.sql.setString(5, wjbs);
			this.sql.executeUpdate();
		}

		return null;
	}

	/**
	 * 保存文件新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public final DataMap saveFileModelAdd(final DataMap para) throws Exception {
		String wjbs = para.getString("wjbs");
		String wjmc = para.getString("wjmc");
		String bz = para.getString("bz");
		CommonsMultipartFile file = (CommonsMultipartFile) para.get("wjnr");
		DataSet dsImgCs = para.getDataSet("gridPrintImg");
		StringBuffer dytpcsBF = new StringBuffer();

		// 判断
		if (StringUtil.chkStrNull(wjbs)) {
			throw new BizException("文件标识为空。");
		}
		if (StringUtil.chkStrNull(wjmc)) {
			throw new BizException("文件名称为空。");
		}
		if (null == file) {
			throw new BizException("文件为空。");
		}
		// 打印图片参数的处理-string
		if (null != dsImgCs && dsImgCs.size() > 0) {
			for (int i = 0, n = dsImgCs.size(); i < n; i++) {
				String csmc = dsImgCs.getString(i, "csmc");
				if (StringUtil.chkStrNull(csmc)) {
					throw new BizException("参数名称不允许为空");
				}
				int tpwz_x = dsImgCs.getInt(i, "tpwz_x");
				int tpwz_y = dsImgCs.getInt(i, "tpwz_y");
				int tpsf = dsImgCs.getInt(i, "tpsf");
				if (tpsf <= 0) {
					throw new BizException("图片缩放比例必须大于0");
				}
				dytpcsBF.append(csmc)
					.append(":")
					.append(tpwz_x)
					.append(":")
					.append(tpwz_y)
					.append(":")
					.append(tpsf)
					.append(",");
			}
			dytpcsBF.setLength(dytpcsBF.length() - 1);
		}

		// 是否已经存在
		this.sql.setSql(" select wjbs from fw.file_model where wjbs = ? and dbid = ? ");
		this.sql.setString(1, wjbs);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该文件标识在数据库中已经存在。");
		}

		// 获取文件格式
		String fileName = file.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1)
			.toLowerCase();

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.file_model ");
		sqlBF.append("   (dbid, wjbs, wjmc, wjnr, bz, cjsj, wjgs, dytpcs) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, empty_blob(), ?, sysdate, ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, wjbs);
		this.sql.setString(3, wjmc);
		this.sql.setString(4, bz);
		this.sql.setString(5, fileExt);

		this.sql.setString(6, dytpcsBF.toString());
		this.sql.executeUpdate();

		// 保存文件内容
		this.sql.setSql(" select wjnr from fw.file_model where dbid = ? and wjbs = ? for update ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, wjbs);
		dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			Blob blob = (Blob) dsTemp.getBlob(0, "wjnr");
			FileIOUtil.saveCommonsMultipartFileToBlob(file, blob);
		}

		return null;
	}


	/**
	 * 保存文件新增并且将数据导入数据库
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public final DataMap saveFileModelAdd1(final DataMap para) throws Exception {
		String wjbs = para.getString("wjbs");
		String wjmc = para.getString("wjmc");
		String bz = para.getString("bz");
		CommonsMultipartFile file = (CommonsMultipartFile) para.get("wjnr");
		DataSet dsImgCs = para.getDataSet("gridPrintImg");
		StringBuffer dytpcsBF = new StringBuffer();

		// 判断
		if (StringUtil.chkStrNull(wjbs)) {
			throw new BizException("文件标识为空。");
		}
		if (StringUtil.chkStrNull(wjmc)) {
			throw new BizException("文件名称为空。");
		}
		if (null == file) {
			throw new BizException("文件为空。");
		}
		// 打印图片参数的处理-string
		if (null != dsImgCs && dsImgCs.size() > 0) {
			for (int i = 0, n = dsImgCs.size(); i < n; i++) {
				String csmc = dsImgCs.getString(i, "csmc");
				if (StringUtil.chkStrNull(csmc)) {
					throw new BizException("参数名称不允许为空");
				}
				int tpwz_x = dsImgCs.getInt(i, "tpwz_x");
				int tpwz_y = dsImgCs.getInt(i, "tpwz_y");
				int tpsf = dsImgCs.getInt(i, "tpsf");
				if (tpsf <= 0) {
					throw new BizException("图片缩放比例必须大于0");
				}
				dytpcsBF.append(csmc)
						.append(":")
						.append(tpwz_x)
						.append(":")
						.append(tpwz_y)
						.append(":")
						.append(tpsf)
						.append(",");
			}
			dytpcsBF.setLength(dytpcsBF.length() - 1);
		}

		// 是否已经存在
		this.sql.setSql(" select wjbs from fw.file_model where wjbs = ? and dbid = ? ");
		this.sql.setString(1, wjbs);
		this.sql.setString(2, GlobalVars.SYS_DBID);
		DataSet dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			throw new BizException("该文件标识在数据库中已经存在。");
		}

		// 获取文件格式
		String fileName = file.getOriginalFilename();
		String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1)
				.toLowerCase();

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.file_model ");
		sqlBF.append("   (dbid, wjbs, wjmc, wjnr, bz, cjsj, wjgs, dytpcs) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, ?, empty_blob(), ?, sysdate, ?, ?) ");

		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, wjbs);
		this.sql.setString(3, wjmc);
		this.sql.setString(4, bz);
		this.sql.setString(5, fileExt);

		this.sql.setString(6, dytpcsBF.toString());
		this.sql.executeUpdate();

		// 保存文件内容
		this.sql.setSql(" select wjnr from fw.file_model where dbid = ? and wjbs = ? for update ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, wjbs);
		dsTemp = this.sql.executeQuery();
		if (dsTemp.size() > 0) {
			Blob blob = (Blob) dsTemp.getBlob(0, "wjnr");
			FileIOUtil.saveCommonsMultipartFileToBlob(file, blob);
		}

		//将文件添加进入数据库
		//首先读取excel中的数据
		ExcelUtil excelUtil = new ExcelUtil();
		DataSet tableInfo = new DataSet();
		DataSet set = excelUtil.parseExcel2DataSet(file, tableInfo);
		//将每一行的数据都读出来
		for (int i = 0; i < set.size(); i++) {
			DataMap paras = new DataMap();
			paras.add("xm",set.getString(i,"考生姓名"));
			paras.add("xb",set.getString(i,"性别"));
			paras.add("yxzjlx",set.getString(i,"证件类型"));
			paras.add("yxzjhm",set.getString(i,"证件号码"));
			paras.add("nl",String.valueOf(set.getInt(i,"年龄")));
			paras.add("lxdh",String.valueOf(set.getInt(i,"联系电话")));
			paras.add("sjhm",String.valueOf(set.getInt(i,"手机号码")));
			paras.add("jtzz",set.getString(i,"家庭住址"));
			paras.add("bzje",String.valueOf(set.getDouble(i,"补助金额")));
//			paras.add("rzrq",String.valueOf(set.getDate(i,"入职日期","yyyy-MM-dd")));
			paras.add("rzrq",set.getDateToString(i,"入职日期","yyyy-MM-dd"));
			StdUserMngBiz stdUserMngBiz = new StdUserMngBiz();
			stdUserMngBiz.saveStdUserAdd(paras);

		}


		return null;
	}


	/**
	 * 文件模板查看页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-16
	 * @since V1.0
	 */
	public final DataMap fwdFileModelView(final DataMap para) throws Exception {
		String wjbs = para.getString("wjbs");
		HttpServletResponse response = (HttpServletResponse) para.get("response");
		wjbs = new String(wjbs.getBytes("ISO8859-1"), "UTF-8");// 解决url中文乱码问题。

		if (StringUtil.chkStrNull(wjbs)) {
			throw new AppException("传入的文件标识为空。");
		}

		// 获取文件内容
		Sql sql = new Sql();
		sql.setSql(" select wjmc, wjgs, wjnr from fw.file_model where dbid = ? and wjbs = ? ");
		sql.setString(1, GlobalVars.SYS_DBID);
		sql.setString(2, wjbs);
		DataSet dsTemp = sql.executeQuery();

		if (dsTemp.size() <= 0) {
			throw new AppException("数据库中没有DBID=" + GlobalVars.SYS_DBID
					+ ",WJBS=" + wjbs + "的系统文件。");
		}
		Blob file = dsTemp.getBlob(0, "wjnr");
		String wjmc = dsTemp.getString(0, "wjmc");
		String wjgs = dsTemp.getString(0, "wjgs");

		// 把文件写到前台下载
		if ("pdf".equalsIgnoreCase(wjgs)) {
			FileIOUtil.writeBlobToResponse(file, wjmc + "." + wjgs, response);
		} else if ("txt".equalsIgnoreCase(wjgs)) {// 文本格式
			// 获取到文件内容；
			String txtContent = StringUtil.bolb2String(file);
			FileIOUtil.writeTxt2PdfToResponse(wjmc, txtContent, response);
		} else if ("jpg,jpeg,png,bmp".indexOf(wjgs) >= 0) {
			// 图片
			BufferedImage image = ImageIO.read(file.getBinaryStream());
			FileIOUtil.writeImg2PdfToResponse(wjmc, image, response);
		} else {
			FileIOUtil.writeBlobToResponse(file, wjmc + "." + wjgs, response);
		}
		return null;
	}

	/**
	 * 下载系统的所有文件
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public final DataMap downloadAllFileModel(final DataMap para) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		org.apache.tools.zip.ZipOutputStream zos = new org.apache.tools.zip.ZipOutputStream(baos);// 这个地方不用java.util.zip.ZipOutputStream,因为它不能处理中文名字
		zos.setEncoding("GBK");// 解决Linux压缩文件乱码问题
		// 文件-查询
		this.sql.setSql(" select wjmc, wjgs, wjnr from fw.file_model where dbid = ? ");
		this.sql.setString(1, GlobalVars.SYS_DBID);
		DataSet dsTemp = this.sql.executeQuery();

		// 循环处理
		for (int i = 0, n = dsTemp.size(); i < n; i++) {
			String wjmc = dsTemp.getString(i, "wjmc");
			String wjgs = dsTemp.getString(i, "wjgs");
			Blob wjnr = (Blob) dsTemp.getObject(i, "wjnr");
			int zbnrLen = (new BigDecimal(wjnr.length())).intValue();
			byte[] byteWjnr = wjnr.getBytes(1, zbnrLen);
			String fileName = wjmc + "." + wjgs;
			ZipEntry ze = new ZipEntry(fileName);
			zos.putNextEntry(ze);
			zos.write(byteWjnr, 0, byteWjnr.length);
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
	 * 文件修改--进入页面
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	public final DataMap fwdFileModelModify(final DataMap para) throws Exception {
		String wjbs = para.getString("wjbs");
		if (StringUtil.chkStrNull(wjbs)) {
			throw new AppException("传入的文件标识为空。");
		}

		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select wjbs, wjmc, wjgs, bz, to_char(cjsj, 'yyyymmddhh24miss') cjsj, dytpcs ");
		sqlBF.append("   from fw.file_model ");
		sqlBF.append("  where dbid = ? ");
		sqlBF.append("    and wjbs = ? ");
		this.sql.setSql(sqlBF.toString());
		this.sql.setString(1, GlobalVars.SYS_DBID);
		this.sql.setString(2, wjbs);
		DataSet dsFile = this.sql.executeQuery();
		if (dsFile.size() <= 0) {
			throw new BizException("该文件标识" + wjbs + "对应的文件文件不存在");
		}

		// 解析图片打印参数
		DataSet dsDytpcs = new DataSet();
		String dytpcs = dsFile.getString(0, "dytpcs");
		if (!StringUtil.chkStrNull(dytpcs)) {
			String[] arrDytpcs = dytpcs.split(",");
			for (int i = 0, n = arrDytpcs.length; i < n; i++) {
				String[] oneTpcs = arrDytpcs[i].split(":");
				DataMap dmTemp = new DataMap();
				dmTemp.put("csmc", oneTpcs[0]);// 参数名称
				dmTemp.put("tpwz_x", Integer.parseInt(oneTpcs[1]));// 绝对位置-X坐标
				dmTemp.put("tpwz_y", Integer.parseInt(oneTpcs[2]));// 绝对位置-Y坐标
				dmTemp.put("tpsf", Integer.parseInt(oneTpcs[3]));// 图片缩放
				dsDytpcs.add(dmTemp);
			}
		}

		// 数据返回
		DataMap rdm = new DataMap();
		rdm.put("dmfile", dsFile.getRow(0));
		rdm.put("dsdycs", dsDytpcs);
		return rdm;
	}
}
