package com.grace.frame.workflow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.LastModified;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import com.grace.frame.annotation.PublicAccess;
import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.localize.LocalHandler;
import com.grace.frame.login.LoginUtil;
import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.ProgressBar;
import com.grace.frame.util.SecUtil;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysUser;

/**
 * 对spring的MultiActionController进行本地化的定制处理
 * 
 * @author yjc
 */
@SuppressWarnings("unchecked")
public class MultiActionController extends AbstractController implements LastModified{
	public static final String LAST_MODIFIED_METHOD_SUFFIX = "LastModified";
	public static final String DEFAULT_COMMAND_NAME = "command";
	public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";
	protected static final Log pageNotFoundLogger = LogFactory.getLog("org.springframework.web.servlet.PageNotFound");
	private Object delegate;
	// 改动InternalPathMethodNameResolver为ParameterMethodNameResolver
	private MethodNameResolver methodNameResolver = new ParameterMethodNameResolver();
	private Validator[] validators;
	private WebBindingInitializer webBindingInitializer;

	private final Map handlerMethodMap = new HashMap();

	private final Map lastModifiedMethodMap = new HashMap();

	private final Map exceptionHandlerMap = new HashMap();

	public MultiActionController() {
		this.delegate = this;
		registerHandlerMethods(this.delegate);
	}

	public MultiActionController(Object delegate) {
		setDelegate(delegate);
	}

	public final void setDelegate(Object delegate) {
		Assert.notNull(delegate, "Delegate must not be null");
		this.delegate = delegate;
		registerHandlerMethods(this.delegate);

		if (this.handlerMethodMap.isEmpty())
			throw new IllegalStateException("No handler methods in class ["
					+ this.delegate.getClass() + "]");
	}

	public final void setMethodNameResolver(
			MethodNameResolver methodNameResolver) {
		this.methodNameResolver = methodNameResolver;
	}

	public final MethodNameResolver getMethodNameResolver() {
		return this.methodNameResolver;
	}

	public final void setValidators(Validator[] validators) {
		this.validators = validators;
	}

	public final Validator[] getValidators() {
		return this.validators;
	}

	public final void setWebBindingInitializer(
			WebBindingInitializer webBindingInitializer) {
		this.webBindingInitializer = webBindingInitializer;
	}

	public final WebBindingInitializer getWebBindingInitializer() {
		return this.webBindingInitializer;
	}

