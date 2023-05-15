package com.grace.frame.workflow;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.util.UrlPathHelper;

import com.grace.frame.annotation.PublicAccess;
import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.exception.BoxException;
import com.grace.frame.exception.FieldTipException;
import com.grace.frame.exception.RedirectException;
import com.grace.frame.exception.ServiceException;
import com.grace.frame.util.AccessTokenMap;
import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysLogUtil;
import com.grace.frame.util.SysUser;

/**
 * 对于服务提供--accessToken的方式
 * 
 * @author yjc
 */
public class ServiceServlet extends HttpServlet{
	private static final long serialVersionUID = -7708592642775651846L;
	/**
	 * service_mapping映射
	 */
	private static ConcurrentHashMap<String, String> service_mapping = new ConcurrentHashMap<String, String>();
	private static String version = "0";
	private static boolean isInit = false;// 是否进行了初始化

	/**
	 * 读取xml服务器配置文件
	 * 
	 * @author yjc
	 * @date 创建时间 2019-9-3
	 * @since V1.0
	 */
	private void readCfg2File(String path) throws ServiceException {
		// 读取文件
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(new File(path));
		} catch (Exception e) {
			throw new ServiceException(ServiceException.Req_ERROR, "服务接口配置文件读取异常。错误信息："
					+ e.getMessage());
		}
		// 迭代循环
		Element root = document.getRootElement();
		for (Iterator<?> it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			String eleName = element.getName();
			if ("request".equalsIgnoreCase(eleName)) {
				String pathStr = element.attributeValue("path").trim();
				String classStr = element.attributeValue("class").trim();
				ServiceServlet.service_mapping.put(pathStr, classStr);
				if (pathStr.startsWith("/")) {
					ServiceServlet.service_mapping.put(pathStr.substring(1), classStr);// 对于前缀的自适应适配
				} else {
					ServiceServlet.service_mapping.put("/" + pathStr, classStr);// 对于前缀的自适应适配
				}
			} else if ("config".equalsIgnoreCase(eleName)) {// 配置类
				Iterator<?> configIt = element.elementIterator();
				while (configIt.hasNext()) {
					Element cfgEle = (Element) configIt.next();
					String cfgEleName = cfgEle.getName();
					if ("multipart_resolver".equalsIgnoreCase(cfgEleName)) {// 文件解析设置
						String defaultEncoding = cfgEle.attributeValue("defaultEncoding")
							.trim();
						int maxInMemorySize = Integer.parseInt(cfgEle.attributeValue("maxInMemorySize")
							.trim());
						long maxUploadSize = Long.parseLong(cfgEle.attributeValue("maxUploadSize")
							.trim());
						ServiceServlet.CommonsMultipartResolver_DefaultEncoding = defaultEncoding;
						ServiceServlet.CommonsMultipartResolver_MaxInMemorySize = maxInMemorySize;
						ServiceServlet.CommonsMultipartResolver_MaxUploadSize = maxUploadSize;
					}
				}
			}
		}
	}

	/**
	 * 初始化service_mapping
	 * 
	 * @author yjc
	 * @throws ServiceException
	 * @date 创建时间 2019-11-14
	 * @since V1.0
	 */
	private void initServiceMapping() throws ServiceException {
		ServiceServlet.service_mapping.clear();
		String configLocation = this.getInitParameter("configLocation");
		String path = this.getServletContext().getRealPath(configLocation);
		this.readCfg2File(path);// 读入配置文件
	}

	// 配置文件最后修改时间
	private static long CfgFile_lastModified = 0;

	/**
	 * 检查配置文件是否变更了
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-29
	 * @since V1.0
	 */
	private void checkCfgFileChng() {
		String configLocation = this.getInitParameter("configLocation");
		String path = this.getServletContext().getRealPath(configLocation);
		File cfgFile = new File(path);
		long lastMod = cfgFile.lastModified();
		if (ServiceServlet.CfgFile_lastModified == lastMod) {// 时间相等的情况，直接返回
			return;
		}
		if (ServiceServlet.isInit) {
			System.out.println("===========重置" + configLocation
					+ "配置信息===========");
		}
		ServiceServlet.isInit = false;
		ServiceServlet.CfgFile_lastModified = lastMod;
	}

	/**
	 * 初始化
	 * 
	 * @author yjc
	 * @throws ServiceException
	 * @date 创建时间 2019-11-14
	 * @since V1.0
	 */
	private void initPara() throws ServiceException {
		if (GlobalVars.DEBUG_MODE) {// 在开发调试模式下，检查配置文件是否改动，改动的话，重新加载
			this.checkCfgFileChng();
		}
		if (!ServiceServlet.isInit) {
			this.initServiceMapping();// 初始化service_mapping
			AccessTokenMap.AccessTokenTimeout = Integer.parseInt(this.getInitParameter("accessTokenTimeout"));// 超时时间
			ServiceServlet.version = this.getInitParameter("version");// 版本号
			ServiceServlet.isInit = true;
		}
	}

	/**
	 * 获取ApiController
	 * 
	 * @author yjc
	 * @throws ServiceException
	 * @date 创建时间 2019-11-14
	 * @since V1.0
	 */
	private ApiController getApiController(HttpServletRequest request) throws ServiceException {
		String reqPath = new UrlPathHelper().getPathWithinApplication(request);
		if (!ServiceServlet.service_mapping.containsKey(reqPath)) {
			throw new ServiceException(ServiceException.NotFound_ERROR, "路径："
					+ reqPath + "未配置ApiController映射");
		}
		String sApiController = (String) ServiceServlet.service_mapping.get(reqPath);
		// 实例化对象
		Class<?> apiControllerClass;
		try {
			apiControllerClass = Class.forName(sApiController);
			ApiController apiController = (ApiController) apiControllerClass.newInstance();
			return apiController;
		} catch (ClassNotFoundException e) {
			throw new ServiceException(ServiceException.NotFound_ERROR, "Class:"
					+ sApiController + "没有发现。");
		} catch (InstantiationException e) {
			throw new ServiceException(ServiceException.Req_ERROR, "实例化："
					+ sApiController + "出错：" + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ServiceException(ServiceException.Req_ERROR, "实例化："
					+ sApiController + "出错：" + e.getMessage());
		}
	}

	/**
	 * 获取方法
	 * 
	 * @author yjc
	 * @throws ServiceException
	 * @date 创建时间 2019-11-15
	 * @since V1.0
	 */
	private Method getMethod(HttpServletRequest request,
			ApiController apiController) throws ServiceException {
		// 方法名称
		String methodName = request.getParameter(ParameterMethodNameResolver.DEFAULT_PARAM_NAME);
		if (StringUtil.chkStrNull(methodName)) {
			throw new ServiceException(ServiceException.NotFound_ERROR, "传入的方法名称为空");
		}
		try {
			// 反射调用ApiController方法
			Method method = apiController.getClass()
				.getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class, DataMap.class);
			return method;
		} catch (SecurityException e) {
			throw new ServiceException(ServiceException.Req_ERROR, "方法："
					+ methodName + "获取失败。信息：" + e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new ServiceException(ServiceException.NotFound_ERROR, "方法："
					+ methodName + "不存在。信息：" + e.getMessage());
		}
	}

	/**
	 * 用来处理系统异常类
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	private void handleException(HttpServletRequest request,
			HttpServletResponse response, Throwable ex) {
		DataMap rdm = new DataMap();
		if (ex instanceof InvocationTargetException) {// 对于反射情况，需要获取真正的异常
			ex = ex.getCause();
		}
		if (ex instanceof BizException) {// 业务异常alter提示
			rdm.put("errcode", ServiceException.Biz_ERROR);
			rdm.put("errtext", ex.getMessage());
		} else if (ex instanceof BoxException) {// 前台提示框的样式
			rdm.put("errcode", ServiceException.Box_ERROR);
			rdm.put("errtext", ex.getMessage());
		} else if (ex instanceof RedirectException) {// 重定向异常
			RedirectException rex = (RedirectException) ex;
			rdm.put("errcode", ServiceException.Redirect_ERROR);
			rdm.put("errtext", rex.getErrText());
			rdm.put("redirect_url", rex.getRedirectUrl());
		} else if (ex instanceof FieldTipException) {// 信息项空异常
			FieldTipException fex = (FieldTipException) ex;
			rdm.put("errcode", ServiceException.FieldTip_ERROR);
			rdm.put("errtext", fex.getErrText());
			rdm.put("fieldid", fex.getFieldId());
		} else if (ex instanceof ServiceException) {// 信息项空异常
			ServiceException sex = (ServiceException) ex;
			rdm.put("errcode", sex.getErrcode());
			rdm.put("errtext", sex.getErrtext());
		} else {// 其他类型异常报红错。
			ex.printStackTrace();// 控制台打印错误信息
			// 如将错误日志记录到服务器上
			SysLogUtil.logError(BizController.class, "系统抛出系统级异常："
					+ ex.getMessage() + "[详细数据信息："
					+ request.getParameterMap().toString(), ex);
			String message = ex.getMessage();
			if (message == null) {
				message = "NULL";
			}
			rdm.put("errcode", ServiceException.App_ERROR);
			rdm.put("errtext", ex.getMessage());
		}
		try {
			ActionUtil.writeDataMapToResponse(response, rdm);
		} catch (Exception e) {
			e.printStackTrace();// 控制台打印错误
		}
	}

	/**
	 * get请求
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-14
	 * @since V1.0
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);// 全部执行post请求操作
	}

	/**
	 * 常量参数-文件上传使用
	 */
	private static String CommonsMultipartResolver_DefaultEncoding = "GBK";
	private static int CommonsMultipartResolver_MaxInMemorySize = 52428800;// 50M
	private static long CommonsMultipartResolver_MaxUploadSize = 104857600;// 100M

	/**
	 * 对于文件上传的请求，进行处理转换
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-28
	 * @since V1.0
	 */
	public HttpServletRequest dealMultiRequest(HttpServletRequest request) {
		CommonsMultipartResolver resolver = new CommonsMultipartResolver(this.getServletContext());
		resolver.setDefaultEncoding(ServiceServlet.CommonsMultipartResolver_DefaultEncoding);
		resolver.setMaxInMemorySize(ServiceServlet.CommonsMultipartResolver_MaxInMemorySize);
		resolver.setMaxUploadSize(ServiceServlet.CommonsMultipartResolver_MaxUploadSize);
		if (resolver.isMultipart(request)) {
			MultipartHttpServletRequest multiRequest = resolver.resolveMultipart(request);
			return multiRequest;
		} else {
			return request;
		}
	}

	/**
	 * post请求
	 * <p>
	 * 对于请求：必须包含data和access_token参数。<br>
	 * 对于返回数据：包含data和errcode等参数。文件流方式无返回参数。
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-14
	 * @since V1.0
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			// 初始化配置文件和参数
			this.initPara();

			// 文件上传的适配
			request = this.dealMultiRequest(request);

			// 获取class实例
			ApiController apiController = this.getApiController(request);

			// 提取参数
			DataMap para = this.getPara(request, response);

			// 初始化操作
			apiController.init(para, request, response);

			// 反射调用ApiController方法
			Method method = this.getMethod(request, apiController);

			// 判断登录权限
			if (!method.isAnnotationPresent(PublicAccess.class)) {// 没有进行注解的话，验证权限
				String accessValidationClassName = this.getInitParameter("accessValidationClass");// 访问验证类
				try {
					Class<?> accessValidationClass = Class.forName(accessValidationClassName);
					ServiceAccessValidation sav = (ServiceAccessValidation) accessValidationClass.newInstance();
					boolean isAllowAccess = sav.verifyAccess(apiController.getClass()
						.getName(), method.getName(), request, response, AccessTokenMap.fromRequest(request), para);
					if (!isAllowAccess) {
						throw new ServiceException(ServiceException.Forbidden_ERROR, "访问被拒绝，请检查您是否有权限访问本服务或者是否登录。");
					}
				} catch (ClassNotFoundException e) {
					throw new ServiceException(ServiceException.Req_ERROR, "访问认证Class:"
							+ accessValidationClassName + "未发现，请检查。");
				}
			}

			// 反射调用
			Object[] paras = { request, response, para };
			DataMap rdm = (DataMap) method.invoke(apiController, paras);

			// 对于返回为空的情况，主要适用于文件流的输出
			if (null != rdm) {
				DataMap reslut = new DataMap();
				reslut.put("errcode", ServiceException.No_ERROR);// 未发生异常
				reslut.put("errtext", "");
				if (method.isAnnotationPresent(Deprecated.class)) {// 对于使用过期标志修饰的接口方法，需要在调用方返回一个告知信息
					reslut.put("dev_info", "本服务接口已经被服务端开发人员标记为不赞成使用的[Deprecated]，请尽快联系开发人员获取最新替代方案。");
				}
				reslut.put("data", rdm);
				ActionUtil.writeDataMapToResponse(response, reslut);// 其他情况，会直接封装数据进行输出
			}
		} catch (Exception e) {
			this.handleException(request, response, e);
		} finally {
			try {
				// 对于redis缓存会话数据的处理
				AccessTokenMap atm = AccessTokenMap.fromRequest(request);
				if (null != atm) {
					atm.sync();// 数据同步
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				SysLogUtil.logError("同步redis缓存会话信息失败。" + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * 获取参数[所有的请求，都需要包含2块的参数，access_token：请求令牌。data：请求数据]
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws ServiceException
	 * @date 创建时间 2019-11-14
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	private DataMap getPara(HttpServletRequest request,
			HttpServletResponse response) throws AppException, ServiceException {
		DataMap dm = new DataMap();
		Map paraMap = request.getParameterMap();

		Object[] keyArray = paraMap.keySet().toArray();
		for (int i = 0; i < paraMap.size(); i++) {
			String paraName = (String) keyArray[i];
			String paraValue = request.getParameter(paraName);
			if (!paraName.equals(ParameterMethodNameResolver.DEFAULT_PARAM_NAME)) {
				String[] paraValues = request.getParameterValues(paraName);
				if (paraValues == null || paraValues.length == 0) {
					dm.put(paraName, null);
				} else if (paraValues.length == 1) {
					dm.put(paraName, StringUtil.scriptHtml2SaftText(paraValue));// 对于script代码的移除-mod.yjc.2017年4月19日
				} else {
					StringBuffer sb = new StringBuffer();
					for (int index = 0; index < paraValues.length; index++) {
						sb.append(paraValues[index]);
						if (index != (paraValues.length - 1)) {
							sb.append(",");
						}
					}
					dm.put(paraName, StringUtil.scriptHtml2SaftText(sb.toString()));// 对于script代码的移除-mod.yjc.2017年4月19日
				}
			}
		}

		// 真正数据
		String data = dm.getString("data", "{}");
		String access_token = dm.getString("access_token", "");// 请求token
		String version = dm.getString("version", "");
		if (StringUtil.chkStrNull(data)) {
			data = "{}";
		}
		// 版本号一致性判断
		if (!ServiceServlet.version.equalsIgnoreCase(version)) {
			throw new ServiceException(ServiceException.Version_ERROR, "版本号不匹配[请求版本号:"
					+ version + ";应用服务版本号:" + ServiceServlet.version + "]");
		}
		DataMap para = DataMap.fromObject(data);
		para = this.dealDirectPara(para, dm);// 处理直传参数

		// 处理上传的文件
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest mReq = (MultipartHttpServletRequest) request;
			Map fileMap = mReq.getFileMap();
			Object[] fileKeyArray = fileMap.keySet().toArray();
			for (int i = 0; i < fileMap.size(); i++) {
				String paraName = (String) fileKeyArray[i];
				para.put(paraName, fileMap.get(paraName));// 放到para中
			}
		}

		// 其他的辅助参数-框架使用
		String jbjgid = dm.getString("__jbjgid", "00000000");
		String jbjgqxfw = dm.getString("__jbjgqxfw", "00000000");
		String yhid = dm.getString("__yhid", "");
		jbjgid = StringUtil.html2Text(jbjgid)
			.replaceAll("\"", "")
			.replace("'", "");// 过滤非法字符
		jbjgqxfw = StringUtil.html2Text(jbjgqxfw)
			.replaceAll("\"", "")
			.replace("'", "");// 过滤非法字符
		yhid = StringUtil.html2Text(yhid).replaceAll("\"", "").replace("'", "");// 过滤非法字符
		SysUser currentSysUser = (SysUser) request.getSession()
			.getAttribute("currentsysuser");// 当前用户
		if (null == currentSysUser) {
			currentSysUser = new SysUser();
		}

		// 放入参数
		para.put("__ip", ActionUtil.getRemoteHost(request));
		para.put("__jbjgid", jbjgid);
		para.put("__jbjgqxfw", jbjgqxfw);
		para.put("__yhid", yhid);
		para.put("__sysuser", currentSysUser);
		para.put("__request", request);
		para.put("__response", response);
		para.put("__access_token", access_token);

		// 部分request参数处理
		AccessTokenMap atm = null;
		if (!StringUtil.chkStrNull(access_token)) {
			atm = new AccessTokenMap(access_token);// 未初始化状态
			atm.reset2Request(request);// 设置atm到request
		}
		return para;
	}

	/**
	 * 处理直传参数
	 * <p>
	 * 对于dm中的非data,access_token,version,__jbjgid,__jbjgqxfw,__yhid放入para中-
	 * 但是如果para中存在此参数，则覆盖
	 * </p>
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-12-4
	 * @since V1.0
	 */
	private DataMap dealDirectPara(DataMap para, DataMap dm) throws AppException {
		Object[] arrKey = dm.keySet().toArray();
		for (int i = 0, n = arrKey.length; i < n; i++) {
			String oneKey = (String) arrKey[i];
			if ("data".equalsIgnoreCase(oneKey)
					|| "access_token".equalsIgnoreCase(oneKey)
					|| "version".equalsIgnoreCase(oneKey)
					|| "__jbjgid".equalsIgnoreCase(oneKey)
					|| "__jbjgqxfw".equalsIgnoreCase(oneKey)
					|| "__yhid".equalsIgnoreCase(oneKey)) {
				continue;
			}
			Object value = dm.get(oneKey);
			para.put(oneKey, value);
		}
		return para;
	}
}