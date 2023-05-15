package com.grace.frame.websocket;

import java.io.File;
import java.net.URL;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.grace.frame.constant.GlobalVars;
import com.grace.frame.exception.AppException;
import com.grace.frame.util.DataSet;
import com.grace.frame.util.ReadXmlUtil;

/**
 * webSocket配置工具
 * 
 * @author yjc
 */
public class WebSocketCfgUtil{

	/**
	 * 读取xml服务器配置文件
	 * 
	 * @author yjc
	 * @throws AppException
	 * @date 创建时间 2019-9-3
	 * @since V1.0
	 */
	public static DataSet readCfg() throws AppException {
		if (GlobalVars.CONFIGINWAR) {
			return WebSocketCfgUtil.readCfg2Url("webSocketCfg.xml");
		} else {
			return WebSocketCfgUtil.readCfg2File(new File(GlobalVars.CONFIGFILEPATH
					+ File.separator + "webSocketCfg.xml"));
		}
	}

	/**
	 * 读取xml服务器配置文件
	 * 
	 * @author yjc
	 * @date 创建时间 2019-9-3
	 * @since V1.0
	 */
	private static DataSet readCfg2File(File file) throws AppException {
		// 读取文件
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			document = reader.read(file);
		} catch (Exception e) {
			throw new AppException(e);
		}
		// 迭代循环
		DataSet ds = new DataSet();
		Element root = document.getRootElement();
		for (Iterator<?> it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			String name = element.attributeValue("name");
			String port = element.attributeValue("port");
			String server_class = element.attributeValue("server_class");
			ds.addRow()
				.add("name", name)
				.add("port", Integer.parseInt(port))
				.add("server_class", server_class);
		}
		return ds;
	}

	/**
	 * 读取xml服务器配置文件
	 * 
	 * @author yjc
	 * @date 创建时间 2019-9-3
	 * @since V1.0
	 */
	private static DataSet readCfg2Url(String cfgurl) throws AppException {
		if (!cfgurl.startsWith("/")) {
			cfgurl = "/" + cfgurl;
		}
		// 读取文件
		Document document = null;
		try {
			SAXReader reader = new SAXReader();
			URL url = ReadXmlUtil.class.getResource(cfgurl);
			document = reader.read(url);
		} catch (Exception e) {
			throw new AppException(e);
		}
		// 迭代循环
		DataSet ds = new DataSet();
		Element root = document.getRootElement();
		for (Iterator<?> it = root.elementIterator(); it.hasNext();) {
			Element element = (Element) it.next();
			String name = element.attributeValue("name");
			String port = element.attributeValue("port");
			String server_class = element.attributeValue("server_class");
			ds.addRow()
				.add("name", name)
				.add("port", Integer.parseInt(port))
				.add("server_class", server_class);
		}
		return ds;
	}
}
