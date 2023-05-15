package com.grace.frame.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.Blob;
import java.sql.Clob;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import sun.misc.BASE64Encoder;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;

/**
 * 用于来使用模板生成word文件（模板使用打印模板）--标记方法与打印模板一直，<br>
 * 但是对于循环，不支持支持行数生成（即不支持：@$loop{dstest:20}中的20参数。）
 * 
 * @author yjc
 */
public class WordCreater{
	// 属性
	private String gsnr;// 格式内容
	private String gsnrTmp;// 临时的格式内容
	private HashMap<String, Object> mapKeys;// 打印格式中的所有keys
	private HashMap<String, DataSet> mapDs;// 用于存储循环数据的内容

	// 常量
	public final static String PAGE_BREAK_STR = "<w:p><w:pPr><w:spacing w:line=\"14\" w:lineRule=\"exact\"/></w:pPr><w:r><w:br w:type=\"page\"/></w:r></w:p>";// 换页符--强制换页符
	public final static String NEW_LINE_STR = "<w:br/>";// 换行-强制换行

	/**
	 * 根据格式类型编号获取到gsid的工具方法--如果获取到多条则报错。
	 * 
	 * @author yjc
	 * @date 创建时间 2016-1-25
	 * @since V1.0
	 */
	public static String getGsidByGslxbh(String gslxbh) throws Exception {
		Sql sql = new Sql();
		sql.setSql(" select gsid from fw.print_model where gslxbh = ? and gszt = '1' ");
		sql.setString(1, gslxbh);
		DataSet dsTemp = sql.executeQuery();
		if (dsTemp.size() <= 0) {
			throw new BizException("对于格式类型编号" + gslxbh + "的格式在系统中没有找到");
		} else if (dsTemp.size() > 1) {
			throw new BizException("对于格式类型编号" + gslxbh + "的格式在系统中找到多条，请检查。");
		}
		return dsTemp.getString(0, "gsid");
	}

	/**
	 * 根据格式类型编号获取到gsid的工具方法--如果获取到多条则报错。
	 * 
	 * @author yjc
	 * @date 创建时间 2016-1-25
	 * @since V1.0
	 */
	public static String getGsidByGslxbh(String jbjgid, String gslxbh) throws Exception {
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select gsid ");
		sqlBF.append("   from fw.print_model a ");
		sqlBF.append("  where a.gslxbh = ? ");
		sqlBF.append("    and a.gszt = '1' ");// 状态为启用的
		sqlBF.append("    and exists (select '1' ");
		sqlBF.append("           from fw.print_config b ");
		sqlBF.append("          where a.gsid = b.gsid ");
		sqlBF.append("            and b.jbjgid = ?) ");
		sql.setSql(sqlBF.toString());
		sql.setString(1, gslxbh);
		sql.setString(2, jbjgid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			throw new BizException("对于格式类型编号" + gslxbh + "的格式在系统中没有找到");
		} else if (ds.size() > 1) {
			throw new BizException("对于格式类型编号" + gslxbh + "的格式在系统中找到多条，请检查。");
		}
		return ds.getString(0, "gsid");
	}

	/**
	 * 获取实体信息--根据参数gslxbh
	 * 
	 * @author yjc
	 * @date 创建时间 2016-1-25
	 * @since V1.0
	 */
	public static WordCreater getInstance(String gslxbh) throws Exception {
		return new WordCreater(WordCreater.getGsidByGslxbh(gslxbh));
	}

	/**
	 * 获取实体信息--根据参数gslxbh
	 * 
	 * @author yjc
	 * @date 创建时间 2016-1-25
	 * @since V1.0
	 */
	public static WordCreater getInstance(String jbjgid, String gslxbh) throws Exception {
		return new WordCreater(jbjgid, WordCreater.getGsidByGslxbh(jbjgid, gslxbh));
	}

	/**
	 * 构造函数-因为格式ID为唯一主键，所有只传id即可。
	 */
	public WordCreater(String gsid) throws AppException {
		if (StringUtil.chkStrNull(gsid)) {
			throw new AppException("传入的gsid为空", "WordCreater");
		}

		// 根据以上两项获取格式模板内容
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select gsnr ");
		sqlBF.append("   from fw.print_model a ");
		sqlBF.append("  where a.gsid = ? ");
		sql.setSql(sqlBF.toString());
		sql.setString(1, gsid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			throw new AppException("打印格式ID=" + gsid + "在系统数据库中不存在。", "WordCreater");
		}
		Clob clob = ds.getClob(0, "gsnr");
		this.initWordCreater(StringUtil.Colb2String(clob));// 初始化格式内容
	}

