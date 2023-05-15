package com.grace.frame.workflow;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.util.WebUtils;

/**
 * 将方法的默认key关键字写为：method
 * 
 * @author yjc
 */
public class ParameterMethodNameResolver implements MethodNameResolver{
	public static final String DEFAULT_PARAM_NAME = "method";// 更改为method
	protected final Log logger = LogFactory.getLog(getClass());
	private String paramName = DEFAULT_PARAM_NAME;
	private String[] methodParamNames;
	private Properties logicalMappings;
	private String defaultMethodName;

	public void setParamName(String paramName) {
		if (paramName != null) {
			Assert.hasText(paramName, "'paramName' must not be empty");
		}
		this.paramName = paramName;
	}

	public void setMethodParamNames(String[] methodParamNames) {
		this.methodParamNames = methodParamNames;
	}

	public void setLogicalMappings(Properties logicalMappings) {
		this.logicalMappings = logicalMappings;
	}

	public void setDefaultMethodName(String defaultMethodName) {
		if (defaultMethodName != null) {
			Assert.hasText(defaultMethodName, "'defaultMethodName' must not be empty");
		}
		this.defaultMethodName = defaultMethodName;
	}

	public String getHandlerMethodName(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
		String methodName = null;

		if (this.methodParamNames != null) {
			for (int i = 0; i < this.methodParamNames.length; i++) {
				String candidate = this.methodParamNames[i];
				if (WebUtils.hasSubmitParameter(request, candidate)) {
					methodName = candidate;
					if (!this.logger.isDebugEnabled())
						break;
					this.logger.debug("Determined handler method '"
							+ methodName
							+ "' based on existence of explicit request parameter of same name");
					break;
				}

			}

		}

		if ((methodName == null) && (this.paramName != null)) {
			methodName = request.getParameter(this.paramName);
			if ((methodName != null) && (this.logger.isDebugEnabled())) {
				this.logger.debug("Determined handler method '" + methodName
						+ "' based on value of request parameter '"
						+ this.paramName + "'");
			}

		}

		if ((methodName != null) && (this.logicalMappings != null)) {
			String originalName = methodName;
			methodName = this.logicalMappings.getProperty(methodName, methodName);
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Resolved method name '" + originalName
						+ "' to handler method '" + methodName + "'");
			}
		}

		if ((methodName != null) && (!StringUtils.hasText(methodName))) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("Method name '" + methodName
						+ "' is empty: treating it as no method name found");
			}
			methodName = null;
		}

		if (methodName == null) {
			if (this.defaultMethodName != null) {
				methodName = this.defaultMethodName;
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("Falling back to default handler method '"
							+ this.defaultMethodName + "'");
				}
			} else {
				throw new NoSuchRequestHandlingMethodException(request);
			}
		}
		return methodName;
	}
}