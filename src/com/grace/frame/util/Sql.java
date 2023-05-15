package com.grace.frame.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.engine.SessionImplementor;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.hibernate.HibernateSession;
import com.grace.frame.hibernate.TransManager;
import com.grace.frame.redis.RedisUtil;

/**
 * 数据库的操作；查询、更新、事物的提交、回滚等
 * 
 * @author yjc
 */
public class Sql{
	private static final String REDIS_KEY_PREFIX = "sqlCache_";
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sqlString = null;
	private Object[] para = null;
	private String dbName = null;

	// 高速数据缓存区Table-使用线程安全ConcurrentHashMap
	private static ConcurrentHashMap<String, DataMap> SQL_DATA_CACHE = new ConcurrentHashMap<String, DataMap>();

	// 增加自定义的数据操作配置
	public static ConcurrentHashMap<String, Object[]> CUSTOM_HIBERNATE_DBINFO_MAP = new ConcurrentHashMap<String, Object[]>();// 用于存储，自定义的数据库连接的参数

	/**
	 * 初始化配置数据信息（跟在根目录下创建一个配置文件一样--只是在内存中初始化而已） <br>
	 * 参数：<br>
	 * dbName:数据库连接的名称，唯一。<br>
	 * dbType:oracle,sqlserver,mysql,db2 <br>
	 * url:数据连接地址,例如：<br>
	 * oracle:localhost:1521:dbname;<br>
	 * sqlserver:localhost:1433;DatabaseName=hibernate<br>
	 * mysql:localhost:3306/myee<br>
	 * db2:localhost:5000/sample<br>
	 * username:用户名<br>
	 * password：密码<br>
	 * isEncrypt:用户名和密码是否加密形式<br>
	 * 
	 * @author yjc
	 * @date 创建时间 2017-7-17
	 * @since V1.0
	 */
	public static void initCustomDbConf(String dbName, DBType dbType,
			String url, String username, String password, boolean isEncrypt) {
		Object[] arrDbinfo = { dbType, url, username, password, isEncrypt };// 数据库连接信息
		Sql.CUSTOM_HIBERNATE_DBINFO_MAP.put(dbName, arrDbinfo);
		HibernateSession.removeSfMapByDbName(dbName);// 重新初始化后，需要移除缓存的sessionFactroy信息
	}

	/**
	 * 初始化配置数据信息（跟在根目录下创建一个配置文件一样--只是在内存中初始化而已） <br>
	 * 参数：<br>
	 * dbName:数据库连接的名称，唯一。<br>
	 * dbType:oracle,sqlserver,mysql,db2 <br>
	 * url:数据连接地址,例如：<br>
	 * oracle:localhost:1521:dbname;<br>
	 * sqlserver:localhost:1433;DatabaseName=hibernate<br>
	 * mysql:localhost:3306/myee<br>
	 * db2:localhost:5000/sample<br>
	 * username:用户名<br>
	 * password：密码<br>
	 * 
	 * @author yjc
	 * @date 创建时间 2017-7-17
	 * @since V1.0
	 */
	public static void initCustomDbConf(String dbName, DBType dbType,
			String url, String username, String password) {
		Sql.initCustomDbConf(dbName, dbType, url, username, password, false);
	}

	/**
	 * 数据库类型
	 * 
	 * @author yjc
	 */
	public enum DBType {
		Oracle, MySql, SqlServer, DB2
	};

	/**
	 * 默认配置文件名
	 */
	public Sql() {
		this.dbName = "hibernate";
	}

