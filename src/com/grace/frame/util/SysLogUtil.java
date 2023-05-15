package com.grace.frame.util;

import org.apache.log4j.Logger;

/**
 * 使用log4j进行系统级的日志记录
 * 
 * @author yjc
 */
public class SysLogUtil{

	/**
	 * log4j日志Info信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logInfo(Class<?> cls, Object msg, Throwable ex) {
		Logger logger = Logger.getLogger(cls);
		logger.info(msg, ex);
	}

	/**
	 * log4j日志Info信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logInfo(Class<?> cls, Object msg) {
		Logger logger = Logger.getLogger(cls);
		logger.info(msg);
	}

	/**
	 * log4j日志Info信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logInfo(Object msg, Throwable ex) {
		Logger logger = Logger.getLogger(SysLogUtil.class);
		logger.info(msg, ex);
	}

	/**
	 * log4j日志Info信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logInfo(Object msg) {
		Logger logger = Logger.getLogger(SysLogUtil.class);
		logger.info(msg);
	}

	/**
	 * log4j日志warn信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logWarn(Class<?> cls, Object msg, Throwable ex) {
		Logger logger = Logger.getLogger(cls);
		logger.warn(msg, ex);
	}

	/**
	 * log4j日志warn信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logWarn(Class<?> cls, Object msg) {
		Logger logger = Logger.getLogger(cls);
		logger.warn(msg);
	}

	/**
	 * log4j日志warn信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logWarn(Object msg, Throwable ex) {
		Logger logger = Logger.getLogger(SysLogUtil.class);
		logger.warn(msg, ex);
	}

	/**
	 * log4j日志warn信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logWarn(Object msg) {
		Logger logger = Logger.getLogger(SysLogUtil.class);
		logger.warn(msg);
	}

	/**
	 * log4j日志ERROR信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logError(Class<?> cls, Object msg, Throwable ex) {
		Logger logger = Logger.getLogger(cls);
		logger.error(msg, ex);
	}

	/**
	 * log4j日志ERROR信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logError(Class<?> cls, Object msg) {
		Logger logger = Logger.getLogger(cls);
		logger.error(msg);
	}

	/**
	 * log4j日志ERROR信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logError(Object msg, Throwable ex) {
		Logger logger = Logger.getLogger(SysLogUtil.class);
		logger.error(msg, ex);
	}

	/**
	 * log4j日志ERROR信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logError(Object msg) {
		Logger logger = Logger.getLogger(SysLogUtil.class);
		logger.error(msg);
	}

	/**
	 * log4j日志FATAL致命信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logFatal(Class<?> cls, Object msg, Throwable ex) {
		Logger logger = Logger.getLogger(cls);
		logger.fatal(msg, ex);
	}

	/**
	 * log4j日志FATAL致命信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logFatal(Class<?> cls, Object msg) {
		Logger logger = Logger.getLogger(cls);
		logger.fatal(msg);
	}

	/**
	 * log4j日志FATAL致命信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logFatal(Object msg, Throwable ex) {
		Logger logger = Logger.getLogger(SysLogUtil.class);
		logger.fatal(msg, ex);
	}

	/**
	 * log4j日志FATAL致命信息级别的记录
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-15
	 * @since V1.0
	 */
	public static void logFatal(Object msg) {
		Logger logger = Logger.getLogger(SysLogUtil.class);
		logger.fatal(msg);
	}
}
