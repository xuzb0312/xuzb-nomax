package com.grace.frame.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.grace.frame.util.ActionUtil;

/**
 * 编码的校正
 * 
 * @author yjc
 */
public class EncodingFilter implements Filter{

	public void init(FilterConfig filterConfig) throws ServletException {}

	/**
	 * 按照utf-8编码进行矫正(一些安全的认证一并放到该处了)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		try {
			String currentEncoding = request.getCharacterEncoding();
			if (currentEncoding == null) {
				currentEncoding = "UTF-8";
				request.setCharacterEncoding(currentEncoding);
			}
			chain.doFilter(request, response);
		} catch (IOException e) {
		} catch (ServletException e) {
			String exceptionData = e.getMessage();
			HttpServletResponse res = (HttpServletResponse) response;
			ActionUtil.writeMessageToResponse(res, exceptionData);
		}
	}

	public void destroy() {}

}
