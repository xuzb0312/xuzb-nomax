package com.grace.frame.util;

import java.net.URL;
import java.util.Iterator;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.grace.frame.exception.AppException;

/**
 * xml的读操作封装
 * 
 * @author yjc
 */
public class ReadXmlUtil{
	/**
	 * 对齐xml文件读取操作--适应与key-Value的形式
	 * <p>
	 * 配置文件模式为： root：config-key-value <br>
	 * 格式： <br>
	 * <?xml version="1.0" encoding="UTF-8"?> <br>
	 * <root><br>
	 * <config key="dbid" value="100"> <br>
	 * </config><br>
	 * </root>
	 * </p>
	 */
	public static DataSet readXml4KeyValue(String xmlurl) throws AppException {
		if (!xmlurl.startsWith("/")) {
			xmlurl = "/" + xmlurl;
		}
		// 读取文件
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			URL url = ReadXmlUtil.class.getResource(xmlurl);
			document = reader.read(url);
		} catch (Exception e) {
			throw new AppException(e);
		}

		// 迭代循环
		DataSet ds = new DataSet();
		Element root = document.getRootElement();
		for (Iterator<?> it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			String key = element.attributeValue("key");
			String value = element.attributeValue("value");
			ds.addRow();
			ds.put(ds.size() - 1, "key", key);
			ds.put(ds.size() - 1, "value", value);
		}

		return ds;
	}

	/**
	 * 对齐xml文件读取操作--适应与key-Value的形式--返回DataMap
	 * <p>
	 * 配置文件模式为： root：config-key-value<br>
	 * 格式： <br>
	 * <?xml version="1.0" encoding="UTF-8"?> <br>
	 * <root><br>
	 * <config key="dbid" value="100"> <br>
	 * </config><br>
	 * </root>
	 * </p>
	 */
	public static DataMap readXml2DataMap(String xmlurl) throws AppException {
		if (!xmlurl.startsWith("/")) {
			xmlurl = "/" + xmlurl;
		}
		// 读取文件
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			URL url = ReadXmlUtil.class.getResource(xmlurl);
			document = reader.read(url);
		} catch (Exception e) {
			throw new AppException(e);
		}

		// 迭代循环
		DataMap dm = new DataMap();
		Element root = document.getRootElement();
		for (Iterator<?> it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			String key = element.attributeValue("key");
			String value = element.attributeValue("value");
			dm.put(key, value);
		}

		return dm;
	}

	/**
	 * 对齐xml文件读取操作--适应与key-Value的形式
	 * <p>
	 * 自定义格式； <br>
	 * 格式： <br>
	 * <?xml version="1.0" encoding="UTF-8"?> <br>
	 * <root><br>
	 * 自定义 <br>
	 * </config><br>
	 * </root>
	 * </p>
	 */
	public static DataSet readXml2DataSet(String xmlurl) throws AppException {
		if (!xmlurl.startsWith("/")) {
			xmlurl = "/" + xmlurl;
		}
		// 读取文件
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			URL url = ReadXmlUtil.class.getResource(xmlurl);
			document = reader.read(url);
		} catch (Exception e) {
			throw new AppException(e);
		}

		// 迭代循环
		DataSet ds = new DataSet();
		Element root = document.getRootElement();
		for (Iterator<?> it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();

			DataMap dmTemp = new DataMap();
			for (int i = 0; i < element.attributeCount(); i++) {
				Attribute attr = element.attribute(i);
				dmTemp.put(attr.getName(), attr.getValue());
			}
			ds.addRow(dmTemp);
		}

		return ds;
	}
}
