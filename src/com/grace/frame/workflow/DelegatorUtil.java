package com.grace.frame.workflow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.hibernate.TransManager;
import com.grace.frame.localize.LocalHandler;
import com.grace.frame.util.DataMap;
import com.grace.frame.util.SysUser;

/**
 * 用于调用biz，对需要注意的事项进行了封装
 * 
 * @author yjc
 */
public class DelegatorUtil{

	/**
	 * 从para中提取出bizpara数据，并且将para中的相关数据移除
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-12-5
	 * @since V1.0
	 */
	public static DataMap genBizparaFromPara(DataMap para) throws AppException {
		// Biz参数
		DataMap bizPara = new DataMap();
		bizPara.put("jbjgid", para.getString("__jbjgid"));
		bizPara.put("jbjgqxfw", para.getString("__jbjgqxfw"));
		bizPara.put("ip", para.getString("__ip"));
		bizPara.put("sysuser", (SysUser) para.get("__sysuser"));
		bizPara.put("request", (HttpServletRequest) para.get("__request"));
		bizPara.put("response", (HttpServletResponse) para.get("__response"));

		// para将bizPara移除掉--对于bizPara对于使用者来说是透明的。
		para.remove("__jbjgid");
		para.remove("__jbjgqxfw");
		para.remove("__yhid");
		para.remove("__ip");
		para.remove("__sysuser");
		para.remove("__request");
		para.remove("__response");

		return bizPara;
	}

	/**
	 * 调用Biz，用于管理事物等操作
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public static DataMap execute(String bizName, String methodName,
			DataMap para) throws Exception {
		if (bizName == null || "".equalsIgnoreCase(bizName)) {
			throw new AppException("传入参数[bizName]为空!", "DelegatorUtil");
		}
		if (methodName == null || "".equalsIgnoreCase(methodName)) {
			throw new AppException("传入参数[methodName]为空!", "DelegatorUtil");
		}
		if (para == null) {
			throw new AppException("传入参数[paraIn]为空!", "DelegatorUtil");
		}
		try {
			Class.forName(bizName);
		} catch (ClassNotFoundException e) {
			throw new AppException("Class:" + bizName + "没有发现。", "DelegatorUtil");
		}
		// 调用biz，标识需要对数据进行操作，并且存在业务逻辑，此处需要检测数据库版本号和业务系统版本号是否一致，不一致的给出报错
		// 1.检测框架版本号
		if (!GlobalVars.FRAME_VERSION.equalsIgnoreCase(GlobalVars.DB_FRAME_VERSION)) {
			throw new AppException("系统框架程序版本号和数据库版本号不一致，不允许进行业务操作。");
		}
		// 2.对于业务版本号，在运行模式下，不允许不一致
		if (!GlobalVars.APP_VERSION.equalsIgnoreCase(GlobalVars.DB_APP_VERSION)) {
			if (GlobalVars.DEBUG_MODE) {
				System.out.println("*警告*业务系统版本号和数据库版本号不一致，请及时更新系统程序或者升级业务系统数据库版本号。");
			} else {
				throw new AppException("业务系统程序版本号和数据库版本号不一致，不允许进行业务操作。");
			}
		}

		// Biz参数
		DataMap bizPara = DelegatorUtil.genBizparaFromPara(para);

		DataMap result;
		TransManager tm = new TransManager();
		try {
			tm.begin();
			// 反射调用biz的方法
			result = DelegatorUtil.executeFunc(bizName, methodName, para, bizPara);
			tm.commit();
		} catch (Exception e) {
			try {
				tm.rollback();
			} catch (AppException op) {
				throw e;
			}
			throw e;
		}
		return result;
	}

	/**
	 * 调用Biz，本地化、反射机制等的封装
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public static DataMap executeFunc(String bizName, String methodName,
			DataMap para, DataMap bizPara) throws Exception {
		Biz biz = null;
		String jbjgid = bizPara.getString("jbjgid");

		// 对机构进行本地化操作。
		try {
			// 本地化
			String bzjm = bizName + "." + methodName;
			LocalHandler lh = new LocalHandler();
			String bdhm = lh.getLocalBdhm(jbjgid, bzjm);
			if (null != bdhm && !"".equals(bdhm)) {
				int lindex = bdhm.lastIndexOf(".");
				bizName = bdhm.substring(0, lindex);
				methodName = bdhm.substring(lindex + 1);
			}

			// 得到biz实例对象
			Class<?> bizClass = Class.forName(bizName);
			biz = (Biz) bizClass.newInstance();

			// 调用初始化方法
			biz.init(bizPara);

			// 调用biz业务方法
			Method method;
			Class<?>[] parad = { DataMap.class };
			Object[] paras = { para };
			method = biz.getClass().getMethod(methodName, parad);
			DataMap vdm = (DataMap) method.invoke(biz, paras);

			return vdm;
		} catch (ClassNotFoundException e) {
			throw new AppException("找不到名称为[" + bizName + "]的Biz类！", "DelegatorUtil");
		} catch (InstantiationException e) {
			throw new AppException("获取名称为[" + bizName + "]的Biz类的实例时出错！", "InstantiationException");
		} catch (NoSuchMethodException ex) {
			throw new AppException(bizName + "不存在方法" + methodName, "NoSuchMethodException");
		} catch (IllegalAccessException e) {
			throw new AppException("使用方法调用调用[" + bizName + "." + methodName
					+ "]时出错！", "IllegalAccessException");
		} catch (IllegalArgumentException e) {
			throw new AppException("使用方法调用调用[" + bizName + "." + methodName
					+ "]时出错！", "IllegalArgumentException");
		} catch (InvocationTargetException ie) {
			Throwable cause = ie.getCause();
			if (cause instanceof BizException) {
				throw (Exception) cause;
			} else if (cause instanceof ExecutionException) {// 增加针对新开线程的操作的异常的抛出获取最原始的异常信息内容
				ExecutionException exex = (ExecutionException) cause;
				throw (Exception) exex.getCause();
			} else {
				throw (Exception) ie.getTargetException();
			}
		}
	}
}
