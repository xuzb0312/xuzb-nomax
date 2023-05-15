package com.grace.frame.util;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.safehaus.uuid.UUID;
import org.safehaus.uuid.UUIDGenerator;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;

/**
 * 字符串操作的封装工具方法
 * 
 * @author yjc
 */
public class StringUtil{

	/**
	 * 检查字符串是否为空的
	 * <p>
	 * 对null和""（trim过的）都返回true，其余返回false
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public static boolean chkStrNull(String para) {
		if (null == para || "".equals(para) || "".equals(para.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 将字符串转化为int，不能转化抛异常。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public static double stringToDouble(String s) throws AppException {
		double i = 0;
		if (StringUtil.chkStrNull(s)) {
			throw new AppException("传入参数为空!", "StringUtil");
		}
		s = s.trim();
		try {
			DecimalFormat df = new DecimalFormat("");
			i = df.parse(s).doubleValue();
		} catch (ParseException e) {
			throw new AppException("StringUtil.stringToDouble出错，传入的字符串" + s
					+ "不是一个包涵数字的字符串!", "StringUtil");
		}
		return i;
	}

	/**
	 * 将字符串转化为int，，不能转化抛异常。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public static int stringToInt(String intString) throws AppException {
		int i = 0;
		if (StringUtil.chkStrNull(intString)) {
			throw new AppException("传入参数为空!", "StringUtil");
		}
		intString = intString.trim();
		try {
			DecimalFormat df = new DecimalFormat("");
			i = df.parse(intString).intValue();
		} catch (ParseException e) {
			throw new AppException("StringUtil.stringToInt出错，传入的字符串"
					+ intString + "不是一个包涵数字的字符串!", "StringUtil");
		}
		return i;
	}

	/**
	 * 获取唯一的uiid字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-19
	 * @since V1.0
	 */
	public static String getUUID() {
		UUIDGenerator generator = UUIDGenerator.getInstance();
		UUID uuid = generator.generateRandomBasedUUID();
		String randomString = uuid.toString();
		randomString = randomString.replaceAll("-", "_");
		randomString = "s" + randomString;// 增加一个字符前缀，防止以数字开头的变量报错
		return randomString;
	}

