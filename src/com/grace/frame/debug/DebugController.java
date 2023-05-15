package com.grace.frame.debug;

import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.*;
import com.grace.frame.util.opencv.OpencvUtil;
import com.grace.frame.workflow.BizController;
import org.opencv.core.Mat;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 调试模式业务controller
 * 
 * @author yjc
 */
public class DebugController extends BizController{

	/**
	 * 进入本地化管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdLocalConfigMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "fwdLocalConfigMng", para);
		return new ModelAndView("frame/jsp/debug/pageLocalConfigMng.jsp", dm);
	}

	/**
	 * 查询本地化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView queryLocalConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "queryLocalConfig", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 进入本地化新增页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdLocalAddWin(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "fwdLocalAddWin", para);
		return new ModelAndView("frame/jsp/debug/winLocalAdd.jsp", dm);
	}

	/**
	 * 新增本地化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView saveLocalConfigAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "saveLocalConfigAdd", para);
		return null;
	}

	/**
	 * 进入本地化选择页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdSearchLocalDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "fwdSearchLocalDoc", para);
		return new ModelAndView("frame/jsp/debug/winSearchLocalDoc.jsp", dm);
	}

	/**
	 * 删除本地化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView deleteLocalConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "deleteLocalConfig", para);
		return null;
	}

	/**
	 * 进入本地化DOC的修正页面。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdDealNoExistsLocalConfigDoc(
			HttpServletRequest request, HttpServletResponse response,
			DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "fwdDealNoExistsLocalConfigDoc", para);
		return new ModelAndView("frame/jsp/debug/winDealNoExistsLocalConfigDoc.jsp", dm);
	}

	/**
	 * 刷新doc数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView refreshDealNoExistsLocalConfigDoc(
			HttpServletRequest request, HttpServletResponse response,
			DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "fwdDealNoExistsLocalConfigDoc", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 本地化doc的删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView deleteNoExistsLocalConfigDoc(
			HttpServletRequest request, HttpServletResponse response,
			DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.LocalConfigBiz", "deleteNoExistsLocalConfigDoc", para);
		return null;
	}

	/**
	 * 重新加载本地化配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView reloadLocalConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		System.out.println("--*重新加载本地化配置.....");
		GlobalVarsUtil.reloadLOCAL_CONFIG_MAP();// 重新加载
		return null;
	}

	/**
	 * 轮询管理配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdPollingConfigMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "fwdPollingConfigMng", para);
		return new ModelAndView("frame/jsp/debug/pagePollingConfigMng.jsp", dm);
	}

	/**
	 * 刷新轮询配置数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView refreshGridPollingConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "refreshGridPollingConfig", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 轮询文档配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdPollingDocMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winPollingDocMng.jsp", para);
	}

	/**
	 * 刷新文档数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView refreshGridPollingDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "refreshGridPollingDoc", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 轮询配置文档新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdPollingDocAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winPollingDocAdd.jsp", para);
	}

	/**
	 * 保存轮询文档新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView savePollingDocAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "savePollingDocAdd", para);
		return null;
	}

	/**
	 * 轮询配置文档修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdPollingDocModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "fwdPollingDocModify", para);
		return new ModelAndView("frame/jsp/debug/winPollingDocModify.jsp", dm);
	}

	/**
	 * 保存轮询文档修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView savePollingDocModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "savePollingDocModify", para);
		return null;
	}

	/**
	 * 保存轮询文档删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView deletePollingDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "deletePollingDoc", para);
		return null;
	}

	/**
	 * 轮询配置新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdPollingConfigAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winPollingConfigAdd.jsp", para);
	}

	/**
	 * 进入轮询DOC选择页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdChoosePollingDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "fwdChoosePollingDoc", para);
		return new ModelAndView("frame/jsp/debug/winChoosePollingDoc.jsp", dm);
	}

	/**
	 * 增加轮询配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView savePollingConfigAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "savePollingConfigAdd", para);
		return null;
	}

	/**
	 * 删除轮询配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView deletePollingConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "deletePollingConfig", para);
		return null;
	}

	/**
	 * 删除轮询配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView runPollingConfigByHand(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "runPollingConfigByHand", para);
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 * 轮询配置修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdPollingConfigModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "fwdPollingConfigModify", para);
		return new ModelAndView("frame/jsp/debug/winPollingConfigModify.jsp", dm);
	}

	/**
	 * 保存修改轮询服务
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView savePollingConfigModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PollingConfigBiz", "savePollingConfigModify", para);
		return null;
	}

	/**
	 * 系统信息查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdSysInfoMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		para.put("request", request);// 把request放进去
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysInfoMngBiz", "fwdSysInfoMng", para);
		return new ModelAndView("frame/jsp/debug/pageSysInfoMng.jsp", dm);
	}

	/**
	 * 获取内存信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView getSysMemoryInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysInfoMngBiz", "getSysMemoryInfo", para);
		ActionUtil.writeDataMapToResponse(response, dm);
		return null;
	}

	/**
	 * 框架各个表数据量的统计结果查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdFrameTablesInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysInfoMngBiz", "fwdFrameTablesInfo", para);
		return new ModelAndView("frame/jsp/debug/pageFrameTablesInfo.jsp", dm);
	}

	/**
	 * 功能权限管理界面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdFuncConfigMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageFuncConfigMng.jsp", para);
	}

	/**
	 * 选择业务领域
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdChooseYwlxFunc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "fwdChooseYwlxFunc", para);
		return new ModelAndView("frame/jsp/debug/winChooseYwlxFunc.jsp", dm);
	}

	/**
	 * 业务领域新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdYwlxFuncAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "fwdYwlxFuncAdd", para);
		return new ModelAndView("frame/jsp/debug/winYwlxFuncAdd.jsp", dm);
	}

	/**
	 * 保存业务领域功能
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView saveYwlxFuncAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "saveYwlxFuncAdd", para);
		return null;
	}

	/**
	 * 进入一个功能权限的doc操作页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView fwdOneFuncDocMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "fwdOneFuncDocMng", para);
		return new ModelAndView("frame/jsp/debug/winOneFuncDocMng.jsp", dm);
	}

	/**
	 * 新增子节点
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView fwdChildrenNodeAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "fwdChildrenNodeAdd", para);
		return new ModelAndView("frame/jsp/debug/winChildrenNodeAdd.jsp", dm);
	}

	/**
	 * 保存子节点
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView saveChildrenNodeAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "saveChildrenNodeAdd", para);
		return null;
	}

	/**
	 * 查看区域合并信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView fwdFuncUnionView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "fwdFuncUnionView", para);
		return new ModelAndView("frame/jsp/debug/winFuncUnionView.jsp", dm);
	}

	/**
	 * 修改节点信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView fwdChildrenNodeModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "fwdChildrenNodeModify", para);
		return new ModelAndView("frame/jsp/debug/winChildrenNodeModify.jsp", dm);
	}

	/**
	 * 保存信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView saveChildrenNodeModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "saveChildrenNodeModify", para);
		return null;
	}

	/**
	 * 删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-3
	 * @since V1.0
	 */
	public ModelAndView saveChildrenNodeDelete(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "saveChildrenNodeDelete", para);
		return null;
	}

	/**
	 * dbid的管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public ModelAndView fwdDbidInfoMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.DbidInfoMngBiz", "getDbidInfoDs", para);
		return new ModelAndView("frame/jsp/debug/pageDbidInfoMng.jsp", dm);
	}

	/**
	 * 刷新DBID数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView refreshDbidInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.DbidInfoMngBiz", "getDbidInfoDs", para);
		return this.refreshData(response, dm);
	}

	/**
	 * dbid的新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public ModelAndView fwdDbidInfoAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winDbidInfoAdd.jsp", para);
	}

	/**
	 * dbid的修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public ModelAndView fwdDbidInfoModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.DbidInfoMngBiz", "fwdDbidInfoModify", para);
		return new ModelAndView("frame/jsp/debug/winDbidInfoModify.jsp", dm);
	}

	/**
	 * dbid新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public ModelAndView saveDbidInfoAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.DbidInfoMngBiz", "saveDbidInfoAdd", para);
		return null;
	}

	/**
	 * dbid修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public ModelAndView saveDbidInfoModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.DbidInfoMngBiz", "saveDbidInfoModify", para);
		return null;
	}

	/**
	 * 业务类别管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdAgencyBizTypeMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.AgencyBizTypeMngBiz", "getAgencyBizTypeInfo", para);
		return new ModelAndView("frame/jsp/debug/pageAgencyBizTypeMng.jsp", dm);
	}

	/**
	 * 刷新数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView refreshAgencyBizTypeInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.AgencyBizTypeMngBiz", "getAgencyBizTypeInfo", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 进入业务类别新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdAgencyBizTypeAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.AgencyBizTypeMngBiz", "fwdAgencyBizTypeAdd", para);
		return new ModelAndView("frame/jsp/debug/winAgencyBizTypeAdd.jsp", dm);
	}

	/**
	 * 新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView saveAgencyBizTypeAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.AgencyBizTypeMngBiz", "saveAgencyBizTypeAdd", para);
		return null;
	}

	/**
	 * 进入业务类别修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdAgencyBizTypeModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.AgencyBizTypeMngBiz", "fwdAgencyBizTypeModify", para);
		return new ModelAndView("frame/jsp/debug/winAgencyBizTypeModify.jsp", dm);
	}

	/**
	 * 修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView saveAgencyBizTypeModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.AgencyBizTypeMngBiz", "saveAgencyBizTypeModify", para);
		return null;
	}

	/**
	 * 进入经办机构信息管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdSysAgencyMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysAgencyMngBiz", "fwdSysAgencyMng", para);
		return new ModelAndView("frame/jsp/debug/pageSysAgencyMng.jsp", dm);
	}

	/**
	 * 查询经办机构信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView querySysAgencyInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysAgencyMngBiz", "querySysAgencyInfo", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 经办机构新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdSysAgencyAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysAgencyMngBiz", "fwdSysAgencyAdd", para);
		return new ModelAndView("frame/jsp/debug/winSysAgencyAdd.jsp", dm);
	}

	/**
	 * 经办机构新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView saveSysAgencyAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SysAgencyMngBiz", "saveSysAgencyAdd", para);
		return null;
	}

	/**
	 * 经办机构修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdSysAgencyModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysAgencyMngBiz", "fwdSysAgencyModify", para);
		return new ModelAndView("frame/jsp/debug/winSysAgencyModify.jsp", dm);
	}

	/**
	 * 经办机构修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView saveSysAgencyModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SysAgencyMngBiz", "saveSysAgencyModify", para);
		return null;
	}

	/**
	 * 经办机构删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView deleteSysAgency(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SysAgencyMngBiz", "deleteSysAgency", para);
		return null;
	}

	/**
	 * 特殊权限管理配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdSpRightConfigMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "refreshGridSpRightConfig", para);
		return new ModelAndView("frame/jsp/debug/pageSpRightConfigMng.jsp", dm);
	}

	/**
	 * 刷新特殊权限配置数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView refreshGridSpRightConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "refreshGridSpRightConfig", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 特殊权限文档配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdSpRightDocMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "refreshGridSpRightDoc", para);
		return new ModelAndView("frame/jsp/debug/winSpRightDocMng.jsp", dm);
	}

	/**
	 * 刷新文档数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView refreshGridSpRightDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "refreshGridSpRightDoc", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 特殊权限配置文档新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdSpRightDocAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winSpRightDocAdd.jsp", para);
	}

	/**
	 * 保存特殊权限文档新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView saveSpRightDocAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "saveSpRightDocAdd", para);
		return null;
	}

	/**
	 * 特殊权限配置文档修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdSpRightDocModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "fwdSpRightDocModify", para);
		return new ModelAndView("frame/jsp/debug/winSpRightDocModify.jsp", dm);
	}

	/**
	 * 保存特殊权限文档修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView saveSpRightDocModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "saveSpRightDocModify", para);
		return null;
	}

	/**
	 * 保存特殊权限文档删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView deleteSpRightDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "deleteSpRightDoc", para);
		return null;
	}

	/**
	 * 特殊权限配置新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdSpRightConfigAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winSpRightConfigAdd.jsp", para);
	}

	/**
	 * 进入特殊权限DOC选择页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdChooseSpRightDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "fwdChooseSpRightDoc", para);
		return new ModelAndView("frame/jsp/debug/winChooseSpRightDoc.jsp", dm);
	}

	/**
	 * 增加特殊权限配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView saveSpRightConfigAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "saveSpRightConfigAdd", para);
		return null;
	}

	/**
	 * 删除特殊权限配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView deleteSpRightConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SpRightConfigBiz", "deleteSpRightConfig", para);
		return null;
	}

	/**
	 * 进入服务注册信息管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdServiceRegMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceRegMngBiz", "fwdServiceRegMng", para);
		return new ModelAndView("frame/jsp/debug/pageServiceRegMng.jsp", dm);
	}

	/**
	 * 进入服务注册信息刷新
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView refreshServiceRegInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceRegMngBiz", "fwdServiceRegMng", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 服务注册新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdServiceRegAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winServiceRegAdd.jsp", para);
	}

	/**
	 * 服务注册新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView saveServiceRegAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ServiceRegMngBiz", "saveServiceRegAdd", para);
		return null;
	}

	/**
	 * 服务注册修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView fwdServiceRegModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceRegMngBiz", "fwdServiceRegModify", para);
		return new ModelAndView("frame/jsp/debug/winServiceRegModify.jsp", dm);
	}

	/**
	 * 服务注册修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView saveServiceRegModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ServiceRegMngBiz", "saveServiceRegModify", para);
		return null;
	}

	/**
	 * 服务注册删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-5
	 * @since V1.0
	 */
	public ModelAndView deleteServiceReg(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ServiceRegMngBiz", "deleteServiceReg", para);
		return null;
	}

	/**
	 *本地服务管理配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdServiceConfigMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "refreshGridServiceConfig", para);
		return new ModelAndView("frame/jsp/debug/pageServiceConfigMng.jsp", dm);
	}

	/**
	 * 刷新本地服务配置数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView refreshGridServiceConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "refreshGridServiceConfig", para);
		return this.refreshData(response, dm);
	}

	/**
	 *本地服务文档配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdServiceDocMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "refreshGridServiceDoc", para);
		return new ModelAndView("frame/jsp/debug/winServiceDocMng.jsp", dm);
	}

	/**
	 * 刷新文档数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView refreshGridServiceDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "refreshGridServiceDoc", para);
		return this.refreshData(response, dm);
	}

	/**
	 *本地服务配置文档新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdServiceDocAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winServiceDocAdd.jsp", para);
	}

	/**
	 * 保存本地服务文档新增 ·
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView saveServiceDocAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "saveServiceDocAdd", para);
		return null;
	}

	/**
	 *本地服务配置文档修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdServiceDocModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "fwdServiceDocModify", para);
		return new ModelAndView("frame/jsp/debug/winServiceDocModify.jsp", dm);
	}

	/**
	 * 保存本地服务文档修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView saveServiceDocModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "saveServiceDocModify", para);
		return null;
	}

	/**
	 * 保存本地服务文档删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView deleteServiceDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "deleteServiceDoc", para);
		return null;
	}

	/**
	 *本地服务配置新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView fwdServiceConfigAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winServiceConfigAdd.jsp", para);
	}

	/**
	 * 进入本地服务DOC选择页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdChooseServiceDoc(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "fwdChooseServiceDoc", para);
		return new ModelAndView("frame/jsp/debug/winChooseServiceDoc.jsp", dm);
	}

	/**
	 * 增加本地服务配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView saveServiceConfigAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "saveServiceConfigAdd", para);
		return null;
	}

	/**
	 * 删除本地服务配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-1
	 * @since V1.0
	 */
	public ModelAndView deleteServiceConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ServiceConfigBiz", "deleteServiceConfig", para);
		return null;
	}

	/**
	 * 进入CODE管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdCodeConfigMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageCodeConfigMng.jsp", para);
	}

	/**
	 * 进入一个CODE管理页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-31
	 * @since V1.0
	 */
	public ModelAndView fwdOneCodeMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "fwdOneCodeMng", para);
		return new ModelAndView("frame/jsp/debug/winOneCodeMng.jsp", dm);
	}

	/**
	 * code新增页面
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdCodeListAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winCodeListAdd.jsp", para);
	}

	/**
	 * 保存CODEList新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView saveCodeListAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "saveCodeListAdd", para);
		return null;
	}

	/**
	 * codeList修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdCodeListModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "fwdCodeListModify", para);
		return new ModelAndView("frame/jsp/debug/winCodeListModify.jsp", dm);
	}

	/**
	 * 保存CODEList修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView saveCodeListModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "saveCodeListModify", para);
		return null;
	}

	/**
	 * CODElist删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView deleteCodeList(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "deleteCodeList", para);
		return null;
	}

	/**
	 * code配置信息本地设置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdCodeConfigSet(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "fwdCodeConfigSet", para);
		return new ModelAndView("frame/jsp/debug/winCodeConfigSet.jsp", dm);
	}

	/**
	 * 修改序号
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdCodeConfigModifyXh(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winCodeConfigModifyXh.jsp", para);
	}

	/**
	 * code-config配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView saveCodeConfig(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "saveCodeConfig", para);
		return null;
	}

	/**
	 * 新增CODE
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdCodeDocAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winCodeDocAdd.jsp", para);
	}

	/**
	 * code-doc配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView saveCodeDocAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "saveCodeDocAdd", para);
		return null;
	}

	/**
	 * 修改CODE
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdCodeDocModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "fwdCodeDocModify", para);
		return new ModelAndView("frame/jsp/debug/winCodeDocModify.jsp", dm);
	}

	/**
	 * code-doc配置信息
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView saveCodeDocModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "saveCodeDocModify", para);
		return null;
	}

	/**
	 * code-doc删除
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView deleteCodeDocInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "deleteCodeDocInfo", para);
		return null;
	}

	/**
	 * 系统缓存重置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdResetSysCache(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ResetSysCacheBiz", "fwdResetSysCache", para);
		return new ModelAndView("frame/jsp/debug/pageResetSysCache.jsp", dm);
	}

	/**
	 * 重置存储操作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView resetSysCache(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ResetSysCacheBiz", "resetSysCache", para);
		return null;
	}

	/**
	 * 打印模板管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdPrintConfigMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pagePrintConfigMng.jsp", para);
	}

	/**
	 * 查询打印模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView queryPrintConfigInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "queryPrintConfigInfo", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 打印模板管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdPrintModelAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winPrintModelAdd.jsp", para);
	}

	/**
	 * 保存打印格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView savePrintModelAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "savePrintModelAdd", para);
		return null;
	}

	/**
	 * 打印模板修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdPrintModelModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "fwdPrintModelModify", para);
		return new ModelAndView("frame/jsp/debug/winPrintModelModify.jsp", dm);
	}

	/**
	 * 保存打印格式修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView savePrintModelModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "savePrintModelModify", para);
		return null;
	}

	/**
	 * 打印模板修改配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdPrintConfigSet(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "fwdPrintConfigSet", para);
		return new ModelAndView("frame/jsp/debug/winPrintConfigSet.jsp", dm);
	}

	/**
	 * 保存打印格式配置修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView savePrintConfigSet(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "savePrintConfigSet", para);
		return null;
	}

	/**
	 * 删除打印格式模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView deletePrintModel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "deletePrintModel", para);
		return null;
	}

	/**
	 * 查看表样
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdPrintModelView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "fwdPrintModelView", para);
		return new ModelAndView("frame/jsp/debug/winPrintModelView.jsp", dm);
	}

	/**
	 * 文件模板管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdFileModelMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageFileModelMng.jsp", para);
	}


	/**
	 * 考生导入模版
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdFileModelMng1(HttpServletRequest request,
										HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageFileModelMng1.jsp", para);
	}

	/**
	 * 查询文件模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView queryFileModel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FileModelMngBiz", "queryFileModel", para);
		return this.refreshData(response, dm);
	}

	/**
	 * 删除文件模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView deleteFileModel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FileModelMngBiz", "deleteFileModel", para);
		return null;
	}

	/**
	 * 文件模板新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdFileModelAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winFileModelAdd.jsp", para);
	}

	/**
	 * 文件模板新增
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdFileModelAdd1(HttpServletRequest request,
										HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winFileModelAdd1.jsp", para);
	}

	/**
	 * 保存文件新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView saveFileModelAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FileModelMngBiz", "saveFileModelAdd", para);
		return null;
	}
	/**
	 * 保存文件新增
	 *
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView saveFileModelAdd1(HttpServletRequest request,
										 HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FileModelMngBiz", "saveFileModelAdd1", para);
		return null;
	}

	/**
	 * 文件修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-8
	 * @since V1.0
	 */
	public ModelAndView fwdFileModelModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FileModelMngBiz", "fwdFileModelModify", para);
		return new ModelAndView("frame/jsp/debug/winFileModelModify.jsp", dm);
	}

	/**
	 * 保存文件修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView saveFileModelModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FileModelMngBiz", "saveFileModelModify", para);
		return null;
	}

	/**
	 * 文档模板查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdFileModelView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		para.put("response", response);
		this.doBizMethod("com.grace.frame.debug.biz.FileModelMngBiz", "fwdFileModelView", para);
		return null;
	}

	/**
	 * 下载所有文档，将文档压缩为压缩包下载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public ModelAndView downloadAllFileModel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.FileModelMngBiz", "downloadAllFileModel", para);
		byte[] zipByte = (byte[]) dm.get("zipbyte");

		// 把文件写到前台下载
		FileIOUtil.writeByteToResponse(zipByte, DateUtil.dateToString(DateUtil.getDBTime())
				+ "批量导出系统文件.zip", response);
		return null;
	}

	/**
	 * 系统节假日管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdSysHolidayMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysHolidayBiz", "fwdSysHolidayMng", para);
		return new ModelAndView("frame/jsp/debug/pageSysHolidayMng.jsp", dm);
	}

	/**
	 * 查询系统的节假日信息
	 * 
	 * @author yjc
	 * @date 创建时间 2016-3-2
	 * @since V1.0
	 */
	public ModelAndView querySysHolidayInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysHolidayBiz", "querySysHolidayInfo", para);
		ActionUtil.writeDataMapToResponse(response, dm);
		return null;
	}

	/**
	 * 查询系统的节假日信息
	 * 
	 * @author yjc
	 * @date 创建时间 2016-3-2
	 * @since V1.0
	 */
	public ModelAndView saveSysHolidayInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SysHolidayBiz", "saveSysHolidayInfo", para);
		return null;
	}

	/**
	 * 下载所有打印模板，将文档压缩为压缩包下载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public ModelAndView downloadAllPrintModel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "downloadAllPrintModel", para);
		byte[] zipByte = (byte[]) dm.get("zipbyte");

		// 把文件写到前台下载
		FileIOUtil.writeByteToResponse(zipByte, DateUtil.dateToString(DateUtil.getDBTime())
				+ "批量导出打印模板文件.zip", response);
		return null;
	}

	/**
	 * 下载选中打印模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public ModelAndView downloadSelectPrintModel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "downloadSelectPrintModel", para);
		byte[] btyegsnr = (byte[]) dm.get("btyegsnr");
		String fileName = dm.getString("filename");

		// 把文件写到前台下载
		FileIOUtil.writeByteToResponse(btyegsnr, fileName, response);
		return null;
	}

	/**
	 * 打印格式上传
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdPrintModelUpload(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winPrintModelUpload.jsp", para);
	}

	/**
	 * 文件上传-打印格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView uploadPrintModelFile(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.PrintConfigMngBiz", "uploadPrintModelFile", para);
		ActionUtil.writeDataMapToResponse(response, dm);
		return null;
	}

	/**
	 * 典型批注配置管理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdNoteConfigMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageNoteConfigMng.jsp", para);
	}

	/**
	 * 典型批准的维护管理页
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdOneNoteMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.NoteMngBiz", "fwdOneNoteMng", para);
		return new ModelAndView("frame/jsp/debug/treepageOneNoteMng.jsp", rdm);
	}

	/**
	 * 典型批准新增
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdNoteListAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winNoteListAdd.jsp", para);
	}

	/**
	 * 保存NOTE_LIST新增
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView saveNoteListAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.NoteMngBiz", "saveNoteListAdd", para);
		return null;
	}

	/**
	 * 保存list修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView saveNoteListModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.NoteMngBiz", "saveNoteListModify", para);
		return null;
	}

	/**
	 * 保存config修改
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView saveNoteConfigModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.NoteMngBiz", "saveNoteConfigModify", para);
		return null;
	}

	/**
	 * 保存note删除
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView saveNoteListDel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.NoteMngBiz", "saveNoteListDel", para);
		return null;
	}

	/**
	 * 进入biz调试页面
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdBizClassDebug(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageBizClassDebug.jsp", para);
	}

	/**
	 * 运行
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView runBizClassDebug(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.BizClassDebugBiz", "runBizClassDebug", para);
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 *开始使用引导配置
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdUseBegin(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageUseBegin.jsp", para);
	}

	/**
	 *参数更页面事例
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdAppParaModfiyView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageAppParaModfiyView.jsp", para);
	}

	/**
	 * 重置code
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView resetFrameCodeSetting(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.CodeConfigMngBiz", "resetFrameCodeSetting", para);
		return null;
	}

	/**
	 *重置func
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView resetFrameFuncSetting(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.FuncConfigMngBiz", "resetFrameFuncSetting", para);
		return null;
	}

	/**
	 * 批次参数维护功能
	 * 
	 * @author yjc
	 * @date 创建时间 2017年3月7日
	 * @since V1.0
	 */
	public ModelAndView fwdSysParaMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.SysParaMngBiz", "fwdSysParaMng", para);
		return new ModelAndView("frame/jsp/debug/pageSysParaMng.jsp", dm);
	}

	/**
	 * 批次参数维护功能--保存
	 * 
	 * @author yjc
	 * @date 创建时间 2017年3月7日
	 * @since V1.0
	 */
	public ModelAndView saveSysPara(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.SysParaMngBiz", "saveSysPara", para);
		return null;
	}

	/**
	 *hibernate加密串生成
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdGenHibernateEncodeStr(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageGenHibernateEncodeStr.jsp", para);
	}

	/**
	 * 生成加密串
	 * 
	 * @author yjc
	 * @date 创建时间 2017年3月7日
	 * @since V1.0
	 */
	public ModelAndView genHibernateEncodeStr(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String name = para.getString("name");
		String pwd = para.getString("pwd");

		// 加密操作
		if (!StringUtil.chkStrNull(name)) {
			name = SecUtil.encryptMode(name);
		}
		if (!StringUtil.chkStrNull(pwd)) {
			pwd = SecUtil.encryptMode(pwd);
		}
		DataMap dmJm = new DataMap();
		dmJm.put("name", name);
		dmJm.put("pwd", pwd);

		DataMap rdm = new DataMap();
		rdm.put("dmencode", dmJm);
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 *系统图片库
	 * 
	 * @author yjc
	 * @date 创建时间 2016-7-24
	 * @since V1.0
	 */
	public ModelAndView fwdSysIconView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String iconPath = StringUtil.combinePath(request.getSession()
			.getServletContext()
			.getRealPath(""), "frame", "plugins", "easyui", "themes", "icons", "extends");
		File pathFile = new File(iconPath);
		if (!pathFile.exists()) {
			throw new BizException("路径获取失败");
		}

		// 获取所有图标
		DataSet dsIcon = new DataSet();
		File iconFiles[] = pathFile.listFiles();
		for (int i = 0; i < iconFiles.length; i++) {
			File iconFile = iconFiles[i];
			if (iconFile.isDirectory()) {
				continue;
			}
			String fileName = iconFile.getName();
			fileName = fileName.substring(0, fileName.lastIndexOf("."));

			// 增加ICO
			dsIcon.addRow();
			dsIcon.put(i, "icon", fileName);
		}
		dsIcon.sort("icon");

		DataMap rdm = new DataMap();
		rdm.put("dsicon", dsIcon);
		return new ModelAndView("frame/jsp/debug/pageSysIconView.jsp", rdm);
	}

	/**
	 * 进入自动化代码工具
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	public ModelAndView fwdAutoCodingTools(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageAutoCodingTools.jsp");
	}

	/**
	 * 查询表字段信息
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	public ModelAndView queryColmInfo4AutoCoding(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.AutoCodingBiz", "queryColmInfo4AutoCoding", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 生成代码
	 * 
	 * @author yjc
	 * @date 创建时间 2017-8-18
	 * @since V1.0
	 */
	public ModelAndView genCode(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.AutoCodingBiz", "genCode", para);
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 * 进入【图片模板】管理页面
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public ModelAndView fwdImageModelMng(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageImageModelMng.jsp");
	}

	/**
	 * 查询【图片模板】信息
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public ModelAndView queryImageModelInfo(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "queryImageModelInfo", para);
		return this.refreshData(response, rdm);
	}

	/**
	 * 【图片模板】信息新增
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public ModelAndView fwdImageModelAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winImageModelAdd.jsp");
	}

	/**
	 * 保存【图片模板】信息新增
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public ModelAndView saveImageModelAdd(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "saveImageModelAdd", para);
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 * 【图片模板】信息修改
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public ModelAndView fwdImageModelModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "fwdImageModelModify", para);
		return new ModelAndView("frame/jsp/debug/winImageModelModify.jsp", rdm);
	}

	/**
	 * 保存【图片模板】信息修改
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public ModelAndView saveImageModelModify(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "saveImageModelModify", para);
		return null;
	}

	/**
	 * 【图片模板】信息删除
	 * 
	 * @author autoCoding-自动代码工具
	 * @date 创建时间 2017-12-29
	 * @since V1.0
	 */
	public ModelAndView saveImageModelDelete(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "saveImageModelDelete", para);
		return null;
	}

	/**
	 * 图片模板修改配置
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView fwdImageModelConfigSet(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "fwdImageModelConfigSet", para);
		return new ModelAndView("frame/jsp/debug/winImageModelConfigSet.jsp", dm);
	}

	/**
	 * 保存图片模板配置修改
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-6
	 * @since V1.0
	 */
	public ModelAndView saveImageModelConfigSet(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "saveImageModelConfigSet", para);
		return null;
	}

	/**
	 * 获取图片模板的信息
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-2
	 * @since V1.0
	 */
	public ModelAndView getImageModelOptions(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap rdm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "getImageModelOptions", para);
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 * 保存设计
	 * 
	 * @author yjc
	 * @date 创建时间 2018-1-2
	 * @since V1.0
	 */
	public ModelAndView saveImageModelDesign(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "saveImageModelDesign", para);
		return null;
	}

	/**
	 * 下载选中打印模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public ModelAndView downloadSelectImageModel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "downloadSelectImageModel", para);
		byte[] btyegsnr = (byte[]) dm.get("bytembinfo");
		String fileName = dm.getString("filename");

		// 把文件写到前台下载
		FileIOUtil.writeByteToResponse(btyegsnr, fileName, response);
		return null;
	}

	/**
	 * 下载所有图片模板，将文档压缩为压缩包下载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-10-23
	 * @since V1.0
	 */
	public ModelAndView downloadAllImageModel(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "downloadAllImageModel", para);
		byte[] zipByte = (byte[]) dm.get("zipbyte");

		// 把文件写到前台下载
		FileIOUtil.writeByteToResponse(zipByte, DateUtil.dateToString(DateUtil.getDBTime())
				+ "批量导出图片模板文件.zip", response);
		return null;
	}

	/**
	 * 图片格式上传
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdImageModelUpload(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/winImageModelUpload.jsp", para);
	}

	/**
	 * 文件上传-图片模板
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView uploadImageModelFile(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "uploadImageModelFile", para);
		ActionUtil.writeDataMapToResponse(response, dm);
		return null;
	}

	/**
	 * 图片模板查看
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdImageModelView(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		DataMap dm = this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "fwdImageModelView", para);
		return new ModelAndView("frame/jsp/debug/winImageModelView.jsp", dm);
	}

	/**
	 * 图片模板查看-PDF
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdImageModelView4PDF(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		this.doBizMethod("com.grace.frame.debug.biz.ImageModelMngBiz", "fwdImageModelView4PDF", para);
		return null;
	}

	/**
	 * 证件照处理程序
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdIDPhotoCheckPage(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageIdPhotoCheck.jsp");
	}

	/**
	 * 证件照检测处理
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-7
	 * @since V1.0
	 */
	public ModelAndView getIDPhotoResult(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String errcode = "0";
		String errtext = "";
		String base64Img = "";
		String base64YtImage = "";
		try {
			CommonsMultipartFile file = (CommonsMultipartFile) para.get("zjz");
			if (null == file) {
				throw new BizException("请选择需要处理的文件");
			}
			int txzl = para.getInt("txzl");
			String clfs = para.getString("clfs");
			if (txzl < 50) {
				txzl = 50;
			}
			if (txzl > 100) {
				txzl = 100;
			}
			byte[] imageByte_In = file.getBytes();// 获取字节数据
			String imgType = OpencvUtil.getImageType(imageByte_In);
			if ("jpg".equals(imgType)) {
				imgType = "jpeg";
			}
			base64YtImage = "data:image/" + imgType + ";base64,"
					+ SecUtil.base64Encode(imageByte_In);

			// 检查是否是已经处理过的照片
			byte[] endByteMsg = new byte[10];
			int imageByte_In_length = imageByte_In.length;
			for (int i = 0, n = endByteMsg.length; i < n; i++) {
				int tempIndex = imageByte_In_length - 10 + i;
				if (tempIndex > 0) {
					endByteMsg[i] = imageByte_In[tempIndex];
				}
			}
			String photoStr = new String(endByteMsg);

			Mat imageMat;
			if (photoStr.length() > 9
					&& "@nomaxchk".equals(photoStr.substring(photoStr.length() - 9, photoStr.length()))) {
				imageMat = OpencvUtil.imageFileByte2Mat(imageByte_In);
				txzl = 100;
			} else {
				// 照片处理
				imageMat = IDPhotoChkUtil.check("", "", imageByte_In);
			}
			if ("inverse".equals(clfs)) {// 反色
				imageMat = OpencvUtil.inverse(imageMat);
			} else if ("brightness".equals(clfs)) {// 亮度提升
				imageMat = OpencvUtil.brightness(imageMat);
			} else if ("darkness".equals(clfs)) {// 亮度降低
				imageMat = OpencvUtil.darkness(imageMat);
			} else if ("gray".equals(clfs)) {// 灰度
				imageMat = OpencvUtil.gray(imageMat);
			} else if ("sharpen".equals(clfs)) {// 锐化
				imageMat = OpencvUtil.sharpen(imageMat);
			} else if ("blur".equals(clfs)) {// 高斯模糊
				imageMat = OpencvUtil.blur(imageMat);
			} else if ("gradient".equals(clfs)) {// 梯度
				imageMat = OpencvUtil.gradient(imageMat);
			}
			byte[] resultImg = OpencvUtil.mat2ImageFileByte(imageMat, txzl);
			byte[] shtgByte = "@@nomaxchk".getBytes();
			byte[] newImg = new byte[resultImg.length + shtgByte.length];
			for (int i = 0, n = newImg.length; i < n; i++) {
				if (i < resultImg.length) {
					newImg[i] = resultImg[i];
				} else {
					newImg[i] = shtgByte[i - resultImg.length];
				}
			}
			base64Img = "data:image/jpeg;base64,"
					+ SecUtil.base64Encode(newImg);
		} catch (Exception e) {
			errcode = "1";
			errtext = e.getMessage();
		}
		DataMap rdm = new DataMap();
		rdm.put("errcode", errcode);
		rdm.put("errtext", errtext);
		rdm.put("image", base64Img);
		rdm.put("ytimage", base64YtImage);
		ActionUtil.writeDataMapToResponse(response, rdm);
		return null;
	}

	/**
	 * 下载文件
	 * 
	 * @author yjc
	 * @date 创建时间 2018-3-9
	 * @since V1.0
	 */
	public ModelAndView downIDPhotoResult(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		String image = para.getString("image");
		if (StringUtil.chkStrNull(image)) {
			throw new BizException("image为空");
		}
		image = image.substring(23);
		byte[] imageByte = SecUtil.base64Decode(image);
		FileIOUtil.writeByteToResponse(imageByte, "报名照片.jpg", response);
		return null;
	}

	/**
	 * Sql格式化
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-17
	 * @since V1.0
	 */
	public ModelAndView fwdSqlFormat(HttpServletRequest request,
			HttpServletResponse response, DataMap para) throws Exception {
		return new ModelAndView("frame/jsp/debug/pageSqlFormat.jsp");
	}

}
