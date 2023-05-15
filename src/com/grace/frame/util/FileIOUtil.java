package com.grace.frame.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 文件流操作
 * 
 * @author yjc
 */
public class FileIOUtil{
	private final static HashMap<String, String> mimeMap = new HashMap<String, String>();
	static {
		FileIOUtil.mimeMap.put("323", "text/h323;charset=UTF-8");
		FileIOUtil.mimeMap.put("acx", "application/internet-property-stream;charset=UTF-8");
		FileIOUtil.mimeMap.put("ai", "application/postscript;charset=UTF-8");
		FileIOUtil.mimeMap.put("aif", "audio/x-aiff;charset=UTF-8");
		FileIOUtil.mimeMap.put("aifc", "audio/x-aiff;charset=UTF-8");
		FileIOUtil.mimeMap.put("aiff", "audio/x-aiff;charset=UTF-8");
		FileIOUtil.mimeMap.put("asf", "video/x-ms-asf;charset=UTF-8");
		FileIOUtil.mimeMap.put("asr", "video/x-ms-asf;charset=UTF-8");
		FileIOUtil.mimeMap.put("asx", "video/x-ms-asf;charset=UTF-8");
		FileIOUtil.mimeMap.put("au", "audio/basic;charset=UTF-8");
		FileIOUtil.mimeMap.put("avi", "video/x-msvideo;charset=UTF-8");
		FileIOUtil.mimeMap.put("axs", "application/olescript;charset=UTF-8");
		FileIOUtil.mimeMap.put("bas", "text/plain;charset=UTF-8");
		FileIOUtil.mimeMap.put("bcpio", "application/x-bcpio;charset=UTF-8");
		FileIOUtil.mimeMap.put("bin", "application/octet-stream;charset=UTF-8");
		FileIOUtil.mimeMap.put("bmp", "image/bmp;charset=UTF-8");
		FileIOUtil.mimeMap.put("c", "text/plain;charset=UTF-8");
		FileIOUtil.mimeMap.put("cat", "application/vnd.ms-pkiseccat;charset=UTF-8");
		FileIOUtil.mimeMap.put("cdf", "application/x-cdf;charset=UTF-8");
		FileIOUtil.mimeMap.put("cer", "application/x-x509-ca-cert;charset=UTF-8");
		FileIOUtil.mimeMap.put("class", "application/octet-stream;charset=UTF-8");
		FileIOUtil.mimeMap.put("clp", "application/x-msclip;charset=UTF-8");
		FileIOUtil.mimeMap.put("cmx", "image/x-cmx;charset=UTF-8");
		FileIOUtil.mimeMap.put("cod", "image/cis-cod;charset=UTF-8");
		FileIOUtil.mimeMap.put("cpio", "application/x-cpio;charset=UTF-8");
		FileIOUtil.mimeMap.put("crd", "application/x-mscardfile;charset=UTF-8");
		FileIOUtil.mimeMap.put("crl", "application/pkix-crl;charset=UTF-8");
		FileIOUtil.mimeMap.put("crt", "application/x-x509-ca-cert;charset=UTF-8");
		FileIOUtil.mimeMap.put("csh", "application/x-csh;charset=UTF-8");
		FileIOUtil.mimeMap.put("css", "text/css;charset=UTF-8");
		FileIOUtil.mimeMap.put("dcr", "application/x-director;charset=UTF-8");
		FileIOUtil.mimeMap.put("der", "application/x-x509-ca-cert;charset=UTF-8");
		FileIOUtil.mimeMap.put("dir", "application/x-director;charset=UTF-8");
		FileIOUtil.mimeMap.put("dll", "application/x-msdownload;charset=UTF-8");
		FileIOUtil.mimeMap.put("dms", "application/octet-stream;charset=UTF-8");
		FileIOUtil.mimeMap.put("doc", "application/msword;charset=UTF-8");
		FileIOUtil.mimeMap.put("dot", "application/msword;charset=UTF-8");
		FileIOUtil.mimeMap.put("dvi", "application/x-dvi;charset=UTF-8");
		FileIOUtil.mimeMap.put("dxr", "application/x-director;charset=UTF-8");
		FileIOUtil.mimeMap.put("eps", "application/postscript;charset=UTF-8");
		FileIOUtil.mimeMap.put("etx", "text/x-setext;charset=UTF-8");
		FileIOUtil.mimeMap.put("evy", "application/envoy;charset=UTF-8");
		FileIOUtil.mimeMap.put("exe", "application/octet-stream;charset=UTF-8");
		FileIOUtil.mimeMap.put("fif", "application/fractals;charset=UTF-8");
		FileIOUtil.mimeMap.put("flr", "x-world/x-vrml;charset=UTF-8");
		FileIOUtil.mimeMap.put("gif", "image/gif;charset=UTF-8");
		FileIOUtil.mimeMap.put("gtar", "application/x-gtar;charset=UTF-8");
		FileIOUtil.mimeMap.put("gz", "application/x-gzip;charset=UTF-8");
		FileIOUtil.mimeMap.put("h", "text/plain;charset=UTF-8");
		FileIOUtil.mimeMap.put("hdf", "application/x-hdf;charset=UTF-8");
		FileIOUtil.mimeMap.put("hlp", "application/winhlp;charset=UTF-8");
		FileIOUtil.mimeMap.put("hqx", "application/mac-binhex40;charset=UTF-8");
		FileIOUtil.mimeMap.put("hta", "application/hta;charset=UTF-8");
		FileIOUtil.mimeMap.put("htc", "text/x-component;charset=UTF-8");
		FileIOUtil.mimeMap.put("htm", "text/html;charset=UTF-8");
		FileIOUtil.mimeMap.put("html", "text/html;charset=UTF-8");
		FileIOUtil.mimeMap.put("htt", "text/webviewhtml;charset=UTF-8");
		FileIOUtil.mimeMap.put("ico", "image/x-icon;charset=UTF-8");
		FileIOUtil.mimeMap.put("ief", "image/ief;charset=UTF-8");
		FileIOUtil.mimeMap.put("iii", "application/x-iphone;charset=UTF-8");
		FileIOUtil.mimeMap.put("ins", "application/x-internet-signup;charset=UTF-8");
		FileIOUtil.mimeMap.put("isp", "application/x-internet-signup;charset=UTF-8");
		FileIOUtil.mimeMap.put("jfif", "image/pipeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("jpe", "image/jpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("jpeg", "image/jpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("jpg", "image/jpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("js", "application/x-javascript;charset=UTF-8");
		FileIOUtil.mimeMap.put("latex", "application/x-latex;charset=UTF-8");
		FileIOUtil.mimeMap.put("lha", "application/octet-stream;charset=UTF-8");
		FileIOUtil.mimeMap.put("lsf", "video/x-la-asf;charset=UTF-8");
		FileIOUtil.mimeMap.put("lsx", "video/x-la-asf;charset=UTF-8");
		FileIOUtil.mimeMap.put("lzh", "application/octet-stream;charset=UTF-8");
		FileIOUtil.mimeMap.put("m13", "application/x-msmediaview;charset=UTF-8");
		FileIOUtil.mimeMap.put("m14", "application/x-msmediaview;charset=UTF-8");
		FileIOUtil.mimeMap.put("m3u", "audio/x-mpegurl;charset=UTF-8");
		FileIOUtil.mimeMap.put("man", "application/x-troff-man;charset=UTF-8");
		FileIOUtil.mimeMap.put("mdb", "application/x-msaccess;charset=UTF-8");
		FileIOUtil.mimeMap.put("me", "application/x-troff-me;charset=UTF-8");
		FileIOUtil.mimeMap.put("mht", "message/rfc822;charset=UTF-8");
		FileIOUtil.mimeMap.put("mhtml", "message/rfc822;charset=UTF-8");
		FileIOUtil.mimeMap.put("mid", "audio/mid;charset=UTF-8");
		FileIOUtil.mimeMap.put("mny", "application/x-msmoney;charset=UTF-8");
		FileIOUtil.mimeMap.put("mov", "video/quicktime;charset=UTF-8");
		FileIOUtil.mimeMap.put("movie", "video/x-sgi-movie;charset=UTF-8");
		FileIOUtil.mimeMap.put("mp2", "video/mpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("mp3", "audio/mpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("mpa", "video/mpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("mpe", "video/mpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("mpeg", "video/mpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("mpg", "video/mpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("mpp", "application/vnd.ms-project;charset=UTF-8");
		FileIOUtil.mimeMap.put("mpv2", "video/mpeg;charset=UTF-8");
		FileIOUtil.mimeMap.put("ms", "application/x-troff-ms;charset=UTF-8");
		FileIOUtil.mimeMap.put("mvb", "application/x-msmediaview;charset=UTF-8");
		FileIOUtil.mimeMap.put("nws", "message/rfc822;charset=UTF-8");
		FileIOUtil.mimeMap.put("oda", "application/oda;charset=UTF-8");
		FileIOUtil.mimeMap.put("p10", "application/pkcs10;charset=UTF-8");
		FileIOUtil.mimeMap.put("p12", "application/x-pkcs12;charset=UTF-8");
		FileIOUtil.mimeMap.put("p7b", "application/x-pkcs7-certificates;charset=UTF-8");
		FileIOUtil.mimeMap.put("p7c", "application/x-pkcs7-mime;charset=UTF-8");
		FileIOUtil.mimeMap.put("p7m", "application/x-pkcs7-mime;charset=UTF-8");
		FileIOUtil.mimeMap.put("p7r", "application/x-pkcs7-certreqresp;charset=UTF-8");
		FileIOUtil.mimeMap.put("p7s", "application/x-pkcs7-signature;charset=UTF-8");
		FileIOUtil.mimeMap.put("pbm", "image/x-portable-bitmap;charset=UTF-8");
		FileIOUtil.mimeMap.put("pdf", "application/pdf;charset=UTF-8");
		FileIOUtil.mimeMap.put("pfx", "application/x-pkcs12;charset=UTF-8");
		FileIOUtil.mimeMap.put("pgm", "image/x-portable-graymap;charset=UTF-8");
		FileIOUtil.mimeMap.put("pko", "application/ynd.ms-pkipko;charset=UTF-8");
		FileIOUtil.mimeMap.put("pma", "application/x-perfmon;charset=UTF-8");
		FileIOUtil.mimeMap.put("pmc", "application/x-perfmon;charset=UTF-8");
		FileIOUtil.mimeMap.put("pml", "application/x-perfmon;charset=UTF-8");
		FileIOUtil.mimeMap.put("pmr", "application/x-perfmon;charset=UTF-8");
		FileIOUtil.mimeMap.put("pmw", "application/x-perfmon;charset=UTF-8");
		FileIOUtil.mimeMap.put("pnm", "image/x-portable-anymap;charset=UTF-8");
		FileIOUtil.mimeMap.put("pot,", "application/vnd.ms-powerpoint;charset=UTF-8");
		FileIOUtil.mimeMap.put("ppm", "image/x-portable-pixmap;charset=UTF-8");
		FileIOUtil.mimeMap.put("pps", "application/vnd.ms-powerpoint;charset=UTF-8");
		FileIOUtil.mimeMap.put("ppt", "application/vnd.ms-powerpoint;charset=UTF-8");
		FileIOUtil.mimeMap.put("prf", "application/pics-rules;charset=UTF-8");
		FileIOUtil.mimeMap.put("ps", "application/postscript;charset=UTF-8");
		FileIOUtil.mimeMap.put("pub", "application/x-mspublisher;charset=UTF-8");
		FileIOUtil.mimeMap.put("qt", "video/quicktime;charset=UTF-8");
		FileIOUtil.mimeMap.put("ra", "audio/x-pn-realaudio;charset=UTF-8");
		FileIOUtil.mimeMap.put("ram", "audio/x-pn-realaudio;charset=UTF-8");
		FileIOUtil.mimeMap.put("ras", "image/x-cmu-raster;charset=UTF-8");
		FileIOUtil.mimeMap.put("rgb", "image/x-rgb;charset=UTF-8");
		FileIOUtil.mimeMap.put("rmi", "audio/mid;charset=UTF-8");
		FileIOUtil.mimeMap.put("roff", "application/x-troff;charset=UTF-8");
		FileIOUtil.mimeMap.put("rtf", "application/rtf;charset=UTF-8");
		FileIOUtil.mimeMap.put("rtx", "text/richtext;charset=UTF-8");
		FileIOUtil.mimeMap.put("scd", "application/x-msschedule;charset=UTF-8");
		FileIOUtil.mimeMap.put("sct", "text/scriptlet;charset=UTF-8");
		FileIOUtil.mimeMap.put("setpay", "application/set-payment-initiation;charset=UTF-8");
		FileIOUtil.mimeMap.put("setreg", "application/set-registration-initiation;charset=UTF-8");
		FileIOUtil.mimeMap.put("sh", "application/x-sh;charset=UTF-8");
		FileIOUtil.mimeMap.put("shar", "application/x-shar;charset=UTF-8");
		FileIOUtil.mimeMap.put("sit", "application/x-stuffit;charset=UTF-8");
		FileIOUtil.mimeMap.put("snd", "audio/basic;charset=UTF-8");
		FileIOUtil.mimeMap.put("spc", "application/x-pkcs7-certificates;charset=UTF-8");
		FileIOUtil.mimeMap.put("spl", "application/futuresplash;charset=UTF-8");
		FileIOUtil.mimeMap.put("src", "application/x-wais-source;charset=UTF-8");
		FileIOUtil.mimeMap.put("sst", "application/vnd.ms-pkicertstore;charset=UTF-8");
		FileIOUtil.mimeMap.put("stl", "application/vnd.ms-pkistl;charset=UTF-8");
		FileIOUtil.mimeMap.put("stm", "text/html;charset=UTF-8");
		FileIOUtil.mimeMap.put("svg", "image/svg+xml;charset=UTF-8");
		FileIOUtil.mimeMap.put("sv4cpio", "application/x-sv4cpio;charset=UTF-8");
		FileIOUtil.mimeMap.put("sv4crc", "application/x-sv4crc;charset=UTF-8");
		FileIOUtil.mimeMap.put("swf", "application/x-shockwave-flash;charset=UTF-8");
		FileIOUtil.mimeMap.put("t", "application/x-troff;charset=UTF-8");
		FileIOUtil.mimeMap.put("tar", "application/x-tar;charset=UTF-8");
		FileIOUtil.mimeMap.put("tcl", "application/x-tcl;charset=UTF-8");
		FileIOUtil.mimeMap.put("tex", "application/x-tex;charset=UTF-8");
		FileIOUtil.mimeMap.put("texi", "application/x-texinfo;charset=UTF-8");
		FileIOUtil.mimeMap.put("texinfo", "application/x-texinfo;charset=UTF-8");
		FileIOUtil.mimeMap.put("tgz", "application/x-compressed;charset=UTF-8");
		FileIOUtil.mimeMap.put("tif", "image/tiff;charset=UTF-8");
		FileIOUtil.mimeMap.put("tiff", "image/tiff;charset=UTF-8");
		FileIOUtil.mimeMap.put("tr", "application/x-troff;charset=UTF-8");
		FileIOUtil.mimeMap.put("trm", "application/x-msterminal;charset=UTF-8");
		FileIOUtil.mimeMap.put("tsv", "text/tab-separated-values;charset=UTF-8");
		FileIOUtil.mimeMap.put("txt", "text/plain;charset=UTF-8");
		FileIOUtil.mimeMap.put("uls", "text/iuls;charset=UTF-8");
		FileIOUtil.mimeMap.put("ustar", "application/x-ustar;charset=UTF-8");
		FileIOUtil.mimeMap.put("vcf", "text/x-vcard;charset=UTF-8");
		FileIOUtil.mimeMap.put("vrml", "x-world/x-vrml;charset=UTF-8");
		FileIOUtil.mimeMap.put("wav", "audio/x-wav;charset=UTF-8");
		FileIOUtil.mimeMap.put("wcm", "application/vnd.ms-works;charset=UTF-8");
		FileIOUtil.mimeMap.put("wdb", "application/vnd.ms-works;charset=UTF-8");
		FileIOUtil.mimeMap.put("wks", "application/vnd.ms-works;charset=UTF-8");
		FileIOUtil.mimeMap.put("wmf", "application/x-msmetafile;charset=UTF-8");
		FileIOUtil.mimeMap.put("wps", "application/vnd.ms-works;charset=UTF-8");
		FileIOUtil.mimeMap.put("wri", "application/x-mswrite;charset=UTF-8");
		FileIOUtil.mimeMap.put("wrl", "x-world/x-vrml;charset=UTF-8");
		FileIOUtil.mimeMap.put("wrz", "x-world/x-vrml;charset=UTF-8");
		FileIOUtil.mimeMap.put("xaf", "x-world/x-vrml;charset=UTF-8");
		FileIOUtil.mimeMap.put("xbm", "image/x-xbitmap;charset=UTF-8");
		FileIOUtil.mimeMap.put("xla", "application/vnd.ms-excel;charset=UTF-8");
		FileIOUtil.mimeMap.put("xlc", "application/vnd.ms-excel;charset=UTF-8");
		FileIOUtil.mimeMap.put("xlm", "application/vnd.ms-excel;charset=UTF-8");
		FileIOUtil.mimeMap.put("xls", "application/vnd.ms-excel;charset=UTF-8");
		FileIOUtil.mimeMap.put("xlt", "application/vnd.ms-excel;charset=UTF-8");
		FileIOUtil.mimeMap.put("xlw", "application/vnd.ms-excel;charset=UTF-8");
		FileIOUtil.mimeMap.put("xof", "x-world/x-vrml;charset=UTF-8");
		FileIOUtil.mimeMap.put("xpm", "image/x-xpixmap;charset=UTF-8");
		FileIOUtil.mimeMap.put("xwd", "image/x-xwindowdump;charset=UTF-8");
		FileIOUtil.mimeMap.put("z", "application/x-compress;charset=UTF-8");
		FileIOUtil.mimeMap.put("zip", "application/zip;charset=UTF-8");
	}

	/**
	 * 获取扩展名
	 * 
	 * @author yjc
	 * @date 创建时间 2018-6-19
	 * @since V1.0
	 */
	public static String getMime(String ext) {
		if (FileIOUtil.mimeMap.containsKey(ext)) {
			return FileIOUtil.mimeMap.get(ext);
		}
		return "application/octet-stream;charset=UTF-8";
	}

	/**
	 * 对字节数组字符串进行Base64解码并生成图片
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-13
	 * @since V1.0
	 */
	public static byte[] getImageByImageStr(String imgStr) throws AppException {
		if (imgStr == null) // 图像数据为空
			return null;
		// Base64解码
		byte[] data = SecUtil.base64Decode(imgStr);
		for (int i = 0; i < data.length; i++) {
			if (data[i] < 0) { // 调整异常数据
				data[i] += 256;
			}
		}
		return data;
	}

	/**
	 * Blob图片转换成image-base64码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-13
	 * @since V1.0
	 */
	public static String getImageStrByBlob(Blob image) throws SQLException, AppException {
		int zbnrLen = (new BigDecimal(image.length())).intValue();
		byte[] filebyte = image.getBytes(1, zbnrLen);
		// 对字节数组Base64编码
		return SecUtil.base64Encode(filebyte); // 返回Base64编码过的字节数组字符串
	}

	/**
	 * 从CommonsMultipartFile图片文件中，获取base64码的图片字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-13
	 * @since V1.0
	 */
	public static String getImageStrByMultipartImage(CommonsMultipartFile image) throws AppException {
		// 对字节数组Base64编码
		return SecUtil.base64Encode(image.getBytes()); // 返回Base64编码过的字节数组字符串
	}

	/**
	 * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-13
	 * @since V1.0
	 */
	public static String getImageStrByImage(File image) throws AppException {
		InputStream in = null;
		byte[] data = null;
		// 读取图片字节数组
		try {
			in = new FileInputStream(image.getAbsolutePath());
			data = new byte[in.available()];
			in.read(data);
			in.close();
		} catch (IOException e) {
			throw new AppException("图片转成Base64编码时出错");
		}
		// 对字节数组Base64编码
		return SecUtil.base64Encode(data); // 返回Base64编码过的字节数组字符串
	}

	/**
	 * 向文件中写入二进制数据流
	 */
	public static void writeBytesToFile(byte[] bytes, File file) throws AppException {
		OutputStream out;
		try {
			out = new FileOutputStream(file);
			out.write(bytes);
			out.close();
		} catch (FileNotFoundException e) {
			throw new AppException("没有找到指定文件.错误信息为：" + e.getMessage());
		} catch (IOException e) {
			throw new AppException("读取文件内容异常.错误信息为：" + e.getMessage());
		}
	}

	/**
	 * 从指定文件中获取字节流数据
	 * 
	 * @since V1.0
	 */
	public static byte[] getBytesFromFile(File file) throws AppException {
		InputStream is;
		try {
			is = new FileInputStream(file);
			long length = file.length();
			byte[] bytes = new byte[(int) length];

			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ file.getName());
			}
			is.close();
			return bytes;
		} catch (FileNotFoundException e) {
			throw new AppException("没有找到指定文件.错误信息为：" + e.getMessage());
		} catch (IOException e) {
			throw new AppException("读取文件内容异常.错误信息为：" + e.getMessage());
		}
	}

	/**
	 * 说明：将服务器端的文档写进数据库中的blob中。
	 * 
	 * @author:yjc
	 * @param blob
	 * @throws AppException
	 */
	public static void saveBlobToFile(Blob blob, File file) throws AppException {
		if (file != null) {
			FileOutputStream outputstream = null;
			InputStream inputstream = null;
			try {
				inputstream = blob.getBinaryStream();
				outputstream = new FileOutputStream(file);

				byte[] buffer = new byte[32528];
				int len = 0;
				while ((len = inputstream.read(buffer)) != -1) {
					outputstream.write(buffer, 0, len);
				}
				outputstream.flush();
			} catch (Exception e) {
				throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			} finally {
				try {
					if (outputstream != null) {
						outputstream.close();
					}
					if (inputstream != null) {
						inputstream.close();
					}
				} catch (Exception e) {
					throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * 说明：将服务器端的文档写进数据库中的blob中。
	 * 
	 * @author:yjc
	 * @param blob
	 * @throws AppException
	 */
	public static void saveFileToBlob(File file, Blob blob) throws AppException {
		if (file != null) {
			OutputStream outputstream = null;
			FileInputStream inputstream = null;
			try {
				outputstream = blob.setBinaryStream(0);
				inputstream = new FileInputStream(file);
				byte[] buffer = new byte[32528];
				int len = 0;
				while ((len = inputstream.read(buffer)) != -1) {
					outputstream.write(buffer, 0, len);
				}
				outputstream.flush();
			} catch (Exception e) {
				throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			} finally {
				try {
					if (outputstream != null) {
						outputstream.close();
					}
					if (inputstream != null) {
						inputstream.close();
					}
				} catch (Exception e) {
					throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * 说明：将附件中的文档写进数据库中的blob中。
	 * 
	 * @author:yjc
	 */
	public static void saveCommonsMultipartFileToBlob(
			CommonsMultipartFile attachfile, Blob blob) throws AppException {
		if (attachfile != null) {
			OutputStream outputstream = null;
			ByteArrayInputStream inputstream = null;
			try {
				outputstream = blob.setBinaryStream(0);
				inputstream = new ByteArrayInputStream(attachfile.getBytes());
				byte[] buffer = new byte[32528];// 获取的是BLOB的大小模式-经过测试
				int len = 0;
				while ((len = inputstream.read(buffer)) != -1) {
					outputstream.write(buffer, 0, len);
				}
				outputstream.flush();
			} catch (Exception e) {
				throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			} finally {
				try {
					if (outputstream != null) {
						outputstream.close();
					}
					if (inputstream != null) {
						inputstream.close();
					}
				} catch (Exception e) {
					throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * 说明：将附件中的文档写进数据库中的blob中。byte->blob
	 * 
	 * @author:yjc
	 */
	public static void saveByteToBlob(byte[] file, Blob blob) throws AppException {
		if (file != null) {
			OutputStream outputstream = null;
			try {
				outputstream = blob.setBinaryStream(0);
				outputstream.write(file);
				outputstream.flush();
			} catch (Exception e) {
				throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			} finally {
				try {
					if (outputstream != null) {
						outputstream.close();
					}
				} catch (Exception e) {
					throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
							+ e.getMessage());
				}
			}
		}
	}

	/**
	 * 说明：将附件中的文件上传到服务器端
	 * 
	 * @author:yjc
	 * @param file
	 * @param attachFile
	 * @throws AppException
	 */
	public static String saveCommonsMultipartFileToServer(
			CommonsMultipartFile attachFile, String filepath, String filename) throws AppException {
		FileOutputStream outputstream = null;
		ByteArrayInputStream inputstream = null;
		File file = null;
		try {
			File dire = new File(filepath);
			if (!dire.exists()) {
				dire.mkdirs();
			}
			if (!dire.isDirectory()) {
				throw new AppException("该路径不是合法的存储路径，请检查！");
			}

			file = new File(filepath + File.separator + filename);
			file.createNewFile();

			inputstream = new ByteArrayInputStream(attachFile.getBytes());
			outputstream = new FileOutputStream(file);
			byte[] byteValue = new byte[1024];
			int len = 0;
			while ((len = inputstream.read(byteValue)) != -1) {
				outputstream.write(byteValue, 0, len);
			}
			outputstream.flush();
			return filepath + File.separator + filename;
		} catch (Exception e) {
			throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为：" + e.getMessage());
		} finally {
			try {
				if (outputstream != null) {
					outputstream.close();
				}
				if (inputstream != null) {
					inputstream.close();
				}
			} catch (Exception e) {
				throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * 保存文件到服务器
	 * 
	 * @author m.yuan
	 * @date 创建时间 2016年6月14日
	 * @since V1.0
	 */
	public static String saveCommonFileToServer(File pFile, String filepath,
			String filename) throws AppException {
		FileOutputStream outputstream = null;
		ByteArrayInputStream inputstream = null;
		File file = null;
		try {
			File dire = new File(filepath);
			if (!dire.exists()) {
				dire.mkdirs();
			}
			if (!dire.isDirectory()) {
				throw new AppException("该路径不是合法的存储路径，请检查！");
			}

			file = new File(filepath + File.separator + filename);
			file.createNewFile();

			FileInputStream fis = new FileInputStream(pFile);
			outputstream = new FileOutputStream(file);
			byte[] b = new byte[1024];
			int len = 0;
			while ((len = fis.read(b)) != -1) {
				outputstream.write(b, 0, len);
			}
			outputstream.flush();
			outputstream.close();
			fis.close();

			return filepath + File.separator + filename;
		} catch (Exception e) {
			throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为：" + e.getMessage());
		} finally {
			try {
				if (outputstream != null) {
					outputstream.close();
				}
				if (inputstream != null) {
					inputstream.close();
				}
			} catch (Exception e) {
				throw new AppException("文件写入异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * 说明：将字节流写入到服务器端的文件中。
	 * 
	 * @author:yjc
	 * @param filebyte
	 * @param filepath
	 * @param filename
	 * @throws AppException
	 */
	public static void writeByteToServer(byte[] filebyte, String filepath,
			String filename) throws AppException {
		ByteArrayInputStream inputstream = null;
		FileOutputStream outputstream = null;
		try {
			if (filename != null && !filename.equals("")) {
				File file = new File(filepath + File.separator + filename);
				file.createNewFile();
				if (filebyte != null && filebyte.length != 0) {
					inputstream = new ByteArrayInputStream(filebyte);
					outputstream = new FileOutputStream(file);
					byte[] b = new byte[1024];
					int len = 0;
					while ((len = inputstream.read(b)) != -1) {
						outputstream.write(b, 0, len);
					}
					outputstream.flush();
					outputstream.close();
					inputstream.close();
				}
			}
		} catch (IOException e) {
			throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为：" + e.getMessage());
		} finally {
			try {
				if (outputstream != null) {
					outputstream.close();
				}
				if (inputstream != null) {
					inputstream.close();
				}
			} catch (Exception e) {
				throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * 说明：将服务器中的文件下载到本地
	 * 
	 * @author:yjc
	 * @param response
	 * @param filepath ：服务器中的文件路径
	 * @param filename
	 * @throws AppException
	 */
	public static void writeDirFileToResponse(String filepath, String filename,
			HttpServletResponse response) throws AppException {
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(filepath + File.separator
					+ filename);
			FileIOUtil.writeStreamToResponse(inputstream, filename, response);
		} catch (IOException e) {
			throw new AppException("文件读取异常：" + e.getMessage());
		} finally {
			try {
				if (inputstream != null) {
					inputstream.close();
				}
			} catch (Exception e) {
				throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * 将inputString输出到response中
	 * 
	 * @return
	 * @author yjc
	 * @date 创建时间 2016-7-27
	 * @since V1.0
	 */
	public static void writeStreamToResponse(InputStream inputstream,
			String filename, HttpServletResponse response) throws AppException {
		OutputStream outputstream = null; // 下载流
		try {
			String fileExt = filename.substring(filename.lastIndexOf(".") + 1)
				.toLowerCase();
			filename = URLEncoder.encode(filename, "UTF-8");
			response.resetBuffer();
			response.setContentType(FileIOUtil.getMime(fileExt));
			response.addHeader("Content-Disposition", "attachment;filename="
					+ filename);
			outputstream = response.getOutputStream(); // 得到向客户端输出二进制数据的对象

			byte[] byteValue = new byte[1024];
			int tempValue = 0;
			while ((tempValue = inputstream.read(byteValue)) > 0) {
				outputstream.write(byteValue, 0, tempValue);
			}
			outputstream.flush();
		} catch (IOException e) {
			if (e.getCause() instanceof SocketException) {
				// 前端连接异常终止，do nothing.
			} else {
				throw new AppException("文件读写异常：" + e.getMessage());
			}
		} finally {
			try {
				if (outputstream != null) {
					outputstream.close();
				}
			} catch (Exception e) {
				// 该处调整为不抛出异常，防止前端终止流传输后，发生的传输异常。
				// throw new AppException("输出流存在异常，可能是文件损坏或不存在!错误信息为："+
				// e.getMessage());
			}
		}
	}

	/**
	 * 将服务器中的文件下载到本地
	 * 
	 * @author m.yuan
	 * @date 创建时间 2016年6月14日
	 * @since V1.0
	 */
	public static void writeFileToResponse(String filepath, String filename,
			HttpServletResponse response) throws AppException {
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(filepath);
			FileIOUtil.writeStreamToResponse(inputstream, filename, response);
		} catch (IOException e) {
			throw new AppException("文件读取异常：" + e.getMessage());
		} finally {
			try {
				if (inputstream != null) {
					inputstream.close();
				}
			} catch (Exception e) {
				throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * 说明-将Blob放到resposne中,主要用于从数据库中读取文件。
	 * 
	 * @author yjc
	 */
	public static void writeBlobToResponse(Blob blob, String filename,
			HttpServletResponse response) throws AppException {
		InputStream inputstream = null;
		if (blob != null) {
			try {
				inputstream = blob.getBinaryStream();
				FileIOUtil.writeStreamToResponse(inputstream, filename, response);
			} catch (SQLException e) {
				throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			} finally {
				try {
					if (inputstream != null) {
						inputstream.close();
					}
				} catch (Exception e) {
					throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为："
							+ e.getMessage());
				}
			}
		} else {
			throw new AppException("该附件不存在");
		}
	}

	/**
	 * 说明：将文件流放到resposne中。
	 * 
	 * @author yjc
	 */
	public static void writeByteToResponse(byte[] filebyte, String filename,
			HttpServletResponse response) throws AppException {
		OutputStream toClient = null;
		if (filebyte != null) {
			try {
				String fileExt = filename.substring(filename.lastIndexOf(".") + 1)
					.toLowerCase();
				response.resetBuffer();
				filename = URLEncoder.encode(filename, "UTF-8");
				response.setContentType(FileIOUtil.getMime(fileExt));
				response.addHeader("Content-Disposition", "attachment; filename="
						+ filename);
				toClient = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
				toClient.write(filebyte); // 输出数据
				toClient.flush();
			} catch (IOException e) {
				// 用户取消下载
			} finally {
				try {
					if (toClient != null) {
						toClient.close();
					}
				} catch (Exception e) {
					throw new AppException("文件下载异常!错误信息为：" + e.getMessage());
				}
			}
		} else {
			throw new AppException("该文件内容不存在");
		}
	}

	/**
	 * 写文本文件
	 * 
	 * @param filePath 文件路径
	 * @param fileContent 文件内容
	 * @param appended 是否追加
	 */
	public static void writeTxtFile(String filePath, String fileContent,
			boolean appended) throws AppException {
		String path = filePath.substring(0, filePath.lastIndexOf(File.separator));
		File dirFile = new File(path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		FileWriter writer = null;
		try {
			writer = new FileWriter(filePath, appended);
		} catch (Exception e) {
			throw new AppException("创建文件错误:" + e.getMessage());
		}
		try {
			writer.write(fileContent);
		} catch (Exception e) {
			throw new AppException("写文件内容错误:" + e.getMessage());
		}
		try {
			writer.close();
		} catch (Exception e) {
			throw new AppException("关闭文件错误:" + e.getMessage());
		}
	}

	/**
	 * zip文件的查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static void writeZipFile4View(String filepath, String zipFileName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(filepath);
			FileIOUtil.writeStreamZipFile4View(inputstream, zipFileName, request, response);
		} catch (IOException e) {
			throw new AppException("文件读取异常：" + e.getMessage());
		} finally {
			try {
				if (inputstream != null) {
					inputstream.close();
				}
			} catch (Exception e) {
				throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * zip文件的查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static void writeDriZipFile4View(String filepath,
			String zipFileName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		FileInputStream inputstream = null;
		try {
			inputstream = new FileInputStream(filepath + File.separator
					+ zipFileName);
			FileIOUtil.writeStreamZipFile4View(inputstream, zipFileName, request, response);
		} catch (IOException e) {
			throw new AppException("文件读取异常：" + e.getMessage());
		} finally {
			try {
				if (inputstream != null) {
					inputstream.close();
				}
			} catch (Exception e) {
				throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * zip文件在线查看--构建ds
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-27
	 * @since V1.0
	 */
	private static DataSet addImageRow4ZipFileView(DataSet ds, String wjmc,
			String base64img) throws Exception {
		ds.addRow();
		ds.put(ds.size() - 1, "wjmc", wjmc);
		ds.put(ds.size() - 1, "img", base64img);
		return ds;
	}

	/**
	 * 文件的一个图标-baise64码的
	 */
	public static String File_IMG_ICON_BASE64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAIAAAACACAYAAADDPmHLAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OURCNjUzRDQ1NUExMTFFM0FENDhCNjk2M0MzODY2RTciIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OURCNjUzRDU1NUExMTFFM0FENDhCNjk2M0MzODY2RTciPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo5REI2NTNEMjU1QTExMUUzQUQ0OEI2OTYzQzM4NjZFNyIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo5REI2NTNEMzU1QTExMUUzQUQ0OEI2OTYzQzM4NjZFNyIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PtbrSCIAAAhqSURBVHja7J1PbBNH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";

	/**
	 * zip文件的查看
	 * 
	 * @author yjc
	 * @throws IOException
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static String writeStreamZipFile4View(InputStream inputstream,
			String zipFileName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		DataSet dsImage = new DataSet();
		ZipInputStream zin = new ZipInputStream(inputstream);
		ZipEntry ze;
		int BUFFER = 325280;
		while ((ze = zin.getNextEntry()) != null) {
			if (ze.isDirectory()) {// 目录则不进行继续检索
			} else {
				String fileName = ze.getName();// 获取文件名称
				String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1)
					.toLowerCase();// 文件扩展名

				// 检查是否为图片
				if ("bmp,jpg,jpeg,png,gif".indexOf(fileExt) < 0) {
					FileIOUtil.addImageRow4ZipFileView(dsImage, fileName, FileIOUtil.File_IMG_ICON_BASE64);
					continue;
				}

				// 如果是图片，需要解析图片
				int count;
				byte data[] = new byte[BUFFER];
				ByteArrayOutputStream dest = new ByteArrayOutputStream();
				while ((count = zin.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				byte[] imgByte = dest.toByteArray();
				FileIOUtil.addImageRow4ZipFileView(dsImage, fileName, "data:image/"
						+ fileExt + ";base64," + SecUtil.base64Encode(imgByte));

				dest.flush();
				dest.close();
			}
		}
		zin.closeEntry();

		// 数据存session
		String uiid_key = "zip_view_" + StringUtil.getUUID();

		// 需要使用的数据
		DataMap dm = new DataMap();
		dm.put("wjmc", zipFileName);
		dm.put("dsimage", dsImage);

		// 放到session中
		request.getSession().setAttribute(uiid_key, dm);

		// uiid数据返回到前台
		DataMap rdm = new DataMap();
		rdm.put("zipsessionkey", uiid_key);
		ActionUtil.writeDataMapToResponse(response, rdm);

		return uiid_key;
	}

	/**
	 * zip文件的查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static String writeBlobZipFile4View(Blob blob, String zipFileName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		return FileIOUtil.writeStreamZipFile4View(blob.getBinaryStream(), zipFileName, request, response);
	}

	/**
	 * zip文件的查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static void writeByteZipFile4View(byte[] filebyte,
			String zipFileName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ByteArrayInputStream inputstream = null;
		try {
			inputstream = new ByteArrayInputStream(filebyte);
			FileIOUtil.writeStreamZipFile4View(inputstream, zipFileName, request, response);
		} catch (IOException e) {
			throw new AppException("文件读取异常：" + e.getMessage());
		} finally {
			try {
				if (inputstream != null) {
					inputstream.close();
				}
			} catch (Exception e) {
				throw new AppException("文件读取异常，可能是文件损坏或不存在!错误信息为："
						+ e.getMessage());
			}
		}
	}

	/**
	 * 将txt文件转换为pdf文件写向前台。
	 * 
	 * @author yjc
	 * @throws DocumentException
	 * @throws IOException
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static void writeTxt2PdfToResponse(String title, String fileContent,
			HttpServletResponse response) throws AppException, DocumentException, IOException {
		// 定义响应头。
		String fileName = title + ".pdf";
		response.resetBuffer();
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.setContentType("application/x-dbf;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ fileName);

		// 生成PDF文档
		Document document = new Document(PageSize.A4);// 纸张大小，确定为A4
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();

		// 标题字体
		BaseFont titleBFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		Font titleFont = new Font(titleBFont, 16, Font.NORMAL);

		// 内容字体
		BaseFont contentBFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		Font contentFont = new Font(contentBFont, 9, Font.NORMAL);

		// 输出标题
		Paragraph titleP = new Paragraph(title + "\n", titleFont);
		titleP.setAlignment(Paragraph.ALIGN_CENTER);
		document.add(titleP);

		// 内容
		Paragraph contentP = new Paragraph(fileContent, contentFont);
		contentP.setAlignment(Paragraph.ALIGN_LEFT);
		document.add(contentP);

		// 文档关闭
		document.close();
	}

	/**
	 * 将img文件转换为pdf文件写向前台。
	 * 
	 * @author yjc
	 * @throws DocumentException
	 * @throws IOException
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static void writeImg2PdfToResponse(String title, BufferedImage img,
			HttpServletResponse response) throws AppException, DocumentException, IOException {
		// 定义响应头。
		String fileName = title + ".pdf";
		response.resetBuffer();
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.setContentType("application/x-dbf;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ fileName);

		// 如果图片为空
		if (null == img) {
			FileIOUtil.writeTxt2PdfToResponse(title, "错误：图片信息为空。", response);
			return;
		}

		ByteArrayOutputStream bAout = new ByteArrayOutputStream();
		ImageIO.write(img, "png", bAout);
		byte[] imgdata = bAout.toByteArray();
		bAout.flush();
		bAout.close();

		Image pdfImg = Image.getInstance(imgdata);

		// 生成PDF文档
		Document document = new Document(new Rectangle(img.getWidth(), img.getHeight()), 0, 0, 0, 0);
		PdfWriter.getInstance(document, response.getOutputStream());
		document.open();

		// 书写图片
		document.add(pdfImg);

		// 文档关闭
		document.close();
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param sPath 被删除目录的文件路径
	 * @return 目录删除成功返回true，否则返回false
	 * @author zyl
	 * @throws BizException
	 * @date 创建时间 2017年5月18日
	 * @since V1.0
	 */
	public static boolean deleteDirectory(String sPath) throws BizException {
		if (StringUtil.chkStrNull(sPath)) {
			throw new BizException("文件路径为空");
		}
		// 如果sPath不以文件分隔符结尾，自动添加文件分隔符
		if (!sPath.endsWith(File.separator)) {
			sPath = sPath + File.separator;
		}
		File dirFile = new File(sPath);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = FileIOUtil.deleteFile(files[i].getAbsolutePath());
				if (!flag)
					break;
			} // 删除子目录
			else {
				flag = FileIOUtil.deleteDirectory(files[i].getAbsolutePath());
				if (!flag)
					break;
			}
		}
		if (!flag)
			return false;
		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param sPath 被删除文件的文件名
	 * @return 单个文件删除成功返回true，否则返回false
	 * @author zyl
	 * @throws BizException
	 * @date 创建时间 2017年5月18日
	 * @since V1.0
	 */
	public static boolean deleteFile(String sPath) throws BizException {
		if (StringUtil.chkStrNull(sPath)) {
			throw new BizException("文件路径为空");
		}
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile() && file.exists()) {
			file.delete();
			flag = true;
		}
		return flag;
	}

	/**
	 * 创建目录
	 * 
	 * @author yjc
	 * @date 创建时间 2017-6-8
	 * @since V1.0
	 */
	public static void createPath(String path) {
		File pathFile = new File(path);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
	}

	/**
	 * 下载远程文件并保存到本地
	 * 
	 * @param remoteFilePath 远程文件路径
	 * @param localFilePath 本地文件路径（带文件名）
	 */
	public static void downloadFile(String remoteFilePath, String localFilePath) throws Exception {
		URL urlfile = null;
		HttpURLConnection httpUrl = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		File f = new File(localFilePath);
		try {
			urlfile = new URL(remoteFilePath);
			httpUrl = (HttpURLConnection) urlfile.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(f));
			int len = 2048;
			byte[] b = new byte[len];
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
			bos.flush();
			bis.close();
			httpUrl.disconnect();
		} finally {
			try {
				bis.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 下载图片，并强制转换为base64串
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-4
	 * @since V1.0
	 */
	public static String downloadImg2Base64(String remoteImgPath) throws Exception {
		URL urlfile = null;
		HttpURLConnection httpUrl = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			urlfile = new URL(remoteImgPath);
			httpUrl = (HttpURLConnection) urlfile.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			bos = new ByteArrayOutputStream();
			int len = 2048;
			byte[] b = new byte[len];
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
			}
			byte[] imgByte = bos.toByteArray();
			bos.flush();
			bis.close();
			httpUrl.disconnect();

			// 开始处理base64数据
			MagicMatch match = Magic.getMagicMatch(imgByte);
			String imgType = match.getMimeType();

			return "data:" + imgType + ";base64,"
					+ SecUtil.base64Encode(imgByte);
		} finally {
			try {
				bis.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