	/**
	 * 解析checkbox的含义
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public static String parseCheckBoxContent(Object para) {
		if (null == para) {
			return "否";
		}
		if (para instanceof Boolean) {
			boolean pt = (Boolean) para;
			if (pt) {
				return "是";
			} else {
				return "否";
			}
		} else if (para instanceof String) {
			String pt = (String) para;
			if ("0".equals(pt)) {
				return "否";
			} else {
				return "是";
			}
		} else if (para instanceof Integer) {
			int pt = (Integer) para;
			if (0 == pt) {
				return "否";
			} else {
				return "是";
			}
		} else if (para instanceof Long) {
			long pt = (Long) para;
			if (0 == pt) {
				return "否";
			} else {
				return "是";
			}
		} else if (para instanceof Double) {
			double pt = (Double) para;
			if (0.00 == pt) {
				return "否";
			} else {
				return "是";
			}
		} else {
			return String.valueOf(para);
		}
	}

	/**
	 * colb数据转string
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws Exception
	 * @date 创建时间 2015-7-28
	 * @since V1.0
	 */
	public static String Colb2String(Clob clob) throws AppException {
		if (null == clob) {
			return "";
		}
		// 解析
		int length = 0;
		char[] chars = new char[1000000];
		length = 0;
		StringBuffer contentBF = new StringBuffer();
		Reader reader;
		try {
			reader = clob.getCharacterStream();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException("解析Clob到String时出错-SQLException", "StringUtil");
		}
		try {
			while ((length = reader.read(chars)) != -1) {
				contentBF.append(new String(chars, 0, length));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException("解析Clob到String时出错-IOException", "StringUtil");
		}
		return contentBF.toString();
	}

	/**
	 * Blob类型数据转String
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static String bolb2String(Blob blob) throws AppException {
		if (null == blob) {
			return "";
		}
		// 解析
		String result = "";
		try {
			result = new String(blob.getBytes((long) 1, (int) blob.length()));
		} catch (Exception e) {
			throw new AppException(e.getCause());
		}
		return result;
	}

	/**
	 * 对2个字符串求交集-字符串使用,隔开的。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-30
	 * @since V1.0
	 */
	public static String mixed2Str(String str1, String str2) {
		if (StringUtil.chkStrNull(str1) || StringUtil.chkStrNull(str2)) {
			return "";
		}
		String[] str1Arr = str1.split(",");
		String[] str2Arr = str2.split(",");
		StringBuffer strBF = new StringBuffer();
		for (int i = 0, n = str1Arr.length; i < n; i++) {
			for (int j = 0, m = str2Arr.length; j < m; j++) {
				if (str1Arr[i].equals(str2Arr[j])) {
					strBF.append(str1Arr[i]).append(",");
					break;
				}
			}
		}
		if (strBF.length() > 0) {
			strBF.setLength(strBF.length() - 1);
		}
		return strBF.toString();
	}

	/**
	 * 转换字符为sql格式字符
	 * <p>
	 * 111,222,333->'111','222','333'
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public static String replaceC2QCQ(String pStr) {
		StringBuffer sbf = new StringBuffer();
		if (pStr == null || "".equals(pStr)) {
			return "";
		}
		pStr = pStr.replaceAll("'", "''");
		sbf.append("'");
		sbf.append(pStr);
		if (",".equalsIgnoreCase(sbf.substring(sbf.length() - 1))) {
			sbf = sbf.deleteCharAt(sbf.length() - 1);
		}
		return sbf.toString().replace(",", "','") + "'";
	}

	/**
	 * 将字符串转化为拼音首字母。
	 */
	public static String getPy(String str) throws AppException {
		return ChnPinyinUtil.getGBKpy(str);
	}

	/**
	 * 合并多个字符串的方法。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-13
	 * @since V1.0
	 */
	public static String combineMultiStr(String... arrStr) {
		StringBuffer strBF = new StringBuffer();
		for (String str : arrStr) {
			strBF.append(str);
		}
		return strBF.toString();
	}

	/**
	 * 合并多个路径，主要用于兼容windows,linux的目录获取拼接。
	 * <p>
	 * 2头不拼接间隔符，根据情况自己手动处理
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-13
	 * @since V1.0
	 */
	public static String combinePath(String... arrPath) {
		if (arrPath.length <= 0) {
			return "";
		}
		StringBuffer strBF = new StringBuffer();
		for (String path : arrPath) {
			strBF.append(path).append(File.separator);
		}
		strBF.setLength(strBF.length() - 1);
		return strBF.toString();
	}

	/**
	 * 对于html代码的过滤，只剩下纯文本
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-18
	 * @since V1.0
	 */
	public static String html2Text(String htmlStr) {
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;

		String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
		String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		textStr = htmlStr;
		return textStr;// 返回文本字符串
	}

	/**
	 * 对于script的脚本进行过滤
	 * 
	 * @author yjc
	 * @date 创建时间 2017-4-19
	 * @since V1.0
	 */
	public static String scriptHtml2SaftText(String scriptHtml) {
		if (StringUtil.chkStrNull(scriptHtml)) {
			return scriptHtml;// 为空的情况直接返回
		}
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;

		p_script = Pattern.compile("<[\\s]*?script[^>]*?>", Pattern.CASE_INSENSITIVE);
		m_script = p_script.matcher(scriptHtml);
		scriptHtml = m_script.replaceAll("&lt;script&gt;"); // 过滤script标签

		p_script = Pattern.compile("<[\\s]*?\\/[\\s]*?script[\\s]*?>", Pattern.CASE_INSENSITIVE);
		m_script = p_script.matcher(scriptHtml);
		scriptHtml = m_script.replaceAll("&lt;/script&gt;"); // 过滤script标签
		return scriptHtml;// 返回文本字符串
	}

	/**
	 * html编码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-17
	 * @since V1.0
	 */
	public static String htmlEncode(String htmlStr) {
		return StringEscapeUtils.escapeHtml(htmlStr);
	}

	/**
	 * html解码
	 * 
	 * @author yjc
	 * @date 创建时间 2015-12-17
	 * @since V1.0
	 */
	public static String htmlDecode(String htmlStr) {
		return StringEscapeUtils.unescapeHtml(htmlStr);
	}

	/**
	 * 格式化数值.format格式参见java.text.DecimalFormat.
	 * 
	 * @throws AppException
	 */

	public static String formatDouble(final double vDouble, final String vFormat) throws AppException {
		if (vFormat == null || vFormat.length() == 0) {
			throw new AppException("未传入vFormat");
		} else {
			return (new DecimalFormat(vFormat)).format(vDouble);
		}
	}

	/**
	 * 获取字符串的长度 <br>
	 * 对于汉字一个汉字认为是2个字符 <br>
	 * str.length一个汉字返回1个字符，有些情况这个不适合
	 * 
	 * @author yjc
	 * @date 创建时间 2016-11-30
	 * @since V1.0
	 */
	public static int getChnStrLen(String chinaString) {
		int len = 0;
		for (int i = 0; i < chinaString.length(); i++) {
			if (((int) chinaString.charAt(i)) > 255)
				len += 2;
			else
				len++;
		}
		return len;
	}

	/**
	 * 主要用于sql的in当超过1000时报错，但是每一条进行执行效率又很低，使用该工具方法把in的个数降到1000一下，同时减少循环次数
	 * 
	 * @author yjc
	 * @date 创建时间 2016-9-13
	 * @since V1.0
	 */
	public static DataSet dealOverlengthIDs(String ids) throws Exception {
		DataSet dsIds = new DataSet();
		if (StringUtil.chkStrNull(ids)) {
			return dsIds;
		}
		StringBuffer strBF = new StringBuffer();
		String[] idsArr = ids.split(",");
		for (int i = 0, n = idsArr.length; i < n; i++) {
			String id = idsArr[i];

			if ((i + 1) % 990 == 0 || i == (n - 1)) {
				if (!StringUtil.chkStrNull(id)) {
					strBF.append(id);
				}
				if (strBF.length() > 0) {
					dsIds.addRow();
					dsIds.put(dsIds.size() - 1, "ids", strBF.toString());
					strBF.setLength(0);
				}
			} else {
				if (!StringUtil.chkStrNull(id)) {
					strBF.append(id).append(",");
				}
			}
		}
		return dsIds;
	}

	/**
	 * 支持当id数量超过1000时的解析方法
	 * 
	 * @author yjc
	 * @date 创建时间 2016-12-1
	 * @since V1.0
	 */
	public static String replaceC2QCQ(String colName, String ids) throws Exception {
		// 如果ID为空，则不进行查询
		if (StringUtil.chkStrNull(ids)) {
			return " 1 = 2 ";
		}
		if (StringUtil.chkStrNull(colName)) {
			throw new BizException("传入的字段名称为空【colName is null】");
		}
		ids = ids.replaceAll("'", "''");
		DataSet dsIds = StringUtil.dealOverlengthIDs(ids);
		StringBuffer strBF = new StringBuffer();
		strBF.append(" (");
		for (int i = 0, n = dsIds.size(); i < n; i++) {
			String idsTemp = dsIds.getString(i, "ids");
			if (StringUtil.chkStrNull(idsTemp)) {
				continue;
			}
			if (i == 0) {
				strBF.append(colName).append(" in ("
						+ StringUtil.replaceC2QCQ(idsTemp) + ")");
			} else {
				strBF.append(" or ").append(colName).append(" in ("
						+ StringUtil.replaceC2QCQ(idsTemp) + ")");
			}
		}
		strBF.append(") ");

		return strBF.toString();
	}

	/**
	 * 获取人民币大写
	 * 
	 * @author yjc
	 * @date 创建时间 2017-6-27
	 * @since V1.0
	 */
	public static String number2CNMontrayUnit(double money) {
		BigDecimal numberOfMoney = new BigDecimal(money);
		return Number2RMB.number2CNMontrayUnit(numberOfMoney);
	}

	/**
	 * 对于html中的图片数据，进行远程获取，然后转换为base64(使用更新的方法进行代替该操作，详见UeditorUtil方法)
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-4
	 * @since V1.0
	 */
	@Deprecated
	public static String dealImg2Base64ForHtmlStr(String htmlStr) {
		if (StringUtil.chkStrNull(htmlStr)) {
			return "";
		}
		Document doc = Jsoup.parse(htmlStr);

		Elements eles = doc.getElementsByTag("img");

		for (Element ele : eles) {
			String src = ele.attr("src");
			if (StringUtil.chkStrNull(src)) {
				continue;
			}
			src = src.trim();
			if (src.startsWith("data") || src.startsWith("DATA")) {
				continue;
			}

			// 远程下载图片
			try {
				String base64ImgStr = FileIOUtil.downloadImg2Base64(src);
				ele.attr("src", base64ImgStr);// 更换数据值
			} catch (Exception e) {
				continue;// 出现异常，则直接忽略该图片
			}
		}

		// 转换一下，只输出body内容
		Element body = doc.body();
		return body.html();
	}

	/**
	 * 对于sql数据的注入字符串进行过滤
	 * 
	 * @author yjc
	 * @date 创建时间 2017-9-4
	 * @since V1.0
	 */
	public static String filterSqlStr(String sqlStr) {
		if (StringUtil.chkStrNull(sqlStr)) {
			return sqlStr;
		}
		String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
		Pattern sqlPattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
		Matcher matcher = sqlPattern.matcher(sqlStr);
		String filterSqlStr = matcher.replaceAll("").trim();
		if (!sqlStr.equals(filterSqlStr)) {
			// 对于检测到注入风险的，进行日志记录，方便后续追踪
			SysLogUtil.logInfo("检测到注入Sql注入风险(系统已经主动拦截)！风险字符串：sqlStr");
		}
		return filterSqlStr;
	}

	/**
	 * 获取域名--根据url地址，获取域名信息（ip）
	 * 
	 * @author yjc
	 * @throws MalformedURLException
	 * @date 创建时间 2017-10-17
	 * @since V1.0
	 */
	public static String getDomain(String urlStr) throws MalformedURLException {
		URL url = new URL(urlStr);
		String host = url.getHost();// 获取主机名
		return host;// 获取到域名，不带协议，不带端口
	}
}
