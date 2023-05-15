package com.grace.frame.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptException;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.exception.FieldTipException;
import com.grace.frame.util.echarts.json.EchartsOption;

/**
 * 继承HashMap<String,Object>对其属性、方法进行扩展；
 * 
 * @author yjc
 * @since-2019年7月3日-yjc-对于结构增加区分大小写的支持
 */
public class DataMap extends HashMap<String, Object> implements Serializable{

	private static final long serialVersionUID = -7454465694571644474L;
	private boolean caseSensitive = false;// key关键字区分大小写
	private DataSet table;// 对于属于dataset的行的DataMap该值指向DataSet结构

	public DataMap() {
		this.caseSensitive = false;// 默认为不区分，全部小写
	}

	public DataMap(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * 返回DataSet整体结构
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public DataSet table() {
		return this.table;
	}

	/**
	 * 设置DataSet整体结构
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public void table(DataSet table) {
		this.table = table;
	}

	/**
	 * 验证是否有关键字
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public boolean containsKey(String key) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}
		return super.containsKey(key);
	}

	/**
	 * 返回所有的关键字
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Set<String> keySet() {
		return super.keySet();
	}

	/**
	 * 返回所有的关键字数组
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public String[] keyArray() {
		Set<String> set = this.keySet();
		String[] array = new String[set.size()];
		return set.toArray(array);
	}

	/**
	 * 清空所有的关键字及其对应的值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public void clear() {
		super.clear();
	}

	/**
	 * 克隆，这种方式为浅克隆-只克隆一层，使用的时候需要注意。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public DataMap clone() {
		return (DataMap) super.clone();
	}

	/**
	 * 存放键值对
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Object put(String key, Object value) {
		if (StringUtil.chkStrNull(key)) {
			// 此处预计的是对外抛出异常，但是由于该put方法继承子hashmap其没有移除抛出声明，无法重写，所以只在控制台输出错误信息，供调试参考
			System.out.println("关键字不允许为空，异常。程序可能存在bug请认真核实，在DataMap的put方法中对于key不允许为空！");
		}
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}
		if (value instanceof java.sql.Date) {
			value = new java.util.Date(((java.sql.Date) value).getTime());
		}
		if (value instanceof java.sql.Timestamp) {
			value = new java.util.Date(((java.sql.Timestamp) value).getTime());
		}
		return super.put(key, value);
	}

	/**
	 * 存放int类型的键值对
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Object put(String key, int value) throws AppException {
		return put(key, new Integer(value));
	}

	/**
	 * 存放double类型的键值对
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Object put(String key, double value) throws AppException {
		return put(key, new Double(value));
	}

	/**
	 * 存放boolean类型的键值对
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */

	public Object put(String key, boolean value) throws AppException {
		return put(key, new Boolean(value));
	}

	/**
	 * 存放键值对-返回this
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public DataMap add(String key, Object value) {
		this.put(key, value);
		return this;
	}

	/**
	 * 存放int类型的键值对-返回this
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public DataMap add(String key, int value) throws AppException {
		this.put(key, value);
		return this;
	}

	/**
	 * 存放double类型的键值对-返回this
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public DataMap add(String key, double value) throws AppException {
		this.put(key, value);
		return this;
	}

	/**
	 * 存放boolean类型的键值对-返回this
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */

	public DataMap add(String key, boolean value) throws AppException {
		this.put(key, value);
		return this;
	}

	/**
	 * 根据关键字获取相应的值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Object get(String key) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}
		if (!super.containsKey(key)) {
			throw new AppException("关键字队列中不存在关键字'" + key + "'", "DataMap");
		}
		return super.get(key);
	}

	/**
	 * 根据关键字获取相应的值,如果不存在返回默认的pdefault。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Object get(String key, Object pdefault) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}
		if (!super.containsKey(key)) {
			return pdefault;
		}
		return super.get(key);
	}

	/**
	 * 取关键字对应的double值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public double getDouble(String key) throws AppException {
		Object o = get(key);
		if (o == null || "".equals(o)) {
			return 0.0;
		}
		if (o instanceof Double) {
			return ((Double) o).doubleValue();
		} else {
			return StringUtil.stringToDouble(o.toString());
		}
	}

	/**
	 * 取关键字所对应的Double值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Double getDoubleClass(String key) throws AppException {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if ("".equals(o)) {
			return Double.valueOf("0.0");
		}
		if (o instanceof Double) {
			return (Double) o;
		} else {
			return Double.valueOf(o.toString());
		}
	}

	/**
	 * 取关键字所对应的double值 当关键字为空，或者内容为空时取 default的值。
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public double getDouble(String key, double pdefault) throws AppException {
		Object o = get(key, Double.valueOf(String.valueOf(pdefault)));
		if (o == null || "".equals(o)) {
			return pdefault;
		}
		if (o instanceof Double) {
			return ((Double) o).doubleValue();
		} else {
			return StringUtil.stringToDouble(o.toString());
		}
	}

	/**
	 * 取关键字所对应的Int值
	 * 
	 * @param key 关键字名
	 * @return 关键字所对应的Int值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public int getInt(String key) throws AppException {
		Object o = get(key);
		if (o == null || "".equals(o)) {
			return 0;
		}
		if (o instanceof Integer) {
			return ((Integer) o).intValue();
		} else {
			return StringUtil.stringToInt(o.toString());
		}
	}

	/**
	 * 取关键字所对应的Int值
	 * 
	 * @param key 关键字名
	 * @return 关键字所对应的Int值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */

	public Integer getIntClass(String key) throws AppException {
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if ("".equals(o)) {
			return Integer.valueOf("0");
		}
		if (o instanceof Integer) {
			return (Integer) o;
		} else {
			return Integer.valueOf(o.toString());
		}
	}

	/**
	 * 取关键字所对应的Int值
	 * 
	 * @param key 关键字名 pdefault 默认值
	 * @return 关键字所对应的Int值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public int getInt(String key, int pdefault) throws AppException {
		Object o = get(key, Double.valueOf(String.valueOf(pdefault)));
		if (o == null || "".equals(o)) {
			return pdefault;
		}
		if (o instanceof Integer) {
			return ((Integer) o).intValue();
		} else {
			return StringUtil.stringToInt(o.toString());
		}
	}

	/**
	 * 取关键字所对应的boolean值
	 * 
	 * @param key 关键字名
	 * @return 关键字所对应的boolean值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public boolean getBoolean(String key) throws AppException {
		Object o = get(key);
		if (o == null) {
			return false;
		}
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue();
		} else {
			if ("true".equals(o.toString().toLowerCase())) {
				return true;
			} else if ("false".equals(o.toString().toLowerCase())) {
				return false;
			} else {
				throw new AppException("关键字['" + key + "']对应的值不是一个boolean类型的值！", "DataMap");
			}
		}
	}

	/**
	 * 取关键字所对应的boolean值
	 * 
	 * @param key 关键字名 boolean pdefault 默认值
	 * @return 关键字所对应的boolean值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public boolean getBoolean(String key, boolean pdefault) throws AppException {
		Object o = get(key, Boolean.valueOf(pdefault));
		if (o == null || "".equals(o)) {
			return pdefault;
		}
		if (o instanceof Boolean) {
			return ((Boolean) o).booleanValue();
		} else {
			if ("true".equals(o.toString().toLowerCase())) {
				return true;
			} else if ("false".equals(o.toString().toLowerCase())) {
				return false;
			} else {
				throw new AppException("关键字['" + key + "']对应的值不是一个boolean类型的值！", "DataMap");
			}
		}
	}

	/**
	 * 取关键字所对应的Blob值
	 * 
	 * @param anameme 关键字名 boolean pdefault 默认值
	 * @return 关键字所对应的boolean值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Blob getBlob(String key) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if (o instanceof Blob) {
			return (Blob) o;
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个Blob类型的值！", "DataMap");
		}
	}

	/**
	 * 取关键字所对应的Date值.
	 * 
	 * @param key 关键字
	 * @return Date 返回日期
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Date getDate(String key) throws AppException {
		Object o = get(key);
		if (o == null || "".equals(o.toString())) {
			return null;
		}
		if (o instanceof Date) {
			return (Date) o;
		} else {
			return DateUtil.stringToDate(o.toString());
		}
	}

	/**
	 * 取关键字所对应的Date值.
	 * 
	 * @param key 关键字
	 * @param format 格式转换的格式
	 * @return Date 返回日期
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Date getDate(String key, String format) throws AppException {
		Object o = get(key);
		if (o == null || "".equals(o.toString())) {
			return null;
		}
		if (o instanceof Date) {
			return (Date) o;
		} else {
			return DateUtil.stringToDate(o.toString(), format);
		}
	}

	/**
	 * 取关键字所对应的Date值.
	 * 
	 * @param key 关键字
	 * @param pdefault 默认值
	 * @return Date 返回日期
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Date getDate(String key, Date pdefault) throws AppException {
		Object o = get(key, pdefault);
		if (o == null || "".equals(o.toString())) {
			return null;
		}
		if (o instanceof Date) {
			return (Date) o;
		} else {
			return DateUtil.stringToDate(o.toString());
		}
	}

	/**
	 * 获取String格式的日期类型.
	 * <p>
	 * 原始值是String的；本方法是为了String->String的格式转换
	 * </p>
	 * 
	 * @param key 关键字
	 * @param tagetFormat 期望转成的串的格式
	 * @return String 返回的结果集
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public String getDateToString(String key, String tagetFormat) throws AppException {
		Date vdate = getDate(key);
		return DateUtil.dateToString(vdate, tagetFormat);
	}

	/**
	 * 获取String格式的日期类型.
	 * <p>
	 * 原始值是String的；本方法是为了String->String的格式转换
	 * </p>
	 * 
	 * @param key 关键字
	 * @param sourceFormat 原数据的格式
	 * @param tagetFormat 期望转成的串的格式
	 * @return String 返回的结果集
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public String getDateToString(String key, String sourceFormat,
			String tagetFormat) throws AppException {
		Date vdate = getDate(key, sourceFormat);
		return DateUtil.dateToString(vdate, tagetFormat);
	}

	/**
	 * 获取String格式的日期类型.
	 * <p>
	 * 原始值是String的；本方法是为了String->String的格式转换
	 * </p>
	 * 
	 * @param key 关键字
	 * @param tagetFormat 期望转成的串的格式
	 * @param pdefault 默认值
	 * @return String 返回的结果集
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public String getDateToString(String key, String tagetFormat, Date pdefault) throws AppException {
		Date vdate = getDate(key, pdefault);
		return DateUtil.dateToString(vdate, tagetFormat);
	}

	/**
	 * 获取日期参数值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public String getDateParaToString(String key, String sourceFormat,
			String tagetFormat) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Date vdate = getDatePara(key, sourceFormat);
		return DateUtil.dateToString(vdate, tagetFormat);
	}

	/**
	 * 获取日期参数值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Date getDatePara(String key, String format) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key);
		if (o == null || "".equals(o.toString())) {// 如果当前值为"" 则返回null
			return null;
		} else if (o instanceof String) {
			return DateUtil.stringToDate(o.toString(), format);
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个String类型的值！", "DataMap");
		}
	}

	/**
	 * 获取日期参数值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Date getDatePara(String key, String format, String pdefault) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key, pdefault);
		if (o == null || "".equalsIgnoreCase(o.toString())) {
			return null;
		}
		if (o instanceof String) {
			return DateUtil.stringToDate(o.toString(), format);
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个String类型的值！", "DataMap");
		}
	}

	/**
	 * 取关键字所对应的String值
	 * 
	 * @param key 关键字名
	 * @return 关键字所对应的String值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public String getString(String key) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if (o instanceof String) {
			return (String) o;
		} else if (o instanceof Clob) {
			return StringUtil.Colb2String((Clob) o);
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个String类型的值！", "DataMap");
		}
	}

	/**
	 * 获取字符串-进行trim的数据
	 * 
	 * @author yjc
	 * @date 创建时间 2017-4-19
	 * @since V1.0
	 */
	public String getTrimString(String key) throws AppException {
		String value = this.getString(key);
		if (null == value) {
			return value;
		}
		return value.trim();
	}

	/**
	 * 获取字符串-进行trim的数据
	 * 
	 * @author yjc
	 * @date 创建时间 2017-4-19
	 * @since V1.0
	 */
	public String getTrimString(String key, String defaultValue) throws AppException {
		String value = this.getString(key, defaultValue);
		if (null == value) {
			return value;
		}
		return value.trim();
	}

	/**
	 * 扩展getString方法，后续逐步使用该方法代替原有的getString方法
	 * <p>
	 * key:字段key; <br>
	 * keyName:字段名称；用于提示<br>
	 * fieldId:字段的ID<br>
	 * validations:验证类
	 * </p>
	 * 
	 * @author yjc
	 * @throws BizException
	 * @throws FieldTipException
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public String getStr(String key, String keyName, String fieldId,
			FieldValidation... validations) throws AppException, BizException, FieldTipException {
		String value = this.getTrimString(key, null);
		if (StringUtil.chkStrNull(keyName)) {
			keyName = key;
		}
		for (FieldValidation validation : validations) {
			try {
				value = validation.validate(value, keyName);
			} catch (Exception e) {
				if (StringUtil.chkStrNull(fieldId)) {
					throw new BizException(e.getMessage());
				} else {
					throw new FieldTipException(e.getMessage(), fieldId);
				}
			}
		}
		return value;
	}

	/**
	 * 重载->扩展getString方法，后续逐步使用该方法代替原有的getString方法
	 * <p>
	 * key:字段key; <br>
	 * keyName:字段名称；用于提示<br>
	 * validations:验证类
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public String getStr(String key, String keyName,
			FieldValidation... validations) throws AppException, BizException, FieldTipException {
		return this.getStr(key, keyName, null, validations);
	}

	/**
	 * 重载->扩展getString方法，后续逐步使用该方法代替原有的getString方法
	 * <p>
	 * key:字段key; <br>
	 * validations:验证类
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2020-8-13
	 * @since V1.0
	 */
	public String getStr(String key, FieldValidation... validations) throws AppException, BizException, FieldTipException {
		return this.getStr(key, null, null, validations);
	}

	/**
	 * 获取字符串-纯文本数据，对于html的标签数据进行过滤的
	 * 
	 * @author yjc
	 * @date 创建时间 2017-4-19
	 * @since V1.0
	 */
	public String getTextString(String key) throws AppException {
		String value = this.getString(key);
		if (null == value) {
			return value;
		}
		return StringUtil.html2Text(value);
	}

	/**
	 * 获取字符串-纯文本数据，对于html的标签数据进行转换主要对‘<>’进行转换
	 * 
	 * @author yjc
	 * @date 创建时间 2017-4-19
	 * @since V1.0
	 */
	public String getHtmlEncodeString(String key) throws AppException {
		String value = this.getString(key);
		if (null == value) {
			return value;
		}
		return StringUtil.htmlEncode(value);
	}

	/**
	 * 获取字符串-纯文本数据，对于html的标签数据进行转换主要对‘<>’进行转换
	 * 
	 * @author yjc
	 * @date 创建时间 2017-4-19
	 * @since V1.0
	 */
	public String getFilterSqlString(String key) throws AppException {
		String value = this.getString(key);
		if (null == value) {
			return value;
		}
		return StringUtil.filterSqlStr(value);
	}

	/**
	 * 取关键字所对应的String值
	 * 
	 * @param key 关键字名 pdefault 默认值
	 * @return 关键字所对应的String值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public String getString(String key, String pdefault) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key, pdefault);
		if (o == null) {
			return null;
		}
		if (o instanceof String) {
			return (String) o;
		} else if (o instanceof Clob) {
			return StringUtil.Colb2String((Clob) o);
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个String类型的值！", "DataMap");
		}
	}

	/**
	 * 对于获取结果进行检查，不允许为空如果为空抛出BizException异常
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws FieldTipException
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public String getStringChkNull(String key, String keyName) throws AppException, BizException {
		String value = this.getString(key);
		if (StringUtil.chkStrNull(keyName)) {
			keyName = key;
		}
		if (StringUtil.chkStrNull(value)) {
			throw new BizException("【" + keyName + "】信息项不允许为空，请检查！");
		}
		return value;
	}

	/**
	 * 对于获取结果进行检查，不允许为空如果为空抛出BizException异常
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws FieldTipException
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public String getStringChkNull(String key) throws AppException, BizException {
		return this.getStringChkNull(key, key);
	}

	/**
	 * 对于获取结果进行检查，不允许为空如果为空抛出FieldTipException异常[有前台置焦点操作]
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws FieldTipException
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public String getStringNotEmpty(String key, String keyName, String fieldId) throws AppException, FieldTipException {
		String value = this.getString(key);
		if (StringUtil.chkStrNull(keyName)) {
			keyName = key;
		}
		if (StringUtil.chkStrNull(fieldId)) {
			fieldId = key;
		}
		if (StringUtil.chkStrNull(value)) {
			throw new FieldTipException("【" + keyName + "】信息项不允许为空，请检查！", fieldId);
		}
		return value;
	}

	/**
	 * 对于获取结果进行检查，不允许为空如果为空抛出FieldTipException异常[有前台置焦点操作]
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public String getStringNotEmpty(String key, String keyName) throws FieldTipException, AppException {
		return this.getStringNotEmpty(key, keyName, key);
	}

	/**
	 * 对于获取结果进行检查，不允许为空如果为空抛出FieldTipException异常[有前台置焦点操作]
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws BizException
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public String getStringNotEmpty(String key) throws FieldTipException, AppException {
		return this.getStringNotEmpty(key, key);
	}

	/**
	 * 取关键字所对应的DataSet值
	 * 
	 * @param key 关键字名
	 * @return 关键字所对应的DataSet值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public DataSet getDataSet(String key) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key);
		if (o == null || "".equals(o)) {
			return null;
		}
		if (o instanceof DataSet) {
			return (DataSet) o;
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个DataSet类型的值！", "DataMap");
		}
	}

	/**
	 * 取关键字所对应的DataSet值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public DataSet getDataSet(String key, DataSet pdefault) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key, pdefault);
		if (o == null || "".equals(o)) {
			return null;
		}
		if (o instanceof DataSet) {
			return (DataSet) o;
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个DataSet类型的值！", "DataMap");
		}
	}

	/**
	 * 取关键字所对应的DataMap值
	 * 
	 * @param key 关键字名
	 * @return 关键字所对应的DataMap值
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public DataMap getDataMap(String key) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key);
		if (o == null || "".equals(o)) {
			return null;
		}
		if (o instanceof DataMap) {
			return (DataMap) o;
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个DataMap类型的值！", "DataMap");
		}
	}

	/**
	 * 取关键字所对应的DataMap值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public DataMap getDataMap(String key, DataMap pdefault) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key, pdefault);
		if (o == null || "".equals(o)) {
			return null;
		}
		if (o instanceof DataMap) {
			return (DataMap) o;
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个DataMap类型的值！", "DataMap");
		}
	}

	/**
	 * 删除给定的关键字
	 * 
	 * @param key 关键字名
	 * @return 如果关键字有对应的值返回关键字所对应的值，反之返回空。
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Object remove(String key) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		if (!this.caseSensitive) {
			key = key.toLowerCase();
		}
		if (!super.containsKey(key)) {
			return null;
		}
		return super.remove(key);
	}

	/**
	 * 获取当前DataMap中关键字key对应的Clob类型值
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-12
	 * @since V1.0
	 */
	public Clob getClob(String key) throws AppException {
		if (StringUtil.chkStrNull(key)) {
			throw new AppException("关键字为空", "DataMap");
		}
		Object o = get(key);
		if (o == null) {
			return null;
		}
		if (o instanceof Clob) {
			return (Clob) o;
		} else {
			throw new AppException("关键字['" + key + "']对应的值不是一个Clob类型的值！", "DataMap");
		}
	}

	/**
	 * 重写putAll方法-调用本身的put方法，目的，key值转小写
	 * 
	 * @author yjc
	 * @date 创建时间 2017-4-29
	 * @since V1.0
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Object> map) {
		if (null == map) {
			return;
		}
		Object[] arrKey = map.keySet().toArray();
		for (int i = 0, n = arrKey.length; i < n; i++) {
			String oneKey = (String) arrKey[i];
			Object value = map.get(oneKey);
			this.put(oneKey, value);
		}
	}

	/**
	 * 处理DataMap的特殊数据类型-如Clob和Blob,date等转换为String类型
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public DataMap dealSepType() throws AppException {
		if (this.size() <= 0) {
			return this;
		}
		// 对于特殊的类型数据进行预处理
		Object[] keys = this.keySet().toArray();
		for (int i = 0, n = keys.length; i < n; i++) {
			String key = (String) keys[i];
			Object oValue = this.get(key);
			if (oValue instanceof Clob) {
				this.put(key, StringUtil.Colb2String((Clob) oValue));
			} else if (oValue instanceof Blob) {
				this.put(key, "[BLOB数据]");
			} else if (oValue instanceof byte[]) {
				this.put(key, "[Byte数据]");
			} else if (oValue instanceof DataSet) {
				DataSet oSet = (DataSet) oValue;
				this.put(key, oSet.dealSepType());
			} else if (oValue instanceof Date) {// 如果是日期类型格式将其进行转换为string：yyyyMMddhhmmss格式
				this.put(key, DateUtil.dateToString((Date) oValue, "yyyyMMddhhmmss"));
			} else if (oValue instanceof EchartsOption) {// 如果类型为EchartsOption-则进行调整为JSONObject对象
				this.put(key, ((EchartsOption) oValue).toJSONObject());
			} else if (oValue instanceof DataMap) {
				DataMap oMap = (DataMap) oValue;
				this.put(key, oMap.dealSepType());
			}
		}
		return this;
	}

	/**
	 * 增加工具方法，对于数据为基本类型，string,DataSet,Date等的数据转换为json数据字符串
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public String toJsonString() throws AppException {
		this.dealSepType();// 对于特殊类型进行处理，然后转换为string类型
		return JSONObject.fromObject(this).toString();
	}

	/**
	 * 增加工具方法，对于数据为基本类型，string,DataSet,Date等的数据转换为json数据字符串
	 * <p>
	 * indentFactor:缩进因子，控制几个空格缩进
	 * </p>
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public String toJsonString(int indentFactor) throws AppException {
		this.dealSepType();// 对于特殊类型进行处理，然后转换为string类型
		return JSONObject.fromObject(this).toString(indentFactor);
	}

	/**
	 * json字符串转换转为DataMap
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public static DataMap fromObject(String json) throws AppException {
		return DataMap.fromObject(json, false);
	}

	/**
	 * JSONObject转换为DataMap数据类型
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public static DataMap fromObject(JSONObject json) throws AppException {
		return DataMap.fromObject(json, false);
	}

	/**
	 * json字符串转换转为DataMap
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public static DataMap fromObject(String json, boolean caseSensitive) throws AppException {
		if (StringUtil.chkStrNull(json)) {// 如果为空返回null
			return null;
		}
		JSONObject jsonObj = JSONObject.fromObject(json);
		return DataMap.fromObject(jsonObj, caseSensitive);
	}

	/**
	 * JSONObject转换为DataMap数据类型
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	@SuppressWarnings("unchecked")
	public static DataMap fromObject(JSONObject json, boolean caseSensitive) throws AppException {
		if (null == json) {
			return null;
		}

		// 开始解析
		DataMap dm = new DataMap(caseSensitive);
		Set<Object> joKeys = json.keySet();
		for (Object key : joKeys) {
			Object JsonValObj = json.get(key);
			if (JsonValObj instanceof JSONArray) {
				try {
					dm.put((String) key, DataSet.fromObject((JSONArray) JsonValObj, caseSensitive));// DataSet格式
				} catch (AppException e) {// 解析出错，则认为普通数组格式
					dm.put((String) key, DataMap.parseArray((JSONArray) JsonValObj));
				}
			} else if (JsonValObj instanceof JSONObject) {
				dm.put((String) key, DataMap.fromObject((JSONObject) JsonValObj, caseSensitive));
			} else if (JsonValObj instanceof JSONNull) {
				dm.put((String) key, null);// 对于null-Json解析出的是JSONNull-这个进行调整为null
			} else {
				dm.put((String) key, JsonValObj);
			}
		}
		return dm;
	}

	/**
	 * 普通数组格式解析
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	private static Object[] parseArray(JSONArray json) {
		if (null == json) {
			return null;
		}
		ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0, n = json.size(); i < n; i++) {
			list.add(json.get(i));
		}
		return list.toArray();
	}

	/**
	 * 将对象输出到输出流中
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public void write2Stream(OutputStream out) throws IOException, AppException {
		if (null == out) {
			throw new AppException("输出流为空");
		}
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(out);
			oos.writeObject(this);
		} finally {
			if (null != oos) {
				try {
					oos.flush();
					oos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 写入文件
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public void write2File(File file) throws SQLException, IOException, AppException {
		OutputStream os = null;
		try {
			os = new FileOutputStream(file);
			this.write2Stream(os);
		} finally {
			if (os != null) {
				try {
					os.flush();// 此处一定报异常（因为在write2Stream方法已经将流关闭），无所谓还是执行关闭
					os.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 写入blob
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public void write2Blob(Blob blob) throws SQLException, IOException, AppException {
		OutputStream outputstream = null;
		try {
			outputstream = blob.setBinaryStream(0);
			this.write2Stream(outputstream);
		} finally {
			if (outputstream != null) {
				try {
					outputstream.flush();// 此处一定报异常（因为在write2Stream方法已经将流关闭），无所谓还是执行关闭
					outputstream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 返回对象序列化后的byte
	 * 
	 * @author yjc
	 * @throws AppException
	 * @throws IOException
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public byte[] write2Byte() throws IOException, AppException {
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			this.write2Stream(baos);
			byte[] arrByte = baos.toByteArray();
			return arrByte;
		} finally {
			if (baos != null) {
				try {
					baos.flush();
					baos.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 序列化流转换对象
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static DataMap fromObject(InputStream is) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
			DataMap dm = (DataMap) ois.readObject();
			return dm;
		} finally {
			if (null != ois) {
				try {
					ois.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 文件中读取DataMap
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static DataMap fromObject(File file) throws IOException, ClassNotFoundException, SQLException {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return DataMap.fromObject(is);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * Blob转DataMap
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static DataMap fromObject(Blob blob) throws IOException, ClassNotFoundException, SQLException {
		InputStream is = null;
		try {
			is = blob.getBinaryStream();
			return DataMap.fromObject(is);
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * byte转DataMap
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static DataMap fromObject(byte[] byteArr) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(byteArr);
			return DataMap.fromObject(bis);
		} finally {
			if (null != bis) {
				try {
					bis.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 根据规则进行判断
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-8
	 * @since V1.0
	 */
	public boolean check(String rules) throws Exception {
		DataSet dsTemp = new DataSet(this.caseSensitive);
		dsTemp.addRow(this);
		dsTemp = dsTemp.find(rules);
		if (dsTemp.size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 通过jsScript脚本进行处理。
	 * <p>
	 * jsScript：js脚本信息。 可用参数：data(数据)
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-8
	 * @since V1.0
	 */
	public DataMap deal(String jsScript) throws ScriptException {
		DataSet dsTemp = new DataSet(this.caseSensitive);
		dsTemp.addRow(this);
		dsTemp.deal(jsScript);
		return this;
	}
}
