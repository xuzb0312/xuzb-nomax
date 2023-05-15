package com.grace.frame.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.grace.frame.exception.AppException;

/**
 * 前台操作请求和响应方式的封装
 * 
 * @author yjc
 */
public class ActionUtil{

	/**
	 * 向前台书写信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	@SuppressWarnings("null")
	public static void writeMessageToResponse(HttpServletResponse response,
			String data, String charset) {
		response.setContentType("text/html;charset=" + charset);
		if (data == null) {
			data = "";
		}
		PrintWriter out = null;
		try {
			response.setContentLength(data.getBytes(charset).length);
			out = response.getWriter();
			out.print(data);
		} catch (IOException ex) {
			System.out.println("往前台写信息时出现异常，异常信息为" + ex.getMessage());
			out.print("往前台写信息时出现异常，异常信息为" + ex.getMessage());
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 向前台书写信息,按照默认UTF-8的编码格式写
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	public static void writeMessageToResponse(HttpServletResponse response,
			String data) {
		ActionUtil.writeMessageToResponse(response, data, "UTF-8");
	}

	/**
	 * 向前台输出一个datamap数据:前台可以使用HashMap进行处理数据：var map=new HashMap(data);//返回结果
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-6-3
	 * @since V1.0
	 */
	public static void writeDataMapToResponse(HttpServletResponse response,
			DataMap para) throws AppException {
		if (null == para) {
			para = new DataMap();
		}
		ActionUtil.writeMessageToResponse(response, para.toJsonString());
	}

	/**
	 * 向前台输出一个dataset数据;前台可以使用List进行处理数据：var list=new List(data);//返回结果
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-6-3
	 * @since V1.0
	 */
	public static void writeDataSetToResponse(HttpServletResponse response,
			DataSet para) throws AppException {
		if (null == para) {
			para = new DataSet();
		}
		ActionUtil.writeMessageToResponse(response, para.toJsonString());
	}

	/**
	 * 获取远程主机的ip地址
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-14
	 * @since V1.0
	 */
	public static String getRemoteHost(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip != null) {
			/**
			 * https://help.aliyun.com/document_detail/54007.html(有关阿里云的说明)<br>
			 * 真实的客户端IP会被负载均衡放在HTTP头部的X-Forwareded-For字段，<br>
			 * 格式如下： X-Forwarded-For: 用户真实IP, 代理服务器1-IP， 代理服务器2-IP，...
			 */
			String[] iparr = ip.split(",");// 兼容阿里云的负载均衡
			ip = iparr[0].trim();
		}
		   //判断ip地址是否正确，防止xss，sql等的注入
        if (!ActionUtil.isIp(ip)) {
            //表示未知
            ip = "0.0.0.0";
        }

		return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
	}

	/**
	 * IPv4验证正则表达式
	 */
	private static final String IPV4_REGEX = "((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})(\\.((2(5[0-5]|[0-4]\\d))|[0-1]?\\d{1,2})){3}";

	/**
	 * IPv6验证正则表达式
	 */
	private static final String IPV6_REGEX = "(^((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4}){1}|:))|(([0-9A-Fa-f]{1,4}:){6}((:[0-9A-Fa-f]{1,4}){1}|((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){5}((:[0-9A-Fa-f]{1,4}){1,2}|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){4}((:[0-9A-Fa-f]{1,4}){1,3}|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){3}((:[0-9A-Fa-f]{1,4}){1,4}|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){2}((:[0-9A-Fa-f]{1,4}){1,5}|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|(([0-9A-Fa-f]{1,4}:){1}((:[0-9A-Fa-f]{1,4}){1,6}|:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:))|(:((:[0-9A-Fa-f]{1,4}){1,7}|(:[fF]{4}){0,1}:((22[0-3]|2[0-1][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})([.](25[0-5]|2[0-4][0-9]|[0-1][0-9][0-9]|([0-9]){1,2})){3})|:)))$)";

	/**
	 * 验证IP地址是否为IPv6格式
	 * 
	 * @param ipAddress ip地址
	 * @return true格式为ipv6, false不是。
	 */
	private static boolean isIpv6(String ipAddress) {
		if (StringUtil.chkStrNull(ipAddress)) {
			return false;
		}
		ipAddress = Normalizer.normalize(ipAddress, Normalizer.Form.NFKC);
		Pattern pattern = Pattern.compile(ActionUtil.IPV6_REGEX);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	/**
	 * 验证IP地址是否为IPv4格式
	 * 
	 * @param ipAddress ip地址
	 * @return true格式为ipv4, false不是。
	 */
	private static boolean isIpv4(String ipAddress) {
		if (StringUtil.chkStrNull(ipAddress)) {
			return false;
		}
		ipAddress = Normalizer.normalize(ipAddress, Normalizer.Form.NFKC);
		Pattern pattern = Pattern.compile(ActionUtil.IPV4_REGEX);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	/**
	 * 判断ip地址格式是否正确，先判断ipv4后判断ipv6
	 * 
	 * @param ipAddress ip地址
	 * @return true格式为ip, false不是。
	 */
	private static boolean isIp(String ipAddress) {
		if (ActionUtil.isIpv4(ipAddress)) {
			return true;
		}
		return ActionUtil.isIpv6(ipAddress);
	}

	/**
	 * JSONObject转DataMap
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-22
	 * @since V1.0
	 */
	public static DataMap jsonObject2DataMap(JSONObject jo) throws AppException {
		return DataMap.fromObject(jo);
	}

	/**
	 * JSONArray转DataSet
	 * 
	 * @author yjc
	 * @date 创建时间 2017年5月24日
	 * @since V1.0
	 */
	public static DataSet jsonArray2DataSet(JSONArray ja) throws AppException {
		return DataSet.fromObject(ja);
	}
}