	/**
	 * 构造函数--业务系统使用，可以协助验证格式合法性
	 */
	public WordCreater(String jbjgid, String gsid) throws AppException {
		if (StringUtil.chkStrNull(jbjgid)) {
			throw new AppException("传入的jbjgid为空", "WordCreater");
		}
		if (StringUtil.chkStrNull(gsid)) {
			throw new AppException("传入的gsid为空", "WordCreater");
		}

		// 根据以上两项获取格式模板内容
		Sql sql = new Sql();
		StringBuffer sqlBF = new StringBuffer();
		sqlBF.append(" select gsnr ");
		sqlBF.append("   from fw.print_model a ");
		sqlBF.append("  where a.gsid = ? ");
		sqlBF.append("    and a.gszt = '1' ");// 状态为启用的
		sqlBF.append("    and exists (select '1' ");
		sqlBF.append("           from fw.print_config b ");
		sqlBF.append("          where a.gsid = b.gsid ");
		sqlBF.append("            and b.jbjgid = ?) ");
		sql.setSql(sqlBF.toString());
		sql.setString(1, gsid);
		sql.setString(2, jbjgid);
		DataSet ds = sql.executeQuery();
		if (ds.size() <= 0) {
			throw new AppException("打印格式ID=" + gsid + ";经办机构ID=" + jbjgid
					+ "在系统数据库中不存在或未启用。", "WordCreater");
		}
		Clob clob = ds.getClob(0, "gsnr");
		this.initWordCreater(StringUtil.Colb2String(clob));// 初始化格式内容
	}

	/**
	 * 直接传入格式内容进行初始化的。
	 * 
	 * @throws BizException
	 */
	public WordCreater(Clob gsnr) throws AppException, BizException {
		if (null == gsnr) {
			throw new BizException("传入的给事内容clob为空");
		}
		this.initWordCreater(StringUtil.Colb2String(gsnr));
	}

	/**
	 * 初始化--不对外提供，只提供给getInstanceByGsnr使用
	 */
	private WordCreater() {
		this.gsnr = "";
	}

	/**
	 * 根据格式内容字符串初始化一个打印对象
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-2
	 * @since V1.0
	 */
	public static WordCreater getInstanceByGsnr(String gsnr) throws Exception {
		if (StringUtil.chkStrNull(gsnr)) {
			throw new BizException("传入的格式内容为空");
		}
		WordCreater word = new WordCreater();
		word.initWordCreater(gsnr);
		return word;
	}

	/**
	 * 根据格式内容初始化打印格式。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-7
	 * @since V1.0
	 */
	private void initWordCreater(String gsnr) {
		this.gsnr = gsnr;

		// 对gsnr的数据进行默认出来-替换换页符@{page_break}
		this.gsnr = this.gsnr.replace("@{page_break}", WordCreater.PAGE_BREAK_STR);
		this.gsnr = this.gsnr.replace("@{PAGE_BREAK}", WordCreater.PAGE_BREAK_STR);

		// 得到key数组
		this.mapKeys = this.parseCreaterKeys();
		this.gsnrTmp = this.gsnr;// 与格式内容一致

		// 循环数据
		this.mapDs = new HashMap<String, DataSet>();// 初始化一下
	}

	/**
	 * 解析所有的key值字符串
	 * 
	 * @日期：2015年2月8日
	 */
	private HashMap<String, Object> parseCreaterKeys() {
		Pattern pattern = Pattern.compile("@\\{(.+?)\\}");
		Matcher matcher = pattern.matcher(this.gsnr);
		HashMap<String, Object> map = new HashMap<String, Object>();// 将key值解析出来-并去除重复
		while (matcher.find()) {
			String key = matcher.group();
			key = key.substring(2, key.length() - 1);
			map.put(key, null);
		}
		return map;
	}

	/**
	 * 清空数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-28
	 * @since V1.0
	 */
	public void clear() {
		Object[] keyArr = this.mapKeys.keySet().toArray();
		for (int i = 0, n = keyArr.length; i < n; i++) {
			this.mapKeys.put((String) keyArr[i], null);
		}
		this.gsnrTmp = this.gsnr;// 与格式内容一致
		this.mapDs.clear();// 数据清空
	}

	/**
	 * 向打印对象中放置循环数据
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-12
	 * @since V1.0
	 */
	public void putDataSet(String key, DataSet ds) throws Exception {
		if (ds == null) {
			ds = new DataSet();
		}
		this.mapDs.put(key, ds);
	}

