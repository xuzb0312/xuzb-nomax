package com.grace.frame.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.Sql;
import com.grace.frame.util.SysLogUtil;
import com.grace.frame.util.SysUser;
import com.grace.frame.workflow.BizController;

/**
 * 服务操作的controller
 * 
 * @author yjc
 */
public class ServiceController extends BizController{
	/**
	 * 服务请求的接口
	 * <p>
	 * 请求路径：http://127.0.0.1:8080/project_name/service.do?method=requestService
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-17
	 * @since V1.0
	 */
	public ModelAndView requestService(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String errcode = "0";// 0：没有异常；1:存在异常（其他的异常代码）
		String errtext = "";// 错误说明
		DataMap rdm = new DataMap();

		// 对于服务的请求情况，记录系统日志
		try {
			SysLogUtil.logInfo(ServiceController.class, "**业务请求发起，请求数据："
					+ para.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			rdm = this.requsetServiceFunc(request, response, para);
		} catch (Exception e) {// 截获所有的异常，进行放回给调用端
			errcode = "1";
			errtext = e.getMessage();
		}

		// 向前台书写请求数据:__errcode,__errtext,__data;
		DataMap resultData = new DataMap();
		resultData.put("__errcode", errcode);
		resultData.put("__errtext", errtext);
		resultData.put("__data", rdm);
		if (rdm.containsKey("__sign")) {// 如果没有发生异常，存在签名，则将签名放到外层数据map中
			resultData.put("__sign", rdm.getString("__sign"));
			rdm.remove("__sign");
		}
		ActionUtil.writeMessageToResponse(response, JSONObject.fromObject(resultData)
			.toString());

		// 对于服务的请求情况，记录系统日志
		try {
			SysLogUtil.logInfo(ServiceController.class, "**业务请求结束，返回数据："
					+ resultData.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 请求的服务操作处理类。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-22
	 * @since V1.0
	 */
	private DataMap requsetServiceFunc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String yhbh = para.getString("__yhbh");
		String sign = para.getString("__sign");// 用户签名
		String serviceName = para.getString("__servicename");
		String serviceMethod = para.getString("__servicemethod");
		String data = para.getString("__data");

		// 数据验证
		if (null == yhbh || "".equals(yhbh.trim())) {
			throw new AppException("登录用户名为空");
		}
		if (null == sign || "".equals(sign.trim())) {
			throw new AppException("用户签名为空");
		}
		if (null == serviceName || "".equals(serviceName.trim())) {
			throw new AppException("服务名为空");
		}
		if (null == serviceMethod || "".equals(serviceMethod.trim())) {
			throw new AppException("服务方法为空");
		}
		if (null == data || "".equals(data.trim())) {
			data = "{}";
		}

		// 验证是否配置开启对外的服务
		if (!GlobalVars.IS_START_SERVICE) {
			throw new AppException("请求的服务器程序配置为不对外提供服务，请求拒绝！");
		}

		// 验证签名是否正确--mod.yjc.2017年4月30日
		SysUser currentUser = SysUser.bulidSysUserByYhbh(yhbh);// 查询到用户
		if (null == currentUser) {
			throw new AppException("服务授权用户（" + yhbh + "）在系统中不存在");
		}
		String qmmy = currentUser.getPassword();// 获取签名密钥

		// 生成本地签名
		String local_sign = this.genServiceStrSign(serviceName, serviceMethod, yhbh, data, qmmy);

		// 判断签名是否一致
		if (!local_sign.equals(sign)) {
			throw new AppException("请求签名错误，不允许访问系统");
		}
		if (!"C".equals(currentUser.getYhlx())) {
			throw new AppException("该登录用户为非服务注册用户，不允许访问系统");
		}
		// 判断用户状态
		if ("0".equals(currentUser.getAllInfoDM().getString("yhzt"))) {
			throw new BizException("该用户已经注销，无法进行系统访问。");
		}

		// 验证访问服务的权限--对于服务注册用户使用，验证是否有权限访问该服务
		String serviceKey = currentUser.getYhid() + "." + serviceName + "."
				+ serviceMethod;
		if (!GlobalVars.DEBUG_MODE
				&& GlobalVars.SERVICE_RIGHT_MAP.containsKey(serviceKey)) {
			if (!GlobalVars.SERVICE_RIGHT_MAP.get(serviceKey)) {
				throw new AppException("该服务注册用户无权限访问该服务操作[" + serviceName + "."
						+ serviceMethod + "]");
			}
		} else {
			Sql sql = new Sql();
			sql.setSql(" select yhid from fw.service_right where yhid = ? and fwmc = ? and fwff = ? ");
			sql.setString(1, currentUser.getYhid());
			sql.setString(2, serviceName);
			sql.setString(3, serviceMethod);
			DataSet dsTemp = sql.executeQuery();
			if (dsTemp.size() > 0) {
				GlobalVars.SERVICE_RIGHT_MAP.put(serviceKey, true);
			} else {
				GlobalVars.SERVICE_RIGHT_MAP.put(serviceKey, false);
				throw new AppException("该服务注册用户无权限访问该服务操作[" + serviceName + "."
						+ serviceMethod + "]");
			}
		}

		// 根据serviceName和servicemethod获取到具体执行的biz方法
		String key = serviceName + ":" + serviceMethod;
		if (!GlobalVars.SERVICE_LIST_MAP.containsKey(key)) {
			throw new AppException("在请求的系统中，不存在服务：" + serviceName + "."
					+ serviceMethod + "的注册信息。");
		}
		String[] arrServiceList = GlobalVars.SERVICE_LIST_MAP.get(key);
		String bizName = arrServiceList[0];
		String bizMethod = arrServiceList[1];

		// 得到para参数
		DataMap reqpara = new DataMap();
		reqpara.put("__jbjgid", "00000000");
		reqpara.put("__jbjgqxfw", "00000000");
		reqpara.put("__yhid", "");
		reqpara.put("__ip", para.getString("__ip"));
		reqpara.put("__sysuser", currentUser);
		reqpara.put("__jsondata", data);// 把请求数据也隐藏到jsondata中，方便之后特殊情况使用
		reqpara.put("__request", request);
		reqpara.put("__response", response);

		// data的解析
		JSONObject jsonObj = JSONObject.fromObject(data);

		// 将jsonObj处理到DataMap;只处理到DataSet，DataMap和基本数据类型
		DataMap dataDm = ActionUtil.jsonObject2DataMap(jsonObj);
		dataDm.putAll(reqpara);// 将标准设置放入dataDm,冲掉数据中原先包含的信息

		// 请求biz
		DataMap dm = this.doBizMethod(bizName, bizMethod, dataDm);
		if (null == dm) {
			dm = new DataMap();
		}

		// 生成返回数据的签名(如果出现异常，则不会产生签名)
		String result_sign = this.genServiceStrSign(serviceName, serviceMethod, yhbh, dm.toJsonString()
			.trim(), qmmy);
		dm.put("__sign", result_sign);// 放入签名
		return dm;
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
