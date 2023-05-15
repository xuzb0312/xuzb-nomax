package com.grace.frame.hibernate;

import java.util.HashMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.grace.frame.exception.AppException;

/**
 * 事物控制
 * <p>
 * Transaction接口负责事物控制，是一个可选的API，可以选择不使用这个接口，取而代之的是Hibernate 的设计者自己写的底层事务处理代码。
 * Transaction 接口是对实际事务实现的一个抽象，这些实现包括JDBC的事务、JTA 中的UserTransaction、甚至可以是CORBA
 * 事务。之所以这样设计是能让开发者能够使用一个统一事务的操作界面，使得自己的项目可以在不同的环境和容器之间方便地移植。
 * </p>
 * 
 * @author yjc
 */
public class TransManager{

	private static final ThreadLocal<HashMap<String, Object>> threadTransMap = new ThreadLocal<HashMap<String, Object>>();

	public TransManager() {}

	public int begin() throws AppException {
		return begin("hibernate");
	}

	/**
	 * 事物开启
	 * 
	 * @param dbName
	 * @return
	 * @throws AppException
	 */
	public int begin(String dbName) throws AppException {
		HashMap<String, Object> transMap = (HashMap<String, Object>) threadTransMap.get();
		try {
			if (dbName == null || dbName.equals("")) {
				dbName = "hibernate";
			}
			if (transMap != null && transMap.containsKey(dbName)) {
				return 0;
			} else {
				Session session = HibernateSession.currentSession(dbName);
				Transaction tx = session.beginTransaction();
				if (transMap == null) {
					transMap = new HashMap<String, Object>();
				}
				transMap.put(dbName, tx);
				threadTransMap.set(transMap);
				return 1;
			}
		} catch (HibernateException e) {
			throw new AppException("进行数据库操作时发生HibernateException,异常信息为:"
					+ e.getMessage(), "HibernateException");
		}
	}

	public void commit() throws AppException {
		commit("hibernate");
	}

	/**
	 * 事物提交
	 * 
	 * @param dbName
	 * @throws AppException
	 */
	public void commit(String dbName) throws AppException {
		HashMap<String, Object> transMap = (HashMap<String, Object>) threadTransMap.get();
		if (dbName == null || dbName.equals("")) {
			dbName = "hibernate";
		}
		if (transMap != null && transMap.containsKey(dbName)) {
			try {
				Transaction t = (Transaction) transMap.get(dbName);
				t.commit();
			} catch (HibernateException e) {
				throw new AppException("进行数据库操作时发生HibernateException,异常信息为:"
						+ e.getMessage(), "HibernateException");
			} finally {
				transMap.remove(dbName);
				threadTransMap.set(transMap);
				HibernateSession.closeSession(dbName);
			}
		}
	}

	public void rollback() throws AppException {
		rollback("hibernate");
	}

	/**
	 * 事物回滚
	 * 
	 * @param dbName
	 * @throws AppException
	 */
	public void rollback(String dbName) throws AppException {
		HashMap<String, Object> transMap = (HashMap<String, Object>) threadTransMap.get();
		if (dbName == null || dbName.equals("")) {
			dbName = "hibernate";
		}
		if (transMap != null && transMap.containsKey(dbName)) {
			try {
				Transaction t = (Transaction) transMap.get(dbName);
				t.rollback();
			} catch (HibernateException e) {
				throw new AppException("进行数据库操作时发生HibernateException,异常信息为:"
						+ e.getMessage(), "HibernateException");
			} finally {
				transMap.remove(dbName);
				threadTransMap.set(transMap);
				HibernateSession.closeSession(dbName);
			}
		}
	}

	/**
	 * 获取事物的状态
	 * 
	 * @param dbName
	 * @return
	 * @throws AppException
	 */
	public static boolean getTransState(String dbName) throws AppException {
		HashMap<String, Object> transMap = (HashMap<String, Object>) threadTransMap.get();
		if (dbName == null || dbName.equals("")) {
			dbName = "hibernate";
		}
		if (transMap != null && transMap.containsKey(dbName)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取事物状态
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public static boolean getTransState() throws AppException {
		return getTransState("hibernate");
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
		threadTransMap.get().clear();
	}
}
