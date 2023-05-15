package com.grace.frame.listener;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.polling.PollingTask;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.ReadXmlUtil;
import com.grace.frame.util.SysLogUtil;

/**
 * 系统启动时，所要执行的程序，例如将一些数据加载入缓存，调整一些系统参数等。
 * 
 * @author yjc
 */
public class AppListener implements ServletContextListener{
	private Timer timer = new Timer(true);// 定时器

	/**
	 * 系统启动时，执行的操作
	 */
	public void contextInitialized(ServletContextEvent arg) {
		System.out.println("***系统正在启动...");
		// 记录系统日志
		SysLogUtil.logInfo(AppListener.class, "******系统启动******");

		// 系统框架层缓存数据
		System.out.println("***系统开始加载框架底层缓存数据...");
		try {
			StartUpHandler suh = new AppStartUpHandler();
			suh.deal(arg);
		} catch (Exception e) {
			e.printStackTrace();
			SysLogUtil.logError(AppListener.class, "系统开始加载框架底层缓存数据时出现异常:"
					+ e.getMessage(), e);
		}

		// 加载业务级缓存数据
		System.out.println("***系统开始加载业务级缓存数据...");
		try {
			DataSet dsPlugins = ReadXmlUtil.readXml4KeyValue("startupPlugins.xml");
			for (int i = 0, n = dsPlugins.size(); i < n; i++) {
				String bizSuhName = dsPlugins.getString(i, "value");
				Class<?> bizSuhClass = Class.forName(bizSuhName);
				StartUpHandler bizSuh = (StartUpHandler) bizSuhClass.newInstance();
				bizSuh.deal(arg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SysLogUtil.logError(AppListener.class, "系统启动加载业务级插件列表时出现异常:"
					+ e.getMessage(), e);
		}

		// 启动轮询服务
		if (GlobalVars.IS_START_POLLING) {
			System.out.println("***系统正在启动轮询服务程序...");
			try {
				timer.schedule(new PollingTask(), 60000, 60000);// 一分钟检测一次
			} catch (Exception e) {
				e.printStackTrace();
				SysLogUtil.logError(AppListener.class, "系统正在启动轮询服务程序:"
						+ e.getMessage(), e);
			}
		}

	}

	/**
	 * 系统终止时，执行的操作
	 */
	public void contextDestroyed(ServletContextEvent arg) {
		System.out.println("***系统正在停止...");

		// 系统停止时，框架层的操作
		System.out.println("***系统正在进行框架级资源回收...");
		try {
			ShutDownHandler sdh = new AppShutDownHandler();
			sdh.deal(arg);
		} catch (Exception e) {
			e.printStackTrace();
			SysLogUtil.logError(AppListener.class, "统正在进行框架级资源回收时出现异常:"
					+ e.getMessage(), e);
		}

		// 系统停止时，业务级的操作
		System.out.println("***系统正在进行业务级资源回收...");
		try {
			DataSet dsPlugins = ReadXmlUtil.readXml4KeyValue("shutdownPlugins.xml");
			for (int i = 0, n = dsPlugins.size(); i < n; i++) {
				String bizSuhName = dsPlugins.getString(i, "value");
				Class<?> bizSuhClass = Class.forName(bizSuhName);
				ShutDownHandler bizSuh = (ShutDownHandler) bizSuhClass.newInstance();
				bizSuh.deal(arg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			SysLogUtil.logError(AppListener.class, "系统正在进行业务级资源回收时出现异常:"
					+ e.getMessage(), e);
		}

		// 结束轮询服务
		if (GlobalVars.IS_START_POLLING) {
			System.out.println("***系统正在停止轮询服务程序...");
			this.timer.cancel();
		}

		// 记录系统日志
		SysLogUtil.logInfo(AppListener.class, "******系统停止******");
	}
}
