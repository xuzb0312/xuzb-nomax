package com.grace.frame.util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONNull;

import com.grace.frame.exception.AppException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;

/**
 * DBF操作方法的封装。
 * 
 * @author yjc
 */
public class DBFUtil{
	/**
	 * 导出文件到DBF中
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static void exportData2Response(HttpServletResponse response,
			String fileName, DataSet dsCols, DataSet dsData) throws AppException {
		if (null == response) {
			throw new AppException("response为空", "DBFUtil");
		}
		if (StringUtil.chkStrNull(fileName)) {
			throw new AppException("fileName为空", "DBFUtil");
		}
		if (null == dsCols) {
			throw new AppException("dsCols为空", "DBFUtil");
		}
		if (null == dsData) {
			throw new AppException("dsData为空", "DBFUtil");
		}

		OutputStream out = null;
		// 结构创建
		DBFField fields[] = new DBFField[dsCols.size()];
		for (int i = 0; i < dsCols.size(); i++) {
			String name = dsCols.getString(i, "name");
			String type = dsCols.getString(i, "type");
			int length = dsCols.getInt(i, "length");
			int decimalcount = dsCols.get(i).getInt("decimalcount", 6);
			fields[i] = new DBFField();
			if (name.length() > 10) {// 字段截取-mod.yjc.2016年10月20日-dbf字段最长10
				fields[i].setName(name.substring(0, 10));
			} else {
				fields[i].setName(name);
			}
			if ("string".equalsIgnoreCase(type)) {
				fields[i].setDataType(DBFField.FIELD_TYPE_C);
				if (length % 2 == 1) {// 不允许奇数位数据写入，如果传入奇数位，则自动加1，转偶数位
					length = length + 1;
				}
				if (length > 254) {// DBF字符型字段最长254，不允许超过这个数，如果超了，自动截取254
					length = 254;
				}
				fields[i].setFieldLength(length);
			} else if ("number".equalsIgnoreCase(type)) {
				fields[i].setDataType(DBFField.FIELD_TYPE_N);
				fields[i].setFieldLength(length);
				fields[i].setDecimalCount(decimalcount);// 默认6位小数点
			} else if ("date".equalsIgnoreCase(type)) {
				fields[i].setDataType(DBFField.FIELD_TYPE_D);
			} else {
				throw new AppException("导出文件设置中有不允许的列类型");
			}
		}

		// 写数据
		try {
			DBFWriter writer = new DBFWriter();
			writer.setCharactersetName("GBK");// 放置中文的生僻字乱码的设置
			writer.setFields(fields);

			for (int i = 0, len = dsData.size(); i < len; i++) {
				Object rowData[] = new Object[dsCols.size()];
				for (int j = 0; j < dsCols.size(); j++) {
					String name = dsCols.getString(j, "name");
					String type = dsCols.getString(j, "type");
					if (type.equalsIgnoreCase("string")) {
						Object valueO = dsData.getObject(i, name);
						String valueS = "";
						if (null != valueO && !(valueO instanceof JSONNull)) {
							valueS = String.valueOf(valueO);
						}
						rowData[j] = valueS;
					} else if (type.equalsIgnoreCase("number")) {
						rowData[j] = new Double(dsData.getDouble(i, name));
					} else if (type.equalsIgnoreCase("date")) {
						rowData[j] = dsData.getDate(i, name);
					} else {
						Object valueO = dsData.getObject(i, name);
						String valueS = "";
						if (null != valueO && !(valueO instanceof JSONNull)) {
							valueS = String.valueOf(valueO);
						}
						rowData[j] = valueS;
					}
				}
				writer.addRecord(rowData);
			}

			response.resetBuffer();
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.setContentType("application/x-dbf;charset=UTF-8");// 定义输出类型-dbf-屏蔽迅雷等下载工具
			response.addHeader("Content-Disposition", "attachment; filename="
					+ fileName + ".dbf");
			out = response.getOutputStream(); // 得到向客户端输出二进制数据的对象
			writer.write(out);
			out.flush();
		} catch (IOException e) {
			// 用户取消下载
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				throw new AppException("文件下载失败!错误信息为：" + e.getMessage());
			}
		}
	}

	/**
	 * 创建列格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet addColumnsRow4DBFUtil(DataSet dsCols, String name,
			String type, int length, int decimalCount) throws Exception {
		if (null == dsCols) {
			dsCols = new DataSet();
		}
		if (StringUtil.chkStrNull(name)) {
			throw new AppException("name为空", "DBFUtil");
		}
		if (StringUtil.chkStrNull(type)) {
			throw new AppException("type为空", "DBFUtil");
		}
		if (length <= 0) {
			throw new AppException("length不能小于等于0", "DBFUtil");
		}
		if (decimalCount < 0) {
			throw new AppException("decimalCount不能小于0", "DBFUtil");
		}
		if (decimalCount > length) {
			throw new AppException("decimalCount不能大于length的值", "DBFUtil");
		}

		// 增加一行
		dsCols.addRow();
		int row = dsCols.size() - 1;
		dsCols.put(row, "name", name);
		dsCols.put(row, "type", type);
		dsCols.put(row, "length", length);
		dsCols.put(row, "decimalcount", decimalCount);

		return dsCols;
	}

	/**
	 * 创建列格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet addColumnsRow4DBFUtil(DataSet dsCols, String name,
			String type, int length) throws Exception {
		return DBFUtil.addColumnsRow4DBFUtil(dsCols, name, type, length, 6);
	}
}
