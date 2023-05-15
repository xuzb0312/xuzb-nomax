package com.grace.frame.hibernate;

import java.io.File;
import java.util.HashMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.Sql;

/**
 * hibernate会话信息
 * <p>
 * Session接口负责执行被持久化对象的CRUD操作(CRUD的任务是完成与数据库的交流，包含了很多常见的SQL语句。)
 * </p>
 * 
 * @author yjc
 */
public class HibernateSession{
	public static final ThreadLocal<HashMap<String, Object>> sessionMap = new ThreadLocal<HashMap<String, Object>>();
	private static final Configuration cfg = new Configuration();
	private static HashMap<String, Object> sfMap = new HashMap<String, Object>();

	public HibernateSession() {}

	/**
	 * 得到session
	 * 
	 * @param dbName
	 * @return
	 * @throws AppException
	 */
	public static Session currentSession() throws AppException {
		return currentSession("hibernate");
	}

	/**
	 * 得到session
	 * 
	 * @param dbName
	 * @return
	 * @throws AppException
	 */
	public static Session currentSession(String dbName) throws AppException {
		HashMap<String, Object> sMap = (HashMap<String, Object>) sessionMap.get();
		Session s;
		try {
			if (dbName == null || dbName.equals("")) {
				dbName = "hibernate";
			}
			if (sMap != null && sMap.size() > 0 && sMap.containsKey(dbName)) {
				s = (Session) sMap.get(dbName);
			} else {
				if (sfMap != null && sfMap.size() > 0
						&& sfMap.containsKey(dbName)) {
					SessionFactory sf = (SessionFactory) sfMap.get(dbName);
					s = sf.openSession();
					if (sMap == null) {
						sMap = new HashMap<String, Object>();
					}
					sMap.put(dbName, s);
					sessionMap.set(sMap);
				} else {
					SessionFactory sf = null;
					if (Sql.CUSTOM_HIBERNATE_DBINFO_MAP.containsKey(dbName)) {
						// 自定义的数据量连接map中存在，则使用缓存map数据创建连接
						sf = CustomHibernateUtil.getFactory(dbName);
					} else {
						if (GlobalVars.CONFIGINWAR) {
							sf = cfg.configure(dbName + ".cfg.xml")
								.buildSessionFactory();
						} else {
							File file = new File(GlobalVars.CONFIGFILEPATH
									+ File.separator + dbName + ".cfg.xml");
							sf = cfg.configure(file).buildSessionFactory();
						}
					}
					sfMap.put(dbName, sf);
					s = sf.openSession();
					if (sMap == null) {
						sMap = new HashMap<String, Object>();
					}
					sMap.put(dbName, s);
					sessionMap.set(sMap);
				}
			}
		} catch (HibernateException e) {
			throw new AppException("在通过sessionFactory获取hibernateSession时出错,"
					+ e.getMessage(), "HibernateException");
		}
		return s;
	}

	/**
	 * 关闭session
	 * 
	 * @param dbName
	 * @throws AppException
	 */
	public static void closeSession(String dbName) throws AppException {
		HashMap<String, Object> sMap = (HashMap<String, Object>) sessionMap.get();
		if (dbName == null || dbName.equals("")) {
			dbName = "hibernate";
		}
		if (sMap.containsKey(dbName)) {
			Session s = (Session) sMap.get(dbName);
			sMap.remove(dbName);
			sessionMap.set(sMap);
			if (s != null) {
				try {
					s.close();
				} catch (HibernateException he) {
					throw new AppException("在关闭hibernateSession时出错,"
							+ he.getMessage(), "HibernateException");
				}
			}
		}
	}

	/**
	 * 关闭session
	 * 
	 * @throws AppException
	 */
	public static void closeSession() throws AppException {
		closeSession("hibernate");
	}

	/**
	 * 获取sessionFactory
	 * 
	 * @author yjc
	 */
	public static SessionFactory getSessionFactory(String dbName) throws AppException {
		if (dbName == null || dbName.equals("")) {
			dbName = "hibernate";
		}
		SessionFactory sf = null;
		if (sfMap != null && sfMap.size() != 0) {
			sf = (SessionFactory) sfMap.get(dbName);
		}
		return sf;
	}

	/**
	 * 网络环境不稳定的情况下，连接断掉后，无法再次重连，导致系统无法使用增加，应急方案，进行数据连接的重置 <br>
	 * 【慎重使用】
	 * 
	 * @author yjc
	 * @date 创建时间 2016-10-11
	 * @since V1.0
	 */
	public static void clear() {
		sessionMap.get().clear();
		sfMap.clear();
	}

	/**
	 * 移除缓存中的factory信息
	 * 
	 * @author yjc
	 * @date 创建时间 2017-7-17
	 * @since V1.0
	 */
	public static void removeSfMapByDbName(String dbName) {
		if (sfMap != null && sfMap.size() > 0 && sfMap.containsKey(dbName)) {
			sfMap.remove(dbName);
		}
	}
}
