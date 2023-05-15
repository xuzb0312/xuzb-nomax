package com.grace.frame.service;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * 框架提供给非使用grace.easyFrame框架的服务请求类；--只允许发送数据，不允许发送二进制文件
 * <p>
 * 需要依赖的jar包文件有：<br>
 * --解析json依赖：<br>
 * commons-beanutils-1.8.0.jar<br>
 * commons-collections.jar<br>
 * commons-lang.jar<br>
 * commons-logging-1.1.1.jar<br>
 * ezmorph-1.0.6.jar<br>
 * json-lib-2.4-jdk15.jar<br>
 * --发送http请求使用：<br>
 * httpclient-4.4.1.jar <br>
 * httpcore-4.4.1.jar<br>
 * httpmime-4.4.1.jar<br>
 * </p>
 * 
 * @author yjc
 */
public class ServiceDAO{
	private String url;// 服务请求地址
	private String yhbh;// 用户编号
	private String yhmy;// 用户密钥-add.yjc.2017年4月30日-改动请求验证方式，使用签名的方式进行验证

	/**
	 * 使用服务请求地址初始化服务对象
	 * 
	 * @param url
	 * @throws Exception
	 */
	public ServiceDAO(String url) throws Exception {
		if (null == url || "".equals(url)) {
			throw new Exception("url不允许为空");
		}
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		this.url = url;
	}

	/**
	 * 登录--只是首先收集用户名和请求密钥，并非真正的登录，只是在发送请求时进行验证连接合法性
	 * <p>
	 * pwd改为用户密钥了-add.yjc.2017年4月30日
	 * </p>
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2015-5-19
	 * @since V1.0
	 */
	public void login(String yhbh, String yhmy) throws Exception {
		if (null == yhbh || "".equals(yhbh)) {
			throw new Exception("yhbh不允许为空");
		}
		if (null == yhmy || "".equals(yhmy)) {
			throw new Exception("用户密钥不允许为空");
		}
		this.yhbh = yhbh;
		this.yhmy = yhmy;
	}

