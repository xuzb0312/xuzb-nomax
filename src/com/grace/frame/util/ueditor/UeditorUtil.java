package com.grace.frame.util.ueditor;

import java.io.File;
import java.sql.Blob;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.FileIOUtil;
import com.grace.frame.util.SeqUtil;
import com.grace.frame.util.Sql;
import com.grace.frame.util.StringUtil;

/**
 * 内容处理类
 * 
 * @author yjc
 */
public class UeditorUtil{
	// 前缀
	public final static String IMG_PREFIX = "ueditor/jsp/upload/image/";
	public final static String FILE_PREFIX = "ueditor/jsp/upload/file/";
	public final static String VEDIO_PREFIX = "ueditor/jsp/upload/video/";
	public final static String NEW_FILE_PATH_PREFIX = "taglib.do?method=downloadUeditorFile&wjid=";

	// 本服务器缓存数据
	public static ConcurrentHashMap<String, String> CACHEFILE = new ConcurrentHashMap<String, String>();
	public final static String CACHE_PATH = "ueditor" + File.separator
			+ "cache" + File.separator;// 缓存路径
	public final static String CACHE_URL_PREFIX = "ueditor/cache/";// 缓存路径

	/**
	 * 保存文件到数据库
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-2-12
	 * @since V1.0
	 */
	private static String saveUeditorFile2Db(String filePath) throws AppException {
		String fileExt = filePath.substring(filePath.lastIndexOf(".") + 1)
			.toLowerCase();
		String wjid = SeqUtil.getId("fw.sq_wjid");

		// 文件存储到数据库
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" insert into fw.ueditor_file ");
		sqlBF.append("   (wjid, wjgs, wjnr, bz, cjsj) ");
		sqlBF.append(" values ");
		sqlBF.append("   (?, ?, empty_blob(), null, sysdate) ");
		sql.setSql(sqlBF.toString());
		sql.setString(1, wjid);
		sql.setString(2, fileExt);
		sql.executeUpdate();