	/**
	 * 默认配置文件名
	 */
	public Sql(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * 为sql设置sql语句
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setSql(String sqlstmt) {
		this.sqlString = sqlstmt;
		int i = -1;
		int count = 0;
		while (true) {
			i = sqlString.indexOf("?", i + 1);
			if (i == -1) {
				break;
			}
			count++;
		}
		para = new Object[count];// 根据sqlstmt中参数个数来初始化参数数组
	}

	/**
	 * 得到当前sql
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String getSql() {
		return this.sqlString;
	}

	/**
	 *空类型
	 */
	private class NullValue{
		private int type;

		public NullValue(int type) {
			this.type = type;
		}

		public int getType() {
			return this.type;
		}
	}

	/**
	 * Blob 类型值
	 */
	private class BlobValue implements Serializable{
		private static final long serialVersionUID = 1L;
		private byte[] value;

		public BlobValue(String value) {
			if (value == null) {
				value = "";
			}
			try {
				this.value = value.getBytes("UTF-8");
			} catch (Exception e) {
				this.value = value.getBytes();
			}
		}

		public BlobValue(byte[] value) {
			if (value == null) {
				value = new byte[0];
			}
			this.value = value;
		}

		public byte[] getValue() {
			return this.value;
		}

		public int getLength() {
			return this.value.length;
		}
	}

	/**
	 * 检查此参数是否存在
	 * 
	 * @param index 参数的位置
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	private void checkIndex(int index) throws AppException {
		if (index < 1 || index > para.length) {
			throw new AppException("参数索引" + index + "不合法", "Sql");
		}
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value 参数值，String型
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setString(int index, String value) throws AppException {
		this.checkIndex(index);
		if (value == null) {
			this.setNull(index, Types.VARCHAR);
		} else {
			para[index - 1] = value;
		}
	}

	/**
	 * 设置参数值（对于空字符串或者trim后为空的串，均设置为Null值）
	 * 
	 * @param index 参数位置
	 * @param value 参数值，String型
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setStringIfEmpty(int index, String value) throws AppException {
		this.checkIndex(index);
		if (StringUtil.chkStrNull(value)) {
			this.setNull(index, Types.VARCHAR);
		} else {
			para[index - 1] = value;
		}
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value int型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setInt(int index, int value) throws AppException {
		this.checkIndex(index);
		para[index - 1] = new Integer(value);
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value int型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setInt(int index, Integer value) throws AppException {
		this.checkIndex(index);
		if (value == null) {
			this.setNull(index, Types.VARCHAR);
		} else {
			para[index - 1] = value;
		}
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value Blob型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setBlob(int index, String value) throws AppException {
		this.checkIndex(index);
		if (value == null) {
			this.setNull(index, Types.LONGVARCHAR);
		} else {
			para[index - 1] = new BlobValue(value);
		}
	}

	/**
	 * 设置参数值(byte数据)
	 * 
	 * @param index 参数位置
	 * @param value Blob型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setBlob(int index, byte[] value) throws AppException {
		this.checkIndex(index);
		if (value == null || value.length <= 0) {
			this.setNull(index, Types.LONGVARCHAR);
		} else {
			para[index - 1] = new BlobValue(value);
		}
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value double型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setDouble(int index, double value) throws AppException {
		this.checkIndex(index);
		para[index - 1] = new Double(value);
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value double型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setDouble(int index, Double value) throws AppException {
		this.checkIndex(index);
		if (value == null) {
			this.setNull(index, Types.VARCHAR);
		} else {
			para[index - 1] = value;
		}
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value boolean型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setBoolean(int index, boolean value) throws AppException {
		this.checkIndex(index);
		para[index - 1] = Boolean.valueOf(value);
	}

	/**
	 * 设置日期型参数值（精确到秒）
	 * 
	 * @param index 参数位置
	 * @param value sql.Date型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setDate(int index, Date value) throws AppException {
		this.checkIndex(index);
		if (value == null) {
			this.setNull(index, Types.DATE);
		} else {
			para[index - 1] = new Timestamp(((Date) value).getTime());
		}
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value sql.Timestamp型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setTimestamp(int index, Timestamp value) throws AppException {
		this.checkIndex(index);
		if (value == null) {
			this.setNull(index, Types.TIMESTAMP);
		} else {
			para[index - 1] = value;
		}
	}

	/**
	 * 设置参数值
	 * 
	 * @param index 参数位置
	 * @param value string型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setLongVarchar(int index, String value) throws AppException {
		this.checkIndex(index);
		if (value == null) {
			this.setNull(index, Types.LONGVARCHAR);
		} else {
			para[index - 1] = new StringBuffer(value);
		}
	}

	/**
	 * 将参数设为数据库的空值
	 * 
	 * @param index 参数位置
	 * @param value int型参数值
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setNull(int index, int sqlType) throws AppException {
		this.checkIndex(index);
		para[index - 1] = new NullValue(sqlType);
	}

	/**
	 * 使用纯sql进行执行查询操作-有些操作可以使用该方法进行-提升查询效率
	 * <p>
	 * 该方法存在执行风险性，使用前提前咨询
	 * <p>
	 * 
	 * @author yjc
	 * @date 创建时间 2016-9-8
	 * @since V1.0
	 */
	public DataSet executeQuickQuery() throws AppException {
		this.setSql(this.getSqlString());
		return this.executeQuery();
	}

	/**
	 * 使用纯sql进行执行查询操作-有些操作可以使用该方法进行-提升查询效率
	 * <p>
	 * 该方法存在执行风险性，使用前提前咨询--限制行数
	 * <p>
	 * 
	 * @author yjc
	 * @date 创建时间 2016-9-8
	 * @since V1.0
	 */
	public DataSet executeQuickQuery(int minNum, int maxNum) throws AppException {
		this.setSql(this.getSqlString());
		return this.executeQuery(minNum, maxNum);
	}

	/**
	 * 查询数据-使用缓存机制-首先查询缓存是否缓存了指定数据信息<br>
	 * timeLimit:超时时间，缓存数据有效期,单位秒，如果为0，则意味着不会超时 <br>
	 * 全局缓存<br>
	 * <br>
	 * resetCache:是否强制重置该sql的缓存。如果为true则不管有没有在有效期内，会将其对应缓存内容重置掉。
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	private DataSet executeQueryByCache4Table(long timeLimit, boolean resetCache) throws AppException {
		String sqlStr = this.getSqlString().trim();
		String key = SecUtil.encodeStrByOriginalMd5(sqlStr) + "@" + this.dbName;
		double now = (new Date()).getTime();
		if (!resetCache && Sql.SQL_DATA_CACHE.containsKey(key)) {
			DataMap dmCache = (DataMap) Sql.SQL_DATA_CACHE.get(key);
			double putTime = (Double) dmCache.get("puttime");
			if (timeLimit == 0 || (now - putTime) <= (timeLimit * 1000)) {
				return dmCache.getDataSet("data").clone();// 缓存数据返回
			}
		}

		// 如果无法从缓存中获取，则连接数据库获取，然后放入缓存
		DataSet dsData = this.executeQuery();

		DataMap dmTemp = new DataMap();
		dmTemp.put("puttime", now);
		dmTemp.put("data", dsData);
		Sql.SQL_DATA_CACHE.put(key, dmTemp);

		// 返回数据
		return dsData.clone();
	}

	/**
	 * 查询数据-使用缓存机制-首先查询缓存是否缓存了指定数据信息<br>
	 * timeLimit:超时时间，缓存数据有效期,单位秒，如果为0，则意味着不会超时 <br>
	 * 全局缓存<br>
	 * <br>
	 * resetCache:是否强制重置该sql的缓存。如果为true则不管有没有在有效期内，会将其对应缓存内容重置掉。
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	private DataSet executeQueryByCache4Redis(long timeLimit, boolean resetCache) throws Exception {
		String sqlStr = this.getSqlString().trim();
		String key = Sql.REDIS_KEY_PREFIX
				+ SecUtil.encodeStrByOriginalMd5(sqlStr) + "@" + this.dbName;
		if (!resetCache) {
			DataMap dmCache = RedisUtil.getDataMap(key);
			if (null != dmCache) {
				return dmCache.getDataSet("data");
			}
		}

		// 如果无法从缓存中获取，则连接数据库获取，然后放入缓存
		DataSet dsData = this.executeQuery();
		DataMap dmTemp = new DataMap();
		dmTemp.put("puttime", System.currentTimeMillis());
		dmTemp.put("data", dsData);
		if (timeLimit <= 0) {
			RedisUtil.set(key, dmTemp);
		} else {
			RedisUtil.set(key, dmTemp, (int) timeLimit);
		}
		// 返回数据
		return dsData;
	}

	/**
	 * 查询数据-使用缓存机制-首先查询缓存是否缓存了指定数据信息<br>
	 * timeLimit:超时时间，缓存数据有效期,单位秒，如果为0，则意味着不会超时 <br>
	 * 全局缓存<br>
	 * <br>
	 * resetCache:是否强制重置该sql的缓存。如果为true则不管有没有在有效期内，会将其对应缓存内容重置掉。
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public DataSet executeQueryByCache(long timeLimit, boolean resetCache) throws Exception {
		if (GlobalVars.ENABLED_REDIS) {
			return this.executeQueryByCache4Redis(timeLimit, resetCache);
		} else {
			return this.executeQueryByCache4Table(timeLimit, resetCache);
		}
	}

	/**
	 * 通过缓存查询
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2018-5-15
	 * @since V1.0
	 */
	public DataSet executeQueryByCache(long timeLimit) throws Exception {
		return this.executeQueryByCache(timeLimit, false);
	}

	/**
	 * 清空缓存操作--防止缓存数据过大，占用系统资源
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static void clearCache() {
		if (GlobalVars.ENABLED_REDIS) {// 启用的redis则需要清空一下redis的缓存
			try {
				RedisUtil.delMulti(Sql.REDIS_KEY_PREFIX + "*");
			} catch (Exception e) {
				e.printStackTrace();
				SysLogUtil.logError("清空redis的Sql缓存失败", e);
			}
		}
		Sql.SQL_DATA_CACHE.clear();
	}

	/**
	 * 对于方法名称的简化
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public DataSet query() throws AppException {
		return this.executeQuery();
	}

	/**
	 * 执行查询语句
	 * 
	 * @return 查询结果集
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataSet executeQuery() throws AppException {
		try {
			Session session = HibernateSession.currentSession(this.dbName);
			this.prepare(session);
			this.setParas(para);
			rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			String[] column = new String[rsmd.getColumnCount()];
			LinkedHashMap<String, String> typeList = new LinkedHashMap<String, String>();
			for (int i = 0; i < column.length; i++) {
				column[i] = rsmd.getColumnName(i + 1);
				column[i] = column[i].toLowerCase();
				int type = rsmd.getColumnType(i + 1);
				if (type == Types.CHAR || type == Types.VARCHAR
						|| type == Types.LONGVARCHAR) {// 在oracle数据库中长文本是clob类型
					typeList.put(column[i], "string");
				} else if (type == Types.NUMERIC || type == Types.INTEGER) {
					typeList.put(column[i], "number");
				} else if (type == Types.DATE || type == Types.TIME
						|| type == Types.TIMESTAMP) {
					typeList.put(column[i], "date");
				} else if (type == Types.BOOLEAN) {
					typeList.put(column[i], "boolean");
				} else {// 处理未知类型 Blob 和 Clob 以及其他特殊类型列
					typeList.put(column[i], "null");
				}
			}

			DataSet ds = new DataSet();
			ds.setTypeList(typeList);
			while (rs.next()) {
				ds.addRow();
				for (int j = 0; j < column.length; j++) {
					// 对rowid进行特殊处理
					if ("rowid".equals(column[j].toLowerCase())) {
						ds.put(rs.getRow() - 1, column[j], rs.getString(j + 1));
					} else {
						if (rsmd.getColumnType(j + 1) == Types.LONGVARCHAR) {
							// LONGVARCHAR类型，使用Reader读出成String
							String longVarCharValue = null;
							java.io.Reader reader = rs.getCharacterStream(j + 1);

							if (reader != null) {
								try {
									StringBuffer sb = new StringBuffer();
									while (true) {
										int ch = reader.read();
										if (ch == -1) {
											break;
										}
										sb.append((char) ch);
									}
									reader.close();
									longVarCharValue = sb.toString();
								} catch (IOException e) {
									throw new AppException("处理LONGVARCHAR类型数据出错!", "Sql");
								}
							}
							ds.put(rs.getRow() - 1, column[j], longVarCharValue);
						} else if (rsmd.getColumnType(j + 1) == Types.TIMESTAMP) {
							ds.put(rs.getRow() - 1, column[j], rs.getTimestamp(j + 1));
						} else {
							ds.put(rs.getRow() - 1, column[j], rs.getObject(j + 1));
						}
					}
				}
			}
			return ds;
		} catch (HibernateException e) {
			throw new AppException("HibernateException-" + e.getMessage(), e);
		} catch (SQLException e) {
			throw new AppException("SQLException-" + e.getMessage(), e);
		} finally {
			StringBuffer sb = new StringBuffer();
			if (this.rs != null) {
				try {
					this.rs.close();
				} catch (Exception e) {
					sb.append("this.rs.close：" + e.getMessage() + "\n");
				}
				this.rs = null;
			}
			if (this.pstmt != null) {
				try {
					this.pstmt.close();
				} catch (Exception e) {
					sb.append("this.pstmt.close()：" + e.getMessage() + "\n");
				}
				this.pstmt = null;
			}
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (Exception e) {
					sb.append("this.conn.close()：" + e.getMessage() + "\n");
				}
				this.conn = null;
			}
			try {
				if (!TransManager.getTransState(this.dbName)) {// 一旦事务关闭,那么清空session
					HibernateSession.closeSession(this.dbName);
				}
			} catch (Exception e) {
				sb.append("事务：" + e.getMessage() + "\n");
			}
			if (sb.toString().length() > 0) {
				throw new AppException("关闭sql时出错:" + sb.toString(), "Sql");
			}
		}
	}

	/**
	 * 查询一行
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataMap queryOneRow() throws AppException {
		DataSet ds = this.executeQuery(0, 1);
		if (ds.isEmpty()) {// 对于没有查询到数据，则直接返回空
			return null;
		}
		return ds.get(0);
	}

	/**
	 * 查询一行(如果传入提示信息，则没有数据时抛出异常：BizException)
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataMap queryOneRow(String nullMsg) throws AppException, BizException {
		if (StringUtil.chkStrNull(nullMsg)) {
			return this.queryOneRow();
		}
		DataSet ds = this.executeQuery(0, 1);
		ds.chkEmpty(nullMsg);// 检测空行
		return ds.get(0);
	}

	/**
	 * 执行查询语句,查询制定范围内的数据，用于分页
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataSet executeQuery(int minNum, int maxNum) throws AppException {
		this.sqlString = "select * from (select rownum as numrow,c.* from ( "
				+ this.sqlString + " ) c) where numrow>=" + minNum
				+ " and numrow<=" + maxNum;
		DataSet ds = this.executeQuery();
		return ds;
	}

	/**
	 * 简化update方法名称
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public int update() throws AppException {
		return this.executeUpdate();
	}

	/**
	 * 执行UPDATE语句 不成功是抛出Exception
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public int executeUpdate() throws AppException {
		Session session = null;
		TransManager trans = null;
		int flag = 0;
		int vi = 0;
		try {
			session = HibernateSession.currentSession(this.dbName);
			trans = new TransManager();
			flag = trans.begin(this.dbName);// 如果已经存在开启了的事务，那么使用该事务，此时flag=0；否则开启一个新的事务，那么flag=1
			this.prepare(session);
			this.setParas(para);
			vi = pstmt.executeUpdate();
			if (1 == flag) {// 如果当前事务是在该方法中开启的，一旦数据库操作成功，那么提交该事务
				trans.commit(this.dbName);
			}
		} catch (SQLException e) {
			throw new AppException("SQLException-" + e.getMessage(), e);
		} finally {
			StringBuffer sb = new StringBuffer();
			if (this.rs != null) {
				try {
					this.rs.close();
				} catch (Exception e) {
					sb.append(e.getMessage() + "\n");
				}
				this.rs = null;
			}
			if (this.pstmt != null) {
				try {
					this.pstmt.close();
				} catch (Exception e) {
					sb.append(e.getMessage() + "\n");
				}
				this.pstmt = null;
			}
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (Exception e) {
					sb.append(e.getMessage() + "\n");
				}
				this.conn = null;
			}
			try {
				if (1 == flag && trans != null) {// 如果当前事务不为空，并且当前事务是在该方法中开启的,一旦操作数据库失败，就要回滚该事务
					trans.rollback(this.dbName);
				}
			} catch (Exception e) {
				sb.append(e.getMessage() + "\n");
			}
			if (sb.toString().length() > 0) {
				throw new AppException("executeUpdate时出错:" + sb.toString());
			}
		}
		return vi;
	}

	/**
	 * 准备session
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	private void prepare(Session session) throws AppException {
		try {
			conn = ((SessionImplementor) session).getJDBCContext()
				.borrowConnection();
			if (conn == null) {
				throw new AppException("获取数据库连接时出错", "Sql");
			}
			conn.setAutoCommit(false); // add by dk 04-03-30
			pstmt = conn.prepareStatement(sqlString);
		} catch (HibernateException e) {
			throw new AppException("HibernateException-" + e.getMessage(), e);
		} catch (SQLException e) {
			throw new AppException("SQLException-" + e.getMessage(), e);
		}
	}

	/**
	 * 设置参数
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	private void setParas(Object[] para) throws AppException {
		if (para == null) {
			return;
		}
		try {
			for (int i = 0; i < para.length; i++) {
				Object o = para[i];
				if (o instanceof java.lang.Integer) {
					pstmt.setInt(i + 1, ((Integer) o).intValue());
				} else if (o instanceof java.lang.Double) {
					pstmt.setDouble(i + 1, ((Double) o).doubleValue());
				} else if (o instanceof java.lang.Boolean) {
					pstmt.setBoolean(i + 1, ((Boolean) o).booleanValue());
				} else if (o instanceof java.lang.String) {
					pstmt.setString(i + 1, (String) o);
				} else if (o instanceof java.sql.Date) {
					pstmt.setDate(i + 1, (java.sql.Date) o);
				} else if (o instanceof java.sql.Timestamp) {
					pstmt.setTimestamp(i + 1, (java.sql.Timestamp) o);
				} else if (o instanceof java.util.Date) {
					pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) o).getTime()));
				} else if (o instanceof java.sql.Blob) {
					pstmt.setBlob(i + 1, (java.sql.Blob) o);
				} else if (o instanceof BlobValue) {
					BlobValue blobValue = (BlobValue) o;
					pstmt.setBinaryStream(i + 1, new java.io.ByteArrayInputStream(blobValue.getValue()), blobValue.getLength());
				}
				// 增加对longVarChar类型数据的处理
				else if (o instanceof StringBuffer) {
					StringBuffer longVarCharValue = (StringBuffer) o;
					StringReader reader = new StringReader(longVarCharValue.toString());
					pstmt.setCharacterStream(i + 1, reader, longVarCharValue.toString()
						.length());
				} else if (o instanceof NullValue) {
					pstmt.setNull(i + 1, ((NullValue) o).getType());
				} else if (o == null) {
					throw new AppException("第" + (i + 1) + "个参数未定义", "Sql");
				} else {
					throw new AppException("第" + (i + 1) + "个参数类型不合法", "Sql");
				}
			}
		} catch (SQLException e) {
			throw new AppException("SQLException-" + e.getMessage(), e);
		}
	}

	/**
	 * 用来获取sqlString。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String getSqlString() throws AppException {
		int count = 0;
		int j = -1;
		Object[] args = new Object[para.length];

		for (int i = 0; i < para.length; i++) {
			Object o = para[i];
			if (o instanceof java.lang.Integer) {
				args[i] = ((Integer) o).toString();
			} else if (o instanceof java.lang.Double) {
				args[i] = ((Double) o).toString();
			} else if (o instanceof java.lang.Boolean) {
				args[i] = ((Boolean) o).toString();
			} else if (o instanceof java.lang.String) {
				args[i] = "'" + ((String) o).replaceAll("'", "''") + "'";
			} else if (o instanceof java.sql.Timestamp) {
				args[i] = "to_date('"
						+ DateUtil.dateToString((Date) o, "yyyyMMddHHmmss")
						+ "','yyyymmddhh24miss')";
			} else if (o instanceof java.util.Date) {
				args[i] = "to_date('"
						+ DateUtil.dateToString((Date) o, "yyyyMMdd")
						+ "','yyyymmdd')";
			} else if (o instanceof java.sql.Blob) {
				throw new AppException("第" + (i + 1) + "个参数类型是Blob，不能转成String", "Sql");
			} else if (o instanceof BlobValue) {
				throw new AppException("第" + (i + 1)
						+ "个参数类型是BlobValue，不能转成String", "Sql");
			} else if (o instanceof StringReader) {// 增加对longVarChar类型数据的处理
				throw new AppException("第" + (i + 1)
						+ "个参数类型是LongVarChar，不能转成String", "Sql");
			} else if (o instanceof NullValue) {
				args[i] = "null";
			} else if (o == null) {
				throw new AppException("第" + (i + 1) + "个参数未定义", "Sql");
			} else {
				throw new AppException("第" + (i + 1) + "个参数类型不合法", "Sql");
			}
		}

		String tmpsql = sqlString;
		while (true) {
			j = tmpsql.indexOf("?", j + 1);
			if (j == -1) {
				break;
			}
			tmpsql = tmpsql.substring(0, j) + (String) args[count]
					+ tmpsql.substring(j + 1);
			j = j + ((String) args[count]).length();
			count++;
		}
		return tmpsql;
	}

	/**
	 * 增加批量
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void addBatch() throws AppException {
		if (pstmt == null) {
			Session session = HibernateSession.currentSession(this.dbName);
			this.prepare(session);
		}
		if (para == null || para.length == 0) {
			throw new AppException("批设置的sql语句必须要有参数");
		}
		this.setParas(para);
		try {
			pstmt.addBatch();
		} catch (SQLException e) {
			throw new AppException("SQLException-" + e.getMessage(), e);
		}
	}

	/**
	 * 增加批量
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public int[] executeBatch() throws AppException {
		TransManager trans = null;
		int flag = 0;
		if (pstmt == null) {
			Session session = HibernateSession.currentSession(this.dbName);
			this.prepare(session);
		}
		try {
			trans = new TransManager();
			flag = trans.begin(this.dbName);// 如果已经存在开启了的事务，那么使用该事务，此时flag=0；否则开启一个新的事务，那么flag=1
			int[] vi = pstmt.executeBatch();

			if (1 == flag) {// 如果当前事务是在该方法中开启的，一旦数据库操作成功，那么提交该事务
				trans.commit(this.dbName);
			}
			return vi;
		} catch (SQLException e) {
			throw new AppException("SQLException-" + e.getMessage(), e);
		} finally {
			StringBuffer sb = new StringBuffer();
			if (this.rs != null) {
				try {
					this.rs.close();
				} catch (Exception e) {
					sb.append(e.getMessage() + "\n");
				}
				this.rs = null;
			}
			if (this.pstmt != null) {
				try {
					this.pstmt.close();
				} catch (Exception e) {
					sb.append(e.getMessage() + "\n");
				}
				this.pstmt = null;
			}
			if (this.conn != null) {
				try {
					this.conn.close();
				} catch (Exception e) {
					sb.append(e.getMessage() + "\n");
				}
				this.conn = null;
			}
			try {
				if (!TransManager.getTransState(this.dbName)) {// 一旦事务关闭,那么清空session
					HibernateSession.closeSession(this.dbName);
				}
			} catch (Exception e) {
				sb.append(e.getMessage() + "\n");
			}
			if (sb.toString().length() > 0) {
				throw new AppException("关闭sql时出错:" + sb.toString());
			}
		}
	}

	/**
	 * 中间提交(提交事物后，会自动开启新事物)
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void commit() throws AppException {
		TransManager trans = new TransManager();
		trans.commit(this.dbName);
		trans.begin(this.dbName);
	}

	/**
	 * 事物回滚-开启新事物
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void rollback() throws AppException {
		TransManager trans = new TransManager();
		trans.rollback(this.dbName);
		trans.begin(this.dbName);
	}

	/**
	 * 设置连接
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setconn(Connection connect) throws Exception {
		this.conn = connect;
	}

	/**
	 * 获取连接
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Connection getconn() throws Exception {
		return this.conn;
	}

	/**
	 * 对于Clob的数据写入数据库
	 */
	public int executeUpdateClob(String blobValue) throws Exception {
		DataSet dsTemp = this.executeQuery();
		int size = dsTemp.size();
		String[] arrCols = dsTemp.getColumnName();
		if (arrCols.length != 1) {
			throw new AppException("sql中的查询结果存在多列【只允许存在一列】。");
		}
		String key = arrCols[0];
		for (int i = 0; i < size; i++) {
			try {
				Clob clob = dsTemp.getClob(i, key);
				Writer writer = clob.setCharacterStream(0);
				writer.write(blobValue);
				writer.flush();
				writer.close();
			} catch (Exception e) {
				throw new AppException("更新Clob数据出现异常。错误信息：" + e.getMessage());
			}
		}
		return size;
	}

	/**
	 * 数据库连接的缓存数据的清空
	 * 
	 * @author yjc
	 * @date 创建时间 2017-7-17
	 * @since V1.0
	 */
	public static void clear() {
		try {
			// 自定义连接信息的清空
			Sql.CUSTOM_HIBERNATE_DBINFO_MAP.clear();

			// 连接信息清空
			HibernateSession.clear();
			TransManager.clear();
		} catch (Exception e) {
			// 不进行特殊处理
		}
	}
}
