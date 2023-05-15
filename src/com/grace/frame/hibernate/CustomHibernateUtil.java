package com.grace.frame.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.grace.frame.exception.AppException;
import com.grace.frame.util.Sql;
import com.grace.frame.util.Sql.DBType;

/**
 * 自定义hibernate的工具
 * 
 * @author yjc
 */
public class CustomHibernateUtil{
	/**
	 * 给定dbName,返回可以用于建立数据库连接的SessionFactory
	 * 
	 * @param dbName
	 * @return
	 * @throws Exception
	 */
	public static SessionFactory getFactory(String dbName) throws AppException {
		if (!Sql.CUSTOM_HIBERNATE_DBINFO_MAP.containsKey(dbName)) {
			throw new AppException(dbName + "在系统缓存中不存在，无法进行初始化");
		}
		Object[] dbInfo = Sql.CUSTOM_HIBERNATE_DBINFO_MAP.get(dbName);

		// 数据库的自定义连接参数
		DBType dbType = (DBType) dbInfo[0];
		String url = (String) dbInfo[1];
		String username = (String) dbInfo[2];
		String password = (String) dbInfo[3];
		boolean isEncrypt = (Boolean) dbInfo[4];
		if (dbType == null) {
			dbType = DBType.Oracle;
		}

		// 初始化参数配置文件
		Configuration configration = new Configuration();
		configration.configure("hibernate.cfg.xml");
		configration.setProperty("hibernate.connection.username", username);
		configration.setProperty("hibernate.connection.password", password);
		if (isEncrypt) {
			configration.setProperty("hibernate.connection.provider_class", "com.grace.frame.hibernate.EFC3P0ConnectionProvider");
		} else {
			configration.getProperties()
				.remove("hibernate.connection.provider_class");
		}
		// 不同类型的数据库的参数
		if (DBType.Oracle.equals(dbType)) {// 连接oracle数据库
			configration.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@"
					+ url);

			configration.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
			configration.setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver");
		} else if (DBType.SqlServer.equals(dbType)) {// 连接sqlserver数据库
			configration.setProperty("hibernate.connection.url", "jdbc:sqlserver://"
					+ url);

			configration.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
			configration.setProperty("hibernate.connection.driver_class", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
			configration.setProperty("hibernate.myeclipse.connection.profile", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} else if (DBType.MySql.equals(dbType)) {
			configration.setProperty("hibernate.connection.url", "jdbc:mysql://"
					+ url);

			configration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
			configration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");

			// 特殊控制
			configration.setProperty("hibernate.connection.characterEncoding", "utf8");// 连接编码
			configration.setProperty("hibernate.connection.useOldAliasMetadataBehavior", "true");// 别名
		} else if (DBType.DB2.equals(dbType)) {
			configration.setProperty("hibernate.connection.url", "jdbc:db2://"
					+ url);

			configration.setProperty("hibernate.dialect", "net.sf.hibernate.dialect.DB2Dialect");
			configration.setProperty("hibernate.connection.driver_class", "com.ibm.db2.jdbc.app.DB2Driver");
		} else {
			throw new AppException("您所维护的数据库连接:" + dbName + "对应的数据库类型无法识别。");
		}

		SessionFactory factory = configration.buildSessionFactory();
		return factory;
	}
}
