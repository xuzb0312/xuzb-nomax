package com.grace.frame.listener;

import javax.servlet.ServletContextEvent;

/**
 * 系统关闭时，需要进行的操作类
 * 
 * @author yjc
 */
public interface ShutDownHandler{
	/**
	 * 操作-处理
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-16
	 * @since V1.0
	 */
	public void deal(ServletContextEvent arg) throws Exception;
}