	/**
	 * 向WordCreater中放置数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-28
	 * @since V1.0
	 */
	public void putPara(String key, Object value) {
		if (this.mapKeys.containsKey(key)) {
			this.mapKeys.put(key, value);
		}
	}

	/**
	 * 图片Blob
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-7-28
	 * @since V1.0
	 */
	public void putImage(String key, Blob imgBlob) throws AppException {
		try {
			this.putImage(key, imgBlob.getBytes(1L, (int) imgBlob.length()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException("BLOB转化图片过程中出现异常！", "WordCreater");
		}
	}

	/**
	 * 图片byte流
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-28
	 * @since V1.0
	 */
	public void putImage(String key, byte[] imgByte) {
		BASE64Encoder encode = new BASE64Encoder();
		String imgBase64 = encode.encode(imgByte);
		this.putImage(key, imgBase64);
	}

	/**
	 * 放置图片
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-28
	 * @since V1.0
	 */
	public void putImage(String key, String base64Img) {
		if (this.mapKeys.containsKey(key) && !StringUtil.chkStrNull(base64Img)) {
			this.mapKeys.put(key, base64Img);
		}
	}

	/**
	 * 打印二维码
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void putQrcode(String key, String code) throws Exception {
		if (StringUtil.chkStrNull(code)) {
			return;
		}
		if (this.mapKeys.containsKey(key)) {
			String barcodeImg = QrCodeUtil.getQrcodeImg(8, code);// 定死为8的级别
			this.putImage(key, barcodeImg);
		}
	}

	/**
	 * 打印条形码(128码-带文字)
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void put128BarcodeWidthCode(String key, String barcode, int width,
			int height) throws Exception {
		if (width <= 0) {
			throw new AppException("宽度必须大于0");
		}
		if (height <= 0) {
			throw new AppException("高度必须大于0");
		}
		if (StringUtil.chkStrNull(barcode)) {
			return;
		}
		if (this.mapKeys.containsKey(key)) {
			String barcodeImg = BarCodeUtil.get128BarImgWithCode(width, height, barcode);
			this.putImage(key, barcodeImg);
		}
	}

	/**
	 * 打印条形码(128码-带文字)--使用jbarcode插件获取条码
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void put128JbBarcodeWidthCode(String key, String barcode) throws Exception {
		if (StringUtil.chkStrNull(barcode)) {
			return;
		}
		if (this.mapKeys.containsKey(key)) {
			String barcodeImg = BarCodeUtil.gen128JbBarImg(barcode);
			this.putImage(key, barcodeImg);
		}
	}

	/**
	 * 打印条形码(128码-不带文字)
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void put128Barcode(String key, String barcode, int width, int height) throws Exception {
		if (width <= 0) {
			throw new AppException("宽度必须大于0");
		}
		if (height <= 0) {
			throw new AppException("高度必须大于0");
		}
		if (StringUtil.chkStrNull(barcode)) {
			return;
		}
		if (this.mapKeys.containsKey(key)) {
			String barcodeImg = BarCodeUtil.get128BarImg(width, height, barcode);
			this.putImage(key, barcodeImg);
		}
	}

	/**
	 * 打印条形码(39码)
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-12-25
	 * @since V1.0
	 */
	public void put39Barcode(String key, String barcode, int width, int height) throws Exception {
		if (width <= 0) {
			throw new AppException("宽度必须大于0");
		}
		if (height <= 0) {
			throw new AppException("高度必须大于0");
		}
		if (StringUtil.chkStrNull(barcode)) {
			return;
		}
		if (this.mapKeys.containsKey(key)) {
			String barcodeImg = BarCodeUtil.get39BarImg(width, height, barcode);
			this.putImage(key, barcodeImg);
		}
	}

	/**
	 * 打印的html字符串信息
	 * 
	 * @throws AppException
	 * @日期：2015年1月28日
	 */
	private String getXmlContent() throws AppException {
		// 对于单值对象的解析
		Object[] keyArr = this.mapKeys.keySet().toArray();
		for (int i = 0, n = keyArr.length; i < n; i++) {
			String key = (String) keyArr[i];
			Object value = this.mapKeys.get(key);
			String strValue = "";
			if (null != value) {
				strValue = value.toString();
			}
			this.gsnrTmp = this.gsnrTmp.replace("@{" + key + "}", strValue);
		}

		// 循环对象的解析
		String[] arrGsnr = this.gsnrTmp.split("@\\$loop");
		if (arrGsnr.length > 1) {
			// 存在循环对象，需要进行解析一下
			StringBuffer gsnrBF = new StringBuffer();
			for (int i = 0, n = arrGsnr.length; i < n; i++) {
				String gsnr_one = arrGsnr[i];
				if (gsnr_one.startsWith("{@")) {// 循环体
					int loopKeyIndexEnd = gsnr_one.indexOf("}");
					String gsnr_key = gsnr_one.substring(2, loopKeyIndexEnd);
					String gsnr_content = gsnr_one.substring(loopKeyIndexEnd + 1);

					String prePartStr = "";
					String suffPartStr = "";
					if (i >= 1) {
						prePartStr = arrGsnr[i - 1];
						String[] head = prePartStr.split("@head");
						if (head.length > 1) {
							prePartStr = head[1];
						} else {
							prePartStr = "";
						}
					}
					if (i < n - 1) {
						suffPartStr = arrGsnr[i + 1];
						String[] end = suffPartStr.split("@end");
						if (end.length > 1) {
							suffPartStr = end[0];
						}
					}
					// 解析结果的拼接
					gsnrBF.append(this.dealOneGsnr(gsnr_key, gsnr_content, prePartStr, suffPartStr));
				} else {
					gsnrBF.append(gsnr_one);
				}
			}
			this.gsnrTmp = gsnrBF.toString();
		}
		return this.gsnrTmp;
	}

	/**
	 * 对于循环数据的处理操作
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2016-8-12
	 * @since V1.0
	 */
	private String dealOneGsnr(String key, String gsnr, String prePartStr,
			String suffPartStr) throws AppException {
		StringBuffer strBF = new StringBuffer();
		String[] arrParaKey = key.split(":");
		int rows = -1;
		if (arrParaKey.length > 1) {
			key = arrParaKey[0];
			rows = Integer.parseInt(arrParaKey[1]);
		}
		if (this.mapDs.containsKey(key)) {
			HashMap<String, Object> keysArr = this.parseCreaterLoopKeys(gsnr);
			DataSet dsLoop = this.mapDs.get(key);
			for (int i = 0, n = dsLoop.size(); i < n; i++) {
				String gsnrTemp = gsnr;
				DataMap dmOne = dsLoop.get(i);
				Object[] keyArr = keysArr.keySet().toArray();
				for (int j = 0, m = keyArr.length; j < m; j++) {
					String one_key = (String) keyArr[j];
					Object one_value = dmOne.get(one_key, "");
					String strValue = "";
					if (null != one_value) {
						strValue = one_value.toString();
					}
					gsnrTemp = gsnrTemp.replace("${" + one_key + "}", strValue);
				}
				strBF.append(gsnrTemp);

				// 分页操作
				if (rows > 0) {
					if ((i + 1) % rows == 0 && i < n - 1) {
						strBF.append(suffPartStr);
						strBF.append(WordCreater.PAGE_BREAK_STR);// 换页
						strBF.append(prePartStr);
					}
				}
			}
		}
		return strBF.toString();
	}

	/**
	 * 解析所有的key值字符串--循环数据
	 */
	private HashMap<String, Object> parseCreaterLoopKeys(String gsnrLoop) {
		Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
		Matcher matcher = pattern.matcher(gsnrLoop);
		HashMap<String, Object> map = new HashMap<String, Object>();// 将key值解析出来-并去除重复
		while (matcher.find()) {
			String key = matcher.group();
			key = key.substring(2, key.length() - 1);
			map.put(key, null);
		}
		return map;
	}

	/**
	 * 生成word文件
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws IOException
	 * @date 创建时间 2017-8-8
	 * @since V1.0
	 */
	public void createWordFile(OutputStream os) throws Exception {
		if (null == os) {
			throw new BizException("输出流为空");
		}
		String wordContentXml = this.getXmlContent();
		os.write(wordContentXml.getBytes("UTF-8")); // 输出数据
		os.flush();
	}

	/**
	 * word文档写到响应中
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2017-8-8
	 * @since V1.0
	 */
	public void exportWord2Response(HttpServletResponse response,
			String fileName) throws Exception {
		if (null == response) {
			throw new AppException("response为空");
		}
		if (StringUtil.chkStrNull(fileName)) {
			throw new AppException("fileName为空");
		}

		// 文件下载的操作
		fileName = fileName + ".doc";
		response.resetBuffer();
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.setContentType("application/msword;charset=UTF-8");
		response.addHeader("Content-Disposition", "attachment; filename="
				+ fileName);

		// 向输出流上写文件
		OutputStream os = response.getOutputStream();
		try {
			this.createWordFile(os);
		} finally {
			try {
				if (os != null) {
					os.flush();
					os.close();
				}
			} catch (Exception e) {
				throw new AppException("构建Word异常!错误信息为：" + e.getMessage());
			}
		}
	}
}
