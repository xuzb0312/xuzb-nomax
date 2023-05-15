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
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.grace.frame.exception.AppException;
import com.grace.frame.exception.BizException;
import com.grace.frame.util.echarts.json.EchartsOption;

/**
 * 内存数据集结构
 * 
 * @author yjc
 * @since-2019年7月3日-yjc-对于结构增加区分大小写的支持
 */
public class DataSet extends ArrayList<DataMap>{
	private static final long serialVersionUID = 2825216837475429370L;

	// 字段类型常量
	public static final String TYPE_STRING = "string";
	public static final String TYPE_NUMBER = "number";
	public static final String TYPE_DATE = "date";
	public static final String TYPE_BOOLEAN = "boolean";
	public static final String TYPE_NULL = "null";

	public static final String TYPE_STRING_AB = "s"; // 缩写
	public static final String TYPE_NUMBER_AB = "n";
	public static final String TYPE_DATE_AB = "d";
	public static final String TYPE_BOOLEAN_AB = "b";
	public static final String TYPE_NULL_AB = "l";

	private LinkedHashMap<String, String> columnTypeMap;

	private boolean caseSensitive = false;// 行DataMap-key是否区分大小写

	/**
	 * 构造函数，初始化列类型列表
	 */
	public DataSet() {
		if (columnTypeMap == null) {
			columnTypeMap = new LinkedHashMap<String, String>();
		}
		this.caseSensitive = false;// 默认为不区分，全部小写
	}

	/**
	 * 构造函数，初始化列类型列表
	 */
	public DataSet(boolean caseSensitive) {
		if (columnTypeMap == null) {
			columnTypeMap = new LinkedHashMap<String, String>();
		}
		this.caseSensitive = caseSensitive;// 行DataMap-key是否区分大小写
	}

	/**
	 * 是否包含某一个元素
	 */
	public boolean contains(Object o) {
		if (!(o instanceof DataMap)) {
			return false;
		}
		return super.contains(o);
	}

	/**
	 * 数据行数
	 */
	public final int size() {
		return super.size();
	}

	/**
	 * 检查行号是否合法
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	private void checkRow(int row) throws AppException {
		if (row < 0 || row >= size()) {
			throw new AppException("无效行号:" + row + ",当前DataSet共有" + size()
					+ "行", "DataSet");
		}
	}

	/**
	 * 删除一行
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataMap delRow(int row) throws AppException {
		this.checkRow(row);
		DataMap rdm = super.remove(row);
		if (null != rdm) {
			rdm.table(null);
		}
		return rdm;
	}

	/**
	 * 在某一行，是否存在某一列的元素
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public boolean containsItem(int row, String column) throws AppException {
		return this.getRow(row).containsKey(column);
	}

	/**
	 * 获取一行数据
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataMap getRow(int row) throws AppException {
		this.checkRow(row);
		return super.get(row);
	}

	/**
	 * 数据清空
	 */
	public void clear() {
		super.clear();
		this.columnTypeMap.clear();
	}

