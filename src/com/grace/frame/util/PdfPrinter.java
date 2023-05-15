package com.grace.frame.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;

/**
 * pdf打印类，精确打印
 * 
 * @author yjc
 */
public class PdfPrinter{
	private DataSet PRINT_IMG_LIST_DS;// 打印参数-图片
	private Blob PRINT_MODEL;// 打印模板
	private DataMap PRINT_PARAS_DM;// 参数的参数集合
	private String __wjbs;// 文件标识
	private String __jbjgid;// 经办机构信息

	/**
	 * pdf打印--精确打印的方案
	 * 
	 * @throws AppException
	 */
	public PdfPrinter(String wjbs, String jbjgid) throws Exception {
		if (StringUtil.chkStrNull(wjbs)) {
			throw new AppException("传入的文件标识为空！");
		}
		this.__jbjgid = jbjgid;
		this.__wjbs = wjbs;

		this.PRINT_IMG_LIST_DS = new DataSet();
		this.PRINT_PARAS_DM = new DataMap();
	}

	/**
	 * 打印组件初始化
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws NumberFormatException
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	private void initPrinter() throws NumberFormatException, AppException {
		// 数据库操作类
		DataSet dsModel = new DataSet();
		Sql sql = new Sql();

		// 获取个格式pdf文件-首先获取经办机构级的，获取不到，获取全局级
		if (!StringUtil.chkStrNull(this.__jbjgid)) {
			sql.setSql(" select wjnr, dytpcs from fw.file_model where dbid = ? and wjbs = ? and lower(wjgs) = 'pdf' ");
			sql.setString(1, GlobalVars.SYS_DBID);
			sql.setString(2, this.__wjbs + "." + this.__jbjgid);
			dsModel = sql.executeQuery();
		}
		if (dsModel.size() <= 0) {
			sql.setSql(" select wjnr, dytpcs from fw.file_model where dbid = ? and wjbs = ? and lower(wjgs) = 'pdf' ");
			sql.setString(1, GlobalVars.SYS_DBID);
			sql.setString(2, this.__wjbs);
			dsModel = sql.executeQuery();
		}
		if (dsModel.size() <= 0) {
			throw new AppException("根据文件标识" + this.__wjbs + "无法查询到pdf打印模板");
		}

		// 图片参数
		String dytpcs = dsModel.getString(0, "dytpcs");
		if (!StringUtil.chkStrNull(dytpcs)) {
			String[] arrDytpcs = dytpcs.split(",");
			for (int i = 0, n = arrDytpcs.length; i < n; i++) {
				String[] oneTpcs = arrDytpcs[i].split(":");
				DataMap dmTemp = new DataMap();
				dmTemp.put("csmc", oneTpcs[0]);// 参数名称
				dmTemp.put("tpwz_x", Integer.parseInt(oneTpcs[1]));// 绝对位置-X坐标
				dmTemp.put("tpwz_y", Integer.parseInt(oneTpcs[2]));// 绝对位置-Y坐标
				dmTemp.put("tpsf", Integer.parseInt(oneTpcs[3]));// 图片缩放
				this.PRINT_IMG_LIST_DS.add(dmTemp);
			}
		}

		// 设置到全局-类
		Blob wjnr = (Blob) dsModel.getObject(0, "wjnr");
		this.PRINT_MODEL = wjnr;
	}

	/**
	 * 向printer中放置数据
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-28
	 * @since V1.0
	 */
	public void putPara(String key, Object value) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("放入参数时，key为空");
		}
		if (null == value) {
			value = "";
		}
		this.PRINT_PARAS_DM.put(key, value);
	}

	/**
	 * 打印操作--到流--流的关闭和释放由调用方处理
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public void print(OutputStream os) throws Exception {
		// 到真正打印环节进行数据的初始化-节省资源考虑
		this.initPrinter();

		// 创建reader
		PdfReader reader = new PdfReader(this.PRINT_MODEL.getBinaryStream());
		PdfStamper stamp = new PdfStamper(reader, os);

		// 图片的输出
		PdfContentByte under = stamp.getUnderContent(1);
		for (int i = 0, n = this.PRINT_IMG_LIST_DS.size(); i < n; i++) {
			String csmc = this.PRINT_IMG_LIST_DS.getString(i, "csmc");
			int tpwz_x = this.PRINT_IMG_LIST_DS.getInt(i, "tpwz_x");
			int tpwz_y = this.PRINT_IMG_LIST_DS.getInt(i, "tpwz_y");
			int tpsf = this.PRINT_IMG_LIST_DS.getInt(i, "tpsf");
			if (this.PRINT_PARAS_DM.containsKey(csmc)) {// 参数列表中存在--解析
				Object img_obj = this.PRINT_PARAS_DM.get(csmc);
				if (null == img_obj) {
					continue;// 为空，继续
				}
				// 支持解析的类型：Blob,byte[],File,String,URL,Image
				Image img;
				if (img_obj instanceof Blob) {// blob类型的图片
					Blob img_blob = (Blob) img_obj;
					int zbnrLen = (new BigDecimal(img_blob.length())).intValue();
					byte[] imgByte = img_blob.getBytes(1, zbnrLen);
					img = Image.getInstance(imgByte);
				} else if (img_obj instanceof byte[]) {
					byte[] imgByte = (byte[]) img_obj;
					img = Image.getInstance(imgByte);
				} else if (img_obj instanceof File) {
					byte[] imgByte = FileIOUtil.getBytesFromFile((File) img_obj);
					img = Image.getInstance(imgByte);
				} else if (img_obj instanceof String) {
					img = Image.getInstance((String) img_obj);
				} else if (img_obj instanceof URL) {
					img = Image.getInstance((URL) img_obj);
				} else if (img_obj instanceof java.awt.Image) {
					img = Image.getInstance((Image) img_obj);
				} else {
					throw new AppException("该图片类型不支持解析解析，请检查！"
							+ img_obj.toString());
				}
				img.scalePercent(tpsf);
				img.setAbsolutePosition(tpwz_x, tpwz_y);
				under.addImage(img);
			}
		}

		// BaseFont bfont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
		// BaseFont.NOT_EMBEDDED);
		// form.addSubstitutionFont(bfont);//
		// 设置字体防止程序不进行解析--mod.yjc.2016年8月9日达到展示效果，此处不进行字体设置

		/* 取出报表模板中的所有字段 */
		AcroFields form = stamp.getAcroFields();

		// 输出参数字符串变量
		HashMap mapFields = form.getFields();// 获取设置的参数列表
		Object[] arrKeys = this.PRINT_PARAS_DM.keySet().toArray();
		for (int i = 0, n = arrKeys.length; i < n; i++) {
			String key = (String) arrKeys[i];
			if (mapFields.containsKey(key)) {
				form.setField(key, String.valueOf(this.PRINT_PARAS_DM.get(key)));
			}
		}
		stamp.setFormFlattening(true);

		stamp.close();
		reader.close();
	}

	/**
	 * 打印操作--到响应-response
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	public void print(HttpServletResponse response) throws Exception {
		response.resetBuffer();
		String fileName = URLEncoder.encode("系统打印文件.pdf", "UTF-8");
		response.setContentType("application/x-dbf;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ fileName);
		OutputStream os = response.getOutputStream();
		try {
			this.print(os);
			os.flush();
		} catch (IOException e) {
			throw new AppException("文件读写异常：" + e.getMessage());
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				throw new AppException("输出流存在异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * 资源的释放
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	public void release() throws Exception {
		this.PRINT_IMG_LIST_DS.clear();
		this.PRINT_PARAS_DM.clear();
		this.PRINT_MODEL = null;
		this.PRINT_IMG_LIST_DS = null;
		this.PRINT_PARAS_DM = null;
	}

	/**
	 * 批量打印的工具方法--输出到一个pdf上--流需要调用方自己释放和关闭
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	public static void batchPrint(List<PdfPrinter> pdfPrinters, OutputStream os) throws Exception {
		Document document = new Document();
		PdfCopy copy = new PdfCopy(document, os);
		document.open();
		for (int i = 0, n = pdfPrinters.size(); i < n; i++) {
			PdfPrinter pdfPrinter = pdfPrinters.get(i);
			if (null == pdfPrinter) {
				throw new AppException("批量打印的pdfPrinter存在空值");
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				pdfPrinter.print(bos);
				PdfReader reader = new PdfReader(bos.toByteArray());
				int pages = reader.getNumberOfPages();
				for (int j = 1; j <= pages; j++) {
					document.newPage();
					PdfImportedPage page = copy.getImportedPage(reader, j);
					copy.addPage(page);
				}
				bos.flush();
				pdfPrinter.release();// 资源释放
			} catch (IOException e) {
				throw new AppException("文件读写异常：" + e.getMessage());
			} finally {
				try {
					if (bos != null) {
						bos.flush();
						bos.close();
					}
				} catch (Exception e) {
					throw new AppException("输出流存在异常，可能是文件损坏或不存在!错误信息为："
							+ e.getMessage());
				}
			}
		}
		document.close();
	}

	/**
	 * 批量打印的工具方法--输出到一个pdf上
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	public static void batchPrint(List<PdfPrinter> pdfPrinters,
			HttpServletResponse response) throws Exception {
		response.resetBuffer();
		String fileName = URLEncoder.encode("系统打印文件.pdf", "UTF-8");
		response.setContentType("application/x-dbf;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ fileName);
		OutputStream os = response.getOutputStream();
		try {
			PdfPrinter.batchPrint(pdfPrinters, response.getOutputStream());
			os.flush();
		} catch (IOException e) {
			throw new AppException("文件读写异常：" + e.getMessage());
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				throw new AppException("输出流存在异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}
}
