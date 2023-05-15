package com.grace.frame.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.UrlPathHelper;

import com.grace.frame.util.ActionUtil;

/**
 * 安全相关的过滤器
 * 
 * @author yjc
 */
public class SecurityFilter implements Filter{
	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	/**
	 * 安全相关过滤
	 * 
	 * @author yjc
	 * @date 创建时间 2019-11-28
	 * @since V1.0
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			boolean forbid = false;// 是否拦截请求

			if (request instanceof HttpServletRequest) {
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				String method = httpRequest.getMethod();

				// 允许的请求方法
				if ("GET".equalsIgnoreCase(method)
						|| "POST".equalsIgnoreCase(method)
						|| "HEAD".equalsIgnoreCase(method)
						|| "OPTIONS".equalsIgnoreCase(method)) {
					String reqPath = new UrlPathHelper().getPathWithinApplication(httpRequest);
					if (reqPath.endsWith(".api")) {// 对于api请求-允许跨域
						HttpServletResponse httpResponse = (HttpServletResponse) response;
						httpResponse.setHeader("Access-Control-Allow-Origin", "*");
						httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
						httpResponse.setHeader("Access-Control-Max-Age", "3600");
						httpResponse.setHeader("Access-Control-Allow-Headers", "x-requested-with");
					} else {
						if ("OPTIONS".equalsIgnoreCase(method)) {
							forbid = true;
						} else {
							HttpServletResponse httpResponse = (HttpServletResponse) response;
							httpResponse.setHeader("X-Frame-Options", "sameorigin");
						}
					}
				} else {
					forbid = true;
				}
			}
			if (forbid) {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.setContentType("text/html;charset=UTF-8");
				httpResponse.setCharacterEncoding("UTF-8");
				httpResponse.setStatus(405);// 发送禁止请求错误代码-（禁用的方法）禁用请求中指定的方法。
				return;
			}
			chain.doFilter(request, response);
		} catch (IOException e) {
		} catch (ServletException e) {
			String exceptionData = e.getMessage();
			HttpServletResponse res = (HttpServletResponse) response;
			ActionUtil.writeMessageToResponse(res, exceptionData);
		}
	}

	@Override
	public void destroy() {}

}