		sql.setSql(" select wjnr from fw.ueditor_file where wjid = ? ");
		sql.setString(1, wjid);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() > 0) {
			Blob wjnr = dsTemp.getBlob(0, "wjnr");
			FileIOUtil.saveFileToBlob(new File(filePath), wjnr);
		}
		return wjid;
	}

	/**
	 * 前台展示前调用的（图片地址处理成静态地址，方便前段浏览器缓存-没有缓存则不生成缓存）
	 * 
	 * @author yjc
	 * @date 创建时间 2019-2-21
	 * @since V1.0
	 */
	public static String parseEditorContentImgFile(String htmlStr) {
		if (StringUtil.chkStrNull(htmlStr)) {
			return "";
		}
		if (htmlStr.startsWith("<?xml")) {// 如果不是xml类型文件，才进行图片提取操作。[xml格式文件为word的模板文件不允许进行调整。]
			return htmlStr;
		}

		Document doc = Jsoup.parse(htmlStr);
		// 图片处理
		Elements eles = doc.getElementsByTag("img");
		for (Element ele : eles) {
			String src = ele.attr("src");
			if (StringUtil.chkStrNull(src)) {
				continue;
			}
			src = src.trim();
			if (!src.startsWith(UeditorUtil.NEW_FILE_PATH_PREFIX)) {
				continue;
			}
			String wjid = src.replace(UeditorUtil.NEW_FILE_PATH_PREFIX, "");// 得到文件ID
			if (!UeditorUtil.CACHEFILE.containsKey(wjid)) {// 是否进行了缓存
				continue;
			}
			String fileName = UeditorUtil.CACHEFILE.get(wjid);
			ele.attr("src", UeditorUtil.CACHE_URL_PREFIX + fileName);// 更换数据值
		}

		// 转换一下，只输出body内容
		Element body = doc.body();
		return body.html();
	}

	/**
	 * 前台展示前调用的（图片地址处理成静态地址，方便前段浏览器缓存-没有缓存主动生成缓存）
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-2-21
	 * @since V1.0
	 */
	public static String parseEditorContentImgFile(String htmlStr,
			HttpServletRequest request) throws AppException {
		if (null == request) {// 请求为空的情况，则直接返回原文。
			return UeditorUtil.parseEditorContentImgFile(htmlStr);
		}
		if (StringUtil.chkStrNull(htmlStr)) {
			return "";
		}
		if (htmlStr.startsWith("<?xml")) {// 如果不是xml类型文件，才进行图片提取操作。[xml格式文件为word的模板文件不允许进行调整。]
			return htmlStr;
		}

		// 缓存路径
		String cachePath = request.getSession()
			.getServletContext()
			.getRealPath("/")
				+ UeditorUtil.CACHE_PATH;

		Document doc = Jsoup.parse(htmlStr);
		// 图片处理
		Elements eles = doc.getElementsByTag("img");
		for (Element ele : eles) {
			String src = ele.attr("src");
			if (StringUtil.chkStrNull(src)) {
				continue;
			}
			src = src.trim();
			if (!src.startsWith(UeditorUtil.NEW_FILE_PATH_PREFIX)) {
				continue;
			}
			String wjid = src.replace(UeditorUtil.NEW_FILE_PATH_PREFIX, "");// 得到文件ID
			if (!UeditorUtil.CACHEFILE.containsKey(wjid)) {// 是否进行了缓存-没有则主动进行缓存
				try {
					// 缓存操作
					File cacheDir = new File(cachePath);
					if (!cacheDir.exists()) {
						cacheDir.mkdirs();
					}

					Sql sql = new Sql();
					sql.setSql(" select wjgs, wjnr from fw.ueditor_file where wjid = ? ");
					sql.setString(1, wjid);
					DataSet dsAttch = sql.executeQuery();
					if (dsAttch.size() <= 0) {
						continue;
					}
					Blob wjnr = dsAttch.getBlob(0, "wjnr");
					String wjgs = dsAttch.getString(0, "wjgs");
					// 文件保存
					String fileName = wjid + "." + wjgs;
					FileIOUtil.saveBlobToFile(wjnr, new File(cachePath
							+ fileName));

					// 加入缓存，下一次则不进行重新静态化了。
					UeditorUtil.CACHEFILE.put(wjid, fileName);
				} catch (Exception e) {
					// 缓存出错，跳过
					e.printStackTrace();
					continue;
				}
			}
			String fileName = UeditorUtil.CACHEFILE.get(wjid);
			ele.attr("src", UeditorUtil.CACHE_URL_PREFIX + fileName);// 更换数据值
		}

		// 转换一下，只输出body内容
		Element body = doc.body();
		return body.html();
	}

	/**
	 * 内容处理
	 * 
	 * @author yjc
	 * @date 创建时间 2019-2-12
	 * @since V1.0
	 */
	public static String dealEditorContentImgVideoFile(String htmlStr,
			HttpServletRequest request) {
		if (null == request) {// 请求为空的情况，则直接返回原文。
			return htmlStr;
		}
		if (StringUtil.chkStrNull(htmlStr)) {
			return "";
		}
		if (htmlStr.startsWith("<?xml")) {// 如果不是xml类型文件，才进行图片提取操作。[xml格式文件为word的模板文件不允许进行调整。]
			return htmlStr;
		}
		Document doc = Jsoup.parse(htmlStr);
		String rootPath = request.getSession()
			.getServletContext()
			.getRealPath("/");

		// 图片处理
		Elements eles = doc.getElementsByTag("img");
		for (Element ele : eles) {
			String src = ele.attr("src");
			if (StringUtil.chkStrNull(src)) {
				continue;
			}
			src = src.trim();
			if (src.startsWith(UeditorUtil.IMG_PREFIX)) {// 图片临时存储前缀
				// 图片转移到数据库中
				try {
					String filePath = rootPath
							+ src.replace("/", File.separator);
					String wjid = UeditorUtil.saveUeditorFile2Db(filePath);
					ele.attr("src", UeditorUtil.NEW_FILE_PATH_PREFIX + wjid);// 更换数据值
				} catch (Exception e) {
					e.printStackTrace();
					continue;// 出现异常，则直接忽略该图片
				}
			} else if (src.startsWith("data") || src.startsWith("DATA")) {
				continue;
			} else if (src.startsWith(UeditorUtil.NEW_FILE_PATH_PREFIX)) {
				continue;
			} else if (src.startsWith(UeditorUtil.CACHE_URL_PREFIX)) {// 如果是缓存路径，也跳过处理。
				continue;
			} else {
				// 远程下载图片-转为base64码
				try {
					String base64ImgStr = FileIOUtil.downloadImg2Base64(src);
					ele.attr("src", base64ImgStr);// 更换数据值
				} catch (Exception e) {
					e.printStackTrace();
					continue;// 出现异常，则直接忽略该图片
				}
			}
		}

		// 附件操作
		eles = doc.getElementsByTag("a");
		for (Element ele : eles) {
			String href = ele.attr("href");
			if (StringUtil.chkStrNull(href)) {
				continue;
			}
			href = href.trim();
			if (!href.startsWith(UeditorUtil.FILE_PREFIX)) {// 附件临时存储前缀
				continue;
			}
			// 附件转移到数据库中
			try {
				String filePath = rootPath + href.replace("/", File.separator);
				String wjid = UeditorUtil.saveUeditorFile2Db(filePath);
				ele.attr("href", UeditorUtil.NEW_FILE_PATH_PREFIX + wjid);// 更换数据值
			} catch (Exception e) {
				e.printStackTrace();
				continue;// 出现异常，则直接忽略该图片
			}
		}

		// 视频操作
		eles = doc.getElementsByTag("video");
		for (Element ele : eles) {
			String src = ele.attr("src");
			if (StringUtil.chkStrNull(src)) {
				continue;
			}
			src = src.trim();
			if (!src.startsWith(UeditorUtil.VEDIO_PREFIX)) {// 附件临时存储前缀
				continue;
			}

			// 图片转移到数据库中
			try {
				String filePath = rootPath + src.replace("/", File.separator);
				String wjid = UeditorUtil.saveUeditorFile2Db(filePath);
				ele.attr("src", UeditorUtil.NEW_FILE_PATH_PREFIX + wjid);// 更换数据值
				Elements sourceEle = ele.getElementsByTag("source");
				sourceEle.attr("src", UeditorUtil.NEW_FILE_PATH_PREFIX + wjid);
			} catch (Exception e) {
				e.printStackTrace();
				continue;// 出现异常，则直接忽略该图片
			}
		}

		// 转换一下，只输出body内容
		Element body = doc.body();
		return body.html();
	}
}