	/**
	 * 发送请求:post方式，请求超时是按毫秒进行计算的
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-19
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public HashMap post4ReturnMap(String serviceName, String serviceMethod,
			HashMap data, int timeout) throws Exception {
		JSONObject json = this.post4ReturnJson(serviceName, serviceMethod, data, timeout);
		HashMap map = this.JSONObject2HashMap(json);
		return map;
	}

	/**
	 * 发送请求:post方式，请求超时是按毫秒进行计算的
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-19
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public JSONObject post4ReturnJson(String serviceName, String serviceMethod,
			HashMap data, int timeout) throws Exception {
		String resStr = "";

		// 数据检测
		if (null == serviceName || "".equals(serviceName.trim())) {
			throw new Exception("服务名称不允许为空");
		}
		if (null == serviceMethod || "".equals(serviceMethod.trim())) {
			throw new Exception("服务方法不允许为空");
		}
		if (null == data) {
			data = new HashMap();
		}
		if (timeout <= 0) {
			throw new Exception("超时时间必须大于0【单位为毫秒】");
		}
		if (null == this.yhbh || "".equals(this.yhbh) || null == this.yhmy
				|| "".equals(this.yhmy)) {
			throw new Exception("未登录，请先登录");
		}

		try {
			// map数据转换
			String str_data = JSONObject.fromObject(data).toString().trim();

			// 生成签名
			String sign = this.genServiceStrSign(serviceName, serviceMethod, this.yhbh, str_data, this.yhmy);

			// 准备请求数据
			List<NameValuePair> paras = new ArrayList<NameValuePair>();
			paras.add(new BasicNameValuePair("__servicename", serviceName));// 服务名
			paras.add(new BasicNameValuePair("__servicemethod", serviceMethod));// 服务方法
			paras.add(new BasicNameValuePair("__yhbh", this.yhbh));// 用户编号
			paras.add(new BasicNameValuePair("__sign", sign));// 签名
			paras.add(new BasicNameValuePair("__data", str_data));// 数据

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paras, "UTF-8");

			// 准备请求
			HttpClient httpClient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(this.url
					+ "service.do?method=requestService");

			httppost.setEntity(entity);// 将数据加入请求中

			// 设置请求超时
			RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(timeout)
				.setConnectTimeout(timeout)
				.setSocketTimeout(timeout)
				.build();
			httppost.setConfig(requestConfig);

			// 执行请求
			HttpResponse response = httpClient.execute(httppost);

			// 获取请求状态
			int resStatu = response.getStatusLine().getStatusCode();

			if (resStatu != HttpStatus.SC_OK) {
				throw new Exception(this.url + "请求失败，错误代码为：" + resStatu);
			} else {
				// 获取返回的数据
				HttpEntity rEntity = response.getEntity();
				resStr = EntityUtils.toString(rEntity);

				// 将返回的数据进行解析到MAP
				if (resStr == null || "".equals(resStr)) {
					resStr = "{}";
				}
				JSONObject json = JSONObject.fromObject(resStr);
				String errcode = json.getString("__errcode");
				String errtext = json.getString("__errtext");
				if (!"0".equals(errcode)) {
					throw new Exception(errcode + ":" + errtext);
				}
				String result_sign = json.getString("__sign");// 返回的签名，进行认证
				String re_local_sign = this.genServiceStrSign(serviceName, serviceMethod, this.yhbh, json.get("__data")
					.toString()
					.trim(), this.yhmy);
				if (!re_local_sign.equals(result_sign)) {
					throw new Exception("返回数据签名错误！");
				}
				return json;
			}
		} catch (ConnectionPoolTimeoutException e) {
			throw new Exception(this.url + "请求超时！原因：" + e.getMessage());
		} catch (ConnectTimeoutException e) {
			throw new Exception(this.url + "请求超时！原因：" + e.getMessage());
		} catch (SocketTimeoutException e) {
			throw new Exception(this.url + "请求超时！原因：" + e.getMessage());
		}
	}

	/**
	 * jsonobject转hashmap
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-21
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	private HashMap JSONObject2HashMap(JSONObject json) throws Exception {
		HashMap map = new HashMap();
		Set<Object> jsonKeys = json.keySet();
		for (Object key : jsonKeys) {
			Object JsonValObj = json.get(key);
			if (JsonValObj instanceof JSONArray) {
				map.put((String) key, JsonToList((JSONArray) JsonValObj));
			} else if (key instanceof JSONObject) {
				map.put((String) key, JSONObject2HashMap((JSONObject) JsonValObj));
			} else {
				map.put((String) key, JsonValObj);
			}
		}
		return map;
	}

	/**
	 * 将JSONArray对象转换成Map-List集合
	 * 
	 * @param jsonArr
	 * @return
	 */
	private Object JsonToList(JSONArray jsonArr) throws Exception {
		List<Object> jsonObjList = new ArrayList<Object>();
		for (Object obj : jsonArr) {
			if (obj instanceof JSONArray) {
				jsonObjList.add(JsonToList((JSONArray) obj));
			} else if (obj instanceof JSONObject) {
				jsonObjList.add(this.JSONObject2HashMap((JSONObject) obj));
			} else {
				jsonObjList.add(obj);
			}
		}
		return jsonObjList;
	}

	/**
	 * 对请求的数据进行签名:首先根据密钥，生产签名sign--密钥将不再传输中传输--使用md5方式签名
	 * <p>
	 * 签名规则：1.将产生拼接，按照：servicename=servicename&servicemethod=
	 * servicemethod&yhbh=yhbh&data=data&yhmy=yhmy；--yhmy为密钥
	 * 使用md5生产32位串，然后转大写-就得到密钥了，然后放到sign参数传递
	 * </p>
	 * 
	 * @author yjc
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @date 创建时间 2017-4-30
	 * @since V1.0
	 */
	private String genServiceStrSign(String serviceName, String serviceMethod,
			String yhbh, String data, String qmmy) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String need_sign_str = "servicename=" + serviceName + "&servicemethod="
				+ serviceMethod + "&yhbh=" + yhbh + "&data=" + data + "&yhmy="
				+ qmmy;// 需要签名的串

		// md5生产签名
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(need_sign_str.getBytes("UTF-8"));
		byte b[] = md.digest();
		int i;
		StringBuffer buf = new StringBuffer();
		for (int offset = 0; offset < b.length; offset++) {
			i = b[offset];
			if (i < 0)
				i += 256;
			if (i < 16)
				buf.append("0");
			buf.append(Integer.toHexString(i));
		}
		String sign = buf.toString();
		sign = sign.toUpperCase();// 转大写
		return sign;
	}
}
