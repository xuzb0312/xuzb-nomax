package com.grace.frame.listener;

import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.constant.GlobalVarsUtil;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.FileIOUtil;
import com.grace.frame.util.ueditor.UeditorUtil;
import com.grace.frame.workflow.ThreadDelegator;

/**
 * 系统关闭时，框架所要进行的操作
 * 
 * @author yjc
 */
public class AppShutDownHandler implements ShutDownHandler{

	/**
	 * 框架操作
	 * 
	 * @throws BizException
	 */
	public void deal(ServletContextEvent arg) throws BizException {
		// 初始化富文本编辑器文件缓存数据
		System.out.println("***处理富文本编辑器文件缓存数据...");
		String tempPath = this.getClass().getResource("/").getPath();
		tempPath = tempPath.substring(1, tempPath.lastIndexOf("/"));
		tempPath = tempPath.substring(0, tempPath.lastIndexOf("/"));
		tempPath = tempPath.substring(0, tempPath.lastIndexOf("/"));
		tempPath = tempPath.replace("/", File.separator);
		tempPath = tempPath + File.separator + "ueditor" + File.separator;
		FileIOUtil.deleteDirectory(tempPath);
		UeditorUtil.CACHEFILE.clear();

		// 停止websocket-server
		if (GlobalVars.ENABLED_WEBSOCKET) {
			System.out.println("***系统正在终止WebSocket服务器...");
			GlobalVarsUtil.stopWebsocektServer();
		}

		// 数据库连接的驱动程序停止操作
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		Driver driver = null;
		while (drivers.hasMoreElements()) {
			try {
				driver = drivers.nextElement();
				DriverManager.deregisterDriver(driver);
			} catch (SQLException ex) {
			}
		}

		// 停止线程池
		try {
			System.out.println("***系统正在终止ThreadDelegator线程池...");
			ThreadDelegator.shutdown();
		} catch (Exception e) {
		}
	}
}
