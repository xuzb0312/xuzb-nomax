package com.grace.frame.shine.taglib.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.grace.frame.util.DataMap;
import com.grace.frame.workflow.BizController;

/**
 * 新标签库的tagController
 * 
 * @author yjc
 */
public class ShineTagController extends BizController{

	/**
	 * 转向框架页面-打开window,tab时使用。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	public ModelAndView fwdPageFrame(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("shine/jsp/layout/pageFrame.jsp", para);
	}

	/**
	 * 转向框架页面-打开window,tab时使用。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-6-25
	 * @since V1.0
	 */
	public ModelAndView fwdClientCookieSetting(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("shine/jsp/taglib/winClientCookieSetting.jsp", para);
	}
}
