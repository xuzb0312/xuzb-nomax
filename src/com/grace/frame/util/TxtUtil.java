package com.grace.frame.util;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONNull;

import com.grace.frame.exception.AppException;

/**
 * Txt相关的操作
 * 
 * @author yjc
 */
public class TxtUtil{
	public final static String TAB = "	";
	public final static String ENTER = "\r\n";
	public final static String SPLIT = "|";

	/**
	 * 将数据导出到response中，进行文件的下载
	 * 
	 * @author yjc
	 * @date 创建时间 2015-7-21
	 * @since V1.0
	 */
	public static void exportData2Response(HttpServletResponse response,
			String fileName, DataSet dsCols, DataSet data) throws Exception {
		if (null == response) {
			throw new AppException("response为空", "TxtUtil");
		}
		if (StringUtil.chkStrNull(fileName)) {
			throw new AppException("fileName为空", "TxtUtil");
		}
		if (null == dsCols) {
			throw new AppException("dsCols为空", "TxtUtil");
		}
		if (null == data) {
			throw new AppException("data为空", "TxtUtil");
		}
		String outstr = TxtUtil.dataSet2Str(dsCols, data);

		// 向response中书写下文件
		FileIOUtil.writeByteToResponse(outstr.getBytes("UTF-8"), fileName
				+ ".txt", response);
	}

	/**
	 * dataSet 转换为str
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static String dataSet2Str(DataSet dsCols, DataSet data) throws Exception {
		if (null == dsCols) {
			throw new AppException("dsCols为空", "TxtUtil");
		}
		if (null == data) {
			throw new AppException("data为空", "TxtUtil");
		}
		StringBuffer strBF = new StringBuffer();
		// 先输出一个标题
		for (int i = 0, n = dsCols.size(); i < n; i++) {
			strBF.append(dsCols.getString(i, "header")).append(TxtUtil.SPLIT);
		}

		// 数据输出
		for (int i = 0, n = data.size(); i < n; i++) {
			strBF.append(TxtUtil.ENTER);// 换行
			for (int j = 0, m = dsCols.size(); j < m; j++) {
				Object value = data.getObject(i, dsCols.getString(j, "name"));
				String valueStr = "";
				if (null != value && !(value instanceof JSONNull)) {
					valueStr = String.valueOf(value);
				}
				strBF.append(valueStr);
				if (j < m - 1) {
					strBF.append(TxtUtil.SPLIT);
				}
			}
		}
		return strBF.toString();
	}

	/**
	 * dataSet 转换为str
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static String dataSet2Str(DataSet dsCols, DataSet data,
			boolean hasHeader, String splitType) throws Exception {
		if (null == dsCols) {
			throw new AppException("dsCols为空", "TxtUtil");
		}
		if (null == data) {
			throw new AppException("data为空", "TxtUtil");
		}
		StringBuffer strBF = new StringBuffer();
		// 先输出一个标题
		if (hasHeader) {
			for (int i = 0, n = dsCols.size(); i < n; i++) {
				strBF.append(dsCols.getString(i, "header")).append(splitType);
			}
		}

		// 数据输出
		for (int i = 0, n = data.size(); i < n; i++) {
			if (hasHeader) {
				strBF.append(TxtUtil.ENTER);// 换行
			} else {
				if (i != 0) {
					strBF.append(TxtUtil.ENTER);// 换行
				}
			}
			for (int j = 0, m = dsCols.size(); j < m; j++) {
				Object value = data.getObject(i, dsCols.getString(j, "name"));
				String valueStr = "";
				if (null != value && !(value instanceof JSONNull)) {
					valueStr = String.valueOf(value);
				}
				strBF.append(valueStr);
				if (j < m - 1) {
					strBF.append(splitType);
				}
			}
		}
		return strBF.toString();
	}

	/**
	 * 创建列格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet addColumnsRow4TxtUtil(DataSet dsCols, String name,
			String header) throws Exception {
		if (null == dsCols) {
			dsCols = new DataSet();
		}
		if (StringUtil.chkStrNull(name)) {
			throw new AppException("name为空", "TxtUtil");
		}
		if (StringUtil.chkStrNull(header)) {
			header = "";
		}

		// 增加一行
		dsCols.addRow();
		int row = dsCols.size() - 1;
		dsCols.put(row, "name", name);
		dsCols.put(row, "header", header);

		return dsCols;
	}
}
