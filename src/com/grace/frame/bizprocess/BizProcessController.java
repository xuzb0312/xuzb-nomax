package com.grace.frame.bizprocess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.grace.frame.util.ActionUtil;
import com.grace.frame.util.DataMap;
import com.grace.frame.workflow.BizController;

/**
 * 业务流程相关的操作
 * 
 * @author yjc
 */
public class BizProcessController extends BizController{

	/**
	 * 进入首页-待办事项查看的。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-12
	 * @since V1.0
	 */
	public ModelAndView fwdProceedingHomePage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.bizprocess.biz.ProceedingBiz", "queryMyProceedingInfo", para);
		return new ModelAndView("frame/jsp/bizprocess/pageHomeProceeding.jsp", dm);
	}

	/**
	 * 待办事项取消
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-11
	 * @since V1.0
	 */
	public ModelAndView saveProceedingBizNullify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.bizprocess.biz.ProceedingBiz", "saveProceedingBizNullify", para);
		return null;
	}

	/**
	 * 进入查看更多办结事项的页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-12
	 * @since V1.0
	 */
	public ModelAndView fwdViewMoreBjsx(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/bizprocess/winMoreBjsxView.jsp");
	}

	/**
	 * 进入首页-待办事项查看的。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-12
	 * @since V1.0
	 */
	public ModelAndView fwdChooseSysUser(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.bizprocess.biz.ProceedingBiz", "fwdChooseSysUser", para);
		return new ModelAndView("frame/jsp/bizprocess/winChooseSysUser.jsp", dm);
	}

	/**
	 * 查询用户办结事项
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-12
	 * @since V1.0
	 */
	public ModelAndView queryUserBjsxInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.bizprocess.biz.ProceedingBiz", "queryUserBjsxInfo", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 获取参数-操作业务
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-12
	 * @since V1.0
	 */
	public ModelAndView getDoProceedingPara(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.bizprocess.biz.ProceedingBiz", "getDoProceedingPara", para);
		ActionUtil.writeDataMapToResponse(response, dm);
		return null;
	}

	/**
	 * 业务流程详情信息查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-11-19
	 * @since V1.0
	 */
	public ModelAndView fwdProceedingBPDetailsView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.bizprocess.biz.ProceedingBiz", "fwdProceedingBPDetailsView", para);
		return new ModelAndView("frame/jsp/bizprocess/winProceedingBPDetailsView.jsp", dm);
	}
}