	private void registerHandlerMethods(Object delegate) {
		this.handlerMethodMap.clear();
		this.lastModifiedMethodMap.clear();
		this.exceptionHandlerMap.clear();

		Method[] methods = delegate.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (isExceptionHandlerMethod(method)) {
				registerExceptionHandlerMethod(method);
			} else if (isHandlerMethod(method)) {
				registerHandlerMethod(method);
				registerLastModifiedMethodIfExists(delegate, method);
			}
		}
	}

	private boolean isHandlerMethod(Method method) {
		Class returnType = method.getReturnType();
		if ((ModelAndView.class.equals(returnType))
				|| (Map.class.equals(returnType))
				|| (String.class.equals(returnType))
				|| (Void.TYPE.equals(returnType))) {
			Class[] parameterTypes = method.getParameterTypes();
			return (parameterTypes.length >= 2)
					&& (HttpServletRequest.class.equals(parameterTypes[0]))
					&& (HttpServletResponse.class.equals(parameterTypes[1]))
					&& ((!"handleRequest".equals(method.getName())) || (parameterTypes.length != 2));
		}

		return false;
	}

	private boolean isExceptionHandlerMethod(Method method) {
		return (isHandlerMethod(method))
				&& (method.getParameterTypes().length == 3)
				&& (Throwable.class.isAssignableFrom(method.getParameterTypes()[2]));
	}

	private void registerHandlerMethod(Method method) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Found action method [" + method + "]");
		}
		this.handlerMethodMap.put(method.getName(), method);
	}

	private void registerLastModifiedMethodIfExists(Object delegate,
			Method method) {
		try {
			Method lastModifiedMethod = delegate.getClass()
				.getMethod(method.getName() + "LastModified", new Class[] { HttpServletRequest.class });

			Class returnType = lastModifiedMethod.getReturnType();
			if ((!Long.TYPE.equals(returnType))
					&& (!Long.class.equals(returnType))) {
				throw new IllegalStateException("last-modified method ["
						+ lastModifiedMethod
						+ "] declares an invalid return type - needs to be 'long' or 'Long'");
			}

			this.lastModifiedMethodMap.put(method.getName(), lastModifiedMethod);
			if (this.logger.isDebugEnabled())
				this.logger.debug("Found last-modified method for handler method ["
						+ method + "]");
		} catch (NoSuchMethodException ex) {
		}
	}

	private void registerExceptionHandlerMethod(Method method) {
		this.exceptionHandlerMap.put(method.getParameterTypes()[2], method);
		if (this.logger.isDebugEnabled())
			this.logger.debug("Found exception handler method [" + method + "]");
	}

	public long getLastModified(HttpServletRequest request) {
		try {
			String handlerMethodName = this.methodNameResolver.getHandlerMethodName(request);
			Method lastModifiedMethod = (Method) this.lastModifiedMethodMap.get(handlerMethodName);
			if (lastModifiedMethod != null) {
				try {
					Long wrappedLong = (Long) lastModifiedMethod.invoke(this.delegate, new Object[] { request });
					return wrappedLong != null ? wrappedLong.longValue() : -1L;
				} catch (Exception ex) {
					this.logger.error("Failed to invoke last-modified method", ex);
				}
			}

		} catch (NoSuchRequestHandlingMethodException ex) {
		}

		return -1L;
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try {
			String methodName = this.methodNameResolver.getHandlerMethodName(request);
			return invokeNamedMethod(methodName, request, response);
		} catch (NoSuchRequestHandlingMethodException ex) {
			return handleNoSuchRequestHandlingMethod(ex, request, response);
		}
	}

	protected ModelAndView handleNoSuchRequestHandlingMethod(
			NoSuchRequestHandlingMethodException ex,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		pageNotFoundLogger.warn(ex.getMessage());
		response.sendError(404);
		return null;
	}

	protected final ModelAndView invokeNamedMethod(String methodName,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Method method = (Method) this.handlerMethodMap.get(methodName);
		if (method == null) {
			throw new NoSuchRequestHandlingMethodException(methodName, getClass());
		}
		ProgressBar pgBar = null;// 进度条对象-在方法调用结束后，使用finish方法结束进度条。
		HashMap<String, String> sessionRemoveMap = new HashMap<String, String>(); // 请求完成后台后，没有发生异常后，进行session元素的移除操作。
		try {
			Class[] paramTypes = method.getParameterTypes();
			List params = new ArrayList(4);
			params.add(request);
			params.add(response);

			// 增加para参数-根据合适的情况再进行调整。
			String jbjgid = "00000000";
			String jbjgqxfw = "00000000";
			String yhid = "";
			if ((paramTypes.length >= 3)
					&& (paramTypes[2].equals(DataMap.class))) {
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

						if (paraName.startsWith("__grid_")) {// 如果数据为数据表格的。将其转换为DataSet;前台的请求一定要注意使用__grid_前缀
							DataMap dmListTmp = ActionUtil.jsonObject2DataMap(JSONObject.fromObject(dm.get(paraName)));
							dm.put(paraName.substring(7), dmListTmp.getDataSet("griddata", new DataSet()));
						} else if (paraName.startsWith("__file_")) {
							String fileUiid = dm.getString(paraName);
							if (!StringUtil.chkStrNull(fileUiid)) {
								// 文件上传的文件
								dm.put(paraName.substring(7), request.getSession()
									.getAttribute("file_"
											+ dm.getString(paraName)));
								// 文件获取过一次后，为节省服务器资源，立即进行session的回收
								sessionRemoveMap.put("file_"
										+ dm.getString(paraName), null);
							} else {
								dm.put(paraName.substring(7), (MultipartFile) null);
							}

						}

						// 进度条对象的获取
						if ("__pgbarid".equals(paraName)) {
							pgBar = (ProgressBar) request.getSession()
								.getAttribute("bgbar_" + dm.getString(paraName));
							if (null == pgBar) {
								throw new AppException("请求中没有获取到进度条对象，请稍候重试。");
							}
							dm.put("__progressbar", pgBar);// 后台直接通过工具方法获取
						}
					}
				}

				// 其他框架级的参数
				jbjgid = dm.getString("__jbjgid", "00000000");
				jbjgqxfw = dm.getString("__jbjgqxfw", "00000000");
				yhid = dm.getString("__yhid", "");
				jbjgid = StringUtil.html2Text(jbjgid)
					.replaceAll("\"", "")
					.replace("'", "");// 过滤非法字符
				jbjgqxfw = StringUtil.html2Text(jbjgqxfw)
					.replaceAll("\"", "")
					.replace("'", "");// 过滤非法字符
				yhid = StringUtil.html2Text(yhid)
					.replaceAll("\"", "")
					.replace("'", "");// 过滤非法字符
				dm.put("__ip", ActionUtil.getRemoteHost(request));
				dm.put("__jbjgid", jbjgid);
				dm.put("__jbjgqxfw", jbjgqxfw);
				SysUser currentSysUser = (SysUser) request.getSession()
					.getAttribute("currentsysuser");// 当前用户
				if (null == currentSysUser) {
					currentSysUser = new SysUser();
				}

				// 公共访问方法
				if (!method.isAnnotationPresent(PublicAccess.class)) {// 没有进行注解的话，验证权限
					// 如果没有配置单独验证Class则走标准验证
					if (StringUtil.chkStrNull(GlobalVars.SEP_LOGIN_VERIFICATION_CLASS)) {
						if (null == currentSysUser.getYhid()
								|| "".equals(currentSysUser.getYhid().trim())) {
							// 用户未登录的情况，只允许系统指定的操作controller通过
							if (!LoginUtil.NO_CHECK_LOIGN_CONTROLLER_METHOD_NAME.containsKey(this.getClass()
								.getName()
									+ "." + methodName)) {
								// 系统退出到登录界面，并返回
								StringBuffer strBF = new StringBuffer();
								strBF.append(GlobalVars.ERR_MSG_SIGN_WORDS_4_JS_DIS);
								strBF.append("<script type=\"text/javascript\">");
								strBF.append("alert(\"用户还未登录或者登录超时，请重新登录！\");");
								strBF.append("top.location=\"./login.do?method=chngLoginPage\";");
								strBF.append("</script>");
								ActionUtil.writeMessageToResponse(response, strBF.toString());
								return null;
							}
						} else {
							// 用户已经登录的情况，验证同源
							if (!SecUtil.checkRefererAndRequestURLIsSame(request)) {
								response.setContentType("text/html;charset=UTF-8");
								response.setCharacterEncoding("UTF-8");
								response.setStatus(403);// 服务器拒绝请求。
								return null;
							}
						}
					} else {
						try {
							Class<?> checkLoginClass = Class.forName(GlobalVars.SEP_LOGIN_VERIFICATION_CLASS);
							LoginCheckSupport loginChkSupp = (LoginCheckSupport) checkLoginClass.newInstance();
							boolean isHasLogin = loginChkSupp.doCheckLogin(this.getClass()
								.getName(), methodName, request, response);
							if (!isHasLogin) {
								System.out.println("用户当前处于未登录状态，无法请求后台资源。");
								return null;
							} else {
								// 用户已经登录的情况，验证同源--mod.yjc.2017年11月22日
								// 本来应该进行同源认证但是，此处无法判断用户是否登录，需要SEP_LOGIN_VERIFICATION_CLASS类进行判断,该处同源认证取消，放到具体的登录认证类中进行认证。
								/**
								 * if (!SecUtil.checkRefererAndRequestURLIsSame(
								 * request)) { response.setContentType(
								 * "text/html;charset=UTF-8");
								 * response.setCharacterEncoding("UTF-8");
								 * response.setStatus(403);// 服务器拒绝请求。 return
								 * null; }
								 **/
							}
						} catch (ClassNotFoundException e) {
							throw new AppException("单独登录验证Class:"
									+ GlobalVars.SEP_LOGIN_VERIFICATION_CLASS
									+ "未发现，请检查。");
						}
					}
				}

				// 检查页面yhid和session用户是否一致
				if (!StringUtil.chkStrNull(yhid) && null != currentSysUser
						&& !StringUtil.chkStrNull(currentSysUser.getYhid())) {
					if (!yhid.equalsIgnoreCase(currentSysUser.getYhid())) {
						// 页面的用户ID和session的不一致，则系统进行强制页面退出
						StringBuffer strBF = new StringBuffer();
						strBF.append(GlobalVars.ERR_MSG_SIGN_WORDS_4_JS_DIS);
						strBF.append("<script type=\"text/javascript\">");
						strBF.append("alert(\"当前页面操作用户已被重置为【"
								+ currentSysUser.getYhmc() + "】，点击确定后刷新页面！\");");
						strBF.append("top.location=\"./login.do?method=fwdMainPage\";");
						strBF.append("</script>");
						ActionUtil.writeMessageToResponse(response, strBF.toString());
						return null;
					}
				}
				dm.put("__sysuser", currentSysUser);

				// 请求信息
				dm.put("__request", request);
				dm.put("__response", response);

				params.add(dm);
			}

			// 将经办机构id和权限范围放到request中
			request.setAttribute("__jbjgid", jbjgid);
			request.setAttribute("__jbjgqxfw", jbjgqxfw);

			Object returnValue = method.invoke(this.delegate, params.toArray(new Object[params.size()]));
			// 增加本地化的操作
			if (returnValue instanceof ModelAndView) {
				ModelAndView returnValueMV = (ModelAndView) returnValue;
				this.dealLocalJspPageInfo(returnValueMV, jbjgid);
				if (GlobalVars.DEBUG_MODE) {// 只有debug模式，才将路径放入。
					request.setAttribute("__jsppath", returnValueMV.getViewName());// 将jsp的路径放到request中。
				}
			}

			// 请求完成后session元素中的缓存数据的清除，保证服务器资源
			Object[] sessionRemoveArr = sessionRemoveMap.keySet().toArray();
			for (int i = 0, n = sessionRemoveArr.length; i < n; i++) {
				String keyTmp = (String) sessionRemoveArr[i];
				request.getSession().removeAttribute(keyTmp);
			}

			return massageReturnValueIfNecessary(returnValue);
		} catch (InvocationTargetException ex) {
			return handleException(request, response, ex.getTargetException());
		} catch (Exception ex) {
			return handleException(request, response, ex);
		} finally {
			if (null != pgBar) {// 结束进度条
				if (!pgBar.isEnableAsynBar()) {// 是否启动了异步bar
					pgBar.finishByFrameUse();
				}
			}
		}
	}

	/**
	 * 操作jsp页面本地化
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	private void dealLocalJspPageInfo(ModelAndView mv, String jbjgid) throws AppException {
		String viewName = mv.getViewName();
		if (null == viewName || "".equals(viewName)) {
			return;
		}
		LocalHandler lh = new LocalHandler();
		String bdhm = lh.getLocalBdhm(jbjgid, viewName);
		if (null != bdhm && !"".equals(bdhm)) {
			mv.setViewName(bdhm);
		}
	}

	private ModelAndView massageReturnValueIfNecessary(Object returnValue) {
		if ((returnValue instanceof ModelAndView)) {
			return (ModelAndView) returnValue;
		}
		if ((returnValue instanceof Map)) {
			return new ModelAndView().addAllObjects((Map) returnValue);
		}
		if ((returnValue instanceof String)) {
			return new ModelAndView((String) returnValue);
		}

		return null;
	}

	protected Object newCommandObject(Class clazz) throws Exception {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Creating new command of class ["
					+ clazz.getName() + "]");
		}
		return BeanUtils.instantiateClass(clazz);
	}

	protected void bind(HttpServletRequest request, Object command) throws Exception {
		this.logger.debug("Binding request parameters onto MultiActionController command");
		ServletRequestDataBinder binder = createBinder(request, command);
		binder.bind(request);
		if (this.validators != null) {
			for (int i = 0; i < this.validators.length; i++) {
				if (this.validators[i].supports(command.getClass())) {
					ValidationUtils.invokeValidator(this.validators[i], command, binder.getBindingResult());
				}
			}
		}
		binder.closeNoCatch();
	}

	protected ServletRequestDataBinder createBinder(HttpServletRequest request,
			Object command) throws Exception {
		ServletRequestDataBinder binder = new ServletRequestDataBinder(command, getCommandName(command));
		initBinder(request, binder);
		return binder;
	}

	protected String getCommandName(Object command) {
		return "command";
	}

	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		if (this.webBindingInitializer != null) {
			this.webBindingInitializer.initBinder(binder, new ServletWebRequest(request));
		}
		initBinder(request, binder);
	}

	/** @deprecated */
	protected void initBinder(ServletRequest request,
			ServletRequestDataBinder binder) throws Exception {}

	protected Method getExceptionHandler(Throwable exception) {
		Class exceptionClass = exception.getClass();
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Trying to find handler for exception class ["
					+ exceptionClass.getName() + "]");
		}
		Method handler = (Method) this.exceptionHandlerMap.get(exceptionClass);
		while ((handler == null) && (!exceptionClass.equals(Throwable.class))) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Trying to find handler for exception superclass ["
						+ exceptionClass.getName() + "]");
			}
			exceptionClass = exceptionClass.getSuperclass();
			handler = (Method) this.exceptionHandlerMap.get(exceptionClass);
		}
		return handler;
	}

	private ModelAndView handleException(HttpServletRequest request,
			HttpServletResponse response, Throwable ex) throws Exception {
		Method handler = getExceptionHandler(ex);
		if (handler != null) {
			if (this.logger.isDebugEnabled())
				this.logger.debug("Invoking exception handler [" + handler
						+ "] for exception: " + ex);
			try {
				Object returnValue = handler.invoke(this.delegate, new Object[] { request, response, ex });
				return massageReturnValueIfNecessary(returnValue);
			} catch (InvocationTargetException ex2) {
				this.logger.error("Original exception overridden by exception handling failure", ex);
				ReflectionUtils.rethrowException(ex2.getTargetException());
			} catch (Exception ex2) {
				this.logger.error("Failed to invoke exception handler method", ex2);
			}
		} else {
			ReflectionUtils.rethrowException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}
}