	/**
	 * 增加一行【空行】
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataMap addRow() throws AppException {
		DataMap dm = new DataMap(this.caseSensitive);
		dm.table(this);// 将table列指向本set
		this.add(dm);
		return dm;
	}

	/**
	 * 增加一行【datamap】
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataSet addRow(DataMap o) {
		if (null != o) {
			o.table(this);// 将table列指向本set
		}
		this.add(o);
		return this;// 返回DataSet
	}

	/**
	 * 获取参数的类型字符串
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	private String getObjectType(Object o) {
		String type = TYPE_NULL; // 缺省
		String cname = null;
		if (o == null) {
			return type;
		}
		cname = o.getClass().getName();
		if ("java.lang.String".equals(cname)) {
			type = TYPE_STRING;
		} else if ("java.lang.Double".equals(cname)
				|| "java.lang.Integer".equals(cname)
				|| "java.lang.Long".equals(cname)
				|| "java.math.BigDecimal".equals(cname)) {
			type = TYPE_NUMBER;
		} else if ("java.lang.Boolean".equals(cname)) {
			type = TYPE_BOOLEAN;
		} else if ("java.util.Date".equals(cname)
				|| "java.sql.Date".equals(cname)
				|| "java.sql.Timestamp".equals(cname)) {
			type = TYPE_DATE;
		}
		return type;
	}

	/**
	 * 检测并设置列类型
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	private void checkAndSaveColumnType(String colName, Object colData) {
		if (columnTypeMap == null) {
			columnTypeMap = new LinkedHashMap<String, String>();
		}
		if (!this.caseSensitive) {
			colName = colName.toLowerCase();
		}
		// 该列未在map中定义,或已有定义但type为NULL时,更新typeMap
		if (!columnTypeMap.containsKey(colName)
				|| columnTypeMap.get(colName).equals(TYPE_NULL)) {
			columnTypeMap.put(colName, this.getObjectType(colData));
		}
	}

	/**
	 * 覆写某一行的某一个字段
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Object put(int row, String column, Object value) throws AppException {
		this.checkRow(row);
		DataMap dbo = getRow(row);
		Object tmp = dbo.put(column, value);
		this.checkAndSaveColumnType(column, value);
		return tmp;
	}

	/**
	 * 覆写某一行的某一个字段（double）
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Object put(int row, String column, double value) throws AppException {
		return put(row, column, new Double(value));
	}

	/**
	 * 覆写某一行的某一个字段(int)
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Object put(int row, String column, int value) throws AppException {
		return put(row, column, new Integer(value));
	}

	/**
	 * 覆写某一行的某一个字段(boolean)
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Object put(int row, String column, boolean value) throws AppException {
		return put(row, column, new Boolean(value));
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Object getObject(int row, String column) throws AppException {
		return getRow(row).get(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Object getObject(int row, String column, Object pDefalut) throws AppException {
		return getRow(row).get(column, pDefalut);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String getString(int row, String column) throws AppException {
		return getRow(row).getString(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String getStringChkNull(int row, String column, String label) throws AppException, BizException {
		String value = getRow(row).getString(column);
		if (StringUtil.chkStrNull(label)) {
			label = column;
		}
		if (StringUtil.chkStrNull(value)) {
			throw new BizException("第" + (row + 1) + "行信息项【" + label
					+ "】不允许为空，请检查！");
		}
		return value;
	}

	/**
	 * 获取不为null的串,如果null则返回""
	 * 
	 * @author yjc
	 * @date 创建时间 2018-6-13
	 * @since V1.0
	 */
	public String getStringNvl(int row, String column) throws AppException {
		String value = this.getString(row, column);
		if (null == value) {
			value = "";
		}
		return value;
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String getString(int row, String column, String pDefalut) throws AppException {
		return getRow(row).getString(column, pDefalut);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public double getDouble(int row, String column) throws AppException {
		return getRow(row).getDouble(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public double getDouble(int row, String column, double pDefalut) throws AppException {
		return getRow(row).getDouble(column, pDefalut);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Double getDoubleClass(int row, String column) throws AppException {
		return getRow(row).getDoubleClass(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public int getInt(int row, String column) throws AppException {
		return getRow(row).getInt(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public int getInt(int row, String column, int pDefalut) throws AppException {
		return getRow(row).getInt(column, pDefalut);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Integer getIntClass(int row, String column) throws AppException {
		return getRow(row).getIntClass(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public boolean getBoolean(int row, String column) throws AppException {
		return getRow(row).getBoolean(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public boolean getBoolean(int row, String column, boolean pDefalut) throws AppException {
		return getRow(row).getBoolean(column, pDefalut);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Blob getBlob(int row, String column) throws AppException {
		return getRow(row).getBlob(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public java.util.Date getDate(int row, String column) throws AppException {
		return getRow(row).getDate(column);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public java.util.Date getDate(int row, String column, String format) throws AppException {
		return getRow(row).getDate(column, format);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public java.util.Date getDate(int row, String column, Date pdefault) throws AppException {
		return getRow(row).getDate(column, pdefault);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Clob getClob(int rowId, String columnName) throws AppException {
		return this.getRow(rowId).getClob(columnName);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public Date getStringDate(int rowId, String columnName, String format) throws AppException {
		return this.getRow(rowId).getDatePara(columnName, format);
	}

	/**
	 * 获取某一行，某一列的对象
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String getDateToString(int rowId, String columnName,
			String targetFormat) throws AppException {
		return this.getRow(rowId).getDateToString(columnName, targetFormat);
	}

	/**
	 * 合并dataSet
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataSet combineDataSet(DataSet ds) throws AppException {
		DataMap row = null;
		try {
			for (int i = 0; i < ds.size(); i++) {
				row = (DataMap) ds.getRow(i).clone();
				this.addRow(row);
			}
			return this;
		} catch (Exception ex) {
			throw new AppException("合并DataSet出错：" + ex.getMessage(), "DataSet");
		}
	}

	/**
	 * toggle：对于格式字符串和简写的转换：
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	private String convertTypeAbbreviation(String colType) {
		if (colType == null) {
			return null;
		}
		if (colType.equals(TYPE_STRING)) {
			return TYPE_STRING_AB;
		} else if (colType.equals(TYPE_NUMBER)) {
			return TYPE_NUMBER_AB;
		} else if (colType.equals(TYPE_DATE)) {
			return TYPE_DATE_AB;
		} else if (colType.equals(TYPE_BOOLEAN)) {
			return TYPE_BOOLEAN_AB;
		} else if (colType.equals(TYPE_NULL)) {
			return TYPE_NULL_AB;
		} else if (colType.equals(TYPE_STRING_AB)) {
			return TYPE_STRING;
		} else if (colType.equals(TYPE_NUMBER_AB)) {
			return TYPE_NUMBER;
		} else if (colType.equals(TYPE_DATE_AB)) {
			return TYPE_DATE;
		} else if (colType.equals(TYPE_BOOLEAN_AB)) {
			return TYPE_BOOLEAN;
		} else if (colType.equals(TYPE_NULL_AB)) {
			return TYPE_NULL;
		}
		return null;
	}

	/**
	 * 设置typelist
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setTypeList(LinkedHashMap<String, String> typeList) {
		this.columnTypeMap = typeList;
	}

	/**
	 * 设置typelist
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public void setTypeList(String typeList) throws AppException {
		if (StringUtil.chkStrNull(typeList)) {
			throw new AppException("输入的typelist为空", "DataSet");
		}
		String[] tmpList = typeList.split(",");
		String vcolName, vcolType;
		for (int i = 0; i < tmpList.length; i++) {
			if (tmpList[i] == null || "".equals(tmpList[i])) {
				continue;
			}
			if (tmpList[i].split(":") == null
					|| tmpList[i].split(":").length != 2) {
				throw new AppException("typeList的结构不对，正确的结构应该是:colName:coltype,colName:coltype", "DataSet");
			}
			vcolName = tmpList[i].split(":")[0];
			vcolType = tmpList[i].split(":")[1];

			if (columnTypeMap == null) {
				columnTypeMap = new LinkedHashMap<String, String>();
			}
			if (!this.caseSensitive) {
				vcolName = vcolName.toLowerCase();
			}
			this.columnTypeMap.put(vcolName, this.convertTypeAbbreviation(vcolType));
		}
	}

	/**
	 * 获取typelist
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String getTypeList() throws AppException {
		if (this.columnTypeMap == null) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		for (String colName : columnTypeMap.keySet()) {
			String colType = convertTypeAbbreviation((String) columnTypeMap.get(colName));
			sb.append(colName).append(":").append(colType).append(",");
		}
		String typeList = sb.toString();
		if (typeList != null && typeList.endsWith(",")) {
			typeList = typeList.substring(0, typeList.length() - 1);
		}
		return typeList;
	}

	/**
	 * 获取列名的数组
	 * <p>
	 * </p>
	 * 
	 * @param
	 * @param
	 * @return
	 * @return
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String[] getColumnName() throws AppException {
		if (this.columnTypeMap == null) {
			return null;
		}
		String[] colNames = new String[columnTypeMap.keySet().size()];
		int i = 0;
		for (String tmp : columnTypeMap.keySet()) {
			colNames[i] = tmp;
			i++;
		}
		return colNames;
	}

	/**
	 * 获取列的类型
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public String getColumnType(String colName) throws AppException {
		String columnType = TYPE_NULL;
		if (!this.caseSensitive) {
			colName = colName.toLowerCase();
		}
		columnType = (String) columnTypeMap.get(colName);
		// 转换成类名
		if (TYPE_DATE.equals(columnType) || TYPE_DATE_AB.equals(columnType)) {
			columnType = TYPE_DATE;
		} else if (TYPE_NUMBER.equals(columnType)
				|| TYPE_NUMBER_AB.equals(columnType)) {
			columnType = TYPE_NUMBER;
		} else if (TYPE_BOOLEAN.equals(columnType)
				|| TYPE_BOOLEAN_AB.equals(columnType)) {
			columnType = TYPE_BOOLEAN;
		} else if (TYPE_STRING.equals(columnType)
				|| TYPE_STRING_AB.equals(columnType)) {
			columnType = TYPE_STRING;
		} else {
			columnType = TYPE_STRING;// 当找不到数据类型时默认使用String
		}
		return columnType;
	}

	/**
	 * 排序
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataSet sort(String colName) throws AppException {
		if (this.size() == 0) {
			return this;
		}
		Collections.sort(this, new DataMapComparator(colName));
		return this;
	}

	/**
	 * 倒序排列
	 * 
	 * @author yjc
	 * @date 创建时间 2015-5-13
	 * @since V1.0
	 */
	public DataSet sortdesc(String column) throws AppException {
		if (this.size() == 0) {
			return this;
		}
		Collections.sort(this, Collections.reverseOrder(new DataMapComparator(column)));
		return this;
	}

	/**
	 * 克隆到DataMap层
	 */
	public DataSet clone() {
		DataSet ds = new DataSet();
		try {
			String vtypelist = this.getTypeList();
			if (!StringUtil.chkStrNull(vtypelist)) {
				ds.setTypeList(this.getTypeList());
			}
			if (this.size() == 0)
				return ds;
			for (int i = 0; i < this.size(); i++) {
				ds.add(this.getRow(i).clone());
			}
		} catch (AppException e1) {
			return ds;
		}
		return ds;
	}

	/**
	 * 截取行数
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public DataSet subDataSet(int beginRow, int endRow) throws AppException {
		if (this.size() == 0) {
			return new DataSet();
		}
		checkRow(beginRow);
		checkRow(endRow);

		DataSet newDs = new DataSet();
		for (int i = beginRow; i <= endRow; i++) {
			newDs.addRow(this.getRow(i));
		}
		String typelist = this.getTypeList();
		if (!StringUtil.chkStrNull(typelist)) {
			newDs.setTypeList(getTypeList());
		}
		return newDs;
	}

	/**
	 * 转换string的方法
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-4
	 * @since V1.0
	 */
	public String toString(String colName) throws AppException {
		if (this.size() <= 0) {// 如果行数小于0，返回空串
			return "";
		}
		if (!this.containsItem(0, colName)) {// 判断列是否存在
			throw new AppException("当前DataSet中没有" + colName + "列");
		}
		StringBuffer strBF = new StringBuffer();
		for (int i = 0, n = this.size(); i < n; i++) {
			Object obj_value = this.getObject(i, colName);
			String str_value = "";
			if (null == obj_value) {
				str_value = "";
			} else {
				str_value = String.valueOf(obj_value);
			}
			strBF.append(str_value).append(",");
		}
		strBF.setLength(strBF.length() - 1);
		return strBF.toString();
	}

	/**
	 * 转换string的方法-转换成sql的in形式：replaceC2QCQ
	 * 
	 * @author yjc
	 * @date 创建时间 2017-3-4
	 * @since V1.0
	 */
	public String toC2QCQString(String colName) throws AppException {
		return StringUtil.replaceC2QCQ(this.toString(colName));
	}

	/**
	 * 转换string的方法-转换成sql的in形式：replaceC2QCQ
	 * <p>
	 * 适用于长条件限制的情况<br>
	 * sqlColName：sql列名<br>
	 * dsColName：ds列名
	 * </p>
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2017-3-4
	 * @since V1.0
	 */
	public String toC2QCQString(String sqlColName, String dsColName) throws Exception {
		return StringUtil.replaceC2QCQ(sqlColName, this.toString(dsColName));
	}

	/**
	 * 处理DataSet的特殊数据类型-如Clob和Blob,date等转换为String类型
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public DataSet dealSepType() throws AppException {
		if (this.size() <= 0) {
			return this;
		}
		DataMap oneRow = this.getRow(0);
		Object[] keys = oneRow.keySet().toArray();
		for (int i = 0, n = keys.length; i < n; i++) {// 这样的目的是为了提高转换效率
			String key = (String) keys[i];
			Object oValue = oneRow.get(key);
			if (oValue instanceof Clob) {
				for (int j = 0, m = this.size(); j < m; j++) {
					this.put(j, key, StringUtil.Colb2String((Clob) this.getClob(j, key)));
				}
			} else if (oValue instanceof Blob) {
				for (int j = 0, m = this.size(); j < m; j++) {
					this.put(j, key, "[BLOB数据]");
				}
			} else if (oValue instanceof byte[]) {
				for (int j = 0, m = this.size(); j < m; j++) {
					this.put(j, key, "[Byte数据]");
				}
			} else if (oValue instanceof Date) {// 日期类型的自动转换为yyyyMMddhhmmss格式
				for (int j = 0, m = this.size(); j < m; j++) {
					this.put(j, key, DateUtil.dateToString((Date) this.getDate(j, key), "yyyyMMddhhmmss"));
				}
			} else if (oValue instanceof DataMap) {// DataMap类型数据
				for (int j = 0, m = this.size(); j < m; j++) {
					DataMap oDm = (DataMap) this.getObject(j, key);
					this.put(j, key, oDm.dealSepType());
				}
			} else if (oValue instanceof EchartsOption) {
				// 如果为EchartsOption类型进行转换
				for (int j = 0, m = this.size(); j < m; j++) {
					EchartsOption echartsOpt = (EchartsOption) this.getObject(j, key);
					this.put(j, key, echartsOpt.toJSONObject());
				}
			} else if (oValue instanceof DataSet) {
				for (int j = 0, m = this.size(); j < m; j++) {
					DataSet oSet = (DataSet) this.getObject(j, key);
					this.put(j, key, oSet.dealSepType());
				}
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
		return JSONArray.fromObject(this).toString();
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
		return JSONArray.fromObject(this).toString(indentFactor);
	}

	/**
	 * json字符串转换转为DataSet
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public static DataSet fromObject(String json) throws AppException {
		return DataSet.fromObject(json, false);
	}

	/**
	 * JSONArray转换为DataSet数据类型
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public static DataSet fromObject(JSONArray json) throws AppException {
		return DataSet.fromObject(json, false);
	}

	/**
	 * json字符串转换转为DataSet
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public static DataSet fromObject(String json, boolean caseSensitive) throws AppException {
		if (StringUtil.chkStrNull(json)) {// 如果为空返回null
			return null;
		}
		JSONArray jsonArr = JSONArray.fromObject(json);
		return DataSet.fromObject(jsonArr, caseSensitive);
	}

	/**
	 * JSONArray转换为DataSet数据类型
	 * 
	 * @author yjc
	 * @date 创建时间 2017-5-24
	 * @since V1.0
	 */
	public static DataSet fromObject(JSONArray json, boolean caseSensitive) throws AppException {
		if (null == json) {
			return null;
		}
		DataSet ds = new DataSet(caseSensitive);
		for (int i = 0, n = json.size(); i < n; i++) {
			if (json.get(i) instanceof JSONObject) {
				ds.add(DataMap.fromObject(json.getJSONObject(i), caseSensitive));
			} else {
				throw new AppException("该json格式，使用该方法无法进行解析操作。");
			}
		}
		return ds;
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
	public static DataSet fromObject(InputStream is) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(is);
			DataSet ds = (DataSet) ois.readObject();
			return ds;
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
	 * Blob转DataSet
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static DataSet fromObject(Blob blob) throws IOException, ClassNotFoundException, SQLException {
		InputStream is = null;
		try {
			is = blob.getBinaryStream();
			return DataSet.fromObject(is);
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
	 * byte转DataSet
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static DataSet fromObject(byte[] byteArr) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bis = null;
		try {
			bis = new ByteArrayInputStream(byteArr);
			return DataSet.fromObject(bis);
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
	 * 数据重复性检测-根据传入的columns（例如：gsmc,xm：对应的数据类型必须为string类型）进行判断数据是否重复 <br>
	 * columns:可以认为是主键。通过返回值判断，重复返回true,否则返回false
	 * 
	 * @author yjc
	 * @date 创建时间 2015-9-24
	 * @since V1.0
	 */
	public boolean isRepeat(String columns) throws Exception {
		boolean isRepeat = false;
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String[] arrKeys = columns.split(",");
		for (int i = 0, n = this.size(); i < n; i++) {
			StringBuffer mapKeyBF = new StringBuffer();
			for (int j = 0, m = arrKeys.length; j < m; j++) {
				mapKeyBF.append(this.getString(i, arrKeys[j])).append("$");
			}
			String mapKey = mapKeyBF.toString();
			if (StringUtil.chkStrNull(mapKey)) {
				continue;
			}
			if (map.containsKey(mapKey)) {
				isRepeat = true;
				break;
			}
			map.put(mapKey, i);
		}
		return isRepeat;
	}

	/**
	 * 数据重复性检测-根据传入的columns（例如：gsmc,xm：对应的数据类型必须为string类型）进行判断数据是否重复 <br>
	 * columns:可以认为是主键 存在重复直接抛出异常
	 * 
	 * @author yjc
	 * @date 创建时间 2015-9-24
	 * @since V1.0
	 */
	public void chkRepeat(String columns) throws Exception {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		String[] arrKeys = columns.split(",");
		for (int i = 0, n = this.size(); i < n; i++) {
			StringBuffer mapKeyBF = new StringBuffer();
			for (int j = 0, m = arrKeys.length; j < m; j++) {
				mapKeyBF.append(this.getString(i, arrKeys[j])).append("$");
			}
			String mapKey = mapKeyBF.toString();
			if (StringUtil.chkStrNull(mapKey)) {
				continue;
			}
			if (map.containsKey(mapKey)) {
				int cfhh = map.get(mapKey) + 1;
				throw new BizException("第" + (i + 1) + "行与第" + cfhh
						+ "行数据重复，请检查！");
			}
			map.put(mapKey, i);
		}
	}

	/**
	 * DataSet转DataMap(通过锁定key,将value值放入)
	 * <p>
	 * keyColumns:DataMap-key关键字。如果不能保证唯一，则会进行数据覆盖<br>
	 * valueColumns：DataMap-value值：如果只有一个列，则value直接对应该值，如果多个列则value对应DataMap.
	 * </p>
	 * 
	 * @author yjc
	 * @throws BizException
	 * @throws AppException
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public DataMap toDataMap(String keyColumns, String valueColumns) throws BizException, AppException {
		if (StringUtil.chkStrNull(keyColumns)) {
			throw new BizException("传入的【keyColumns】为空");
		}
		if (StringUtil.chkStrNull(valueColumns)) {
			throw new BizException("传入的【valueColumns】为空");
		}

		DataMap map = new DataMap(true);// 区分大小写
		String[] arrKeys = keyColumns.split(",");
		String[] arrValues = valueColumns.split(",");

		for (int i = 0, n = this.size(); i < n; i++) {
			StringBuffer mapKeyBF = new StringBuffer();
			for (int j = 0, m = arrKeys.length; j < m; j++) {
				mapKeyBF.append(this.getString(i, arrKeys[j])).append("$");
			}
			String mapKey = mapKeyBF.toString();
			if (StringUtil.chkStrNull(mapKey)) {// key值为空，不进行转换
				continue;
			}
			if (arrValues.length == 1) {// 仅有一个value值
				map.put(mapKey, this.getObject(i, arrValues[0]));
			} else {// 多个value值
				DataMap dmTemp = new DataMap(this.caseSensitive);
				for (int j = 0, m = arrValues.length; j < m; j++) {
					dmTemp.put(arrValues[j], this.getObject(i, arrValues[j]));
				}
				map.put(mapKey, dmTemp);
			}
		}
		return map;
	}

	/**
	 * 分类操作，根据传入的keyColumns作为键将DataSet转为多个DataSet DataSet转DataMap(通过锁定ke)
	 * <p>
	 * keyColumns:DataMap-key关键字。如果不能保证唯一，则会进行数据覆盖<br>
	 * 对应值为DataSet
	 * </p>
	 * 
	 * @author yjc
	 * @throws BizException
	 * @throws AppException
	 * @date 创建时间 2019-7-3
	 * @since V1.0
	 */
	public DataMap classify(String keyColumns) throws BizException, AppException {
		if (StringUtil.chkStrNull(keyColumns)) {
			throw new BizException("传入的【keyColumns】为空");
		}
		DataMap map = new DataMap(true);// 区分大小写
		String[] arrKeys = keyColumns.split(",");
		for (int i = 0, n = this.size(); i < n; i++) {
			StringBuffer mapKeyBF = new StringBuffer();
			for (int j = 0, m = arrKeys.length; j < m; j++) {
				mapKeyBF.append(this.getString(i, arrKeys[j])).append("$");
			}
			String mapKey = mapKeyBF.toString();
			if (StringUtil.chkStrNull(mapKey)) {// key值为空，不进行转换
				continue;
			}
			// 分类操作
			DataSet dsTemp;
			if (map.containsKey(mapKey)) {
				dsTemp = map.getDataSet(mapKey);
			} else {
				dsTemp = new DataSet(this.caseSensitive);
			}
			dsTemp.addRow(this.get(i));
			map.put(mapKey, dsTemp);
		}
		return map;
	}

	/**
	 * 检测DataSet的行数是否为零.
	 * <p>
	 * msg:对于行数为零的情况，需要抛出的异常信息。抛出异常类型为BizException
	 * </p>
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public void chkEmpty(String msg) throws BizException {
		if (StringUtil.chkStrNull(msg)) {
			msg = "DataSet数据行数为0。请核实。";
		}
		if (this.isEmpty()) {
			throw new BizException(msg);
		}
	}

	/**
	 * 对于toDataMap,classify转换成的DataMap,key需要按照keyColumns顺序传入，生成key
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-13
	 * @since V1.0
	 */
	public static String genMapKey(String... keys) {
		StringBuffer strBF = new StringBuffer();
		for (String key : keys) {
			strBF.append(key).append("$");
		}
		return strBF.toString();
	}

	/**
	 * 对于toDataMap,classify转换成的DataMap.
	 * <p>
	 * 根据keyColumns，解析mapkey返回相应的DataMap数据
	 * </p>
	 * 
	 * @author yjc
	 * @throws BizException
	 * @date 创建时间 2015-8-13
	 * @since V1.0
	 */
	public static DataMap parseMapKey(String mapKey, String keyColumns) throws BizException {
		if (StringUtil.chkStrNull(keyColumns)) {
			throw new BizException("传入的【keyColumns】为空");
		}

		String[] arrKeys = keyColumns.split(",");
		String[] arrKeyValues = mapKey.split("\\$");

		// 根据mapKey转换成相应的数值
		DataMap map = new DataMap();
		for (int j = 0, m = arrKeys.length; j < m; j++) {
			map.put(arrKeys[j], arrKeyValues[j]);
		}
		return map;
	}

	/**
	 * 写入文件
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public void write2File(File file) throws SQLException, IOException, AppException {
		FileOutputStream os = null;
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
	 * 文件中读取DataSet
	 * 
	 * @author yjc
	 * @date 创建时间 2018-5-11
	 * @since V1.0
	 */
	public static DataSet fromObject(File file) throws IOException, ClassNotFoundException, SQLException {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return DataSet.fromObject(is);
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
	 * 查找数据
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public DataSet find(DataSetFilter filter) throws Exception {
		DataSet dsResult = new DataSet(this.caseSensitive);
		for (DataMap dmTemp : this) {// 循环每一行
			if (filter.filter(dmTemp)) {
				dsResult.addRow(dmTemp);
			}
		}
		return dsResult;
	}

	/**
	 * 通过定义的滤波器规则进行选择 <br>
	 * <p>
	 * filterRules规则：<br>
	 * (column type value) and/or (column type value)<br>
	 * ()：组划分（可循环嵌套）一个()中限定一个条件使用and/or拼接。 <br>
	 * 最外层无需进行组限定。<br>
	 * column:列名<br>
	 * type:<小于,<=小于登录,=等于,<>不等于,>=大于等于,>大于,like包含,in存在其中。<br>
	 * value:对于int,double等直接写入值，对于string请使用''包含。只允许数字类型和string类型比较，in使用[]限定项目<br>
	 * and/or:组运算关系，同一层级只允许一个运算关系<br>
	 * 例如：<br>
	 * (grxl in ['10', '20']) and (nl <= 32)。<br>
	 * 标识过滤：个人学历为10或20且年龄小于等于32的数据
	 * </p>
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-4
	 * @since V1.0
	 */
	public DataSet find(String filterRules) throws Exception {
		DataSetFilterRules rules = new DataSetFilterRules(filterRules);
		return this.find(rules);
	}

	/**
	 * 开发人员使用，用于检查过滤规则是否设置正确(在控制台打印解析整理后的规则字符串和json格式字符串)
	 * 
	 * @author yjc
	 * @date 创建时间 2019-7-5
	 * @since V1.0
	 */
	public void checkFilterRules(String filterRules) throws Exception {
		DataSetFilterRules rules = new DataSetFilterRules(filterRules);
		System.out.println(rules.toString());
		System.out.println(rules.toJsonString(1));
	}

	/**
	 * 通过js动态脚本进行数据处理(执行效率较慢，合适情况下使用)
	 * <p>
	 * jsScript：js脚本信息。 可用参数：data(每行数据),index(行号)。
	 * </p>
	 * 
	 * @author yjc
	 * @throws ScriptException
	 * @date 创建时间 2019-7-8
	 * @since V1.0
	 */
	public DataSet deal(String jsScript) throws ScriptException {
		if (StringUtil.chkStrNull(jsScript)) {
			return this;
		}
		// 创建组合js处理函数
		String script = "function deal(data, index){" + jsScript
				+ "}; deal(data, index);"; // 定义函数并调用

		ScriptEngineManager manager = new ScriptEngineManager(); // 创建一个ScriptEngineManager对象
		ScriptEngine engine = manager.getEngineByName("JavaScript"); // 通过ScriptEngineManager获得ScriptEngine对象
		Bindings bindings = engine.createBindings(); // Local级别的Binding
		Compilable compilable = (Compilable) engine;
		CompiledScript jsFunction = compilable.compile(script); // 解析编译脚本函数

		// 循环脚本处理
		for (int i = 0, n = this.size(); i < n; i++) {
			bindings.clear();
			DataMap dmRow = this.get(i);
			bindings.put("data", dmRow);
			bindings.put("index", i);
			jsFunction.eval(bindings);
		}

		// 置空，并清除数据，以便资源可以快速回收
		bindings.clear();
		manager = null;
		engine = null;
		bindings = null;
		compilable = null;
		jsFunction = null;
		return this;
	}

	/**
	 * union合并2个dataSet(默认合并) <br>
	 * <p>
	 * keyColumns:唯一<br>
	 * 多个key使用,间隔，例如：xm,rybh
	 * </p>
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-12-12
	 * @since V1.0
	 */
	public DataSet unionDataSet(String keyColumns, DataSet ds) throws Exception {
		return this.unionDataSet(keyColumns, ds, new DataSetUnion(){
			@Override
			public DataMap union(DataMap one, DataMap two, boolean isOneExist,
					boolean isTowExist) throws Exception {
				one.putAll(two);
				return one;
			}
		});
	}

	/**
	 * union合并2个dataSet（自定义union实现的情况） <br>
	 * <p>
	 * keyColumns:唯一<br>
	 * 多个key使用,间隔，例如：xm,rybh<br>
	 * union:自定义union实现
	 * </p>
	 * 
	 * @author yjc
	 * @throws Exception
	 * @date 创建时间 2019-12-12
	 * @since V1.0
	 */
	public DataSet unionDataSet(String keyColumns, DataSet ds,
			DataSetUnion union) throws Exception {
		if (ds == null) {
			throw new BizException("传入的ds为空");
		}

		// 获取所有的列
		String[] keysSrc;
		if (this.size() > 0) {
			keysSrc = this.getRow(0).keyArray();
		} else {
			keysSrc = this.getColumnName();
		}
		String[] keysTarget;
		if (ds.size() > 0) {
			keysTarget = ds.getRow(0).keyArray();
		} else {
			keysTarget = ds.getColumnName();
		}
		String[] keyColumnsArr = keyColumns.split(",");

		// 先使用分类函数进行分类
		DataMap dmSrc = this.classify(keyColumns);
		DataMap dmTarget = ds.classify(keyColumns);

		// 结果DataSet
		DataSet dsResult = new DataSet(this.caseSensitive);
		String[] keys = dmSrc.keyArray();
		for (String key : keys) {
			// 源
			DataSet dsSrcTemp = dmSrc.getDataSet(key);
			if (dsSrcTemp.size() != 1) {
				throw new AppException("传入的keyColumns在DataSet中不唯一[源]");
			}
			DataMap dmSrcTemp = dsSrcTemp.get(0);

			// 获取目标
			DataMap dmTargetTemp;
			boolean isTowExist;
			if (dmTarget.containsKey(key)) {
				DataSet dsTargetTemp = dmTarget.getDataSet(key);
				if (dsTargetTemp.size() != 1) {
					throw new AppException("传入的keyColumns在DataSet中不唯一[目标]");
				}
				dmTargetTemp = dsTargetTemp.get(0);
				dmTarget.remove(key);// 移除行
				isTowExist = true;
			} else {
				dmTargetTemp = new DataMap();
				for (String keyTarget : keysTarget) {
					dmTargetTemp.put(keyTarget, null);
				}
				for (String keyColumn : keyColumnsArr) {// key值增加
					dmTargetTemp.put(keyColumn, dmSrcTemp.get(keyColumn));
				}
				isTowExist = false;
			}
			DataMap dmResult = union.union(dmSrcTemp, dmTargetTemp, true, isTowExist);
			dsResult.addRow(null == dmResult ? dmSrcTemp : dmResult);
		}

		// 目标行
		keys = dmTarget.keyArray();
		for (String key : keys) {
			// 目标
			DataSet dsTargetTemp = dmTarget.getDataSet(key);
			if (dsTargetTemp.size() != 1) {
				throw new AppException("传入的keyColumns在DataSet中不唯一[目标]");
			}
			DataMap dmTargetTemp = dsTargetTemp.get(0);

			// 源
			DataMap dmSrcTemp = new DataMap();
			for (String keySrc : keysSrc) {
				dmSrcTemp.put(keySrc, null);
			}
			for (String keyColumn : keyColumnsArr) {// key值增加
				dmSrcTemp.put(keyColumn, dmTargetTemp.get(keyColumn));
			}
			DataMap dmResult = union.union(dmSrcTemp, dmTargetTemp, false, true);
			dsResult.addRow(null == dmResult ? dmSrcTemp : dmResult);
		}

		// 资源释放
		dmSrc.clear();
		dmTarget.clear();
		dmSrc = null;
		dmTarget = null;

		return dsResult;
	}
}
