package com.grace.frame.redis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.util.StringUtil;
import com.grace.frame.util.SysLogUtil;

/**
 * 读取配置文件工具类
 * 
 * @author yjc
 */
public class PropertiesUtil{
	private final static String RedisConfFileName = "redis.properties";// 配置文件名称
	private static Properties props;
	static {
		PropertiesUtil.init();// 初始化
	}

	/**
	 * 初始化
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static void init() {
		PropertiesUtil.props = new Properties();
		if (GlobalVars.ENABLED_REDIS) {// 未启用，则不加载
			InputStream is = null;
			try {
				if (GlobalVars.CONFIGINWAR) {
					is = PropertiesUtil.class.getClassLoader()
						.getResourceAsStream(PropertiesUtil.RedisConfFileName);
				} else {
					File file = new File(GlobalVars.CONFIGFILEPATH
							+ File.separator + PropertiesUtil.RedisConfFileName);
					is = new FileInputStream(file);
				}
				PropertiesUtil.props.load(new InputStreamReader(is, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				SysLogUtil.logError("redis配置文件读取失败", e);
			} catch (IOException e) {
				e.printStackTrace();
				SysLogUtil.logError("redis配置文件读取失败", e);
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
					}
				}
			}
		}
	}

	/**
	 * 获取属性
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String getProperty(String key) {
		String value = props.getProperty(key.trim());
		if (StringUtil.chkStrNull(value)) {
			return null;
		}
		return value.trim();
	}

	/**
	 * 获取属性
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-18
	 * @since V1.0
	 */
	public static String getProperty(String key, String defaultValue) {
		String value = props.getProperty(key.trim());
		if (StringUtil.chkStrNull(value)) {
			value = defaultValue;
		}
		return value.trim();
	}
}
