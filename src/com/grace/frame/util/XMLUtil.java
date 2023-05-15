package com.grace.frame.util;

import java.io.StringWriter;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.grace.frame.exception.AppException;

/**
 * 主要构建将dataset转换成xml的方法
 * 
 * @author yjc
 */
public class XMLUtil{

	/**
	 * 将数据导出到文件下载
	 * 
	 * @author yjc
	 * @date 创建时间 2016-8-18
	 * @since V1.0
	 */
	public static void exportData2Response(HttpServletResponse response,
			String fileName, DataSet dsCols, DataSet data) throws Exception {
		if (null == response) {
			throw new AppException("response为空", "XMLUtil");
		}
		if (StringUtil.chkStrNull(fileName)) {
			throw new AppException("fileName为空", "XMLUtil");
		}
		if (null == dsCols) {
			throw new AppException("dsCols为空", "XMLUtil");
		}
		if (null == data) {
			throw new AppException("data为空", "XMLUtil");
		}

		// 获取xml_str
		String outstr = XMLUtil.dataSet2XMLStr(dsCols, data, true);

		// 向response中书写下文件
		FileIOUtil.writeByteToResponse(outstr.getBytes("UTF-8"), fileName
				+ ".xml", response);
	}

	/**
	 * dataSet 转换为XMLstr
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static String dataSet2XMLStr(DataSet dsCols, DataSet data,
			boolean hasHeader) throws Exception {
		if (null == dsCols) {
			throw new AppException("dsCols为空", "XMLUtil");
		}
		if (null == data) {
			throw new AppException("data为空", "XMLUtil");
		}

		Element root = DocumentHelper.createElement("root");
		Document document = DocumentHelper.createDocument(root);
		root.addAttribute("date", DateUtil.dateToString(new Date()));
		// 如果需要输出标题
		if (hasHeader) {
			Element header = root.addElement("header");
			for (int i = 0, n = dsCols.size(); i < n; i++) {
				String nameT = dsCols.getString(i, "name");
				String headerT = dsCols.getString(i, "header");

				header.addElement(nameT).addText(headerT);
			}
		}

		// 数据体
		Element dataBody = root.addElement("body");
		for (int i = 0, n = data.size(); i < n; i++) {
			Element rowEle = dataBody.addElement("row");
			for (int j = 0, m = dsCols.size(); j < m; j++) {
				String nameT = dsCols.getString(j, "name");
				Object objValue = data.getRow(i).get(nameT, null);
				rowEle.addElement(nameT).addText(String.valueOf(objValue));
			}
		}

		// 转换字符串
		OutputFormat format = new OutputFormat("	", true);
		format.setEncoding("UTF-8");
		StringWriter stringWriter = new StringWriter(); // 创建字符串缓冲区
		XMLWriter xmlWriter = new XMLWriter(stringWriter, format);
		xmlWriter.write(document);
		xmlWriter.close();

		// 返回数据
		return stringWriter.toString();
	}

	/**
	 * 创建列格式
	 * 
	 * @author yjc
	 * @date 创建时间 2015-8-4
	 * @since V1.0
	 */
	public static DataSet addColumnsRow4XMLUtil(DataSet dsCols, String name,
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
